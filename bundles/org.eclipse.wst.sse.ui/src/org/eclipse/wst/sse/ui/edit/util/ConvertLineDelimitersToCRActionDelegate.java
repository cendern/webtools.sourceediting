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
package org.eclipse.wst.sse.ui.edit.util;

public class ConvertLineDelimitersToCRActionDelegate extends ConvertLineDelimitersToCRLFActionDelegate {
	public ConvertLineDelimitersToCRActionDelegate() {
		super();

		setLineDelimiter("\r"); //$NON-NLS-1$
	}
}
