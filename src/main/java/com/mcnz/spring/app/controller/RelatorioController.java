package com.mcnz.spring.app.controller;

import com.mcnz.spring.app.service.RelatorioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin/relatorios")
public class RelatorioController {

    @Autowired
    private RelatorioService relatorioService;

    // Dashboard principal de relatórios
    @GetMapping
    public String dashboard(Model model) {
        Map<String, Object> estatisticas = relatorioService.getDashboardEstatisticas();

        System.out.println("========================================");
        System.out.println("📊 DASHBOARD - Estatísticas:");
        System.out.println("   Livros: " + estatisticas.get("totalLivros"));
        System.out.println("   Usuários: " + estatisticas.get("totalUsuarios"));
        System.out.println("   Reservas: " + estatisticas.get("totalReservas"));
        System.out.println("========================================");

        model.addAttribute("estatisticas", estatisticas);
        return "admin-relatorios-dashboard";
    }

    // ==================== JUNÇÕES MÚLTIPLAS ====================

    @GetMapping("/reservas-completo")
    public String relatorioReservasCompleto(Model model) {
        System.out.println("\n🔍 Buscando relatório completo de reservas...");

        List<Map<String, Object>> dados = relatorioService.getRelatorioReservasCompleto();

        System.out.println("✅ Dados retornados: " + dados.size() + " registros");

        if (!dados.isEmpty()) {
            System.out.println("📋 Primeiro registro:");
            System.out.println("   " + dados.get(0));
            System.out.println("📋 Colunas: " + dados.get(0).keySet());
        } else {
            System.out.println("⚠️ LISTA VAZIA! Verificar query SQL ou dados no banco.");
        }

        model.addAttribute("titulo", "Relatório Completo de Reservas (JOIN múltiplo)");
        model.addAttribute("descricao", "Consulta com JOIN entre Reservas, Usuários e Livros");
        model.addAttribute("dados", dados);
        model.addAttribute("tipo", "reservas-completo");

        return "admin-relatorio-generico";
    }

    // ==================== SUBCONSULTAS ====================

    @GetMapping("/livros-acima-media")
    public String livrosAcimaMedia(Model model) {
        System.out.println("\n🔍 Buscando livros acima da média...");

        List<Map<String, Object>> dados = relatorioService.getLivrosMaisReservadosQueMedia();

        System.out.println("✅ Dados retornados: " + dados.size() + " registros");
        if (!dados.isEmpty()) {
            System.out.println("📋 Primeiro registro: " + dados.get(0));
        }

        model.addAttribute("titulo", "Livros Mais Reservados que a Média");
        model.addAttribute("descricao", "Consulta com SUBCONSULTA para comparar com média");
        model.addAttribute("dados", dados);
        model.addAttribute("tipo", "livros-media");
        return "admin-relatorio-generico";
    }

    @GetMapping("/usuarios-sem-reservas")
    public String usuariosSemReservas(Model model) {
        System.out.println("\n🔍 Buscando usuários sem reservas...");

        List<Map<String, Object>> dados = relatorioService.getUsuariosSemReservas();

        System.out.println("✅ Dados retornados: " + dados.size() + " registros");

        model.addAttribute("titulo", "Usuários Sem Reservas");
        model.addAttribute("descricao", "Consulta com SUBCONSULTA usando NOT IN");
        model.addAttribute("dados", dados);
        model.addAttribute("tipo", "usuarios-sem-reservas");
        return "admin-relatorio-generico";
    }

    // ==================== AGREGADAS (GROUP BY, HAVING, COUNT, AVG) ====================

    @GetMapping("/estatisticas-usuarios")
    public String estatisticasUsuarios(Model model) {
        System.out.println("\n🔍 Buscando estatísticas por usuário...");

        List<Map<String, Object>> dados = relatorioService.getEstatisticasReservasPorUsuario();

        System.out.println("✅ Dados retornados: " + dados.size() + " registros");
        if (!dados.isEmpty()) {
            System.out.println("📋 Primeiro registro: " + dados.get(0));
        }

        model.addAttribute("titulo", "Estatísticas de Reservas por Usuário");
        model.addAttribute("descricao", "Consulta com GROUP BY, HAVING e funções agregadas (COUNT, SUM)");
        model.addAttribute("dados", dados);
        model.addAttribute("tipo", "estatisticas-usuarios");
        return "admin-relatorio-generico";
    }

