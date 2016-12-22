package com.github.yeriomin.playstoreapi;

import java.io.IOException;

public class GooglePlayException extends IOException {

	private int code;

	public GooglePlayException(String message) {
		super(message);
	}
	public GooglePlayException(String message, int code) {
		super(message);
		this.code = code;
	}

	public GooglePlayException(String message, Throwable cause) {
		super(message, cause);
	}

	private byte[] body;

	public void setBody(byte[] body) {
	    this.body = body;
    }

    public byte[] getBody() {
	    return body;
    }

    public int getCode() {
        return this.code;
    }
}
