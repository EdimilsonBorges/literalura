package com.edimilson.literalura.principal;

import com.edimilson.literalura.model.*;
import com.edimilson.literalura.repository.AutorRepository;
import com.edimilson.literalura.repository.LivroRepository;
import com.edimilson.literalura.service.ConsumoApi;
import com.edimilson.literalura.service.ConverteDados;
import jakarta.transaction.Transactional;

import java.util.*;
import java.util.stream.Collectors;

public class Principal {
    private final String URL_BASE = "https://gutendex.com/books/";
    private final Scanner leitura = new Scanner(System.in);
    private final ConsumoApi consumoApi = new ConsumoApi();
    private final ConverteDados conversor = new ConverteDados();
    private List<Livro> livros = new ArrayList<>();
    private LivroRepository livroRepository;

    private AutorRepository autorRepository;

    public Principal(LivroRepository livroRepository, AutorRepository autorRepository){
        this.livroRepository = livroRepository;
        this.autorRepository = autorRepository;
    }

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
            obterLivro(dadosApi);
        }else {
            System.out.println("Foram encontrado " + dadosApi.quantidade() + " livros nessa pesquisa, deseja ver todos esses livros? (S/N)");
            String opcao = leitura.nextLine();
            if(opcao.equalsIgnoreCase("s")){
                obterTodosLivros(dadosApi);
            }
        }
    }

    private void obterTodosLivros(DadosApi dadosApi){
        boolean proximo = true;
        String json;

        for(int i = 1; i <= dadosApi.quantidade(); i++){
            if(proximo){
                proximo = false;
                obterLivro(dadosApi);
            }else if(dadosApi.proximo() == null){
                break;
            }else{
                System.out.println("Buscando, aguarde...");
                json = consumoApi.obterDados(dadosApi.proximo());
                dadosApi = conversor.obterDados(json, DadosApi.class);
                obterLivro(dadosApi);
            }
        }
    }
    private void obterLivro(DadosApi dadosApi) {

        List<Autor> autores = dadosApi.livros().stream()
                .flatMap(l -> l.autores().stream())
                .map(a -> new Autor(new DadosAutor(a.getNome(), a.getAnoNascimento(), a.getAnoFalecimento())))
                .toList();

        Set<Livro> livros = dadosApi.livros().stream()
                .map(l -> {
                    List<Autor> livroAutores = l.autores().stream()
                            .map(a -> autores.stream()
                                    .filter(existingAutor -> existingAutor.getNome().equals(a.getNome()))
                                    .findFirst()
                                    .orElseThrow(() -> new IllegalArgumentException("Autor não encontrado: " + a.getNome())))
                            .collect(Collectors.toList());
                    Livro livro = new Livro(new DadosLivro(l.titulo(), l.idioma(), livroAutores, l.numeroDownloads()));
                    livro.setAutores(livroAutores);
                    return livro;
                })
                .collect(Collectors.toSet());

        livroRepository.saveAll(livros);
        livros.forEach(System.out::println);
    }

    private void listarLivrosRegistrados(){
        livros =  livroRepository.findAll();
        if(livros.isEmpty()){
            System.out.println("Não há livros registrados");
            return;
        }
        livros.forEach(System.out::println);
    }

    private void listarAutoresRegistrados(){
        List<Autor> autores = autorRepository.findAll();
        if(autores.isEmpty()){
            System.out.println("Não há autores registrados");
            return;
        }
        autores.forEach(System.out::println);
    }

    private void listarAutorVivoEmDeterminadoAno() {

        System.out.println("Insira o ano que deseja pesquisar");
        String anoLido = leitura.nextLine();

        if(isAnoValidado(anoLido)){
            List<Autor> autores = autorRepository.listaAutoresVivosEmDeterminadoAno(Integer.parseInt(anoLido));
            if(autores.isEmpty()){
                System.out.println("Não foram encontrados autores cadastrados vivos entre esse periodo");
                return;
            }
            autores.forEach(System.out::println);
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
        List<Livro> livrosFiltrados = livros.stream()
                .filter(l -> l.getIdioma().equalsIgnoreCase(idioma))
                .toList();

        if(livrosFiltrados.isEmpty()){
            System.out.println("Não foram encontrados livros neste idioma");
            return;
        }
        livrosFiltrados.forEach(System.out::println);
    }

}
