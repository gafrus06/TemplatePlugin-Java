package ru.joutak.template.atm.listener;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import ru.joutak.template.atm.AtmService;

public class AtmCreationListener implements Listener {

    private final AtmService atmService;

    public AtmCreationListener(AtmService atmService) {
        this.atmService = atmService;
    }

    @EventHandler
    public void onSignChange(SignChangeEvent event) {

        String firstLine = event.getLine(0);

        if (firstLine == null
                || !firstLine.equalsIgnoreCase("[joybank]")) {
            return;
        }

        if (!event.getPlayer()
                .hasPermission("joybank.atm.create")) {

            event.getPlayer().sendMessage(
                    "У вас нет прав на создание банкомата."
            );

            return;
        }

        Block signBlock = event.getBlock();

        BlockData blockData = signBlock.getBlockData();

        if (!(blockData instanceof WallSign wallSign)) {
            event.getPlayer().sendMessage(
                    "Табличка должна быть прикреплена к боку бочки."
            );
            return;
        }

        Block barrel = signBlock.getRelative(
                wallSign.getFacing().getOppositeFace()
        );

        if (barrel.getType() != Material.BARREL) {
            event.getPlayer().sendMessage(
                    "Табличка должна быть прикреплена к бочке."
            );
            return;
        }

        Block base = barrel.getRelative(0, -1, 0);

        if (base.getType() != Material.IRON_BLOCK) {
            event.getPlayer().sendMessage(
                    "Под бочкой должен находиться железный блок."
            );
            return;
        }

        if (atmService.findByBlock(barrel.getLocation()).isPresent()) {
            event.getPlayer().sendMessage(
                    "Этот банкомат уже зарегистрирован."
            );
            return;
        }

        atmService.register(
                barrel.getLocation(),
                signBlock.getLocation()
        );

        event.setLine(0, "JOY BANK");
        event.setLine(1, "Банкомат");
        event.setLine(2, "");
        event.setLine(3, "");

        event.getPlayer().sendMessage(
                "Банкомат успешно зарегистрирован."
        );
    }
}