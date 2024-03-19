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
import java.util.concurrent.SynchronousQueue;
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
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.eclipse.microprofile.rest.client.RestClientDefinitionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.usi.da.smr.message.Message;
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
    closeLoggers();
  }

  @POST
  @Path("closeLoggers")
  public void closeLoggers() {
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
    logger.info("Registering replica logger [{}]", config);
    ReplicaLoggerClient replicaLoggerClient = new ReplicaLoggerClient(config.ring + "", config.ring + ":L",
        config.id, 0, zookeeperUrl, config.pathPrefix, config.trackerNumber);
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
      logger.info("Calling logger in url id [{}] with id [{}] and path [{}]", urlId, newId, config.pathPrefix);
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
  @Path("executeLoggerPerformanceTest/{threadCounter}/{ringCounter}")
  public void executePerformanceTest(@PathParam("threadCounter") Integer threadCounter,
      @PathParam("ringCounter") Integer ringCounter) throws Exception {
    logger.info("Executing performance test with thread counter [{}] and ring counter [{}]",
        threadCounter, ringCounter);

    LoggerConfig config = new LoggerConfig();
    config.id = 4;
    config.pathPrefix = "/media/disk";
    config.trackerNumber = 1000;
    config.ring = "1";

    List<ConsumerGenerator> consumerGenerators = new ArrayList<>();
    
    for (int i = 1; i < ringCounter + 1; i++) {
      ReplicaLoggerClient replicaLoggerClient = new ReplicaLoggerClient(i + "", (i + config.ring) + ":L",
          i, 0, zookeeperUrl, config.pathPrefix + i + "/", config.trackerNumber);
      SynchronousQueue<Message> queue = new SynchronousQueue<>();
      replicaLoggerClient.start();
      loggerClients.add(replicaLoggerClient);
      
      ConsumerGenerator consumerGenerator = new ConsumerGenerator(replicaLoggerClient, queue);
      consumerGenerators.add(consumerGenerator);
      Thread thread = new Thread(consumerGenerator);
      thread.setPriority(7);
      thread.start();
    }

    List<Thread> threads = new ArrayList<>();
    for (int i = 0; i < threadCounter; i++) {
      for (ConsumerGenerator consumerGenerator : consumerGenerators) {
        Thread t = new Thread(new LoadGenerator(consumerGenerator.queue));
        threads.add(t);
      }
    }

    for (Thread thread : threads) {
      thread.start();
    }

    for (Thread t : threads) {
      t.join();
    }

    LoadGenerator.savingLatencies();
  }

  @POST
  @Path("generateDatFiles")
  public Response generateDatFiles() throws Exception {
    List<String> applications = List.of("io", "cpu");
    List<String> loggerTypes = List.of("sem", "cou", "dec");
    List<String> others = List.of("90");
    List<String> commandsSizes = List.of("001");
    List<String> threadCounters = Arrays.asList("8", "16", "32", "64", "128", "256", "512", "768", "1024", "1280", "1536", "1792", "2048");
    final String separator = "_";

    String basePath = "/home/joaolucas/code/shared-logger-client/evaluation/thinking_time_3/";
    StringBuilder datFile = new StringBuilder();

    datFile
        .append("name, sem_throughputReplica (avg)(kCommands/s), sem_throughputReplica (med)(kCommands/s), sem_throughputReplica (p95)(kCommands/s), sem_latency (avg)(ms), sem_latency (med)(ms), sem_latency (p95)(ms), ")
        .append(
            "cou_throughputReplica (avg)(kCommands/s), cou_throughputReplica (med)(kCommands/s), cou_throughputReplica (p95)(kCommands/s), cou_latency (avg)(ms), cou_latency (med)(ms), cou_latency (med)(ms), ")
        .append(
            "dec_throughputReplica (avg)(kCommands/s), dec_throughputReplica (med)(kCommands/s), dec_throughputReplica (p95)(kCommands/s), dec_latency (avg)(ms), dec_latency (med)(ms), dec_latency (p95)(ms), \n");
    
    datFile.append("\n").append("Ring counter: ").append("1 ring").append("\n");
    for (String other : others) {
      for (String commandsSize : commandsSizes) {
        for (String application : applications) {
          for (String threadCounter : threadCounters) {
            datFile.append(application
                + separator + threadCounter
                + separator + other
                + separator + commandsSize + ",");
            for (String loggerType : loggerTypes) {
              String folderPath = basePath + "1rings"
                  + "/" + application
                  + separator + loggerType
                  + separator + threadCounter
                  + separator + other
                  + separator + commandsSize
                  + "/";
                  String latencyReplicaPath = folderPath + "client_latency_1.csv";
                  String replicaPath = folderPath + "replica_1_ring_1.csv";
              String latencyAvg = readAvgLatency(latencyReplicaPath);
              String latencyP50 = readPercentilLatency(latencyReplicaPath, 50);
              String latencyP95 = readPercentilLatency(latencyReplicaPath, 95);
              String throughputAvgReplica = readAvgThroughput(replicaPath);
              String throughputP50Replica = readPercentilThroughput(replicaPath, 50);
              String throughputP95Replica = readPercentilThroughput(replicaPath, 95);

              datFile
                  .append(throughputAvgReplica + ",")
                  .append(throughputP50Replica + ",")
                  .append(throughputP95Replica + ",")
                  .append(latencyAvg + ",")
                  .append(latencyP50 + ",")
                  .append(latencyP95 + ",");
            }
            datFile.append("\n");
          }
          datFile.append("\n");
        }
      }
    }
    datFile.append("\n").append("\n");

    datFile
    .append("name, sem_throughputReplica (avg)(kCommands/s), sem_throughputReplica (med)(kCommands/s), sem_throughputReplica (p95)(kCommands/s), sem_latency (avg)(ms), sem_latency (med)(ms), sem_latency (p95)(ms), ")
    .append(
        "cou_throughputReplica (avg)(kCommands/s), cou_throughputReplica (med)(kCommands/s), cou_throughputReplica (p95)(kCommands/s), cou_latency (avg)(ms), cou_latency (med)(ms), cou_latency (med)(ms), ")
    .append(
        "dec_throughputReplica (avg)(kCommands/s), dec_throughputReplica (med)(kCommands/s), dec_throughputReplica (p95)(kCommands/s), dec_latency (avg)(ms), dec_latency (med)(ms), dec_latency (p95)(ms), \n");
        datFile.append("\n").append("Ring counter: ").append("2 rings").append("\n");
    for (String other : others) {
      for (String commandsSize : commandsSizes) {
        for (String application : applications) {
          for (String threadCounter : threadCounters) {
            datFile.append(application
                + separator + threadCounter
                + separator + other
                + separator + commandsSize + ",");
            for (String loggerType : loggerTypes) {
              String folderPath = basePath + "2rings"
                  + "/" + application
                  + separator + loggerType
                  + separator + threadCounter
                  + separator + other
                  + separator + commandsSize
                  + "/";
              String latencyPath = folderPath + "client_latency_1.csv";
              String replicaPath = folderPath + "replica_1_ring_1.csv";
              String latencyAvg = readAvgLatency(latencyPath);
              String latencyP50 = readPercentilLatency(latencyPath, 50);
              String latencyP95 = readPercentilLatency(latencyPath, 95);
              String throughputAvgReplica = readAvgThroughput(replicaPath);
              String throughputP50Replica = readPercentilThroughput(replicaPath, 50);
              String throughputP95Replica = readPercentilThroughput(replicaPath, 95);

              datFile
                  .append(throughputAvgReplica + ",")
                  .append(throughputP50Replica + ",")
                  .append(throughputP95Replica + ",")
                  .append(latencyAvg + ",")
                  .append(latencyP50 + ",")
                  .append(latencyP95 + ",");
            }
            datFile.append("\n");
          }
          datFile.append("\n");
        }
      }
    }

    datFile
    .append("name, sem_throughputReplica (avg)(kCommands/s), sem_throughputReplica (med)(kCommands/s), sem_throughputReplica (p95)(kCommands/s), sem_latency (avg)(ms), sem_latency (med)(ms), sem_latency (p95)(ms), ")
    .append(
        "cou_throughputReplica (avg)(kCommands/s), cou_throughputReplica (med)(kCommands/s), cou_throughputReplica (p95)(kCommands/s), cou_latency (avg)(ms), cou_latency (med)(ms), cou_latency (med)(ms), ")
    .append(
        "dec_throughputReplica (avg)(kCommands/s), dec_throughputReplica (med)(kCommands/s), dec_throughputReplica (p95)(kCommands/s), dec_latency (avg)(ms), dec_latency (med)(ms), dec_latency (p95)(ms), \n");
   datFile.append("\n").append("Ring counter: ").append("4 rings").append("\n");
    for (String other : others) {
      for (String commandsSize : commandsSizes) {
        for (String application : applications) {
          for (String threadCounter : threadCounters) {
            datFile.append(application
                + separator + threadCounter
                + separator + other
                + separator + commandsSize + ",");
            for (String loggerType : loggerTypes) {
              String folderPath = basePath + "4rings"
                  + "/" + application
                  + separator + loggerType
                  + separator + threadCounter
                  + separator + other
                  + separator + commandsSize
                  + "/";
              String latencyPath = folderPath + "client_latency_1.csv";
              String replicaPath = folderPath + "replica_1_ring_1.csv";
              String latencyAvg = readAvgLatency(latencyPath);
              String latencyP50 = readPercentilLatency(latencyPath, 50);
              String latencyP95 = readPercentilLatency(latencyPath, 95);
              String throughputAvgReplica = readAvgThroughput(replicaPath);
              String throughputP50Replica = readPercentilThroughput(replicaPath, 50);
              String throughputP95Replica = readPercentilThroughput(replicaPath, 95);

              datFile
                  .append(throughputAvgReplica + ",")
                  .append(throughputP50Replica + ",")
                  .append(throughputP95Replica + ",")
                  .append(latencyAvg + ",")
                  .append(latencyP50 + ",")
                  .append(latencyP95 + ",");
            }
            datFile.append("\n");
          }
          datFile.append("\n");
        }
      }
    }


    datFile.append("\n\n\n").append("Ring counter: ").append("1 ring sync/async thinking time de 3 ").append("\n");
    basePath = "/home/joaolucas/code/shared-logger-client/evaluation/thinking_time_3/";
    applications = List.of("cpu");
    loggerTypes = List.of("sem");
    datFile
    .append("name, sem_throughputReplica (avg)(kCommands/s), sem_throughputReplica (med)(kCommands/s), sem_throughputReplica (p95)(kCommands/s), sem_latency (avg)(ms), sem_latency (med)(ms), sem_latency (p95)(ms), ")
    .append(
        "cou_throughputReplica (avg)(kCommands/s), cou_throughputReplica (med)(kCommands/s), cou_throughputReplica (p95)(kCommands/s), cou_latency (avg)(ms), cou_latency (med)(ms), cou_latency (med)(ms), ")
    .append(
        "dec_throughputReplica (avg)(kCommands/s), dec_throughputReplica (med)(kCommands/s), dec_throughputReplica (p95)(kCommands/s), dec_latency (avg)(ms), dec_latency (med)(ms), dec_latency (p95)(ms), \n");
    commandsSizes = List.of("001", "001_sync","001_async");
    for (String other : others) {
      for (String commandsSize : commandsSizes) {
        for (String application : applications) {
          for (String threadCounter : threadCounters) {
            datFile.append(application
                + separator + threadCounter
                + separator + other
                + separator + commandsSize + ",");
            for (String loggerType : loggerTypes) {
              String folderPath = basePath + "1rings"
                  + "/" + application
                  + separator + loggerType
                  + separator + threadCounter
                  + separator + other
                  + separator + commandsSize
                  + "/";
              String latencyReplicaPath = folderPath + "client_latency_1.csv";
              String replicaPath = folderPath + "replica_1_ring_1.csv";
              System.out.println("latencyReplicaPath: " + latencyReplicaPath);
              System.out.println("replicaPath: " + replicaPath);
              System.out.println("-------");
              String latencyAvg = readAvgLatency(latencyReplicaPath);
              String latencyP50 = readPercentilLatency(latencyReplicaPath, 50);
              String latencyP95 = readPercentilLatency(latencyReplicaPath, 95);
              String throughputAvgReplica = readAvgThroughput(replicaPath);
              String throughputP50Replica = readPercentilThroughput(replicaPath, 50);
              String throughputP95Replica = readPercentilThroughput(replicaPath, 95);

              datFile
                  .append(throughputAvgReplica + ",")
                  .append(throughputP50Replica + ",")
                  .append(throughputP95Replica + ",")
                  .append(latencyAvg + ",")
                  .append(latencyP50 + ",")
                  .append(latencyP95 + ",");
            }
            datFile.append("\n");
          }
          datFile.append("\n");
        }
      }
    }
    datFile.append("\n").append("\n");

    return Response.ok(datFile.toString()).build();

  }

  private String readAvgThroughput(String replicaPath) throws Exception {
    java.nio.file.Path path = Paths.get(replicaPath);
    String regexIsNumberOrComma = "^(\\d+?,? ?)*$";
    Pattern patternIsNumberOrComma = Pattern.compile(regexIsNumberOrComma);

    String regexIsZeroThroughput = "^.* 0, 0$";
    Pattern patternIsZeroThroughput = Pattern.compile(regexIsZeroThroughput);
    if (!path.toFile().exists())
      return "0.00";
    List<String> allLines = Files.readAllLines(path);

    if (allLines.isEmpty()) {
      logger.warn("File [{}] is empty", replicaPath);
      return "0.00";
    }

    String[] splittedLastLine = allLines.stream()
        .filter(l -> patternIsNumberOrComma.matcher(l).find())
        .reduce((a, b) -> b).orElse("").split(", ");
    String regexLastLine;

    if (splittedLastLine.length > 1) {
      regexLastLine = "^.* 0, " + splittedLastLine[2] + "$";
    } else {
      logger.warn("Laste line for file [{}] is [{}]", replicaPath, allLines.get(allLines.size() - 1));
      regexLastLine = "^.* 0, 0$";
    }
    Pattern patternRegexLastLine = Pattern.compile(regexLastLine);

    long size = allLines.stream()
        .filter(l -> patternIsNumberOrComma.matcher(l).find())
        .filter(l -> !patternIsZeroThroughput.matcher(l).find())
        .filter(l -> !patternRegexLastLine.matcher(l).find())
        .skip(2).count() - 2;
    if (size < 0) {
      logger.warn("Could not find size for replica file [{}]", replicaPath);
      return "0.00";
    }

    Double avg = allLines.stream()
        .filter(l -> patternIsNumberOrComma.matcher(l).find())
        .filter(l -> !patternIsZeroThroughput.matcher(l).find())
        .filter(l -> !patternRegexLastLine.matcher(l).find())
        .skip(2)
        .limit(size)
        .mapToLong(
            line -> Long.parseLong(line.split(", ")[1]))
        .average().orElse(0D) / 1_000D;

    return String.format(Locale.ROOT, "%.2f", avg);
  }

  private String readPercentilThroughput(String replicaPath, int percentil) throws Exception {
    java.nio.file.Path path = Paths.get(replicaPath);
    String regexIsNumberOrComma = "^(\\d+?,? ?)*$";
    Pattern patternIsNumberOrComma = Pattern.compile(regexIsNumberOrComma);

    String regexIsZeroThroughput = "^.* 0, 0$";
    Pattern patternIsZeroThroughput = Pattern.compile(regexIsZeroThroughput);
    if (!path.toFile().exists())
      return "0.00";
    List<String> allLines = Files.readAllLines(path);

    if (allLines.isEmpty()) {
      logger.warn("File [{}] is empty", replicaPath);
      return "0.00";
    }

    String[] splittedLastLine = allLines.stream()
        .filter(l -> patternIsNumberOrComma.matcher(l).find())
        .reduce((a, b) -> b).orElse("").split(", ");
    String regexLastLine;

    if (splittedLastLine.length > 1) {
      regexLastLine = "^.* 0, " + splittedLastLine[2] + "$";
      // logger.info("[{}] -> [{}]", replicaPath, regexLastLine);
    } else {
      logger.warn("Laste line for file [{}] is [{}]", replicaPath, allLines.get(allLines.size() - 1));
      regexLastLine = "^.* 0, 0$";
      return "0.0";
    }
    Pattern patternRegexLastLine = Pattern.compile(regexLastLine);

    long size = allLines.stream()
        .filter(l -> patternIsNumberOrComma.matcher(l).find())
        .filter(l -> !patternIsZeroThroughput.matcher(l).find())
        .filter(l -> !patternRegexLastLine.matcher(l).find())
        .skip(2).count() - 2;
    if (size < 0) {
      logger.warn("Could not find size for replica file [{}]", replicaPath);
      return "0.00";
    }

    double p = allLines.stream()
        .filter(l -> patternIsNumberOrComma.matcher(l).find())
        .filter(l -> !patternIsZeroThroughput.matcher(l).find())
        .filter(l -> !patternRegexLastLine.matcher(l).find())
        .skip(2)
        .limit(size)
        .mapToDouble(
            line -> Long.parseLong(line.split(", ")[1]))
        .sorted()
        .skip((size * percentil) / 100)
        .findFirst()
        .orElse(-1L) / 1_000D;

    return String.format(Locale.ROOT, "%.2f", p);
  }

  private String readAvgLatency(String latencyPath) throws Exception {
    java.nio.file.Path path = Paths.get(latencyPath).normalize();

    if (!path.toFile().exists())
      return "0.00";

    String regexIsZeroLatency = "^.*,0$";
    Pattern patternIsZeroLatency = Pattern.compile(regexIsZeroLatency);
    long size = Files.readAllLines(path).stream()
        .filter(l -> !patternIsZeroLatency.matcher(l).find())
        .skip(2).count() - 2;

    if (size < 0) {
      size = 1;
      logger.warn("Could not find size for latency file [{}]", latencyPath);
    }

    Double avg = Files.readAllLines(path).stream()
        .filter(l -> !patternIsZeroLatency.matcher(l).find())
        .skip(2)
        .limit(size)
        .mapToLong(
            line -> Long.parseLong(line.split(",")[1]))
        .average().orElse(0D) / 1000_000D;

    return String.format(Locale.ROOT, "%.2f", avg);
  }

  private String readPercentilLatency(String latencyPath, int percentil) throws Exception {
    java.nio.file.Path path = Paths.get(latencyPath).normalize();

    if (!path.toFile().exists())
      return "0.00";

    String regexIsZeroLatency = "^.*,0$";
    Pattern patternIsZeroLatency = Pattern.compile(regexIsZeroLatency);
    long size = Files.readAllLines(path).stream()
        .filter(l -> !patternIsZeroLatency.matcher(l).find())
        .skip(2).count() - 2;

    if (size < 0) {
      size = 1;
      logger.warn("Could not find size for latency file [{}]", latencyPath);
      return "0.0";
    }

    Double p = Files.readAllLines(path).stream()
        .filter(l -> !patternIsZeroLatency.matcher(l).find())
        .skip(2)
        .limit(size)
        .mapToLong(
            line -> Long.parseLong(line.split(",")[1]))
        .sorted()
        .skip((size * percentil) / 100)
        .findFirst()
        .orElse(-1L) / 1000_000D;

    return String.format(Locale.ROOT, "%.2f", p);
  }
}