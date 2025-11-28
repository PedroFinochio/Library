package com.mcnz.spring.app.repository;

import com.mcnz.spring.app.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface RepositorioUsuario extends JpaRepository<Usuario, Long> {

    // ==================== MÉTODOS EXISTENTES ====================

    Optional<Usuario> findByUsername(String username);

    Optional<Usuario> findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    // ==================== CONSULTAS SQL COMPLEXAS PARA RELATÓRIOS ====================

    // 2. SUBCONSULTAS - Usuários que nunca fizeram reservas (NOT IN)
    @Query(value = "SELECT " +
            "u.id AS id, " +
            "u.username AS username, " +
            "u.email AS email, " +
            "u.role AS role " +
            "FROM usuarios u " +
            "WHERE u.id NOT IN ( " +
            "    SELECT DISTINCT r.usuario_id FROM reservas r WHERE r.usuario_id IS NOT NULL " +
            ") " +
            "ORDER BY u.username",
            nativeQuery = true)
    List<Map<String, Object>> findUsuariosSemReservas();

    // 5. ORDENAÇÃO E LIMITAÇÃO - Top usuários mais ativos (com mais reservas)
    @Query(value = "SELECT " +
            "u.id AS id, " +
            "u.username AS username, " +
            "u.email AS email, " +
            "COUNT(r.id) AS total_reservas " +
            "FROM usuarios u " +
            "INNER JOIN reservas r ON u.id = r.usuario_id " +
            "GROUP BY u.id, u.username, u.email " +
            "ORDER BY total_reservas DESC " +
            "LIMIT :limite",
            nativeQuery = true)
    List<Map<String, Object>> findTopUsuariosMaisAtivos(@Param("limite") int limite);
}