package org.java.spring.ticket_platform.Repository;

import java.util.List;
import java.util.Optional;

import org.java.spring.ticket_platform.model.Utente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UtenteRepository extends JpaRepository<Utente, Integer>{
    Optional<Utente> findById(Integer id);
    Optional<Utente> findByUsername(String username);
    List<Utente> findByRuoliNomeRuoloAndStatoTrue(String nomeRuolo);
}
