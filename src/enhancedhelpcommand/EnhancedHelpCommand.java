package enhancedhelpcommand;

import arc.Events;
import arc.math.Mathf;
import arc.struct.Seq;
import arc.util.CommandHandler;
import arc.util.Strings;
import mindustry.Vars;
import mindustry.gen.Player;
import pluginutil.GHPlugin;

public class EnhancedHelpCommand extends GHPlugin {

    private CommandHandler clientCommands;

    private final Seq<String> adminCommands;

    public EnhancedHelpCommand() {
        configurables = new String[]{};
        adminCommands = new Seq<>();
    }

    @Override
    public void registerClientCommands(CommandHandler handler) {
        clientCommands = handler;
        fetch();
    }

    private void fetch() {
        Events.fire(getClass());

        // Magic, NetServer:270
        clientCommands.<Player>register("help", "[page]", "Lists all commands.", (args, player) -> {
            Seq<CommandHandler.Command> commands = clientCommands.getCommandList();

            if(!player.admin)
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
    }

    public void add(String adminCommand){
        EnhancedHelpCommand ehc = (EnhancedHelpCommand) Vars.mods.getMod(EnhancedHelpCommand.class).main;
        if (ehc != null)
        ehc.adminCommands.add(adminCommand);
    }
}
