package ru.joutak.template.atm;

import org.bukkit.Location;
import org.bukkit.World;
import ru.joutak.template.storage.AtmRepository;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class AtmService {

    private final AtmRepository atmRepository;
    private final Map<UUID, Atm> atms = new HashMap<>();

    public AtmService(AtmRepository atmRepository) {
        this.atmRepository = atmRepository;

        for (Atm atm : atmRepository.findAll()) {
            atms.put(atm.id(), atm);
        }
    }

    public Atm register(
            Location barrelLocation,
            Location signLocation
    ) {
        Atm atm = new Atm(
                UUID.randomUUID(),
                barrelLocation.getWorld().getUID(),

                barrelLocation.getBlockX(),
                barrelLocation.getBlockY(),
                barrelLocation.getBlockZ(),

                signLocation.getBlockX(),
                signLocation.getBlockY(),
                signLocation.getBlockZ()
        );
        atmRepository.save(atm);
        atms.put(atm.id(), atm);

        return atm;
    }

    public Optional<Atm> findByBlock(Location location) {
        World world = location.getWorld();

        if (world == null) {
            return Optional.empty();
        }

        for (Atm atm : atms.values()) {

            if (!atm.worldId().equals(world.getUID())) {
                continue;
            }

            int x = location.getBlockX();
            int y = location.getBlockY();
            int z = location.getBlockZ();

            boolean isBarrel =
                    x == atm.barrelX()
                            && y == atm.barrelY()
                            && z == atm.barrelZ();

            boolean isSign =
                    x == atm.signX()
                            && y == atm.signY()
                            && z == atm.signZ();

            boolean isBase =
                    x == atm.barrelX()
                            && y == atm.barrelY() - 1
                            && z == atm.barrelZ();

            if (isBarrel || isSign || isBase) {
                return Optional.of(atm);
            }
        }

        return Optional.empty();
    }

    public Collection<Atm> getAll() {
        return atms.values();
    }

    public void unregister(UUID atmId) {

        atmRepository.delete(atmId);
        atms.remove(atmId);
    }
    public Optional<Atm> findById(UUID atmId) {
        return Optional.ofNullable(
                atms.get(atmId)
        );
    }
}