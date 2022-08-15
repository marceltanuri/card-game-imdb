package com.tanuri.adaprova3.model;

import java.time.LocalDateTime;
import java.util.Objects;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Jogador {

	protected Jogador() {
		super();
	}

	public Jogador(String nome) {
		super();
		this.nome = nome;
	}

	@Id
	private String nome;

	@Basic
	private LocalDateTime ultimoAcesso;

	private Double pontuacaoRecord;

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public LocalDateTime getUltimoAcesso() {
		return ultimoAcesso;
	}

	public void setUltimoAcesso(LocalDateTime ultimoAcesso) {
		this.ultimoAcesso = ultimoAcesso;
	}

	public Double getPontuacaoRecord() {
		return pontuacaoRecord;
	}

	public void setPontuacaoRecord(Double pontuacaoRecord) {
		this.pontuacaoRecord = pontuacaoRecord;
	}

	@Override
	public int hashCode() {
		return Objects.hash(nome);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Jogador other = (Jogador) obj;
		return Objects.equals(nome, other.nome);
	}

}
