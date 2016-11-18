package com.akio.vertx.cluster;

import com.hazelcast.config.Config;
import com.hazelcast.config.InterfacesConfig;
import com.hazelcast.config.TcpIpConfig;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.NotNull;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * a Simple cluster for {@link Vertx} technologie.
 * Created by ftronche@akio.com on 17/11/16.
 */
public class SimpleCluster {

    /**
     * Create a new {@link Member} for the {@link SimpleCluster} : it could be local, when passing true to the local parameter.
     *
     * @param hostorIp  the host name, or IP adress.
     * @param local     true if local, else false.
     * @param verticles each verticle need to be specified, running in this {@link Member} of the {@link SimpleCluster}.
     * @return the reated new {@link Member}.
     */
    public final static Member newMember(@NotNull final String hostorIp, @NotNull final boolean local, final String... verticles) {
        final Member m = new Member();
        m.hostOrIp = hostorIp;
        m.local = local;
        for (final String abstractVerticle : verticles) {
            m.verticles.add(abstractVerticle);
        }
        return m;
    }

    /**
     * Factory method for creating a new local {@link Member}. The local address is resolve internally.
     *
     * @param verticles the verticle to launch in the new created {@link Member}.
     * @return a new local {@link Member}.
     */
    public final static Member newLocalMember(final String... verticles) {
        final Member m = new Member();
        try {
            m.hostOrIp = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {/* ignore*/}
        m.local = true;

        for (final String abstractVerticle : verticles) {
            m.verticles.add(abstractVerticle);
        }
        return m;
    }

    /**
     * Factory method for creating a new {@link SimpleCluster} instance, enclosing the previous, created {@link Member}'s.
     *
     * @param ipInterface the IP interface of the {@link SimpleCluster}: it need to be a valid
     * @param members     the other {@link Member} of the {@link SimpleCluster}.
     * @return a {@link SimpleCluster} instance enclosing all the {@link Member}.
     */
    public final static SimpleCluster newSimpleCluster(@NotNull final String ipInterface, @NotNull final Member... members) {
        return new SimpleCluster(ipInterface, members);
    }

    /**
     * {@link Member} static class.
     * All fields are private, wich where be resolve internally bi the {@link SimpleCluster}.
     * These fields are not visible from outside.
     */
    public static class Member {
        private Stack<String> verticles = new Stack<>();
        private String hostOrIp;
        private boolean local;

        @Override
        public String toString() {
            return ToStringBuilder.reflectionToString(this);
        }
    }

    /**
     * a {@link Logger} for the class
     */
    private final static Logger LOGGER = LoggerFactory.getLogger(SimpleCluster.class.getName());
    private final List<Member> members = new ArrayList<>();
    private String ipInterface;

    /**
     * Protected {@link SimpleCluster} constructor. It's not possible to instantiate it, you need to pass by the static factory method {@link SimpleCluster#newMember(String, boolean, String...)} instead.
     *
     * @param ipInterface the IP interface of the cluster. It need to be a valid IP address
     * @param members     the other {@link Member} of the cluster.
     */
    protected SimpleCluster(@NotNull final String ipInterface, @NotNull final Member... members) {
        super();
        this.ipInterface = ipInterface;
        for (Member member : members) {
            this.members.add(member);
        }

    }

    /**
     * start the {@link SimpleCluster}.
     */
    public void start() {
        run();
    }

    public boolean removeMember(@NotNull final Member memberToRemove) {
        for (Member m : members) {
            if (m.equals(memberToRemove)) {
                members.remove(m);
                return true;
            }
        }
        return false;
    }

    /**
     * Run the {@link SimpleCluster}. Note that this method is protected! To call it, call the {@link SimpleCluster#start()} method instead.
     */
    protected void run() {
        for (Member member : members) {
            launchClusteredVerticle(setUpCluster(), member);
        }
    }

    /**
     * Set up the {@link SimpleCluster}.
     *
     * @return the required {@link VertxOptions}, to launch the enclosed {@link Member} of the {@link SimpleCluster}.
     */
    protected VertxOptions setUpCluster() {
        LOGGER.info("Set Up the cluster");
        final Config hazelcastConfig = new Config();
        final TcpIpConfig tcpIpConfig = new TcpIpConfig();
        for (Member member : members) {
            LOGGER.info("Cluster add a new Member:" + member);
            tcpIpConfig.addMember(member.hostOrIp);
        }
        tcpIpConfig.setEnabled(true);
        // join the TcpIpConfig config
        hazelcastConfig.getNetworkConfig().getJoin().setTcpIpConfig(tcpIpConfig);
        // desactive multicast
        LOGGER.info("Multicast will be disabeled, the cluster work only with know host");
        hazelcastConfig.getNetworkConfig().getJoin().getMulticastConfig().setEnabled(false);

        // set up the network interface
        hazelcastConfig.getNetworkConfig().setInterfaces(new InterfacesConfig().addInterface(this.ipInterface).setEnabled(true));

        // create the ClusterManager whith the hazelcastConfig
        final ClusterManager
                mgr = new HazelcastClusterManager(hazelcastConfig);
        return new VertxOptions().setClustered(true).setClusterManager(mgr);
    }

    /**
     * @param vertxOptions
     * @param member
     */
    protected void launchClusteredVerticle(final VertxOptions vertxOptions, final Member member) {

        if (member.local) {
            for (String vertxClass : member.verticles) {
                Vertx.clusteredVertx(vertxOptions, res -> {
                    if (res.succeeded()) {
                        res.result().deployVerticle(vertxClass);
                    }
                });
            }

        }
    }
}
