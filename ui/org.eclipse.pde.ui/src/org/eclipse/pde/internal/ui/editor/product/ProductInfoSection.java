package org.eclipse.pde.internal.ui.editor.product;

import org.eclipse.pde.core.IBaseModel;
import org.eclipse.pde.internal.core.iproduct.IProduct;
import org.eclipse.pde.internal.core.iproduct.IProductModel;
import org.eclipse.pde.internal.ui.editor.FormEntryAdapter;
import org.eclipse.pde.internal.ui.editor.PDEFormPage;
import org.eclipse.pde.internal.ui.editor.PDESection;
import org.eclipse.pde.internal.ui.parts.FormEntry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;


public class ProductInfoSection extends PDESection {

	private FormEntry fNameEntry;
	private FormEntry fIdEntry;
	private FormEntry fAppIdEntry;

	public ProductInfoSection(PDEFormPage page, Composite parent) {
		super(page, parent, Section.DESCRIPTION);
		createClient(getSection(), page.getEditor().getToolkit());
	}

	/* (non-Javadoc)
	 * @see org.eclipse.pde.internal.ui.editor.PDESection#createClient(org.eclipse.ui.forms.widgets.Section, org.eclipse.ui.forms.widgets.FormToolkit)
	 */
	protected void createClient(Section section, FormToolkit toolkit) {
		section.setText("Product Definition");
		TableWrapData td = new TableWrapData(TableWrapData.FILL, TableWrapData.TOP);
		td.grabHorizontal = true;
		section.setLayoutData(td);
		section.setDescription("Define the product and the default application that will run when running the product:");
		
		Composite client = toolkit.createComposite(section);
		GridLayout layout = new GridLayout();
		layout.marginWidth = toolkit.getBorderStyle() != SWT.NULL ? 0 : 2;
		layout.numColumns = 3;
		client.setLayout(layout);

		section.setClient(client);
		
		IActionBars actionBars = getPage().getPDEEditor().getEditorSite().getActionBars();
		createNameEntry(client, toolkit, actionBars);
		createIdEntry(client, toolkit, actionBars);
		createApplicationEntry(client, toolkit, actionBars);
		toolkit.paintBordersFor(client);
	}
	
	private void createNameEntry(Composite client, FormToolkit toolkit, IActionBars actionBars) {
		fNameEntry = new FormEntry(client, toolkit, "Product Name:", null, false); //$NON-NLS-1$
		fNameEntry.setFormEntryListener(new FormEntryAdapter(this, actionBars) {
			public void textValueChanged(FormEntry entry) {
				getProduct().setName(entry.getValue());
			}
		});
		fNameEntry.setEditable(isEditable());
	}
	
	private void createIdEntry(Composite client, FormToolkit toolkit, IActionBars actionBars) {
		fIdEntry = new FormEntry(client, toolkit, "Product ID:", "Browse...", false); //$NON-NLS-1$
		fIdEntry.setFormEntryListener(new FormEntryAdapter(this, actionBars) {
			public void textValueChanged(FormEntry entry) {
				getProduct().setId(entry.getValue());
			}
			public void browseButtonSelected(FormEntry entry) {			
			}
		});
		fIdEntry.setEditable(isEditable());
	}

	private void createApplicationEntry(Composite client, FormToolkit toolkit, IActionBars actionBars) {
		fAppIdEntry = new FormEntry(client, toolkit, "Application ID:", "Browse...", false); //$NON-NLS-1$
		fAppIdEntry.setFormEntryListener(new FormEntryAdapter(this, actionBars) {
			public void textValueChanged(FormEntry entry) {
				getProduct().setApplication(entry.getValue());
			}
			public void browseButtonSelected(FormEntry entry) {				
			}
		});
		fAppIdEntry.setEditable(isEditable());
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.forms.AbstractFormPart#commit(boolean)
	 */
	public void commit(boolean onSave) {
		fNameEntry.commit();
		fIdEntry.commit();
		fAppIdEntry.commit();
		super.commit(onSave);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.pde.internal.ui.editor.PDESection#cancelEdit()
	 */
	public void cancelEdit() {
		fNameEntry.cancelEdit();
		fIdEntry.cancelEdit();
		fAppIdEntry.cancelEdit();
		super.cancelEdit();
	}
	
	private IProduct getProduct() {
		IBaseModel model = getPage().getPDEEditor().getAggregateModel();
		return ((IProductModel)model).getProduct();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.forms.AbstractFormPart#refresh()
	 */
	public void refresh() {
		IProduct product = getProduct();
		fNameEntry.setValue(product.getName(), true);
		fIdEntry.setValue(product.getId(), true);
		fAppIdEntry.setValue(product.getApplication(), true);
		super.refresh();
	}

}
