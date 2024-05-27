package com.edimilson.literalura.model;

import com.fasterxml.jackson.annotation.JsonAlias;

import java.util.List;

public record DadosApi(@JsonAlias("count") int quantidade,
                       @JsonAlias("next") String proximo,
                       @JsonAlias("previous") String anterior,
                       @JsonAlias("results") List<DadosLivro> livros) {
}
