package ru.joutak.template;

import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import ru.joutak.template.command.JoyCommand;

public final class JoyBankPlugin extends JavaPlugin {
    @Getter
    private static JoyBankPlugin instance;

    @Override
    public void onEnable() {
        instance = this;
        getCommand("joy").setExecutor(new JoyCommand());
        getLogger().info(
                String.format("Плагин %s версии %s включен!", getPluginMeta().getName(), getPluginMeta().getVersion())
        );
    }

    @Override
    public void onDisable() {

    }
}
