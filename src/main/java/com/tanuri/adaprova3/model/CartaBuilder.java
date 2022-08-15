package com.tanuri.adaprova3.model;

public class CartaBuilder {

	private Carta carta = new Carta();

	public CartaBuilder comTitulo(String titulo) {
		this.carta.setTitulo(titulo);
		return this;
	}

	public CartaBuilder comAno(String anoStr) {

		try {
			if (anoStr != null && !anoStr.isEmpty()) {
				carta.setAno(Integer.valueOf(anoStr));
			}
		} catch (NumberFormatException e) {
		}
		return this;
	}

	public CartaBuilder comRating(String ratingStr) {

		try {
			if (ratingStr != null && !ratingStr.isEmpty()) {
				carta.setRating(Double.valueOf(ratingStr));
			}
		} catch (NumberFormatException e) {
		}
		return this;
	}

	public CartaBuilder comImagem(String imagem) {
		this.carta.setImagem(imagem);
		return this;
	}

	public CartaBuilder comImdbId(String imdbId) {
		if (imdbId != null && !imdbId.isEmpty() && imdbId.contains("/")) {
			int indexBegin = imdbId.indexOf("/", imdbId.indexOf("/tt"));
			String formatedImdId = imdbId.substring(indexBegin+1, imdbId.indexOf("/", indexBegin + 1));
			this.carta.setImdbId(formatedImdId);
		} else {
			this.carta.setImdbId(imdbId);
		}
		return this;
	}

	public Carta build() {
		return this.carta;
	}

}
