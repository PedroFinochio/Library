package com.mcnz.spring.app.controller;

import com.mcnz.spring.app.repository.RepositorioUsuario;
import com.mcnz.spring.app.model.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {

    @Autowired
    private RepositorioUsuario repositorioUsuario;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "erro", required = false) String erro,
                            @RequestParam(value = "logout", required = false) String logout,
                            @RequestParam(value = "registro", required = false) String registro,
                            Model model) {
        if (erro != null) {
            model.addAttribute("erro", "Usuário ou senha inválidos!");
        }
        if (logout != null) {
            model.addAttribute("mensagem", "Logout realizado com sucesso!");
        }
        if (registro != null) {
            model.addAttribute("sucesso", "Cadastro realizado! Faça login.");
        }
        return "login";
    }

    @GetMapping("/registro")
    public String registroPage(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "registro";
    }

    @PostMapping("/registro")
    public String registrarUsuario(@ModelAttribute Usuario usuario, Model model) {
        // Validações
        if (usuario.getUsername() == null || usuario.getUsername().trim().isEmpty()) {
            model.addAttribute("erro", "Nome de usuário é obrigatório!");
            return "registro";
        }

        if (usuario.getEmail() == null || usuario.getEmail().trim().isEmpty()) {
            model.addAttribute("erro", "Email é obrigatório!");
            return "registro";
        }

        if (usuario.getPassword() == null || usuario.getPassword().trim().isEmpty()) {
            model.addAttribute("erro", "Senha é obrigatória!");
            return "registro";
        }

        if (repositorioUsuario.existsByUsername(usuario.getUsername())) {
            model.addAttribute("erro", "Nome de usuário já existe!");
            return "registro";
        }

        if (repositorioUsuario.existsByEmail(usuario.getEmail())) {
            model.addAttribute("erro", "Email já está cadastrado!");
            return "registro";
        }

        // Criptografa a senha e salva
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        usuario.setRole("USER");
        usuario.setAtivo(true);
        repositorioUsuario.save(usuario);

        return "redirect:/login?registro=sucesso";
    }

    @GetMapping("/home")
    public String homePage() {
        return "home";
    }
}