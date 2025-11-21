package com.mcnz.spring.app.controller;

import com.mcnz.spring.app.model.Livro;
import com.mcnz.spring.app.repository.RepositorioLivro;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class LivroController {

    @Autowired
    RepositorioLivro repositorioLivro;

    @PostMapping("/livros")
    public Livro criarLivro(@RequestBody Livro livro) {
        Livro livroSalvo = repositorioLivro.save(livro);
        return livroSalvo;
    }

    @GetMapping("/livros")
    public List<Livro> retorneTodosLivros(){
        return repositorioLivro.findAll();
    }

    @PutMapping("/livros/{id}")
    public Livro atualizarLivro(@PathVariable int id, @RequestBody Livro livro){
        livro.setId(id);
        return repositorioLivro.save(livro);
    }

    @DeleteMapping("/livros/{id}")
    public ResponseEntity<?> removerLivro(@PathVariable int id) {
        if (repositorioLivro.existsById(id)) {
            repositorioLivro.deleteById(id);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}
