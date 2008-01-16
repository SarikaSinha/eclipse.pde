/*******************************************************************************
 * Copyright (c) 2006, 2007 IBM Corporation and others.
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
import org.eclipse.pde.internal.ui.util.PDELabelUtility;
import org.eclipse.pde.internal.ui.wizards.cheatsheet.BaseCSCreationOperation;

/**
 * SimpleCSAddStepAction
 *
 */
public class SimpleCSAddStepAction extends Action {

	private ISimpleCS fCheatsheet;

	private ISimpleCSItem fItem;

	private ISimpleCSIntro fIntro;

	/**
	 * 
	 */
	public SimpleCSAddStepAction() {
		setText(PDEUIMessages.SimpleCSAddStepAction_0);
	}

	/**
	 * @param csObject
	 */
	public void setDataObject(ISimpleCSObject csObject) {
		// Determine input
		if (csObject.getType() == ISimpleCSConstants.TYPE_CHEAT_SHEET) {
			fIntro = null;
			fItem = null;
			fCheatsheet = (ISimpleCS) csObject;
		} else if (csObject.getType() == ISimpleCSConstants.TYPE_ITEM) {
			fIntro = null;
			fItem = (ISimpleCSItem) csObject;
			fCheatsheet = fItem.getSimpleCS();
		} else if (csObject.getType() == ISimpleCSConstants.TYPE_INTRO) {
			fIntro = (ISimpleCSIntro) csObject;
			fItem = null;
			fCheatsheet = fIntro.getSimpleCS();
		} else {
			// Invalid input, action will not run
			fIntro = null;
			fItem = null;
			fCheatsheet = null;
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.action.Action#run()
	 */
	public void run() {
		// Ensure we have valid input
		if (fCheatsheet == null) {
			return;
		}
		// Create the new item
		ISimpleCSItem newItem = createNewItem();
		// Insert the new item
		insertNewItem(newItem);
	}

	/**
	 * @param item
	 */
	private void insertNewItem(ISimpleCSItem newItem) {
		// Insert the new item depending on the input specfied
		if (fIntro != null) {
			// Intro input object
			// Insert item as the first cheat sheet child item which is the first
			// node after the introduction node
			fCheatsheet.addItem(0, newItem);
		} else if (fItem != null) {
			// Item input object
			// Insert item right after the input item object
			int index = fCheatsheet.indexOfItem(fItem) + 1;
			fCheatsheet.addItem(index, newItem);
		} else {
			// Cheat sheet input object
			// Add item as the last cheat sheet child item
			fCheatsheet.addItem(newItem);
		}
	}

	/**
	 * @return
	 */
	private ISimpleCSItem createNewItem() {
		ISimpleCSModelFactory factory = fCheatsheet.getModel().getFactory();
		// Create the new item
		// Element: item
		ISimpleCSItem item = factory.createSimpleCSItem(fCheatsheet);

		ISimpleCSItem[] items = fCheatsheet.getItems();
		String[] itemNames = new String[items.length];

		for (int i = 0; i < items.length; ++i) {
			itemNames[i] = items[i].getTitle();
		}

		item.setTitle(PDELabelUtility.generateName(itemNames, PDEUIMessages.SimpleCheatSheetCreationOperation_1));
		// Element: description
		ISimpleCSDescription description = factory.createSimpleCSDescription(item);
		description.setContent(BaseCSCreationOperation.formatTextBold(PDEUIMessages.SimpleCheatSheetCreationOperation_2));
		item.setDescription(description);
		return item;
	}
}
