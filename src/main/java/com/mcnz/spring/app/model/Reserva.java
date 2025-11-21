package com.mcnz.spring.app.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reservas")
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "livro_id", nullable = false)
    private Livro livro;

    @Column(name = "data_reserva", nullable = false)
    private LocalDateTime dataReserva = LocalDateTime.now();

    @Column(name = "data_devolucao_prevista")
    private LocalDateTime dataDevolucaoPrevista;

    @Column(name = "data_devolucao")
    private LocalDateTime dataDevolucao;

    // Status: PENDENTE, APROVADA, REJEITADA, DEVOLVIDA, CANCELADA
    @Column(nullable = false)
    private String status = "PENDENTE";

    @Column(length = 500)
    private String observacao;

    public Reserva() {}

    public Reserva(Usuario usuario, Livro livro) {
        this.usuario = usuario;
        this.livro = livro;
        this.dataReserva = LocalDateTime.now();
        this.dataDevolucaoPrevista = LocalDateTime.now().plusDays(14); // 14 dias padrão
        this.status = "PENDENTE";
    }

    // Métodos auxiliares
    public boolean isPendente() { return "PENDENTE".equals(status); }
    public boolean isAprovada() { return "APROVADA".equals(status); }
    public boolean isRejeitada() { return "REJEITADA".equals(status); }
    public boolean isDevolvida() { return "DEVOLVIDA".equals(status); }
    public boolean isCancelada() { return "CANCELADA".equals(status); }
    public boolean isAtiva() { return isPendente() || isAprovada(); }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public Livro getLivro() { return livro; }
    public void setLivro(Livro livro) { this.livro = livro; }

    public LocalDateTime getDataReserva() { return dataReserva; }
    public void setDataReserva(LocalDateTime dataReserva) { this.dataReserva = dataReserva; }

    public LocalDateTime getDataDevolucaoPrevista() { return dataDevolucaoPrevista; }
    public void setDataDevolucaoPrevista(LocalDateTime dataDevolucaoPrevista) { this.dataDevolucaoPrevista = dataDevolucaoPrevista; }

    public LocalDateTime getDataDevolucao() { return dataDevolucao; }
    public void setDataDevolucao(LocalDateTime dataDevolucao) { this.dataDevolucao = dataDevolucao; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getObservacao() { return observacao; }
    public void setObservacao(String observacao) { this.observacao = observacao; }
}