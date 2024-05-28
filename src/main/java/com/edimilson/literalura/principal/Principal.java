package com.edimilson.literalura.principal;

import com.edimilson.literalura.service.AutorService;
import com.edimilson.literalura.service.LivroService;

import java.util.*;

public class Principal {
    private Scanner leitura = new Scanner(System.in);
    private LivroService livroService;
    private AutorService autorService;

    public Principal(LivroService livroService, AutorService autorService) {
        this.livroService = livroService;
        this.autorService = autorService;
    }

    public void exibeMenu() {

        while (true) {
            String menu = """
                    ----------------------------------------------
                    Escolha um número de sua opção:
                    1) Buscar livro pelo titulo ou por Autor
                    2) Listar livos registrados
                    3) Listar autores registrados
                    4) Listar autores vivos em um determinado ano
                    5) Listar livros em um determinado idioma
                    6) Listar os 10 livros mais baixados;
                    7) Listar autor por nome
                    0) Sair
                    -----------------------------------------------
                    """;
            System.out.println(menu);
            String opcao = leitura.nextLine();

            if (opcao.equals("0")) {
                System.out.println("Você saiu.");
                break;
            }

            switch (opcao) {
                case "1":
                    livroService.buscarLivro();
                    break;
                case "2":
                    livroService.listarLivrosRegistrados();
                    break;
                case "3":
                    autorService.listarAutoresRegistrados();
                    break;
                case "4":
                    autorService.listarAutorVivoEmDeterminadoAno();
                    break;
                case "5":
                    livroService.listarLivrosEmDeterminadoIdioma();
                    break;
                case "6":
                    livroService.listarTop10LivrosMaisBaixados();
                    break;
                case "7":
                    autorService.listarAutorPorNome();
                    break;
                default:
                    System.out.println("Opção inválida digite de 0 a 7");
            }
        }
    }
}