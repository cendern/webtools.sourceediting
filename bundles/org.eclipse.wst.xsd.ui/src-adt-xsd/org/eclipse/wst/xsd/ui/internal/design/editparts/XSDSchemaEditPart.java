/*******************************************************************************
 * Copyright (c) 2001, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.design.editparts;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.gef.EditPart;
import org.eclipse.wst.xsd.ui.internal.adapters.CategoryAdapter;
import org.eclipse.wst.xsd.ui.internal.adapters.XSDSchemaAdapter;
import org.eclipse.wst.xsd.ui.internal.adt.design.editparts.BaseEditPart;
import org.eclipse.wst.xsd.ui.internal.adt.typeviz.design.figures.HeadingFigure;
import org.eclipse.wst.xsd.ui.internal.design.layouts.FillLayout;
import org.eclipse.wst.xsd.ui.internal.editor.XSDEditorPlugin;
import org.eclipse.xsd.XSDSchema;

public class XSDSchemaEditPart extends BaseEditPart
{
  protected Label label;

  Figure outer, contentFigure;
  HeadingFigure headingFigure;

  public IFigure getContentPane()
  {
    return contentFigure;
  }

  protected IFigure createFigure()
  {
    outer = new Figure();
    // outer.setBorder(new RoundedLineBorder(1, 6));
    outer.setBorder(new LineBorder(1));

    FillLayout fillLayout = new FillLayout(4);
    outer.setLayoutManager(fillLayout);

    headingFigure = new HeadingFigure();
    outer.add(headingFigure);

    final int theMinHeight = 200;
    FillLayout outerLayout = new FillLayout()
    {
      protected Dimension calculatePreferredSize(IFigure parent, int width, int height)
      {
        Dimension d = super.calculatePreferredSize(parent, width, height);
        d.union(new Dimension(250, theMinHeight));
        return d;
      }
    };
    outerLayout.setHorizontal(false);
    outer.setLayoutManager(outerLayout);

    RectangleFigure line = new RectangleFigure()
    {
      public Dimension getPreferredSize(int wHint, int hHint)
      {
        Dimension d = super.getPreferredSize(wHint, hHint);
        d.width += 20;
        d.height = 1;
        return d;
      }
    };
    ToolbarLayout lineLayout = new ToolbarLayout(false);
    lineLayout.setVertical(true);
    lineLayout.setStretchMinorAxis(true);
    line.setLayoutManager(lineLayout);
    outer.add(line);

    contentFigure = new Figure();
    contentFigure.setBorder(new MarginBorder(4, 4, 4, 4));
    fillLayout = new FillLayout(4);
    contentFigure.setLayoutManager(fillLayout);

    outer.add(contentFigure);

    return outer;
  }

  protected List getModelChildren()
  {
    XSDSchemaAdapter schemaAdapter = (XSDSchemaAdapter) getModel();
    List list = new ArrayList();

    List templist = new ArrayList();
    templist.add(schemaAdapter.getCategory(CategoryAdapter.DIRECTIVES));
    Holder holder = new Holder(templist);
    list.add(holder);

    templist = new ArrayList();
    templist.add(schemaAdapter.getCategory(CategoryAdapter.ELEMENTS));
    templist.add(schemaAdapter.getCategory(CategoryAdapter.TYPES));
    holder = new Holder(templist);
    list.add(holder);

    templist = new ArrayList();
    templist.add(schemaAdapter.getCategory(CategoryAdapter.ATTRIBUTES));
    templist.add(schemaAdapter.getCategory(CategoryAdapter.GROUPS));
    holder = new Holder(templist);
    list.add(holder);

    return list;
  }

  protected EditPart createChild(Object model)
  {
    CategoryRowEditPart result = new CategoryRowEditPart();
    result.setModel(model);
    result.setParent(this);
    return result;
  }

  protected void refreshVisuals()
  {
    super.refreshVisuals();
    String targetNamespaceValue = ((XSDSchema) ((XSDSchemaAdapter) getModel()).getTarget()).getTargetNamespace();
    if (targetNamespaceValue == null || targetNamespaceValue.length() == 0)
    {
      targetNamespaceValue = XSDEditorPlugin.getXSDString("_UI_GRAPH_XSDSCHEMA_NO_NAMESPACE");
    }
    headingFigure.getLabel().setText(XSDEditorPlugin.getXSDString("_UI_GRAPH_XSDSCHEMA") + " : " + targetNamespaceValue);
  }

  protected void createEditPolicies()
  {
    // TODO Auto-generated method stub

  }

  protected class Holder
  {
    List list;

    public Holder(List list)
    {
      this.list = list;
    }

    public List getList()
    {
      return list;
    }
  }

  protected class CategoryRowEditPart extends BaseEditPart
  {
    protected XSDSchema schema;
    protected Figure contentPane;

    protected IFigure createFigure()
    {
      Figure containerFigure = new Figure();
      containerFigure.setBorder(new MarginBorder(4, 4, 4, 4));
      // containerFigure.setBorder(new LineBorder(1));
      // containerFigure.setBackgroundColor(ColorConstants.green);

      FillLayout fillLayout = new FillLayout(4);
      fillLayout.setHorizontal(true);
      containerFigure.setLayoutManager(fillLayout);

      return containerFigure;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#getContentPane()
     */
    public IFigure getContentPane()
    {
      return super.getContentPane();
    }

    protected List getModelChildren()
    {
      Holder holder = (Holder) getModel();
      return holder.getList();
    }

    protected void createEditPolicies()
    {
      // TODO Auto-generated method stub

    }

  }
}
