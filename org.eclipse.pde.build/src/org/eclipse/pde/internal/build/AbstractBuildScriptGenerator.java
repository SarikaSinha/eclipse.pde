package org.eclipse.pde.internal.build;
/*
 * (c) Copyright IBM Corp. 2000, 2001.
 * All Rights Reserved.
 */
import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.internal.plugins.InternalFactory;
import org.eclipse.core.internal.plugins.PluginRegistry;
import org.eclipse.core.runtime.*;
import org.eclipse.core.runtime.model.Factory;
import org.eclipse.core.runtime.model.PluginRegistryModel;


/**
 * 
 */
public abstract class AbstractBuildScriptGenerator extends AbstractBuildScriptGeneratorTemp {

	/**
	 * 
	 */
	protected BuildAntScript script;

	/**
	 * Where to find the elements.
	 */
	protected String installLocation;

	/**
	 * Location of the plug-ins and fragments.
	 */
	private URL[] pluginPath;

	/**
	 * Plug-in registry for the elements. Should only be accessed by getRegistry().
	 */
	private PluginRegistry registry;


public void setInstallLocation(String location) {
	this.installLocation = location;
}

protected PluginRegistry getRegistry() throws CoreException {
	if (registry == null) {
		URL[] pluginPath = getPluginPath();
		MultiStatus problems = new MultiStatus(PI_PDEBUILD, EXCEPTION_MODEL_PARSE, Policy.bind("exception.pluginParse"), null);
		InternalFactory factory = new InternalFactory(problems);
		registry = (PluginRegistry) Platform.parsePlugins(pluginPath, factory);
		registry.resolve(false, false);
		IStatus status = factory.getStatus();
		if (Utils.contains(status, IStatus.ERROR))
			throw new CoreException(status);
	}
	return registry;
}


protected URL[] getPluginPath() {
	// Get the plugin path if one was spec'd.
	if (pluginPath != null)
		return pluginPath;
	// Otherwise, if the install location was spec'd, compute the default path.
	if (installLocation != null) {
		try {
			StringBuffer sb = new StringBuffer();
			sb.append("file:");
			sb.append(installLocation);
			sb.append("/");
			sb.append(DEFAULT_PLUGIN_LOCATION);
			sb.append("/");
			return new URL[] { new URL(sb.toString()) };
		} catch (MalformedURLException e) {
		}
	}
	return null;
}







	




/**
 * Sets the pluginPath.
 */
public void setPluginPath(URL[] pluginPath) {
	this.pluginPath = pluginPath;
}




}