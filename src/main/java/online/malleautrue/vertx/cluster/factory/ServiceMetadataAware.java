package online.malleautrue.vertx.cluster.factory;

import javax.validation.constraints.NotNull;

/**
 * Give a behavior to get out as a {@link ServiceMetadatas} instance.
 * @ftronche@akio.com
 */
public interface ServiceMetadataAware extends WaitingSupport {

    @NotNull
    ServiceMetadatas asServiceMetatdatas();

}
