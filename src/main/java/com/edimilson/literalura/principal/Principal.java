package com.edimilson.literalura.principal;

import com.edimilson.literalura.model.DadosApi;
import com.edimilson.literalura.model.DadosAutor;
import com.edimilson.literalura.model.DadosLivro;
import com.edimilson.literalura.service.ConsumoApi;
import com.edimilson.literalura.service.ConverteDados;

import java.util.*;

public class Principal {
    private final String URL_BASE = "https://gutendex.com/books/";
    private final Scanner leitura = new Scanner(System.in);
    private final ConsumoApi consumoApi = new ConsumoApi();
    private final ConverteDados conversor = new ConverteDados();
    private final Set<DadosLivro> livros = new HashSet<>();
    private final Set<DadosAutor> autores = new HashSet<>();

    public void exibeMenu(){

        while (true){
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

            if(opcao.equals("0")){
                System.out.println("Você saiu.");
                break;
            }

            switch (opcao){
                case "1":
                    buscarLivro();
                    break;
                case "2":
                    listarLivrosRegistrados();
                    break;
                case "3":
                    listarAutoresRegistrados();
                    break;
                case "4":
                    listarAutorVivoEmDeterminadoAno();
                    break;
                case "5":
                    listarLivrosEmDeterminadoIdioma();
                    break;
                default:
                    System.out.println("Opção inválida digite de 0 a 5");
            }
        }
    }

    private boolean isAnoValidado(String anoLido) {
        if(anoLido.length() == 4){
            try {
                Integer.parseInt(anoLido);
                return true;
            }catch (Exception e){
                return false;
            }
        }
       return false;
    }

    private void buscarLivro() {
        System.out.println("Insira o nome do livro que você deseja procurar");
        String nome = leitura.nextLine();
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
        if( !dadosLivro.autor().isEmpty() &&
                !dadosLivro.autor().getFirst().nome().equalsIgnoreCase("Anonymous") &&
                !dadosLivro.autor().getFirst().nome().equalsIgnoreCase("Unknown") &&
                !dadosLivro.autor().getFirst().nome().equalsIgnoreCase("Various")){

            autores.add(dadosLivro.autor().getFirst());
        }
        imprimirLivro(dadosLivro);
    }

    private void listarLivrosRegistrados(){
        if(livros.isEmpty()){
            System.out.println("Não há livros registrados");
            return;
        }
        livros.forEach(this::imprimirLivro);
    }

    private void listarAutoresRegistrados(){
        if(autores.isEmpty()){
            System.out.println("Não há autores registrados");
            return;
        }
        autores.forEach(this::imprimirAutor);
    }

    private void listarAutorVivoEmDeterminadoAno() {

        System.out.println("Insira o ano que deseja pesquisar");
        String anoLido = leitura.nextLine();

        if(isAnoValidado(anoLido)){
            List<DadosAutor> autoresEncontrados = autores.stream()
                    .filter(a -> a.anoNascimento() != null && a.anoFalecimento() != null &&  a.anoNascimento() < Integer.parseInt(anoLido) && a.anoFalecimento() >  Integer.parseInt(anoLido))
                    .toList();

            if(autoresEncontrados.isEmpty()){
                System.out.println("Não foram encontrados autores cadastrados vivos entre esse periodo");
                return;
            }
            autoresEncontrados.forEach(this::imprimirAutor);
        }else{
            System.out.println("Ano inválido, digite 4 números ex: 1930");
        }
    }
    private void listarLivrosEmDeterminadoIdioma() {

        String menuIdioma = """
                            
                            Insira o idioma para realizar a busca:
                            es- Espanhou
                            en- Inglês
                            fr- Francês
                            pt- Português
                            """;
        System.out.println(menuIdioma);
        String idioma = leitura.nextLine();

        if(!idioma.equalsIgnoreCase("es") &&
                !idioma.equalsIgnoreCase("en") &&
                !idioma.equalsIgnoreCase("fr") &&
                !idioma.equalsIgnoreCase("pt")){

            System.out.println("Opção inválida, São aceitos apenas os idiomas (es), (en), (fr) ou (pt)");
            return;
        }
        List<DadosLivro> livrosFiltrados = livros.stream()
                .filter(l -> l.idioma().getFirst().equalsIgnoreCase(idioma))
                .toList();

        if(livrosFiltrados.isEmpty()){
            System.out.println("Não foram encontrados livros neste idioma");
            return;
        }
        livrosFiltrados.forEach(this::imprimirLivro);
    }

    private void imprimirLivro(DadosLivro dadosLivro){
        String nomeAutor = !dadosLivro.autor().isEmpty() ? String.valueOf(dadosLivro.autor().getFirst().nome()) : "Autor desconhecido";
        String livro = String.format("""
                --------------------------------
                Titulo: %s
                Autor: %s
                Idioma: %s
                Número de Downloads: %s
                --------------------------------
                """, dadosLivro.titulo(), nomeAutor, dadosLivro.idioma().getFirst(),dadosLivro.numeroDownloads());
        System.out.println(livro);
    }

    private void imprimirAutor(DadosAutor dadosAutor){
        String nomeAutor = !dadosAutor.nome().isEmpty() ? dadosAutor.nome() : "Autor desconhecido";
        String autor = String.format("""
                --------------------------------
                Autor: %s
                Ano de nascimento: %s
                Ano de falecimento: %s
                --------------------------------
                """, nomeAutor, dadosAutor.anoNascimento(),dadosAutor.anoFalecimento());
        System.out.println(autor);
    }
}
