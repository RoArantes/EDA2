package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List; // Para verificar a ordem se necessário, embora Collection seja suficiente
import java.util.ArrayList; // Para construir a lista esperada se necessário

import static org.junit.jupiter.api.Assertions.*;

class ProdutoMemoryDAOTest {

    private ProdutoDAO dao;

    @BeforeEach
    void setUp() {
        dao = new ProdutoMemoryDAO(); // Instancia a nova versão com TreeMap
    }

    @Test
    @DisplayName("Deve inserir um produto com sucesso")
    void insere_QuandoProdutoNovo_DeveAdicionarAoMapa() {
        Produto produto = new Produto(1, "Teste", new BigDecimal("10.00"));
        assertDoesNotThrow(() -> dao.insere(produto));
        assertEquals(1, dao.listarTodos().size());
        // Verificar se o produto inserido pode ser pesquisado
        Produto pesquisado = dao.pesquisarPorId(1);
        assertNotNull(pesquisado);
        assertEquals(produto.getId(), pesquisado.getId());
        assertEquals(produto.getNome(), pesquisado.getNome());
        assertEquals(0, produto.getPreco().compareTo(pesquisado.getPreco()));
    }

    @Test
    @DisplayName("Não deve inserir produto com ID duplicado e deve lançar exceção")
    void insere_QuandoIdDuplicado_DeveLancarIllegalArgumentException() {
        Produto produto1 = new Produto(1, "Teste 1", new BigDecimal("10.00"));
        dao.insere(produto1);

        Produto produto2 = new Produto(1, "Teste 2", new BigDecimal("20.00"));
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            dao.insere(produto2);
        });
        assertEquals("Erro: Já existe um produto com o ID 1.", exception.getMessage());
        assertEquals(1, dao.listarTodos().size());
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar inserir produto nulo")
    void insere_QuandoProdutoNulo_DeveLancarIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            dao.insere(null);
        });
        assertEquals("Produto ou ID do produto não pode ser nulo.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar inserir produto com ID nulo")
    void insere_QuandoProdutoComIdNulo_DeveLancarIllegalArgumentException() {
        Produto produtoComIdNulo = new Produto(null, "Produto Sem ID", new BigDecimal("5.00"));
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            dao.insere(produtoComIdNulo);
        });
        assertEquals("Produto ou ID do produto não pode ser nulo.", exception.getMessage());
    }


    @Test
    @DisplayName("Deve listar todos os produtos inseridos ordenados por ID")
    void listarTodos_QuandoHaProdutos_DeveRetornarColecaoCorretaOrdenada() {
        Produto produto2 = new Produto(2, "Produto B", new BigDecimal("2.00"));
        Produto produto1 = new Produto(1, "Produto A", new BigDecimal("1.00"));
        Produto produto3 = new Produto(3, "Produto C", new BigDecimal("3.00"));
        dao.insere(produto2); // Inseridos fora de ordem de ID
        dao.insere(produto1);
        dao.insere(produto3);

        Collection<Produto> produtosListados = dao.listarTodos();
        assertNotNull(produtosListados);
        assertEquals(3, produtosListados.size());

        // TreeMap.values() retorna uma coleção cujos elementos estão na ordem das chaves.
        // Convertendo para List para verificar a ordem.
        List<Produto> listaProdutos = new ArrayList<>(produtosListados);
        assertEquals(produto1.getId(), listaProdutos.get(0).getId());
        assertEquals(produto2.getId(), listaProdutos.get(1).getId());
        assertEquals(produto3.getId(), listaProdutos.get(2).getId());

        // Verificar a presença (independente da ordem, embora a ordem seja esperada)
        assertTrue(produtosListados.stream().anyMatch(p -> p.getId().equals(produto1.getId())));
        assertTrue(produtosListados.stream().anyMatch(p -> p.getId().equals(produto2.getId())));
        assertTrue(produtosListados.stream().anyMatch(p -> p.getId().equals(produto3.getId())));
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
        assertEquals(produto.getId(), encontrado.getId()); // Comparar os IDs é mais direto
        assertEquals(produto.getNome(), encontrado.getNome());
        assertEquals(0, produto.getPreco().compareTo(encontrado.getPreco()));
    }

    @Test
    @DisplayName("Deve retornar null ao pesquisar produto com ID inexistente")
    void pesquisarPorId_QuandoProdutoNaoExiste_DeveRetornarNull() {
        Produto encontrado = dao.pesquisarPorId(999);
        assertNull(encontrado);
    }

    @Test
    @DisplayName("Deve retornar null ao pesquisar produto com ID nulo")
    void pesquisarPorId_QuandoIdNulo_DeveRetornarNull() {
        Produto encontrado = dao.pesquisarPorId(null);
        assertNull(encontrado);
    }

    @Test
    @DisplayName("Deve alterar os dados de um produto existente")
    void alterar_QuandoProdutoExiste_DeveAtualizarDadosERetornarTrue() {
        Produto produtoOriginal = new Produto(5, "Original", new BigDecimal("50.00"));
        dao.insere(produtoOriginal);

        Produto produtoAlterado = new Produto(5, "Alterado Nome", new BigDecimal("55.50"));
        boolean resultado = dao.alterar(produtoAlterado);

        assertTrue(resultado);
        Produto produtoVerificado = dao.pesquisarPorId(5);
        assertNotNull(produtoVerificado);
        assertEquals("Alterado Nome", produtoVerificado.getNome());
        assertEquals(0, new BigDecimal("55.50").compareTo(produtoVerificado.getPreco()));
        assertEquals(1, dao.listarTodos().size());
    }

    @Test
    @DisplayName("Não deve alterar produto e retornar false se ID não existe")
    void alterar_QuandoProdutoNaoExiste_DeveRetornarFalse() {
        Produto produtoNaoExistente = new Produto(99, "Inexistente", new BigDecimal("1.00"));
        boolean resultado = dao.alterar(produtoNaoExistente);
        assertFalse(resultado);
    }

    @Test
    @DisplayName("Deve retornar false ao tentar alterar produto nulo")
    void alterar_QuandoProdutoNulo_DeveRetornarFalse() {
        boolean resultado = dao.alterar(null);
        assertFalse(resultado);
    }

    @Test
    @DisplayName("Deve retornar false ao tentar alterar produto com ID nulo")
    void alterar_QuandoProdutoComIdNulo_DeveRetornarFalse() {
        Produto produtoComIdNulo = new Produto(null, "Alterar Sem ID", new BigDecimal("1.00"));
        boolean resultado = dao.alterar(produtoComIdNulo);
        assertFalse(resultado);
    }

    @Test
    @DisplayName("Deve excluir um produto existente e retornar true")
    void excluir_QuandoProdutoExiste_DeveRemoverDoMapaERetornarTrue() {
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

    @Test
    @DisplayName("Deve retornar false ao tentar excluir com ID nulo")
    void excluir_QuandoIdNulo_DeveRetornarFalse() {
        boolean resultado = dao.excluir(null);
        assertFalse(resultado);
    }
}