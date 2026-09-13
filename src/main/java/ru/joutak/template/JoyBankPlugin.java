package ru.joutak.template;

import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import ru.joutak.template.atm.AtmService;
import ru.joutak.template.atm.listener.AtmMenuListener;
import ru.joutak.template.coin.CoinService;
import ru.joutak.template.command.JoyCommand;
import ru.joutak.template.bank.BankService;
import ru.joutak.template.storage.AccountRepository;
import ru.joutak.template.storage.YamlAccountRepository;
import ru.joutak.template.storage.AtmRepository;
import ru.joutak.template.storage.YamlAtmRepository;
import ru.joutak.template.atm.listener.AtmCreationListener;
import ru.joutak.template.gui.AtmMenu;
import ru.joutak.template.atm.listener.AtmProtectionListener;

import ru.joutak.template.atm.listener.AtmInteractionListener;

public final class JoyBankPlugin extends JavaPlugin {
    @Getter
    private static JoyBankPlugin instance;
    private CoinService coinService;
    private BankService bankService;
    private AtmService atmService;
    private AtmMenu atmMenu;

    @Override
    public void onEnable() {
        instance = this;
        coinService = new CoinService(this);
        AccountRepository accountRepository =
                new YamlAccountRepository(this);
        bankService = new BankService(accountRepository);

        AtmRepository atmRepository =
                new YamlAtmRepository(this);

        atmService =
                new AtmService(atmRepository);

        atmMenu = new AtmMenu(bankService);

        getCommand("joy")
                .setExecutor(
                        new JoyCommand(coinService, bankService)
                );
        getLogger().info(
                String.format("Плагин %s версии %s включен!", getPluginMeta().getName(), getPluginMeta().getVersion())
        );

        getServer()
                .getPluginManager()
                .registerEvents(
                        new AtmCreationListener(atmService),
                        this
                );
        getServer()
                .getPluginManager()
                .registerEvents(
                        new AtmInteractionListener(
                                atmService,
                                atmMenu
                        ),
                        this
                );
        getServer()
                .getPluginManager()
                .registerEvents(
                        new AtmMenuListener(
                                coinService,
                                bankService,
                                atmService,
                                atmMenu
                        ),
                        this
                );
        getServer()
                .getPluginManager()
                .registerEvents(
                        new AtmProtectionListener(atmService),
                        this
                );
    }

    @Override
    public void onDisable() {

    }
}
