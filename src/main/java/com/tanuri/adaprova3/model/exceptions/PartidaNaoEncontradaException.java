package com.tanuri.adaprova3.model.exceptions;

public class PartidaNaoEncontradaException extends RegistroNaoEncontradoException {

	public PartidaNaoEncontradaException(String m) {
		super(m);
	}

	public PartidaNaoEncontradaException() {
		super("Não há partidadas para este jogador.");
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
