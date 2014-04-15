package org.easycluster.easyquartz.quartz;

import org.easycluster.easyquartz.Closure;

public class EchoClosure3 implements Closure {

	@Override
	public void execute(Object msg) {
		System.out.println("EchoClosure3 received msg " + msg);
	}

}
