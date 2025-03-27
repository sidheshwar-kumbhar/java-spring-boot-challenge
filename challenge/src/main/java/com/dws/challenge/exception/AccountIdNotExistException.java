package com.dws.challenge.exception;

public class AccountIdNotExistException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public AccountIdNotExistException(String message) {
		super(message);
	}
}
