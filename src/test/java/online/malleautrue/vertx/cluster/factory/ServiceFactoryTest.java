package online.malleautrue.vertx.cluster.factory;



import static online.malleautrue.vertx.cluster.factory.WaitingSupport.waitFor;

import online.malleautrue.vertx.cluster.Server;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.servicediscovery.Record;
import io.vertx.servicediscovery.ServiceReference;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Test for {@link ServiceFactory}
 * fabien.
 */
@RunWith(VertxUnitRunner.class)
public class ServiceFactoryTest implements ServiceMetadataAware {

    private final static Logger LOGGER = LoggerFactory.getLogger(ServiceFactoryTest.class.getName());

    private Vertx vertx;
    HttpServer server;

    @Before
    public void before(TestContext context) throws Exception {
        Server server = new Server();
        //  server.launchMember();
        //Thread.currentThread().sleep(10000);
        waitFor(10000);
        vertx = server.getVertx();

    }

    @After
    public void after(TestContext context) {
        vertx.close(context.asyncAssertSuccess());
    }

    @Test
    public void testCreateHttpEndpointRecord(TestContext context) {
        // get the default ServiceFactory instance
        ServiceFactory factory = ServiceFactory.getDefault();
        // Create ServiceMetadatas fore regisring the Service within the factory
        ServiceMetadatas serviceMetadatas = createServiceMetadatas();
        // publish the Service within the ServiceFactory
        boolean result = factory.publish(serviceMetadatas);

        // check the returned value
        context.assertTrue(result);
    }

    @Test
    public void testCreateHttpEndpointRecordWithServiceMetadatasAware(TestContext context) {
        // get the default ServiceFactory instance
        ServiceFactory factory = ServiceFactory.getDefault();

        // publish the Service within the ServiceFactory
        boolean result = factory.publish(this);

        // check the returned value
        context.assertTrue(result);
    }

    private ServiceMetadatas createServiceMetadatas() {
        return ServiceMetadatas.create(vertx)
                .setName(Server.class.getName())
                .setHost("localhost")
                .setPort(8080)
                .setRoot("/")
                .setServiceType(ServiceFactory.KindOf.HTTP_ENDPOINT);
    }

    @Test
    public void testGetServiceReferenceByName(TestContext testContext) throws InterruptedException {
        // get the default ServiceFactory instance
        ServiceFactory factory = ServiceFactory.getDefault();
        boolean published = factory.publish(createServiceMetadatas(), null);

        final List<Record> recordList = new ArrayList<>();

        factory.retrieveServiceRecords(vertx, Server.class.getName(), recordList);

        waitFor(200);

        if (!recordList.isEmpty()) {
            final ServiceReference reference = factory.getServiceReference(recordList.get(0));
            HttpClient httpClient = reference.get();

            httpClient.getNow("/", response -> {
                if (response.statusCode() == HttpResponseStatus.ACCEPTED.code()) {
                    response.bodyHandler(body -> {
                        ByteBuf byteBuf = body.getByteBuf().readBytes(body.getByteBuf().array().length);
                        String res = new String(byteBuf.array());
                        System.out.println(res);
                    });
                }
            });
            waitFor(5000);
        }
    }

    @Override
    public ServiceMetadatas asServiceMetatdatas() {
        return createServiceMetadatas();
    }
}
