package org.acme;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.eclipse.microprofile.rest.client.RestClientDefinitionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.runtime.ShutdownEvent;

@Path("/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class LoggerController {

  @Inject
  @ConfigProperty(name = "nodes_replicas_url", defaultValue = "http://127.0.0.1:8888")
  List<String> nodesReplicas;

  @Inject
  @ConfigProperty(name = "zookeeper_url", defaultValue = "10.10.1.1")
  public String zookeeperUrl;

  List<LoggerRestClient> loggerRestClients;

  List<LoggerClient> loggerClients;
  Logger logger = LoggerFactory.getLogger(getClass());

  @PostConstruct
  public void init() {
    loggerClients = new ArrayList<>();
    loggerRestClients = nodesReplicas.stream().map(
        url -> {
          try {
            return RestClientBuilder.newBuilder()//
                .baseUrl(new URL(url)) //
                .build(LoggerRestClient.class);
          } catch (IllegalStateException | RestClientDefinitionException | MalformedURLException e) {
            throw new RuntimeException(e);
          }
        }).collect(Collectors.toList());
    logger.info("loggerRestClients size [{}]", loggerRestClients.size());
  }

  public void destroy(@Observes ShutdownEvent ignored) {
    loggerClients.stream().forEach(a -> {
      try {
        a.close();
      } catch (Exception e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    });
  }

  @POST
  @Path("registerReplica")
  public Response registerReplica(LoggerConfig config) throws Exception {
    logger.info("Registering replica logger");
    ReplicaLoggerClient replicaLoggerClient = new ReplicaLoggerClient(config.ring + "", config.ring + ":L",
        config.id, 0, zookeeperUrl, config.pathPrefix);
    replicaLoggerClient.start();
    loggerClients.add(replicaLoggerClient);
    return Response.ok().build();
  }

  @GET
  @Path("ping")
  public Response ping() {
    logger.info("Pong");
    return Response.ok("pong").build();
  }

  @POST
  @Path("register")
  public Response registerLogger(LoggerConfig config) {
    logger.info("Registering config [{}]", config);
    int clusterSize = config.replicas;
    if (clusterSize <= 0)
      clusterSize = 1;
    int id = config.id;
    for (int i = 0; i < clusterSize; i++) {
      int newId = i + id;
      int urlId = (newId) % clusterSize;
      logger.info("Calling logger in url id [{}] with id [{}]", urlId, newId);
      config.id = newId;
      loggerRestClients.get(urlId).registerReplica(config);
    }
    return Response.ok().build();
  }

  @GET
  @Path("getAllLogs")
  public Response getAll() {
    logger.info("Received a get all request.");
    List<List<String>> loggedValues = loggerClients.stream().map(LoggerClient::getAllLogs)
        .collect(Collectors.toList());
    logger.info("Size [{}]", loggedValues.size());

    return Response.ok(loggedValues).build();
  }

  @POST
  @Path("generateDatFiles")
  public Response generateDatFiles() throws Exception {
    List<String> applications = List.of("io");
    List<String> loggerTypes = List.of("sem", "cou", "des");
    List<String> others = List.of("500");
    List<String> commandsSizes = List.of("001");
    List<String> threadCounters = Arrays.asList("4", "8", "16", "32", "64", "128", "256", "512", "1024");
    final String separator = "_";

    String basePath = "/home/joaolucas/code/shared-logger-client/evaluation/thinking_time_50/";
    StringBuilder datFile = new StringBuilder();

    datFile.append("name, sem_latency (ms), sem_throughputReplica (kCommands/s), sem_throughputLogger (kCommands/s),")
        .append(" cou_latency (ms), cou_throughputReplica (kCommands/s), cou_throughputLogger (kCommands/s),")
        .append(" des_latency (ms), des_throughputReplica (kCommands/s), des_throughputLogger (kCommands/s)\n");
    for (String other : others) {
      for (String commandsSize : commandsSizes) {
        for (String application : applications) {
          for (String threadCounter : threadCounters) {
            datFile.append(application
                + separator + threadCounter
                + separator + other
                + separator + commandsSize + ",");
            for (String loggerType : loggerTypes) {
              String folderPath = basePath + application
                  + separator + loggerType
                  + separator + threadCounter
                  + separator + other
                  + separator + commandsSize
                  + "/";
              String latencyPath = folderPath + "client_latency.csv";
              String replicaPath = folderPath + "replica_1.csv";
              String loggerPath = folderPath + "logger_1.csv";

              String latency = readLatency(latencyPath);
              String throughputReplica = readThroughput(replicaPath);
              String throughputLogger = readThroughput(loggerPath);

              datFile
                  .append(latency + ",")
                  .append(throughputReplica + ",")
                  .append(throughputLogger + ",");
            }
            datFile.append("\n");
          }
        }
      }
    }
    return Response.ok(datFile.toString()).build();
  }

  private String readThroughput(String replicaPath) throws Exception {
    java.nio.file.Path path = Paths.get(replicaPath);
    String regexIsNumberOrComma = "^(\\d+?,? ?)*$";
    Pattern patternIsNumberOrComma = Pattern.compile(regexIsNumberOrComma);

    String regexIsZeroThroughput = "^.* 0, 0$";
    Pattern patternIsZeroThroughput = Pattern.compile(regexIsZeroThroughput);

    if (!path.toFile().exists())
      return "0.00";

    long size = Files.readAllLines(path).stream()
        .filter(l -> patternIsNumberOrComma.matcher(l).find())
        .filter(l -> !patternIsZeroThroughput.matcher(l).find())
        .skip(2).count() - 2;

    Double avg = Files.readAllLines(path).stream()
        .filter(l -> patternIsNumberOrComma.matcher(l).find())
        .filter(l -> !patternIsZeroThroughput.matcher(l).find())
        .skip(2)
        .limit(size)
        .mapToLong(
            line -> Long.parseLong(line.split(", ")[1]))
        .average().orElse(0D) / 1_000D;

    return String.format(Locale.ROOT, "%.2f", avg);
  }

  private String readLatency(String latencyPath) throws Exception {
    java.nio.file.Path path = Paths.get(latencyPath).normalize();

    if (!path.toFile().exists())
      return "0.00";

    String regexIsZeroLatency = "^.*,0$";
    Pattern patternIsZeroLatency = Pattern.compile(regexIsZeroLatency);
    long size = Files.readAllLines(path).stream()
        .filter(l -> !patternIsZeroLatency.matcher(l).find())
        .skip(2).count() - 2;

    Double avg = Files.readAllLines(path).stream()
        .filter(l -> !patternIsZeroLatency.matcher(l).find())
        .skip(2)
        .limit(size)
        .mapToLong(
            line -> Long.parseLong(line.split(",")[1]))
        .average().orElse(0D) / 1000_000D;

    return String.format(Locale.ROOT, "%.2f", avg);
  }
}