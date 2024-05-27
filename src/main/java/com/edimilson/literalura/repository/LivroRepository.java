package com.edimilson.literalura.repository;

import com.edimilson.literalura.model.Livro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LivroRepository extends JpaRepository<Livro, Long> {

    @Query("SELECT l FROM Livro l WHERE l.idioma ILIKE %:idioma%")
    List<Livro> livrosEmDeterminadoIdioma(String idioma);

    @Query("SELECT l FROM Livro l ORDER BY l.numeroDownloads DESC LIMIT 10")
    List<Livro> top10LivrosMaisBaixados();
}
