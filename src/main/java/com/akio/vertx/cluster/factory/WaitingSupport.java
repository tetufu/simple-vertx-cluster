package com.akio.vertx.cluster.factory;

/**
 * Created by ftronche@akio.com on 18/11/16.
 */
public interface WaitingSupport {
    static void waitFor(long milliseconds) {
        try { Thread.currentThread().sleep(milliseconds); } catch (InterruptedException e) {/* ignore */ }
    }
}
