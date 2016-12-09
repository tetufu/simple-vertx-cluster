package online.malleautrue.vertx.cluster;

import com.hazelcast.config.Config;
import com.hazelcast.config.InterfacesConfig;
import com.hazelcast.config.TcpIpConfig;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * a Simple cluster for {@link Vertx} technologie.
 * Created by ftronche@akio.com on 17/11/16.
 */
public class SimpleHazelcastCluster extends Thread implements Runnable, SimpleCluster {


    /**
     * a {@link Logger} for the class
     */
    private final static Logger LOGGER = LoggerFactory.getLogger(SimpleHazelcastCluster.class.getName());
    private final List<Member> members = new ArrayList<>();
    private String ipInterface;
    //private Thread thread;
    private Handler<AsyncResult<?>> startHandler;
    private boolean started = false;

    /**
     * Protected {@link SimpleHazelcastCluster} constructor. It's not possible to instantiate it, you need to pass by the static factory method {@link SimpleCluster#newMember(String, boolean, String...)} instead.
     *
     * @param ipInterface the IP interface of the cluster. It need to be a valid IP address
     * @param members     the other {@link Member} of the cluster.
     */
    protected SimpleHazelcastCluster(@NotNull final String ipInterface, @NotNull final Member... members) {
        super();
        this.ipInterface = ipInterface;
        for (Member member : members) {
            this.members.add(member);
        }

    }

    /**
     * start the {@link SimpleHazelcastCluster}.
     */
    @Override
    public void start() {
        super.start();
    }

    public void start(@NotNull Handler<AsyncResult<?>> handler) {
        this.startHandler = handler;
        this.start();
    }

    /**
     * Remove the {@link Member} named memberToRemove, from the current {@link SimpleHazelcastCluster} instance.
     *
     * @param localMemberToRemove the local {@link Member} to remove from the cuurent {@link SimpleHazelcastCluster} instance.
     * @return true if removed
     * @precondition localMemberToRemove.local need to be true, otherwith it will throw a {@link RuntimeException}.
     */
    @Override
    public boolean removeLocalMember(@NotNull final Member localMemberToRemove, @NotNull Handler<AsyncResult<Void>> handler) {

        assert localMemberToRemove.local;

        if (localMemberToRemove.local) {
            for (Member m : members) {
                if (m.equals(localMemberToRemove)) {
                    // remove the member from the members list.
                    members.remove(m);

                    // get ID's of running verticles members
                    String[] stringIds = m.verticleIds.keySet().toArray(new String[m.verticleIds.keySet().size()]);
                    for (String id : stringIds) {
                        undeployVerticle(id, handler);
                        LOGGER.info("undeployed verticle:" + id);
                    }
                    return true;
                }
            }
        } else {
            throw new ClusterRuntimeException("Can only remove local member! The member you're trying to remove is local:" + localMemberToRemove.local);
        }
        return false;
    }

    @Override
    public boolean started() {
        return false;
    }

    protected boolean undeployVerticle(@NotNull final String verticleId, @NotNull Handler<AsyncResult<Void>> handler) {
        Vertx.vertx().undeploy(verticleId, handler);
        return true;
    }

    /**
     * Run the {@link SimpleHazelcastCluster}. To call it, call the {@link SimpleHazelcastCluster#start()} method instead.
     */
    public void run() {
        for (Member member : members) {
            launchClusteredVerticle(member);
        }
        this.started = true;
    }

    /**
     * Set up the {@link SimpleHazelcastCluster}.
     *
     * @return the required {@link VertxOptions}, to launch the enclosed {@link Member} of the {@link SimpleHazelcastCluster}.
     */
    protected VertxOptions setUpCluster() {
        LOGGER.info("Set Up the cluster");
        final Config hazelcastConfig = new Config();

        final TcpIpConfig tcpIpConfig = new TcpIpConfig();
        for (Member member : members) {
            LOGGER.info("Cluster add a new Member:" + member);
            if (member.required) {
                tcpIpConfig.setRequiredMember(member.hostOrIp);
            } else {
                tcpIpConfig.addMember(member.hostOrIp);
            }
        }
        tcpIpConfig.setEnabled(true);
        // join the TcpIpConfig config
        hazelcastConfig.getNetworkConfig().getJoin().setTcpIpConfig(tcpIpConfig);
        // desactive multicast
        LOGGER.info("Multicast will be disabeled, the cluster work only with know hosts");
        hazelcastConfig.getNetworkConfig().getJoin().getMulticastConfig().setEnabled(false);

        // set up the network interface
        hazelcastConfig.getNetworkConfig().setInterfaces(new InterfacesConfig().addInterface(this.ipInterface).setEnabled(true));

        // create the ClusterManager whith the hazelcastConfig
        final ClusterManager
                mgr = new HazelcastClusterManager(hazelcastConfig);
        return new VertxOptions().setClustered(true).setClusterManager(mgr).setHAEnabled(true);
    }

    /**
     *
     * @param member
     */
    protected void launchClusteredVerticle(@NotNull final Member member) {

        if (member.local) {
            for (String vertxClass : member.verticles) {
                Vertx.clusteredVertx(setUpCluster(), res -> {
                    if (res.succeeded()) {
                        res.result().deployVerticle(vertxClass, ch -> {
                            if (ch.succeeded()) {
                                String id = ch.result();
                                member.verticleIds.put(id, vertxClass);
                               // member.verticle = this;
                                LOGGER.info("New Verticle launched: verticle: " + vertxClass + " id: " + id);
                            } else {
                                this.startHandler.handle(ch);
                            }
                        });
                    } else {
                        this.startHandler.handle(res);
                    }
                });
            }
        }
    }
}
