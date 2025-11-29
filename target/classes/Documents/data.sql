-- ========================================
-- 1. CRIAÇÃO DO BANCO DE DADOS
-- ========================================

DROP DATABASE IF EXISTS biblioteca;
CREATE DATABASE biblioteca CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE biblioteca;

-- ========================================
-- 2. CRIAÇÃO DAS TABELAS (DDL)
-- ========================================

-- ----------------------------------------
-- 2.1 TABELA: USUARIOS
-- ----------------------------------------
DROP TABLE IF EXISTS usuarios;

CREATE TABLE usuarios (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          username VARCHAR(255) NOT NULL UNIQUE,
                          password VARCHAR(255) NOT NULL,
                          email VARCHAR(255) NOT NULL,
                          role VARCHAR(20) DEFAULT 'USER',
                          ativo BOOLEAN DEFAULT TRUE,

    -- Constraints
                          CONSTRAINT uk_username UNIQUE (username),
                          CONSTRAINT uk_email UNIQUE (email),
                          CONSTRAINT chk_role CHECK (role IN ('ADMIN', 'BIBLIOTECARIO', 'USER'))

) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Usuários do sistema (administradores, bibliotecários e leitores)';

-- Índices adicionais
CREATE INDEX idx_email ON usuarios(email);
CREATE INDEX idx_role ON usuarios(role);

-- ----------------------------------------
-- 2.2 TABELA: LIVROS
-- ----------------------------------------
DROP TABLE IF EXISTS livros;

CREATE TABLE livros (
                        id INT AUTO_INCREMENT PRIMARY KEY,
                        titulo VARCHAR(255) NOT NULL,
                        autor VARCHAR(255) NOT NULL,
                        preco DOUBLE NOT NULL,
                        image_url VARCHAR(500),
                        quantidade INT NOT NULL DEFAULT 0,
                        quantidade_disponivel INT NOT NULL DEFAULT 0,
                        disponivel BOOLEAN NOT NULL DEFAULT TRUE,

    -- Constraints
                        CONSTRAINT chk_preco CHECK (preco >= 0),
                        CONSTRAINT chk_quantidade CHECK (quantidade >= 0),
                        CONSTRAINT chk_quantidade_disponivel CHECK (
                            quantidade_disponivel >= 0 AND
                            quantidade_disponivel <= quantidade
                            )

) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Acervo de livros da biblioteca';

-- Índices adicionais
CREATE INDEX idx_titulo ON livros(titulo);
CREATE INDEX idx_autor ON livros(autor);
CREATE INDEX idx_disponivel ON livros(disponivel);

-- ----------------------------------------
-- 2.3 TABELA: RESERVAS
-- ----------------------------------------
DROP TABLE IF EXISTS reservas;

CREATE TABLE reservas (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          usuario_id BIGINT NOT NULL,
                          livro_id INT NOT NULL,
                          data_reserva DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          data_devolucao_prevista DATETIME,
                          data_devolucao DATETIME,
                          status VARCHAR(20) NOT NULL DEFAULT 'PENDENTE',
                          observacao VARCHAR(500),

    -- Foreign Keys
                          CONSTRAINT fk_reserva_usuario
                              FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
                                  ON DELETE CASCADE
                                  ON UPDATE CASCADE,

                          CONSTRAINT fk_reserva_livro
                              FOREIGN KEY (livro_id) REFERENCES livros(id)
                                  ON DELETE RESTRICT
                                  ON UPDATE CASCADE,

    -- Constraints
                          CONSTRAINT chk_status CHECK (status IN (
                                                                  'PENDENTE', 'APROVADA', 'DEVOLVIDA', 'REJEITADA', 'CANCELADA'
                              )),
                          CONSTRAINT chk_data_devolucao CHECK (
                              data_devolucao IS NULL OR data_devolucao >= data_reserva
                              )

) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Reservas e empréstimos de livros';

-- Índices adicionais
CREATE INDEX idx_usuario_id ON reservas(usuario_id);
CREATE INDEX idx_livro_id ON reservas(livro_id);
CREATE INDEX idx_status ON reservas(status);
CREATE INDEX idx_data_reserva ON reservas(data_reserva);
CREATE INDEX idx_usuario_livro_status ON reservas(usuario_id, livro_id, status);

