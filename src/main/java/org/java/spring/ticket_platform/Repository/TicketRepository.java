package org.java.spring.ticket_platform.Repository;

import java.util.List;
import java.util.Optional;

import org.java.spring.ticket_platform.model.Ticket;
import org.java.spring.ticket_platform.model.Utente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketRepository extends JpaRepository<Ticket, Integer>{
    public List<Ticket> findByTitoloContainingIgnoreCase(String title);
    public List<Ticket> findByTitoloContainingIgnoreCaseAndUtenteId(String title, Integer utenteId);
    public List<Ticket> findByUtente(Optional<Utente> utente);
    public Optional<Ticket> findByUtenteId(Integer utenteId);
    public Optional<Ticket> findByIdAndUtenteId(Integer id, Integer utenteId);
    public List<Ticket> findByUtenteIdAndStatoIdNot(Integer idUtente, Integer idStato);
}
