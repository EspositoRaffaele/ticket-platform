package org.java.spring.ticket_platform.Repository;

import java.util.Set;

import org.java.spring.ticket_platform.model.Ruolo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RuoloRepository extends JpaRepository<Ruolo, Integer>{
    public Set<Ruolo> findByNomeRuolo(String nomeRuolo);
}
