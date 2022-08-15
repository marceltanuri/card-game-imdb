package com.tanuri.adaprova3;

import java.io.IOException;
import java.util.stream.IntStream;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.tanuri.adaprova3.model.Carta;
import com.tanuri.adaprova3.model.Jogador;
import com.tanuri.adaprova3.repository.CartaRepository;
import com.tanuri.adaprova3.repository.JogadorRepository;
import com.tanuri.adaprova3.scraper.ImbdScraper;

@Component
public class DataLoader implements ApplicationRunner {

	private CartaRepository cartaRepository;

	private JogadorRepository jogadorRepository;

	public DataLoader(CartaRepository cartaRepository, JogadorRepository jogadorRepository) {
		this.cartaRepository = cartaRepository;
		this.jogadorRepository = jogadorRepository;
	}

	public void run(ApplicationArguments args) throws IOException {
		
		for (Carta carta : ImbdScraper.extrair()) {
			cartaRepository.save(carta);
		}

		jogadorRepository.save(new Jogador("user"));

		IntStream.range(0, 12).forEachOrdered(n -> {
			jogadorRepository.save(new Jogador("user" + (n + 1 * 1)));
		});

	}

}
