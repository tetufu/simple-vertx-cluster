package com.akio.vertx.cluster;

import java.net.UnknownHostException;

/**
 * Created by ftronche@akio.com on 17/11/16.
 */
public class Main {
    public static void main(String... args) {
        // create a local member of the cluster. It is possible to launch multiple time the same Verticle instance
        final SimpleCluster.Member member1 = SimpleCluster.newLocalMember(Server.class.getName()
                                                                            , Server.class.getName());
        // create one remote member, with an other guy of the Feature Team
        final SimpleCluster.Member member2 = SimpleCluster.newMember("10.34.1.23",
                                                                        false,
                                                                            Server.class.getName());
        // create another local member
        final SimpleCluster.Member member3 = SimpleCluster.newLocalMember(Server.class.getName(), Server.class.getName());
        // create the cluster instance with the previous created members, and required IP of the bound interface.
        final SimpleCluster simpleCluster = SimpleCluster.newSimpleCluster("10.34.1.9",
                                                                                member1, member2, member3);
        // start the cluster.
        simpleCluster.start();
    }
}
