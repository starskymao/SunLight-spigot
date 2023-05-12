package su.nightexpress.sunlight.command.teleport;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.CommandResult;
import su.nightexpress.sunlight.Perms;
import su.nightexpress.sunlight.Placeholders;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.command.CommandFlags;
import su.nightexpress.sunlight.command.api.ToggleCommand;
import su.nightexpress.sunlight.command.teleport.impl.TeleportRequest;
import su.nightexpress.sunlight.config.Lang;
import su.nightexpress.sunlight.data.impl.SunUser;

public class TeleportToggleCommand extends ToggleCommand {

    public static final String NAME = "toggle";

    public TeleportToggleCommand(@NotNull SunLight plugin) {
        super(plugin, new String[]{NAME}, Perms.COMMAND_TELEPORT_TOGGLE, Perms.COMMAND_TELEPORT_TOGGLE_OTHERS, 1);
        this.setAllowDataLoad();
        this.setDescription(plugin.getMessage(Lang.COMMAND_TELEPORT_TOGGLE_DESC));
        this.setUsage(plugin.getMessage(Lang.COMMAND_TELEPORT_TOGGLE_USAGE));
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {
        Player target = this.getCommandTarget(sender, result);
        if (target == null) return;

        Mode mode = this.getMode(sender, result);
        SunUser user = plugin.getUserManager().getUserData(target);
        boolean state = mode.apply(user.getSettings().get(TeleportRequest.SETTING_REQUESTS));
        user.getSettings().set(TeleportRequest.SETTING_REQUESTS, state);
        user.saveData(this.plugin);

        if (sender != target) {
            plugin.getMessage(Lang.COMMAND_TELEPORT_TOGGLE_TARGET)
                .replace(Placeholders.Player.replacer(target))
                .replace(Placeholders.GENERIC_STATE, Lang.getEnable(state))
                .send(sender);
        }
        if (!result.hasFlag(CommandFlags.SILENT)) {
            plugin.getMessage(Lang.COMMAND_TELEPORT_TOGGLE_NOTIFY)
                .replace(Placeholders.Player.replacer(sender))
                .replace(Placeholders.GENERIC_STATE, Lang.getEnable(state))
                .send(target);
        }
    }
}
