package org.palladiosimulator.analyzer.slingshot.ui;

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.palladiosimulator.analyzer.slingshot.core.Slingshot;

public class SlingshotUIPlugin extends Plugin implements BundleActivator {

	private static SlingshotUIPlugin instance = null;
	
	private Slingshot slingshot = null;

	@Override
	public void start(BundleContext context) throws Exception {
		instance = this;
		slingshot = Slingshot.getInstance();
		System.out.println("Slingshot UI Plugin started");
		super.start(context);
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		instance = null;
		slingshot = null;
		System.out.println("Slingshot UI Plugin ended.");
		super.stop(context);
	}
	
	
	
}
