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

import io.quarkus.runtime.ShutdownEvent;

@Path("/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class LoggerController {

    @Inject
    @ConfigProperty(name = "nodes.replicas.url", defaultValue = "127.0.0.1:8080")
    List<String> nodesReplicas;
    List<LoggerRestClient> loggerRestClients;

    List<LoggerClient> loggerClients;

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

    @GET
    @Path("ping")
    public Response ping() {
        return Response.ok().build();
    }

    @POST
    @Path("init")
    public Response initLogs(LoggerConfig config) {
        LoggerClient loggerClient = new MultRingPaxosLoggerClient(config.url, config.id, config.ring);
        loggerClients.add(loggerClient);

        return Response.ok().build();
    }

    @POST
    @Path("register")
    public Response registerLogger(LoggerConfig config) {
        int clusterSize = nodesReplicas.size();
        int initialId = (((int) (Math.random() * clusterSize) % clusterSize));
        for (int i = 0; i < initialId; i++) {
            int urlId = (initialId + i) % clusterSize;
            
            loggerRestClients.get(urlId).initLogs(config);
        }
        return Response.ok().build();
    }

    @GET
    public Response getAll() {
        return Response.ok( //
                loggerClients.stream().map(LoggerClient::getAllLogs).collect(Collectors.toList())//
        ).build();
    }
}