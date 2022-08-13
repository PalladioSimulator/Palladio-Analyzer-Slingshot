package org.palladiosimulator.analyzer.slingshot.ui;

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class SlingshotUIPlugin extends Plugin implements BundleActivator {

	private static SlingshotUIPlugin instance = null;

	@Override
	public void start(BundleContext context) throws Exception {
		instance = this;
		System.out.println("Slingshot UI Plugin started");
		super.start(context);
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		instance = null;
		super.stop(context);
	}
	
	
	
}
