package online.malleautrue.vertx.cluster;

/**
 * Created by ftronche@akio.com on 24/11/16.
 */
public class ClusterRuntimeException extends RuntimeException {
    public ClusterRuntimeException() {
    }

    public ClusterRuntimeException(String message) {
        super(message);
    }

    public ClusterRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public ClusterRuntimeException(Throwable cause) {
        super(cause);
    }

    public ClusterRuntimeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
