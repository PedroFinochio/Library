package com.mcnz.spring.app.config;

import com.mcnz.spring.app.model.Livro;
import com.mcnz.spring.app.model.Reserva;
import com.mcnz.spring.app.model.Usuario;
import com.mcnz.spring.app.repository.RepositorioLivro;
import com.mcnz.spring.app.repository.RepositorioReserva;
import com.mcnz.spring.app.repository.RepositorioUsuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private RepositorioUsuario repositorioUsuario;

    @Autowired
    private RepositorioLivro repositorioLivro;

    @Autowired
    private RepositorioReserva repositorioReserva;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // Só inicializa se não houver usuários no banco
        if (repositorioUsuario.count() == 0) {
            System.out.println("=== Inicializando dados do sistema ===");
            inicializarUsuarios();
            inicializarLivros();
            inicializarReservas(); // ← NOVO: Cria reservas de teste
            System.out.println("=== Dados inicializados com sucesso ===");
        } else {
            System.out.println("=== Banco já possui dados, pulando inicialização ===");
        }
    }

    private void inicializarUsuarios() {
        // Senha padrão para todos: senha123
        String senhaEncriptada = passwordEncoder.encode("senha123");

        // Administradores
        Usuario admin = new Usuario("admin", senhaEncriptada, "admin@biblioteca.com");
        admin.setRole("ADMIN");
        repositorioUsuario.save(admin);

        Usuario carlos = new Usuario("carlos.admin", senhaEncriptada, "carlos@biblioteca.com");
        carlos.setRole("ADMIN");
        repositorioUsuario.save(carlos);

        // Bibliotecários
        Usuario maria = new Usuario("maria.biblio", senhaEncriptada, "maria@biblioteca.com");
        maria.setRole("BIBLIOTECARIO");
        repositorioUsuario.save(maria);

        Usuario joao = new Usuario("joao.biblio", senhaEncriptada, "joao@biblioteca.com");
        joao.setRole("BIBLIOTECARIO");
        repositorioUsuario.save(joao);

        // Usuários comuns
        String[][] usuarios = {
                {"ana.silva", "ana.silva@email.com"},
                {"pedro.santos", "pedro.santos@email.com"},
                {"julia.costa", "julia.costa@email.com"},
                {"lucas.oliveira", "lucas.oliveira@email.com"},
                {"fernanda.lima", "fernanda.lima@email.com"},
                {"roberto.alves", "roberto.alves@email.com"}
        };

        for (String[] u : usuarios) {
            Usuario user = new Usuario(u[0], senhaEncriptada, u[1]);
            user.setRole("USER");
            repositorioUsuario.save(user);
        }

        System.out.println("Usuários criados: " + repositorioUsuario.count());
    }

    private void inicializarLivros() {
        // titulo, autor, preco, quantidadeTotal, quantidadeDisponivel
        Object[][] livrosData = {
                // Livros com boa disponibilidade
                {"Dom Casmurro", "Machado de Assis", 35.90, 5, 4},
                {"1984", "George Orwell", 45.90, 6, 5},
                {"Harry Potter e a Pedra Filosofal", "J.K. Rowling", 39.90, 8, 7},
                {"O Código Da Vinci", "Dan Brown", 44.90, 5, 4},
                {"Sapiens", "Yuval Noah Harari", 64.90, 4, 3},
                {"Orgulho e Preconceito", "Jane Austen", 32.90, 4, 3},

                // Livros com disponibilidade média
                {"Grande Sertão: Veredas", "Guimarães Rosa", 59.90, 3, 2},
                {"Capitães da Areia", "Jorge Amado", 42.50, 4, 2},
                {"O Senhor dos Anéis", "J.R.R. Tolkien", 69.90, 4, 2},
                {"O Nome do Vento", "Patrick Rothfuss", 54.90, 3, 2},
                {"Assassinato no Expresso do Oriente", "Agatha Christie", 34.90, 3, 2},
                {"O Poder do Hábito", "Charles Duhigg", 49.90, 3, 1},
                {"Código Limpo", "Robert C. Martin", 89.90, 3, 1},

                // Livros com baixa disponibilidade (última unidade)
                {"Fahrenheit 451", "Ray Bradbury", 38.90, 2, 1},
                {"Neuromancer", "William Gibson", 52.90, 2, 1},
                {"O Morro dos Ventos Uivantes", "Emily Brontë", 36.50, 2, 1},
                {"A Metamorfose", "Franz Kafka", 29.90, 2, 1},
                {"Crime e Castigo", "Fiódor Dostoiévski", 58.90, 2, 1},

                // Livros indisponíveis (todos emprestados)
                {"A Ilha do Tesouro", "Robert Louis Stevenson", 33.90, 3, 0},
                {"Design Patterns", "Erich Gamma", 125.00, 2, 0},
                {"O Hobbit", "J.R.R. Tolkien", 42.90, 3, 0},
                {"Cem Anos de Solidão", "Gabriel García Márquez", 49.90, 2, 0},
                {"A Revolução dos Bichos", "George Orwell", 34.90, 2, 0}
        };

        for (Object[] l : livrosData) {
            Livro livro = new Livro();
            livro.setTitulo((String) l[0]);
            livro.setAutor((String) l[1]);
            livro.setPreco((Double) l[2]);
            int qtdTotal = (Integer) l[3];
            int qtdDisponivel = (Integer) l[4];

            livro.setQuantidade(qtdTotal);
            livro.setQuantidadeDisponivel(qtdDisponivel);
            livro.setDisponivel(qtdDisponivel > 0);

            repositorioLivro.save(livro);

            // Log para debug
            System.out.println(String.format("Livro: %s - Total: %d, Disponível: %d, Status: %s",
                    livro.getTitulo(), qtdTotal, qtdDisponivel, (qtdDisponivel > 0 ? "Disponível" : "Indisponível")));
        }

        System.out.println("Livros criados: " + repositorioLivro.count());
        System.out.println("Livros disponíveis para reserva: " +
                repositorioLivro.findAll().stream().filter(l -> l.getQuantidadeDisponivel() > 0).count());
        System.out.println("Livros indisponíveis: " +
                repositorioLivro.findAll().stream().filter(l -> l.getQuantidadeDisponivel() == 0).count());
    }

    private void inicializarReservas() {
        System.out.println("\n=== Criando reservas de teste ===");

        List<Usuario> usuarios = repositorioUsuario.findAll();
        List<Livro> livros = repositorioLivro.findAll();

        if (usuarios.size() < 6 || livros.isEmpty()) {
            System.out.println("⚠️  Não há usuários ou livros suficientes para criar reservas");
            return;
        }

        // Pegar usuários comuns (índices 4-9 são os users criados)
        Usuario ana = usuarios.get(4);      // ana.silva
        Usuario pedro = usuarios.get(5);    // pedro.santos
        Usuario julia = usuarios.get(6);    // julia.costa
        Usuario lucas = usuarios.get(7);    // lucas.oliveira
        Usuario fernanda = usuarios.get(8); // fernanda.lima
        Usuario roberto = usuarios.get(9);  // roberto.alves

        // ============ RESERVAS PENDENTES (aguardando aprovação) ============
        System.out.println("Criando reservas PENDENTES...");

        Reserva r1 = new Reserva(ana, livros.get(0)); // Dom Casmurro
        r1.setStatus("PENDENTE");
        r1.setDataReserva(LocalDateTime.now().minusHours(2));
        repositorioReserva.save(r1);

        Reserva r2 = new Reserva(pedro, livros.get(1)); // 1984
        r2.setStatus("PENDENTE");
        r2.setDataReserva(LocalDateTime.now().minusHours(5));
        repositorioReserva.save(r2);

        Reserva r3 = new Reserva(julia, livros.get(4)); // Sapiens
        r3.setStatus("PENDENTE");
        r3.setDataReserva(LocalDateTime.now().minusDays(1));
        repositorioReserva.save(r3);

        // ============ RESERVAS APROVADAS (empréstimos ativos) ============
        System.out.println("Criando reservas APROVADAS (empréstimos ativos)...");

        Reserva r4 = new Reserva(lucas, livros.get(2)); // Harry Potter
        r4.setStatus("APROVADA");
        r4.setDataReserva(LocalDateTime.now().minusDays(3));
        r4.setDataDevolucaoPrevista(LocalDateTime.now().plusDays(11));
        repositorioReserva.save(r4);

        Reserva r5 = new Reserva(fernanda, livros.get(3)); // Código Da Vinci
        r5.setStatus("APROVADA");
        r5.setDataReserva(LocalDateTime.now().minusDays(5));
        r5.setDataDevolucaoPrevista(LocalDateTime.now().plusDays(9));
        repositorioReserva.save(r5);

        Reserva r6 = new Reserva(roberto, livros.get(8)); // Senhor dos Anéis
        r6.setStatus("APROVADA");
        r6.setDataReserva(LocalDateTime.now().minusDays(7));
        r6.setDataDevolucaoPrevista(LocalDateTime.now().plusDays(7));
        repositorioReserva.save(r6);

        Reserva r7 = new Reserva(ana, livros.get(9)); // O Nome do Vento
        r7.setStatus("APROVADA");
        r7.setDataReserva(LocalDateTime.now().minusDays(10));
        r7.setDataDevolucaoPrevista(LocalDateTime.now().plusDays(4));
        repositorioReserva.save(r7);

        // ============ RESERVAS DEVOLVIDAS (histórico) ============
        System.out.println("Criando reservas DEVOLVIDAS...");

        Reserva r8 = new Reserva(pedro, livros.get(5)); // Orgulho e Preconceito
        r8.setStatus("DEVOLVIDA");
        r8.setDataReserva(LocalDateTime.now().minusDays(30));
        r8.setDataDevolucaoPrevista(LocalDateTime.now().minusDays(16));
        r8.setDataDevolucao(LocalDateTime.now().minusDays(15));
        repositorioReserva.save(r8);

        Reserva r9 = new Reserva(julia, livros.get(6)); // Grande Sertão
        r9.setStatus("DEVOLVIDA");
        r9.setDataReserva(LocalDateTime.now().minusDays(25));
        r9.setDataDevolucaoPrevista(LocalDateTime.now().minusDays(11));
        r9.setDataDevolucao(LocalDateTime.now().minusDays(10));
        repositorioReserva.save(r9);

        Reserva r10 = new Reserva(lucas, livros.get(7)); // Capitães da Areia
        r10.setStatus("DEVOLVIDA");
        r10.setDataReserva(LocalDateTime.now().minusDays(20));
        r10.setDataDevolucaoPrevista(LocalDateTime.now().minusDays(6));
        r10.setDataDevolucao(LocalDateTime.now().minusDays(5));
        repositorioReserva.save(r10);

        Reserva r11 = new Reserva(fernanda, livros.get(10)); // Assassinato no Expresso
        r11.setStatus("DEVOLVIDA");
        r11.setDataReserva(LocalDateTime.now().minusDays(18));
        r11.setDataDevolucaoPrevista(LocalDateTime.now().minusDays(4));
        r11.setDataDevolucao(LocalDateTime.now().minusDays(3));
        repositorioReserva.save(r11);

        Reserva r12 = new Reserva(roberto, livros.get(11)); // O Poder do Hábito
        r12.setStatus("DEVOLVIDA");
        r12.setDataReserva(LocalDateTime.now().minusDays(15));
        r12.setDataDevolucaoPrevista(LocalDateTime.now().minusDays(1));
        r12.setDataDevolucao(LocalDateTime.now().minusHours(12));
        repositorioReserva.save(r12);

        // ============ RESERVAS REJEITADAS ============
        System.out.println("Criando reservas REJEITADAS...");

        Reserva r13 = new Reserva(ana, livros.get(18)); // A Ilha do Tesouro (indisponível)
        r13.setStatus("REJEITADA");
        r13.setDataReserva(LocalDateTime.now().minusDays(8));
        r13.setObservacao("Livro sem estoque disponível no momento");
        repositorioReserva.save(r13);

        Reserva r14 = new Reserva(pedro, livros.get(19)); // Design Patterns (indisponível)
        r14.setStatus("REJEITADA");
        r14.setDataReserva(LocalDateTime.now().minusDays(6));
        r14.setObservacao("Todos os exemplares emprestados");
        repositorioReserva.save(r14);

        // ============ RESERVAS CANCELADAS ============
        System.out.println("Criando reservas CANCELADAS...");

        Reserva r15 = new Reserva(julia, livros.get(12)); // Código Limpo
        r15.setStatus("CANCELADA");
        r15.setDataReserva(LocalDateTime.now().minusDays(4));
        r15.setObservacao("Usuário cancelou a reserva");
        repositorioReserva.save(r15);

        Reserva r16 = new Reserva(lucas, livros.get(13)); // Fahrenheit 451
        r16.setStatus("CANCELADA");
        r16.setDataReserva(LocalDateTime.now().minusDays(2));
        r16.setObservacao("Cancelada pelo sistema - não retirado no prazo");
        repositorioReserva.save(r16);

        // ============ MAIS RESERVAS PARA ANÁLISE ESTATÍSTICA ============
        System.out.println("Criando reservas adicionais para estatísticas...");

        // Ana Silva - usuário mais ativo (total: 4 reservas)
        Reserva r17 = new Reserva(ana, livros.get(14)); // Neuromancer
        r17.setStatus("DEVOLVIDA");
        r17.setDataReserva(LocalDateTime.now().minusDays(45));
        r17.setDataDevolucaoPrevista(LocalDateTime.now().minusDays(31));
        r17.setDataDevolucao(LocalDateTime.now().minusDays(30));
        repositorioReserva.save(r17);

        // Pedro Santos - 3 reservas totais
        Reserva r18 = new Reserva(pedro, livros.get(15)); // Morro dos Ventos Uivantes
        r18.setStatus("DEVOLVIDA");
        r18.setDataReserva(LocalDateTime.now().minusDays(40));
        r18.setDataDevolucaoPrevista(LocalDateTime.now().minusDays(26));
        r18.setDataDevolucao(LocalDateTime.now().minusDays(25));
        repositorioReserva.save(r18);

        // Júlia Costa - 3 reservas totais
        Reserva r19 = new Reserva(julia, livros.get(16)); // A Metamorfose
        r19.setStatus("DEVOLVIDA");
        r19.setDataReserva(LocalDateTime.now().minusDays(35));
        r19.setDataDevolucaoPrevista(LocalDateTime.now().minusDays(21));
        r19.setDataDevolucao(LocalDateTime.now().minusDays(20));
        repositorioReserva.save(r19);

        // Lucas Oliveira - 3 reservas totais
        Reserva r20 = new Reserva(lucas, livros.get(17)); // Crime e Castigo
        r20.setStatus("DEVOLVIDA");
        r20.setDataReserva(LocalDateTime.now().minusDays(50));
        r20.setDataDevolucaoPrevista(LocalDateTime.now().minusDays(36));
        r20.setDataDevolucao(LocalDateTime.now().minusDays(35));
        repositorioReserva.save(r20);

        // Estatísticas finais
        long total = repositorioReserva.count();
        long pendentes = repositorioReserva.countPendentes();
        long aprovadas = repositorioReserva.countAprovadas();

        System.out.println("\n✅ Reservas criadas com sucesso!");
        System.out.println("   📊 Total de reservas: " + total);
        System.out.println("   ⏳ Pendentes: " + pendentes);
        System.out.println("   ✅ Aprovadas (empréstimos ativos): " + aprovadas);
        System.out.println("   📚 Devolvidas: " + repositorioReserva.findByStatus("DEVOLVIDA").size());
        System.out.println("   ❌ Rejeitadas: " + repositorioReserva.findByStatus("REJEITADA").size());
        System.out.println("   🚫 Canceladas: " + repositorioReserva.findByStatus("CANCELADA").size());
    }
}