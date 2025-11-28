package com.mcnz.spring.app.service;

import com.mcnz.spring.app.repository.RepositorioLivro;
import com.mcnz.spring.app.repository.RepositorioReserva;
import com.mcnz.spring.app.repository.RepositorioUsuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RelatorioService {

    @Autowired
    private RepositorioLivro repositorioLivro;

    @Autowired
    private RepositorioReserva repositorioReserva;

    @Autowired
    private RepositorioUsuario repositorioUsuario;

    // Dashboard - Estatísticas gerais do sistema
    public Map<String, Object> getDashboardEstatisticas() {
        Map<String, Object> estatisticas = new HashMap<>();

        try {
            estatisticas.put("totalLivros", repositorioLivro.count());
        } catch (Exception e) {
            estatisticas.put("totalLivros", 0L);
        }

        try {
            estatisticas.put("totalUsuarios", repositorioUsuario.count());
        } catch (Exception e) {
            estatisticas.put("totalUsuarios", 0L);
        }

        try {
            estatisticas.put("totalReservas", repositorioReserva.count());
        } catch (Exception e) {
            estatisticas.put("totalReservas", 0L);
        }

        try {
            estatisticas.put("reservasPendentes", repositorioReserva.countPendentes());
        } catch (Exception e) {
            estatisticas.put("reservasPendentes", 0L);
        }

        try {
            estatisticas.put("reservasAprovadas", repositorioReserva.countAprovadas());
        } catch (Exception e) {
            estatisticas.put("reservasAprovadas", 0L);
        }

        try {
            estatisticas.put("livrosDisponiveis", repositorioLivro.countDisponiveis());
        } catch (Exception e) {
            estatisticas.put("livrosDisponiveis", 0L);
        }

        try {
            estatisticas.put("livrosIndisponiveis", repositorioLivro.countIndisponiveis());
        } catch (Exception e) {
            estatisticas.put("livrosIndisponiveis", 0L);
        }

        return estatisticas;
    }

    // 1. JUNÇÕES MÚLTIPLAS - Relatório completo de reservas
    public List<Map<String, Object>> getRelatorioReservasCompleto() {
        try {
            return repositorioReserva.findRelatorioReservasCompleto();
        } catch (Exception e) {
            System.err.println("Erro getRelatorioReservasCompleto: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    // 2. SUBCONSULTAS - Livros mais reservados que a média
    public List<Map<String, Object>> getLivrosMaisReservadosQueMedia() {
        try {
            return repositorioLivro.findLivrosMaisReservadosQueMedia();
        } catch (Exception e) {
            System.err.println("Erro getLivrosMaisReservadosQueMedia: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    // 2b. SUBCONSULTAS - Usuários sem reservas
    public List<Map<String, Object>> getUsuariosSemReservas() {
        try {
            return repositorioUsuario.findUsuariosSemReservas();
        } catch (Exception e) {
            System.err.println("Erro getUsuariosSemReservas: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    // 3. AGREGADAS - Estatísticas de reservas por usuário
    public List<Map<String, Object>> getEstatisticasReservasPorUsuario() {
        try {
            return repositorioReserva.findEstatisticasReservasPorUsuario();
        } catch (Exception e) {
            System.err.println("Erro getEstatisticasReservasPorUsuario: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    // 3b. AGREGADAS - Estatísticas de livros por autor
    public List<Map<String, Object>> getEstatisticasLivrosPorAutor() {
        try {
            return repositorioLivro.findEstatisticasLivrosPorAutor();
        } catch (Exception e) {
            System.err.println("Erro getEstatisticasLivrosPorAutor: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    // 3c. AGREGADAS - Livros com baixo estoque
    public List<Map<String, Object>> getLivrosBaixoEstoque() {
        try {
            return repositorioLivro.findLivrosBaixoEstoque();
        } catch (Exception e) {
            System.err.println("Erro getLivrosBaixoEstoque: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    // 4. COMPARAÇÃO STRINGS - Busca avançada
    public List<Map<String, Object>> buscaAvancadaLivros(String termo) {
        try {
            if (termo == null || termo.trim().isEmpty()) {
                return Collections.emptyList();
            }
            return repositorioLivro.findBuscaAvancada(termo);
        } catch (Exception e) {
            System.err.println("Erro buscaAvancadaLivros: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    // 4b. MULTICONJUNTO - Livros mais caros que autor
    public List<Map<String, Object>> getLivrosCarosQueAutor(String autor) {
        try {
            return repositorioLivro.findLivrosCarosQueAutor(autor);
        } catch (Exception e) {
            System.err.println("Erro getLivrosCarosQueAutor: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    // 5. ORDENAÇÃO E LIMITAÇÃO - Top livros mais caros
    public List<Map<String, Object>> getTopLivrosMaisCaros(int limite) {
        try {
            return repositorioLivro.findTopLivrosMaisCaros(limite);
        } catch (Exception e) {
            System.err.println("Erro getTopLivrosMaisCaros: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    // 5b. Top livros mais reservados
    public List<Map<String, Object>> getTopLivrosMaisReservados(int limite) {
        try {
            return repositorioReserva.findTopLivrosMaisReservados(limite);
        } catch (Exception e) {
            System.err.println("Erro getTopLivrosMaisReservados: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    // 5c. Últimas reservas
    public List<Map<String, Object>> getUltimasReservas(int limite) {
        try {
            return repositorioReserva.findUltimasReservas(limite);
        } catch (Exception e) {
            System.err.println("Erro getUltimasReservas: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    // Reservas por status
    public List<Map<String, Object>> getReservasPorStatus() {
        try {
            return repositorioReserva.findReservasPorStatus();
        } catch (Exception e) {
            System.err.println("Erro getReservasPorStatus: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    // Reservas por período
    public List<Map<String, Object>> getReservasPorPeriodo(String dataInicio, String dataFim) {
        try {
            return repositorioReserva.findReservasPorPeriodo(dataInicio, dataFim);
        } catch (Exception e) {
            System.err.println("Erro getReservasPorPeriodo: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    // Top usuários mais ativos
    public List<Map<String, Object>> getTopUsuariosMaisAtivos(int limite) {
        try {
            return repositorioUsuario.findTopUsuariosMaisAtivos(limite);
        } catch (Exception e) {
            System.err.println("Erro getTopUsuariosMaisAtivos: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    // Livros disponíveis para reserva
    public List<Map<String, Object>> getLivrosDisponiveisParaReserva() {
        try {
            return repositorioLivro.findLivrosDisponiveisParaReserva();
        } catch (Exception e) {
            System.err.println("Erro getLivrosDisponiveisParaReserva: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    // Livros por faixa de preço
    public List<Map<String, Object>> getLivrosPorFaixaPreco() {
        try {
            return repositorioLivro.findLivrosPorFaixaPreco();
        } catch (Exception e) {
            System.err.println("Erro getLivrosPorFaixaPreco: " + e.getMessage());
            return Collections.emptyList();
        }
    }
}