package com.akio.vertx.cluster.factory;

import static com.akio.vertx.cluster.factory.WaitingSupport.waitFor;

import com.akio.vertx.cluster.Server;
import com.akio.vertx.cluster.SimpleCluster;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.UnknownHostException;

/**
 * Created by ftronche@akio.com on 17/11/16.
 */
@RunWith(VertxUnitRunner.class)
public class SimpleClusterTest implements WaitingSupport {
    private final static Logger LOGGER = LoggerFactory.getLogger(SimpleClusterTest.class.getName());

    private Vertx vertx;
    HttpServer server;

    @Before
    public void before(TestContext context) throws Exception {
        vertx = Vertx.vertx();

    }

    @After
    public void after(TestContext context) {
        vertx.close(context.asyncAssertSuccess());
    }

    @Test
    public void testCreateMember(TestContext context) throws InterruptedException, UnknownHostException {
        //
        final SimpleCluster.Member member = SimpleCluster.newMember("uicdev.akio.fr", true, Server.class.getName(), Server.class.getName());
        final SimpleCluster simpleCluster = SimpleCluster.newSimpleCluster("10.34.1.9", member);
        simpleCluster.start();

        waitFor(10000);
        simpleCluster.removeLocalMember(member);
        waitFor(10000);
    }

}