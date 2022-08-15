package com.tanuri.adaprova3.model;

import java.util.Objects;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tanuri.adaprova3.model.exceptions.CartaEscolhidaInvalidaException;
import com.tanuri.adaprova3.model.exceptions.RodadaException;

/**
 * 
 * @author marcel.tanuri@gmail.com
 *
 */

@Entity
public class Rodada {

	private Integer numeroDeCartasPermitido;

	// For JPA
	@Deprecated
	protected Rodada() {
		super();
	}

	public Rodada(Set<Carta> cartas, Integer numeroDeCartas, int numeroDaRodada) {
		super();
		this.cartas = cartas;
		this.numeroDeCartasPermitido = numeroDeCartas;
		this.numeroDaRodada = numeroDaRodada;
	}

	public Rodada(Set<Carta> cartas, Integer numeroDeCartas, int numeroDaRodada, String imdbEscolhido) {
		super();
		this.cartas = cartas;
		this.numeroDeCartasPermitido = numeroDeCartas;
		this.numeroDaRodada = numeroDaRodada;
		this.imdbEscolhido = imdbEscolhido;
	}

	// For test Only
	@Deprecated
	public Rodada(Set<Carta> cartas, String imdbEscolhido, Integer numeroDeCartas) {
		super();
		this.cartas = cartas;
		this.imdbEscolhido = imdbEscolhido;
		this.numeroDeCartasPermitido = numeroDeCartas;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	private int numeroDaRodada;

	@ManyToMany
	private Set<Carta> cartas;

	private String imdbEscolhido;

	/**
	 * 
	 * @return true ou false para a resposta da rodada.
	 * @throws Exception
	 */
	@JsonIgnore
	public boolean acertou() {

		boolean parametrosValidos = cartas != null && !cartas.isEmpty();

		if (parametrosValidos) {

			boolean qtdCartasConformeAsRegrasDoJogo = cartas.size() == this.getNumeroDeCartasPermitido();
			if (qtdCartasConformeAsRegrasDoJogo) {

				boolean imdbValido = (imdbEscolhido != null && !imdbEscolhido.isEmpty()
						&& this.cartas.stream().anyMatch(c -> c.getImdbId().equals(imdbEscolhido)));
				if (imdbValido) {

					Double maiorRating = cartas.stream()
							.max((carta1, carta2) -> carta1.getRating() > carta2.getRating() ? 1 : -1).get()
							.getRating();

					Carta cartaEscolhida = cartas.stream().filter(c -> c.getImdbId().equals(imdbEscolhido)).findFirst()
							.get();

					boolean certaResposta = cartaEscolhida.getRating().equals(maiorRating);

					return certaResposta;
				} else {
					throw new CartaEscolhidaInvalidaException();
				}
			} else {
				throw new RodadaException("A quantidade de cartas entregue não é válida.");
			}
		} else {
			throw new RodadaException("Não foi possível processar as cartas entregues nessa rodada.");
		}
	}

	/**
	 * 
	 * @return true/false se imdb foi ou nao escolhido pelo jogador
	 */
	public boolean houveResposta() {
		return this.imdbEscolhido != null && !imdbEscolhido.isEmpty();
	}

	// Getters and Setters...

	private int getNumeroDeCartasPermitido() {

		if (this.numeroDeCartasPermitido != null && this.numeroDeCartasPermitido > 1) {
			return this.numeroDeCartasPermitido;
		}

		return Partida.DFLT_NUMERO_DE_CARTAS_PERMITIDO;
	}

	public int getNumeroDaRodada() {
		return numeroDaRodada;
	}

	public Rodada setImdbEscolhido(String imdbEscolhido) {
		this.imdbEscolhido = imdbEscolhido;
		return this;
	}

	public String getImdbEscolhido() {
		return imdbEscolhido;
	}

	public Set<Carta> getCartas() {
		return cartas;
	}

	// Hash, Equals, ToString ...

	@Override
	public int hashCode() {
		return Objects.hash(numeroDaRodada);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Rodada other = (Rodada) obj;
		return numeroDaRodada == other.numeroDaRodada;
	}

	@Override
	public String toString() {
		return "Rodada [numeroDeCartasPermitido=" + numeroDeCartasPermitido + ", id=" + id + ", numeroDaRodada="
				+ numeroDaRodada + ", cartas=" + cartas + ", imdbEscolhido=" + imdbEscolhido + "]";
	}

}
