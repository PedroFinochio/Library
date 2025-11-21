package com.mcnz.spring.app.config;

import com.mcnz.spring.app.model.Livro;
import com.mcnz.spring.app.model.Usuario;
import com.mcnz.spring.app.repository.RepositorioLivro;
import com.mcnz.spring.app.repository.RepositorioUsuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private RepositorioUsuario repositorioUsuario;

    @Autowired
    private RepositorioLivro repositorioLivro;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // Só inicializa se não houver usuários no banco
        if (repositorioUsuario.count() == 0) {
            System.out.println("=== Inicializando dados do sistema ===");
            inicializarUsuarios();
            inicializarLivros();
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
}