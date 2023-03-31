package group.aelysium.rustyconnector.plugin.velocity.lib.events;

import com.velocitypowered.api.event.EventTask;
import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.PlayerChooseInitialServerEvent;
import com.velocitypowered.api.proxy.Player;
import group.aelysium.rustyconnector.core.central.PluginLogger;
import group.aelysium.rustyconnector.plugin.velocity.VelocityRustyConnector;
import group.aelysium.rustyconnector.plugin.velocity.central.VelocityAPI;
import group.aelysium.rustyconnector.plugin.velocity.lib.load_balancing.PaperServerLoadBalancer;
import group.aelysium.rustyconnector.plugin.velocity.lib.module.ServerFamily;
import group.aelysium.rustyconnector.plugin.velocity.lib.module.Whitelist;
import net.kyori.adventure.text.Component;

public class OnPlayerChooseInitialServer {
    /**
     * Runs when a player first joins the proxy
     */
    @Subscribe(order = PostOrder.LAST)
    public EventTask onPlayerChooseInitialServer(PlayerChooseInitialServerEvent event) {
        VelocityAPI api = VelocityRustyConnector.getAPI();
        PluginLogger logger = api.getLogger();
        Player player = event.getPlayer();

        return EventTask.async(() -> {
            try {
                // Check if there's a whitelist, run it if there is.
                Whitelist whitelist = api.getVirtualProcessor().getProxyWhitelist();
                if(whitelist != null) {
                    if (!whitelist.validate(player)) {
                        logger.log("Player isn't whitelisted on the proxy whitelist! Kicking...");
                        player.disconnect(Component.text(whitelist.getMessage()));
                        return;
                    }
                }

                ServerFamily<? extends PaperServerLoadBalancer> rootFamily = api.getVirtualProcessor().getRootFamily();

                rootFamily.connect(player, event);
            } catch (Exception e) {
                player.disconnect(Component.text("Disconnected. "+e.getMessage()));
                e.printStackTrace();
            }
        });
    }
}
