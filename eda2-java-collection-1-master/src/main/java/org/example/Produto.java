package org.example;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString; // Adicione esta importação

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString // Adicione esta anotação para gerar o toString automaticamente
public class Produto implements Comparable<Produto> { // Boa prática especificar o tipo genérico

    @EqualsAndHashCode.Include
    private Integer id;

    private String nome; // Renomeei de 'produto' para 'nome' para clareza

    private BigDecimal preco;

    @Override
    public int compareTo(Produto outroProduto) { // Especificando o tipo
        return id.compareTo(outroProduto.id);
    }

    // Removido o compareTo(Object o) genérico, pois o Comparable<Produto> é mais seguro
    // Se precisar manter a compatibilidade com Object por algum motivo específico,
    // pode manter o anterior, mas o genérico é preferível.
}