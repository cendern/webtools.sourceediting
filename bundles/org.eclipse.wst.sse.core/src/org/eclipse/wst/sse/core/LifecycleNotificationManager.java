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

import org.eclipse.core.runtime.Platform;
import org.eclipse.wst.sse.core.internal.Logger;
import org.eclipse.wst.sse.core.util.Utilities;


/**
 * For "internal use" only by AbstractStructuredModel
 */

class LifecycleNotificationManager {
	static final String DEFAULT_TRACE_CATEGORY = "org.eclipse.wst.sse.core/structuredmodel/lifecycle";
	static final boolean DEBUG = "true".equalsIgnoreCase(Platform.getDebugOption(DEFAULT_TRACE_CATEGORY));


	private Object[] fListeners;

	LifecycleNotificationManager() {
		super();
	}

	/**
	 * Adds a new copy of the given listener to the list of Life Cycle
	 * Listeners.
	 * 
	 * Multiple copies of the same listener are allowed. This is required to
	 * support threaded listener management properly and for model-driven move
	 * to work. For example, two adds and a single remove should result in the
	 * listener still listening for events.
	 * 
	 * @param listener
	 */
	void addListener(IModelLifecycleListener listener) {
		if (Utilities.contains(fListeners, listener)) {
			Logger.log(Logger.WARNING_DEBUG, "IModelLifecycleListener " + listener + " listening more than once");
		}
		int oldSize = 0;
		if (fListeners != null) {
			// normally won't be null, but we need to be sure, for first
			// time through
			oldSize = fListeners.length;
		}
		int newSize = oldSize + 1;
		Object[] newListeners = new Object[newSize];
		if (fListeners != null) {
			System.arraycopy(fListeners, 0, newListeners, 0, oldSize);
		}
		// add listener to last position
		newListeners[newSize - 1] = listener;
		//
		// now switch new for old
		fListeners = newListeners;
	}

	/**
	 * Removes a single copy of the given listener from the list of Life Cycle
	 * Listeners.
	 * 
	 * @param listener
	 */
	void removeListener(IModelLifecycleListener listener) {
		if (Utilities.contains(fListeners, listener)) {
			// if its not in the listeners, we'll ignore the request
			int oldSize = fListeners.length;
			int newSize = oldSize - 1;
			Object[] newListeners = new Object[newSize];
			int index = 0;
			boolean removedOnce = false;
			for (int i = 0; i < oldSize; i++) {
				if (fListeners[i] == listener && !removedOnce) {
					// ignore on the first match
					removedOnce = true;
				} else {
					// copy old to new if it's not the one we are removing
					newListeners[index++] = fListeners[i];
				}
			}
			// now that we have a new array, let's switch it for the old
			// one
			fListeners = newListeners;
		}
		if (Utilities.contains(fListeners, listener)) {
			Logger.log(Logger.WARNING_DEBUG, "IModelLifecycleListener " + listener + " removed once but still listening");
		}
	}

	void signalLifecycleEvent(ModelLifecycleEvent event) {
		if (DEBUG) {
			Logger.trace(DEFAULT_TRACE_CATEGORY, "ModelLifecycleEvent fired for " + event.getModel().getId() + ": " + event.toString());
			System.out.println("ModelLifecycleEvent fired for " + event.getModel().getId() + ": " + event.toString());
		}
		// We must assign listeners to local variable, since the add and
		// remove listener methods can change the actual instance of the
		// listener array from another thread
		if (fListeners != null) {
			Object[] holdListeners = fListeners;
			for (int i = 0; i < holdListeners.length; i++) {
				IModelLifecycleListener listener = (IModelLifecycleListener) holdListeners[i];
				// only one type of listener for now ... this could become
				// more complex
				if ((event.getInternalType() & ModelLifecycleEvent.PRE_EVENT) == ModelLifecycleEvent.PRE_EVENT) {
					listener.processPreModelEvent(event);
				}
				if ((event.getInternalType() & ModelLifecycleEvent.POST_EVENT) == ModelLifecycleEvent.POST_EVENT) {
					listener.processPostModelEvent(event);
				}
			}
		}
	}
}