-- ----------------------------------------
-- 2.4 TABELA: FAVORITOS
-- ----------------------------------------
DROP TABLE IF EXISTS favoritos;

CREATE TABLE favoritos (
                           id BIGINT AUTO_INCREMENT PRIMARY KEY,
                           usuario_id BIGINT NOT NULL,
                           livro_id INT NOT NULL,
                           data_adicao DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Foreign Keys
                           CONSTRAINT fk_favorito_usuario
                               FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
                                   ON DELETE CASCADE
                                   ON UPDATE CASCADE,

                           CONSTRAINT fk_favorito_livro
                               FOREIGN KEY (livro_id) REFERENCES livros(id)
                                   ON DELETE CASCADE
                                   ON UPDATE CASCADE,

    -- Constraint único
                           CONSTRAINT uk_usuario_livro UNIQUE (usuario_id, livro_id)

) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Livros favoritos dos usuários';

-- Índices adicionais
CREATE INDEX idx_favorito_livro ON favoritos(livro_id);
CREATE INDEX idx_data_adicao ON favoritos(data_adicao);

-- ========================================
-- 3. INSERÇÃO DE DADOS (DML)
-- ========================================

-- ----------------------------------------
-- 3.1 INSERIR USUÁRIOS
-- ----------------------------------------
-- Senha para todos: senha123 (hash BCrypt)
-- Hash gerado: $2a$10$xvZB8P5Q5K.i6Y0jzfN0Zu7B3qH8yqyN.wZ9nKzCwC3cJ6P5x5xLa

INSERT INTO usuarios (username, password, email, role, ativo) VALUES
-- Administrador
('admin', '$2a$10$xvZB8P5Q5K.i6Y0jzfN0Zu7B3qH8yqyN.wZ9nKzCwC3cJ6P5x5xLa', 'admin@biblioteca.com', 'ADMIN', TRUE),

-- Bibliotecários
('maria.biblio', '$2a$10$xvZB8P5Q5K.i6Y0jzfN0Zu7B3qH8yqyN.wZ9nKzCwC3cJ6P5x5xLa', 'maria@biblioteca.com', 'BIBLIOTECARIO', TRUE),
('joao.biblio', '$2a$10$xvZB8P5Q5K.i6Y0jzfN0Zu7B3qH8yqyN.wZ9nKzCwC3cJ6P5x5xLa', 'joao@biblioteca.com', 'BIBLIOTECARIO', TRUE),

-- Usuários comuns
('ana.silva', '$2a$10$xvZB8P5Q5K.i6Y0jzfN0Zu7B3qH8yqyN.wZ9nKzCwC3cJ6P5x5xLa', 'ana.silva@email.com', 'USER', TRUE),
('pedro.santos', '$2a$10$xvZB8P5Q5K.i6Y0jzfN0Zu7B3qH8yqyN.wZ9nKzCwC3cJ6P5x5xLa', 'pedro.santos@email.com', 'USER', TRUE),
('julia.costa', '$2a$10$xvZB8P5Q5K.i6Y0jzfN0Zu7B3qH8yqyN.wZ9nKzCwC3cJ6P5x5xLa', 'julia.costa@email.com', 'USER', TRUE),
('lucas.oliveira', '$2a$10$xvZB8P5Q5K.i6Y0jzfN0Zu7B3qH8yqyN.wZ9nKzCwC3cJ6P5x5xLa', 'lucas.oliveira@email.com', 'USER', TRUE),
('fernanda.lima', '$2a$10$xvZB8P5Q5K.i6Y0jzfN0Zu7B3qH8yqyN.wZ9nKzCwC3cJ6P5x5xLa', 'fernanda.lima@email.com', 'USER', TRUE),
('roberto.alves', '$2a$10$xvZB8P5Q5K.i6Y0jzfN0Zu7B3qH8yqyN.wZ9nKzCwC3cJ6P5x5xLa', 'roberto.alves@email.com', 'USER', TRUE),
('carlos.mendes', '$2a$10$xvZB8P5Q5K.i6Y0jzfN0Zu7B3qH8yqyN.wZ9nKzCwC3cJ6P5x5xLa', 'carlos.mendes@email.com', 'USER', TRUE);

-- ----------------------------------------
-- 3.2 INSERIR LIVROS
-- ----------------------------------------

