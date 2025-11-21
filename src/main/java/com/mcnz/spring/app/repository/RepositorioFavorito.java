package com.mcnz.spring.app.repository;

import com.mcnz.spring.app.model.Favorito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RepositorioFavorito extends JpaRepository<Favorito, Long> {
    boolean existsByUsuarioIdAndLivroId(Long usuarioId, Integer livroId);
    void deleteByUsuarioIdAndLivroId(Long usuarioId, Integer livroId);

    @Query("SELECT f.livroId FROM Favorito f WHERE f.usuarioId = :usuarioId")
    List<Integer> findLivroIdsByUsuarioId(Long usuarioId);

    long countByLivroId(Integer livroId);
}