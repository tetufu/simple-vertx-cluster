package com.akio.vertx.cluster;

import com.hazelcast.config.Config;
import com.hazelcast.config.TcpIpConfig;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.management.ManagementFactory;

/**
 *
 */
public class Server extends AbstractVerticle {
    private final static Logger LOGGER = LoggerFactory.getLogger(Server.class.getName());
    // a static http port wich will be increment each time.
//    static String[] members;
    static int httpPort;
//
//    static {
//        String membersStr = System.getProperty("members");
//        LOGGER.info("List of cluster member: " + membersStr);
//        members = membersStr.split(",");
//        httpPort = Integer.parseInt(System.getProperty("httpport"));
//        LOGGER.info("http port: " + httpPort);
//    }
//
//    // Convenience method so you can run it in your IDE
//    public static void main(String[] args) {
//        // each call to launchMember, laucnh a new member of cluster inside the same JVM.
//        launchMember();
////        launchMember();
////        launchMember();
//    }

    @Override
    public void start() throws Exception {
        vertx.createHttpServer().requestHandler(req -> {
            final String name = ManagementFactory.getRuntimeMXBean().getName();
            req.response().end("Happily served by " + name + " instance: " + toString());
        }).// increment the static httpPort value, listening on different httpPort for each server instance.
                listen(httpPort++);
    }

//    public static void launchMember() {
//        // create an hazelcastConfig instance
//        final VertxOptions vertxOptions = setUpCluster();
//
//        launchClusteredVerticle(vertxOptions);
//    }
//
//    private static VertxOptions setUpCluster() {
//
//        LOGGER.info("Set Up the cluster");
//        final Config hazelcastConfig = new Config();
//        final TcpIpConfig tcpIpConfig = new TcpIpConfig();
//        for (String member : members) {
//            LOGGER.info("Cluster add a new Member:" + member);
//            tcpIpConfig.addMember(member);
//        }
//        tcpIpConfig.setEnabled(true);
//        // join the TcpIpConfig config
//        hazelcastConfig.getNetworkConfig().getJoin().setTcpIpConfig(tcpIpConfig);
//        // desactive multicast
//        LOGGER.info("Multicast will be disabeled, the cluster work only with know host");
//        hazelcastConfig.getNetworkConfig().getJoin().getMulticastConfig().setEnabled(false);
//
//        // create the ClusterManager whith the hazelcastConfig
//        final ClusterManager
//                mgr = new HazelcastClusterManager(hazelcastConfig);
//        return new VertxOptions().setClustered(true).setClusterManager(mgr);
//    }
//
//    private static void launchClusteredVerticle(final VertxOptions vertxOptions) {
//        Vertx.clusteredVertx(vertxOptions, res -> {
//            if (res.succeeded()) {
//                res.result().deployVerticle(Server.class.getName());
//            }
//        });
//    }
}
