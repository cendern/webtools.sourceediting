package org.eclipse.wst.xsd.ui.internal.common.properties.sections.appinfo;

import org.eclipse.gef.commands.Command;
import org.eclipse.wst.xml.core.internal.contentmodel.CMAttributeDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.CMDataType;
import org.eclipse.wst.xml.core.internal.contentmodel.CMElementDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNode;
import org.eclipse.wst.xml.core.internal.contentmodel.modelquery.ModelQuery;
import org.eclipse.wst.xml.core.internal.modelquery.ModelQueryUtil;
import org.eclipse.wst.xsd.ui.internal.common.commands.UpdateAttributeValueCommand;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

class DOMExtensionItem
{     
  Node node;
  Element parent;
  CMNode cmNode;
  
  DOMExtensionItem(Node node)
  {
    this.node = node;
  }
  
  DOMExtensionItem(Element parent, CMElementDeclaration ed)
  {
    this.parent = parent;
    this.cmNode = ed;
  }   
  
  DOMExtensionItem(Element parent, CMAttributeDeclaration ad)
  {
    this.parent = parent;
    this.cmNode = ad;
  }   
  
  public String getName()
  {
    if (node instanceof Attr)
    {
      Attr attr = (Attr)node;
      return attr.getName();
    }  
    else if (cmNode instanceof CMAttributeDeclaration)
    {
      CMAttributeDeclaration ad = (CMAttributeDeclaration)cmNode;
      return ad.getNodeName() + "*";
    }  
    else if (cmNode instanceof CMDataType)
    {
      return "text()";
    }      
    return "";
  }

  public String getValue()
  {
    if (node instanceof Attr)
    {
      Attr attr = (Attr)node;
      return attr.getValue();
    }  
    return "";
  }
  
  public String[] getPossibleValues()
  {
    String[] result = {};
    if (node instanceof Attr)
    {  
      Attr attr = (Attr)node;
      ModelQuery modelQuery = ModelQueryUtil.getModelQuery(attr.getOwnerDocument());
      if (modelQuery != null)
      {
        CMAttributeDeclaration ad = modelQuery.getCMAttributeDeclaration(attr);
        if (ad != null)
        {  
          result = modelQuery.getPossibleDataTypeValues(attr.getOwnerElement(), ad);
        }  
      }  
    }
    else if (parent != null)
    {  
      if (cmNode == null || cmNode instanceof CMDataType)
      {
        // TODO
        //        
      }  
      else if (cmNode instanceof CMAttributeDeclaration)
      {
        CMAttributeDeclaration ad = (CMAttributeDeclaration)cmNode;
        ModelQuery modelQuery = ModelQueryUtil.getModelQuery(parent.getOwnerDocument());
        if (modelQuery != null)
        {
           result = modelQuery.getPossibleDataTypeValues(parent, ad);
        }       
      }    
    }          
    return result;
  }
  
  public Command getUpdateValueCommand(String newValue)
  {
    if (node instanceof Attr)
    {  
      Attr attr = (Attr)node;
      return new UpdateAttributeValueCommand(attr.getOwnerElement(), attr.getNodeName(), newValue);
    }
    else if (parent != null)
    {  
      if (cmNode == null || cmNode instanceof CMDataType)
      {
        // in this case we need to update the parent's text
        //        
      }  
      else if (cmNode instanceof CMAttributeDeclaration)
      {
        // TODO (cs) add namespace prefix to attribute name if req'd
        //
        CMAttributeDeclaration ad = (CMAttributeDeclaration)cmNode;
        return new UpdateAttributeValueCommand(parent, ad.getAttrName(), newValue);       
      }    
    }      
    return null;      
  }
}
