package org.eclipse.pde.internal.ui.editor.schema;
/*
 * (c) Copyright IBM Corp. 2000, 2001.
 * All Rights Reserved.
 */

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.*;
import org.eclipse.jface.text.rules.DefaultPartitioner;
import org.eclipse.jface.text.source.*;
import org.eclipse.jface.viewers.*;
import org.eclipse.pde.core.IEditable;
import org.eclipse.pde.internal.core.ischema.*;
import org.eclipse.pde.internal.core.schema.*;
import org.eclipse.pde.internal.ui.PDEPlugin;
import org.eclipse.pde.internal.ui.editor.*;
import org.eclipse.pde.internal.ui.editor.text.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.update.ui.forms.internal.*;

public class DescriptionSection extends PDEFormSection {
	private SourceViewer editor;
	private Button applyButton;
	private Button resetButton;
	private IDocument document;
	private boolean editable = true;
	private SourceViewerConfiguration sourceConfiguration;
	private ISchemaObject element;
	private SourceViewer sourceViewer;
	public static final String SECTION_TITLE =
		"SchemaEditor.DescriptionSection.title";
	public static final String KEY_PENDING_TITLE =
		"SchemaEditor.DescriptionSection.pending.title";
	public static final String KEY_APPLY = "Actions.apply.flabel";
	public static final String KEY_RESET = "Actions.reset.flabel";
	public static final String KEY_PENDING_MESSAGE =
		"SchemaEditor.DescriptionSection.pending.message";
	public static final String SECTION_DESC =
		"SchemaEditor.DescriptionSection.desc";
	private IDocumentPartitioner partitioner;
	private ISchema schema;
	private boolean ignoreChange = false;
	private IColorManager colorManager;