    @GetMapping("/estatisticas-autores")
    public String estatisticasAutores(Model model) {
        System.out.println("\n🔍 Buscando estatísticas por autor...");

        List<Map<String, Object>> dados = relatorioService.getEstatisticasLivrosPorAutor();

        System.out.println("✅ Dados retornados: " + dados.size() + " registros");
        if (!dados.isEmpty()) {
            System.out.println("📋 Primeiro registro: " + dados.get(0));
        }

        model.addAttribute("titulo", "Estatísticas de Livros por Autor");
        model.addAttribute("descricao", "Consulta com GROUP BY, HAVING e funções AVG, COUNT, SUM");
        model.addAttribute("dados", dados);
        model.addAttribute("tipo", "estatisticas-autores");
        return "admin-relatorio-generico";
    }

    @GetMapping("/livros-baixo-estoque")
    public String livrosBaixoEstoque(Model model) {
        System.out.println("\n🔍 Buscando livros com baixo estoque...");

        List<Map<String, Object>> dados = relatorioService.getLivrosBaixoEstoque();

        System.out.println("✅ Dados retornados: " + dados.size() + " registros");

        model.addAttribute("titulo", "Livros com Baixo Estoque");
        model.addAttribute("descricao", "Consulta agregada com filtro HAVING para estoque crítico");
        model.addAttribute("dados", dados);
        model.addAttribute("tipo", "baixo-estoque");
        return "admin-relatorio-generico";
    }

    // ==================== COMPARAÇÃO DE STRINGS E MULTICONJUNTO ====================

    @GetMapping("/busca-avancada")
    public String buscaAvancada(@RequestParam(required = false, defaultValue = "") String termo,
                                Model model) {
        System.out.println("\n🔍 Busca avançada com termo: '" + termo + "'");

        List<Map<String, Object>> dados = relatorioService.buscaAvancadaLivros(termo);

        System.out.println("✅ Dados retornados: " + dados.size() + " registros");

        model.addAttribute("titulo", "Busca Avançada de Livros");
        model.addAttribute("descricao", "Consulta com LIKE para comparação de strings");
        model.addAttribute("dados", dados);
        model.addAttribute("termo", termo);
        model.addAttribute("tipo", "busca-avancada");
        return "admin-relatorio-busca";
    }

    @GetMapping("/livros-caros-autor")
    public String livrosCarosQueAutor(@RequestParam(required = false, defaultValue = "Machado de Assis") String autor,
                                      Model model) {
        System.out.println("\n🔍 Buscando livros mais caros que autor: " + autor);

        List<Map<String, Object>> dados = relatorioService.getLivrosCarosQueAutor(autor);

        System.out.println("✅ Dados retornados: " + dados.size() + " registros");

        model.addAttribute("titulo", "Livros Mais Caros Que Obras de " + autor);
        model.addAttribute("descricao", "Consulta com operador SOME/ANY para comparação multiconjunto");
        model.addAttribute("dados", dados);
        model.addAttribute("autorReferencia", autor);
        model.addAttribute("tipo", "livros-caros");
        return "admin-relatorio-generico";
    }

    // ==================== ORDENAÇÃO E LIMITAÇÃO ====================

    @GetMapping("/top-livros-caros")
    public String topLivrosCaros(@RequestParam(required = false, defaultValue = "10") int limite,
                                 Model model) {
        System.out.println("\n🔍 Buscando top " + limite + " livros mais caros...");

        List<Map<String, Object>> dados = relatorioService.getTopLivrosMaisCaros(limite);

        System.out.println("✅ Dados retornados: " + dados.size() + " registros");

        model.addAttribute("titulo", "Top " + limite + " Livros Mais Caros");
        model.addAttribute("descricao", "Consulta com ORDER BY DESC e LIMIT");
        model.addAttribute("dados", dados);
        model.addAttribute("tipo", "top-caros");
        return "admin-relatorio-generico";
    }

