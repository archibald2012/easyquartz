package org.easycluster.easyquartz.exception;

public class SchedulerException extends RuntimeException {
	private static final long	serialVersionUID	= 1L;

	public SchedulerException(String message, Throwable cause) {
		super(message, cause);
	}

	public SchedulerException(String message) {
		super(message);
	}

}
