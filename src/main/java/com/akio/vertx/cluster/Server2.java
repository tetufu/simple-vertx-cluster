package com.akio.vertx.cluster;


import com.hazelcast.config.Config;
import com.hazelcast.config.InterfacesConfig;
import com.hazelcast.config.MulticastConfig;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;

import java.lang.management.ManagementFactory;

/**
 * A simple clustered verticle http server service,
 * launched 3 times in the same JVM, listening on a different port.
 */
@Deprecated
public class Server2 extends AbstractVerticle {
    // a static http port wich will be increment each time.
    static int httpPort = 8080;

    // Convenience method so you can run it in your IDE
    public static void main(String[] args) {
        launchMember(0);
        launchMember(1);
        launchMember(2);
    }

    @Override
    public void start() throws Exception {
        vertx.createHttpServer().requestHandler(req -> {
            final String name = ManagementFactory.getRuntimeMXBean().getName();
            req.response().end("Happily served by " + name + " instance: " + toString());
        }).// increment the static httpPort value, listening on different httpPort for each server instance.
                listen(httpPort++);
    }

    private static void launchMember(final int number) {
        // create an hazelcastConfig instance
        final Config hazelcastConfig = new Config();
        final MulticastConfig multicastConfig = new MulticastConfig().setEnabled(true);

        // set up the network interface
        hazelcastConfig.getNetworkConfig().setInterfaces(new InterfacesConfig().addInterface("10.34.*.*").setEnabled(true));
        // join the multicast config
        hazelcastConfig.getNetworkConfig().getJoin().setMulticastConfig(multicastConfig);

        // create the ClusterManager whith the hazelcastConfig
        final ClusterManager
                mgr = new HazelcastClusterManager(hazelcastConfig);
        final VertxOptions vertxOptions = new VertxOptions().setClustered(true).setClusterManager(mgr);

        launchClusteredVerticle(vertxOptions);
    }

    private static void launchClusteredVerticle(final VertxOptions vertxOptions) {
        Vertx.clusteredVertx(vertxOptions, res -> {
            if (res.succeeded()) {
                res.result().deployVerticle(Server.class.getName());
            }
        });
    }
}

