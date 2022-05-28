package org.acme;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.usi.da.smr.Replica;
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
        ReplicaLoggerClient replicaLoggerClient = new ReplicaLoggerClient("0", config.ring, config.id, 0, zookeeperUrl);
        replicaLoggerClient.start();
        loggerClients.add(replicaLoggerClient);
        return Response.ok().build();
    }

    @GET
    @Path("ping")
    public Response ping() {
        logger.info("Pong");
        return Response.ok().build();
    }

    @POST
    @Path("init")
    public Response initLogs(LoggerConfig config) {
        logger.info("Initing config [{}]", config);

        LoggerClient loggerClient = new MultiRingPaxosLoggerClient(zookeeperUrl, config.id, config.ring);
        loggerClients.add(loggerClient);

        return Response.ok().build();
    }

    @POST
    @Path("register")
    public Response registerLogger(LoggerConfig config) {
        logger.info("Registering config [{}]", config);
        int clusterSize = nodesReplicas.size();
        if (clusterSize <= 0)
            clusterSize = 1;
        int initialId = (((int) (Math.random() * clusterSize) % clusterSize));
        for (int i = 0; i < clusterSize; i++) {
            int newId = (int) (Math.random() * clusterSize * 100);
            int urlId = (initialId + i) % clusterSize;
            logger.info("Calling logger in url id [{}] with id [{}]", urlId, newId);
            config.id = newId;
            loggerRestClients.get(urlId).initLogs(config);
        }
        return Response.ok().build();
    }

    @GET
    @Path("getAll")
    public Response getAll() {
        logger.info("Received a get all request.");
        List<List<String>> loggedValues = loggerClients.stream().map(LoggerClient::getAllLogs)
                .collect(Collectors.toList());
        logger.info("Size [{}]", loggedValues.size());

        return Response.ok(loggedValues).build();
    }
}