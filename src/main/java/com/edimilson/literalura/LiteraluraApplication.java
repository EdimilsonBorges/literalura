package com.edimilson.literalura;

import com.edimilson.literalura.principal.Principal;
import com.edimilson.literalura.repository.AutorRepository;
import com.edimilson.literalura.repository.LivroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LiteraluraApplication implements CommandLineRunner {
    @Autowired
    LivroRepository livroRepository;

    @Autowired
    AutorRepository autorRepository;

    public static void main(String[] args) {
        SpringApplication.run(LiteraluraApplication.class, args);
    }

    @Override
    public void run(String... args) {
        Principal principal = new Principal(livroRepository, autorRepository);
        principal.exibeMenu();
    }
}
