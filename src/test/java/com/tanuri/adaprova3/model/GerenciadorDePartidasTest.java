package com.tanuri.adaprova3.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.tanuri.adaprova3.repository.CartaRepository;
import com.tanuri.adaprova3.repository.JogadorRepository;
import com.tanuri.adaprova3.repository.PartidaRepository;
import com.tanuri.adaprova3.repository.RodadaRepository;
import com.tanuri.adaprova3.service.GerenciadorDePartidas;

@ExtendWith(MockitoExtension.class)
class GerenciadorDePartidasTest {

	@Mock
	PartidaRepository partidaRepository;

	@Mock
	CartaRepository cartaRepository;

	@Mock
	RodadaRepository rodadaRepository;

	@Mock
	JogadorRepository jogadorRepository;

	private GerenciadorDePartidas gerenciadorDePartidas;

	static List<Carta> cartas = new ArrayList<>(9);

	@BeforeAll
	@SuppressWarnings("deprecation")
	static void beforeAll() {
		cartas.add(new Carta("tt0000001", 9.6));
		cartas.add(new Carta("tt0000002", 9.5));
		cartas.add(new Carta("tt0000003", 2.4));
		cartas.add(new Carta("tt0000004", 8.7));
		cartas.add(new Carta("tt0000005", 5.3));
		cartas.add(new Carta("tt0000006", 8.9));
		cartas.add(new Carta("tt0000007", 6.2));
		cartas.add(new Carta("tt0000008", 5.1));
		cartas.add(new Carta("tt0000009", 4.6));
	}

	@BeforeEach
	public void initEach() {
		this.gerenciadorDePartidas = new GerenciadorDePartidas(partidaRepository, cartaRepository, rodadaRepository,
				jogadorRepository);

		lenient().when(cartaRepository.findAll()).thenReturn(cartas);
		lenient().when(jogadorRepository.findById("user1")).thenReturn(Optional.of(new Jogador("user1")));
		lenient().when(jogadorRepository.findById("user2")).thenReturn(Optional.of(new Jogador("user2")));
		lenient().when(partidaRepository.findByEmAndamentoAndJogador(true, new Jogador("user1")))
				.thenReturn(new ArrayList<>());
	}

	@Test
	void o_metodo_iniciar_deve_salvar_a_data_de_ultimo_acesso_do_jogador() throws InterruptedException {
		LocalDateTime ultimoAcesso = gerenciadorDePartidas.iniciar("user1").getJogador().getUltimoAcesso();
		Thread.sleep(10);
		assertTrue(!ultimoAcesso.equals(gerenciadorDePartidas.iniciar("user1").getJogador().getUltimoAcesso()));
	}

	@Test
	void o_metodo_iniciar_deve_iniciar_nova_partida_se_nenhuma_estiver_em_andamento_para_o_jogador__numero_da_primeira_jogada_deve_ser_1() {
		Rodada rodada = gerenciadorDePartidas.iniciar("user1").getRodadaAtual();
		assertEquals(1, rodada.getNumeroDaRodada());
	}

	@Test
	void o_metodo_iniciar_deve_salvar_a_rodada_no_bd() {
		gerenciadorDePartidas.iniciar("user1");
		verify(rodadaRepository, times(1)).save(ArgumentMatchers.any(Rodada.class));
	}

	@Test
	void o_metodo_iniciar_deve_salvar_a_partida_no_bd() {
		gerenciadorDePartidas.iniciar("user1");
		verify(partidaRepository, times(1)).save(ArgumentMatchers.any(Partida.class));
	}

	@Test
	void o_metodo_iniciar_deve_salvar_o_jogador_no_bd() {
		gerenciadorDePartidas.iniciar("user1");
		verify(jogadorRepository, times(1)).save(ArgumentMatchers.any(Jogador.class));
	}

	@Test
	void o_metodo_finalizar_deve_salvar_a_partida_no_bd() {
		List<Partida> partidas = Arrays.asList(new Partida("user1", cartas));
		when(partidaRepository.findByEmAndamentoAndJogador(true, new Jogador("user1"))).thenReturn(partidas);
		gerenciadorDePartidas.desistir("user1");
		verify(partidaRepository, times(1)).save(ArgumentMatchers.any(Partida.class));
	}

	@Test
	void o_metodo_finalizar_deve_salvar_o_jogador_no_bd() {
		List<Partida> partidas = Arrays.asList(new Partida("user1", cartas));
		when(partidaRepository.findByEmAndamentoAndJogador(true, new Jogador("user1"))).thenReturn(partidas);
		gerenciadorDePartidas.desistir("user1");
		verify(jogadorRepository, times(1)).save(ArgumentMatchers.any(Jogador.class));
	}

	@Test
	void o_metodo_status_deve_mostrar_o_status_atual_da_partida() {
		Partida partida = gerenciadorDePartidas.iniciar("user1");

		ArrayList<Partida> list = new ArrayList<>();
		list.add(partida);
		lenient().when(partidaRepository.findByEmAndamentoAndJogador(true, new Jogador("user1"))).thenReturn(list);

		assertTrue(gerenciadorDePartidas.statusPartidaEmAndamento("user1") != null);
		assertEquals("user1", gerenciadorDePartidas.statusPartidaEmAndamento("user1").getJogador().getNome());
	}

	@Test
	void o_metodo_iniciar_deve_continuar_partida_se_a_mesma_estiver_em_andamento_para_o_jogador() {
		// inicializando um partida anterior ao assert do teste
		Partida partida = gerenciadorDePartidas.iniciar("user2");
		ArrayList<Partida> list = new ArrayList<>();
		list.add(partida);
		lenient().when(partidaRepository.findByEmAndamentoAndJogador(true, new Jogador("user2"))).thenReturn(list);

		// mock de partida em andamento encontrada na base de dados
		lenient().when(partidaRepository.findByEmAndamentoAndJogador(true, new Jogador("user2"))).thenReturn(list);

		Partida partidaReinicio = gerenciadorDePartidas.iniciar("user2");
		assertEquals(partida, partidaReinicio);
	}

}
