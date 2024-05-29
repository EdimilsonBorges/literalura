package com.edimilson.literalura.service;

import com.edimilson.literalura.model.*;
import com.edimilson.literalura.repository.LivroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class LivroService {
    @Autowired
    LivroRepository livroRepository;
    private static final String URL_BASE = "https://gutendex.com/books/";
    private ConsumoApi consumoApi = new ConsumoApi();
    private ConverteDados conversor = new ConverteDados();
    private Scanner leitura = new Scanner(System.in);

    public void buscarLivro() {
        System.out.println("Insira o nome do livro que você deseja procurar e salvar no banco de dados.");
        String nome = leitura.nextLine();
        System.out.println("Buscando, aguarde...");
        String json = consumoApi.obterDados(URL_BASE + "?search=" + nome.replace(" ", "%20"));
        DadosApi dadosApi = conversor.obterDados(json, DadosApi.class);

        if (dadosApi.quantidade() == 0) {
            System.out.println("Não foram encontrado livros nessa pesquisa");
        } else if (dadosApi.quantidade() == 1) {
            obterLivro(dadosApi);
        } else {
            System.out.println("Foram encontrado " + dadosApi.quantidade() + " livros nessa pesquisa, deseja ver todos esses livros e salvar no banco de dados? (S/N)");
            String opcao = leitura.nextLine();
            if (opcao.equalsIgnoreCase("s")) {
                obterTodosLivros(dadosApi);
            }
        }
    }

    private void obterLivro(DadosApi dadosApi) {

        List<Autor> autores = dadosApi.livros().stream().flatMap(l -> l.autores().stream()).map(Autor::new).toList();

        dadosApi.livros().stream()
                .distinct()
                .forEach(l -> {
            List<Autor> livroAutores = l.autores().stream()
                    .map(a -> autores.stream()
                            .filter(existingAutor -> existingAutor.getNome().equals(a.nome()))
                            .findFirst().orElseThrow(() -> new IllegalArgumentException("Autor não encontrado: " + a.nome())))
                    .collect(Collectors.toList());
            Livro livro = new Livro(new DadosLivro(l.titulo(), l.idioma(), l.autores(), l.numeroDownloads()));
            livro.setAutores(livroAutores);
            try {
                livroRepository.save(livro);
                System.out.println(livro);
            } catch (DataIntegrityViolationException e) {
                System.out.println(livro);
                System.err.println("Erro ao salvar o livro "+livro.getTitulo()+", este livro já existe no banco de dados.");
            } catch (InvalidDataAccessApiUsageException e) {
                System.err.println("Erro. Entidade desanexada passada para persistir.");
            }
        });

    }

    private void obterTodosLivros(DadosApi dadosApi) {
        boolean proximo = true;
        String json;

        for (int i = 1; i <= dadosApi.quantidade(); i++) {
            if (proximo) {
                proximo = false;
                obterLivro(dadosApi);
            } else if (dadosApi.proximo() == null) {
                break;
            } else {
                System.out.println("Buscando, aguarde...");
                json = consumoApi.obterDados(dadosApi.proximo());
                dadosApi = conversor.obterDados(json, DadosApi.class);
                obterLivro(dadosApi);
            }
        }
    }

    public void listarLivrosRegistrados() {
        System.out.println("Buscando, aguarde...");
        List<Livro> livros = livroRepository.findAll();
        if (livros.isEmpty()) {
            System.out.println("Não há livros registrados");
            return;
        }
        if (livros.size() <= 3) {
            livros.forEach(System.out::println);
        } else {
            System.out.println("Foram encontrado " + livros.size() + " livros no banco de dados deseja ver todos esses livros? (S/N)");
            String opcao = leitura.nextLine();
            if (opcao.equalsIgnoreCase("s")) {
                livros.forEach(System.out::println);
            }
        }
    }

    public void listarLivrosEmDeterminadoIdioma() {

        String menuIdioma = """
                                            
                Insira o idioma para realizar a busca:
                es- Espanhou
                en- Inglês
                fr- Francês
                pt- Português
                """;
        System.out.println(menuIdioma);
        String idioma = leitura.nextLine();

        if (!idioma.equalsIgnoreCase("es") && !idioma.equalsIgnoreCase("en") && !idioma.equalsIgnoreCase("fr") && !idioma.equalsIgnoreCase("pt")) {

            System.out.println("Opção inválida, São aceitos apenas os idiomas (es), (en), (fr) ou (pt)");
            return;
        }
        List<Livro> livrosFiltrados = livroRepository.livrosEmDeterminadoIdioma(idioma);

        if (livrosFiltrados.isEmpty()) {
            System.out.println("Não foram encontrados livros neste idioma");
            return;
        }
        livrosFiltrados.forEach(System.out::println);
    }

    public void listarTop10LivrosMaisBaixados() {

        System.out.println("10 mais populares da API ou do Banco de dados? (A/B)");
        String opcao = leitura.nextLine();

        if (opcao.equalsIgnoreCase("a")) {
            listarTop10Api();
        } else if (opcao.equalsIgnoreCase("b")) {
            listarTop10BancoDeDados();
        } else {
            System.out.println("Opção inválida, São aceitos apenas (A), (B)");
        }
    }

    private void listarTop10Api() {
        System.out.println("Buscando, aguarde...");
        String json = consumoApi.obterDados(URL_BASE + "?sort=popular");
        DadosApi dadosApi = conversor.obterDados(json, DadosApi.class);

        if (dadosApi.quantidade() == 0) {
            System.out.println("Não foram encontrado livros nessa pesquisa");
        } else {
            obter10LivrosMaisPopulares(dadosApi);
        }
    }

    private void obter10LivrosMaisPopulares(DadosApi dadosApi) {
        List<Livro> livrosFiltrados = dadosApi.livros().stream().limit(10).map(l -> new Livro(new DadosLivro(l.titulo(), l.idioma(), l.autores(), l.numeroDownloads()))).toList();
        livrosFiltrados.forEach(System.out::println);
    }

    private void listarTop10BancoDeDados() {
        List<Livro> livros = livroRepository.top10LivrosMaisBaixados();
        if (livros.isEmpty()) {
            System.out.println("Não foram encontrado nenhum livro no banco de dados");
            return;
        }
        livros.forEach(System.out::println);
    }
}