    @GetMapping("/top-livros-reservados")
    public String topLivrosReservados(@RequestParam(required = false, defaultValue = "10") int limite,
                                      Model model) {
        System.out.println("\n🔍 Buscando top " + limite + " livros mais reservados...");

        List<Map<String, Object>> dados = relatorioService.getTopLivrosMaisReservados(limite);

        System.out.println("✅ Dados retornados: " + dados.size() + " registros");

        model.addAttribute("titulo", "Top " + limite + " Livros Mais Reservados");
        model.addAttribute("descricao", "Consulta com JOIN, GROUP BY, ORDER BY e LIMIT");
        model.addAttribute("dados", dados);
        model.addAttribute("tipo", "top-reservados");
        return "admin-relatorio-generico";
    }

    @GetMapping("/ultimas-reservas")
    public String ultimasReservas(@RequestParam(required = false, defaultValue = "20") int limite,
                                  Model model) {
        System.out.println("\n🔍 Buscando últimas " + limite + " reservas...");

        List<Map<String, Object>> dados = relatorioService.getUltimasReservas(limite);

        System.out.println("✅ Dados retornados: " + dados.size() + " registros");

        model.addAttribute("titulo", "Últimas " + limite + " Reservas");
        model.addAttribute("descricao", "Consulta com ORDER BY data DESC e LIMIT");
        model.addAttribute("dados", dados);
        model.addAttribute("tipo", "ultimas-reservas");
        return "admin-relatorio-generico";
    }

    // ==================== RELATÓRIOS ADICIONAIS ====================

    @GetMapping("/reservas-por-status")
    public String reservasPorStatus(Model model) {
        List<Map<String, Object>> dados = relatorioService.getReservasPorStatus();
        model.addAttribute("titulo", "Distribuição de Reservas por Status");
        model.addAttribute("descricao", "Consulta agregada com GROUP BY status");
        model.addAttribute("dados", dados);
        model.addAttribute("tipo", "reservas-status");
        return "admin-relatorio-generico";
    }

    @GetMapping("/reservas-periodo")
    public String reservasPorPeriodo(@RequestParam(required = false) String dataInicio,
                                     @RequestParam(required = false) String dataFim,
                                     Model model) {
        if (dataInicio == null || dataInicio.isEmpty()) {
            dataInicio = java.time.LocalDate.now().minusDays(30).toString();
        }
        if (dataFim == null || dataFim.isEmpty()) {
            dataFim = java.time.LocalDate.now().toString();
        }

        List<Map<String, Object>> dados = relatorioService.getReservasPorPeriodo(dataInicio, dataFim);
        model.addAttribute("titulo", "Reservas por Período");
        model.addAttribute("descricao", "Consulta com filtro de data BETWEEN");
        model.addAttribute("dados", dados);
        model.addAttribute("dataInicio", dataInicio);
        model.addAttribute("dataFim", dataFim);
        model.addAttribute("tipo", "reservas-periodo");
        return "admin-relatorio-periodo";
    }

    @GetMapping("/top-usuarios-ativos")
    public String topUsuariosAtivos(@RequestParam(required = false, defaultValue = "10") int limite,
                                    Model model) {
        List<Map<String, Object>> dados = relatorioService.getTopUsuariosMaisAtivos(limite);
        model.addAttribute("titulo", "Top " + limite + " Usuários Mais Ativos");
        model.addAttribute("descricao", "Consulta com JOIN, GROUP BY, COUNT e ORDER BY");
        model.addAttribute("dados", dados);
        model.addAttribute("tipo", "top-usuarios");
        return "admin-relatorio-generico";
    }

    @GetMapping("/livros-disponiveis")
    public String livrosDisponiveis(Model model) {
        List<Map<String, Object>> dados = relatorioService.getLivrosDisponiveisParaReserva();
        model.addAttribute("titulo", "Livros Disponíveis para Reserva");
        model.addAttribute("descricao", "Consulta com filtro WHERE quantidadeDisponivel > 0");
        model.addAttribute("dados", dados);
        model.addAttribute("tipo", "livros-disponiveis");
        return "admin-relatorio-generico";
    }

    @GetMapping("/livros-faixa-preco")
    public String livrosPorFaixaPreco(Model model) {
        List<Map<String, Object>> dados = relatorioService.getLivrosPorFaixaPreco();
        model.addAttribute("titulo", "Distribuição de Livros por Faixa de Preço");
        model.addAttribute("descricao", "Consulta com CASE WHEN para categorização");
        model.addAttribute("dados", dados);
        model.addAttribute("tipo", "faixa-preco");
        return "admin-relatorio-generico";
    }
}