package com.edimilson.literalura.repository;

import com.edimilson.literalura.model.Autor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AutorRepository extends JpaRepository<Autor, Long> {

    @Query("SELECT a FROM Autor a WHERE :ano > a.anoNascimento AND :ano < a.anoFalecimento")
    List<Autor> listaAutoresVivosEmDeterminadoAno(Integer ano);
}
