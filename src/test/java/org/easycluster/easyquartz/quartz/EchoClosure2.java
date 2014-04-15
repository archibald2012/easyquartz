package org.easycluster.easyquartz.quartz;

import org.easycluster.easyquartz.Closure;

public class EchoClosure2 implements Closure {

	@Override
	public void execute(Object msg) {
		System.out.println("EchoClosure2 received msg " + msg);
	}

}
