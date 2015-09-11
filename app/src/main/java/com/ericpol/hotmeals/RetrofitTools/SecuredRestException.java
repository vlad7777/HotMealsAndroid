package com.ericpol.hotmeals.RetrofitTools;

import java.lang.RuntimeException;

public class SecuredRestException extends RuntimeException {

	public SecuredRestException() {
		super();
	}

	public SecuredRestException(String message, Throwable cause,
								boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause);
		//super(message, cause, enableSuppression, writableStackTrace);
	}

	public SecuredRestException(String message, Throwable cause) {
		super(message, cause);
	}

	public SecuredRestException(String message) {
		super(message);
	}

	public SecuredRestException(Throwable cause) {
		super(cause);
	}

}
