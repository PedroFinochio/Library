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
        // titulo, autor, preco, categoria, quantidade
        Object[][] livrosData = {
                {"Dom Casmurro", "Machado de Assis", 35.90, "Literatura Brasileira", 5},
                {"Grande Sertão: Veredas", "Guimarães Rosa", 59.90, "Literatura Brasileira", 3},
                {"Capitães da Areia", "Jorge Amado", 42.50, "Literatura Brasileira", 4},
                {"1984", "George Orwell", 45.90, "Ficção Científica", 6},
                {"Fahrenheit 451", "Ray Bradbury", 38.90, "Ficção Científica", 2},
                {"Neuromancer", "William Gibson", 52.90, "Ficção Científica", 2},
                {"O Senhor dos Anéis", "J.R.R. Tolkien", 69.90, "Fantasia", 4},
                {"Harry Potter e a Pedra Filosofal", "J.K. Rowling", 39.90, "Fantasia", 8},
                {"O Nome do Vento", "Patrick Rothfuss", 54.90, "Fantasia", 3},
                {"Orgulho e Preconceito", "Jane Austen", 32.90, "Romance", 4},
                {"O Morro dos Ventos Uivantes", "Emily Brontë", 36.50, "Romance", 2},
                {"O Código Da Vinci", "Dan Brown", 44.90, "Suspense", 5},
                {"Assassinato no Expresso do Oriente", "Agatha Christie", 34.90, "Mistério", 3},
                {"Sapiens", "Yuval Noah Harari", 64.90, "Não-Ficção", 4},
                {"O Poder do Hábito", "Charles Duhigg", 49.90, "Autoajuda", 3},
                {"A Metamorfose", "Franz Kafka", 29.90, "Clássicos", 2},
                {"Crime e Castigo", "Fiódor Dostoiévski", 58.90, "Clássicos", 2},
                {"Código Limpo", "Robert C. Martin", 89.90, "Tecnologia", 3},
                {"Design Patterns", "Erich Gamma", 125.00, "Tecnologia", 1},
                {"A Ilha do Tesouro", "Robert Louis Stevenson", 33.90, "Aventura", 0} // Sem estoque para teste
        };

        for (Object[] l : livrosData) {
            Livro livro = new Livro();
            livro.setTitulo((String) l[0]);
            livro.setAutor((String) l[1]);
            livro.setPreco((Double) l[2]);
            livro.setCategoria((String) l[3]);
            int qtd = (Integer) l[4];
            livro.setQuantidade(qtd);
            livro.setQuantidadeDisponivel(qtd);
            livro.setDisponivel(qtd > 0);
            repositorioLivro.save(livro);
        }

        System.out.println("Livros criados: " + repositorioLivro.count());
    }
}