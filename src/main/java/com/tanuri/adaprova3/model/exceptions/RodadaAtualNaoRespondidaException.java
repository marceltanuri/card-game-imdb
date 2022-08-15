package com.tanuri.adaprova3.model.exceptions;

public class RodadaAtualNaoRespondidaException extends PartidaException {

	public RodadaAtualNaoRespondidaException(String msg) {
		super(msg);
	}

	public RodadaAtualNaoRespondidaException() {
		super("Não é permitido seguir para a próxima rodada sem antes responder na rodada atual.");
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