-- Machado de Assis
INSERT INTO livros (titulo, autor, preco, quantidade, quantidade_disponivel, disponivel) VALUES
                                                                                             ('Dom Casmurro', 'Machado de Assis', 5.00, 5, 4, TRUE),
                                                                                             ('Memórias Póstumas de Brás Cubas', 'Machado de Assis', 5.00, 4, 3, TRUE),
                                                                                             ('Quincas Borba', 'Machado de Assis', 5.00, 3, 2, TRUE),
                                                                                             ('Esaú e Jacó', 'Machado de Assis', 4.50, 2, 1, TRUE),
                                                                                             ('O Alienista', 'Machado de Assis', 3.00, 4, 0, FALSE);

-- George Orwell
INSERT INTO livros (titulo, autor, preco, quantidade, quantidade_disponivel, disponivel) VALUES
                                                                                             ('1984', 'George Orwell', 6.00, 6, 5, TRUE),
                                                                                             ('A Revolução dos Bichos', 'George Orwell', 4.00, 3, 2, TRUE),
                                                                                             ('Na Pior em Paris e Londres', 'George Orwell', 5.50, 2, 1, TRUE);

-- J.K. Rowling
INSERT INTO livros (titulo, autor, preco, quantidade, quantidade_disponivel, disponivel) VALUES
                                                                                             ('Harry Potter e a Pedra Filosofal', 'J.K. Rowling', 8.00, 8, 7, TRUE),
                                                                                             ('Harry Potter e a Câmara Secreta', 'J.K. Rowling', 8.00, 6, 4, TRUE),
                                                                                             ('Harry Potter e o Prisioneiro de Azkaban', 'J.K. Rowling', 8.00, 5, 3, TRUE),
                                                                                             ('Harry Potter e o Cálice de Fogo', 'J.K. Rowling', 9.00, 4, 2, TRUE),
                                                                                             ('Harry Potter e a Ordem da Fênix', 'J.K. Rowling', 9.00, 3, 1, TRUE);

-- Dan Brown
INSERT INTO livros (titulo, autor, preco, quantidade, quantidade_disponivel, disponivel) VALUES
                                                                                             ('O Código Da Vinci', 'Dan Brown', 7.00, 5, 4, TRUE),
                                                                                             ('Anjos e Demônios', 'Dan Brown', 7.00, 4, 3, TRUE),
                                                                                             ('O Símbolo Perdido', 'Dan Brown', 7.00, 3, 2, TRUE),
                                                                                             ('Inferno', 'Dan Brown', 7.50, 2, 1, TRUE);

-- Yuval Noah Harari
INSERT INTO livros (titulo, autor, preco, quantidade, quantidade_disponivel, disponivel) VALUES
                                                                                             ('Sapiens', 'Yuval Noah Harari', 10.00, 4, 3, TRUE),
                                                                                             ('Homo Deus', 'Yuval Noah Harari', 10.00, 3, 2, TRUE),
                                                                                             ('21 Lições para o Século 21', 'Yuval Noah Harari', 10.00, 2, 1, TRUE);

-- Jane Austen
INSERT INTO livros (titulo, autor, preco, quantidade, quantidade_disponivel, disponivel) VALUES
                                                                                             ('Orgulho e Preconceito', 'Jane Austen', 5.00, 4, 3, TRUE),
                                                                                             ('Emma', 'Jane Austen', 5.00, 3, 2, TRUE),
                                                                                             ('Razão e Sensibilidade', 'Jane Austen', 5.00, 2, 1, TRUE),
                                                                                             ('Persuasão', 'Jane Austen', 4.50, 2, 0, FALSE);

-- J.R.R. Tolkien
INSERT INTO livros (titulo, autor, preco, quantidade, quantidade_disponivel, disponivel) VALUES
                                                                                             ('O Hobbit', 'J.R.R. Tolkien', 8.00, 5, 4, TRUE),
                                                                                             ('O Senhor dos Anéis: A Sociedade do Anel', 'J.R.R. Tolkien', 10.00, 4, 3, TRUE),
                                                                                             ('O Senhor dos Anéis: As Duas Torres', 'J.R.R. Tolkien', 10.00, 3, 2, TRUE),
                                                                                             ('O Senhor dos Anéis: O Retorno do Rei', 'J.R.R. Tolkien', 10.00, 3, 2, TRUE),
                                                                                             ('O Silmarillion', 'J.R.R. Tolkien', 9.00, 2, 1, TRUE);

