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
package org.eclipse.wst.xsl.core.internal.model;

/**
 * The <code>xsl:parameter</code> or <code>xsl:with-param</code> model element.
 * 
 * @author Doug Satchwell
 */
public class Parameter extends Variable
{
	private boolean valueSet;

	/**
	 * Create a new instance of this.
	 * 
	 * @param stylesheet the stylesheet that this belongs to
	 */
	public Parameter(Stylesheet stylesheet)
	{
		super(stylesheet);
	}
	
	/**
	 * Set whether this has a value.
	 * 
	 * @param valueSet <code>true</code> if this has a value
	 */
	public void setValue(boolean valueSet)
	{
		this.valueSet = valueSet;
	}

	/**
	 * Get whether this has a value.
	 * 
	 * @return <code>true</code> if this has a value
	 */
	public boolean isValue()
	{
		if (valueSet)
			return true;
		if (getAttributeValue("select") != null) //$NON-NLS-1$
			return true;
		return false; 
	}
}
