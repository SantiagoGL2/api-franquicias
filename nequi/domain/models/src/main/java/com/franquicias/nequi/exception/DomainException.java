package com.franquicias.nequi.exception;


public abstract class DomainException extends RuntimeException {

	protected DomainException(String mensaje) {
		super(mensaje);
	}

}
