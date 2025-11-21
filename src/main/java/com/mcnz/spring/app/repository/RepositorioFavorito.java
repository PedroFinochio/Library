package com.mcnz.spring.app.repository;

import com.mcnz.spring.app.model.Favorito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RepositorioFavorito extends JpaRepository<Favorito, Long> {

    // Busca todos os favoritos de um usuário
    List<Favorito> findByUsuarioId(Long usuarioId);

    // Busca apenas os IDs dos livros favoritados por um usuário
    @Query("SELECT f.livroId FROM Favorito f WHERE f.usuarioId = :usuarioId")
    List<Integer> findLivroIdsByUsuarioId(@Param("usuarioId") Long usuarioId);

    // Verifica se um livro está nos favoritos do usuário
    boolean existsByUsuarioIdAndLivroId(Long usuarioId, Integer livroId);

    // Remove um favorito específico
    void deleteByUsuarioIdAndLivroId(Long usuarioId, Integer livroId);

    // Conta quantos usuários favoritaram um livro
    long countByLivroId(Integer livroId);

    // Remove todos os favoritos de um livro (útil ao deletar livro)
    void deleteByLivroId(Integer livroId);

    // Remove todos os favoritos de um usuário
    void deleteByUsuarioId(Long usuarioId);
}