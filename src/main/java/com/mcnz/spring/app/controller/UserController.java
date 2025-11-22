package com.mcnz.spring.app.controller;

import com.mcnz.spring.app.model.Livro;
import com.mcnz.spring.app.model.Reserva;
import com.mcnz.spring.app.model.Usuario;
import com.mcnz.spring.app.repository.RepositorioLivro;
import com.mcnz.spring.app.repository.RepositorioReserva;
import com.mcnz.spring.app.repository.RepositorioUsuario;
import com.mcnz.spring.app.service.FavoritoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private RepositorioUsuario repositorioUsuario;

    @Autowired
    private RepositorioLivro repositorioLivro;

    @Autowired
    private RepositorioReserva repositorioReserva;

    @Autowired
    private FavoritoService favoritoService;

    // Home do usuário
    @GetMapping("/home")
    public String home(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        Usuario usuario = repositorioUsuario.findByUsername(userDetails.getUsername()).orElse(null);
        model.addAttribute("usuario", usuario);
        return "user-home";
    }

    // Explorar biblioteca (visualizar livros)
    @GetMapping("/biblioteca")
    public String biblioteca(@RequestParam(required = false) String busca,
                             @AuthenticationPrincipal UserDetails userDetails,
                             Model model) {
        Usuario usuario = repositorioUsuario.findByUsername(userDetails.getUsername()).orElse(null);
        List<Livro> livros;

        if (busca != null && !busca.trim().isEmpty()) {
            livros = repositorioLivro.findByTituloContaining(busca);
            model.addAttribute("busca", busca);
        } else {
            livros = repositorioLivro.findAll();
        }

        // IDs dos livros favoritados pelo usuário
        List<Integer> favoritosIds = favoritoService.getFavoritosPorUsuario(usuario.getId())
                .stream()
                .map(Long::intValue)
                .collect(Collectors.toList());

        model.addAttribute("livros", livros);
        model.addAttribute("usuario", usuario);
        model.addAttribute("favoritosIds", favoritosIds);

        return "user-biblioteca";
    }

    // Página de reservas - buscar livros para reservar
    @GetMapping("/reservas")
    public String reservas(@RequestParam(required = false) String busca,
                           @AuthenticationPrincipal UserDetails userDetails,
                           Model model) {
        Usuario usuario = repositorioUsuario.findByUsername(userDetails.getUsername()).orElse(null);
        List<Livro> livros;

        if (busca != null && !busca.trim().isEmpty()) {
            livros = repositorioLivro.findByTituloContaining(busca);
            model.addAttribute("busca", busca);
        } else {
            livros = repositorioLivro.findAll();
        }

        // Verifica quais livros o usuário já tem reserva ativa
        List<Integer> livrosReservadosIds = livros.stream()
                .filter(l -> repositorioReserva.existsReservaAtivaByUsuarioAndLivro(usuario, l))
                .map(Livro::getId)
                .collect(Collectors.toList());

        model.addAttribute("livros", livros);
        model.addAttribute("usuario", usuario);
        model.addAttribute("livrosReservadosIds", livrosReservadosIds);

        return "user-reservas";
    }

    // Solicitar reserva - APROVAÇÃO AUTOMÁTICA
    @PostMapping("/reservas/solicitar/{livroId}")
    public String solicitarReserva(@PathVariable int livroId,
                                   @AuthenticationPrincipal UserDetails userDetails,
                                   RedirectAttributes redirectAttributes) {
        Usuario usuario = repositorioUsuario.findByUsername(userDetails.getUsername()).orElse(null);
        Livro livro = repositorioLivro.findById(livroId).orElse(null);

        if (usuario == null || livro == null) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao processar reserva.");
            return "redirect:/user/reservas";
        }

        // Verificar se já existe reserva ativa
        if (repositorioReserva.existsReservaAtivaByUsuarioAndLivro(usuario, livro)) {
            redirectAttributes.addFlashAttribute("erro",
                    "Você já possui uma reserva ativa para este livro!");
            return "redirect:/user/reservas";
        }

        // Verificar disponibilidade
        if (livro.getQuantidadeDisponivel() <= 0) {
            redirectAttributes.addFlashAttribute("erro",
                    "Este livro não está disponível no momento.");
            return "redirect:/user/reservas";
        }

        // Criar reserva
        Reserva reserva = new Reserva(usuario, livro);

        // APROVAÇÃO AUTOMÁTICA - Como há disponibilidade, aprovar imediatamente
        reserva.setStatus("APROVADA");
        reserva.setObservacao("Reserva aprovada automaticamente");
        reserva.setDataDevolucaoPrevista(LocalDateTime.now().plusDays(14));

        // Decrementar quantidade disponível
        livro.setQuantidadeDisponivel(livro.getQuantidadeDisponivel() - 1);
        livro.setDisponivel(livro.getQuantidadeDisponivel() > 0);

        // Salvar
        repositorioLivro.save(livro);
        repositorioReserva.save(reserva);

        redirectAttributes.addFlashAttribute("sucesso",
                "✅ Reserva aprovada com sucesso! O livro está disponível para retirada. " +
                        "Data de devolução prevista: " + reserva.getDataDevolucaoPrevista().toLocalDate());

        return "redirect:/user/minhas-reservas";
    }

    // Minhas reservas
    @GetMapping("/minhas-reservas")
    public String minhasReservas(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        Usuario usuario = repositorioUsuario.findByUsername(userDetails.getUsername()).orElse(null);
        List<Reserva> reservas = repositorioReserva.findByUsuarioOrderByDataReservaDesc(usuario);

        model.addAttribute("usuario", usuario);
        model.addAttribute("reservas", reservas);

        return "user-minhas-reservas";
    }

    // Cancelar reserva
    @PostMapping("/reservas/cancelar/{reservaId}")
    public String cancelarReserva(@PathVariable Long reservaId,
                                  @AuthenticationPrincipal UserDetails userDetails,
                                  RedirectAttributes redirectAttributes) {
        Usuario usuario = repositorioUsuario.findByUsername(userDetails.getUsername()).orElse(null);
        Reserva reserva = repositorioReserva.findById(reservaId).orElse(null);

        if (reserva == null || !reserva.getUsuario().getId().equals(usuario.getId())) {
            redirectAttributes.addFlashAttribute("erro", "Reserva não encontrada.");
            return "redirect:/user/minhas-reservas";
        }

        // Só permite cancelar se estiver pendente ou aprovada
        if (!reserva.isPendente() && !reserva.isAprovada()) {
            redirectAttributes.addFlashAttribute("erro",
                    "Esta reserva não pode ser cancelada!");
            return "redirect:/user/minhas-reservas";
        }

        // Se estiver aprovada, devolver a quantidade ao estoque
        if (reserva.isAprovada()) {
            Livro livro = reserva.getLivro();
            livro.setQuantidadeDisponivel(livro.getQuantidadeDisponivel() + 1);
            livro.setDisponivel(true);
            repositorioLivro.save(livro);
        }

        // Cancelar reserva
        reserva.setStatus("CANCELADA");
        reserva.setObservacao("Cancelada pelo usuário");
        repositorioReserva.save(reserva);

        redirectAttributes.addFlashAttribute("sucesso", "Reserva cancelada com sucesso.");
        return "redirect:/user/minhas-reservas";
    }

    // ==================== FAVORITOS ====================

    // Página de favoritos
    @GetMapping("/favoritos")
    public String favoritos(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        Usuario usuario = repositorioUsuario.findByUsername(userDetails.getUsername()).orElse(null);
        List<Livro> livros = favoritoService.getLivrosFavoritos(usuario.getId());

        model.addAttribute("usuario", usuario);
        model.addAttribute("livros", livros);

        return "user-favoritos";
    }

    // Adicionar aos favoritos
    @PostMapping("/favoritos/adicionar/{livroId}")
    public String adicionarFavorito(@PathVariable int livroId,
                                    @AuthenticationPrincipal UserDetails userDetails,
                                    RedirectAttributes redirectAttributes) {
        Usuario usuario = repositorioUsuario.findByUsername(userDetails.getUsername()).orElse(null);

        if (usuario == null) {
            redirectAttributes.addFlashAttribute("erro", "Usuário não encontrado.");
            return "redirect:/user/biblioteca";
        }

        favoritoService.adicionarFavorito(usuario.getId(), livroId);
        redirectAttributes.addFlashAttribute("sucesso", "Livro adicionado aos favoritos!");

        return "redirect:/user/biblioteca";
    }

    // Remover dos favoritos
    @PostMapping("/favoritos/remover/{livroId}")
    public String removerFavorito(@PathVariable int livroId,
                                  @AuthenticationPrincipal UserDetails userDetails,
                                  RedirectAttributes redirectAttributes) {
        Usuario usuario = repositorioUsuario.findByUsername(userDetails.getUsername()).orElse(null);

        if (usuario == null) {
            redirectAttributes.addFlashAttribute("erro", "Usuário não encontrado.");
            return "redirect:/user/favoritos";
        }

        favoritoService.removerFavorito(usuario.getId(), livroId);
        redirectAttributes.addFlashAttribute("sucesso", "Livro removido dos favoritos.");

        return "redirect:/user/favoritos";
    }
}