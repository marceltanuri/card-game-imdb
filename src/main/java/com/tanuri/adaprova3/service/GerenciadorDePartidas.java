package com.tanuri.adaprova3.service;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.tanuri.adaprova3.model.Carta;
import com.tanuri.adaprova3.model.Jogador;
import com.tanuri.adaprova3.model.Partida;
import com.tanuri.adaprova3.model.exceptions.FaltaDeCartasException;
import com.tanuri.adaprova3.model.exceptions.FimDeJogoException;
import com.tanuri.adaprova3.model.exceptions.GerenciadorDePartidaException;
import com.tanuri.adaprova3.model.exceptions.JogadorNaoEncontradoException;
import com.tanuri.adaprova3.model.exceptions.NaoHaPartidaEmAndamentoException;
import com.tanuri.adaprova3.model.exceptions.PartidaException;
import com.tanuri.adaprova3.model.exceptions.PartidaNaoEncontradaException;
import com.tanuri.adaprova3.repository.CartaRepository;
import com.tanuri.adaprova3.repository.JogadorRepository;
import com.tanuri.adaprova3.repository.PartidaRepository;
import com.tanuri.adaprova3.repository.RodadaRepository;

@Service
public class GerenciadorDePartidas {

	PartidaRepository partidaRepository;

	CartaRepository cartaRepository;

	RodadaRepository rodadaRepository;

	JogadorRepository jogadorRepository;

	private static List<Carta> cartas;

	public GerenciadorDePartidas(PartidaRepository partidaRepository, CartaRepository cartaRepository,
			RodadaRepository rodadaRepository, JogadorRepository jogadorRepository) {
		super();
		this.partidaRepository = partidaRepository;
		this.cartaRepository = cartaRepository;
		this.rodadaRepository = rodadaRepository;
		this.jogadorRepository = jogadorRepository;
	}

	/**
	 * Cria uma nova partida para o jogador
	 * 
	 * @param nomeJogador
	 * @return A rodada 1 de uma nova partida
	 * @throws PartidaException
	 * @throws GerenciadorDePartidaException
	 * @throws FaltaDeCartasException
	 */
	public Partida iniciar(String nomeJogador)
			throws PartidaException, GerenciadorDePartidaException, FaltaDeCartasException {
		if (cartas == null)
			cartas = cartaRepository.findAll();

		Jogador jogador = getJogador(nomeJogador);

		jogador.setUltimoAcesso(LocalDateTime.now());
		jogadorRepository.save(jogador);

		List<Partida> partidaEmAndamento = partidaRepository.findByEmAndamentoAndJogador(true, jogador);

		boolean naoHaPartidaEmAndamentoParaOJogador = (partidaEmAndamento.isEmpty());

		if (naoHaPartidaEmAndamentoParaOJogador) {
			Partida partida = new Partida(jogador, cartas);
			rodadaRepository.save(partida.getRodadaAtual());
			partidaRepository.save(partida);
			return partida;
		}
		return partidaEmAndamento.get(0);
	}

	public Partida proximaRodada(String nomeJogador, String imdbEscolhido) {

		Partida partida = this.getPartidaEmAndamento(this.getJogador(nomeJogador));
		try {
			partida.registrarResposta(imdbEscolhido);
		} catch (FimDeJogoException e) {
			this.finalizaPartidaPorLimiteDeErros(partida);
			throw e;
		}
		rodadaRepository.save(partida.getRodadaAtual());
		try {
			partida.novaRodada(cartas);
		} catch (FaltaDeCartasException e) {
			this.finalizaPartidaPorLimiteDeCartas(partida);
			throw e;
		}
		rodadaRepository.save(partida.getRodadaAtual());
		return partidaRepository.save(partida);
	}

	public Partida finalizaPartidaPorLimiteDeErros(Partida p) {
		p.fimDeJogoPorErros();
		partidaRepository.save(p);
		return p;
	}

	public Partida finalizaPartidaPorLimiteDeCartas(Partida p) {
		p.fimDeJogoFimDasCartas();
		partidaRepository.save(p);
		return p;
	}

	public Partida statusPartidaEmAndamento(String nomeJogador) throws GerenciadorDePartidaException {
		return this.getPartidaEmAndamento(this.getJogador(nomeJogador));

	}

	public Partida statusUltimaPartidaEncerrada(String nomeJogador) throws GerenciadorDePartidaException {
		return this.getUltimaPartidaEncerrada(this.getJogador(nomeJogador));

	}

	public Partida desistir(String nomeJogador) throws GerenciadorDePartidaException, NaoHaPartidaEmAndamentoException {
		Partida partida = this.getPartidaEmAndamento(this.getJogador(nomeJogador));
		partida.desistir();
		jogadorRepository.save(partida.getJogador());
		partidaRepository.save(partida);
		return partida;
	}

	/**
	 * 
	 * @return lista com jogadores com maior pontuação
	 */
	public Collection<Jogador> top10() {

		List<Jogador> jogadoresByRecord = jogadorRepository.findByOrderByPontuacaoRecordDesc();
		if (jogadoresByRecord != null && !jogadoresByRecord.isEmpty()) {
			if (jogadoresByRecord.size() > 10) {
				return jogadoresByRecord.subList(0, 10);
			} else {
				return jogadoresByRecord;
			}
		}

		return null;
	}

	/**
	 * 
	 * @param nome do jogador
	 * @return posicao do jogador no ranking geral
	 * @throws GerenciadorDePartidaException
	 */
	public long ranking(String nome) throws GerenciadorDePartidaException {

		Jogador jogador = getJogador(nome);

		List<Jogador> jogadoresByRecord = jogadorRepository.findByOrderByPontuacaoRecordDesc();
		if (jogadoresByRecord != null && !jogadoresByRecord.isEmpty()) {
			return jogadoresByRecord.indexOf(jogador) + 1;
		}

		return -1;
	}

	private Partida getPartidaEmAndamento(Jogador jogador) throws NaoHaPartidaEmAndamentoException {
		List<Partida> findByEmAndamentoAndJogador = partidaRepository.findByEmAndamentoAndJogador(true, jogador);

		boolean naoHaPartidaEmAndamentoParaOJogador = (findByEmAndamentoAndJogador.isEmpty());

		if (naoHaPartidaEmAndamentoParaOJogador) {
			throw new NaoHaPartidaEmAndamentoException("Não há partidas em andamento para o jogador.");
		}
		return findByEmAndamentoAndJogador.get(0);
	}

	private Partida getUltimaPartidaEncerrada(Jogador jogador) {
		List<Partida> findByEncerradasAndJogador = partidaRepository
				.findByEmAndamentoAndJogadorOrderByEmAndamentoDesc(false, jogador);

		boolean naoHaPartidaEncerradaParaOJogador = (findByEncerradasAndJogador.isEmpty());

		if (naoHaPartidaEncerradaParaOJogador) {
			throw new PartidaNaoEncontradaException();
		}

		return findByEncerradasAndJogador.get(0);
	}

	private Jogador getJogador(String nomeJogador) {
		Optional<Jogador> findById = jogadorRepository.findById(nomeJogador);
		if (findById.isPresent()) {
			return findById.get();
		} else {
			throw new JogadorNaoEncontradoException();
		}
	}

}
