package su.nightexpress.sunlight.module.kits.menu;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.config.JYML;
import su.nexmedia.engine.api.menu.AbstractMenuAuto;
import su.nexmedia.engine.api.menu.MenuClick;
import su.nexmedia.engine.api.menu.MenuItem;
import su.nexmedia.engine.api.menu.MenuItemType;
import su.nexmedia.engine.utils.Colorizer;
import su.nexmedia.engine.utils.ItemUtil;
import su.nexmedia.engine.utils.StringUtil;
import su.nexmedia.engine.utils.TimeUtil;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.config.Lang;
import su.nightexpress.sunlight.data.impl.SunUser;
import su.nightexpress.sunlight.module.kits.Kit;
import su.nightexpress.sunlight.module.kits.KitsModule;
import su.nightexpress.sunlight.module.kits.util.Placeholders;
import su.nightexpress.sunlight.module.kits.config.KitsLang;
import su.nightexpress.sunlight.data.impl.cooldown.CooldownInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class KitsMenu extends AbstractMenuAuto<SunLight, Kit> {

    private static final String PLACEHOLDER_NO_MONEY = "%no_money%";
    private static final String PLACEHOLDER_NO_PERMISSION = "%no_permission%";
    private static final String PLACEHOLDER_COOLDOWN = "%cooldown%";

    private final KitsModule kitsModule;

    private final String       kitName;
    private final List<String> kitLoreAll;
    private final List<String> kitLoreNoPerm;
    private final List<String> kitLoreNoMoney;
    private final List<String> kitLoreCooldown;
    private final int[]        kitSlots;

    public KitsMenu(@NotNull KitsModule kitsModule) {
        super(kitsModule.plugin(), JYML.loadOrExtract(kitsModule.plugin(), kitsModule.getLocalPath() + "/menu/kit_list.yml"), "");
        this.kitsModule = kitsModule;

        this.kitName = Colorizer.apply(cfg.getString("Kit.Name", Placeholders.KIT_NAME));
        this.kitLoreAll = Colorizer.apply(cfg.getStringList("Kit.Lore.Default"));
        this.kitLoreNoPerm = Colorizer.apply(cfg.getStringList("Kit.Lore.No_Permission"));
        this.kitLoreNoMoney = Colorizer.apply(cfg.getStringList("Kit.Lore.No_Money"));
        this.kitLoreCooldown = Colorizer.apply(cfg.getStringList("Kit.Lore.Cooldown"));
        this.kitSlots = cfg.getIntArray("Kit.Slots");

        MenuClick click = (player, type, e) -> {
            if (type instanceof MenuItemType type2) {
                this.onItemClickDefault(player, type2);
            }
        };

        for (String sId : cfg.getSection("Content")) {
            MenuItem menuItem = cfg.getMenuItem("Content." + sId, MenuItemType.class);

            if (menuItem.getType() != null) {
                menuItem.setClickHandler(click);
            }
            this.addItem(menuItem);
        }
    }

    @Override
    public boolean open(@NotNull Player player, int page) {
        if (this.getObjects(player).isEmpty()) {
            this.plugin.getMessage(KitsLang.KIT_ERROR_NO_KITS).send(player);
            return false;
        }
        return super.open(player, page);
    }

    @Override
    public int[] getObjectSlots() {
        return kitSlots;
    }

    @Override
    @NotNull
    protected List<Kit> getObjects(@NotNull Player player) {
        return this.kitsModule.getKits().stream().sorted(Comparator.comparingInt(Kit::getPriority)).toList();
    }

    @Override
    @NotNull
    protected ItemStack getObjectStack(@NotNull Player player, @NotNull Kit kit) {
        ItemStack item = kit.getIcon();
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;

        SunUser user = plugin.getUserManager().getUserData(player);
        long cooldown = user.getCooldown(kit).map(CooldownInfo::getExpireDate).orElse(0L);

        List<String> lore = new ArrayList<>(this.kitLoreAll);
        lore = StringUtil.replace(lore, PLACEHOLDER_NO_MONEY, false, !kit.canAfford(player) ? this.kitLoreNoMoney : Collections.emptyList());
        lore = StringUtil.replace(lore, PLACEHOLDER_NO_PERMISSION, false, !kit.hasPermission(player) ? this.kitLoreNoPerm : Collections.emptyList());
        lore = StringUtil.replace(lore, PLACEHOLDER_COOLDOWN, false, kit.isOnCooldown(player) ? this.kitLoreCooldown : Collections.emptyList());
        lore.replaceAll(line -> line
            .replace(Placeholders.GENERIC_COOLDOWN, cooldown < 0 ? plugin.getMessage(Lang.OTHER_NEVER).getLocalized() : TimeUtil.formatTimeLeft(cooldown))
        );

        meta.setDisplayName(this.kitName);
        meta.setLore(lore);
        item.setItemMeta(meta);

        ItemUtil.replace(item, kit.replacePlaceholders());
        return item;
    }

    @Override
    @NotNull
    protected MenuClick getObjectClick(@NotNull Player player, @NotNull Kit kit) {
        return (player1, type, e) -> {
            if (e.isLeftClick()) {
                kit.give(player1, false);
                player1.closeInventory();
            }
            else if (e.isRightClick()) {
                kit.getPreview().open(player1, 1);
            }
        };
    }

    @Override
    public boolean cancelClick(@NotNull InventoryClickEvent e, @NotNull SlotType slotType) {
        return true;
    }
}
