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
package org.eclipse.wst.sse.ui.internal.ui;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.texteditor.IAnnotationImageProvider;

/**
 * Image provider for structured text editor annotations.
 * 
 * @author pavery
 */
public class StructuredTextAnnotationImageProvider implements IAnnotationImageProvider {
	/**
	 * The descriptor currently has no associated image so that temporary
	 * annotations do not show up in the vertical ruler.
	 */
	private final String IMAGE_DESCRIPTOR_ID = "org.eclipse.wst.sse.temp.imageDescriptor"; //$NON-NLS-1$

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.texteditor.IAnnotationImageProvider#getImageDescriptor(java.lang.String)
	 */
	public ImageDescriptor getImageDescriptor(String imageDescritporId) {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.texteditor.IAnnotationImageProvider#getImageDescriptorId(org.eclipse.jface.text.source.Annotation)
	 */
	public String getImageDescriptorId(Annotation annotation) {
		String descriptorId = null;
		if (!annotation.isPersistent())
			// this allows temporary annotations to not show up in vertical
			// ruler
			descriptorId = IMAGE_DESCRIPTOR_ID;

		return descriptorId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.texteditor.IAnnotationImageProvider#getManagedImage(org.eclipse.jface.text.source.Annotation)
	 */
	public Image getManagedImage(Annotation annotation) {
		// future return different types of managed images as JDT does
		// eg. overlay icon images, "grayed" images, quick fixable, etc...
		return null;
	}

}
