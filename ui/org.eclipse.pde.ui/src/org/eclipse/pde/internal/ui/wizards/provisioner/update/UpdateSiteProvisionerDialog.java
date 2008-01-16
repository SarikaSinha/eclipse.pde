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
package org.eclipse.pde.internal.ui.wizards.provisioner.update;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.StatusDialog;
import org.eclipse.pde.core.plugin.TargetPlatform;
import org.eclipse.pde.internal.ui.PDEPlugin;
import org.eclipse.pde.internal.ui.PDEUIMessages;
import org.eclipse.pde.internal.ui.util.SWTUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

public class UpdateSiteProvisionerDialog extends StatusDialog {

	private Text fInstallLocationText;
	private Label fInstallLocationLabel;
	private Text fSiteLocationText;
	private Label fSiteLocationLabel;
	private IUpdateSiteProvisionerEntry fEntry;
	private IStatus fOkStatus;
	private IStatus fErrorStatus;

	private String fInstallLocation;
	private String fSiteLocation;

	public UpdateSiteProvisionerDialog(Shell parent, String installLocation, String siteLocation, String title) {
		super(parent);
		setShellStyle(getShellStyle() | SWT.RESIZE);
		fInstallLocation = installLocation;
		fSiteLocation = siteLocation;
		setTitle(title);
	}

	protected Control createDialogArea(Composite parent) {
		Composite container = new Composite(parent, SWT.RESIZE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		layout.marginHeight = layout.marginWidth = 10;
		container.setLayout(layout);
		container.setLayoutData(new GridData(GridData.FILL_BOTH));

		createEntry(container);

		// add modify listeners
		ModifyListener listener = new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				dialogChanged();
			}
		};
		fInstallLocationText.addModifyListener(listener);
		fSiteLocationText.addModifyListener(listener);
		Dialog.applyDialogFont(container);

		dialogChanged();

		return container;
	}

	protected void createEntry(Composite container) {
		fSiteLocationLabel = new Label(container, SWT.NONE);
		fSiteLocationLabel.setText(PDEUIMessages.UpdateSiteProvisionerDialog_siteLocation);

		fSiteLocationText = new Text(container, SWT.SINGLE | SWT.BORDER);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		fSiteLocationText.setLayoutData(gd);
		if (fSiteLocation != null)
			fSiteLocationText.setText(fSiteLocation);

		fInstallLocationLabel = new Label(container, SWT.NULL);
		fInstallLocationLabel.setText(PDEUIMessages.UpdateSiteProvisionerDialog_installLocation);

		fInstallLocationText = new Text(container, SWT.SINGLE | SWT.BORDER);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.widthHint = 250;
		fInstallLocationText.setLayoutData(gd);
		if (fInstallLocation != null)
			fInstallLocationText.setText(fInstallLocation);

		Button fs = new Button(container, SWT.PUSH);
		fs.setText(PDEUIMessages.UpdateSiteProvisionerDialog_fileSystem);
		fs.setLayoutData(new GridData());
		fs.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				handleBrowseFileSystem();
			}
		});
		SWTUtil.setButtonDimensionHint(fs);
	}

	private IStatus createErrorStatus(String message) {
		return new Status(IStatus.ERROR, PDEPlugin.getPluginId(), IStatus.OK, message, null);
	}

	private void dialogChanged() {
		IStatus status = null;
		if (fInstallLocationText.getText().length() == 0 && fSiteLocationText.getText().length() == 0)
			status = getEmptyErrorStatus();

		if (status == null)
			status = getOKStatus();
		updateStatus(status);
	}

	private IStatus getOKStatus() {
		if (fOkStatus == null)
			fOkStatus = new Status(IStatus.OK, PDEPlugin.getPluginId(), IStatus.OK, "", //$NON-NLS-1$
					null);
		return fOkStatus;
	}

	private IStatus getEmptyErrorStatus() {
		if (fErrorStatus == null)
			fErrorStatus = createErrorStatus(PDEUIMessages.UpdateSiteProvisionerDialog_missBothErrorMessage);
		return fErrorStatus;
	}

	protected void handleBrowseFileSystem() {
		DirectoryDialog dialog = new DirectoryDialog(getShell());

		String text = fInstallLocationText.getText();
		if (text == null || text.length() == 0) {
			dialog.setFilterPath(TargetPlatform.getLocation());
		} else {
			dialog.setFilterPath(fInstallLocationText.getText());
		}
		dialog.setText(PDEUIMessages.BaseBlock_dirSelection);
		dialog.setMessage(PDEUIMessages.BaseBlock_dirChoose);
		String result = dialog.open();
		if (result != null) {
			fInstallLocationText.setText(result);
		}
	}

	protected void okPressed() {
		fEntry = new UpdateSiteProvisionerEntry(fInstallLocationText.getText(), fSiteLocationText.getText());
		super.okPressed();
	}

	public IUpdateSiteProvisionerEntry getEntry() {
		return fEntry;
	}

}
