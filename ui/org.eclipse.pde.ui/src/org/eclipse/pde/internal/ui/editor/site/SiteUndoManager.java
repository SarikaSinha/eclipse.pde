/*******************************************************************************
 * Copyright (c) 2000, 2003 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.pde.internal.ui.editor.site;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.pde.core.*;
import org.eclipse.pde.core.IModelChangeProvider;
import org.eclipse.pde.internal.core.isite.*;
import org.eclipse.pde.internal.core.isite.ISiteModel;
import org.eclipse.pde.internal.core.site.SiteObject;
import org.eclipse.pde.internal.ui.PDEPlugin;
import org.eclipse.pde.internal.ui.editor.*;

/**
 * @version 	1.0
 * @author
 */
public class SiteUndoManager extends ModelUndoManager {
	public SiteUndoManager(SiteEditor editor) {
		super(editor);
		setUndoLevelLimit(30);
	}

	/*
	 * @see IModelUndoManager#execute(ModelUndoOperation)
	 */

	public void connect(IModelChangeProvider provider) {
		ISiteModel model = (ISiteModel) provider;
		ISiteBuildModel buildModel = model.getBuildModel();
		super.connect(provider);
		super.connect(buildModel);
	}
	
	public void disconnect(IModelChangeProvider provider) {
		ISiteModel model = (ISiteModel)provider;
		ISiteBuildModel buildModel = model.getBuildModel();
		super.disconnect(provider);
		super.disconnect(buildModel);
	}

	protected String getPageId(Object obj) {
		return FeaturesPage.PAGE_ID;
	}

	protected void execute(IModelChangedEvent event, boolean undo) {
		ISiteModel model = (ISiteModel)event.getChangeProvider();
		Object[] elements = event.getChangedObjects();
		int type = event.getChangeType();
		String propertyName = event.getChangedProperty();

		switch (type) {
			case IModelChangedEvent.INSERT :
				if (undo)
					executeRemove(model, elements);
				else
					executeAdd(model, elements);
				break;
			case IModelChangedEvent.REMOVE :
				if (undo)
					executeAdd(model, elements);
				else
					executeRemove(model, elements);
				break;
			case IModelChangedEvent.CHANGE :
				if (undo)
					executeChange(
						elements[0],
						propertyName,
						event.getNewValue(),
						event.getOldValue());
				else
					executeChange(
						elements[0],
						propertyName,
						event.getOldValue(),
						event.getNewValue());
		}
	}

	private void executeAdd(ISiteModel model, Object[] elements) {
		ISite site = model.getSite();
		ISiteBuild siteBuild = model.getBuildModel().getSiteBuild();

		try {
			for (int i = 0; i < elements.length; i++) {
				Object element = elements[i];

				if (element instanceof ISiteFeature) {
					site.addFeatures(new ISiteFeature [] {(ISiteFeature) element});
				} else if (element instanceof ISiteBuildFeature) {
					siteBuild.addFeatures(new ISiteBuildFeature [] {(ISiteBuildFeature) element});
				} else if (element instanceof ISiteArchive) {
					site.addArchives(new ISiteArchive[] {(ISiteArchive) element});
				} else if (element instanceof ISiteCategoryDefinition) {
					site.addCategoryDefinitions(new ISiteCategoryDefinition[] {(ISiteCategoryDefinition) element});
				} else if (element instanceof ISiteCategory) {
					ISiteCategory category = (ISiteCategory)element;
					ISiteFeature feature = (ISiteFeature)category.getParent();
					feature.addCategories(new ISiteCategory[] {category});
				}
			}
		} catch (CoreException e) {
			PDEPlugin.logException(e);
		}
	}

	private void executeRemove(ISiteModel model, Object[] elements) {
		ISite site = model.getSite();
		ISiteBuild siteBuild = model.getBuildModel().getSiteBuild();

		try {
			for (int i = 0; i < elements.length; i++) {
				Object element = elements[i];

				if (element instanceof ISiteFeature) {
					site.removeFeatures(new ISiteFeature [] {(ISiteFeature) element});
				} else if (element instanceof ISiteBuildFeature) {
					siteBuild.removeFeatures(new ISiteBuildFeature [] {(ISiteBuildFeature) element});
				} else if (element instanceof ISiteArchive) {
					site.removeArchives(new ISiteArchive[] {(ISiteArchive) element});
				} else if (element instanceof ISiteCategoryDefinition) {
					site.removeCategoryDefinitions(new ISiteCategoryDefinition[] {(ISiteCategoryDefinition) element});
				} else if (element instanceof ISiteCategory) {
					ISiteCategory category = (ISiteCategory)element;
					ISiteFeature feature = (ISiteFeature)category.getParent();
					feature.removeCategories(new ISiteCategory[] {category});
				}
			}
		} catch (CoreException e) {
			PDEPlugin.logException(e);
		}
	}

	private void executeChange(
		Object element,
		String propertyName,
		Object oldValue,
		Object newValue) {

		if (element instanceof SiteObject) {
			SiteObject sobj = (SiteObject) element;
			try {
				sobj.restoreProperty(propertyName, oldValue, newValue);
			} catch (CoreException e) {
				PDEPlugin.logException(e);
			}
		}
	}

	public void modelChanged(IModelChangedEvent event) {
		if (event.getChangeType() == IModelChangedEvent.CHANGE) {
			Object object = event.getChangedObjects()[0];
			if (object instanceof ISiteObject) {
				ISiteObject obj = (ISiteObject) object;
				//Ignore events from objects that are not yet in the model.
				if (!(obj instanceof ISite) && !obj.isInTheModel())
					return;
			}
		}
		super.modelChanged(event);
	}
}
