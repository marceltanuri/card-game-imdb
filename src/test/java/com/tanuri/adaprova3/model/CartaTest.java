package com.tanuri.adaprova3.model;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class CartaTest {

	@Test
	void o_metodo_validaImdb_deve_retornar_true_se_imdb_for_valido() {
		assertTrue(Carta.validaImdbId("tt0000001"));
		assertTrue(Carta.validaImdbId("tt00000012"));
		assertTrue(Carta.validaImdbId("tt000000123"));
	}

	@Test
	void o_metodo_validaImdb_deve_retornar_false_se_imdb_for_invalido() {
		assertTrue(!Carta.validaImdbId("/tt0000001"));
		assertTrue(!Carta.validaImdbId("tt0000001/"));
		assertTrue(!Carta.validaImdbId("tt00000"));
		assertTrue(!Carta.validaImdbId("000000001"));
	}

}
