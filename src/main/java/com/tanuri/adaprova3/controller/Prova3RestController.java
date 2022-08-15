package com.tanuri.adaprova3.controller;

import java.security.Principal;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tanuri.adaprova3.model.Jogador;
import com.tanuri.adaprova3.model.Partida;
import com.tanuri.adaprova3.model.Rodada;
import com.tanuri.adaprova3.model.exceptions.GerenciadorDePartidaException;
import com.tanuri.adaprova3.model.exceptions.NaoHaPartidaEmAndamentoException;
import com.tanuri.adaprova3.model.exceptions.Prova3Exception;
import com.tanuri.adaprova3.service.GerenciadorDePartidas;

@RestController
public class Prova3RestController {

	@Autowired
	private GerenciadorDePartidas gerenciadorDePartidas;

	@GetMapping("/about")
	public String about(Principal principal) {
		return principal.getName() + " Prova3 Java Test, version 1.0.0";
	}

	@PostMapping("/iniciar")
	public ResponseEntity<Rodada> iniciar(Principal principal) {
		try {
			Partida partida = gerenciadorDePartidas.iniciar(principal.getName());
			return ResponseEntity.ok(partida.getRodadaAtual());
		} catch (Prova3Exception e) {
			e.printStackTrace();
			return ResponseEntity.internalServerError().build();
		}
	}

	@PostMapping("/desistir")
	public ResponseEntity<Partida> desistir(Principal principal) {
		try {
			Partida partida = gerenciadorDePartidas.desistir(principal.getName());
			return ResponseEntity.ok(partida);
		} catch (Prova3Exception e) {
			e.printStackTrace();
			return ResponseEntity.internalServerError().build();
		}
	}

	@GetMapping("/top10")
	public ResponseEntity<Jogador[]> top10() {
		Collection<Jogador> ranking = gerenciadorDePartidas.top10();
		return ResponseEntity.ok(ranking.toArray(new Jogador[ranking.size()]));
	}

	@GetMapping("/meuRanking")
	public ResponseEntity<Long> meuRanking(Principal principal) {
		try {
			return ResponseEntity.ok(gerenciadorDePartidas.ranking(principal.getName()));
		} catch (GerenciadorDePartidaException e) {
			e.printStackTrace();
			return ResponseEntity.internalServerError().build();
		}
	}

	@GetMapping("/status")
	public ResponseEntity<Partida> status(Principal principal) {
		try {
			Partida partida = gerenciadorDePartidas.statusPartidaEmAndamento(principal.getName());
			return ResponseEntity.ok(partida);
		} catch (NaoHaPartidaEmAndamentoException e1) {
			try {
				Partida partidaEncerrada = gerenciadorDePartidas.statusUltimaPartidaEncerrada(principal.getName());
				return ResponseEntity.ok(partidaEncerrada);
			} catch (GerenciadorDePartidaException e) {
				return ResponseEntity.internalServerError().build();
			}
		} catch (GerenciadorDePartidaException e) {
			return ResponseEntity.internalServerError().build();
		}
	}

	@PostMapping("/proxima/{imdbEscolhido}")
	public ResponseEntity<Rodada> proxima(Principal principal, @PathVariable String imdbEscolhido)
			throws Prova3Exception {
		Partida partida = gerenciadorDePartidas.proximaRodada(principal.getName(), imdbEscolhido);
		return ResponseEntity.ok(partida.getRodadaAtual());

	}

}
