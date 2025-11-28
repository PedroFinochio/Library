package com.mcnz.spring.app.repository;

import com.mcnz.spring.app.model.Reserva;
import com.mcnz.spring.app.model.Usuario;
import com.mcnz.spring.app.model.Livro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface RepositorioReserva extends JpaRepository<Reserva, Long> {

    // ==================== MÉTODOS EXISTENTES ====================

    List<Reserva> findByUsuario(Usuario usuario);

    List<Reserva> findByUsuarioOrderByDataReservaDesc(Usuario usuario);

    List<Reserva> findByLivro(Livro livro);

    List<Reserva> findByStatus(String status);

    List<Reserva> findAllByOrderByDataReservaDesc();

    // Busca reservas ativas (pendentes ou aprovadas) de um usuário para um livro
    @Query("SELECT r FROM Reserva r WHERE r.usuario = :usuario AND r.livro = :livro AND r.status IN ('PENDENTE', 'APROVADA')")
    Optional<Reserva> findReservaAtivaByUsuarioAndLivro(@Param("usuario") Usuario usuario, @Param("livro") Livro livro);

    // Verifica se usuário já tem reserva ativa para o livro
    @Query("SELECT COUNT(r) > 0 FROM Reserva r WHERE r.usuario = :usuario AND r.livro = :livro AND r.status IN ('PENDENTE', 'APROVADA')")
    boolean existsReservaAtivaByUsuarioAndLivro(@Param("usuario") Usuario usuario, @Param("livro") Livro livro);

    // Conta reservas pendentes
    @Query("SELECT COUNT(r) FROM Reserva r WHERE r.status = 'PENDENTE'")
    long countPendentes();

    // Conta reservas aprovadas (empréstimos ativos)
    @Query("SELECT COUNT(r) FROM Reserva r WHERE r.status = 'APROVADA'")
    long countAprovadas();

    // Busca reservas do usuário por status
    List<Reserva> findByUsuarioAndStatusOrderByDataReservaDesc(Usuario usuario, String status);

    // ==================== CONSULTAS SQL COMPLEXAS PARA RELATÓRIOS - CORRIGIDAS ====================

    // 1. JUNÇÕES MÚLTIPLAS - Relatório completo de reservas (CORRIGIDO - datas como STRING)
    @Query(value = "SELECT " +
            "r.id AS reserva_id, " +
            "u.username AS usuario, " +
            "u.email AS email, " +
            "l.titulo AS livro, " +
            "l.autor AS autor, " +
            "DATE_FORMAT(r.data_reserva, '%d/%m/%Y %H:%i') AS data_reserva, " +
            "DATE_FORMAT(r.data_devolucao_prevista, '%d/%m/%Y') AS devolucao_prevista, " +
            "r.status AS status, " +
            "r.observacao AS observacao " +
            "FROM reservas r " +
            "INNER JOIN usuarios u ON r.usuario_id = u.id " +
            "INNER JOIN livros l ON r.livro_id = l.id " +
            "ORDER BY r.data_reserva DESC",
            nativeQuery = true)
    List<Map<String, Object>> findRelatorioReservasCompleto();

    // 3. AGREGADAS - Estatísticas de reservas por usuário (GROUP BY, HAVING, COUNT, SUM)
    @Query(value = "SELECT " +
            "u.username AS usuario, " +
            "u.email AS email, " +
            "COUNT(r.id) AS total_reservas, " +
            "SUM(CASE WHEN r.status = 'APROVADA' THEN 1 ELSE 0 END) AS aprovadas, " +
            "SUM(CASE WHEN r.status = 'PENDENTE' THEN 1 ELSE 0 END) AS pendentes, " +
            "SUM(CASE WHEN r.status = 'REJEITADA' THEN 1 ELSE 0 END) AS rejeitadas, " +
            "SUM(CASE WHEN r.status = 'DEVOLVIDA' THEN 1 ELSE 0 END) AS devolvidas, " +
            "SUM(CASE WHEN r.status = 'CANCELADA' THEN 1 ELSE 0 END) AS canceladas " +
            "FROM usuarios u " +
            "LEFT JOIN reservas r ON u.id = r.usuario_id " +
            "GROUP BY u.id, u.username, u.email " +
            "HAVING COUNT(r.id) > 0 " +
            "ORDER BY total_reservas DESC",
            nativeQuery = true)
    List<Map<String, Object>> findEstatisticasReservasPorUsuario();

    // 5. ORDENAÇÃO E LIMITAÇÃO - Top livros mais reservados (ORDER BY + LIMIT)
    @Query(value = "SELECT " +
            "l.id AS livro_id, " +
            "l.titulo AS titulo, " +
            "l.autor AS autor, " +
            "l.preco AS preco, " +
            "COUNT(r.id) AS total_reservas " +
            "FROM livros l " +
            "INNER JOIN reservas r ON l.id = r.livro_id " +
            "GROUP BY l.id, l.titulo, l.autor, l.preco " +
            "ORDER BY total_reservas DESC " +
            "LIMIT :limite",
            nativeQuery = true)
    List<Map<String, Object>> findTopLivrosMaisReservados(@Param("limite") int limite);

    // 5b. ORDENAÇÃO E LIMITAÇÃO - Últimas reservas do sistema (CORRIGIDO)
    @Query(value = "SELECT " +
            "r.id AS id, " +
            "u.username AS usuario, " +
            "l.titulo AS livro, " +
            "DATE_FORMAT(r.data_reserva, '%d/%m/%Y %H:%i') AS data_reserva, " +
            "r.status AS status " +
            "FROM reservas r " +
            "INNER JOIN usuarios u ON r.usuario_id = u.id " +
            "INNER JOIN livros l ON r.livro_id = l.id " +
            "ORDER BY r.data_reserva DESC " +
            "LIMIT :limite",
            nativeQuery = true)
    List<Map<String, Object>> findUltimasReservas(@Param("limite") int limite);

    // AGREGADAS - Distribuição de reservas por status
    @Query(value = "SELECT " +
            "r.status AS status, " +
            "COUNT(*) AS total " +
            "FROM reservas r " +
            "GROUP BY r.status " +
            "ORDER BY total DESC",
            nativeQuery = true)
    List<Map<String, Object>> findReservasPorStatus();

    // FILTRO COM BETWEEN - Reservas por período de datas (CORRIGIDO)
    @Query(value = "SELECT " +
            "r.id AS id, " +
            "u.username AS usuario, " +
            "l.titulo AS livro, " +
            "DATE_FORMAT(r.data_reserva, '%d/%m/%Y %H:%i') AS data_reserva, " +
            "r.status AS status " +
            "FROM reservas r " +
            "INNER JOIN usuarios u ON r.usuario_id = u.id " +
            "INNER JOIN livros l ON r.livro_id = l.id " +
            "WHERE DATE(r.data_reserva) BETWEEN :dataInicio AND :dataFim " +
            "ORDER BY r.data_reserva DESC",
            nativeQuery = true)
    List<Map<String, Object>> findReservasPorPeriodo(
            @Param("dataInicio") String dataInicio,
            @Param("dataFim") String dataFim);
}