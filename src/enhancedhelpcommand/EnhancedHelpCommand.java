package enhancedhelpcommand;

import arc.Events;
import arc.math.Mathf;
import arc.struct.Seq;
import arc.util.CommandHandler;
import arc.util.Strings;
import mindustry.game.EventType;
import mindustry.gen.Player;
import pluginutil.GHPlugin;

import java.util.*;

import static pluginutil.PluginUtil.f;

@SuppressWarnings("unused")
public class EnhancedHelpCommand extends GHPlugin {

    private HashSet<String> adminCommandsSet;

    public EnhancedHelpCommand() {
        super();
        VERSION = "1.11";
    }

    public void init(){
        super.init();
        if(cfg().adminCommands.length > 0) {
            adminCommandsSet.addAll(Arrays.asList(cfg().adminCommands));
            debug(f("Admin Only Command: %s Loaded from config.", adminCommandsSet.toString()));
        }

        Events.on(EventType.ServerLoadEvent.class, e -> {
            Events.fire(new EnhancedHelpCommand());
            debug(f("Help Command Overwritten. Amount of admin commands: %s", adminCommandsSet.size()));
        });

        debug("Initialized\n");
    }

    @Override
    public void registerClientCommands(CommandHandler handler) {
        handler.removeCommand("help");

        // Magic, NetServer:270
        handler.<Player>register("help", "[page]", "Lists all commands.", (args, player) -> {
            Seq<CommandHandler.Command> commands = handler.getCommandList().copy();
            ArrayList<CommandHandler.Command> adminOnlyCommands = commands.copy().removeAll(cmd -> !adminCommandsSet.contains(cmd.text)).list();
            ArrayList<CommandHandler.Command> playerCommands = commands.copy().removeAll(cmd -> adminCommandsSet.contains(cmd.text)).list();
            commands.clear();

            // Sort in Alphabetical order
            adminOnlyCommands.sort(Comparator.comparing(a -> a.text));
            playerCommands.sort(Comparator.comparing(a -> a.text));

            if (player.admin)
                commands.addAll(adminOnlyCommands);
            commands.addAll(playerCommands);

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
                result.append(adminOnlyCommands.contains(command) ? "[scarlet]" : "[orange]").append(" /").append(command.text).append("[white] ").append(command.paramText).append("[lightgray] - ").append(command.description).append("\n");
            }
            player.sendMessage(result.toString());
        });
        // Magic
    }

    public void add(String cmd){
        adminCommandsSet.add(cmd);
        debug(f("Admin Only Command: %sregistered.", cmd));
    }

    public void add(String[] cmd){
        adminCommandsSet.addAll(Arrays.asList(cmd));
        debug(f("Admin Only Commands: %s registered.", Arrays.toString(cmd)));
    }


    @Override
    protected void defConfig() {
        adminCommandsSet = new HashSet<>();
        cfg = new EnhancedHelpCommandConfig();
    }

    @SuppressWarnings("unchecked")
    protected EnhancedHelpCommandConfig cfg(){
        return (EnhancedHelpCommandConfig) cfg;
    }

    public static class EnhancedHelpCommandConfig extends GHPluginConfig {
        @SuppressWarnings("MismatchedReadAndWriteOfArray")
        private String[] adminCommands;

        public void reset(){
            adminCommands = new String[0];
        }

        @Override
        protected boolean softReset() {
            return false;
        }
    }
}
