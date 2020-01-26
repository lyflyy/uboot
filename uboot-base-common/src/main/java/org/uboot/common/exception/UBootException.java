package org.uboot.common.exception;

public class UBootException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public UBootException(String message){
		super(message);
	}

	public UBootException(Throwable cause)
	{
		super(cause);
	}

	public UBootException(String message, Throwable cause)
	{
		super(message,cause);
	}
}
