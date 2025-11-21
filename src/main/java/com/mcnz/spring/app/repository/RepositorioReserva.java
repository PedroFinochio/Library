package com.mcnz.spring.app.repository;

import com.mcnz.spring.app.model.Reserva;
import com.mcnz.spring.app.model.Usuario;
import com.mcnz.spring.app.model.Livro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RepositorioReserva extends JpaRepository<Reserva, Long> {

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
}