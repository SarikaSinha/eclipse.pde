package org.eclipse.pde.internal.ui.model;

import org.eclipse.jface.text.*;
import org.eclipse.pde.core.*;
import org.eclipse.pde.internal.ui.editor.text.*;

/**
 * @author melhem
 *
 */
public interface IEditingModel extends IModel, IModelChangeProvider, IReconcilingParticipant, IEditable {

public IDocument getDocument();
}
