package com.tanuri.adaprova3.model;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.tanuri.adaprova3.repository.PartidaRepository;

@ExtendWith(MockitoExtension.class)
class RodadaTest {

	@Mock
	PartidaRepository partidaRepository;

	@SuppressWarnings("deprecation")
	@Test
	void o_metodo_acertou_deve_dizer_se_a_escolha_feita_pelo_jogador_esta_correta() {

		Set<Carta> cartas = new HashSet<>();
		cartas.add(new Carta("tt0000001", 7.8));
		Carta cartaDeMaiorRating = new Carta("tt0000002", 9.6);
		cartas.add(cartaDeMaiorRating);
		Rodada subject = new Rodada(cartas, cartaDeMaiorRating.getImdbId(), null);

		assertTrue(subject.acertou());
	}

	@SuppressWarnings("deprecation")
	@Test
	void o_metodo_acertou_deve_dizer_se_a_escolha_feita_pelo_jogador_esta_incorreta() {

		Set<Carta> cartas = new HashSet<>();
		Carta cartaDeMenorRating = new Carta("tt0000001", 7.8);
		cartas.add(new Carta("tt0000002", 9.6));
		cartas.add(cartaDeMenorRating);
		Rodada subject = new Rodada(cartas, cartaDeMenorRating.getImdbId(), null);

		assertTrue(!subject.acertou());
	}

	@SuppressWarnings("deprecation")
	@Test
	void o_metodo_acertou_deve_retornar_true_em_caso_de_empate() {

		Set<Carta> cartas = new HashSet<>();
		cartas.add(new Carta("tt0000001", 9.6));
		cartas.add(new Carta("tt0000002", 9.6));

		Rodada subject = new Rodada(cartas, "tt0000001", null);
		assertTrue(subject.acertou());

		subject = new Rodada(cartas, "tt0000002", null);
		assertTrue(subject.acertou());
	}

	@SuppressWarnings("deprecation")
	@Test
	void o_metodo_acertou_deve_funcionar_em_diferentes_configuracoes_de_numero_de_carta() {

		// Rodada om 5 cartas a escolha
		Set<Carta> cartas = new HashSet<>();
		cartas.add(new Carta("tt0000001", 9.6));
		cartas.add(new Carta("tt0000002", 9.6));
		cartas.add(new Carta("tt0000003", 4.2));
		cartas.add(new Carta("tt0000004", 6.2));
		cartas.add(new Carta("tt0000005", 2.8));

		int numeroDeCartas = 5;
		Rodada subject = new Rodada(cartas, "tt0000001", numeroDeCartas);
		assertTrue(subject.acertou());

		subject = new Rodada(cartas, "tt0000002", numeroDeCartas);
		assertTrue(subject.acertou());
	}

}
