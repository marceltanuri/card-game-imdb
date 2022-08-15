package com.tanuri.adaprova3.model;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.tanuri.adaprova3.scraper.ImbdScraper;

class ImdbScraperTest {

	static List<Carta> cartas = new ArrayList<>();

	@BeforeAll
	static void beforAll() throws IOException {
		cartas = ImbdScraper.extrair();
	}

	@Test
	void o_metodo_extrair_deve_retornar_uma_lista_de_cartas_com_conteudo_extraido_do_site_imdb_dot_com()
			throws IOException {
		assertTrue(!cartas.isEmpty());
	}

	@Test
	void o_metodo_extrair_deve_retornar_uma_lista_de_cartas_com_conteudo_extraido_do_site_imdb_dot_com__deve_conter_titulo_imdbid_rating()
			throws IOException {
		assertTrue(!cartas.get(0).getTitulo().isEmpty());
		assertTrue(!cartas.get(0).getImdbId().isEmpty());
		assertTrue(cartas.get(0).getRating().doubleValue() > 0);
	}

	@Test
	void o_valor_imdbId_deve_ser_formatado() throws IOException {
		for (Carta carta : cartas) {
			assertTrue(carta.getImdbId().matches("^[a-zA-Z]{2}[0-9]{7,12}$"));
		}
	}

}
