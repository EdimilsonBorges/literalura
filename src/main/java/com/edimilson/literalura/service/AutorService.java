package com.edimilson.literalura.service;

import com.edimilson.literalura.model.Autor;
import com.edimilson.literalura.repository.AutorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Scanner;

@Service
public class AutorService {
    @Autowired
    AutorRepository autorRepository;
    private Scanner leitura = new Scanner(System.in);

    private boolean isAnoValidado(String anoLido) {
        if (anoLido.length() == 4) {
            try {
                Integer.parseInt(anoLido);
                return true;
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }

    public void listarAutoresRegistrados() {
        List<Autor> autores = autorRepository.findAll();
        if (autores.isEmpty()) {
            System.out.println("Não há autores registrados");
            return;
        }
        autores.forEach(System.out::println);
    }

    public void listarAutorVivoEmDeterminadoAno() {

        System.out.println("Insira o ano que deseja pesquisar");
        String anoLido = leitura.nextLine();

        if (isAnoValidado(anoLido)) {
            List<Autor> autoresFiltados = autorRepository.listaAutoresVivosEmDeterminadoAno(Integer.parseInt(anoLido));
            if (autoresFiltados.isEmpty()) {
                System.out.println("Não foram encontrados autores cadastrados vivos entre esse periodo");
                return;
            }
            autoresFiltados.forEach(System.out::println);
        } else {
            System.out.println("Ano inválido, digite 4 números ex: 1930");
        }
    }

    public void listarAutorPorNome() {
        System.out.println("Qual é o nome do autor que você deseja pesquisar?");
        String nome = leitura.nextLine();
        List<Autor> autor = autorRepository.listarAutorPorNome(nome);
        if (!autor.isEmpty()) {
            autor.forEach(System.out::println);
        } else {
            System.out.println("Autor não encontrado no Banco de Dados.");
        }
    }
}
