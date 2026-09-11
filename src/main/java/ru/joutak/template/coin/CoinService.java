package ru.joutak.template.coin;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

public class CoinService {

    private final NamespacedKey coinKey;

    public CoinService(JavaPlugin plugin) {
        this.coinKey = new NamespacedKey(plugin, "joy_coin");
    }

    public ItemStack createCoin(int amount) {
        ItemStack coin = new ItemStack(Material.GOLD_NUGGET, amount);

        ItemMeta meta = coin.getItemMeta();

        meta.displayName(
                Component.text("Джой", NamedTextColor.GOLD)
        );

        meta.getPersistentDataContainer().set(
                coinKey,
                PersistentDataType.BYTE,
                (byte) 1
        );

        coin.setItemMeta(meta);

        return coin;
    }

    public boolean isCoin(ItemStack item) {
        if (item == null) {
            return false;
        }

        if (item.getType() != Material.GOLD_NUGGET) {
            return false;
        }

        ItemMeta meta = item.getItemMeta();

        return meta.getPersistentDataContainer().has(
                coinKey,
                PersistentDataType.BYTE
        );
    }
}