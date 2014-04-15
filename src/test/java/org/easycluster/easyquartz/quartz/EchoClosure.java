package org.easycluster.easyquartz.quartz;

import java.util.Date;

import org.easycluster.easyquartz.Closure;

public class EchoClosure implements Closure {

	@Override
	public void execute(Object msg) {
		System.out.println("Received msg " + msg);
	}

}
