/*******************************************************************************
 * Copyright (c) 2005, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.pde.internal.ui.nls;

import java.util.Properties;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.pde.core.plugin.IFragmentModel;
import org.eclipse.pde.internal.core.text.IDocumentAttributeNode;
import org.eclipse.pde.internal.core.text.IDocumentTextNode;
import org.eclipse.pde.internal.core.text.bundle.ManifestHeader;
import org.eclipse.pde.internal.core.text.plugin.*;

public class ModelChangeElement {

	private static final String DELIM = "."; //$NON-NLS-1$
	private static final String KEY_PREFIX = "%"; //$NON-NLS-1$
	private static final String FRAGMENT_PREFIX = "f"; //$NON-NLS-1$

	private String fValue = ""; //$NON-NLS-1$
	private String fKey = ""; //$NON-NLS-1$
	private int fOffset = 0;
	private int fLength = 0;
	private boolean fExternalized = true;
	private ModelChange fParent;
	private Object fUnderlying;

	public ModelChangeElement(ModelChange parent, Object incoming) {
		fParent = parent;
		fUnderlying = incoming;
		if (incoming instanceof PluginElementNode) {
			PluginElementNode elem = (PluginElementNode) incoming;
			IDocumentTextNode text = elem.getTextNode();
			fValue = elem.getText();
			generateValidKey(elem.getParent().getName(), elem.getName());
			fOffset = text.getOffset();
			fLength = text.getLength();
		} else if (incoming instanceof PluginAttribute) {
			PluginAttribute attr = (PluginAttribute) incoming;
			fValue = attr.getValue();
			generateValidKey(attr.getEnclosingElement().getXMLTagName(), attr.getName());
			fOffset = attr.getValueOffset();
			fLength = attr.getValueLength();
		} else if (incoming instanceof PluginExtensionPointNode) {
			PluginExtensionPointNode extP = (PluginExtensionPointNode) incoming;
			fValue = extP.getName();
			generateValidKey("extension-point", "name"); //$NON-NLS-1$ //$NON-NLS-2$
			IDocumentAttributeNode attr = extP.getDocumentAttribute("name"); //$NON-NLS-1$
			fOffset = attr.getValueOffset();
			fLength = attr.getValueLength();
		} else if (incoming instanceof ManifestHeader) {
			ManifestHeader header = (ManifestHeader) incoming;
			fValue = header.getValue();
			generateValidKey(header.getName());
			fLength = fValue.length();
			fOffset = header.getOffset() + header.getLength() - header.getLineLimiter().length() - fLength;
		}
	}

	public String getKey() {
		return fKey;
	}

	public void setKey(String key) {
		fKey = key;
	}

	public String getValue() {
		return fValue;
	}

	public void setValue(String value) {
		fValue = value;
	}

	public boolean isExternalized() {
		return fExternalized;
	}

	public void setExternalized(boolean externalzied) {
		fExternalized = externalzied;
	}

	public int getOffset() {
		return fOffset;
	}

	public int getLength() {
		return fLength;
	}

	private void generateValidKey(String pre, String mid) {
		generateValidKey(pre + DELIM + mid);
	}

	private void generateValidKey(String key) {
		int suffix = 0;
		Properties properties = fParent.getProperties();
		String newKey = fParent.getParentModel() instanceof IFragmentModel ? key + DELIM + FRAGMENT_PREFIX : key + DELIM;
		while (properties.containsKey(newKey + suffix))
			suffix += 1;
		properties.setProperty(newKey + suffix, fValue);
		fKey = newKey + suffix;
	}

	public String getExternKey() {
		return KEY_PREFIX + fKey;
	}

	public boolean updateValue() {
		try {
			String key = getExternKey();
			if (fUnderlying instanceof PluginElementNode) {
				PluginElementNode elem = (PluginElementNode) fUnderlying;
				elem.setText(key);
			} else if (fUnderlying instanceof PluginAttribute) {
				PluginAttribute attr = (PluginAttribute) fUnderlying;
				String attrName = attr.getName();
				attr.getEnclosingElement().setXMLAttribute(attrName, key);
			} else if (fUnderlying instanceof PluginExtensionPointNode) {
				PluginExtensionPointNode extP = (PluginExtensionPointNode) fUnderlying;
				extP.setName(key);
			} else if (fUnderlying instanceof ManifestHeader) {
				ManifestHeader header = (ManifestHeader) fUnderlying;
				header.setValue(key);
			} else
				return false;
		} catch (CoreException e) {
			return false;
		}
		return true;
	}
}
