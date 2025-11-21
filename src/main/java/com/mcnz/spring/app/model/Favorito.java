package com.mcnz.spring.app.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "favoritos")
public class Favorito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    @Column(name = "livro_id", nullable = false)
    private Integer livroId;

    @Column(name = "data_favoritado")
    private LocalDateTime dataFavoritado;

    // Construtor padrão
    public Favorito() {
        this.dataFavoritado = LocalDateTime.now();
    }

    // Construtor com parâmetros
    public Favorito(Long usuarioId, Integer livroId) {
        this.usuarioId = usuarioId;
        this.livroId = livroId;
        this.dataFavoritado = LocalDateTime.now();
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public Integer getLivroId() {
        return livroId;
    }

    public void setLivroId(Integer livroId) {
        this.livroId = livroId;
    }

    public LocalDateTime getDataFavoritado() {
        return dataFavoritado;
    }

    public void setDataFavoritado(LocalDateTime dataFavoritado) {
        this.dataFavoritado = dataFavoritado;
    }

    @Override
    public String toString() {
        return "Favorito{" +
                "id=" + id +
                ", usuarioId=" + usuarioId +
                ", livroId=" + livroId +
                ", dataFavoritado=" + dataFavoritado +
                '}';
    }
}