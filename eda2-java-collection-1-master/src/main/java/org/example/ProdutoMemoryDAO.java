package org.example;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.TreeSet;

public class ProdutoMemoryDAO implements ProdutoDAO {
    private Collection<Produto> produtos = new TreeSet<>();

    @Override
    public void insere(Produto produto) {
        if (pesquisarPorId(produto.getId()) == null) {
            produtos.add(produto);
        } else {
            throw new IllegalArgumentException("Erro: Já existe um produto com o ID " + produto.getId() + ".");
        }
    }

    @Override
    public Collection<Produto> listarTodos() {
        return produtos;
    }

    @Override
    public Produto pesquisarPorId(Integer id) {
        for (Produto p : produtos) {
            if (p.getId().equals(id)) {
                return p;
            }
        }
        return null;
    }

    @Override
    public boolean alterar(Produto produtoAtualizado) {
        Produto produtoExistente = pesquisarPorId(produtoAtualizado.getId());
        if (produtoExistente != null) {
            produtoExistente.setNome(produtoAtualizado.getNome());
            produtoExistente.setPreco(produtoAtualizado.getPreco());
            return true;
        }
        return false;
    }

    @Override
    public boolean excluir(Integer id) {
        return produtos.remove(new Produto(id, null, null));
    }
}