### Setup

Put this plugin in your mods folder.


### Usage

1. Assigning admin command(s) at Runtime
```java
// Find the mod
LoadedMod mod = (LoadedMod)Vars.mods.list().find((m) -> {
    return m.main != null && m.main.getClass().getSimpleName().equals("EnhancedHelpCommand");
});

if (mod != null) {
    try {

        // Get method
        Method add = mod.main.getClass().getDeclaredMethod("add", String.class);
        add.invoke(mod.main, "adminOnlyCommand");
        Log.info("Admin only command registered.");
    } catch (Exception e) {
        Log.warn("An error has occurred while registering admin only command(s).");
        e.printStackTrace();
    }
}
```

2. Assigning admin command(s) before running the server
Add the commands into the JSON config file which has the same name as the plugin.
E.g. `{"adminCommands":["team"]}`
