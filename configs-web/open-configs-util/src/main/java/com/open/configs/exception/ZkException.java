package com.open.configs.exception;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.KeeperException.Code;

public class ZkException extends RuntimeException {
	protected Code code;

	private static final long serialVersionUID = 1L;

	public ZkException() {
		super();
	}

	public ZkException(String message, Throwable cause) {
		super(message, cause);
	}

	public ZkException(String message) {
		super(message);
	}

	public ZkException(Throwable cause) {
		super(cause);
	}

	public ZkException(KeeperException e) {
        super(e);
		KeeperException exception = (KeeperException) e;
		if (exception.code() == Code.SESSIONEXPIRED) {
			//JdZookeeperClient.sessionExpired = true;
		}
		code = e.code();
	}

	public Code getCode() {
		return code;
	}

	public void setCode(Code code) {
		this.code = code;
	}

}
