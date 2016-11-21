package com.akio.vertx.cluster;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.akio.vertx.cluster.SimpleCluster.*;
import static com.akio.vertx.cluster.factory.WaitingSupport.waitFor;

/**
 * Created by ftronche@akio.com on 17/11/16.
 */
public class Main {
    private final static Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(String... args) {
        // create a local member of the cluster. It is possible to launch multiple time the same Verticle instance
        final Member member1 = newLocalMember(Server.class.getName(), Server.class.getName());
        // create one remote member, with an other guy of the Feature Team
        final Member member2 = newMember("10.34.1.23", false, Server.class.getName());
        // create another local member
        final Member member3 = newLocalMember("uicdev.akio.fr",Server.class.getName(), Server.class.getName());
        // create the cluster instance with the previous created members, and required IP of the bounded interface.
        final SimpleCluster simpleCluster = newSimpleCluster("10.34.1.9", member1, member2, member3);
        // start the cluster.
        simpleCluster.start();
        LOGGER.info("Waiting 10 seconds while the cluster is started ");
        waitFor(10000);

        LOGGER.info("Try to remove a member from the SimpleCluster : " + member3);
        simpleCluster.removeLocalMember(member3);
    }
}
