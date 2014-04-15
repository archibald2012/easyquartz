package org.easycluster.easyquartz.quartz;

import org.easycluster.easyquartz.Closure;

public class EchoClosure implements Closure {

	@Override
	public void execute(Object msg) {
		System.out.println("EchoClosure received msg " + msg);
	}

}
