package com.edimilson.literalura.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DadosLivro(Integer id,
                         @JsonAlias("title") String titulo,
                         @JsonAlias("languages") List<String> idioma,
                         @JsonAlias("authors") List<DadosAutor> autor,
                         @JsonAlias("download_count") Integer numeroDownloads) {
}
