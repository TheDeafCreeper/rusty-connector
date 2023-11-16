package group.aelysium.rustyconnector.toolkit.velocity.players;

import com.velocitypowered.api.proxy.Player;
import group.aelysium.rustyconnector.toolkit.core.serviceable.interfaces.Service;

import java.util.Optional;
import java.util.UUID;

public interface IPlayerService extends Service {
    /**
     * Fetch a {@link IRustyPlayer} using a {@link UUID}.
     * <p>
     * Velocity, unlike Paper, doesn't have any memory of players that join the network.
     * This method hooks into RustyConnector's remote storage connector to keep track of player's that have joined the network.
     * @param uuid The {@link UUID} to search with.
     * @return A {@link IRustyPlayer}. The resolvable player can be resolved into a {@link Player} only if that player is currently online.
     */
    Optional<? extends IRustyPlayer> fetch(UUID uuid);

    /**
     * Fetch a {@link IRustyPlayer} using a player's username.
     * <p>
     * Velocity, unlike Paper, doesn't have any memory of players that join the network.
     * This method hooks into RustyConnector's remote storage connector to keep track of player's that have joined the network.
     * @param username The username to search with.
     * @return A {@link IRustyPlayer}. The resolvable player can be resolved into a {@link Player} only if that player is currently online.
     */
    Optional<? extends IRustyPlayer> fetch(String username);

    /**
     * Save a player into RustyConnector's remote storage connector.
     * Player's that are saved via this method will be able to be resolved using {@link IPlayerService#fetch}.
     * @param player The {@link Player} to store.
     */
    void savePlayer(Player player);
}
