package online.malleautrue.vertx.cluster.factory;

import io.vertx.core.Vertx;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

/**
 * Service metadatas, to be used to register services own by the {@link io.vertx.servicediscovery.ServiceDiscovery}.
 */
public class ServiceMetadatas {

    private Vertx verticle;
    private ServiceFactory.KindOf serviceType;
    private String name;
    private String host;
    private String root;
    private int port;
    private Map<String, String> metadatas = new HashMap<>();



    public final static ServiceMetadatas create(final @NotNull Vertx verticle, final @NotNull ServiceFactory.KindOf serviceType, final @NotNull String name, Map<String, String> metadatas) {
        return new ServiceMetadatas(verticle, serviceType, name, metadatas);
    }

    public final static ServiceMetadatas create(final @NotNull Vertx verticle) {
        return new ServiceMetadatas(verticle);
    }

    @NotNull
    protected ServiceMetadatas(final @NotNull Vertx verticle, final @NotNull ServiceFactory.KindOf serviceType, final @NotNull String name, Map<String, String> metadatas) {
        this(verticle);
        this.verticle = verticle;
        this.serviceType = serviceType;
        this.name = name;
        this.metadatas = metadatas;
    }

    public Vertx getVerticle() {
        return verticle;
    }

    @NotNull
    public ServiceMetadatas setVerticle(final @NotNull Vertx verticle) {
        this.verticle = verticle;
        return this;
    }

    public ServiceFactory.KindOf getServiceType() {
        return serviceType;
    }

    @NotNull
    public ServiceMetadatas setServiceType(final @NotNull ServiceFactory.KindOf serviceType) {
        this.serviceType = serviceType;
        return this;
    }

    public String getName() {
        return name;
    }

    @NotNull
    public ServiceMetadatas setName(String name) {
        this.name = name;
        return this;
    }

    public Map<String, String> getMetadatas() {
        return metadatas;
    }

    @NotNull
    public ServiceMetadatas setMetadatas(Map<String, String> metadatas) {
        this.metadatas = metadatas;
        return this;
    }


    /**
     * Default constructor, protected prevent creation from outside. Use the static factory method {@link ServiceMetadatas#create(Vertx)} instead.
     */
    @NotNull
    protected ServiceMetadatas() {
        super();
    }

    /**
     * Default constructor, protected prevent creation from outside. Use the static factory method {@link ServiceMetadatas#create(Vertx)} instead.
     */
    @NotNull
    protected ServiceMetadatas(@NotNull final Vertx verticle) {
        this();
        this.verticle = verticle;
    }

    public String getRoot() {
        return root;
    }
    @NotNull
    public ServiceMetadatas setRoot(String root) {
        this.root = root;
        return this;
    }

    public int getPort() {
        return port;
    }

    @NotNull
    public ServiceMetadatas setPort(@NotNull int port) {
        this.port = port;
        return this;
    }

    public String getHost() {
        return host;
    }

    @NotNull
    public ServiceMetadatas setHost(String host) {
        this.host = host;
        return this;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
