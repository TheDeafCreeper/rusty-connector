package group.aelysium.rustyconnector.plugin.velocity.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.ServerInfo;
import group.aelysium.rustyconnector.plugin.velocity.PluginLogger;
import group.aelysium.rustyconnector.plugin.velocity.VelocityRustyConnector;
import group.aelysium.rustyconnector.plugin.velocity.central.VelocityAPI;
import group.aelysium.rustyconnector.plugin.velocity.lib.family.BaseServerFamily;
import group.aelysium.rustyconnector.plugin.velocity.lib.module.PlayerServer;
import group.aelysium.rustyconnector.plugin.velocity.lib.processor.VirtualProxyProcessor;

public class CommandHub {
    public static BrigadierCommand create() {
        VelocityAPI api = VelocityRustyConnector.getAPI();
        PluginLogger logger = api.getLogger();
        VirtualProxyProcessor virtualProcessor = api.getVirtualProcessor();

        LiteralCommandNode<CommandSource> hub = LiteralArgumentBuilder
                .<CommandSource>literal("hub")
                .requires(source -> source instanceof Player)
                .executes(context -> {
                    if(!(context.getSource() instanceof Player player)) {
                        logger.log("/hub must be sent as a player!");
                        return Command.SINGLE_SUCCESS;
                    }

                    ServerInfo sendersServerInfo = ((Player) context.getSource()).getCurrentServer().orElseThrow().getServerInfo();

                    PlayerServer sendersServer = virtualProcessor.findServer(sendersServerInfo);
                    String familyName = sendersServer.getFamilyName();

                    switch (familyName) {
                        case "woolsacre-game" -> {
                            BaseServerFamily parentFamily = virtualProcessor.getFamilyManager().find("woolsacre-lobby");
                            parentFamily.connect(player);
                        }
                        case "skysthelimit-game" -> {
                            BaseServerFamily parentFamily = virtualProcessor.getFamilyManager().find("skysthelimit-lobby");
                            parentFamily.connect(player);
                        }
                        case "ghostingrave-game" -> {
                            BaseServerFamily parentFamily = virtualProcessor.getFamilyManager().find("ghostingrave-lobby");
                            parentFamily.connect(player);
                        }
                        case "parkourtag-game" -> {
                            BaseServerFamily parentFamily = virtualProcessor.getFamilyManager().find("parkourtag-lobby");
                            parentFamily.connect(player);
                        }
                        default -> {
                            BaseServerFamily parentFamily = virtualProcessor.getRootFamily();
                            if (!familyName.equals(parentFamily.getName())) parentFamily.connect(player);
                        }
                    }

                    return Command.SINGLE_SUCCESS;
                })
                .build();

        return new BrigadierCommand(hub);
    }
}
