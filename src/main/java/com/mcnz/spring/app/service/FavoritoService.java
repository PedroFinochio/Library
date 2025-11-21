package com.mcnz.spring.app.service;

import com.mcnz.spring.app.model.Favorito;
import com.mcnz.spring.app.model.Livro;
import com.mcnz.spring.app.repository.RepositorioFavorito;
import com.mcnz.spring.app.repository.RepositorioLivro;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FavoritoService {

    @Autowired
    private RepositorioFavorito repositorioFavorito;

    @Autowired
    private RepositorioLivro repositorioLivro;

    @Transactional
    public void adicionarFavorito(Long usuarioId, int livroId) {
        // Verifica se já não está favoritado
        if (!repositorioFavorito.existsByUsuarioIdAndLivroId(usuarioId, livroId)) {
            Favorito favorito = new Favorito(usuarioId, livroId);
            repositorioFavorito.save(favorito);
        }
    }

    @Transactional
    public void removerFavorito(Long usuarioId, int livroId) {
        repositorioFavorito.deleteByUsuarioIdAndLivroId(usuarioId, livroId);
    }

    public List<Long> getFavoritosPorUsuario(Long usuarioId) {
        return repositorioFavorito.findLivroIdsByUsuarioId(usuarioId)
                .stream()
                .map(Integer::longValue)
                .collect(Collectors.toList());
    }

    public List<Livro> getLivrosFavoritos(Long usuarioId) {
        List<Integer> favoritoIds = repositorioFavorito.findLivroIdsByUsuarioId(usuarioId);
        return favoritoIds.stream()
                .map(id -> repositorioLivro.findById(id).orElse(null))
                .filter(livro -> livro != null)
                .collect(Collectors.toList());
    }

    public boolean isFavorito(Long usuarioId, int livroId) {
        return repositorioFavorito.existsByUsuarioIdAndLivroId(usuarioId, livroId);
    }

    public long contarFavoritosPorLivro(int livroId) {
        return repositorioFavorito.countByLivroId(livroId);
    }
}