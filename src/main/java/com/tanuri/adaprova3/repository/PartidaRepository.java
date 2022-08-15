package com.tanuri.adaprova3.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tanuri.adaprova3.model.Jogador;
import com.tanuri.adaprova3.model.Partida;

public interface PartidaRepository extends JpaRepository<Partida, Long> {

	List<Partida> findByJogador(String jogador);

	List<Partida> findByEmAndamentoAndJogador(boolean emAndamento, Jogador jogador);
	
	List<Partida> findByEmAndamentoAndJogadorOrderByEmAndamentoDesc(boolean emAndamento, Jogador jogador);

	Partida findById(long id);

}
