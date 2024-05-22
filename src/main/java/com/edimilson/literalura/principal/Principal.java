package com.edimilson.literalura.principal;

import java.util.Scanner;

public class Principal {
    private Scanner leitura = new Scanner(System.in);
    private final String URL_BASE = "https://gutendex.com/books/?search=";

    public void exibeMenu(){

        boolean sair = false;

        while (!sair){
            String menu = """
                ----------------------------------------------
                Escolha um número de sua opção:
                1) Buscar livro pelo titulo
                2) Listar livos registrados
                3) Listar autores registrados
                4) Listar autores vivos em um determinado ano
                5) Listar livros em um determinado ano
                0) Sair
                -----------------------------------------------
                """;
            System.out.println(menu);
            String opçao = leitura.nextLine();

            switch (opçao){
                case "0":
                    sair = true;
                    System.out.println("Você saiu.");
                    break;
                case "1":
                    System.out.println("Insira o nome do livro que você deseja procurar");
                    break;
                case "2":
                    break;
                case "3":
                    break;
                case "4":
                    System.out.println("Insira o ano que deseja pesquisar");
                    break;
                case "5":
                    String menuIdioma = """
                            
                            Insira o idioma para realizar a busca:
                            es- Espanhou
                            en- Inglês
                            fr- Francês
                            pt- Português
                            """;
                    System.out.println(menuIdioma);
                    break;
                default:
                    System.out.println("Opção inválida digite de 0 a 5");
            }
        }
    }
}
