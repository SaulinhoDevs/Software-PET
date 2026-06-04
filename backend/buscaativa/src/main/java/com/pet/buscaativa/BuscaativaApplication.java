package com.pet.buscaativa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling //Necessário para atualização da lista de Unidades do BD
public class BuscaativaApplication {

	public static void main(String[] args) {
		SpringApplication.run(BuscaativaApplication.class, args);
	}

}
