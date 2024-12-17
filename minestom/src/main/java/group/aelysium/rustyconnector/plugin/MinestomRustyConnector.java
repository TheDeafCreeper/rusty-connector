package group.aelysium.rustyconnector.plugin;

import group.aelysium.declarative_yaml.DeclarativeYAML;
import group.aelysium.rustyconnector.RC;
import group.aelysium.rustyconnector.RustyConnector;
import group.aelysium.rustyconnector.common.errors.Error;
import group.aelysium.rustyconnector.common.lang.LangLibrary;
import group.aelysium.rustyconnector.plugin.common.config.GitOpsConfig;
import group.aelysium.rustyconnector.plugin.common.config.PrivateKeyConfig;
import group.aelysium.rustyconnector.plugin.serverCommon.DefaultConfig;
import group.aelysium.rustyconnector.plugin.serverCommon.ServerLang;
import group.aelysium.rustyconnector.server.ServerKernel;
import group.aelysium.rustyconnector.server.magic_link.WebSocketMagicLink;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.command.ConsoleSender;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.InstanceManager;

public final class MinestomRustyConnector {
    Instance server;

    public MinestomRustyConnector(Instance server) {
        this.server = server;

        ConsoleSender console = new ConsoleSender();
        console.sendMessage("Initializing RustyConnector...");

        try {
            //metricsFactory.make(this, 17972);
            console.sendMessage("Registered to bstats!");
        } catch (Exception e) {
            e.printStackTrace();
            console.sendMessage("Failed to register to bstats!");
        }

        try {
            if(PrivateKeyConfig.Load().isEmpty()) {
                console.sendMessage(Component.join(
                        JoinConfiguration.newlines(),
                        Component.empty(),
                        Component.empty(),
                        Component.empty(),
                        Component.empty(),
                        Component.text("Looks like I'm still waiting on a private.key from the proxy!", NamedTextColor.BLUE),
                        Component.text("You'll need to copy ", NamedTextColor.BLUE).append(Component.text("plugins/rustyconnector/metadata/aes.private", NamedTextColor.YELLOW)).append(Component.text(" and paste it into this server in that same folder!", NamedTextColor.BLUE)),
                        Component.text("Both the proxy and I need to have the same aes.private!", NamedTextColor.BLUE),
                        Component.empty(),
                        Component.empty(),
                        Component.empty()
                ));
                return;
            }

            {
                GitOpsConfig config = GitOpsConfig.New();
                if(config != null) DeclarativeYAML.registerRepository("rustyconnector", config.config());
            }

            ServerKernel.Tinder tinder = DefaultConfig.New().data(
                    new MinestomServerAdapter(this.server)
            );
            RustyConnector.registerAndIgnite(tinder.flux());
            RustyConnector.Kernel(flux->{
                flux.onStart(kernel -> {
                    try {
                        kernel.fetchPlugin("LangLibrary").onStart(l -> ((LangLibrary) l).registerLangNodes(ServerLang.class));
                    } catch (Exception e) {
                        RC.Error(Error.from(e));
                    }
                    try {
                        kernel.fetchPlugin("MagicLink").onStart(l -> ((WebSocketMagicLink) l).connect());
                    } catch (Exception e) {
                        RC.Error(Error.from(e));
                    }
                });
            });

//            LegacyPaperCommandManager<PaperClient> commandManager = new LegacyPaperCommandManager<>(
//                    this,
//                    ExecutionCoordinator.asyncCoordinator(),
//                    SenderMapper.create(
//                            sender -> new PaperClient(sender),
//                            client -> client.toSender()
//                    )
//            );
//            commandManager.registerCommandPreProcessor(new ValidateClient<>());
//
//            AnnotationParser<PaperClient> annotationParser = new AnnotationParser<>(commandManager, PaperClient.class);
//            annotationParser.parse(new CommonCommands());
//            annotationParser.parse(new CommandRusty());
            RC.Lang("rustyconnector-wordmark").send(RC.Kernel().version());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public void onDisable() {
        try {
            RustyConnector.unregister();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}