-- Guimarães Rosa
INSERT INTO livros (titulo, autor, preco, quantidade, quantidade_disponivel, disponivel) VALUES
                                                                                             ('Grande Sertão: Veredas', 'Guimarães Rosa', 8.00, 3, 2, TRUE),
                                                                                             ('Sagarana', 'Guimarães Rosa', 6.00, 2, 1, TRUE);

-- Jorge Amado
INSERT INTO livros (titulo, autor, preco, quantidade, quantidade_disponivel, disponivel) VALUES
                                                                                             ('Capitães da Areia', 'Jorge Amado', 5.50, 4, 2, TRUE),
                                                                                             ('Gabriela, Cravo e Canela', 'Jorge Amado', 6.00, 3, 2, TRUE),
                                                                                             ('Dona Flor e Seus Dois Maridos', 'Jorge Amado', 6.00, 2, 1, TRUE);

-- Outros autores
INSERT INTO livros (titulo, autor, preco, quantidade, quantidade_disponivel, disponivel) VALUES
                                                                                             ('O Nome do Vento', 'Patrick Rothfuss', 9.00, 3, 2, TRUE),
                                                                                             ('O Temor do Sábio', 'Patrick Rothfuss', 9.00, 2, 1, TRUE),
                                                                                             ('O Poder do Hábito', 'Charles Duhigg', 7.50, 3, 2, TRUE),
                                                                                             ('Código Limpo', 'Robert C. Martin', 12.00, 3, 2, TRUE),
                                                                                             ('Arquitetura Limpa', 'Robert C. Martin', 12.00, 2, 1, TRUE),
                                                                                             ('Fahrenheit 451', 'Ray Bradbury', 6.00, 2, 1, TRUE),
                                                                                             ('Crônicas Marcianas', 'Ray Bradbury', 6.50, 2, 1, TRUE),
                                                                                             ('Neuromancer', 'William Gibson', 8.00, 2, 1, TRUE),
                                                                                             ('O Morro dos Ventos Uivantes', 'Emily Brontë', 5.50, 2, 1, TRUE),
                                                                                             ('A Metamorfose', 'Franz Kafka', 4.00, 3, 2, TRUE),
                                                                                             ('O Processo', 'Franz Kafka', 5.00, 2, 1, TRUE),
                                                                                             ('O Castelo', 'Franz Kafka', 5.50, 2, 0, FALSE),
                                                                                             ('Crime e Castigo', 'Fiódor Dostoiévski', 8.00, 3, 2, TRUE),
                                                                                             ('Os Irmãos Karamázov', 'Fiódor Dostoiévski', 9.00, 2, 1, TRUE),
                                                                                             ('O Idiota', 'Fiódor Dostoiévski', 8.00, 2, 0, FALSE),
                                                                                             ('A Ilha do Tesouro', 'Robert Louis Stevenson', 5.00, 3, 0, FALSE),
                                                                                             ('O Médico e o Monstro', 'Robert Louis Stevenson', 4.50, 2, 1, TRUE),
                                                                                             ('Design Patterns', 'Erich Gamma', 15.00, 2, 0, FALSE),
                                                                                             ('Cem Anos de Solidão', 'Gabriel García Márquez', 8.00, 3, 1, TRUE),
                                                                                             ('O Amor nos Tempos do Cólera', 'Gabriel García Márquez', 7.00, 2, 0, FALSE),
                                                                                             ('Crônica de uma Morte Anunciada', 'Gabriel García Márquez', 6.00, 2, 1, TRUE),
                                                                                             ('Assassinato no Expresso do Oriente', 'Agatha Christie', 6.00, 3, 0, FALSE),
                                                                                             ('Morte no Nilo', 'Agatha Christie', 6.00, 2, 1, TRUE),
                                                                                             ('O Caso dos Dez Negrinhos', 'Agatha Christie', 6.50, 2, 1, TRUE),
                                                                                             ('O Pequeno Príncipe', 'Antoine de Saint-Exupéry', 4.00, 5, 4, TRUE),
                                                                                             ('Admirável Mundo Novo', 'Aldous Huxley', 6.50, 3, 2, TRUE),
                                                                                             ('Laranja Mecânica', 'Anthony Burgess', 6.00, 2, 1, TRUE);

