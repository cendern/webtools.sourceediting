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
package org.eclipse.wst.sse.core;



public interface INodeAdapter {

	/**
	 * Allowing the INodeAdapter to compare itself against the type allows it
	 * to return true in more than one case.
	 */
	boolean isAdapterForType(Object type);

	/**
	 */
	void notifyChanged(INodeNotifier notifier, int eventType, Object changedFeature, Object oldValue, Object newValue, int pos);
}
