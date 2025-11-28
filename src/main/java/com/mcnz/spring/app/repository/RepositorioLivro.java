package com.mcnz.spring.app.repository;

import com.mcnz.spring.app.model.Livro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface RepositorioLivro extends JpaRepository<Livro, Integer> {

    // Métodos existentes
    List<Livro> findByTituloContaining(String titulo);

    // Contadores para dashboard
    @Query("SELECT COUNT(l) FROM Livro l WHERE l.quantidadeDisponivel > 0")
    long countDisponiveis();

    @Query("SELECT COUNT(l) FROM Livro l WHERE l.quantidadeDisponivel = 0")
    long countIndisponiveis();

    // 2. SUBCONSULTAS - Livros mais reservados que a média
    @Query(value = "SELECT " +
            "l.id AS id, " +
            "l.titulo AS titulo, " +
            "l.autor AS autor, " +
            "COUNT(r.id) AS total_reservas " +
            "FROM livros l " +
            "LEFT JOIN reservas r ON l.id = r.livro_id " +
            "GROUP BY l.id, l.titulo, l.autor " +
            "HAVING COUNT(r.id) > COALESCE(( " +
            "    SELECT AVG(cnt) FROM ( " +
            "        SELECT COUNT(*) AS cnt FROM reservas GROUP BY livro_id " +
            "    ) AS subq " +
            "), 0) " +
            "ORDER BY total_reservas DESC",
            nativeQuery = true)
    List<Map<String, Object>> findLivrosMaisReservadosQueMedia();

    // 3. AGREGADAS - Estatísticas de livros por autor
    @Query(value = "SELECT " +
            "l.autor AS autor, " +
            "COUNT(*) AS total_livros, " +
            "ROUND(AVG(l.preco), 2) AS preco_medio, " +
            "MIN(l.preco) AS preco_minimo, " +
            "MAX(l.preco) AS preco_maximo, " +
            "SUM(l.quantidade) AS estoque_total, " +
            "SUM(l.quantidade_disponivel) AS disponivel_total " +
            "FROM livros l " +
            "GROUP BY l.autor " +
            "HAVING COUNT(*) >= 1 " +
            "ORDER BY total_livros DESC",
            nativeQuery = true)
    List<Map<String, Object>> findEstatisticasLivrosPorAutor();

    // 3b. AGREGADAS - Livros com baixo estoque
    @Query(value = "SELECT " +
            "l.id AS id, " +
            "l.titulo AS titulo, " +
            "l.autor AS autor, " +
            "l.quantidade AS quantidade_total, " +
            "l.quantidade_disponivel AS quantidade_disponivel, " +
            "l.preco AS preco " +
            "FROM livros l " +
            "WHERE l.quantidade_disponivel <= 2 " +
            "ORDER BY l.quantidade_disponivel ASC",
            nativeQuery = true)
    List<Map<String, Object>> findLivrosBaixoEstoque();

    // 4. COMPARAÇÃO DE STRINGS - Busca avançada com LIKE
    @Query(value = "SELECT " +
            "l.id AS id, " +
            "l.titulo AS titulo, " +
            "l.autor AS autor, " +
            "l.preco AS preco, " +
            "l.quantidade_disponivel AS disponivel " +
            "FROM livros l " +
            "WHERE LOWER(l.titulo) LIKE LOWER(CONCAT('%', :termo, '%')) " +
            "   OR LOWER(l.autor) LIKE LOWER(CONCAT('%', :termo, '%')) " +
            "ORDER BY l.titulo",
            nativeQuery = true)
    List<Map<String, Object>> findBuscaAvancada(@Param("termo") String termo);

    // 4b. MULTICONJUNTO - Livros mais caros que algum livro de um autor
    @Query(value = "SELECT " +
            "l.id AS id, " +
            "l.titulo AS titulo, " +
            "l.autor AS autor, " +
            "l.preco AS preco " +
            "FROM livros l " +
            "WHERE l.preco > COALESCE(( " +
            "    SELECT MIN(l2.preco) FROM livros l2 WHERE l2.autor = :autor " +
            "), 0) " +
            "AND l.autor != :autor " +
            "ORDER BY l.preco DESC",
            nativeQuery = true)
    List<Map<String, Object>> findLivrosCarosQueAutor(@Param("autor") String autor);

    // 5. ORDENAÇÃO E LIMITAÇÃO - Top livros mais caros
    @Query(value = "SELECT " +
            "l.id AS id, " +
            "l.titulo AS titulo, " +
            "l.autor AS autor, " +
            "l.preco AS preco, " +
            "l.quantidade_disponivel AS disponivel " +
            "FROM livros l " +
            "ORDER BY l.preco DESC " +
            "LIMIT :limite",
            nativeQuery = true)
    List<Map<String, Object>> findTopLivrosMaisCaros(@Param("limite") int limite);

    // Livros disponíveis para reserva
    @Query(value = "SELECT " +
            "l.id AS id, " +
            "l.titulo AS titulo, " +
            "l.autor AS autor, " +
            "l.preco AS preco, " +
            "l.quantidade_disponivel AS disponivel " +
            "FROM livros l " +
            "WHERE l.quantidade_disponivel > 0 " +
            "ORDER BY l.titulo",
            nativeQuery = true)
    List<Map<String, Object>> findLivrosDisponiveisParaReserva();

    // Análise por faixa de preço com CASE WHEN
    @Query(value = "SELECT " +
            "CASE " +
            "    WHEN l.preco < 30 THEN 'Econômico (< R$ 30)' " +
            "    WHEN l.preco >= 30 AND l.preco < 50 THEN 'Médio (R$ 30-50)' " +
            "    WHEN l.preco >= 50 AND l.preco < 70 THEN 'Premium (R$ 50-70)' " +
            "    ELSE 'Luxo (> R$ 70)' " +
            "END AS faixa_preco, " +
            "COUNT(*) AS total_livros, " +
            "ROUND(AVG(l.preco), 2) AS preco_medio " +
            "FROM livros l " +
            "GROUP BY CASE " +
            "    WHEN l.preco < 30 THEN 'Econômico (< R$ 30)' " +
            "    WHEN l.preco >= 30 AND l.preco < 50 THEN 'Médio (R$ 30-50)' " +
            "    WHEN l.preco >= 50 AND l.preco < 70 THEN 'Premium (R$ 50-70)' " +
            "    ELSE 'Luxo (> R$ 70)' " +
            "END " +
            "ORDER BY preco_medio",
            nativeQuery = true)
    List<Map<String, Object>> findLivrosPorFaixaPreco();
}