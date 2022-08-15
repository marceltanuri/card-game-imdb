package com.tanuri.adaprova3.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.lenient;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.tanuri.adaprova3.model.exceptions.FaltaDeCartasException;
import com.tanuri.adaprova3.model.exceptions.ImdbInvalidoException;
import com.tanuri.adaprova3.model.exceptions.PartidaException;
import com.tanuri.adaprova3.repository.CartaRepository;
import com.tanuri.adaprova3.repository.PartidaRepository;
import com.tanuri.adaprova3.repository.RodadaRepository;

@ExtendWith(MockitoExtension.class)
class PartidaTest {

	private static final int CARTAS_POR_RODADA = 2;

	@Mock
	PartidaRepository partidaRepository;

	@Mock
	CartaRepository cartaRepository;

	@Mock
	RodadaRepository rodadaRepository;

	private List<Carta> cartas;

	private Partida partida;

	@SuppressWarnings("deprecation")
	@BeforeEach
	public void initEach() {
		cartas = new ArrayList<>(2);
		cartas.add(new Carta("tt0000001", 9.6));
		cartas.add(new Carta("tt0000002", 9.5));
		cartas.add(new Carta("tt0000003", 2.4));
		cartas.add(new Carta("tt0000004", 8.7));
		cartas.add(new Carta("tt0000005", 5.3));
		cartas.add(new Carta("tt0000006", 8.9));
		cartas.add(new Carta("tt0000007", 6.2));
		cartas.add(new Carta("tt0000008", 5.1));
		cartas.add(new Carta("tt0000009", 4.6));
		lenient().when(cartaRepository.findAll()).thenReturn(cartas);
	}

	@Test
	void o_construtor_da_partida_deve_iniciar_nova_partida__numero_da_primeira_jogada_deve_ser_1()
			throws PartidaException, FaltaDeCartasException {
		Partida partida = new Partida(new Jogador("user1"), cartas);
		assertEquals(1, partida.getRodadaAtual().getNumeroDaRodada());
	}

	@SuppressWarnings("deprecation")
	@Test
	void o_metodo_registrar_resposta_deve_guardar_o_imdbid_escolhido_no_objeto_rodada() throws Exception {
		cartas = new ArrayList<>(2);
		cartas.add(new Carta("tt0000001", 9.6));
		cartas.add(new Carta("tt0000002", 9.5));
		Partida partida = new Partida(new Jogador("user1"), cartas);
		partida.registrarResposta("tt0000001");
		assertEquals("tt0000001", partida.getRodadaAtual().getImdbEscolhido());
	}

	@SuppressWarnings("deprecation")
	@Test
	void o_metodo_registrar_resposta_deve_validar_o_imdb_informado_e_lancar_uma_ImdbInvalidoException_se_for_invalido()
			throws Exception {
		cartas = new ArrayList<>(2);
		cartas.add(new Carta("tt0000001", 9.6));
		cartas.add(new Carta("tt0000002", 9.5));
		Partida partida = new Partida(new Jogador("user1"), cartas);

		ImdbInvalidoException thrown = Assertions.assertThrows(ImdbInvalidoException.class, () -> {
			partida.registrarResposta("xtt0000001");
		});
		Assertions.assertTrue(!thrown.getMessage().isEmpty());
		Assertions.assertTrue(thrown.getClass().isInstance(new ImdbInvalidoException()));
	}

	@Test
	void o_construtor_da_partida_deve_iniciar_uma_rodada_com_as_cartas_da_rodada() {
		Partida partida = new Partida(new Jogador("user1"), cartas);
		Set<Carta> cartas = partida.getRodadaAtual().getCartas();
		assertTrue(cartas != null);
		assertEquals(CARTAS_POR_RODADA, cartas.size());
	}

	@Test
	void o_metodo_finalizar_deve_alterar_o_estado_da_partida() {
		Partida partida = new Partida(new Jogador("user1"), cartas);

		assertEquals(false, partida.desistir().isEmAndamento());
	}

	@Test
	void o_metodo_finalizar_deve_registrar_o_motivo_do_termino() {
		Partida partida = new Partida(new Jogador("user1"), cartas);

		assertEquals(false, partida.desistir().getMotivoTerminoDaPartida().isEmpty());
	}

