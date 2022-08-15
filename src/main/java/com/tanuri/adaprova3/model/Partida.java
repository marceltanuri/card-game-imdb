package com.tanuri.adaprova3.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import com.tanuri.adaprova3.model.exceptions.FaltaDeCartasException;
import com.tanuri.adaprova3.model.exceptions.FimDeJogoException;
import com.tanuri.adaprova3.model.exceptions.ImdbInvalidoException;
import com.tanuri.adaprova3.model.exceptions.PartidaException;
import com.tanuri.adaprova3.model.exceptions.RodadaAtualNaoRespondidaException;
import com.tanuri.adaprova3.model.exceptions.RodadaException;

/**
 * 
 * @author marcel.tanuri@gmail.com
 *
 */

@Entity
public class Partida {

	private static final int SCORE_DECIMAL_PLACES = 2;

	protected static final int DFLT_NUMERO_DE_CARTAS_PERMITIDO = 2;

	private static final int LIMITE_DE_ERROS = 3;

	@Deprecated // JPA use only
	protected Partida() {
		super();
	}

	protected Partida(String jogador, List<Carta> cartas) throws PartidaException, FaltaDeCartasException {
		this(new Jogador(jogador), cartas);
	}

	public Partida(Jogador jogador, List<Carta> cartas) throws PartidaException, FaltaDeCartasException {
		super();
		this.jogador = jogador;
		this.dataInicio = LocalDateTime.now();
		this.erros = 0;
		this.rodadas = new HashSet<>(10);
		this.novaRodada(cartas);
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Basic
	private LocalDateTime dataInicio;

	@OneToOne
	private Jogador jogador;

	private int erros;

	private boolean emAndamento = true;

	private int rodadaAtual;

	private String motivoTerminoDaPartida;

	@OneToMany
	private Set<Rodada> rodadas = new HashSet<Rodada>(10);

	/**
	 * Inicia uma nova rodada dentro da partida em andamento
	 * 
	 * @param cartaRepository
	 * @param partidaRepository
	 * @throws PartidaException
	 * @throws FaltaDeCartasException
	 */
	public Partida novaRodada(List<Carta> cartas) {
		if (this.getRodadaAtual() == null || this.getRodadaAtual().houveResposta()) {

			if (cartas == null || cartas.size() < 1
					|| this.rodadaAtual >= obterNumeroMaxDeRodadasPossiveis(cartas.size())) {
				throw new FaltaDeCartasException();
			}

			Set<Carta> novasCartas = obterNovasCartas(cartas);
			final Long hashNovasCartas = getHashDeCartas(novasCartas);

			boolean combincaoDeCartasJaUsadaEmOutraRodada = this.rodadas.stream()
					.anyMatch((rodada -> hashNovasCartas == getHashDeCartas(rodada.getCartas())));

			if (combincaoDeCartasJaUsadaEmOutraRodada) {
				novasCartas = obterNovasCartas(cartas);
			}

			this.rodadas.add(new Rodada(novasCartas, DFLT_NUMERO_DE_CARTAS_PERMITIDO, ++this.rodadaAtual));

			return this;

		} else {
			throw new RodadaAtualNaoRespondidaException();
		}
	}

	/**
	 * Registra a resposta da rodada em andamento
	 * 
	 * @param imdbEscolhido
	 * @param partidaRepository
	 * @throws RodadaException
	 * @throws FimDeJogoException
	 */
	public Partida registrarResposta(String imdbEscolhido) {

		if (Carta.validaImdbId(imdbEscolhido)) {
			if (!getRodadaAtual().setImdbEscolhido(imdbEscolhido).acertou()) {
				if (++erros > LIMITE_DE_ERROS) {
					throw new FimDeJogoException("Limite de erros foi atingigo. O jogo acabou");
				}
			}
			return this;
		} else {
			throw new ImdbInvalidoException();
		}

	}

	@SuppressWarnings("unused")
	private double score;

	/**
	 * 
	 * @return score da partida
	 */
	public double getScore() {
		double rodadasRespondidas = this.rodadas.parallelStream().filter(rodada -> rodada.houveResposta()).count();
		double respostasCorretas = rodadasRespondidas - this.erros;

		if (rodadasRespondidas == 0 || respostasCorretas == 0) {
			return 0.0;
		}

		BigDecimal porcentagemDeAcerto = new BigDecimal(respostasCorretas / rodadasRespondidas)
				.setScale(SCORE_DECIMAL_PLACES, RoundingMode.HALF_DOWN);
		double score = new BigDecimal(rodadasRespondidas * porcentagemDeAcerto.doubleValue())
				.setScale(SCORE_DECIMAL_PLACES, RoundingMode.HALF_DOWN).doubleValue();
		return score;
	}

	/**
	 * 
	 * @return o objeto rodada em andamento
	 */
	public Rodada getRodadaAtual() {
		Optional<Rodada> findFirst = this.rodadas.parallelStream()
				.filter(rodada -> rodada.getNumeroDaRodada() == rodadaAtual).findFirst();
		if (findFirst.isPresent()) {
			return findFirst.get();
		}
		return null;
	}

	/**
	 * 
	 * @param cartas
	 * @param cartaRepository
	 * @return um set de cartas obtidas aleatoriamente
	 */
	private Set<Carta> obterNovasCartas(List<Carta> cartas) {
		int maxIndex = cartas.size() - 1;
		int minIndex = 0;
		List<Integer> usedIndexes = new ArrayList<>(DFLT_NUMERO_DE_CARTAS_PERMITIDO);

		Set<Carta> novasCartas = new HashSet<Carta>(DFLT_NUMERO_DE_CARTAS_PERMITIDO);

		int cont = 1;

		while (cont <= DFLT_NUMERO_DE_CARTAS_PERMITIDO) {
			int randomIndex = new Random().nextInt((maxIndex - minIndex) + 1);

			if (!usedIndexes.contains(randomIndex)) {
				novasCartas.add(cartas.get(randomIndex));
				usedIndexes.add(randomIndex);
				cont++;
			} else {
				continue;
			}

		}

		return novasCartas;

	}

	/**
	 * Calcula o numero max de rodadas possiveis de acordo com a quantidade de
	 * cartas e de modo que nao se repitam as mesmas cartas em mais de uma rodada.
	 * Por exemplo: [A] + [B] é o mesmo que [B] + [A] e por isso essa combinacao nao
	 * pode ser repitida nao importa a ordem.
	 * 
	 * O calculo abaixo considera esse cenario e descobre a quantidade maxima de
	 * combinacoes sem repeticao.
	 * 
	 * @param cartasSize
	 * @return numero max de rodadas possiveis
	 */
	public static int obterNumeroMaxDeRodadasPossiveis(int cartasSize) {
		int numeroMaxDeRodadasPossiveis = 0;

		for (int i = cartasSize - 1; i > 0; i--) {
			numeroMaxDeRodadasPossiveis = numeroMaxDeRodadasPossiveis + (cartasSize - i);
		}

		return numeroMaxDeRodadasPossiveis;
	}

	/**
	 * 
	 * @param cartas
	 * @return um valor unico HASH que identifica a combinacao do set de cartas nao
	 *         importa a ordem das cartas
	 */
	public static Long getHashDeCartas(Set<Carta> cartas) {
		if (cartas != null && !cartas.isEmpty()) {
			Long hash = 1L;
			for (Carta carta : cartas) {
				hash = hash * carta.getHashImdbId();
			}
			return hash;

		}

		return 0l;
	}

	public Partida fimDeJogoFimDasCartas() {
		return this.finalizar("Fim de Jogo, todas as cartas já foram usadas, considere-se um vencedor!");
	}

	public Partida fimDeJogoPorErros() {
		return this.finalizar("Fim de Jogo, limite de erros excedido");
	}

	public Partida desistir() {
		return this.finalizar("Desistiu");
	}

	/**
	 * Fnaliza a partida e faz set do score no obj Jogador caso seja um score record
	 * 
	 * @return resumo da partida
	 */
	private Partida finalizar(String motivo) {
		this.emAndamento = false;
		this.motivoTerminoDaPartida = motivo;
		double score = getScore();
		if (this.jogador.getPontuacaoRecord() == null || this.jogador.getPontuacaoRecord() < score) {
			this.jogador.setPontuacaoRecord(score);
		}
		return this;
	}

	// Getters and Setters ...

	public Long getId() {
		return id;
	}

	public LocalDateTime getDataInicio() {
		return dataInicio;
	}

	public Jogador getJogador() {
		return jogador;
	}

	public int getErros() {
		return erros;
	}

	public Set<Rodada> getRodadas() {
		return rodadas;
	}

	public boolean isEmAndamento() {
		return emAndamento;
	}

	public String getMotivoTerminoDaPartida() {
		return motivoTerminoDaPartida;
	}

	// ToString, Equals, HashCode ...

	@Override
	public String toString() {
		return "Partida [id=" + id + ", dataInicio=" + dataInicio + ", jogador=" + jogador + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Partida other = (Partida) obj;
		return Objects.equals(id, other.id);
	}

}
