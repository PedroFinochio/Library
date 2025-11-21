package com.mcnz.spring.app.controller;

import com.mcnz.spring.app.model.Livro;
import com.mcnz.spring.app.model.Reserva;
import com.mcnz.spring.app.model.Usuario;
import com.mcnz.spring.app.repository.RepositorioLivro;
import com.mcnz.spring.app.repository.RepositorioReserva;
import com.mcnz.spring.app.repository.RepositorioUsuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private RepositorioLivro repositorioLivro;

    @Autowired
    private RepositorioUsuario repositorioUsuario;

    @Autowired
    private RepositorioReserva repositorioReserva;

    // Dashboard do Admin/Bibliotecário
    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        Usuario usuario = repositorioUsuario.findByUsername(userDetails.getUsername()).orElse(null);
        List<Livro> livros = repositorioLivro.findAll();

        model.addAttribute("usuario", usuario);
        model.addAttribute("totalLivros", livros.size());
        model.addAttribute("livros", livros);
        model.addAttribute("reservasPendentes", repositorioReserva.countPendentes());
        model.addAttribute("emprestimosAtivos", repositorioReserva.countAprovadas());

        return "admin-dashboard";
    }

    // Gerenciar biblioteca (visualizar todos os livros)
    @GetMapping("/biblioteca")
    public String biblioteca(@RequestParam(required = false) String busca,
                             @AuthenticationPrincipal UserDetails userDetails,
                             Model model) {
        List<Livro> livros;

        if (busca != null && !busca.trim().isEmpty()) {
            livros = repositorioLivro.findByTituloContaining(busca);
            model.addAttribute("busca", busca);
        } else {
            livros = repositorioLivro.findAll();
        }

        Usuario usuario = repositorioUsuario.findByUsername(userDetails.getUsername()).orElse(null);

        model.addAttribute("livros", livros);
        model.addAttribute("usuario", usuario);

        return "admin-biblioteca";
    }

    // Formulário de novo livro
    @GetMapping("/livro/novo")
    public String novoLivroForm(Model model) {
        model.addAttribute("livro", new Livro());
        return "admin-livro-form";
    }

    // Salvar livro (novo ou editar)
    @PostMapping("/livro/salvar")
    public String salvarLivro(@ModelAttribute Livro livro, RedirectAttributes redirectAttributes) {
        // Se é novo livro, quantidade disponível = quantidade total
        if (livro.getId() == 0) {
            livro.setQuantidadeDisponivel(livro.getQuantidade());
        }
        livro.setDisponivel(livro.getQuantidadeDisponivel() > 0);
        repositorioLivro.save(livro);
        redirectAttributes.addFlashAttribute("sucesso",
                livro.getId() == 0 ? "Livro adicionado com sucesso!" : "Livro atualizado com sucesso!");
        return "redirect:/admin/biblioteca";
    }

    // Formulário de editar livro
    @GetMapping("/livro/editar/{id}")
    public String editarLivroForm(@PathVariable int id, Model model) {
        Livro livro = repositorioLivro.findById(id).orElse(null);
        if (livro == null) {
            return "redirect:/admin/biblioteca";
        }
        model.addAttribute("livro", livro);
        return "admin-livro-form";
    }

    // Deletar livro
    @PostMapping("/livro/deletar/{id}")
    public String deletarLivro(@PathVariable int id, RedirectAttributes redirectAttributes) {
        repositorioLivro.deleteById(id);
        redirectAttributes.addFlashAttribute("sucesso", "Livro deletado com sucesso!");
        return "redirect:/admin/biblioteca";
    }

    // ==================== GERENCIAMENTO DE RESERVAS ====================

    // Listar todas as reservas
    @GetMapping("/reservas")
    public String listarReservas(@RequestParam(required = false) String status,
                                 @AuthenticationPrincipal UserDetails userDetails,
                                 Model model) {
        Usuario usuario = repositorioUsuario.findByUsername(userDetails.getUsername()).orElse(null);
        List<Reserva> reservas;

        if (status != null && !status.isEmpty()) {
            reservas = repositorioReserva.findByStatus(status.toUpperCase());
            model.addAttribute("statusFiltro", status);
        } else {
            reservas = repositorioReserva.findAllByOrderByDataReservaDesc();
        }

        model.addAttribute("usuario", usuario);
        model.addAttribute("reservas", reservas);
        model.addAttribute("totalPendentes", repositorioReserva.countPendentes());

        return "admin-reservas";
    }

    // Aprovar reserva
    @PostMapping("/reservas/aprovar/{id}")
    public String aprovarReserva(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Reserva reserva = repositorioReserva.findById(id).orElse(null);

        if (reserva == null) {
            redirectAttributes.addFlashAttribute("erro", "Reserva não encontrada.");
            return "redirect:/admin/reservas";
        }

        if (!reserva.isPendente()) {
            redirectAttributes.addFlashAttribute("erro", "Esta reserva não está pendente.");
            return "redirect:/admin/reservas";
        }

        Livro livro = reserva.getLivro();
        if (livro.getQuantidadeDisponivel() <= 0) {
            redirectAttributes.addFlashAttribute("erro", "Livro sem estoque disponível.");
            return "redirect:/admin/reservas";
        }

        // Aprova a reserva e decrementa estoque
        reserva.setStatus("APROVADA");
        reserva.setDataDevolucaoPrevista(LocalDateTime.now().plusDays(14));
        repositorioReserva.save(reserva);

        livro.setQuantidadeDisponivel(livro.getQuantidadeDisponivel() - 1);
        livro.setDisponivel(livro.getQuantidadeDisponivel() > 0);
        repositorioLivro.save(livro);

        redirectAttributes.addFlashAttribute("sucesso", "Reserva aprovada com sucesso!");
        return "redirect:/admin/reservas";
    }

    // Rejeitar reserva
    @PostMapping("/reservas/rejeitar/{id}")
    public String rejeitarReserva(@PathVariable Long id,
                                  @RequestParam(required = false) String observacao,
                                  RedirectAttributes redirectAttributes) {
        Reserva reserva = repositorioReserva.findById(id).orElse(null);

        if (reserva == null) {
            redirectAttributes.addFlashAttribute("erro", "Reserva não encontrada.");
            return "redirect:/admin/reservas";
        }

        if (!reserva.isPendente()) {
            redirectAttributes.addFlashAttribute("erro", "Esta reserva não está pendente.");
            return "redirect:/admin/reservas";
        }

        reserva.setStatus("REJEITADA");
        reserva.setObservacao(observacao);
        repositorioReserva.save(reserva);

        redirectAttributes.addFlashAttribute("sucesso", "Reserva rejeitada.");
        return "redirect:/admin/reservas";
    }

    // Registrar devolução
    @PostMapping("/reservas/devolver/{id}")
    public String registrarDevolucao(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Reserva reserva = repositorioReserva.findById(id).orElse(null);

        if (reserva == null) {
            redirectAttributes.addFlashAttribute("erro", "Reserva não encontrada.");
            return "redirect:/admin/reservas";
        }

        if (!reserva.isAprovada()) {
            redirectAttributes.addFlashAttribute("erro", "Esta reserva não está em empréstimo.");
            return "redirect:/admin/reservas";
        }

        // Registra devolução e incrementa estoque
        reserva.setStatus("DEVOLVIDA");
        reserva.setDataDevolucao(LocalDateTime.now());
        repositorioReserva.save(reserva);

        Livro livro = reserva.getLivro();
        livro.setQuantidadeDisponivel(livro.getQuantidadeDisponivel() + 1);
        livro.setDisponivel(true);
        repositorioLivro.save(livro);

        redirectAttributes.addFlashAttribute("sucesso", "Devolução registrada com sucesso!");
        return "redirect:/admin/reservas";
    }
}