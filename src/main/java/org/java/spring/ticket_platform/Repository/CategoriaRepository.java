package org.java.spring.ticket_platform.Repository;

import org.java.spring.ticket_platform.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriaRepository extends JpaRepository<Categoria, Integer>{
    public Categoria findByNome(String nome);
}
