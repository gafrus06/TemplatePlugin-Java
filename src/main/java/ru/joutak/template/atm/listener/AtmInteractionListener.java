package ru.joutak.template.atm.listener;

import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import ru.joutak.template.atm.Atm;
import ru.joutak.template.atm.AtmService;
import ru.joutak.template.gui.AtmMenu;

import java.util.Optional;

public class AtmInteractionListener implements Listener {

    private final AtmService atmService;
    private final AtmMenu atmMenu;

    public AtmInteractionListener(
            AtmService atmService,
            AtmMenu atmMenu
    ) {
        this.atmService = atmService;
        this.atmMenu = atmMenu;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {

        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        Block block = event.getClickedBlock();

        if (block == null) {
            return;
        }

        Optional<Atm> atm =
                atmService.findByBlock(
                        block.getLocation()
                );

        if (atm.isEmpty()) {
            return;
        }

        event.setCancelled(true);

        atmMenu.open(
                event.getPlayer(),
                atm.get()
        );
    }
}