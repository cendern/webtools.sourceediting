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
package org.eclipse.wst.sse.ui.views.contentoutline;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.texteditor.IUpdate;

/**
 * An IAction.AS_CHECK_BOX action that sets and gets its checked state along
 * with a value from a preference store. Should be used with
 * PropertyChangeUpdateActionContributionItem to listen to changes in the
 * store.
 */
public class PropertyChangeUpdateAction extends Action implements IUpdate {
	private String fPreferenceKey;
	private IPreferenceStore fStore;
	private boolean fUpdateFromPropertyChange = true;

	public PropertyChangeUpdateAction(String text, IPreferenceStore store, String preferenceKey, boolean defaultValue) {
		super(text, IAction.AS_CHECK_BOX);
		fPreferenceKey = preferenceKey;
		fStore = store;
		fStore.setDefault(getPreferenceKey(), defaultValue);
		setChecked(getPreferenceStore().getBoolean(getPreferenceKey()));
	}

	/**
	 * @return Returns the orderPreferenceKey.
	 */
	public String getPreferenceKey() {
		return fPreferenceKey;
	}

	/**
	 * @return Returns the store.
	 */
	public IPreferenceStore getPreferenceStore() {
		return fStore;
	}

	/**
	 * @return Returns the updateFromPropertyChange.
	 */
	public boolean isUpdateFromPropertyChange() {
		return fUpdateFromPropertyChange;
	}

	public final void run() {
		super.run();
		fStore.setValue(getPreferenceKey(), isChecked());
		if (!isUpdateFromPropertyChange())
			update();
	}

	/**
	 * @param updateFromPropertyChange
	 *            The updateFromPropertyChange to set.
	 */
	public void setUpdateFromPropertyChange(boolean updateFromPropertyChange) {
		fUpdateFromPropertyChange = updateFromPropertyChange;
	}

	public void update() {
		setChecked(fStore.getBoolean(getPreferenceKey()));
	}

}
