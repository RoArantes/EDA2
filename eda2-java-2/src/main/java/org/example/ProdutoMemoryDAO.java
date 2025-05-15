package org.example;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Map; // Importar Map
import java.util.TreeMap; // Importar TreeMap
import java.util.ArrayList; // Para retornar cópia da coleção de valores

public class ProdutoMemoryDAO implements ProdutoDAO {
    // Alterar de TreeSet para TreeMap
    // A chave é o ID do produto (Integer), o valor é o objeto Produto.
    private final Map<Integer, Produto> produtos = new TreeMap<>();

    @Override
    public void insere(Produto produto) {
        if (produto == null || produto.getId() == null) {
            throw new IllegalArgumentException("Produto ou ID do produto não pode ser nulo.");
        }
        // Verificar se a chave (ID) já existe no TreeMap
        if (produtos.containsKey(produto.getId())) {
            throw new IllegalArgumentException("Erro: Já existe um produto com o ID " + produto.getId() + ".");
        }
        produtos.put(produto.getId(), produto);
    }

    @Override
    public Collection<Produto> listarTodos() {
        // O método values() retorna uma Collection com todos os valores (Produtos).
        // Retornamos uma nova ArrayList para garantir que a coleção interna não seja modificada externamente
        // e para fornecer uma Collection que é comumente utilizável (ex: para iteração).
        // Os produtos estarão ordenados pela chave (ID) devido ao TreeMap.
        return new ArrayList<>(produtos.values());
    }

    @Override
    public Produto pesquisarPorId(Integer id) {
        if (id == null) return null;
        // O método get(key) retorna o valor associado à chave, ou null se a chave não existir.
        // Poderia retornar uma cópia se quisesse imutabilidade do objeto retornado, mas para 'alterar' funcionar
        // da forma que modifica o objeto diretamente, precisamos da referência.
        // Ex: return new Produto(produtos.get(id).getId(), produtos.get(id).getNome(), produtos.get(id).getPreco());
        return produtos.get(id);
    }

    @Override
    public boolean alterar(Produto produtoAtualizado) {
        if (produtoAtualizado == null || produtoAtualizado.getId() == null) {
            return false; // Ou lançar exceção
        }
        // Verificar se o produto (baseado no ID/chave) existe para ser alterado.
        if (produtos.containsKey(produtoAtualizado.getId())) {
            // O ID (chave) não deve ser alterado. Apenas os atributos do valor (Produto).
            // Simplesmente substituímos o valor antigo pelo novo com o mesmo ID.
            produtos.put(produtoAtualizado.getId(), produtoAtualizado);
            return true;
        }
        return false; // Produto não encontrado para alteração
    }

    @Override
    public boolean excluir(Integer id) {
        if (id == null) return false;
        // O método remove(key) remove a entrada pela chave e retorna o valor removido,
        // ou null se a chave não existia.
        // Podemos verificar se o valor retornado é não nulo para confirmar a exclusão.
        return produtos.remove(id) != null;
    }
}