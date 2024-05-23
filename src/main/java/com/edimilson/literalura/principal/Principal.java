package com.edimilson.literalura.principal;

import com.edimilson.literalura.model.DadosApi;
import com.edimilson.literalura.model.DadosLivro;
import com.edimilson.literalura.service.ConsumoApi;
import com.edimilson.literalura.service.ConverteDados;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Principal {
    private final String URL_BASE = "https://gutendex.com/books/";
    private final Scanner leitura = new Scanner(System.in);
    private final ConsumoApi consumoApi = new ConsumoApi();
    private final ConverteDados conversor = new ConverteDados();
    private final List<DadosLivro> livros = new ArrayList<>();

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
                5) Listar livros em um determinado idioma
                0) Sair
                -----------------------------------------------
                """;
            System.out.println(menu);
            String opcao = leitura.nextLine();

            switch (opcao){
                case "0":
                    sair = true;
                    System.out.println("Você saiu.");
                    break;
                case "1":
                    System.out.println("Insira o nome do livro que você deseja procurar");
                    String nome = leitura.nextLine();
                    buscarLivro(nome);
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

    private void buscarLivro(String nome) {
        System.out.println("Buscando, aguarde...");
		String json = consumoApi.obterDados(URL_BASE + "?search=" + nome.replace(" ", "%20"));
		DadosApi dadosApi = conversor.obterDados(json, DadosApi.class);

        if(dadosApi.quantidade() == 0){
            System.out.println("Não foram encontrado livros nessa pesquisa");
        }else if(dadosApi.quantidade() == 1){
            obterLivro(dadosApi, 0);
        }else {
            System.out.println("Foram encontrado " + dadosApi.quantidade() + " livros nessa pesquisa, deseja ver todos esses livros? (S/N)");
            String opcao = leitura.nextLine();
            if(opcao.equalsIgnoreCase("s")){
                percorrerLivros(dadosApi);
            }
        }
    }

    private void percorrerLivros(DadosApi dadosApi){
        int proximo = dadosApi.livros().size();
        String json;
        int index = 0;

        for(int i = 1; i <= dadosApi.quantidade(); i++){
            if( i <= proximo ){
                obterLivro(dadosApi, index);
                index++;
            }else{
                proximo += dadosApi.livros().size();
                json = consumoApi.obterDados(dadosApi.proximo());
                dadosApi = conversor.obterDados(json, DadosApi.class);
                index = 0;
                obterLivro(dadosApi, index);
                index++;
            }
        }
    }

    private void obterLivro(DadosApi dadosApi, int index) {
        String json = consumoApi.obterDados(URL_BASE + dadosApi.livros().get(index).id() + "/");
        DadosLivro dadosLivro = conversor.obterDados(json, DadosLivro.class);
        livros.add(dadosLivro);
        imprimirLivro(dadosLivro);
    }

    private void imprimirLivro(DadosLivro dadosLivro){
        String autor = !dadosLivro.autor().isEmpty() ? String.valueOf(dadosLivro.autor().getFirst().nome()) : "Autor desconhecido";
        String livro = String.format("""
                --------------------------------
                Titulo: %s
                Autor: %s
                Idioma: %s
                Número de Downloads: %s
                --------------------------------
                """, dadosLivro.titulo(), autor, dadosLivro.idioma().getFirst(),dadosLivro.numeroDownloads());
        System.out.println(livro);
    }
}
