package com.tanuri.adaprova3.model.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class FimDeJogoException extends PartidaException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public FimDeJogoException(String m) {
		super(m);
	}

}
