package com.tanuri.adaprova3.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tanuri.adaprova3.model.Jogador;

public interface JogadorRepository extends JpaRepository<Jogador, String> {

	List<Jogador> findByNome(String jogadorNome);

	List<Jogador> findByOrderByPontuacaoRecordDesc();

}
