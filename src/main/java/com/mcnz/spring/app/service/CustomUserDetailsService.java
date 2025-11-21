package com.mcnz.spring.app.service;

import com.mcnz.spring.app.model.Usuario;
import com.mcnz.spring.app.repository.RepositorioUsuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private RepositorioUsuario repositorioUsuario;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = repositorioUsuario.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + username));

        if (!usuario.isAtivo()) {
            throw new UsernameNotFoundException("Usuário está inativo: " + username);
        }

        // Adiciona o prefixo ROLE_ se não existir
        String role = usuario.getRole();
        if (!role.startsWith("ROLE_")) {
            role = "ROLE_" + role;
        }

        return new org.springframework.security.core.userdetails.User(
                usuario.getUsername(),
                usuario.getPassword(),
                usuario.isAtivo(),
                true,
                true,
                true,
                Collections.singletonList(new SimpleGrantedAuthority(role))
        );
    }
}