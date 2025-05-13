package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.math.BigDecimal;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*; // Importações estáticas para assertions

class ProdutoMemoryDAOTest {

    private ProdutoDAO dao;

    @BeforeEach
    void setUp() {
        // Cria uma nova instância do DAO antes de cada teste para garantir isolamento
        dao = new ProdutoMemoryDAO();
    }

    @Test
    @DisplayName("Deve inserir um produto com sucesso")
    void insere_QuandoProdutoNovo_DeveAdicionarALista() {
        Produto produto = new Produto(1, "Teste", new BigDecimal("10.00"));
        assertDoesNotThrow(() -> dao.insere(produto)); // Verifica se não lança exceção
        assertEquals(1, dao.listarTodos().size());
        assertEquals(produto, dao.pesquisarPorId(1));
    }

    @Test
    @DisplayName("Não deve inserir produto com ID duplicado e deve lançar exceção")
    void insere_QuandoIdDuplicado_DeveLancarIllegalArgumentException() {
        Produto produto1 = new Produto(1, "Teste 1", new BigDecimal("10.00"));
        dao.insere(produto1);

        Produto produto2 = new Produto(1, "Teste 2", new BigDecimal("20.00"));
        // Verifica se a exceção esperada é lançada
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            dao.insere(produto2);
        });
        assertEquals("Erro: Já existe um produto com o ID 1.", exception.getMessage());
        assertEquals(1, dao.listarTodos().size()); // Garante que o segundo não foi inserido
    }

    @Test
    @DisplayName("Deve listar todos os produtos inseridos")
    void listarTodos_QuandoHaProdutos_DeveRetornarColecaoCorreta() {
        Produto produto1 = new Produto(1, "Produto A", new BigDecimal("1.00"));
        Produto produto2 = new Produto(2, "Produto B", new BigDecimal("2.00"));
        dao.insere(produto1);
        dao.insere(produto2);

        Collection<Produto> produtos = dao.listarTodos();
        assertNotNull(produtos);
        assertEquals(2, produtos.size());
        assertTrue(produtos.contains(produto1));
        assertTrue(produtos.contains(produto2));
    }

    @Test
    @DisplayName("Deve retornar coleção vazia ao listar quando não há produtos")
    void listarTodos_QuandoNaoHaProdutos_DeveRetornarColecaoVazia() {
        Collection<Produto> produtos = dao.listarTodos();
        assertNotNull(produtos);
        assertTrue(produtos.isEmpty());
    }

    @Test
    @DisplayName("Deve pesquisar e encontrar um produto existente pelo ID")
    void pesquisarPorId_QuandoProdutoExiste_DeveRetornarProduto() {
        Produto produto = new Produto(10, "Produto X", new BigDecimal("99.99"));
        dao.insere(produto);
        Produto encontrado = dao.pesquisarPorId(10);
        assertNotNull(encontrado);
        assertEquals(produto, encontrado);
    }

    @Test
    @DisplayName("Deve retornar null ao pesquisar produto com ID inexistente")
    void pesquisarPorId_QuandoProdutoNaoExiste_DeveRetornarNull() {
        Produto encontrado = dao.pesquisarPorId(999);
        assertNull(encontrado);
    }

    @Test
    @DisplayName("Deve alterar os dados de um produto existente")
    void alterar_QuandoProdutoExiste_DeveAtualizarDadosERetornarTrue() {
        Produto produtoOriginal = new Produto(5, "Original", new BigDecimal("50.00"));
        dao.insere(produtoOriginal);

        Produto produtoAlterado = new Produto(5, "Alterado", new BigDecimal("55.50"));
        boolean resultado = dao.alterar(produtoAlterado);

        assertTrue(resultado);
        Produto produtoVerificado = dao.pesquisarPorId(5);
        assertNotNull(produtoVerificado);
        assertEquals("Alterado", produtoVerificado.getNome());
        // Usar compareTo para BigDecimal é mais seguro para igualdade de valor
        assertEquals(0, new BigDecimal("55.50").compareTo(produtoVerificado.getPreco()));
        assertEquals(1, dao.listarTodos().size()); // Garante que não duplicou
    }

    @Test
    @DisplayName("Não deve alterar produto e retornar false se ID não existe")
    void alterar_QuandoProdutoNaoExiste_DeveRetornarFalse() {
        Produto produtoNaoExistente = new Produto(99, "Inexistente", new BigDecimal("1.00"));
        boolean resultado = dao.alterar(produtoNaoExistente);
        assertFalse(resultado);
    }

    @Test
    @DisplayName("Deve excluir um produto existente e retornar true")
    void excluir_QuandoProdutoExiste_DeveRemoverDaListaERetornarTrue() {
        Produto produto = new Produto(7, "Para Excluir", new BigDecimal("70.00"));
        dao.insere(produto);
        assertEquals(1, dao.listarTodos().size());

        boolean resultado = dao.excluir(7);
        assertTrue(resultado);
        assertNull(dao.pesquisarPorId(7));
        assertTrue(dao.listarTodos().isEmpty());
    }

    @Test
    @DisplayName("Não deve excluir e retornar false se produto com ID não existe")
    void excluir_QuandoProdutoNaoExiste_DeveRetornarFalse() {
        boolean resultado = dao.excluir(888);
        assertFalse(resultado);
    }
}