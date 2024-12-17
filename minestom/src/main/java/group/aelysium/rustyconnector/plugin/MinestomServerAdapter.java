package group.aelysium.rustyconnector.plugin;

import group.aelysium.rustyconnector.RC;
import group.aelysium.rustyconnector.common.errors.Error;
import group.aelysium.rustyconnector.server.ServerAdapter;
import net.kyori.adventure.text.Component;
import net.minestom.server.command.ConsoleSender;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.Instance;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;

public class MinestomServerAdapter extends ServerAdapter {
    private final Instance server;

    public MinestomServerAdapter(@NotNull Instance server) {
        this.server = server;
    }

    @Override
    public void setMaxPlayers(int max) {}

    @Override
    public int onlinePlayerCount() { return this.server.getPlayers().size(); }

    @Override
    public Optional<UUID> playerUUID(@NotNull String username) {
        Optional<Player> player = this.server.getPlayers().stream().filter(check -> check.getUsername().equals(username)).findAny();
        return player.map(Entity::getUuid);
    }

    @Override
    public Optional<String> playerUsername(@NotNull UUID uuid) {
        Player player = this.server.getPlayerByUuid(uuid);
        if(player == null) return Optional.empty();
        return Optional.of(player.getUsername());
    }

    @Override
    public boolean isOnline(@NotNull UUID uuid) {
        Player player = this.server.getPlayerByUuid(uuid);
        if(player == null) return false;
        return player.isOnline();
    }

    @Override
    public void messagePlayer(@NotNull UUID uuid, @NotNull Component message) {
        try {
            Player player = this.server.getPlayerByUuid(uuid);
            if(player == null) throw new NullPointerException("No player with the uuid "+uuid+" is online.");
            player.sendMessage(message);
        } catch (Exception e) {
            RC.Error(Error.from(e));
        }
    }

    @Override
    public void log(@NotNull Component message) {
        (new ConsoleSender()).sendMessage(message);
    }
}
