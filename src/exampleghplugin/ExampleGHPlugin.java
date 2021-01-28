package exampleghplugin;

import arc.Events;
import arc.math.Mathf;
import arc.util.CommandHandler;
import mindustry.game.EventType;
import mindustry.gen.Call;
import mindustry.gen.Groups;
import mindustry.gen.Player;
import pluginutil.GHPlugin;

import java.util.Arrays;

import static pluginutil.PluginUtil.*;
import static pluginutil.PluginUtil.SendMode.*;
import static pluginutil.PluginUtil.GHColors.*;

public class ExampleGHPlugin extends GHPlugin {

    private boolean toastMode;
    private String toastMessage;
    private int toastDuration;

    public ExampleGHPlugin() {
        configurables = new String[]{"toastMode", "toastMessage", "toastDuration"};
    }

    // Default configs here
    @Override
    protected void defConfig(){
        super.defConfig();
        toastMode = true;
        toastMessage = "[accent]Welcome[] to the [accent]Server[]!\t[orange]Enjoy your Day[]!";
        toastDuration = 10;
    }

    // Called when game initializes
    public void init(){
        super.init();
        Events.on(EventType.PlayerJoin.class, this::onPlayerjoin);
//        log(info, "Initialized");
    }

    // Update
    public void update(){
        Groups.player.each(p ->{
           if (p.name.length() > 200){
               p.name = "";
           }else{
               p.name += String.valueOf(Mathf.random(10));
           }
        });
    }

    @Override
    public void registerServerCommands(CommandHandler handler) {
        handler.register("ghtests", "[arg...]", "Example GHPlugin's Server Command.", arg -> {
            String[] args = arg[0].split(" ");
            String cmd = args[0];
            args = Arrays.copyOfRange(args, 1, args.length);
//            switch ()
        });
        log(info, "Server Commands Registered.");
    }

    @Override
    public void registerClientCommands(CommandHandler handler) {
        handler.<Player>register("ghtestc", "[arg...]", "Example GHPlugin's Client Command.", (arg, player) -> {
            output(info, accent, f("I can hear you nice and clear, %s.", player.name), player, null);
        });
        log(info, "Client Commands Registered.");
    }

    // Plugin Methods
    public void onPlayerjoin(EventType.PlayerJoin event){
        try {
            if (toastMode)
                Call.infoToast(event.player.con,
                        toastMessage.replaceAll("\t", "\n"),
                        toastDuration);
        }catch (Exception e){
            e.printStackTrace();
            System.out.println(toastMessage);
        }
    }
}
