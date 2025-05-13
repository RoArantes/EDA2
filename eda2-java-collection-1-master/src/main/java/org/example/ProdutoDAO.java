package org.example;

import java.util.Collection;

public interface ProdutoDAO {
    void insere(Produto produto);
    Collection<Produto> listarTodos();
    Produto pesquisarPorId(Integer id);
    boolean alterar(Produto produto);
    boolean excluir(Integer id); // Novo método
}