	@Test
	void o_metodo_finalizar_deve_atualizar_o_record_do_jogador() {
		Partida partida = new Partida(new Jogador("user1"), cartas);

		assertEquals(0, partida.desistir().getJogador().getPontuacaoRecord());

		Carta cartaEscolhida = partida.getRodadaAtual().getCartas().stream()
				.max((o1, o2) -> o1.getRating() > o2.getRating() ? 1 : -1).get();
		partida.registrarResposta(cartaEscolhida.getImdbId());

		assertEquals(1, partida.desistir().getJogador().getPontuacaoRecord());
	}

	@SuppressWarnings("deprecation")
	@Test
	/**
	 * Em partida com apenas duas cartas no baralho não se consegue jogar mais de
	 * uma rodada sem que as cartas se repitam, portanto somente 1 rodada é
	 * permitida nesse caso.
	 */
	void o_metodo_nova_rodada_deve_lancar_excecao_caso_nao_exista_mais_combinacoes_de_carta_disponiveis() {

		List<Carta> cartas = new ArrayList<Carta>(2);
		// Somente 2 cartas no baralho
		cartas.add(new Carta("tt0000001", 9.6));
		cartas.add(new Carta("tt0000002", 9.5));

		partida = new Partida(new Jogador("user1"), cartas);

		FaltaDeCartasException thrown = Assertions.assertThrows(FaltaDeCartasException.class, () -> {

			partida.registrarResposta("tt0000001");
			partida.novaRodada(cartas);
		});
		Assertions.assertTrue(!thrown.getMessage().isEmpty());
	}

	@Test
	/**
	 * Numero maximo de rodadas varia de acordo com o numero de cartas no baralho.
	 * 
	 * @throws Exception
	 */
	void testa_o_metodo_obterNumeroMaxDeRodadasPossiveis() {
		assertEquals(1, Partida.obterNumeroMaxDeRodadasPossiveis(2));
		assertEquals(6, Partida.obterNumeroMaxDeRodadasPossiveis(4));
		assertEquals(10, Partida.obterNumeroMaxDeRodadasPossiveis(5));
		assertEquals(45, Partida.obterNumeroMaxDeRodadasPossiveis(10));
	}

	@Test
	void o_metodo_nova_rodada_deve_lancar_excecao_caso_a_rodada_anterior_esteja_sem_resposta() {

		PartidaException thrown = Assertions.assertThrows(PartidaException.class, () -> {

			Partida partida = new Partida(new Jogador("user1"), cartas);
			partida.novaRodada(cartas);
		});
		Assertions.assertTrue(!thrown.getMessage().isEmpty());
	}