-- ----------------------------------------
-- 3.3 INSERIR RESERVAS
-- ----------------------------------------

-- Reservas PENDENTES (aguardando aprovação)
INSERT INTO reservas (usuario_id, livro_id, data_reserva, status) VALUES
                                                                      (4, 1, DATE_SUB(NOW(), INTERVAL 2 HOUR), 'PENDENTE'),
                                                                      (5, 2, DATE_SUB(NOW(), INTERVAL 5 HOUR), 'PENDENTE'),
                                                                      (6, 5, DATE_SUB(NOW(), INTERVAL 1 DAY), 'PENDENTE');

-- Reservas APROVADAS (em andamento)
INSERT INTO reservas (usuario_id, livro_id, data_reserva, data_devolucao_prevista, status) VALUES
                                                                                               (7, 3, DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_ADD(NOW(), INTERVAL 11 DAY), 'APROVADA'),
                                                                                               (8, 4, DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_ADD(NOW(), INTERVAL 9 DAY), 'APROVADA'),
                                                                                               (9, 9, DATE_SUB(NOW(), INTERVAL 7 DAY), DATE_ADD(NOW(), INTERVAL 7 DAY), 'APROVADA'),
                                                                                               (4, 10, DATE_SUB(NOW(), INTERVAL 10 DAY), DATE_ADD(NOW(), INTERVAL 4 DAY), 'APROVADA'),
                                                                                               (10, 11, DATE_SUB(NOW(), INTERVAL 4 DAY), DATE_ADD(NOW(), INTERVAL 10 DAY), 'APROVADA');

-- Reservas DEVOLVIDAS (finalizadas)
INSERT INTO reservas (usuario_id, livro_id, data_reserva, data_devolucao_prevista, data_devolucao, status) VALUES
                                                                                                               (5, 6, DATE_SUB(NOW(), INTERVAL 30 DAY), DATE_SUB(NOW(), INTERVAL 16 DAY), DATE_SUB(NOW(), INTERVAL 15 DAY), 'DEVOLVIDA'),
                                                                                                               (6, 7, DATE_SUB(NOW(), INTERVAL 25 DAY), DATE_SUB(NOW(), INTERVAL 11 DAY), DATE_SUB(NOW(), INTERVAL 10 DAY), 'DEVOLVIDA'),
                                                                                                               (7, 8, DATE_SUB(NOW(), INTERVAL 20 DAY), DATE_SUB(NOW(), INTERVAL 6 DAY), DATE_SUB(NOW(), INTERVAL 5 DAY), 'DEVOLVIDA'),
                                                                                                               (8, 12, DATE_SUB(NOW(), INTERVAL 18 DAY), DATE_SUB(NOW(), INTERVAL 4 DAY), DATE_SUB(NOW(), INTERVAL 3 DAY), 'DEVOLVIDA'),
                                                                                                               (9, 13, DATE_SUB(NOW(), INTERVAL 15 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 12 HOUR), 'DEVOLVIDA'),
                                                                                                               (10, 14, DATE_SUB(NOW(), INTERVAL 12 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 36 HOUR), 'DEVOLVIDA'),
                                                                                                               (4, 15, DATE_SUB(NOW(), INTERVAL 45 DAY), DATE_SUB(NOW(), INTERVAL 31 DAY), DATE_SUB(NOW(), INTERVAL 30 DAY), 'DEVOLVIDA');

-- Reservas REJEITADAS
INSERT INTO reservas (usuario_id, livro_id, data_reserva, status, observacao) VALUES
                                                                                  (5, 19, DATE_SUB(NOW(), INTERVAL 8 DAY), 'REJEITADA', 'Livro temporariamente indisponível para manutenção'),
                                                                                  (6, 20, DATE_SUB(NOW(), INTERVAL 6 DAY), 'REJEITADA', 'Usuário possui pendências');

-- Reservas CANCELADAS
INSERT INTO reservas (usuario_id, livro_id, data_reserva, status, observacao) VALUES
                                                                                  (7, 16, DATE_SUB(NOW(), INTERVAL 4 DAY), 'CANCELADA', 'Cancelada pelo usuário'),
                                                                                  (8, 17, DATE_SUB(NOW(), INTERVAL 2 DAY), 'CANCELADA', 'Cancelada pelo usuário');

