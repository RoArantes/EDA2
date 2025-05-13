package org.example;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {
    private static Scanner s = new Scanner(System.in);
    private static ProdutoDAO dao = new ProdutoMemoryDAO();

    public static void main(String[] args) {
        int opcao = 0;
        while (opcao != 6) {
            System.out.println("\nCadastro de produtos");
            System.out.println("Digite a opção desejada");
            System.out.println("1 - Inserir");
            System.out.println("2 - Alterar");
            System.out.println("3 - Pesquisar");
            System.out.println("4 - Excluir");
            System.out.println("5 - Listar");
            System.out.println("6 - Sair");

            try {
                System.out.print("Opção: ");
                opcao = s.nextInt();
                s.nextLine(); // Consumir a nova linha

                switch (opcao) {
                    case 1 -> insere();
                    case 2 -> alterar();
                    case 3 -> pesquisar();
                    case 4 -> excluir(); // Adicionar case para excluir
                    case 5 -> listar();
                    case 6 -> System.out.println("Saindo do sistema...");
                    default -> System.out.println("Opção inválida. Tente novamente.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Entrada inválida. Por favor, digite um número.");
                s.nextLine(); // Limpar o buffer do scanner
                opcao = 0; // Resetar a opção para continuar no loop
            }
        }
        s.close();
        System.out.println("Sistema encerrado.");
    }

    private static void insere() {
        System.out.println("\n--- Inserindo Novo Produto ---");
        Integer id;
        try {
            System.out.print("Digite o ID do produto: ");
            id = s.nextInt();
            s.nextLine(); // Consumir nova linha
        } catch (InputMismatchException e) {
            System.out.println("ID inválido. Deve ser um número.");
            s.nextLine(); // Limpar buffer
            return;
        }

        System.out.print("Digite o nome do produto: ");
        String nome = s.nextLine();

        BigDecimal preco;
        try {
            System.out.print("Digite o preço do produto (ex: 10.99): ");
            String precoStr = s.nextLine();
            preco = new BigDecimal(precoStr);
        } catch (NumberFormatException e) {
            System.out.println("Preço inválido. Formato incorreto.");
            return;
        } catch (InputMismatchException e) { // Caso use s.nextBigDecimal()
            System.out.println("Preço inválido. Deve ser um número decimal.");
            s.nextLine(); // Limpar buffer
            return;
        }

        Produto novoProduto = new Produto(id, nome, preco);
        try {
            dao.insere(novoProduto);
            System.out.println("Produto inserido com sucesso!");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
        System.out.println("------------------------------");
    }

    private static void listar() {
        System.out.println("\n--- Lista de Produtos ---");
        Collection<Produto> produtos = dao.listarTodos();
        if (produtos.isEmpty()) {
            System.out.println("Nenhum produto cadastrado.");
        } else {
            produtos.forEach(System.out::println); // Maneira mais curta de imprimir
        }
        System.out.println("-------------------------");
    }

    private static void pesquisar() {
        System.out.println("\n--- Pesquisando Produto ---");
        System.out.print("Digite o ID do produto a ser pesquisado: ");
        Integer idBusca;
        try {
            idBusca = s.nextInt();
            s.nextLine(); // Consumir nova linha
        } catch (InputMismatchException e) {
            System.out.println("ID inválido. Deve ser um número.");
            s.nextLine(); // Limpar buffer
            return;
        }

        Produto produtoEncontrado = dao.pesquisarPorId(idBusca);

        if (produtoEncontrado != null) {
            System.out.println("Produto encontrado:");
            System.out.println(produtoEncontrado);
        } else {
            System.out.println("Produto com ID " + idBusca + " não encontrado.");
        }
        System.out.println("---------------------------");
    }

    private static void alterar() {
        System.out.println("\n--- Alterando Produto ---");
        System.out.print("Digite o ID do produto a ser alterado: ");
        Integer idAltera;
        try {
            idAltera = s.nextInt();
            s.nextLine(); // Consumir nova linha
        } catch (InputMismatchException e) {
            System.out.println("ID inválido. Deve ser um número.");
            s.nextLine(); // Limpar buffer
            return;
        }

        Produto produtoExistente = dao.pesquisarPorId(idAltera);

        if (produtoExistente == null) {
            System.out.println("Produto com ID " + idAltera + " não encontrado. Não é possível alterar.");
            return;
        }

        System.out.println("Produto encontrado: " + produtoExistente);
        System.out.print("Digite o novo nome do produto (atual: '" + produtoExistente.getNome() + "', deixe em branco para não alterar): ");
        String novoNome = s.nextLine();
        if (novoNome.trim().isEmpty()) { // Usar trim para verificar se está realmente vazio
            novoNome = produtoExistente.getNome();
        }

        BigDecimal novoPreco = produtoExistente.getPreco(); // Padrão é o preço existente
        System.out.print("Digite o novo preço do produto (atual: '" + produtoExistente.getPreco() + "', deixe em branco para não alterar): ");
        String novoPrecoStr = s.nextLine();
        if (!novoPrecoStr.trim().isEmpty()) {
            try {
                novoPreco = new BigDecimal(novoPrecoStr.replace(',', '.')); // Permitir vírgula como separador decimal
            } catch (NumberFormatException e) {
                System.out.println("Preço inválido. Formato incorreto. Preço não alterado.");
                // Mantém o novoPreco com o valor antigo
            }
        }

        Produto produtoAtualizado = new Produto(idAltera, novoNome, novoPreco);
        if (dao.alterar(produtoAtualizado)) {
            System.out.println("Produto alterado com sucesso!");
            System.out.println("Dados atualizados: " + produtoAtualizado);
        } else {
            System.out.println("Erro ao alterar o produto. Verifique se o ID existe (pouco provável se chegou aqui).");
        }
        System.out.println("-------------------------");
    }

    private static void excluir() {
        System.out.println("\n--- Excluindo Produto ---");
        System.out.print("Digite o ID do produto a ser excluído: ");
        Integer idExcluir;
        try {
            idExcluir = s.nextInt();
            s.nextLine(); // Consumir nova linha
        } catch (InputMismatchException e) {
            System.out.println("ID inválido. Deve ser um número.");
            s.nextLine(); // Limpar buffer
            return;
        }

        // Confirmação antes de excluir
        Produto produtoParaExcluir = dao.pesquisarPorId(idExcluir);
        if (produtoParaExcluir == null) {
            System.out.println("Produto com ID " + idExcluir + " não encontrado.");
            System.out.println("-------------------------");
            return;
        }

        System.out.println("Você tem certeza que deseja excluir o seguinte produto?");
        System.out.println(produtoParaExcluir);
        System.out.print("Digite 'S' para confirmar ou qualquer outra tecla para cancelar: ");
        String confirmacao = s.nextLine();

        if (confirmacao.equalsIgnoreCase("S")) {
            if (dao.excluir(idExcluir)) {
                System.out.println("Produto excluído com sucesso!");
            } else {
                // Esta mensagem não deveria aparecer se a pesquisa acima funcionou
                // e o remove do DAO foi implementado corretamente.
                System.out.println("Erro ao excluir o produto. Produto não encontrado (após confirmação).");
            }
        } else {
            System.out.println("Exclusão cancelada.");
        }
        System.out.println("-------------------------");
    }
}