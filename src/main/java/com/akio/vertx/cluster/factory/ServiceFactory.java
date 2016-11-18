package com.akio.vertx.cluster.factory;

import io.vertx.core.Vertx;
import io.vertx.servicediscovery.Record;
import io.vertx.servicediscovery.ServiceReference;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * The service factory interface.
 */
public interface ServiceFactory extends WaitingSupport {

    /**
     * Give the default factory implementation.
     * @return ServiceFactory the default.
     */
    static ServiceFactory getDefault() {
        return new DefaultServiceFactory();
    }


    /**
     *  Publish the verticle vertx, on an EventBus Record.
     * @param metadata some metadatas.
     * @return true
     */
    @AssertTrue
    boolean publish(@NotNull final ServiceMetadatas serviceMetadatas, final Map<String, Object> metadata);

    @AssertTrue
    boolean publish(@NotNull final ServiceMetadatas serviceMetadatas);

    @AssertTrue
    boolean publish(@NotNull final ServiceMetadataAware serviceMetadatas, final Map<String, Object> metadata);

    @AssertTrue
    boolean publish(@NotNull final ServiceMetadataAware serviceMetadatas);


    ServiceReference getServiceReference(@NotNull final Record record);

    void retrieveServiceRecords(@NotNull final Vertx vertx, @NotNull final String name, @NotNull final List<Record> resultHandler);

    enum KindOf {
        HTTP_ENDPOINT,
        EVENT_BUS,
        WEB_SOCKET,
    }
}
