/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.pde.internal.ui.wizards.toc;

import java.lang.reflect.InvocationTargetException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.pde.internal.core.text.toc.*;
import org.eclipse.pde.internal.core.util.CoreUtility;
import org.eclipse.pde.internal.ui.*;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.*;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.ISetSelectionTarget;

public class TocOperation extends WorkspaceModifyOperation {

	private IFile fFile;
	private String fTocName;

	public TocOperation(IFile file, String tocName) {
		fFile = file;
		fTocName = tocName;
	}

	protected void execute(IProgressMonitor monitor) throws CoreException, InvocationTargetException, InterruptedException {
		TocModel model = new TocModel(CoreUtility.getTextDocument(fFile.getContents()), false);
		model.setUnderlyingResource(fFile);
		initializeToc(model);
		model.save();
		model.dispose();
		openFile();
		monitor.done();
	}

	private void initializeToc(TocModel model) {
		// Create Topic
		TocTopic topic = createTopic(model);

		// Bind the created topic to this TOC
		model.getToc().addChild(topic);

		// Set the initial TOC name 
		model.getToc().setFieldLabel(fTocName);
	}

	private TocTopic createTopic(TocModel model) {
		TocDocumentFactory factory = model.getFactory();
		TocTopic topic = factory.createTocTopic();

		topic.setFieldLabel(PDEUIMessages.TocPage_TocTopic);

		return topic;
	}

	protected void openFile() {
		Display.getCurrent().asyncExec(new Runnable() {
			public void run() {
				IWorkbenchWindow ww = PDEPlugin.getActiveWorkbenchWindow();
				if (ww == null) {
					return;
				}
				IWorkbenchPage page = ww.getActivePage();
				if (page == null || !fFile.exists())
					return;
				IWorkbenchPart focusPart = page.getActivePart();
				if (focusPart instanceof ISetSelectionTarget) {
					ISelection selection = new StructuredSelection(fFile);
					((ISetSelectionTarget) focusPart).selectReveal(selection);
				}
				try {
					IDE.openEditor(page, fFile, IPDEUIConstants.TABLE_OF_CONTENTS_EDITOR_ID);
				} catch (PartInitException e) {
				}
			}
		});
	}

}
