package su.nightexpress.sunlight.command.enderchest;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.CommandResult;
import su.nightexpress.sunlight.Perms;
import su.nightexpress.sunlight.Placeholders;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.command.CommandFlags;
import su.nightexpress.sunlight.command.api.TargetCommand;
import su.nightexpress.sunlight.config.Lang;

public class EnderchestClearCommand extends TargetCommand {

    public static final String NAME = "clear";

    public EnderchestClearCommand(@NotNull SunLight plugin) {
        super(plugin, new String[]{NAME}, Perms.COMMAND_ENDERCHEST_CLEAR, Perms.COMMAND_ENDERCHEST_CLEAR_OTHERS, 1);
        this.setAllowDataLoad();
        this.setDescription(plugin.getMessage(Lang.COMMAND_ENDERCHEST_CLEAR_DESC));
        this.setUsage(plugin.getMessage(Lang.COMMAND_ENDERCHEST_CLEAR_USAGE));
        this.addFlag(CommandFlags.SILENT);
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {
        Player target = this.getCommandTarget(sender, result);
        if (target == null) return;

        plugin.getSunNMS().getPlayerEnderChest(target).clear();
        if (!target.isOnline()) target.saveData();

        if (sender != target) {
            plugin.getMessage(Lang.COMMAND_ENDERCHEST_CLEAR_DONE_TARGET)
                .replace(Placeholders.Player.replacer(target))
                .send(sender);
        }
        if (!result.hasFlag(CommandFlags.SILENT)) {
            plugin.getMessage(Lang.COMMAND_ENDERCHEST_CLEAR_DONE_NOTIFY)
                .replace(Placeholders.Player.replacer(sender))
                .send(target);
        }
    }
}
