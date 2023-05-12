package su.nightexpress.sunlight.module.worlds.commands.main;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.CommandFlag;
import su.nexmedia.engine.api.command.CommandResult;
import su.nightexpress.sunlight.config.Lang;
import su.nightexpress.sunlight.module.ModuleCommand;
import su.nightexpress.sunlight.module.worlds.WorldsModule;
import su.nightexpress.sunlight.module.worlds.config.WorldsLang;
import su.nightexpress.sunlight.module.worlds.config.WorldsPerms;
import su.nightexpress.sunlight.module.worlds.impl.WorldConfig;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DeleteSubCommand extends ModuleCommand<WorldsModule> {

    public static final String NAME = "delete";

    private static final CommandFlag<Boolean> FLAG_FOLDER = CommandFlag.booleanFlag("f");

    public DeleteSubCommand(@NotNull WorldsModule worldsModule) {
        super(worldsModule, new String[]{NAME}, WorldsPerms.COMMAND_WORLDS_DELETE);
        this.setDescription(this.plugin.getMessage(WorldsLang.COMMAND_WORLDS_DELETE_DESC));
        this.setUsage(this.plugin.getMessage(WorldsLang.COMMAND_WORLDS_DELETE_USAGE));
        this.addFlag(FLAG_FOLDER);
    }

    @Override
    @NotNull
    public List<String> getTab(@NotNull Player player, int arg, @NotNull String[] args) {
        if (arg == 1) {
            return new ArrayList<>(this.module.getConfigsMap().keySet());
        }
        if (arg == 2) {
            return Arrays.asList(String.valueOf(true), String.valueOf(false));
        }
        return super.getTab(player, arg, args);
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {
        if (result.length() < 2) {
            this.printUsage(sender);
            return;
        }

        WorldConfig worldConfig = this.module.getWorldById(result.getArg(1));
        if (worldConfig == null) {
            plugin.getMessage(Lang.ERROR_WORLD_INVALID).send(sender);
            return;
        }

        boolean withFolder = result.hasFlag(FLAG_FOLDER);
        if (!worldConfig.deleteWorld(withFolder)) {
            this.plugin.getMessage(WorldsLang.COMMAND_WORLDS_DELETE_ERROR).replace(worldConfig.replacePlaceholders()).send(sender);
            return;
        }

        this.plugin.getMessage(WorldsLang.COMMAND_WORLDS_DELETE_DONE).replace(worldConfig.replacePlaceholders()).send(sender);
    }
}
