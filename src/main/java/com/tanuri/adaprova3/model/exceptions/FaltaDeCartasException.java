package com.tanuri.adaprova3.model.exceptions;

public class FaltaDeCartasException extends Prova3Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public FaltaDeCartasException(String m) {
		super(m);
	}

	public FaltaDeCartasException() {
		super("Não há mais cartas suficientes para continuar a partida, considere-se um vencedor :)");
	}

}
