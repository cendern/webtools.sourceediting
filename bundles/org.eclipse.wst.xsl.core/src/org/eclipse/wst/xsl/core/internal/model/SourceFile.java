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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;

public class SourceFile
{
	private static final int INCLUDE = 1;
	private static final int IMPORT = 2;
	
	final IFile file;
	final Map<IFile, SourceFile> sourceFiles;
	final List<Include> includes = new ArrayList<Include>();
	final List<Template> namedTemplates = new ArrayList<Template>();
	final List<Template> calledTemplates = new ArrayList<Template>();

	public SourceFile(Map<IFile, SourceFile> sourceFiles, IFile file)
	{
		this.file = file;
		this.sourceFiles = sourceFiles;
		sourceFiles.put(file,this);
	}

	public void addInclude(Include include)
	{
		includes.add(include);
	}

	public void addImport(Include include)
	{
		includes.add(include);
	}

	public void addNamedTemplate(Template template)
	{
		namedTemplates.add(template);
	}

	public void addCalledTemplate(Template template)
	{
		calledTemplates.add(template);
	}
	
	public IFile getFile()
	{
		return file;
	}

	public Map<IFile, SourceFile> getSourceFiles()
	{
		return sourceFiles;
	}

	public List<Include> getIncludes()
	{
		return includes;
	}

	public List<Template> getNamedTemplates()
	{
		return namedTemplates;
	}

	public List<Template> getCalledTemplates()
	{
		return calledTemplates;
	}

	public Map<String,List<Template>> calculateTemplates()
	{
		Map<String,List<Template>> templateMap = new HashMap<String,List<Template>>();
		calculateTemplates(templateMap,INCLUDE);
		return templateMap;
	}
	
	private void calculateTemplates(Map<String,List<Template>> templateMap, int type)
	{
		if (type == INCLUDE)
		{// add all named templates
			for (Template template : namedTemplates)
			{
				List<Template> list = templateMap.get(template.name);
				if (list == null)
				{
					list = new ArrayList<Template>();
					templateMap.put(template.name, list);
				}
				list.add(template);
			}
		}
		else if (type == IMPORT)
		{
			for (Template template : namedTemplates)
			{// only add if not over-ridden
				if (!templateMap.containsKey(template.name))
				{
					List<Template> list = new ArrayList<Template>();
					list.add(template);
					templateMap.put(template.name, list);
				}
			}			
		}
		for (Include include : includes)
		{
			// for includes, just add all templates
			SourceFile sf = include.getSourceFile();
			sf.calculateTemplates(templateMap,include.getType());
		}		
	}
}
