package com.tanuri.adaprova3.model.exceptions;

public class JogadorNaoEncontradoException extends RegistroNaoEncontradoException {

	public JogadorNaoEncontradoException(String m) {
		super(m);
	}

	public JogadorNaoEncontradoException() {
		super("O jogador não foi encontrado na base de dados.");
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
