package online.malleautrue.vertx.cluster;

import online.malleautrue.vertx.cluster.factory.ServiceFactory;
import online.malleautrue.vertx.cluster.factory.ServiceMetadataAware;
import online.malleautrue.vertx.cluster.factory.ServiceMetadatas;
import io.vertx.core.AbstractVerticle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.management.ManagementFactory;

/**
 *
 */
public class Server extends AbstractVerticle implements ServiceMetadataAware {
    private final static Logger LOGGER = LoggerFactory.getLogger(Server.class.getName());

    static int httpPort;

    @Override
    public void start() throws Exception {
        vertx.createHttpServer().requestHandler(req -> {
            final String name = ManagementFactory.getRuntimeMXBean().getName();
            LOGGER.info("Name of the Verticle instance: " + name);
            req.response().end("Happily served by " + name + " instance: " + toString());
        }).// increment the static httpPort value, listening on different httpPort for each server instance.
                listen(httpPort++);
    }

    @Override
    public ServiceMetadatas asServiceMetatdatas() {
        return ServiceMetadatas.create(this.getVertx(), ServiceFactory.KindOf.HTTP_ENDPOINT, this.getClass().getName(),null).setHost(System.getenv("HOSTNAME")).setPort(httpPort);
    }
}
