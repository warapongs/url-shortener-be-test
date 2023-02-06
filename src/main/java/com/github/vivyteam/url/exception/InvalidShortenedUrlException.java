package com.github.vivyteam.url.exception;

@SuppressWarnings("serial")
public class InvalidShortenedUrlException extends RuntimeException {

	public InvalidShortenedUrlException(String message) {
		super(message);
	}
	
	public InvalidShortenedUrlException(String message, Throwable cause) {
		super(message, cause);
	}

}
