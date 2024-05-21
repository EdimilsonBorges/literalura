package com.edimilson.literalura.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DadosApi(@JsonAlias("count") int quantidade,
                       @JsonAlias("next") String proximo,
                       @JsonAlias("previous") String anterior,
                       @JsonAlias("results") List<DadosLivro> livros){
}
