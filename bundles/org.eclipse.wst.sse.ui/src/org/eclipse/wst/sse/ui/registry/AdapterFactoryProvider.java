/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Jens Lukowski/Innoopract - initial renaming/restructuring
 *     
 *******************************************************************************/
package org.eclipse.wst.sse.ui.registry;

import org.eclipse.wst.sse.core.IStructuredModel;
import org.eclipse.wst.sse.core.modelhandler.IDocumentTypeHandler;


public interface AdapterFactoryProvider {

	public void addAdapterFactories(IStructuredModel structuredModel);

	// TODO_issue: IDocumentTypeHandler doesn't seem correct in this API.
	// reexamine and see if should be ModelHandler, or ContentTypeIdentifer
	// instead.
	public boolean isFor(IDocumentTypeHandler contentTypeDescription);

	/**
	 * This method should only add those factories related to embedded content
	 * type
	 */
	public void reinitializeFactories(IStructuredModel structuredModel);
}
