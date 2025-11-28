package com.mcnz.spring.app.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reservas")
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "livro_id", nullable = false)
    private Livro livro;

    @Column(name = "data_reserva", nullable = false)
    private LocalDateTime dataReserva;

    @Column(name = "data_devolucao_prevista")
    private LocalDateTime dataDevolucaoPrevista;

    @Column(name = "data_devolucao")
    private LocalDateTime dataDevolucao;

    @Column(nullable = false, length = 20)
    private String status;

    @Column(length = 500)
    private String observacao;

    // Construtor vazio
    public Reserva() {}

    // Construtor com usuário e livro
    public Reserva(Usuario usuario, Livro livro) {
        this.usuario = usuario;
        this.livro = livro;
    }

    // Callback para inicializar valores padrão antes de persistir
    @PrePersist
    protected void onCreate() {
        if (this.dataReserva == null) {
            this.dataReserva = LocalDateTime.now();
        }
        if (this.status == null) {
            this.status = "PENDENTE";
        }
    }

    // Métodos auxiliares para verificar status
    public boolean isPendente() {
        return "PENDENTE".equals(this.status);
    }

    public boolean isAprovada() {
        return "APROVADA".equals(this.status);
    }

    public boolean isRejeitada() {
        return "REJEITADA".equals(this.status);
    }

    public boolean isDevolvida() {
        return "DEVOLVIDA".equals(this.status);
    }

    public boolean isCancelada() {
        return "CANCELADA".equals(this.status);
    }

    public boolean isAtiva() {
        return isPendente() || isAprovada();
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Livro getLivro() {
        return livro;
    }

    public void setLivro(Livro livro) {
        this.livro = livro;
    }

    public LocalDateTime getDataReserva() {
        return dataReserva;
    }

    public void setDataReserva(LocalDateTime dataReserva) {
        this.dataReserva = dataReserva;
    }

    public LocalDateTime getDataDevolucaoPrevista() {
        return dataDevolucaoPrevista;
    }

    public void setDataDevolucaoPrevista(LocalDateTime dataDevolucaoPrevista) {
        this.dataDevolucaoPrevista = dataDevolucaoPrevista;
    }

    public LocalDateTime getDataDevolucao() {
        return dataDevolucao;
    }

    public void setDataDevolucao(LocalDateTime dataDevolucao) {
        this.dataDevolucao = dataDevolucao;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    @Override
    public String toString() {
        return "Reserva{" +
                "id=" + id +
                ", usuario=" + (usuario != null ? usuario.getUsername() : "null") +
                ", livro=" + (livro != null ? livro.getTitulo() : "null") +
                ", status='" + status + '\'' +
                ", dataReserva=" + dataReserva +
                '}';
    }
}