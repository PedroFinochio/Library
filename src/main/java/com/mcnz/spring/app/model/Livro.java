package com.mcnz.spring.app.model;

import jakarta.persistence.*;

@Entity
@Table(name = "livros")
public class Livro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String titulo;

    @Column(nullable = false)
    private String autor;

    @Column(nullable = false)
    private Double preco;

    @Column(name = "image_url")
    private String imageUrl;

    // Quantidade total no acervo
    @Column(nullable = false)
    private Integer quantidade = 0;

    // Quantidade disponível para empréstimo
    @Column(name = "quantidade_disponivel", nullable = false)
    private Integer quantidadeDisponivel = 0;

    // Status de disponibilidade
    @Column(nullable = false)
    private Boolean disponivel = true;

    public Livro() {}

    public Livro(String titulo, String autor, Double preco) {
        this.titulo = titulo;
        this.autor = autor;
        this.preco = preco;
        this.quantidade = 1;
        this.quantidadeDisponivel = 1;
        this.disponivel = true;
    }

    // Getters e Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public Double getPreco() {
        return preco;
    }

    public void setPreco(Double preco) {
        this.preco = preco;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public Integer getQuantidadeDisponivel() {
        return quantidadeDisponivel;
    }

    public void setQuantidadeDisponivel(Integer quantidadeDisponivel) {
        this.quantidadeDisponivel = quantidadeDisponivel;
    }

    public Boolean getDisponivel() {
        return disponivel;
    }

    public void setDisponivel(Boolean disponivel) {
        this.disponivel = disponivel;
    }

    // Método auxiliar para verificar se há estoque
    public boolean temEstoque() {
        return quantidadeDisponivel != null && quantidadeDisponivel > 0;
    }

    @Override
    public String toString() {
        return "Livro{" +
                "id=" + id +
                ", titulo='" + titulo + '\'' +
                ", autor='" + autor + '\'' +
                ", preco=" + preco +
                ", quantidade=" + quantidade +
                ", quantidadeDisponivel=" + quantidadeDisponivel +
                ", disponivel=" + disponivel +
                '}';
    }
}