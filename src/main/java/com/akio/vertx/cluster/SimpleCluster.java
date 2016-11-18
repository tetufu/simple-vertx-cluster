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
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * a Simple cluster
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

    public final static SimpleCluster newSimpleCluster(@NotNull final Member... members) {
        return new SimpleCluster(members);
    }


    public static class Member {
        private Stack<String> verticles = new Stack<>();
        private String hostOrIp;
        private boolean local;

        @Override
        public String toString() {
            return ToStringBuilder.reflectionToString(this);
        }
    }

    private final static Logger LOGGER = LoggerFactory.getLogger(SimpleCluster.class.getName());
    private final List<Member> members = new ArrayList<>();
    private Member localMember;

    protected SimpleCluster(@NotNull final Member... members) {
        super();
        for (Member member : members) {
            this.members.add(member);
        }

    }

    public Member getLocalMember() {
        return localMember;
    }

    public SimpleCluster setLocalMember(Member localMember) {
        this.localMember = localMember;
        return this;
    }

    public void start() throws UnknownHostException {
        run();
    }

    public void run() throws UnknownHostException {
        // create an hazelcastConfig instanceTestContext context
        //final VertxOptions vertxOptions = setUpCluster();
        for (Member member : members) {
            launchClusteredVerticle(setUpCluster(), member);
        }
    }

    private VertxOptions setUpCluster() throws UnknownHostException {
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
        InetAddress inet4Address = Inet4Address.getLocalHost();
        LOGGER.info("Local InnetAddress: " + inet4Address.getHostName());
        // set up the network interface
        hazelcastConfig.getNetworkConfig().setInterfaces(new InterfacesConfig().addInterface("10.34.1.9").setEnabled(true));

        // create the ClusterManager whith the hazelcastConfig
        final ClusterManager
                mgr = new HazelcastClusterManager(hazelcastConfig);
        return new VertxOptions().setClustered(true).setClusterManager(mgr);
    }

    private void launchClusteredVerticle(final VertxOptions vertxOptions, final Member member) {

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
