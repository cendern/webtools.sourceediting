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
package org.eclipse.wst.dtd.ui.internal.editor;

import org.eclipse.wst.dtd.ui.DTDEditorPlugin;

/**
 * Help context ids for the DTD Source Editor.
 * <p>
 * This interface contains constants only; it is not intended to be
 * implemented.
 * </p>
 *  
 */
public interface IHelpContextIds {

	// org.eclipse.wst.dtd.ui.
	public static final String PREFIX = DTDEditorPlugin.getDefault().getBundle().getSymbolicName() + "."; //$NON-NLS-1$
	// DTD Files Preference page
	public static final String DTD_PREFWEBX_FILES_HELPID = PREFIX + "webx0020"; //$NON-NLS-1$
	// DTD Styles Preference page
	public static final String DTD_PREFWEBX_STYLES_HELPID = PREFIX + "webx0021"; //$NON-NLS-1$

	// DTD Source page editor
	public static final String DTD_SOURCEVIEW_HELPID = PREFIX + "edtd0000"; //$NON-NLS-1$
}
