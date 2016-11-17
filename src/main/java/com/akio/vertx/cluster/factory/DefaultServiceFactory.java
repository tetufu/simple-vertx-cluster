package com.akio.vertx.cluster.factory;

import io.vertx.core.AsyncResult;
import io.vertx.core.AsyncResultHandler;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.servicediscovery.Record;
import io.vertx.servicediscovery.ServiceDiscovery;
import io.vertx.servicediscovery.ServiceReference;
import io.vertx.servicediscovery.types.HttpEndpoint;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * The default service factory class, implementing the {@link ServiceFactory}
 */
public class DefaultServiceFactory implements ServiceFactory {

    /**
     * prevent creation from outside.
     */
    protected DefaultServiceFactory() {
        super();

    }

    @Override
    public boolean publish(@NotNull final ServiceMetadatas serviceMetadatas, Map<String, Object> metadata) {
        final ServiceDiscovery serviceDiscovery = ServiceDiscovery.create(serviceMetadatas.getVerticle());
        Record record;
        switch (serviceMetadatas.getServiceType()) {
            case HTTP_ENDPOINT:
                record = createHttpEndpointRecord(serviceMetadatas, metadata);
                serviceDiscovery.publish(record, event -> {
                    if (event.succeeded()) {
                        Record publishedRecord = event.result();
                    } else {
                        // publication failed
                        throw new RuntimeException(event.cause());
                    }
                });
                return true;
            case EVENT_BUS:
                return true;
        }
        return false;
    }

    @Override
    public boolean publish(@NotNull final ServiceMetadatas serviceMetadatas) {
        return false;
    }

    @Override
    public boolean publish(@NotNull final ServiceMetadataAware serviceMetadataAware, Map<String, Object> metadata) {
        return publish(serviceMetadataAware.asServiceMetatdatas(), metadata);
    }

    @Override
    public boolean publish(@NotNull final ServiceMetadataAware serviceMetadatas) {
        return publish(serviceMetadatas, null);
    }

    @Override
    public ServiceReference getServiceReference(@NotNull final Record record) {
        final ServiceDiscovery serviceDiscovery = ServiceDiscovery.create(Vertx.vertx());
        return serviceDiscovery.getReference(record);
    }

    @Override
    public void retrieveServiceRecords(@NotNull Vertx vertx, @NotNull final String name, @NotNull final List<Record> lstRecords) {
        final ServiceDiscovery serviceDiscovery = ServiceDiscovery.create(vertx);
        serviceDiscovery.getRecords(new JsonObject().put("name", name), event -> {
            if (event.succeeded()) {
                lstRecords.addAll(event.result());
            }
        });
    }


    private Record createHttpEndpointRecord(ServiceMetadatas serviceMetadatas, Map<String, Object> metadata) {
        JsonObject jsonObject = new JsonObject(metadata);
        Record record = HttpEndpoint.createRecord(serviceMetadatas.getName(),
                serviceMetadatas.getHost(),
                serviceMetadatas.getPort(),
                serviceMetadatas.getRoot(),
                jsonObject);
        return record;
    }
}
