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
package org.eclipse.wst.sse.ui.preferences.ui;

import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.Assert;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.help.WorkbenchHelp;
import org.eclipse.wst.sse.ui.EditorPlugin;
import org.eclipse.wst.sse.ui.internal.editor.IHelpContextIds;
import org.eclipse.wst.sse.ui.nls.ResourceHandler;
import org.eclipse.wst.sse.ui.preferences.CommonEditorPreferenceNames;


public class TranslucencyPreferenceTab implements IPreferenceTab {

	private PreferencePage fMainPreferencePage;
	private IntegerFieldEditor fTranslucencyScale = null;
	private final int MAX_PERCENTAGE = 100;

	private IPropertyChangeListener validityChangeListener = new IPropertyChangeListener() {
		public void propertyChange(PropertyChangeEvent event) {
			if (event.getProperty().equals(FieldEditor.IS_VALID))
				updateValidState();
		}
	};

	/**
	 *  
	 */
	public TranslucencyPreferenceTab() {
		super();
	}

	public TranslucencyPreferenceTab(PreferencePage mainPreferencePage) {
		Assert.isNotNull(mainPreferencePage);
		setMainPreferencePage(mainPreferencePage);
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.ui.preferences.ui.IPreferenceTab#createContents(org.eclipse.swt.widgets.Composite)
	 */
	public Control createContents(Composite tabFolder) {
		Composite composite = new Composite(tabFolder, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		composite.setLayout(layout);

		String text = ResourceHandler.getString("TranslucencyPreferenceTab.1"); //$NON-NLS-1$
		fTranslucencyScale = new IntegerFieldEditor(CommonEditorPreferenceNames.READ_ONLY_FOREGROUND_SCALE, text, composite);

		fTranslucencyScale.setErrorMessage(JFaceResources.getString("StringFieldEditor.errorMessage"));//$NON-NLS-1$
		fTranslucencyScale.setPreferenceStore(getPreferenceStore());
		fTranslucencyScale.setPreferencePage(getMainPreferencePage());
		fTranslucencyScale.setTextLimit(Integer.toString(MAX_PERCENTAGE).length());
		fTranslucencyScale.setValidateStrategy(StringFieldEditor.VALIDATE_ON_KEY_STROKE);
		fTranslucencyScale.setValidRange(0, MAX_PERCENTAGE);
		fTranslucencyScale.load();
		fTranslucencyScale.setPropertyChangeListener(validityChangeListener);

		WorkbenchHelp.setHelp(composite, IHelpContextIds.PREFWEBX_READONLY_HELPID);
		return composite;
	}

	/**
	 * @return Returns the fMainPreferencePage.
	 */
	private PreferencePage getMainPreferencePage() {
		return fMainPreferencePage;
	}

	/**
	 * @return Returns the preference store used in this tab
	 */
	private IPreferenceStore getPreferenceStore() {
		return EditorPlugin.getDefault().getPreferenceStore();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.ui.preferences.ui.IPreferenceTab#getTitle()
	 */
	public String getTitle() {
		return ResourceHandler.getString("TranslucencyPreferenceTab.0"); //$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.ui.preferences.ui.IPreferenceTab#performApply()
	 */
	public void performApply() {
		fTranslucencyScale.store();
		EditorPlugin.getDefault().savePluginPreferences();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.ui.preferences.ui.IPreferenceTab#performDefaults()
	 */
	public void performDefaults() {
		fTranslucencyScale.loadDefault();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.ui.preferences.ui.IPreferenceTab#performOk()
	 */
	public void performOk() {
		performApply();
	}

	/**
	 * @param mainPreferencePage
	 *            The fMainPreferencePage to set.
	 */
	private void setMainPreferencePage(PreferencePage mainPreferencePage) {
		fMainPreferencePage = mainPreferencePage;
	}

	private void updateValidState() {
		if (getMainPreferencePage() != null) {
			getMainPreferencePage().setValid(fTranslucencyScale.isValid());
		}
	}
}
