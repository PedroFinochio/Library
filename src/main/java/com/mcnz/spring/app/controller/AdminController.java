package com.mcnz.spring.app.controller;

import com.mcnz.spring.app.model.Livro;
import com.mcnz.spring.app.repository.RepositorioLivro;
import com.mcnz.spring.app.repository.RepositorioUsuario;
import com.mcnz.spring.app.model.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private RepositorioLivro repositorioLivro;

    @Autowired
    private RepositorioUsuario repositorioUsuario;

    // Dashboard do Admin/Bibliotecário
    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        Usuario usuario = repositorioUsuario.findByUsername(userDetails.getUsername()).orElse(null);
        List<Livro> livros = repositorioLivro.findAll();

        model.addAttribute("usuario", usuario);
        model.addAttribute("totalLivros", livros.size());
        model.addAttribute("livros", livros);

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
}