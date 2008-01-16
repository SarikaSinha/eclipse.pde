/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.pde.ui;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.pde.core.plugin.IPluginModelBase;
import org.eclipse.pde.core.plugin.IPluginReference;

/**
 * Classes that implement this interface are contributed via the extension point
 * <code>org.eclipse.pde.ui.pluginContent</code>. The expectation is that
 * classes also extend JFace Wizard class. The role of this wizard is to provide
 * additional plug-in content after the project and the critical plug-in project
 * files have been created. The wizard is nested in the overall 'New' wizard and
 * can contribute one or more pages that allow users to configure how this
 * content will be generated. A typical implementation of this interface would
 * be a template wizard that populates the plug-in project with content that can
 * be useful right away (for example, a view or an editor extension).
 * <p>
 * Due to the call order of the method <code>performFinish</code> in nested
 * wizards, classes that implement this interface should not place the code that
 * generates new content in the implementation of the abstract method
 * <code>Wizard.performFinish()</code>. Instead, they should simply return
 * <code>true</code> and have all the real code in <code>performFinish</code>
 * defined in this interface. This version of the method passes all the context
 * required for the content generation and is called AFTER the project and vital
 * plug-in files have been already created.
 * 
 * @since 3.0
 */
public interface IPluginContentWizard extends IBasePluginWizard {
	/**
	 * Initializes the wizard with the data collected from the parent wizard
	 * pages. The data can be used to customize the generated content.
	 * 
	 * @param data
	 *            all the information collected in the parent wizard that can be
	 *            used in configuring the generated content
	 */
	void init(IFieldData data);

	/**
	 * Returns new dependencies that are required by this wizard. If the wizard
	 * adds extensions or code to the plug-in that depend on other plug-ins, it
	 * must report it by returning a reference to that plug-in. This information
	 * will be used to compose a correct list of plug-in dependencies so that
	 * the plug-in compiles without errors in the first build after creation.
	 * 
	 * @param schemaVersion
	 *            version of the plug-in manifest, or <samp>null </samp> if
	 *            older manifest (prior to 3.0) has been created. Depending on
	 *            the manifest version, the list of dependencies may vary.
	 * @return the array of plug-in dependencies required by this wizard
	 */
	IPluginReference[] getDependencies(String schemaVersion);

	/**
	 * The wizard is required to return an array of new file and folder names
	 * that are generated by it for the purpose of inclusion in
	 * <code>build.properties</code> file. All files and folders that must be
	 * part of the binary build must be listed in <code>bin.includes</code>
	 * variable. Since the tokens obtained by this method will be passed to the
	 * variable as-is, it is legal to use all wild cards also legal in
	 * <code>build.properties,
	 * such as "*.gif".
	 * 
	 * @return an array of new file and folder names
	 */
	String[] getNewFiles();

	/**
	 * Executes the wizard when 'Finish' button has been pressed. Note that you
	 * should put all your working code in this method and not in
	 * 'performFinish' of the Wizard class due to the wrong calling order. In
	 * addition, this method provides progress monitor so that the execution of
	 * the content wizard can be reported as a part of the overall new project
	 * creation operation.
	 * 
	 * @param project
	 *            the newly created plug-in project handle
	 * @param model
	 *            the model of the plug-in manifest that can be used to add
	 *            extension markup
	 * @param monitor
	 *            the progress monitor to track progress of the content
	 *            generation
	 * @return <code>true</code> if the content has been generated
	 *         successfully, <code>false</code> otherwise. In case of failure,
	 *         the wizard dialog will stay open.
	 */
	boolean performFinish(IProject project, IPluginModelBase model, IProgressMonitor monitor);

}
