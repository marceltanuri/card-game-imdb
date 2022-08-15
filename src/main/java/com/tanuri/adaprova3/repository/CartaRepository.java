package com.tanuri.adaprova3.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tanuri.adaprova3.model.Carta;
import com.tanuri.adaprova3.model.Partida;

public interface CartaRepository extends JpaRepository<Carta, String> {

	List<Partida> findByImdbId(String jogador);

}
