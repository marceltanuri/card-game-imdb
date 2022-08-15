package com.tanuri.adaprova3.model.exceptions;

public class CartaEscolhidaInvalidaException extends RodadaException {

	public CartaEscolhidaInvalidaException(String m) {
		super(m);
		// TODO Auto-generated constructor stub
	}

	public CartaEscolhidaInvalidaException() {
		super("A carta escolhida não confere com as cartas entregues na rodada, escolha uma entre as cartas entregues.");
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
