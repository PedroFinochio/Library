package com.mcnz.spring.app.controller;

import com.mcnz.spring.app.repository.RepositorioLivro;
import com.mcnz.spring.app.repository.RepositorioReserva;
import com.mcnz.spring.app.repository.RepositorioUsuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller de teste para verificar se as queries SQL estão funcionando
 * Acesse: http://localhost:8080/api/test-queries
 *
 * REMOVER EM PRODUÇÃO!
 */
@RestController
@RequestMapping("/api/test-queries")
public class TestQueriesController {

    @Autowired
    private RepositorioLivro repositorioLivro;

    @Autowired
    private RepositorioReserva repositorioReserva;

    @Autowired
    private RepositorioUsuario repositorioUsuario;

    @GetMapping
    public Map<String, Object> testAllQueries() {
        Map<String, Object> results = new HashMap<>();

        try {
            // Teste 1: Contagens básicas
            results.put("total_usuarios", repositorioUsuario.count());
            results.put("total_livros", repositorioLivro.count());
            results.put("total_reservas", repositorioReserva.count());
            results.put("reservas_pendentes", repositorioReserva.countPendentes());
            results.put("reservas_aprovadas", repositorioReserva.countAprovadas());

            // Teste 2: Relatório Completo
            List<Map<String, Object>> relatorioCompleto = repositorioReserva.findRelatorioReservasCompleto();
            results.put("relatorio_completo_count", relatorioCompleto.size());
            results.put("relatorio_completo_sample", relatorioCompleto.isEmpty() ? "VAZIO" : relatorioCompleto.get(0));

            // Teste 3: Livros Mais Reservados Que Média
            List<Map<String, Object>> livrosMedia = repositorioLivro.findLivrosMaisReservadosQueMedia();
            results.put("livros_acima_media_count", livrosMedia.size());
            results.put("livros_acima_media_sample", livrosMedia.isEmpty() ? "VAZIO" : livrosMedia.get(0));

            // Teste 4: Estatísticas por Usuário
            List<Map<String, Object>> estatUsuarios = repositorioReserva.findEstatisticasReservasPorUsuario();
            results.put("estatisticas_usuarios_count", estatUsuarios.size());
            results.put("estatisticas_usuarios_sample", estatUsuarios.isEmpty() ? "VAZIO" : estatUsuarios.get(0));

            // Teste 5: Estatísticas por Autor
            List<Map<String, Object>> estatAutores = repositorioLivro.findEstatisticasLivrosPorAutor();
            results.put("estatisticas_autores_count", estatAutores.size());
            results.put("estatisticas_autores_sample", estatAutores.isEmpty() ? "VAZIO" : estatAutores.get(0));

            // Teste 6: Livros Baixo Estoque
            List<Map<String, Object>> baixoEstoque = repositorioLivro.findLivrosBaixoEstoque();
            results.put("livros_baixo_estoque_count", baixoEstoque.size());

            // Teste 7: Usuários Sem Reservas
            List<Map<String, Object>> semReservas = repositorioUsuario.findUsuariosSemReservas();
            results.put("usuarios_sem_reservas_count", semReservas.size());

            // Teste 8: Top Livros Mais Reservados
            List<Map<String, Object>> topLivros = repositorioReserva.findTopLivrosMaisReservados(10);
            results.put("top_livros_reservados_count", topLivros.size());
            results.put("top_livros_reservados_sample", topLivros.isEmpty() ? "VAZIO" : topLivros.get(0));

            // Teste 9: Top Usuários Ativos
            List<Map<String, Object>> topUsuarios = repositorioUsuario.findTopUsuariosMaisAtivos(10);
            results.put("top_usuarios_ativos_count", topUsuarios.size());
            results.put("top_usuarios_ativos_sample", topUsuarios.isEmpty() ? "VAZIO" : topUsuarios.get(0));

            // Teste 10: Reservas por Status
            List<Map<String, Object>> porStatus = repositorioReserva.findReservasPorStatus();
            results.put("reservas_por_status", porStatus);

            results.put("status", "SUCCESS");
            results.put("message", "Todas as queries foram executadas com sucesso!");

        } catch (Exception e) {
            results.put("status", "ERROR");
            results.put("error_message", e.getMessage());
            results.put("error_type", e.getClass().getSimpleName());
            e.printStackTrace();
        }

        return results;
    }

    @GetMapping("/relatorio-completo")
    public List<Map<String, Object>> testRelatorioCompleto() {
        return repositorioReserva.findRelatorioReservasCompleto();
    }

    @GetMapping("/livros-acima-media")
    public List<Map<String, Object>> testLivrosAcimaMedia() {
        return repositorioLivro.findLivrosMaisReservadosQueMedia();
    }

    @GetMapping("/estatisticas-usuarios")
    public List<Map<String, Object>> testEstatisticasUsuarios() {
        return repositorioReserva.findEstatisticasReservasPorUsuario();
    }

    @GetMapping("/estatisticas-autores")
    public List<Map<String, Object>> testEstatisticasAutores() {
        return repositorioLivro.findEstatisticasLivrosPorAutor();
    }
}