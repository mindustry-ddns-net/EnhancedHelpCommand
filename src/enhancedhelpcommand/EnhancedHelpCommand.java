package enhancedhelpcommand;

import arc.math.Mathf;
import arc.struct.Seq;
import arc.util.CommandHandler;
import arc.util.Strings;
import mindustry.gen.Player;
import pluginutil.GHPlugin;

import java.util.HashSet;

import static pluginutil.PluginUtil.SendMode.info;
import static pluginutil.PluginUtil.f;

public class EnhancedHelpCommand extends GHPlugin {

    private final HashSet<String> adminCommands;

    public EnhancedHelpCommand() {
        configurables = new String[]{};
        adminCommands = new HashSet<>();
    }

    @Override
    public void registerClientCommands(CommandHandler handler) {
        // Magic, NetServer:270
        handler.<Player>register("help", "[page]", "Lists all commands.", (args, player) -> {
            Seq<CommandHandler.Command> commands = handler.getCommandList();

            if (!player.admin)
                commands.removeAll(cmd -> adminCommands.contains(cmd.text));

            if (args.length > 0 && !Strings.canParseInt(args[0])) {
                player.sendMessage("[scarlet]'page' must be a number.");
                return;
            }
            int commandsPerPage = 6;
            int page = args.length > 0 ? Strings.parseInt(args[0]) : 1;
            int pages = Mathf.ceil((float) commands.size / commandsPerPage);

            page--;

            if (page >= pages || page < 0) {
                player.sendMessage("[scarlet]'page' must be a number between[orange] 1[] and[orange] " + pages + "[scarlet].");
                return;
            }

            StringBuilder result = new StringBuilder();
            result.append(Strings.format("[orange]-- Commands Page[lightgray] @[gray]/[lightgray]@[orange] --\n\n", (page + 1), pages));

            for (int i = commandsPerPage * page; i < Math.min(commandsPerPage * (page + 1), commands.size); i++) {
                CommandHandler.Command command = commands.get(i);
                result.append("[orange] /").append(command.text).append("[white] ").append(command.paramText).append("[lightgray] - ").append(command.description).append("\n");
            }
            player.sendMessage(result.toString());
        });
        // Magic

        log(info, f("Help Command Overwritten. Amount of admin commands: %s", adminCommands.size()));
    }
}
