package online.malleautrue.vertx.cluster;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Verticle;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;
import java.util.Map;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by ftronche@akio.com on 23/11/16.
 */
public interface SimpleCluster {

    /**
     * Create a new {@link Member} for the {@link SimpleCluster} : it could be local, when passing true to the local parameter.
     *
     * @param hostorIp  the host name, or IP adress.
     * @param local     true if local, else false.
     * @param verticles each verticle need to be specified, running in this {@link Member} of the {@link SimpleHazelcastCluster}.
     * @return the reated new {@link Member}.
     */
    static Member newMember(@NotNull String hostorIp, @NotNull boolean local, String... verticles) {
        final Member m = new Member();
        m.hostOrIp = hostorIp;
        m.local = local;
        for (final String abstractVerticle : verticles) {
            m.verticles.add(abstractVerticle);
        }
        return m;
    }

    /**
     * Create a new {@link Member} for the {@link SimpleCluster} : it could be local, when passing true to the local parameter.
     *
     * @param hostorIp the host name, or IP adress.
     * @param local    true if local, else false.
     * @param verticle each verticle need to be specified, running in this {@link Member} of the {@link SimpleHazelcastCluster}.
     * @return the reated new {@link Member}.
     */
    static Member newRequiredMember(@NotNull String hostorIp, @NotNull boolean local, @NotNull String verticle) {
        final Member m = new Member();
        m.required = true;
        m.hostOrIp = hostorIp;
        m.local = local;

        m.verticles.add(verticle);
        return m;
    }

    /**
     * Factory method for creating a new local {@link Member}. The local address is resolve internally.
     *
     * @param verticles the verticle to launch in the new created {@link Member}.
     * @return a new local {@link Member}.
     */
    static Member newLocalMember(@NotNull String domainOrIp, @NotNull String... verticles) {
        return newMember(domainOrIp, true, verticles);
    }

    /**
     * Factory method for creating a new {@link SimpleHazelcastCluster} instance, enclosing the previous, created {@link Member}'s.
     *
     * @param ipInterface the IP interface of the {@link SimpleHazelcastCluster}: it need to be a valid
     * @param members     the other {@link Member} of the {@link SimpleHazelcastCluster}.
     * @return a {@link SimpleHazelcastCluster} instance enclosing all the {@link Member}.
     */
    static SimpleCluster newSimpleCluster(@NotNull String ipInterface, @NotNull Member... members) {
        return new SimpleHazelcastCluster(ipInterface, members);
    }

    /**
     * Remove the {@link Member} localMemberToRemove, from the {@link SimpleCluster} instance.
     *
     * @param localMemberToRemove the {@link Member} to remove.
     * @return true, if removed, else throw an {@link RuntimeException}
     */
    @AssertTrue
    boolean removeLocalMember(@NotNull Member localMemberToRemove, @NotNull Handler<AsyncResult<Void>> handler);

    boolean started();

    void start();

    void start(@NotNull Handler<AsyncResult<?>> handler);

    /**
     * {@link Member} static class.
     * All fields are protected, wich were resolve internally bi the {@link SimpleHazelcastCluster}.
     * These fields are not visible from outside.
     */
    class Member {
        protected Stack<String> verticles = new Stack<>();
        protected String hostOrIp;
        protected boolean local;
        protected Map<String, String> verticleIds = new ConcurrentHashMap<>();
        protected boolean required = false;
        protected Verticle verticle;

        @Override
        public String toString() {
            return ToStringBuilder.reflectionToString(this);
        }
    }
}
