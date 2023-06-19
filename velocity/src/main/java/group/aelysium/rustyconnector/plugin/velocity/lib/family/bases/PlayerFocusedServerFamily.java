package group.aelysium.rustyconnector.plugin.velocity.lib.family.bases;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.ServerInfo;
import group.aelysium.rustyconnector.plugin.velocity.VelocityRustyConnector;
import group.aelysium.rustyconnector.plugin.velocity.central.VelocityAPI;
import group.aelysium.rustyconnector.plugin.velocity.lib.load_balancing.LoadBalancer;
import group.aelysium.rustyconnector.plugin.velocity.lib.server.PlayerServer;
import group.aelysium.rustyconnector.plugin.velocity.lib.server.ServerService;
import group.aelysium.rustyconnector.plugin.velocity.lib.whitelist.Whitelist;
import group.aelysium.rustyconnector.plugin.velocity.lib.tpa.TPAHandler;
import group.aelysium.rustyconnector.plugin.velocity.lib.tpa.TPASettings;
import group.aelysium.rustyconnector.plugin.velocity.lib.whitelist.WhitelistService;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

/**
 * This class should never be used directly.
 * Player-focused families offer features such as /tpa, whitelists, load-balancing, and direct connection.
 */
public abstract class PlayerFocusedServerFamily extends BaseServerFamily<PlayerServer> {
    protected LoadBalancer loadBalancer = null;
    protected String whitelist;
    protected boolean weighted;
    protected TPAHandler tpaHandler;

    protected PlayerFocusedServerFamily(String name, Whitelist whitelist, Class<? extends LoadBalancer> clazz, boolean weighted, boolean persistence, int attempts, TPASettings tpaSettings) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        super(name);
        if(whitelist == null) this.whitelist = null;
        else this.whitelist = whitelist.getName();
        this.weighted = weighted;

        try {
            this.loadBalancer = clazz.getDeclaredConstructor().newInstance();
        } catch (Exception ignore) {}
        this.loadBalancer.setPersistence(persistence, attempts);
        this.loadBalancer.setWeighted(weighted);

        this.tpaHandler = new TPAHandler(tpaSettings);
    }

    /**
     * Connect a player to this family
     * @param player The player to connect
     * @return A PlayerServer on successful connection.
     * @throws RuntimeException If the connection cannot be made.
     */
    public abstract PlayerServer connect(Player player);

    public boolean isWeighted() {
        return weighted;
    }

    public LoadBalancer getLoadBalancer() {
        return this.loadBalancer;
    }

    public TPAHandler getTPAHandler() {
        return tpaHandler;
    }

    /**
     * Get the whitelist for this family, or `null` if there isn't one.
     * @return The whitelist or `null` if there isn't one.
     */
    public Whitelist getWhitelist() {
        VelocityAPI api = VelocityRustyConnector.getAPI();
        if(this.name == null) return null;
        return api.getService(WhitelistService.class).find(this.whitelist);
    }

    public long serverCount() { return this.loadBalancer.size(); }

    @Override
    public long getPlayerCount() {
        AtomicLong newPlayerCount = new AtomicLong();
        this.loadBalancer.dump().forEach(server -> newPlayerCount.addAndGet(server.getPlayerCount()));

        return newPlayerCount.get();
    }

    @Override
    public List<PlayerServer> getRegisteredServers() {
        return this.loadBalancer.dump();
    }

    @Override
    public void addServer(PlayerServer server) {
        this.loadBalancer.add(server);
    }

    @Override
    public void removeServer(PlayerServer server) {
        this.loadBalancer.remove(server);
    }

    @Override
    public PlayerServer getServer(@NotNull ServerInfo serverInfo) {
        return this.getRegisteredServers().stream()
                .filter(server -> Objects.equals(server.getServerInfo(), serverInfo)
                ).findFirst().orElse(null);
    }

    @Override
    public void unregisterServers() throws Exception {
        VelocityAPI api = VelocityRustyConnector.getAPI();
        for (PlayerServer server : this.loadBalancer.dump()) {
            if(server == null) continue;
            api.getService(ServerService.class).unregisterServer(server.getServerInfo(),this.name, false);
        }
    }

    @Override
    public List<Player> getAllPlayers(int max) {
        List<Player> players = new ArrayList<>();

        for (PlayerServer server : this.getRegisteredServers()) {
            if(players.size() > max) break;

            players.addAll(server.getRegisteredServer().getPlayersConnected());
        }

        return players;
    }
}
