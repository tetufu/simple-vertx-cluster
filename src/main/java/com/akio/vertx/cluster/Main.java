package com.akio.vertx.cluster;

/**
 * Created by ftronche@akio.com on 17/11/16.
 */
public class Main {
    public static void main(String... args) {
        SimpleCluster.Member member1 = SimpleCluster.newLocalMember(Server.class.getName(), Server.class.getName());
        SimpleCluster simpleCluster = SimpleCluster.newSimpleCluster(member1);
        simpleCluster.start();
    }
}
