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
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

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
    @Transactional
    public void run(String... args) {
        // Só inicializa se não houver usuários no banco
        if (repositorioUsuario.count() == 0) {
            System.out.println("\n========================================");
            System.out.println("📄 INICIALIZANDO DADOS DO SISTEMA");
            System.out.println("========================================\n");

            try {
                inicializarUsuarios();
                inicializarLivros();
                inicializarReservas();

                System.out.println("\n========================================");
                System.out.println("✅ DADOS INICIALIZADOS COM SUCESSO!");
                System.out.println("========================================");

                imprimirEstatisticas();
            } catch (Exception e) {
                System.err.println("❌ ERRO ao inicializar dados: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("\n========================================");
            System.out.println("ℹ️  Banco já possui dados");
            System.out.println("========================================");
            imprimirEstatisticas();
        }
    }

    private void inicializarUsuarios() {
        System.out.println("👥 Criando usuários...");

        String senhaEncriptada = passwordEncoder.encode("senha123");

        // Admin
        Usuario admin = new Usuario("admin", senhaEncriptada, "admin@biblioteca.com");
        admin.setRole("ADMIN");
        admin.setAtivo(true);
        repositorioUsuario.save(admin);

        // Bibliotecários
        Usuario maria = new Usuario("maria.biblio", senhaEncriptada, "maria@biblioteca.com");
        maria.setRole("BIBLIOTECARIO");
        maria.setAtivo(true);
        repositorioUsuario.save(maria);

        Usuario joao = new Usuario("joao.biblio", senhaEncriptada, "joao@biblioteca.com");
        joao.setRole("BIBLIOTECARIO");
        joao.setAtivo(true);
        repositorioUsuario.save(joao);

        // Usuários comuns
        String[][] usuarios = {
                {"ana.silva", "ana.silva@email.com"},
                {"pedro.santos", "pedro.santos@email.com"},
                {"julia.costa", "julia.costa@email.com"},
                {"lucas.oliveira", "lucas.oliveira@email.com"},
                {"fernanda.lima", "fernanda.lima@email.com"},
                {"roberto.alves", "roberto.alves@email.com"},
                {"carlos.mendes", "carlos.mendes@email.com"}
        };

        for (String[] u : usuarios) {
            Usuario user = new Usuario(u[0], senhaEncriptada, u[1]);
            user.setRole("USER");
            user.setAtivo(true);
            repositorioUsuario.save(user);
        }

        System.out.println("   ✅ " + repositorioUsuario.count() + " usuários criados");
    }

    private void inicializarLivros() {
        System.out.println("📚 Criando livros...");

        // Array com dados dos livros: título, autor, preço, qtdTotal, qtdDisponível, imagemUrl
        Object[][] livrosData = {
                // ========== MACHADO DE ASSIS ==========
                {"Dom Casmurro", "Machado de Assis", 5.00, 5, 4, "/images/dom_casmurro.jpg"},
                {"Memórias Póstumas de Brás Cubas", "Machado de Assis", 5.00, 4, 3, "/images/memorias_postumas.jpg"},
                {"Quincas Borba", "Machado de Assis", 5.00, 3, 2, "/images/quincas_borba.jpg"},
                {"Esaú e Jacó", "Machado de Assis", 4.50, 2, 1, "/images/esau_e_jaco.jpg"},
                {"O Alienista", "Machado de Assis", 3.00, 4, 0, "/images/o_alienista.jpg"},

                // ========== GEORGE ORWELL ==========
                {"1984", "George Orwell", 6.00, 6, 5, "/images/1984.jpg"},
                {"A Revolução dos Bichos", "George Orwell", 4.00, 3, 2, "/images/revolucao_bichos.jpg"},
                {"Na Pior em Paris e Londres", "George Orwell", 5.50, 2, 1, "/images/na_pior.jpg"},

                // ========== J.K. ROWLING ==========
                {"Harry Potter e a Pedra Filosofal", "J.K. Rowling", 8.00, 8, 7, "/images/pedra_filosofal.jpg"},
                {"Harry Potter e a Câmara Secreta", "J.K. Rowling", 8.00, 6, 4, "/images/camara_secreta.jpg"},
                {"Harry Potter e o Prisioneiro de Azkaban", "J.K. Rowling", 8.00, 5, 3, "/images/prisioneiro_askabam.jpg"},
                {"Harry Potter e o Cálice de Fogo", "J.K. Rowling", 9.00, 4, 2, "/images/calice_de_fogo.jpg"},
                {"Harry Potter e a Ordem da Fênix", "J.K. Rowling", 9.00, 3, 1, "/images/ordem_da_fenix.jpg"},

                // ========== DAN BROWN ==========
                {"O Código Da Vinci", "Dan Brown", 7.00, 5, 4, "/images/codigo_da_vinci.jpg"},
                {"Anjos e Demônios", "Dan Brown", 7.00, 4, 3, "/images/anjos_e_demonios.jpg"},
                {"O Símbolo Perdido", "Dan Brown", 7.00, 3, 2, "/images/simbolo_perdido.jpg"},
                {"Inferno", "Dan Brown", 7.50, 2, 1, "/images/inferno.jpg"},

                // ========== YUVAL NOAH HARARI ==========
                {"Sapiens", "Yuval Noah Harari", 10.00, 4, 3, "/images/sapiens.jpg"},
                {"Homo Deus", "Yuval Noah Harari", 10.00, 3, 2, "/images/homo_Deus.jpg"},
                {"21 Lições para o Século 21", "Yuval Noah Harari", 10.00, 2, 1, "/images/licoes.jpg"},

                // ========== JANE AUSTEN ==========
                {"Orgulho e Preconceito", "Jane Austen", 5.00, 4, 3, "/images/orgulho_e_preconceito.jpg"},
                {"Emma", "Jane Austen", 5.00, 3, 2, "/images/emma.jpg"},
                {"Razão e Sensibilidade", "Jane Austen", 5.00, 2, 1, "/images/razao_e_sensibilidade.jpg"},
                {"Persuasão", "Jane Austen", 4.50, 2, 0, "/images/persuasao.jpg"},

                // ========== J.R.R. TOLKIEN ==========
                {"O Hobbit", "J.R.R. Tolkien", 8.00, 5, 4, "/images/hobbit.jpg"},
                {"O Senhor dos Anéis: A Sociedade do Anel", "J.R.R. Tolkien", 10.00, 4, 3, "/images/sociedade_anel.jpg"},
                {"O Senhor dos Anéis: As Duas Torres", "J.R.R. Tolkien", 10.00, 3, 2, "/images/torres.jpg"},
                {"O Senhor dos Anéis: O Retorno do Rei", "J.R.R. Tolkien", 10.00, 3, 2, "/images/retorno_rei.jpg"},
                {"O Silmarillion", "J.R.R. Tolkien", 9.00, 2, 1, "/images/silmarillion.jpg"},

                // ========== GUIMARÃES ROSA ==========
                {"Grande Sertão: Veredas", "Guimarães Rosa", 8.00, 3, 2, "/images/grande_sertao_veredas.jpg"},
                {"Sagarana", "Guimarães Rosa", 6.00, 2, 1, "/images/sagarana.jpg"},

                // ========== JORGE AMADO ==========
                {"Capitães da Areia", "Jorge Amado", 5.50, 4, 2, "/images/capitaes_da_areia.jpg"},
                {"Gabriela, Cravo e Canela", "Jorge Amado", 6.00, 3, 2, "/images/gabriela.jpg"},
                {"Dona Flor e Seus Dois Maridos", "Jorge Amado", 6.00, 2, 1, "/images/dona_flor.jpg"},

                // ========== PATRICK ROTHFUSS ==========
                {"O Nome do Vento", "Patrick Rothfuss", 9.00, 3, 2, "/images/nome_do_vento.jpg"},
                {"O Temor do Sábio", "Patrick Rothfuss", 9.00, 2, 1, "/images/temor_do_sabio.jpg"},

                // ========== CHARLES DUHIGG ==========
                {"O Poder do Hábito", "Charles Duhigg", 7.50, 3, 2, "/images/poder_do_habito.jpg"},

                // ========== ROBERT C. MARTIN ==========
                {"Código Limpo", "Robert C. Martin", 12.00, 3, 2, "/images/codigo_limpo.jpg"},
                {"Arquitetura Limpa", "Robert C. Martin", 12.00, 2, 1, "/images/arquitetura_limpa.jpg"},

                // ========== RAY BRADBURY ==========
                {"Fahrenheit 451", "Ray Bradbury", 6.00, 2, 1, "/images/fahrenheit_451.jpg"},
                {"Crônicas Marcianas", "Ray Bradbury", 6.50, 2, 1, "/images/cronicas_marcianas.jpg"},

                // ========== WILLIAM GIBSON ==========
                {"Neuromancer", "William Gibson", 8.00, 2, 1, "/images/neuromancer.jpg"},

                // ========== EMILY BRONTË ==========
                {"O Morro dos Ventos Uivantes", "Emily Brontë", 5.50, 2, 1, "/images/morro_dos_ventos.jpg"},

                // ========== FRANZ KAFKA ==========
                {"A Metamorfose", "Franz Kafka", 4.00, 3, 2, "/images/metamorfose.jpg"},
                {"O Processo", "Franz Kafka", 5.00, 2, 1, "/images/processo.jpg"},
                {"O Castelo", "Franz Kafka", 5.50, 2, 0, "/images/castelo.jpg"},

                // ========== FIÓDOR DOSTOIÉVSKI ==========
                {"Crime e Castigo", "Fiódor Dostoiévski", 8.00, 3, 2, "/images/crime_castigo.jpg"},
                {"Os Irmãos Karamázov", "Fiódor Dostoiévski", 9.00, 2, 1, "/images/irmaos.jpg"},
                {"O Idiota", "Fiódor Dostoiévski", 8.00, 2, 0, "/images/idiota.jpg"},

                // ========== ROBERT LOUIS STEVENSON ==========
                {"A Ilha do Tesouro", "Robert Louis Stevenson", 5.00, 3, 0, "/images/ilha.jpg"},
                {"O Médico e o Monstro", "Robert Louis Stevenson", 4.50, 2, 1, "/images/medico_monstro.jpg"},

                // ========== ERICH GAMMA (Gang of Four) ==========
                {"Design Patterns", "Erich Gamma", 15.00, 2, 0, "/images/design_patterns.jpg"},

                // ========== GABRIEL GARCÍA MÁRQUEZ ==========
                {"Cem Anos de Solidão", "Gabriel García Márquez", 8.00, 3, 1, "/images/solidao.jpg"},
                {"O Amor nos Tempos do Cólera", "Gabriel García Márquez", 7.00, 2, 0, "/images/colera.jpg"},
                {"Crônica de uma Morte Anunciada", "Gabriel García Márquez", 6.00, 2, 1, "/images/cronica_anunciada.jpg"},

                // ========== AGATHA CHRISTIE ==========
                {"Assassinato no Expresso do Oriente", "Agatha Christie", 6.00, 3, 0, "/images/oriente.jpg"},
                {"Morte no Nilo", "Agatha Christie", 6.00, 2, 1, "/images/nilo.jpg"},
                {"O Caso dos Dez Negrinhos", "Agatha Christie", 6.50, 2, 1, "/images/dez.jpg"},

                // ========== OUTROS CLÁSSICOS ==========
                {"O Pequeno Príncipe", "Antoine de Saint-Exupéry", 4.00, 5, 4, "/images/pequeno.jpg"},
                {"Admirável Mundo Novo", "Aldous Huxley", 6.50, 3, 2, "/images/admiravel.jpg"},
                {"Laranja Mecânica", "Anthony Burgess", 6.00, 2, 1, "/images/laranja.jpg"}
        };

        for (Object[] dados : livrosData) {
            Livro livro = new Livro();
            livro.setTitulo((String) dados[0]);
            livro.setAutor((String) dados[1]);
            livro.setPreco((Double) dados[2]);
            livro.setQuantidade((Integer) dados[3]);
            livro.setQuantidadeDisponivel((Integer) dados[4]);
            livro.setDisponivel((Integer) dados[4] > 0);
            livro.setImageUrl((String) dados[5]);

            repositorioLivro.save(livro);
        }

        System.out.println("   ✅ " + repositorioLivro.count() + " livros criados");
        System.out.println("   📁 Imagens devem estar em: src/main/resources/static/images/");
    }

    private void inicializarReservas() {
        System.out.println("📋 Criando reservas...");

        var usuarios = repositorioUsuario.findAll();
        var livros = repositorioLivro.findAll();

        if (usuarios.size() < 7 || livros.size() < 20) {
            System.err.println("   ⚠️ Dados insuficientes para criar reservas");
            return;
        }

        // Pegar usuários comuns (índices 3-9)
        Usuario ana = usuarios.get(3);
        Usuario pedro = usuarios.get(4);
        Usuario julia = usuarios.get(5);
        Usuario lucas = usuarios.get(6);
        Usuario fernanda = usuarios.get(7);
        Usuario roberto = usuarios.get(8);
        Usuario carlos = usuarios.get(9);

        // === RESERVAS PENDENTES (3) ===
        criarReserva(ana, livros.get(0), "PENDENTE", -2, 0, null);
        criarReserva(pedro, livros.get(1), "PENDENTE", -5, 0, null);
        criarReserva(julia, livros.get(4), "PENDENTE", -24, 0, null);

        // === RESERVAS APROVADAS (5) ===
        criarReserva(lucas, livros.get(2), "APROVADA", -72, 11, null);
        criarReserva(fernanda, livros.get(3), "APROVADA", -120, 9, null);
        criarReserva(roberto, livros.get(8), "APROVADA", -168, 7, null);
        criarReserva(ana, livros.get(9), "APROVADA", -240, 4, null);
        criarReserva(carlos, livros.get(10), "APROVADA", -96, 6, null);

        // === RESERVAS DEVOLVIDAS (7) ===
        criarReserva(pedro, livros.get(5), "DEVOLVIDA", -720, -384, -360);
        criarReserva(julia, livros.get(6), "DEVOLVIDA", -600, -264, -240);
        criarReserva(lucas, livros.get(7), "DEVOLVIDA", -480, -144, -120);
        criarReserva(fernanda, livros.get(11), "DEVOLVIDA", -432, -96, -72);
        criarReserva(roberto, livros.get(12), "DEVOLVIDA", -360, -24, -12);
        criarReserva(carlos, livros.get(13), "DEVOLVIDA", -288, -48, -36);
        criarReserva(ana, livros.get(14), "DEVOLVIDA", -1080, -744, -720);

        // === RESERVAS REJEITADAS (2) ===
        criarReserva(pedro, livros.get(18), "REJEITADA", -192, 0, null);
        criarReserva(julia, livros.get(19), "REJEITADA", -144, 0, null);

        // === RESERVAS CANCELADAS (2) ===
        criarReserva(lucas, livros.get(15), "CANCELADA", -96, 0, null);
        criarReserva(fernanda, livros.get(16), "CANCELADA", -48, 0, null);

        System.out.println("   ✅ " + repositorioReserva.count() + " reservas criadas");
    }

    private void criarReserva(Usuario usuario, Livro livro, String status,
                              int horasReserva, int diasDevolucao, Integer horasDevolucao) {
        Reserva reserva = new Reserva(usuario, livro);
        reserva.setStatus(status);
        reserva.setDataReserva(LocalDateTime.now().plusHours(horasReserva));

        if (diasDevolucao > 0) {
            reserva.setDataDevolucaoPrevista(LocalDateTime.now().plusDays(diasDevolucao));
        }

        if (horasDevolucao != null) {
            reserva.setDataDevolucao(LocalDateTime.now().plusHours(horasDevolucao));
        }

        repositorioReserva.save(reserva);
    }

    private void imprimirEstatisticas() {
        System.out.println("\n📊 ESTATÍSTICAS DO SISTEMA:");
        System.out.println("   👥 Usuários: " + repositorioUsuario.count());
        System.out.println("   📚 Livros: " + repositorioLivro.count());
        System.out.println("   📋 Reservas: " + repositorioReserva.count());
        System.out.println("   ⏳ Pendentes: " + repositorioReserva.countPendentes());
        System.out.println("   ✅ Aprovadas: " + repositorioReserva.countAprovadas());
        System.out.println("   📗 Disponíveis: " + repositorioLivro.countDisponiveis());
        System.out.println("   📕 Indisponíveis: " + repositorioLivro.countIndisponiveis());
        System.out.println();
    }
}