package com.tanuri.adaprova3.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.tanuri.adaprova3.model.exceptions.FaltaDeCartasException;
import com.tanuri.adaprova3.model.exceptions.FimDeJogoException;
import com.tanuri.adaprova3.model.exceptions.ImdbInvalidoException;
import com.tanuri.adaprova3.model.exceptions.Prova3Exception;
import com.tanuri.adaprova3.model.exceptions.RegistroNaoEncontradoException;
import com.tanuri.adaprova3.model.exceptions.RodadaAtualNaoRespondidaException;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler(value = { Prova3Exception.class, IllegalStateException.class })
	protected ResponseEntity<Object> handleConflict(RuntimeException ex, WebRequest request) {
		return handleExceptionInternal(ex, ex.getMessage(), new HttpHeaders(), HttpStatus.CONFLICT, request);
	}

	@ExceptionHandler(value = { ImdbInvalidoException.class, RodadaAtualNaoRespondidaException.class })
	protected ResponseEntity<Object> handleBadRequest(RuntimeException ex, WebRequest request) {
		return handleExceptionInternal(ex, ex.getMessage(), new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
	}

	@ExceptionHandler(value = { FimDeJogoException.class, FaltaDeCartasException.class })
	protected ResponseEntity<Object> handleFimDeJogo(RuntimeException ex, WebRequest request) {
		return handleExceptionInternal(ex, ex.getMessage(), new HttpHeaders(), HttpStatus.CONFLICT, request);
	}

	@ExceptionHandler(value = { RegistroNaoEncontradoException.class })
	protected ResponseEntity<Object> handleFimNotFound(RuntimeException ex, WebRequest request) {
		return handleExceptionInternal(ex, ex.getMessage(), new HttpHeaders(), HttpStatus.NOT_FOUND, request);
	}

}