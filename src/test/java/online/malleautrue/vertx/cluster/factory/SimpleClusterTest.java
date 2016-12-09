package online.malleautrue.vertx.cluster.factory;

import io.vertx.core.Vertx;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import online.malleautrue.vertx.cluster.Server;
import online.malleautrue.vertx.cluster.SimpleCluster;
import online.malleautrue.vertx.cluster.SimpleHazelcastCluster;
import org.awaitility.Duration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.UnknownHostException;

import static online.malleautrue.vertx.cluster.SimpleCluster.*;
import static org.awaitility.Awaitility.await;

/**
 * Created by ftronche@akio.com on 17/11/16.
 */
@RunWith(VertxUnitRunner.class)
public class SimpleClusterTest implements WaitingSupport {
    private final static Logger LOGGER = LoggerFactory.getLogger(SimpleClusterTest.class.getName());

    private Vertx vertx;


    @Before
    public void before(TestContext context) throws Exception {
        vertx = Vertx.vertx();
    }

    @After
    public void after(TestContext context) {
        context.async().complete();
        vertx.close(context.asyncAssertSuccess());
    }

    @Test
    public void testCreateMember(TestContext context) throws InterruptedException, UnknownHostException {
        //
        Async async = context.async();
        final Member member = newMember("uicdev.akio.fr", true, Server.class.getName(), Server.class.getName());
        final Member member2 = newMember("127.0.0.1", true, Server.class.getName());
        final SimpleCluster simpleCluster = SimpleCluster.newSimpleCluster("10.34.1.9", member, member2);
        await().until((SimpleHazelcastCluster) simpleCluster);
        await().atMost(Duration.ONE_MINUTE);
    }

    @Test
    public void testCreateRequiredMember(TestContext context) throws InterruptedException, UnknownHostException {
        Async async = context.async();
        final Member member = newLocalMember("uicdev.akio.fr", Server.class.getName());
        final Member member2 = newMember("127.0.0.1", true, Server.class.getName());
        final SimpleCluster simpleCluster = newSimpleCluster("10.34.1.9", member, member2);
        startCluster(simpleCluster);

        await().atMost(Duration.ONE_MINUTE);
        simpleCluster.removeLocalMember(member2, context.asyncAssertSuccess());
        async.complete();
        await().atMost(Duration.ONE_MINUTE);
    }

    private void startCluster(SimpleCluster simpleCluster) {
        simpleCluster.start(errorHandler -> {
            if (errorHandler.succeeded()) {
                errorHandler.result();
            } else if (errorHandler.failed()) {
                LOGGER.error("Failed to deploy Verticle: ", errorHandler.cause());
            }
        });
    }
}