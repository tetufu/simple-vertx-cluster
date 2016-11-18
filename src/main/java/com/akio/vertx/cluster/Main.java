package com.akio.vertx.cluster;

import java.net.UnknownHostException;

/**
 * Created by ftronche@akio.com on 17/11/16.
 */
public class Main {
    public static void main(String... args) throws UnknownHostException {
        SimpleCluster.Member member1 = SimpleCluster.newLocalMember(Server.class.getName(), Server.class.getName());
        SimpleCluster.Member member2 = SimpleCluster.newMember("10.34.1.9", true, Server.class.getName());
        SimpleCluster simpleCluster = SimpleCluster.newSimpleCluster(member1, member2);
        simpleCluster.start();
    }
}
