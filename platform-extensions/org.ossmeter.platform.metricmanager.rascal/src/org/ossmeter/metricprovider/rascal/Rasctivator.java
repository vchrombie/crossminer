package org.ossmeter.metricprovider.rascal;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.ossmeter.platform.logging.OssmeterLoggerFactory;

public class Rasctivator implements BundleActivator {
	private static final Logger LOGGER = new OssmeterLoggerFactory().makeNewLoggerInstance("rascalLogger");
  private static BundleContext context;
	
	static BundleContext getContext() {
		return context;
	}

	@Override
	public void start(BundleContext bundleContext) throws Exception {
		Rasctivator.context = bundleContext;

		

	}
	
	public static void logException(Object message, Throwable cause) {
	  LOGGER.error(message, cause);
	}

	@Override
	public void stop(BundleContext bundleContext) throws Exception {
		Rasctivator.context = null;
	}

}
