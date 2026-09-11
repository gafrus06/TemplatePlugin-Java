package ru.joutak.template.storage;

import ru.joutak.template.atm.Atm;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface AtmRepository {

    void save(Atm atm);

    void delete(UUID atmId);

    Optional<Atm> findById(UUID atmId);

    Collection<Atm> findAll();
}