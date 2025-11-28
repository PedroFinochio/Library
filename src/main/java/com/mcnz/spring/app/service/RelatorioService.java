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
            long totalLivros = repositorioLivro.count();
            long totalUsuarios = repositorioUsuario.count();
            long totalReservas = repositorioReserva.count();
            long reservasPendentes = repositorioReserva.countPendentes();
            long reservasAprovadas = repositorioReserva.countAprovadas();
            long livrosDisponiveis = repositorioLivro.countDisponiveis();
            long livrosIndisponiveis = repositorioLivro.countIndisponiveis();

            estatisticas.put("totalLivros", totalLivros);
            estatisticas.put("totalUsuarios", totalUsuarios);
            estatisticas.put("totalReservas", totalReservas);
            estatisticas.put("reservasPendentes", reservasPendentes);
            estatisticas.put("reservasAprovadas", reservasAprovadas);
            estatisticas.put("livrosDisponiveis", livrosDisponiveis);
            estatisticas.put("livrosIndisponiveis", livrosIndisponiveis);

            // Cálculos adicionais
            double taxaDisponibilidade = totalLivros > 0
                    ? Math.round((livrosDisponiveis * 100.0 / totalLivros) * 10.0) / 10.0
                    : 0.0;
            estatisticas.put("taxaDisponibilidade", taxaDisponibilidade);

            System.out.println("📊 Dashboard carregado: " + totalLivros + " livros, " +
                    totalReservas + " reservas");

        } catch (Exception e) {
            System.err.println("❌ Erro ao carregar dashboard: " + e.getMessage());
            estatisticas.put("totalLivros", 0L);
            estatisticas.put("totalUsuarios", 0L);
            estatisticas.put("totalReservas", 0L);
            estatisticas.put("reservasPendentes", 0L);
            estatisticas.put("reservasAprovadas", 0L);
            estatisticas.put("livrosDisponiveis", 0L);
            estatisticas.put("livrosIndisponiveis", 0L);
            estatisticas.put("taxaDisponibilidade", 0.0);
        }

        return estatisticas;
    }

    // 1. JUNÇÕES MÚLTIPLAS - Relatório completo de reservas
    public List<Map<String, Object>> getRelatorioReservasCompleto() {
        try {
            List<Map<String, Object>> resultado = repositorioReserva.findRelatorioReservasCompleto();
            System.out.println("✅ Relatório completo: " + resultado.size() + " registros");
            return resultado;
        } catch (Exception e) {
            System.err.println("❌ Erro getRelatorioReservasCompleto: " + e.getMessage());
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    // 2. SUBCONSULTAS - Livros mais reservados que a média
    public List<Map<String, Object>> getLivrosMaisReservadosQueMedia() {
        try {
            List<Map<String, Object>> resultado = repositorioLivro.findLivrosMaisReservadosQueMedia();
            System.out.println("✅ Livros acima da média: " + resultado.size() + " livros");
            return resultado;
        } catch (Exception e) {
            System.err.println("❌ Erro getLivrosMaisReservadosQueMedia: " + e.getMessage());
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    // 2b. SUBCONSULTAS - Usuários sem reservas
    public List<Map<String, Object>> getUsuariosSemReservas() {
        try {
            List<Map<String, Object>> resultado = repositorioUsuario.findUsuariosSemReservas();
            System.out.println("✅ Usuários sem reservas: " + resultado.size() + " usuários");
            return resultado;
        } catch (Exception e) {
            System.err.println("❌ Erro getUsuariosSemReservas: " + e.getMessage());
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    // 3. AGREGADAS - Estatísticas de reservas por usuário
    public List<Map<String, Object>> getEstatisticasReservasPorUsuario() {
        try {
            List<Map<String, Object>> resultado = repositorioReserva.findEstatisticasReservasPorUsuario();
            System.out.println("✅ Estatísticas por usuário: " + resultado.size() + " usuários");
            return resultado;
        } catch (Exception e) {
            System.err.println("❌ Erro getEstatisticasReservasPorUsuario: " + e.getMessage());
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    // 3b. AGREGADAS - Estatísticas de livros por autor
    public List<Map<String, Object>> getEstatisticasLivrosPorAutor() {
        try {
            List<Map<String, Object>> resultado = repositorioLivro.findEstatisticasLivrosPorAutor();
            System.out.println("✅ Estatísticas por autor: " + resultado.size() + " autores");
            return resultado;
        } catch (Exception e) {
            System.err.println("❌ Erro getEstatisticasLivrosPorAutor: " + e.getMessage());
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    // 3c. AGREGADAS - Livros com baixo estoque
    public List<Map<String, Object>> getLivrosBaixoEstoque() {
        try {
            List<Map<String, Object>> resultado = repositorioLivro.findLivrosBaixoEstoque();
            System.out.println("✅ Livros baixo estoque: " + resultado.size() + " livros");
            return resultado;
        } catch (Exception e) {
            System.err.println("❌ Erro getLivrosBaixoEstoque: " + e.getMessage());
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    // 4. COMPARAÇÃO STRINGS - Busca avançada
    public List<Map<String, Object>> buscaAvancadaLivros(String termo) {
        try {
            if (termo == null || termo.trim().isEmpty()) {
                System.out.println("⚠️ Termo de busca vazio");
                return Collections.emptyList();
            }
            List<Map<String, Object>> resultado = repositorioLivro.findBuscaAvancada(termo);
            System.out.println("✅ Busca '" + termo + "': " + resultado.size() + " resultados");
            return resultado;
        } catch (Exception e) {
            System.err.println("❌ Erro buscaAvancadaLivros: " + e.getMessage());
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    // 4b. MULTICONJUNTO - Livros mais caros que autor
    public List<Map<String, Object>> getLivrosCarosQueAutor(String autor) {
        try {
            List<Map<String, Object>> resultado = repositorioLivro.findLivrosCarosQueAutor(autor);
            System.out.println("✅ Livros mais caros que " + autor + ": " + resultado.size() + " livros");
            return resultado;
        } catch (Exception e) {
            System.err.println("❌ Erro getLivrosCarosQueAutor: " + e.getMessage());
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    // 5. ORDENAÇÃO E LIMITAÇÃO - Top livros mais caros
    public List<Map<String, Object>> getTopLivrosMaisCaros(int limite) {
        try {
            List<Map<String, Object>> resultado = repositorioLivro.findTopLivrosMaisCaros(limite);
            System.out.println("✅ Top " + limite + " mais caros: " + resultado.size() + " livros");
            return resultado;
        } catch (Exception e) {
            System.err.println("❌ Erro getTopLivrosMaisCaros: " + e.getMessage());
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    // 5b. Top livros mais reservados
    public List<Map<String, Object>> getTopLivrosMaisReservados(int limite) {
        try {
            List<Map<String, Object>> resultado = repositorioReserva.findTopLivrosMaisReservados(limite);
            System.out.println("✅ Top " + limite + " mais reservados: " + resultado.size() + " livros");
            return resultado;
        } catch (Exception e) {
            System.err.println("❌ Erro getTopLivrosMaisReservados: " + e.getMessage());
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    // 5c. Últimas reservas
    public List<Map<String, Object>> getUltimasReservas(int limite) {
        try {
            List<Map<String, Object>> resultado = repositorioReserva.findUltimasReservas(limite);
            System.out.println("✅ Últimas " + limite + " reservas: " + resultado.size() + " registros");
            return resultado;
        } catch (Exception e) {
            System.err.println("❌ Erro getUltimasReservas: " + e.getMessage());
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    // Reservas por status
    public List<Map<String, Object>> getReservasPorStatus() {
        try {
            List<Map<String, Object>> resultado = repositorioReserva.findReservasPorStatus();
            System.out.println("✅ Reservas por status: " + resultado.size() + " status");
            return resultado;
        } catch (Exception e) {
            System.err.println("❌ Erro getReservasPorStatus: " + e.getMessage());
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    // Reservas por período
    public List<Map<String, Object>> getReservasPorPeriodo(String dataInicio, String dataFim) {
        try {
            List<Map<String, Object>> resultado = repositorioReserva.findReservasPorPeriodo(dataInicio, dataFim);
            System.out.println("✅ Reservas entre " + dataInicio + " e " + dataFim + ": " + resultado.size() + " registros");
            return resultado;
        } catch (Exception e) {
            System.err.println("❌ Erro getReservasPorPeriodo: " + e.getMessage());
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    // Top usuários mais ativos
    public List<Map<String, Object>> getTopUsuariosMaisAtivos(int limite) {
        try {
            List<Map<String, Object>> resultado = repositorioUsuario.findTopUsuariosMaisAtivos(limite);
            System.out.println("✅ Top " + limite + " usuários ativos: " + resultado.size() + " usuários");
            return resultado;
        } catch (Exception e) {
            System.err.println("❌ Erro getTopUsuariosMaisAtivos: " + e.getMessage());
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    // Livros disponíveis para reserva
    public List<Map<String, Object>> getLivrosDisponiveisParaReserva() {
        try {
            List<Map<String, Object>> resultado = repositorioLivro.findLivrosDisponiveisParaReserva();
            System.out.println("✅ Livros disponíveis: " + resultado.size() + " livros");
            return resultado;
        } catch (Exception e) {
            System.err.println("❌ Erro getLivrosDisponiveisParaReserva: " + e.getMessage());
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    // Livros por faixa de preço
    public List<Map<String, Object>> getLivrosPorFaixaPreco() {
        try {
            List<Map<String, Object>> resultado = repositorioLivro.findLivrosPorFaixaPreco();
            System.out.println("✅ Distribuição por faixa de preço: " + resultado.size() + " faixas");
            return resultado;
        } catch (Exception e) {
            System.err.println("❌ Erro getLivrosPorFaixaPreco: " + e.getMessage());
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}