-- ----------------------------------------
-- 3.4 INSERIR FAVORITOS
-- ----------------------------------------

INSERT INTO favoritos (usuario_id, livro_id, data_adicao) VALUES
-- Favoritos de ana.silva (id: 4)
(4, 1, DATE_SUB(NOW(), INTERVAL 15 DAY)),
(4, 6, DATE_SUB(NOW(), INTERVAL 12 DAY)),
(4, 9, DATE_SUB(NOW(), INTERVAL 10 DAY)),
(4, 14, DATE_SUB(NOW(), INTERVAL 8 DAY)),

-- Favoritos de pedro.santos (id: 5)
(5, 2, DATE_SUB(NOW(), INTERVAL 14 DAY)),
(5, 7, DATE_SUB(NOW(), INTERVAL 11 DAY)),
(5, 18, DATE_SUB(NOW(), INTERVAL 9 DAY)),

-- Favoritos de julia.costa (id: 6)
(6, 3, DATE_SUB(NOW(), INTERVAL 13 DAY)),
(6, 19, DATE_SUB(NOW(), INTERVAL 7 DAY)),
(6, 35, DATE_SUB(NOW(), INTERVAL 5 DAY)),

-- Favoritos de lucas.oliveira (id: 7)
(7, 25, DATE_SUB(NOW(), INTERVAL 10 DAY)),
(7, 26, DATE_SUB(NOW(), INTERVAL 6 DAY)),

-- Favoritos de fernanda.lima (id: 8)
(8, 40, DATE_SUB(NOW(), INTERVAL 8 DAY)),
(8, 42, DATE_SUB(NOW(), INTERVAL 4 DAY)),

-- Favoritos de roberto.alves (id: 9)
(9, 50, DATE_SUB(NOW(), INTERVAL 7 DAY)),
(9, 52, DATE_SUB(NOW(), INTERVAL 3 DAY)),

-- Favoritos de carlos.mendes (id: 10)
(10, 55, DATE_SUB(NOW(), INTERVAL 5 DAY)),
(10, 57, DATE_SUB(NOW(), INTERVAL 2 DAY));

-- ========================================
-- 4. VIEWS (OPCIONAL - para facilitar consultas)
-- ========================================

-- View: Reservas com informações completas
CREATE OR REPLACE VIEW vw_reservas_completas AS
SELECT
    r.id AS reserva_id,
    u.username AS usuario,
    u.email AS email_usuario,
    l.titulo AS livro,
    l.autor AS autor,
    r.data_reserva,
    r.data_devolucao_prevista,
    r.data_devolucao,
    r.status,
    r.observacao,
    DATEDIFF(COALESCE(r.data_devolucao, NOW()), r.data_reserva) AS dias_emprestimo
FROM reservas r
         INNER JOIN usuarios u ON r.usuario_id = u.id
         INNER JOIN livros l ON r.livro_id = l.id;

-- View: Estatísticas por usuário
CREATE OR REPLACE VIEW vw_estatisticas_usuario AS
SELECT
    u.id,
    u.username,
    u.email,
    COUNT(r.id) AS total_reservas,
    SUM(CASE WHEN r.status = 'APROVADA' THEN 1 ELSE 0 END) AS reservas_ativas,
    SUM(CASE WHEN r.status = 'DEVOLVIDA' THEN 1 ELSE 0 END) AS reservas_devolvidas,
    SUM(CASE WHEN r.status = 'PENDENTE' THEN 1 ELSE 0 END) AS reservas_pendentes,
    COUNT(f.id) AS total_favoritos
FROM usuarios u
         LEFT JOIN reservas r ON u.id = r.usuario_id
         LEFT JOIN favoritos f ON u.id = f.usuario_id
GROUP BY u.id, u.username, u.email;

-- View: Livros mais populares
CREATE OR REPLACE VIEW vw_livros_populares AS
SELECT
    l.id,
    l.titulo,
    l.autor,
    l.preco,
    l.quantidade,
    l.quantidade_disponivel,
    COUNT(DISTINCT r.id) AS total_reservas,
    COUNT(DISTINCT f.id) AS total_favoritos,
    (COUNT(DISTINCT r.id) + COUNT(DISTINCT f.id)) AS popularidade