	public DescriptionSection(PDEFormPage page, IColorManager colorManager) {
		super(page);
		setHeaderText(PDEPlugin.getResourceString(SECTION_TITLE));
		setDescription(PDEPlugin.getResourceString(SECTION_DESC));
		this.colorManager = colorManager;
		sourceConfiguration = new XMLConfiguration(colorManager);
		document = new Document();
		partitioner =
			new DefaultPartitioner(
				new PDEPartitionScanner(),
				new String[] {
					PDEPartitionScanner.XML_TAG,
					PDEPartitionScanner.XML_COMMENT });
		partitioner.connect(document);
		document.setDocumentPartitioner(partitioner);
	}
	private void checkForPendingChanges() {
		if (applyButton.isEnabled()) {
			if (MessageDialog
				.openQuestion(
					PDEPlugin.getActiveWorkbenchShell(),
					PDEPlugin.getResourceString(KEY_PENDING_TITLE),
					PDEPlugin.getResourceString(KEY_PENDING_MESSAGE))
				== true)
				handleApply();
		}
	}
	public void commitChanges(boolean onSave) {
		handleApply();
		setDirty(false);
	}
	public Composite createClient(
		Composite parent,
		FormWidgetFactory factory) {
		Composite container = factory.createComposite(parent);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.marginWidth = 2;
		container.setLayout(layout);
		GridData gd;
		int styles =
			SWT.MULTI
				| SWT.WRAP
				| SWT.V_SCROLL
				| SWT.H_SCROLL
				| FormWidgetFactory.BORDER_STYLE;
		sourceViewer = new SourceViewer(container, null, styles);
		sourceViewer.configure(sourceConfiguration);
		sourceViewer.setDocument(document);
		sourceViewer.setEditable(isEditable());
		sourceViewer
			.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				updateSelection(event.getSelection());
			}
		});
		Control styledText = sourceViewer.getTextWidget();
		styledText.setFont(
			JFaceResources.getFontRegistry().get(JFaceResources.TEXT_FONT));
		if (SWT.getPlatform().equals("motif") == false)
			factory.paintBordersFor(container);
		Control[] children = container.getChildren();
		Control control = children[children.length - 1];
		gd = new GridData(GridData.FILL_BOTH);
		control.setLayoutData(gd);
		styledText.setMenu(getFormPage().getEditor().getContextMenu());
		styledText.addFocusListener(new FocusAdapter() {
			public void focusGained(FocusEvent e) {
				updateSelection(sourceViewer.getSelection());
			}
		});

		Composite buttonContainer = factory.createComposite(container);
		layout = new GridLayout();
		layout.marginHeight = 0;
		buttonContainer.setLayout(layout);
		gd = new GridData(GridData.FILL_VERTICAL);
		buttonContainer.setLayoutData(gd);

		applyButton =
			factory.createButton(
				buttonContainer,
				PDEPlugin.getResourceString(KEY_APPLY),
				SWT.PUSH);
		applyButton.setEnabled(false);
		gd =
			new GridData(
				GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING);
		applyButton.setLayoutData(gd);
		applyButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				handleApply();
			}
		});

		resetButton =
			factory.createButton(
				buttonContainer,
				PDEPlugin.getResourceString(KEY_RESET),
				SWT.PUSH);
		resetButton.setEnabled(false);
		gd =
			new GridData(
				GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING);
		resetButton.setLayoutData(gd);
		resetButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				handleReset();
			}
		});
		return container;
	}

	private void updateSelection(ISelection selection) {
		getFormPage().getEditor().setSelection(selection);
	}

	public boolean doGlobalAction(String actionId) {
		if (actionId.equals(org.eclipse.ui.IWorkbenchActionConstants.CUT)) {
			sourceViewer.doOperation(SourceViewer.CUT);
			return true;
		} else if (
			actionId.equals(org.eclipse.ui.IWorkbenchActionConstants.COPY)) {
			sourceViewer.doOperation(SourceViewer.COPY);
			return true;
		} else if (
			actionId.equals(org.eclipse.ui.IWorkbenchActionConstants.PASTE)) {
			sourceViewer.doOperation(SourceViewer.PASTE);
			return true;
		} else if (
			actionId.equals(org.eclipse.ui.IWorkbenchActionConstants.SELECT_ALL)) {
			sourceViewer.doOperation(SourceViewer.SELECT_ALL);
			return true;
		} else if (
			actionId.equals(org.eclipse.ui.IWorkbenchActionConstants.DELETE)) {
			sourceViewer.doOperation(SourceViewer.DELETE);
			return true;
		} else if (
			actionId.equals(org.eclipse.ui.IWorkbenchActionConstants.UNDO)) {
			sourceViewer.doOperation(SourceViewer.UNDO);
			return true;
		} else if (
			actionId.equals(org.eclipse.ui.IWorkbenchActionConstants.REDO)) {
			sourceViewer.doOperation(SourceViewer.REDO);
			return true;
		}
		return false;
	}
	protected void fillContextMenu(IMenuManager manager) {
		getFormPage().getEditor().getContributor().contextMenuAboutToShow(
			manager);
	}
	private void handleApply() {
		if (element != null) {
			if (element == schema) {
				((Schema)schema).setDescription(document.get());
			}
			else {
			 ((SchemaObject) element).setDescription(document.get());
			}
		}
		applyButton.setEnabled(false);
		resetButton.setEnabled(false);
	}
	private void handleReset() {
		updateDocument();
	}
	public void initialize(Object input) {
		schema = (ISchema) input;
		element = schema;
		updateDocument();
		document.addDocumentListener(new IDocumentListener() {
			public void documentChanged(DocumentEvent e) {
				if (!ignoreChange && schema instanceof IEditable) {
					setDirty(true);
					((IEditable) schema).setDirty(true);
					getFormPage().getEditor().fireSaveNeeded();
				}
				applyButton.setEnabled(true);
				resetButton.setEnabled(true);
			}
			public void documentAboutToBeChanged(DocumentEvent e) {
			}
		});
	}
	public boolean isEditable() {
		return editable;
	}
	public void sectionChanged(
		FormSection source,
		int changeType,
		Object changeObject) {
		checkForPendingChanges();
		if (!(source instanceof ElementSection))
			return;
		if (changeType != FormSection.SELECTION)
			return;
		element = (ISchemaObject) changeObject;
		if (element == null)
			element = schema;
		updateDocument();
	}
	public void setFocus() {
		sourceViewer.getTextWidget().setFocus();
	}
	public void setEditable(boolean newEditable) {
		editable = newEditable;
	}
	public void updateDocument() {
		ignoreChange = true;
		String text = element.getDescription();
		if (text == null)
			text = "";
		/*
		else
			text = TextUtil.createMultiLine(text, 60, false);
		*/
		document.set(text);
		resetButton.setEnabled(false);
		applyButton.setEnabled(false);
		ignoreChange = false;
		ISchemaObject eobj = element;
		if (element instanceof ISchemaAttribute) {
			eobj = element.getParent();
		}
		//sourceViewer.setEditable(eobj.getName().equals("extension")==false);
	}

	public boolean canPaste(Clipboard clipboard) {
		return sourceViewer.canDoOperation(SourceViewer.PASTE);
	}
}