package com.tanuri.adaprova3.scraper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.tanuri.adaprova3.model.Carta;
import com.tanuri.adaprova3.model.CartaBuilder;

/**
 * 
 * @author marcel.tanuri
 *
 */
public class ImbdScraper {

	/**
	 * 
	 * @return lista de cartas extraídas do banco de dados de filmes IMDB. As cartas
	 *         sao extraidas de 3 paginas diferentes, nomeadamente: os filmes mais
	 *         populares, os filmes de maior nota e os piores filmes :)
	 * @throws IOException
	 */
	public static List<Carta> extrair() throws IOException {
		String host = "https://www.imdb.com";
		String[] paths = { "/chart/moviemeter", "/chart/top", "/chart/bottom" };

		List<Carta> cartas = new ArrayList<>(200);

		for (String path : paths) {
			Document doc = Jsoup.connect(host + path).get();
			Elements movieElements = doc.select(".lister-list tr");
			cartas.addAll(getMoviesFromDOM(movieElements));
		}

		return cartas;
	}

	private static List<Carta> getMoviesFromDOM(Elements movieElements) {
		List<Carta> cartas = new ArrayList<>(50);
		for (Element element : movieElements) {
			String title = element.select(".titleColumn").first().select("a").first().text();
			String year = element.select(".titleColumn").first().select(".secondaryInfo").first().text();
			String imdbId = element.select(".titleColumn").first().select("a").attr("href");
			String rating = element.select(".imdbRating").first().select("strong").text();
			String image = element.select(".posterColumn").first().select("a").first().select("img").attr("src");

			Carta carta = new CartaBuilder().comTitulo(title).comAno(year).comImagem(image).comRating(rating)
					.comImdbId(imdbId).build();
			if (carta.getRating() != null) {
				cartas.add(carta);
			}
		}
		return cartas;
	}

}