FROM livros l
         LEFT JOIN reservas r ON l.id = r.livro_id
         LEFT JOIN favoritos f ON l.id = f.livro_id
GROUP BY l.id, l.titulo, l.autor, l.preco, l.quantidade, l.quantidade_disponivel
ORDER BY popularidade DESC;

-- ========================================
-- 5. PROCEDURES (OPCIONAL - operações comuns)
-- ========================================

-- Procedure: Aprovar reserva
DELIMITER //

CREATE PROCEDURE sp_aprovar_reserva(
    IN p_reserva_id BIGINT
)
BEGIN
    DECLARE v_livro_id INT;
    DECLARE v_qtd_disponivel INT;

    -- Buscar informações da reserva
SELECT livro_id INTO v_livro_id
FROM reservas
WHERE id = p_reserva_id AND status = 'PENDENTE';

-- Verificar disponibilidade
SELECT quantidade_disponivel INTO v_qtd_disponivel
FROM livros
WHERE id = v_livro_id;

IF v_qtd_disponivel > 0 THEN
        -- Aprovar reserva
UPDATE reservas
SET status = 'APROVADA',
    data_devolucao_prevista = DATE_ADD(NOW(), INTERVAL 14 DAY)
WHERE id = p_reserva_id;

-- Decrementar estoque
UPDATE livros
SET quantidade_disponivel = quantidade_disponivel - 1,
    disponivel = (quantidade_disponivel - 1 > 0)
WHERE id = v_livro_id;

SELECT 'Reserva aprovada com sucesso!' AS mensagem;
ELSE
SELECT 'Livro indisponível!' AS mensagem;
END IF;
END //

DELIMITER ;

-- Procedure: Registrar devolução
DELIMITER //

CREATE PROCEDURE sp_registrar_devolucao(
    IN p_reserva_id BIGINT
)
BEGIN
    DECLARE v_livro_id INT;

    -- Buscar livro da reserva
SELECT livro_id INTO v_livro_id
FROM reservas
WHERE id = p_reserva_id AND status = 'APROVADA';

IF v_livro_id IS NOT NULL THEN
        -- Registrar devolução
UPDATE reservas
SET status = 'DEVOLVIDA',
    data_devolucao = NOW()
WHERE id = p_reserva_id;

-- Incrementar estoque
UPDATE livros
SET quantidade_disponivel = quantidade_disponivel + 1,
    disponivel = TRUE
WHERE id = v_livro_id;

SELECT 'Devolução registrada com sucesso!' AS mensagem;
ELSE
SELECT 'Reserva não encontrada ou já devolvida!' AS mensagem;
END IF;
END //

DELIMITER ;

-- ========================================
-- 6. CONSULTAS DE VERIFICAÇÃO
-- ========================================

-- Verificar dados inseridos
SELECT 'USUARIOS' AS tabela, COUNT(*) AS total FROM usuarios
UNION ALL
SELECT 'LIVROS', COUNT(*) FROM livros
UNION ALL
SELECT 'RESERVAS', COUNT(*) FROM reservas
UNION ALL
SELECT 'FAVORITOS', COUNT(*) FROM favoritos;

-- Verificar distribuição de reservas por status
SELECT status, COUNT(*) AS total
FROM reservas
GROUP BY status
ORDER BY total DESC;

-- Verificar livros disponíveis vs indisponíveis
SELECT
    CASE WHEN disponivel THEN 'Disponível' ELSE 'Indisponível' END AS situacao,
    COUNT(*) AS total
FROM livros
GROUP BY disponivel;

-- ========================================
-- 7. GRANTS (OPCIONAL - segurança)
-- ========================================

-- Criar usuário específico para a aplicação
-- CREATE USER 'biblioteca_app'@'localhost' IDENTIFIED BY 'senha_segura_aqui';
-- GRANT SELECT, INSERT, UPDATE, DELETE ON biblioteca.* TO 'biblioteca_app'@'localhost';
-- FLUSH PRIVILEGES;

-- ========================================
-- FIM DO SCRIPT
-- ========================================

SELECT '✅ Script executado com sucesso!' AS status;
SELECT '📚 Banco de dados BIBLIOTECA criado e populado!' AS mensagem;