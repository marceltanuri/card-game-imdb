package com.tanuri.adaprova3.model;

import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * 
 * @author marcel.tanuri@gmail.com
 *
 */

@Entity
public class Carta {

	protected Carta() {
		super();
	}

	// For tests only
	@Deprecated
	public Carta(String imdbId, Double rating) {
		super();
		this.imdbId = imdbId;
		this.rating = rating;
	}

	public Carta(String imdbId, String titulo, int ano, String imagem, Double rating) {
		super();
		this.imdbId = imdbId;
		this.titulo = titulo;
		this.ano = ano;
		this.imagem = imagem;
		this.rating = rating;
	}

	@Id
	private String imdbId;

	private String titulo;

	private int ano;

	private String imagem;

	@JsonIgnore
	private Double rating;

	/**
	 * O ImdbId inicia-se sempre com duas letras seguido de no minimo 7 digitos
	 * numericos
	 * 
	 * @param imdbId
	 * @return true ou false, se o imdb informado e valido ou invalido
	 */
	public static boolean validaImdbId(String imdbId) {

		if (imdbId != null && !imdbId.isEmpty()) {
			return imdbId.matches("^[a-zA-Z]{2}[0-9]{7,12}$");
		}

		return false;
	}

	// Getters and Setters ...

	public String getImdbId() {
		return imdbId;
	}

	public String getTitulo() {
		return titulo;
	}

	public int getAno() {
		return ano;
	}

	public String getImagem() {
		return imagem;
	}

	public Double getRating() {
		return rating;
	}

	public void setImdbId(String imdbId) {
		this.imdbId = imdbId;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public void setAno(int ano) {
		this.ano = ano;
	}

	public void setImagem(String imagem) {
		this.imagem = imagem;
	}

	public void setRating(Double rating) {
		this.rating = rating;
	}

	// Hash, Equals, ToString ...

	@Override
	public int hashCode() {
		return Objects.hash(imdbId);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Carta other = (Carta) obj;
		return Objects.equals(imdbId, other.imdbId);
	}

	public Long getHashImdbId() {
		if (this.imdbId != null) {
			return Long.parseLong(this.imdbId.replaceAll("[^\\d.]", ""));
		}
		return 0l;
	}

	@Override
	public String toString() {
		return "Carta [imdbId=" + imdbId + ", titulo=" + titulo + ", ano=" + ano + ", imagem=" + imagem + "]";
	}

}
