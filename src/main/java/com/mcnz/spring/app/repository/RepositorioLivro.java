package com.mcnz.spring.app.repository;

import com.mcnz.spring.app.model.Livro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RepositorioLivro extends JpaRepository<Livro, Integer> {
    List<Livro> findByTituloContaining(String titulo);
}