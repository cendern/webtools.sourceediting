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
package org.eclipse.wst.sse.core.format;

import org.eclipse.core.runtime.IProgressMonitor;
import org.w3c.dom.Node;

public interface IStructuredFormatter {
	void format(Node node);

	void format(Node node, IStructuredFormatContraints formatContraints);

	IStructuredFormatContraints getFormatContraints();

	IStructuredFormatPreferences getFormatPreferences();

	void setFormatPreferences(IStructuredFormatPreferences formatPreferences);

	void setProgressMonitor(IProgressMonitor monitor);
}
