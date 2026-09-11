package ru.joutak.template;

import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import ru.joutak.template.coin.CoinService;
import ru.joutak.template.command.JoyCommand;
import ru.joutak.template.bank.BankService;
import ru.joutak.template.storage.AccountRepository;
import ru.joutak.template.storage.YamlAccountRepository;

public final class JoyBankPlugin extends JavaPlugin {
    @Getter
    private static JoyBankPlugin instance;
    private CoinService coinService;
    private BankService bankService;

    @Override
    public void onEnable() {
        instance = this;
        coinService = new CoinService(this);
        AccountRepository accountRepository =
                new YamlAccountRepository(this);
        bankService = new BankService(accountRepository);
        getCommand("joy")
                .setExecutor(
                        new JoyCommand(coinService, bankService)
                );
        getLogger().info(
                String.format("Плагин %s версии %s включен!", getPluginMeta().getName(), getPluginMeta().getVersion())
        );
    }

    @Override
    public void onDisable() {

    }
}