	@Test
	void o_metodo_score_deve_retornar_a_nota_da_quantidade_de_partidas_multiplicada_pela_porcentagem_de_acertos() {

		// cenario 1: 4_corretas_de_6
		Partida partida = new Partida(new Jogador("user1"), cartas);
		Carta cartaEscolhida = partida.getRodadaAtual().getCartas().stream()
				.max((o1, o2) -> o1.getRating() > o2.getRating() ? 1 : -1).get();
		partida.registrarResposta(cartaEscolhida.getImdbId());

		partida.novaRodada(cartas);
		cartaEscolhida = partida.getRodadaAtual().getCartas().stream()
				.max((o1, o2) -> o1.getRating() > o2.getRating() ? 1 : -1).get();
		partida.registrarResposta(cartaEscolhida.getImdbId());

		partida.novaRodada(cartas);
		cartaEscolhida = partida.getRodadaAtual().getCartas().stream()
				.max((o1, o2) -> o1.getRating() > o2.getRating() ? 1 : -1).get();
		partida.registrarResposta(cartaEscolhida.getImdbId());

		partida.novaRodada(cartas);
		cartaEscolhida = partida.getRodadaAtual().getCartas().stream()
				.max((o1, o2) -> o1.getRating() > o2.getRating() ? 1 : -1).get();
		partida.registrarResposta(cartaEscolhida.getImdbId());

		partida.novaRodada(cartas);
		cartaEscolhida = partida.getRodadaAtual().getCartas().stream()
				.max((o1, o2) -> o1.getRating() < o2.getRating() ? 1 : -1).get();
		partida.registrarResposta(cartaEscolhida.getImdbId());

		partida.novaRodada(cartas);
		cartaEscolhida = partida.getRodadaAtual().getCartas().stream()
				.max((o1, o2) -> o1.getRating() < o2.getRating() ? 1 : -1).get();
		partida.registrarResposta(cartaEscolhida.getImdbId());

		assertEquals(4.02, partida.getScore());

		// cenario 2: 7_corretas_de_9
		partida = new Partida(new Jogador("user1"), cartas);

		// resposta 1
		cartaEscolhida = partida.getRodadaAtual().getCartas().stream()
				.max((o1, o2) -> o1.getRating() > o2.getRating() ? 1 : -1).get();
		partida.registrarResposta(cartaEscolhida.getImdbId());
		partida.novaRodada(cartas);

		// resposta 2
		cartaEscolhida = partida.getRodadaAtual().getCartas().stream()
				.max((o1, o2) -> o1.getRating() > o2.getRating() ? 1 : -1).get();
		partida.registrarResposta(cartaEscolhida.getImdbId());
		partida.novaRodada(cartas);

		// resposta 3
		cartaEscolhida = partida.getRodadaAtual().getCartas().stream()
				.max((o1, o2) -> o1.getRating() > o2.getRating() ? 1 : -1).get();
		partida.registrarResposta(cartaEscolhida.getImdbId());
		partida.novaRodada(cartas);

		// resposta 4
		cartaEscolhida = partida.getRodadaAtual().getCartas().stream()
				.max((o1, o2) -> o1.getRating() > o2.getRating() ? 1 : -1).get();
		partida.registrarResposta(cartaEscolhida.getImdbId());
		partida.novaRodada(cartas);

		// resposta 5
		cartaEscolhida = partida.getRodadaAtual().getCartas().stream()
				.max((o1, o2) -> o1.getRating() > o2.getRating() ? 1 : -1).get();
		partida.registrarResposta(cartaEscolhida.getImdbId());
		partida.novaRodada(cartas);

		// resposta 6
		cartaEscolhida = partida.getRodadaAtual().getCartas().stream()
				.max((o1, o2) -> o1.getRating() > o2.getRating() ? 1 : -1).get();
		partida.registrarResposta(cartaEscolhida.getImdbId());
		partida.novaRodada(cartas);

		// resposta 7
		cartaEscolhida = partida.getRodadaAtual().getCartas().stream()
				.max((o1, o2) -> o1.getRating() > o2.getRating() ? 1 : -1).get();
		partida.registrarResposta(cartaEscolhida.getImdbId());
		partida.novaRodada(cartas);

		// resposta 8
		cartaEscolhida = partida.getRodadaAtual().getCartas().stream()
				.max((o1, o2) -> o1.getRating() < o2.getRating() ? 1 : -1).get();
		partida.registrarResposta(cartaEscolhida.getImdbId());
		partida.novaRodada(cartas);

		// resposta 9
		cartaEscolhida = partida.getRodadaAtual().getCartas().stream()
				.max((o1, o2) -> o1.getRating() < o2.getRating() ? 1 : -1).get();
		partida.registrarResposta(cartaEscolhida.getImdbId());

		assertEquals(7.02, partida.getScore());

		// cenario 3: 1_corretas_de_2
		partida = new Partida(new Jogador("user1"), cartas);

		// resposta 1
		cartaEscolhida = partida.getRodadaAtual().getCartas().stream()
				.max((o1, o2) -> o1.getRating() > o2.getRating() ? 1 : -1).get();
		partida.registrarResposta(cartaEscolhida.getImdbId());

		partida.novaRodada(cartas);

		// resposta 2
		cartaEscolhida = partida.getRodadaAtual().getCartas().stream()
				.max((o1, o2) -> o1.getRating() < o2.getRating() ? 1 : -1).get();
		partida.registrarResposta(cartaEscolhida.getImdbId());

		assertEquals(1, partida.getScore());

	}
}
