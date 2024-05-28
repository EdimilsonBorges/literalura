package com.edimilson.literalura;

import com.edimilson.literalura.principal.Principal;
import com.edimilson.literalura.service.AutorService;
import com.edimilson.literalura.service.LivroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LiteraluraApplication implements CommandLineRunner {
    @Autowired
    LivroService livroService;
    @Autowired
    AutorService autorService;

    public static void main(String[] args) {
        SpringApplication.run(LiteraluraApplication.class, args);
    }

    @Override
    public void run(String... args) {
        Principal principal = new Principal(livroService, autorService);
        principal.exibeMenu();
    }
}
