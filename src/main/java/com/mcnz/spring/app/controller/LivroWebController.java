package com.mcnz.spring.app.controller;

import com.mcnz.spring.app.model.Livro;
import com.mcnz.spring.app.model.Usuario;
import com.mcnz.spring.app.repository.RepositorioLivro;
import com.mcnz.spring.app.repository.RepositorioUsuario;
import com.mcnz.spring.app.service.FavoritoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/biblioteca")
public class LivroWebController {

    @Autowired
    private RepositorioLivro repositorioLivro;

    @Autowired
    private RepositorioUsuario repositorioUsuario;

    @Autowired
    private FavoritoService favoritoService;

    // Página principal da biblioteca
    @GetMapping
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
        List<Long> favoritosIds = favoritoService.getFavoritosPorUsuario(usuario.getId());

        model.addAttribute("livros", livros);
        model.addAttribute("favoritosIds", favoritosIds);
        model.addAttribute("usuario", usuario);

        return "biblioteca";
    }

    // Página de favoritos
    @GetMapping("/favoritos")
    public String favoritos(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        Usuario usuario = repositorioUsuario.findByUsername(userDetails.getUsername()).orElse(null);
        List<Livro> favoritos = favoritoService.getLivrosFavoritos(usuario.getId());

        model.addAttribute("livros", favoritos);
        model.addAttribute("usuario", usuario);
        model.addAttribute("isFavoritosPage", true);

        return "favoritos";
    }

    // Adicionar aos favoritos
    @PostMapping("/favoritos/adicionar/{livroId}")
    public String adicionarFavorito(@PathVariable int livroId,
                                    @AuthenticationPrincipal UserDetails userDetails,
                                    RedirectAttributes redirectAttributes) {
        Usuario usuario = repositorioUsuario.findByUsername(userDetails.getUsername()).orElse(null);
        favoritoService.adicionarFavorito(usuario.getId(), livroId);

        redirectAttributes.addFlashAttribute("sucesso", "Livro adicionado aos favoritos!");
        return "redirect:/biblioteca";
    }

    // Remover dos favoritos
    @PostMapping("/favoritos/remover/{livroId}")
    public String removerFavorito(@PathVariable int livroId,
                                  @AuthenticationPrincipal UserDetails userDetails,
                                  RedirectAttributes redirectAttributes) {
        Usuario usuario = repositorioUsuario.findByUsername(userDetails.getUsername()).orElse(null);
        favoritoService.removerFavorito(usuario.getId(), livroId);

        redirectAttributes.addFlashAttribute("sucesso", "Livro removido dos favoritos!");
        return "redirect:/biblioteca";
    }

    // Página de adicionar livro
    @GetMapping("/novo")
    public String novoLivroForm(Model model) {
        model.addAttribute("livro", new Livro());
        return "livro-form";
    }

    // Salvar novo livro
    @PostMapping("/salvar")
    public String salvarLivro(@ModelAttribute Livro livro, RedirectAttributes redirectAttributes) {
        repositorioLivro.save(livro);
        redirectAttributes.addFlashAttribute("sucesso", "Livro salvo com sucesso!");
        return "redirect:/biblioteca";
    }

    // Página de editar livro
    @GetMapping("/editar/{id}")
    public String editarLivroForm(@PathVariable int id, Model model) {
        Livro livro = repositorioLivro.findById(id).orElse(null);
        if (livro == null) {
            return "redirect:/biblioteca";
        }
        model.addAttribute("livro", livro);
        return "livro-form";
    }

    // Deletar livro
    @PostMapping("/deletar/{id}")
    public String deletarLivro(@PathVariable int id, RedirectAttributes redirectAttributes) {
        repositorioLivro.deleteById(id);
        redirectAttributes.addFlashAttribute("sucesso", "Livro deletado com sucesso!");
        return "redirect:/biblioteca";
    }
}