/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.pde.internal.ui.editor.cheatsheet.simple.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.pde.internal.core.icheatsheet.simple.*;
import org.eclipse.pde.internal.ui.PDEUIMessages;

/**
 * SimpleCSAddStepAction
 *
 */
public class SimpleCSRemoveRunObjectAction extends Action {

	private ISimpleCSRunContainerObject fRunContainerObject;

	/**
	 * 
	 */
	public SimpleCSRemoveRunObjectAction() {
		setText(PDEUIMessages.SimpleCSRemoveStepAction_0);
//		setImageDescriptor(PDEPluginImages.DESC_GEL_SC_OBJ);
//		setToolTipText(PDEUIMessages.SchemaEditor_NewElement_tooltip);
	}

	/**
	 * @param cheatsheet
	 */
	public void setRunObject(ISimpleCSRunContainerObject runObject) {
		fRunContainerObject = runObject;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.action.Action#run()
	 */
	public void run() {
		if (fRunContainerObject != null) {
			// Determine parent type and remove accordingly 
			ISimpleCSObject parent = fRunContainerObject.getParent();
			if (parent.getType() == ISimpleCSConstants.TYPE_ITEM) {
				ISimpleCSItem item = (ISimpleCSItem) parent;
				item.setExecutable(null);
			} else if (parent.getType() == ISimpleCSConstants.TYPE_SUBITEM) {
				ISimpleCSSubItem subitem = (ISimpleCSSubItem) parent;
				subitem.setExecutable(null);
			} else if (parent.getType() == ISimpleCSConstants.TYPE_PERFORM_WHEN) {
				// Specifically for perform-when edge case
				// Action and command supported; but, will never be applicable
				if ((fRunContainerObject.getType() == ISimpleCSConstants.TYPE_ACTION) || (fRunContainerObject.getType() == ISimpleCSConstants.TYPE_COMMAND)) {
					ISimpleCSPerformWhen performWhen = (ISimpleCSPerformWhen) parent;
					performWhen.removeExecutable((ISimpleCSRunObject) fRunContainerObject);
				}
			}
		}
	}
}
