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
package org.eclipse.wst.sse.ui.contentproperties;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public abstract class AbstractSubject implements INotify, ISubject {



	private static Map listenerList = new Hashtable();

	public synchronized void addListener(IContentSettingsListener listener) {
		listenerList.put(listener, listener);
	}

	/*
	 * @see IContentSettingsEventSubject#notifyContentSettingsListeners(Object)
	 */
	public synchronized void notifyListeners(org.eclipse.core.resources.IResource changedResource) {

		Set keys = listenerList.keySet();
		Iterator iter = keys.iterator();

		while (iter.hasNext()) {
			IContentSettingsListener csl = (IContentSettingsListener) iter.next();
			csl.contentSettingsChanged(changedResource);
		}
	}

	public synchronized void removeListener(IContentSettingsListener listener) {
		listenerList.remove(listener);
	}



}
