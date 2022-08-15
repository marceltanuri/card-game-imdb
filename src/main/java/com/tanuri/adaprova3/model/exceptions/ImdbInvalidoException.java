package com.tanuri.adaprova3.model.exceptions;

public class ImdbInvalidoException extends PartidaException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ImdbInvalidoException() {
		super("O ImdbId informado não é válido");
	}

	public ImdbInvalidoException(String message, Throwable cause) {
		super(message, cause);
	}

	public ImdbInvalidoException(String message) {
		super(message);
	}

	public ImdbInvalidoException(Throwable cause) {
		super(cause);
	}

}
