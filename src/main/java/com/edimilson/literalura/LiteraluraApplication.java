package com.edimilson.literalura;

import com.edimilson.literalura.model.DadosApi;
import com.edimilson.literalura.model.DadosLivro;
import com.edimilson.literalura.service.ConsumoApi;
import com.edimilson.literalura.service.ConverteDados;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class LiteraluraApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(LiteraluraApplication.class, args);
	}

	@Override
	public void run(String... args) {
		ConsumoApi consumoApi = new ConsumoApi();
		String json = consumoApi.obterDados("https://gutendex.com/books/?search=Swanston+Edition");

		ConverteDados conversor = new ConverteDados();
		DadosApi dadosApi = conversor.obterDados(json, DadosApi.class);

		List<DadosLivro> livros = new ArrayList<>();
		for(int i = 1; i <= dadosApi.quantidade(); i++){
			json = consumoApi.obterDados("https://gutendex.com/books/"+i+"/");
			DadosLivro dadosLivro = conversor.obterDados(json, DadosLivro.class);
			livros.add(dadosLivro);
		}

		livros.forEach(System.out::println);

	}
}
