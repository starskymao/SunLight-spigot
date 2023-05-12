package su.nightexpress.sunlight.module.kits.command.kits;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.utils.CollectionsUtil;
import su.nightexpress.sunlight.module.ModuleCommand;
import su.nightexpress.sunlight.module.kits.Kit;
import su.nightexpress.sunlight.module.kits.KitsModule;
import su.nightexpress.sunlight.module.kits.config.KitsLang;
import su.nightexpress.sunlight.module.kits.util.KitsPerms;
import su.nightexpress.sunlight.module.kits.util.Placeholders;

import java.util.List;
import java.util.Map;

public class KitsGiveCommand extends ModuleCommand<KitsModule> {

    public KitsGiveCommand(@NotNull KitsModule module) {
        super(module, new String[]{"give"}, KitsPerms.COMMAND_KITS_GIVE);
    }

    @Override
    @NotNull
    public String getUsage() {
        return plugin.getMessage(KitsLang.COMMAND_KITS_GIVE_USAGE).getLocalized();
    }

    @Override
    @NotNull
    public String getDescription() {
        return plugin.getMessage(KitsLang.COMMAND_KITS_GIVE_DESC).getLocalized();
    }

    @Override
    public boolean isPlayerOnly() {
        return false;
    }

    @Override
    @NotNull
    public List<String> getTab(@NotNull Player player, int arg, @NotNull String[] args) {
        if (arg == 1) {
            return this.module.getKitIds();
        }
        if (arg == 2) {
            return CollectionsUtil.playerNames(player);
        }
        return super.getTab(player, arg, args);
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args, @NotNull Map<String, String> flags) {
        if (args.length < 3) {
            this.printUsage(sender);
            return;
        }

        Kit kit = this.module.getKitById(args[1]);
        if (kit == null) {
            plugin.getMessage(KitsLang.KIT_ERROR_INVALID_KIT).send(sender);
            return;
        }

        Player player = plugin.getServer().getPlayer(args[2]);
        if (player == null) {
            this.errorPlayer(sender);
            return;
        }

        kit.give(player, true);
        if (!sender.equals(player)) {
            plugin.getMessage(KitsLang.COMMAND_KITS_GIVE_DONE)
                .replace(kit.replacePlaceholders())
                .replace(Placeholders.Player.replacer(player))
                .send(sender);
        }

        plugin.getMessage(KitsLang.COMMAND_KITS_GIVE_NOTIFY)
            .replace(kit.replacePlaceholders())
            .replace(Placeholders.Player.replacer(sender))
            .send(player);
    }
}
