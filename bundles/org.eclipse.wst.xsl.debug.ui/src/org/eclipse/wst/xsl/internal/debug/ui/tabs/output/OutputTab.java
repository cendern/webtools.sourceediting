/*******************************************************************************
 * Copyright (c) 2007 Chase Technology Ltd - http://www.chasetechnology.co.uk
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Doug Satchwell (Chase Technology Ltd) - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsl.internal.debug.ui.tabs.output;

import org.eclipse.debug.ui.ILaunchConfigurationTab;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.wst.xsl.internal.debug.ui.XSLLaunchConfigurationTab;
import org.eclipse.wst.xsl.internal.debug.ui.tabs.main.XSLMainTab;

public class OutputTab extends XSLLaunchConfigurationTab
{
	private final OutputPropertiesBlock outputBlock;
	private final OutputFileBlock outputFileBlock;

	public OutputTab(XSLMainTab main)
	{
		outputFileBlock = new OutputFileBlock();
		outputBlock = new OutputPropertiesBlock(main);

		setBlocks(new ILaunchConfigurationTab[]
		{ outputBlock, outputFileBlock });
	}

	@Override
	public void createControl(Composite parent)
	{
		super.createControl(parent);
		Composite comp = (Composite) getControl();
		GridLayout layout = new GridLayout(1, false);
		comp.setLayout(layout);

		outputFileBlock.createControl(comp);

		Group group = new Group(comp, SWT.NULL);
		group.setText(Messages.OutputTab_0);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		group.setLayoutData(gd);
		layout = new GridLayout(2, false);
		group.setLayout(layout);

		outputBlock.createControl(group);
	}

	public String getName()
	{
		return Messages.OutputTab_1;
	}
}
