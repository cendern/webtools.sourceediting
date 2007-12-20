/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xml.core.internal.formatter;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Position;
import org.eclipse.text.edits.DeleteEdit;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.IndexedRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionList;
import org.eclipse.wst.xml.core.internal.Logger;
import org.eclipse.wst.xml.core.internal.contentmodel.CMAttributeDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.CMDataType;
import org.eclipse.wst.xml.core.internal.contentmodel.CMElementDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNamedNodeMap;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMText;
import org.eclipse.wst.xml.core.internal.regions.DOMRegionContext;
import org.eclipse.wst.xml.core.internal.ssemodelquery.ModelQueryAdapter;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class DefaultXMLPartitionFormatter {
	/**
	 * Just a small container class that holds a DOMNode & documentRegion that
	 * should represent each other.
	 */
	protected class DOMRegion {
		public IDOMNode domNode;
		public IStructuredDocumentRegion documentRegion;
	}

	static private final String PRESERVE = "preserve";//$NON-NLS-1$
	static private final String PRESERVE_QUOTED = "\"preserve\"";//$NON-NLS-1$
	static private final String XML_SPACE = "xml:space";//$NON-NLS-1$
	static private final String XSL_NAMESPACE = "http://www.w3.org/1999/XSL/Transform"; //$NON-NLS-1$
	static private final String XSL_ATTRIBUTE = "attribute"; //$NON-NLS-1$
	static private final String XSL_TEXT = "text"; //$NON-NLS-1$
	static private final String SPACE = " "; //$NON-NLS-1$
	static private final String STRING = "string"; //$NON-NLS-1$

	private XMLFormattingPreferences fPreferences = null;
	private IProgressMonitor fProgressMonitor;

	private int collapseSpaces(TextEdit textEdit, int spaceStartOffset, int availableLineWidth, String whitespaceRun) {
		// prefer to use use existing whitespace
		int existingWhitespaceOffset = whitespaceRun.indexOf(' ');
		if (existingWhitespaceOffset > -1) {
			// delete whitespaces before and after existing whitespace
			if (existingWhitespaceOffset > 0) {
				DeleteEdit deleteEdit = new DeleteEdit(spaceStartOffset, existingWhitespaceOffset);
				textEdit.addChild(deleteEdit);
			}
			if (existingWhitespaceOffset < whitespaceRun.length() - 1) {
				int nextOffset = existingWhitespaceOffset + 1;
				DeleteEdit deleteEdit = new DeleteEdit(spaceStartOffset + nextOffset, whitespaceRun.length() - nextOffset);
				textEdit.addChild(deleteEdit);
			}
		}
		else {
			// delete all whitespace and insert new one
			// collapse whitespace by deleting whitespace
			DeleteEdit deleteEdit = new DeleteEdit(spaceStartOffset, whitespaceRun.length());
			textEdit.addChild(deleteEdit);
			// then insert one space
			InsertEdit insertEdit = new InsertEdit(spaceStartOffset, SPACE);
			textEdit.addChild(insertEdit);
		}
		// remember to account for space added
		--availableLineWidth;
		return availableLineWidth;
	}

	private void deleteTrailingSpaces(TextEdit textEdit, ITextRegion currentTextRegion, IStructuredDocumentRegion currentDocumentRegion) {
		int textEnd = currentTextRegion.getTextEnd();
		int textEndOffset = currentDocumentRegion.getStartOffset() + textEnd;
		int difference = currentTextRegion.getEnd() - textEnd;
		DeleteEdit deleteEdit = new DeleteEdit(textEndOffset, difference);
		textEdit.addChild(deleteEdit);
	}

	public TextEdit format(IDocument document, int start, int length) {
		return format(document, start, length, new XMLFormattingPreferences());
	}

	public TextEdit format(IDocument document, int start, int length, XMLFormattingPreferences preferences) {
		TextEdit edit = null;
		if (document instanceof IStructuredDocument) {
			IStructuredModel model = StructuredModelManager.getModelManager().getModelForEdit((IStructuredDocument) document);
			if (model != null) {
				try {
					edit = format(model, start, length, preferences);
				}
				finally {
					model.releaseFromEdit();
				}
			}
		}
		return edit;
	}

	public TextEdit format(IStructuredModel model, int start, int length) {
		return format(model, start, length, new XMLFormattingPreferences());
	}

	public TextEdit format(IStructuredModel model, int start, int length, XMLFormattingPreferences preferences) {
		setFormattingPreferences(preferences);

		TextEdit edit = new MultiTextEdit();
		IStructuredDocument document = model.getStructuredDocument();
		// get initial document region
		IStructuredDocumentRegion currentRegion = document.getRegionAtCharacterOffset(start);
		if (currentRegion != null) {
			int startOffset = currentRegion.getStartOffset();

			// get initial dom node
			IndexedRegion currentIndexedRegion = model.getIndexedRegion(startOffset);
			if (currentIndexedRegion instanceof IDOMNode) {
				// set up domRegion which will contain current region to be
				// formatted
				IDOMNode currentDOMNode = (IDOMNode) currentIndexedRegion;
				DOMRegion domRegion = new DOMRegion();
				domRegion.documentRegion = currentRegion;
				domRegion.domNode = currentDOMNode;

				// determine parent constraint information by trying to
				// determine child constraint for parent node
				DOMRegion parentDOMRegion = new DOMRegion();
				parentDOMRegion.domNode = (IDOMNode) currentDOMNode.getParentNode();
				parentDOMRegion.documentRegion = parentDOMRegion.domNode.getFirstStructuredDocumentRegion();
				XMLFormattingConstraints parentConstraints = new XMLFormattingConstraints();
				updateFormattingConstraints(null, null, parentConstraints, parentDOMRegion);

				// TODO: initialize indentLevel
				// initialize available line width
				int lineWidth = getFormattingPreferences().getMaxLineWidth();
				try {
					IRegion lineInfo = document.getLineInformationOfOffset(startOffset);
					lineWidth = lineWidth - (startOffset - lineInfo.getOffset());
				}
				catch (BadLocationException e) {
					Logger.log(Logger.WARNING_DEBUG, e.getMessage(), e);
				}
				parentConstraints.setAvailableLineWidth(lineWidth);

				// format all siblings (and their children) as long they
				// overlap with start/length
				Position formatRange = new Position(start, length);
				formatSiblings(edit, domRegion, parentConstraints, formatRange);
			}
		}
		return edit;
	}

	/**
	 * Formats the given xml content region
	 * 
	 * @param textEdit
	 * @param formatRange
	 * @param parentConstraints
	 * @param currentDOMRegion
	 * @param previousRegion
	 */
	private void formatContent(TextEdit textEdit, Position formatRange, XMLFormattingConstraints parentConstraints, DOMRegion currentDOMRegion, IStructuredDocumentRegion previousRegion) {
		IStructuredDocumentRegion currentRegion = currentDOMRegion.documentRegion;
		String fullText = currentRegion.getFullText();

		// check if in preserve space mode, if so, don't touch anything but
		// make sure to update available line width
		String whitespaceMode = parentConstraints.getWhitespaceStrategy();
		if (whitespaceMode == XMLFormattingConstraints.PRESERVE) {
			int availableLineWidth = parentConstraints.getAvailableLineWidth();
			availableLineWidth = updateLineWidthWithLastLine(fullText, availableLineWidth);

			// update available line width in constraints
			parentConstraints.setAvailableLineWidth(availableLineWidth);
			return;
		}

		// if content is just whitespace and there's something after it
		// just skip over this region because region will take care of it
		boolean isAllWhitespace = ((IDOMText) currentDOMRegion.domNode).isElementContentWhitespace();
		IStructuredDocumentRegion nextDocumentRegion = null;
		if (isAllWhitespace) {
			nextDocumentRegion = currentRegion.getNext();
			if (nextDocumentRegion != null)
				return;
		}

		// special handling if text follows an entity or cdata region
		if (whitespaceMode != XMLFormattingConstraints.COLLAPSE && previousRegion != null) {
			String previouRegionType = previousRegion.getType();
			if (previouRegionType == DOMRegionContext.XML_ENTITY_REFERENCE || previouRegionType == DOMRegionContext.XML_CDATA_TEXT)
				whitespaceMode = XMLFormattingConstraints.COLLAPSE;
		}
		// also, special handling if text is before an entity or cdata region
		if (whitespaceMode != XMLFormattingConstraints.COLLAPSE) {
			// get next document region if dont already have it
			if (nextDocumentRegion == null)
				nextDocumentRegion = currentRegion.getNext();
			if (nextDocumentRegion != null) {
				String nextRegionType = nextDocumentRegion.getType();
				if (nextRegionType == DOMRegionContext.XML_ENTITY_REFERENCE || nextRegionType == DOMRegionContext.XML_CDATA_TEXT)
					whitespaceMode = XMLFormattingConstraints.COLLAPSE;
			}
		}
		formatTextInContent(textEdit, parentConstraints, currentRegion, fullText, whitespaceMode);
	}

	private void formatEmptyStartTagWithNoAttr(TextEdit textEdit, XMLFormattingConstraints constraints, IStructuredDocumentRegion currentDocumentRegion, IStructuredDocumentRegion previousDocumentRegion, int availableLineWidth, String indentStrategy, String whitespaceStrategy, ITextRegion currentTextRegion) {
		// get preference if there should be a space or not between tag
		// name and empty tag close
		// <tagName />
		boolean oneSpaceInTagName = getFormattingPreferences().getSpaceBeforeEmptyCloseTag();

		// calculate available line width
		int tagNameLineWidth = currentTextRegion.getTextLength() + 3;
		if (oneSpaceInTagName) {
			// add one more to account for space before empty tag close
			++tagNameLineWidth;
		}
		availableLineWidth -= tagNameLineWidth;

		if (indentStrategy == XMLFormattingConstraints.INLINE) {
			// if was inlining, need to check if out of available line
			// width
			if (availableLineWidth < 0) {
				// need to indent if possible
				int lineWidth = indentIfPossible(textEdit, constraints, currentDocumentRegion, previousDocumentRegion, whitespaceStrategy, indentStrategy, true);
				// update available line width
				if (lineWidth > 0)
					availableLineWidth = lineWidth - tagNameLineWidth;
				else
					availableLineWidth -= tagNameLineWidth;
			}
			else {
				// no need to indent
				// just make sure to delete previous whitespace if
				// needed
				if ((previousDocumentRegion.getType() == DOMRegionContext.XML_CONTENT) && (previousDocumentRegion.getFullText().trim().length() == 0)) {
					availableLineWidth = collapseSpaces(textEdit, previousDocumentRegion.getStartOffset(), availableLineWidth, previousDocumentRegion.getFullText());
				}
			}
		}

		// delete any trail spaces after tag name
		int textLength = currentTextRegion.getTextLength();
		int regionLength = currentTextRegion.getLength();

		boolean thereAreSpaces = textLength < regionLength;
		if (!oneSpaceInTagName && thereAreSpaces) {
			deleteTrailingSpaces(textEdit, currentTextRegion, currentDocumentRegion);
		}
		else {
			insertSpaceAndCollapse(textEdit, currentDocumentRegion, availableLineWidth, currentTextRegion);
		}
		constraints.setAvailableLineWidth(availableLineWidth);
	}

	/**
	 * Formats an end tag
	 * 
	 * @param textEdit
	 * @param currentRegion
	 * @param textRegions
	 */
	private void formatEndTag(TextEdit textEdit, Position formatRange, XMLFormattingConstraints constraints, DOMRegion currentDOMRegion, IStructuredDocumentRegion previousDocumentRegion) {
		IStructuredDocumentRegion currentDocumentRegion = currentDOMRegion.documentRegion;

		String whitespaceStrategy = constraints.getWhitespaceStrategy();
		String indentStrategy = constraints.getIndentStrategy();

		// do not format space before start tag if preserving spaces
		if (whitespaceStrategy != XMLFormattingConstraints.PRESERVE) {
			// format like indent strategy says
			if (indentStrategy == XMLFormattingConstraints.INDENT || indentStrategy == XMLFormattingConstraints.NEW_LINE) {
				int availableLineWidth = indentIfPossible(textEdit, constraints, currentDocumentRegion, previousDocumentRegion, whitespaceStrategy, indentStrategy, false);
				constraints.setAvailableLineWidth(availableLineWidth);
			}
		}
		// format the end tag itself
		formatWithinEndTag(textEdit, constraints, currentDocumentRegion, previousDocumentRegion);
	}

	/**
	 * Formats the given region (and all its children) contained in domRegion.
	 * 
	 * @param edit
	 *            edits required to format
	 * @param formatRange
	 *            document range to format (only format content within this
	 *            range)
	 * @param parentConstraints
	 * @param domRegion
	 *            assumes dom node & region are not null
	 * @param previousRegion
	 *            could be null
	 * @return Returns the last region formatted
	 */
	private DOMRegion formatRegion(TextEdit edit, Position formatRange, XMLFormattingConstraints parentConstraints, DOMRegion domRegion, IStructuredDocumentRegion previousRegion) {
		IStructuredDocumentRegion currentRegion = domRegion.documentRegion;
		String regionType = currentRegion.getType();
		if (regionType == DOMRegionContext.XML_TAG_NAME) {
			ITextRegion textRegion = currentRegion.getFirstRegion();
			String textRegionType = textRegion.getType();
			if (textRegionType == DOMRegionContext.XML_TAG_OPEN) {
				domRegion = formatStartTag(edit, formatRange, parentConstraints, domRegion, previousRegion);
			}
			else if (textRegionType == DOMRegionContext.XML_END_TAG_OPEN) {
				formatEndTag(edit, formatRange, parentConstraints, domRegion, previousRegion);
			}
		}
		else if (regionType == DOMRegionContext.XML_CONTENT) {
			formatContent(edit, formatRange, parentConstraints, domRegion, previousRegion);
		}
		else {
			// unknown, so just leave alone for now but make sure to update
			// available line width
			String fullText = currentRegion.getFullText();
			int width = updateLineWidthWithLastLine(fullText, parentConstraints.getAvailableLineWidth());
			parentConstraints.setAvailableLineWidth(width);
		}
		return domRegion;
	}

	/**
	 * Formats the domRegion and all of its children and siblings
	 * 
	 * @param edit
	 * @param domRegion
	 * @param parentConstraints
	 * @param formatRange
	 */
	private void formatSiblings(TextEdit edit, DOMRegion domRegion, XMLFormattingConstraints parentConstraints, Position formatRange) {
		IStructuredDocumentRegion previousRegion = null;
		IStructuredDocumentRegion currentRegion = domRegion.documentRegion;
		IDOMNode currentDOMNode = domRegion.domNode;
		while (currentDOMNode != null && currentRegion != null && formatRange.overlapsWith(currentRegion.getStartOffset(), currentRegion.getLength()) && (fProgressMonitor == null || !fProgressMonitor.isCanceled())) {
			domRegion.documentRegion = currentRegion;
			domRegion.domNode = currentDOMNode;

			// need to make sure current document region and current
			// dom node match up
			if (currentRegion == currentDOMNode.getFirstStructuredDocumentRegion()) {
				// format this document region/node, formatRegion will
				// return the last node/region formatted
				domRegion = formatRegion(edit, formatRange, parentConstraints, domRegion, previousRegion);
			}
			else {
				// TODO: need to figure out what to do if they don't
				// match up
			}
			previousRegion = domRegion.documentRegion;
			// get the next sibling information
			if (domRegion.domNode != null)
				currentDOMNode = (IDOMNode) domRegion.domNode.getNextSibling();
			else
				currentDOMNode = null;
			currentRegion = previousRegion.getNext();
		}
	}

	/**
	 * Formats a start tag
	 * 
	 * @param textEdit
	 * @param currentRegion
	 * @param textRegions
	 */
	private DOMRegion formatStartTag(TextEdit textEdit, Position formatRange, XMLFormattingConstraints parentConstraints, DOMRegion currentDOMRegion, IStructuredDocumentRegion previousDocumentRegion) {
		// determine proper indent by referring to parent constraints,
		// previous node, and current node
		IStructuredDocumentRegion currentDocumentRegion = currentDOMRegion.documentRegion;
		IDOMNode currentDOMNode = currentDOMRegion.domNode;

		// create a constraint for this tag
		XMLFormattingConstraints thisConstraints = new XMLFormattingConstraints();
		XMLFormattingConstraints childrenConstraints = new XMLFormattingConstraints();
		updateFormattingConstraints(parentConstraints, thisConstraints, childrenConstraints, currentDOMRegion);

		String whitespaceStrategy = thisConstraints.getWhitespaceStrategy();
		String indentStrategy = thisConstraints.getIndentStrategy();
		int availableLineWidth = thisConstraints.getAvailableLineWidth();

		// format space before start tag
		// do not format space before start tag if preserving spaces
		if (whitespaceStrategy != XMLFormattingConstraints.PRESERVE) {
			// format like indent strategy says
			if (indentStrategy == XMLFormattingConstraints.INDENT || indentStrategy == XMLFormattingConstraints.NEW_LINE) {
				availableLineWidth = indentIfPossible(textEdit, thisConstraints, currentDocumentRegion, previousDocumentRegion, whitespaceStrategy, indentStrategy, true);
				if (availableLineWidth > 0)
					thisConstraints.setAvailableLineWidth(availableLineWidth);
			}
		}
		// format the start tag itself
		boolean tagEnded = formatWithinTag(textEdit, thisConstraints, currentDocumentRegion, previousDocumentRegion);

		// format children
		if (!tagEnded) {
			// update childConstraints with thisConstraint's indentLevel &
			// availableLineWidth
			childrenConstraints.setIndentLevel(thisConstraints.getIndentLevel());
			childrenConstraints.setAvailableLineWidth(thisConstraints.getAvailableLineWidth());

			previousDocumentRegion = currentDocumentRegion;
			IDOMNode childDOMNode = (IDOMNode) currentDOMNode.getFirstChild();
			IStructuredDocumentRegion nextRegion = currentDocumentRegion.getNext();
			boolean passedFormatRange = false;
			// as long as there is one child
			if (childDOMNode != null && nextRegion != null) {
				while (childDOMNode != null && nextRegion != null && !passedFormatRange && (fProgressMonitor == null || !fProgressMonitor.isCanceled())) {
					DOMRegion childDOMRegion = new DOMRegion();
					childDOMRegion.documentRegion = nextRegion;
					childDOMRegion.domNode = childDOMNode;
					if (nextRegion == childDOMNode.getFirstStructuredDocumentRegion()) {
						// format children. pass in child constraints
						childDOMRegion = formatRegion(textEdit, formatRange, childrenConstraints, childDOMRegion, previousDocumentRegion);
					}
					else {
						// TODO: what happens if they dont match up?
					}

					// update childDOMRegion with next dom/region node
					if (childDOMRegion.domNode != null) {
						childDOMNode = (IDOMNode) childDOMRegion.domNode.getNextSibling();
					}
					else {
						childDOMNode = null;
					}
					previousDocumentRegion = childDOMRegion.documentRegion;
					nextRegion = previousDocumentRegion.getNext();
					if (nextRegion != null)
						passedFormatRange = !formatRange.overlapsWith(nextRegion.getStartOffset(), nextRegion.getLength());
				}
			}
			else {
				// there were no children, so keep end tag inlined
				childrenConstraints.setWhitespaceStrategy(XMLFormattingConstraints.COLLAPSE);
				childrenConstraints.setIndentStrategy(XMLFormattingConstraints.INLINE);
			}

			if (!passedFormatRange) {
				// update the dom region with the last formatted region/dom
				// node should be end tag and this tag's DOMNode
				currentDOMRegion.documentRegion = nextRegion;
				currentDOMRegion.domNode = currentDOMNode;

				// end tag's indent level should be same as start tag's
				childrenConstraints.setIndentLevel(thisConstraints.getIndentLevel());
				// format end tag
				if (nextRegion.getFirstRegion().getType() == DOMRegionContext.XML_END_TAG_OPEN)
					formatEndTag(textEdit, formatRange, childrenConstraints, currentDOMRegion, previousDocumentRegion);
				else {
					// missing end tag so return last formatted document
					// region
					currentDOMRegion.documentRegion = previousDocumentRegion;
				}
			}
			else {
				// passed format range before could finish, so update dom
				// region to last known formatted region
				currentDOMRegion.documentRegion = nextRegion;
				currentDOMRegion.domNode = childDOMNode;
			}

			// update parent constraint since this is what is passed back
			parentConstraints.setAvailableLineWidth(childrenConstraints.getAvailableLineWidth());
		}
		else {
			// update available line width
			parentConstraints.setAvailableLineWidth(thisConstraints.getAvailableLineWidth());
		}
		return currentDOMRegion;
	}

	private void formatStartTagWithNoAttr(TextEdit textEdit, XMLFormattingConstraints constraints, IStructuredDocumentRegion currentDocumentRegion, IStructuredDocumentRegion previousDocumentRegion, int availableLineWidth, String indentStrategy, String whitespaceStrategy, ITextRegion currentTextRegion) {
		// calculate available line width
		int tagNameLineWidth = currentTextRegion.getTextLength() + 2;
		availableLineWidth -= tagNameLineWidth;

		if (indentStrategy == XMLFormattingConstraints.INLINE) {
			// if was inlining, need to check if out of available line
			// width
			if (availableLineWidth < 0) {
				// need to indent if possible
				int lineWidth = indentIfPossible(textEdit, constraints, currentDocumentRegion, previousDocumentRegion, whitespaceStrategy, indentStrategy, true);
				// update available line width
				if (lineWidth > 0)
					availableLineWidth = lineWidth - tagNameLineWidth;
				else
					availableLineWidth -= tagNameLineWidth;
			}
			else {
				// no need to indent
				// just make sure to delete previous whitespace if
				// needed
				if (previousDocumentRegion != null) {
					if (previousDocumentRegion.getType() == DOMRegionContext.XML_CONTENT) {
						String previousDocumentRegionText = previousDocumentRegion.getFullText();
						if (previousDocumentRegionText.trim().length() == 0) {
							availableLineWidth = collapseSpaces(textEdit, previousDocumentRegion.getStartOffset(), availableLineWidth, previousDocumentRegionText);
						}
					}
				}
			}
		}

		// delete any trail spaces after tag name
		if (currentTextRegion.getTextLength() < currentTextRegion.getLength()) {
			deleteTrailingSpaces(textEdit, currentTextRegion, currentDocumentRegion);
		}
		constraints.setAvailableLineWidth(availableLineWidth);
	}

	/**
	 * Format the text in xml content
	 * 
	 * @param textEdit
	 * @param parentConstraints
	 * @param currentRegion
	 * @param fullText
	 * @param whitespaceMode
	 */
	private void formatTextInContent(TextEdit textEdit, XMLFormattingConstraints parentConstraints, IStructuredDocumentRegion currentRegion, String fullText, String whitespaceMode) {
		int availableLineWidth = parentConstraints.getAvailableLineWidth();

		// determine indentation
		boolean forceInitialIndent = false;
		int indentLevel = parentConstraints.getIndentLevel() + 1;
		String indentMode = parentConstraints.getIndentStrategy();
		if (indentMode == XMLFormattingConstraints.INDENT) {
			forceInitialIndent = true;
		}
		if (indentMode == XMLFormattingConstraints.NEW_LINE) {
			indentLevel = parentConstraints.getIndentLevel();
			forceInitialIndent = true;
		}

		int fullTextOffset = 0;
		char[] fullTextArray = fullText.toCharArray();
		while (fullTextOffset < fullTextArray.length) {
			// gather all whitespaces
			String whitespaceRun = getCharacterRun(fullTextArray, fullTextOffset, true);
			if (whitespaceRun.length() > 0) {
				// offset where whitespace starts
				int whitespaceStart = fullTextOffset;
				// update current offset in fullText
				fullTextOffset += whitespaceRun.length();

				// gather following word
				String characterRun = getCharacterRun(fullTextArray, fullTextOffset, false);
				int characterRunLength = characterRun.length();
				if (characterRunLength > 0) {
					// indent if word is too long or forcing initial
					// indent
					availableLineWidth -= characterRunLength;
					// offset where indent/collapse will happen
					int startOffset = currentRegion.getStartOffset() + whitespaceStart;
					if (forceInitialIndent || (availableLineWidth <= 0)) {
						// indent if not already indented
						availableLineWidth = indentIfNotAlreadyIndented(textEdit, currentRegion, indentLevel, startOffset, whitespaceRun);
						// remember to subtract word length
						availableLineWidth -= characterRunLength;
						forceInitialIndent = false; // initial indent done
					}
					else {
						// just collapse spaces
						availableLineWidth = collapseSpaces(textEdit, startOffset, availableLineWidth, whitespaceRun);
					}

					fullTextOffset += characterRunLength;
				}
				else {
					// handle trailing whitespace
					int whitespaceOffset = currentRegion.getStartOffset() + whitespaceStart;
					if (whitespaceMode == XMLFormattingConstraints.IGNORE) {
						// if ignore, trim
						DeleteEdit deleteTrailing = new DeleteEdit(whitespaceOffset, whitespaceRun.length());
						textEdit.addChild(deleteTrailing);
					}
					else {
						// if collapse, leave a space. but what if end up
						// wanting to add indent? then need to delete space
						// added and add indent instead
						availableLineWidth = collapseSpaces(textEdit, whitespaceOffset, availableLineWidth, whitespaceRun);
					}
				}
			}
			else {
				// gather word
				String characterRun = getCharacterRun(fullTextArray, fullTextOffset, false);
				int characterRunLength = characterRun.length();
				if (characterRunLength > 0) {
					// indent if word is too long or forcing initial
					// indent
					availableLineWidth = availableLineWidth - characterRunLength;
					if (whitespaceMode == XMLFormattingConstraints.IGNORE && (forceInitialIndent || (availableLineWidth <= 0))) {
						// indent if not already indented
						availableLineWidth = indentIfNotAlreadyIndented(textEdit, currentRegion, indentLevel, currentRegion.getStartOffset(), whitespaceRun);
						// remember to subtract word length
						availableLineWidth -= characterRunLength;
						forceInitialIndent = false; // initial indent done
					}
					else {
						// just collapse spaces
						availableLineWidth -= characterRunLength;
					}

					fullTextOffset += characterRunLength;
				}
			}
		}
		// update available line width
		parentConstraints.setAvailableLineWidth(availableLineWidth);
	}

	private void formatWithinEndTag(TextEdit textEdit, XMLFormattingConstraints constraints, IStructuredDocumentRegion currentDocumentRegion, IStructuredDocumentRegion previousDocumentRegion) {
		String indentStrategy = constraints.getIndentStrategy();
		String whitespaceStrategy = constraints.getWhitespaceStrategy();
		int availableLineWidth = constraints.getAvailableLineWidth();
		ITextRegionList textRegions = currentDocumentRegion.getRegions();
		int currentTextRegionIndex = 1;

		ITextRegion currentTextRegion = textRegions.get(currentTextRegionIndex);
		String currentType = currentTextRegion.getType();
		// tag name should always be the first text region
		if (currentType == DOMRegionContext.XML_TAG_NAME) {
			ITextRegion nextTextRegion = textRegions.get(currentTextRegionIndex + 1);
			String nextType = nextTextRegion.getType();
			if (nextType == DOMRegionContext.XML_TAG_CLOSE) {
				// calculate available line width
				int tagNameLineWidth = currentTextRegion.getTextLength() + 3;
				availableLineWidth -= tagNameLineWidth;

				if (indentStrategy == XMLFormattingConstraints.INLINE) {
					// if was inlining, need to check if out of available line
					// width
					if (availableLineWidth < 0) {
						// need to deindent if possible
						int lineWidth = indentIfPossible(textEdit, constraints, currentDocumentRegion, previousDocumentRegion, whitespaceStrategy, indentStrategy, false);
						// update available line width
						if (lineWidth > 0)
							availableLineWidth = lineWidth - tagNameLineWidth;
					}
					else {
						// no need to indent
						// just make sure to delete previous whitespace if
						// needed
						if (previousDocumentRegion != null) {
							if (previousDocumentRegion.getType() == DOMRegionContext.XML_CONTENT) {
								String previousDocumentRegionText = previousDocumentRegion.getFullText();
								if (previousDocumentRegionText.trim().length() == 0) {
									availableLineWidth = collapseSpaces(textEdit, previousDocumentRegion.getStartOffset(), availableLineWidth, previousDocumentRegionText);
								}
							}
						}
					}
				}
				// delete any trail spaces after tag name
				if (currentTextRegion.getTextLength() < currentTextRegion.getLength()) {
					deleteTrailingSpaces(textEdit, currentTextRegion, currentDocumentRegion);
				}
			}
		}
		else {
			// end tag has unexpected stuff, so just leave it alone
		}
		constraints.setAvailableLineWidth(availableLineWidth);
	}

	/**
	 * Formats the contents within a tag like tag name and attributes
	 * 
	 * @param textEdit
	 * @param currentDocumentRegion
	 * @param textRegions
	 *            contains at least 3 regions
	 * @return true if tag was ended, false otherwise
	 */
	private boolean formatWithinTag(TextEdit textEdit, XMLFormattingConstraints constraints, IStructuredDocumentRegion currentDocumentRegion, IStructuredDocumentRegion previousDocumentRegion) {
		int availableLineWidth = constraints.getAvailableLineWidth();
		String indentStrategy = constraints.getIndentStrategy();
		String whitespaceStrategy = constraints.getWhitespaceStrategy();
		int indentLevel = constraints.getIndentLevel();
		ITextRegionList textRegions = currentDocumentRegion.getRegions();
		int currentTextRegionIndex = 1;

		ITextRegion currentTextRegion = textRegions.get(currentTextRegionIndex);
		String currentType = currentTextRegion.getType();
		// tag name should always be the first text region
		if (currentType == DOMRegionContext.XML_TAG_NAME) {
			ITextRegion nextTextRegion = textRegions.get(currentTextRegionIndex + 1);
			String nextType = nextTextRegion.getType();
			if (nextType == DOMRegionContext.XML_TAG_CLOSE) {
				// already at tag close
				formatStartTagWithNoAttr(textEdit, constraints, currentDocumentRegion, previousDocumentRegion, availableLineWidth, indentStrategy, whitespaceStrategy, currentTextRegion);
				return false;
			}
			else if (nextType == DOMRegionContext.XML_EMPTY_TAG_CLOSE) {
				// already at empty tag close
				formatEmptyStartTagWithNoAttr(textEdit, constraints, currentDocumentRegion, previousDocumentRegion, availableLineWidth, indentStrategy, whitespaceStrategy, currentTextRegion);
				return true;
			}
			else {
				availableLineWidth -= (currentTextRegion.getTextLength() + 2);
				boolean alignFinalBracket = getFormattingPreferences().getAlignFinalBracket();
				boolean oneSpaceInTagName = getFormattingPreferences().getSpaceBeforeEmptyCloseTag();
				boolean indentMultipleAttribute = getFormattingPreferences().getIndentMultipleAttributes();
				// indicates if tag spanned more than one line
				boolean spanMoreThan1Line = false;
				// indicates if all attributes should be indented
				boolean indentAllAttributes = false;
				if (indentMultipleAttribute) {
					int attributesCount = 0;
					int i = 2;
					while (i < textRegions.size() && attributesCount < 2) {
						if (textRegions.get(i).getType() == DOMRegionContext.XML_TAG_ATTRIBUTE_NAME)
							++attributesCount;
					}
					indentAllAttributes = (attributesCount > 1);
				}

				while ((currentTextRegionIndex + 1) < textRegions.size()) {
					nextTextRegion = textRegions.get(currentTextRegionIndex + 1);
					nextType = nextTextRegion.getType();
					if (nextType == DOMRegionContext.XML_TAG_ATTRIBUTE_NAME) {
						boolean indentAttribute = indentAllAttributes;
						if (!indentAttribute)
							indentAttribute = shouldIndentBeforeAttribute(constraints, textRegions, availableLineWidth, currentTextRegionIndex, currentTextRegion, nextTextRegion);
						if (indentAttribute) {
							availableLineWidth = indentIfNotAlreadyIndented(textEdit, indentLevel + 1, currentDocumentRegion, currentTextRegion);
							spanMoreThan1Line = true;
						}
						else {
							// otherwise, insertSpaceAndCollapse
							insertSpaceAndCollapse(textEdit, currentDocumentRegion, availableLineWidth, currentTextRegion);
							// update available line width
							availableLineWidth -= (currentTextRegion.getTextLength() + 1);
						}
					}
					else if (nextType == DOMRegionContext.XML_TAG_CLOSE) {
						// if need to align bracket on next line, indent
						if (alignFinalBracket && spanMoreThan1Line) {
							availableLineWidth = indentIfNotAlreadyIndented(textEdit, indentLevel, currentDocumentRegion, currentTextRegion);
							--availableLineWidth; // for tag close itself
						}
						else {
							// otherwise, just delete space before tag close
							if (currentTextRegion.getTextLength() < currentTextRegion.getLength()) {
								deleteTrailingSpaces(textEdit, currentTextRegion, currentDocumentRegion);
								availableLineWidth -= (currentTextRegion.getTextLength() + 1);
							}
						}
						// update line width
						constraints.setAvailableLineWidth(availableLineWidth);
						return false;
					}
					else if (nextType == DOMRegionContext.XML_EMPTY_TAG_CLOSE) {
						int textLength = currentTextRegion.getTextLength();
						int regionLength = currentTextRegion.getLength();

						boolean thereAreSpaces = textLength < regionLength;
						if (!oneSpaceInTagName && thereAreSpaces) {
							// delete any trail spaces after tag name
							deleteTrailingSpaces(textEdit, currentTextRegion, currentDocumentRegion);
							availableLineWidth -= (currentTextRegion.getTextLength() + 2);
						}
						else {
							insertSpaceAndCollapse(textEdit, currentDocumentRegion, availableLineWidth, currentTextRegion);
							availableLineWidth -= (currentTextRegion.getTextLength() + 3);
						}
						// update line width
						constraints.setAvailableLineWidth(availableLineWidth);
						return true;
					}
					else {
						if (currentType == DOMRegionContext.XML_TAG_ATTRIBUTE_NAME && nextType == DOMRegionContext.XML_TAG_ATTRIBUTE_EQUALS) {
							if (currentTextRegion.getTextLength() < currentTextRegion.getLength()) {
								deleteTrailingSpaces(textEdit, currentTextRegion, currentDocumentRegion);
							}
							// update available width
							availableLineWidth -= currentTextRegion.getTextLength();
						}
						else if (currentType == DOMRegionContext.XML_TAG_ATTRIBUTE_EQUALS && nextType == DOMRegionContext.XML_TAG_ATTRIBUTE_VALUE) {
							if (currentTextRegion.getTextLength() < currentTextRegion.getLength()) {
								deleteTrailingSpaces(textEdit, currentTextRegion, currentDocumentRegion);
							}
							// update available width
							availableLineWidth -= currentTextRegion.getTextLength();
						}
						else {
							// otherwise, insertSpaceAndCollapse
							insertSpaceAndCollapse(textEdit, currentDocumentRegion, availableLineWidth, currentTextRegion);
							// update available line width
							availableLineWidth -= (currentTextRegion.getTextLength() + 1);
						}
					}
					currentTextRegion = nextTextRegion;
					currentType = nextType;
					++currentTextRegionIndex;
				}
			}
		}
		// update line width
		constraints.setAvailableLineWidth(availableLineWidth);
		return false;
	}

	/**
	 * Returns either a String of whitespace or characters depending on
	 * forWhitespace
	 * 
	 * @param fullTextArray
	 *            the text array to look in
	 * @param textOffset
	 *            the start offset to start searching
	 * @param forWhitespace
	 *            true if should return whitespaces, false otherwise
	 * @return a String of either all whitespace or all characters. Never
	 *         returns null
	 */
	private String getCharacterRun(char[] fullTextArray, int textOffset, boolean forWhitespace) {
		StringBuffer characterRun = new StringBuffer();
		boolean nonCharacterFound = false;
		while (textOffset < fullTextArray.length && !nonCharacterFound) {
			char c = fullTextArray[textOffset];
			boolean isWhitespace = Character.isWhitespace(c);
			if ((forWhitespace && isWhitespace) || (!forWhitespace && !isWhitespace))
				characterRun.append(c);
			else
				nonCharacterFound = true;
			++textOffset;
		}
		return characterRun.toString();
	}

	private String getIndentString(int indentLevel) {
		StringBuffer indentString = new StringBuffer();
		String indent = getFormattingPreferences().getOneIndent();
		for (int i = 0; i < indentLevel; ++i) {
			indentString.append(indent);
		}
		return indentString.toString();
	}

	protected XMLFormattingPreferences getFormattingPreferences() {
		if (fPreferences == null)
			fPreferences = new XMLFormattingPreferences();
		return fPreferences;
	}

	protected void setFormattingPreferences(XMLFormattingPreferences preferences) {
		fPreferences = preferences;
	}

	/**
	 * Indent if whitespaceRun does not already contain an indent
	 * 
	 * @param textEdit
	 * @param indentLevel
	 * @param indentStartOffset
	 * @param maxAvailableLineWidth
	 * @param whitespaceRun
	 * @return new available line width up to where indented
	 */
	private int indentIfNotAlreadyIndented(TextEdit textEdit, IStructuredDocumentRegion currentRegion, int indentLevel, int indentStartOffset, String whitespaceRun) {
		int maxAvailableLineWidth = getFormattingPreferences().getMaxLineWidth();

		int availableLineWidth;
		String indentString = getIndentString(indentLevel);
		String newLineAndIndent = getLineDelimiter(currentRegion) + indentString;

		// if not already correctly indented
		if (!newLineAndIndent.equals(whitespaceRun)) {
			// delete existing whitespace run
			if (whitespaceRun != null) {
				DeleteEdit deleteEdit = new DeleteEdit(indentStartOffset, whitespaceRun.length());
				textEdit.addChild(deleteEdit);
			}
			// then insert correct indent
			InsertEdit insertEdit = new InsertEdit(indentStartOffset, newLineAndIndent);
			textEdit.addChild(insertEdit);
		}
		// update line width
		availableLineWidth = maxAvailableLineWidth - indentString.length();
		return availableLineWidth;
	}

	private int indentIfNotAlreadyIndented(TextEdit textEdit, int indentLevel, IStructuredDocumentRegion currentDocumentRegion, ITextRegion currentTextRegion) {
		// indent if not already indented
		int textLength = currentTextRegion.getTextLength();
		int regionLength = currentTextRegion.getLength();
		int indentStartOffset = currentDocumentRegion.getTextEndOffset(currentTextRegion);
		String fullText = currentDocumentRegion.getFullText(currentTextRegion);
		String whitespaceRun = fullText.substring(textLength, regionLength);

		// update line width
		int availableLineWidth = indentIfNotAlreadyIndented(textEdit, currentDocumentRegion, indentLevel, indentStartOffset, whitespaceRun);
		return availableLineWidth;
	}

	private int indentIfPossible(TextEdit textEdit, XMLFormattingConstraints thisConstraints, IStructuredDocumentRegion currentDocumentRegion, IStructuredDocumentRegion previousDocumentRegion, String whitespaceStrategy, String indentStrategy, boolean addIndent) {
		int availableLineWidth = -1;
		// if there is no previous document region, there is no need to indent
		// because we're at beginning of document
		if (previousDocumentRegion == null)
			return availableLineWidth;

		// only indent if ignoring whitespace or if collapsing and
		// there was a whitespace character before this region
		boolean canIndent = false;
		String previousRegionFullText = null;
		String previousRegionType = null;

		if (whitespaceStrategy == XMLFormattingConstraints.IGNORE) {
			// if ignoring, need to check if previous region was cdata
			previousRegionType = previousDocumentRegion.getType();
			if (previousRegionType == DOMRegionContext.XML_CDATA_TEXT)
				canIndent = false;
			else
				canIndent = true;
		}
		else if (whitespaceStrategy == XMLFormattingConstraints.COLLAPSE) {
			// if collapsing, need to check if previous region ended in a
			// whitespace
			previousRegionType = previousDocumentRegion.getType();
			if (previousRegionType == DOMRegionContext.XML_CONTENT) {
				previousRegionFullText = previousDocumentRegion.getFullText();
				int length = previousRegionFullText.length();
				if (length > 1)
					canIndent = Character.isWhitespace(previousRegionFullText.charAt(length - 1));
			}
		}
		if (canIndent) {
			int indentStartOffset = currentDocumentRegion.getStartOffset();
			String whitespaceRun = null;

			// get previous region type if it was not previously retrieved
			if (previousRegionType == null)
				previousRegionType = previousDocumentRegion.getType();

			// get previous region's text if it was not previously retrieved
			if (previousRegionFullText == null && previousRegionType == DOMRegionContext.XML_CONTENT)
				previousRegionFullText = previousDocumentRegion.getFullText();

			// if previous region was only whitespace, this may
			// already be indented, so need to make sure
			if ((previousRegionFullText != null) && (previousRegionFullText.trim().length() == 0)) {
				indentStartOffset = previousDocumentRegion.getStartOffset();
				whitespaceRun = previousRegionFullText;
			}

			int indentLevel = thisConstraints.getIndentLevel();
			if (addIndent && indentStrategy == XMLFormattingConstraints.INDENT) {
				++indentLevel;
				thisConstraints.setIndentLevel(indentLevel);
			}

			// indent if not already indented
			availableLineWidth = indentIfNotAlreadyIndented(textEdit, currentDocumentRegion, indentLevel, indentStartOffset, whitespaceRun);
		}
		return availableLineWidth;
	}

	/**
	 * Allow exactly one whitespace in currentTextRegion. If there are more,
	 * collapse to one. If there are none, insert one.
	 * 
	 * @param textEdit
	 * @param currentDocumentRegion
	 * @param availableLineWidth
	 * @param currentTextRegion
	 */
	private void insertSpaceAndCollapse(TextEdit textEdit, IStructuredDocumentRegion currentDocumentRegion, int availableLineWidth, ITextRegion currentTextRegion) {
		int textLength = currentTextRegion.getTextLength();
		int regionLength = currentTextRegion.getLength();
		boolean thereAreSpaces = textLength < regionLength;
		int spacesStartOffset = currentDocumentRegion.getStartOffset(currentTextRegion) + textLength;

		if (thereAreSpaces) {
			String fullTagName = currentDocumentRegion.getFullText(currentTextRegion);
			String whitespaceRun = fullTagName.substring(textLength, regionLength);
			collapseSpaces(textEdit, spacesStartOffset, availableLineWidth, whitespaceRun);
		}
		else {
			// insert a space
			InsertEdit insertEdit = new InsertEdit(spacesStartOffset, SPACE);
			textEdit.addChild(insertEdit);
		}
	}

	private boolean shouldIndentBeforeAttribute(XMLFormattingConstraints constraints, ITextRegionList textRegions, int availableLineWidth, int currentTextRegionIndex, ITextRegion currentTextRegion, ITextRegion nextTextRegion) {
		boolean indentAttribute = false;

		// look ahead to see if going to hit max line width
		// something attrName
		int currentWidth = currentTextRegion.getTextLength() + nextTextRegion.getTextLength() + 1;
		if (currentWidth > availableLineWidth)
			indentAttribute = true;
		else {
			if ((currentTextRegionIndex + 2) < textRegions.size()) {
				// still okay, so try next region
				// something attrName=
				ITextRegion textRegion = textRegions.get(currentTextRegionIndex + 2);
				if (textRegion.getType() == DOMRegionContext.XML_TAG_ATTRIBUTE_EQUALS) {
					++currentWidth;
					if (currentWidth > availableLineWidth)
						indentAttribute = true;
					else {
						if ((currentTextRegionIndex + 3) < textRegions.size()) {
							// still okay, so try next region
							// something attrName=attrValue
							textRegion = textRegions.get(currentTextRegionIndex + 3);
							if (textRegion.getType() == DOMRegionContext.XML_TAG_ATTRIBUTE_VALUE) {
								currentWidth = +textRegion.getTextLength();
								if (currentWidth > availableLineWidth)
									indentAttribute = true;
							}
						}
					}
				}
			}
		}
		return indentAttribute;
	}

	/**
	 * Given the provided information (parentConstraints & currentDOMRegion),
	 * update the formatting constraints (for this & child)
	 * 
	 * @param parentConstraints
	 *            can be null
	 * @param thisConstraints
	 *            can be null
	 * @param childConstraints
	 *            can be null
	 * @param currentDOMRegion
	 *            cannot be null
	 */
	protected void updateFormattingConstraints(XMLFormattingConstraints parentConstraints, XMLFormattingConstraints thisConstraints, XMLFormattingConstraints childConstraints, DOMRegion currentDOMRegion) {
		IStructuredDocumentRegion currentRegion = currentDOMRegion.documentRegion;
		IDOMNode currentNode = currentDOMRegion.domNode;

		// default to whatever parent's constraint said to do
		if (parentConstraints != null) {
			if (thisConstraints != null) {
				thisConstraints.copyConstraints(parentConstraints);
			}
			if (childConstraints != null) {
				childConstraints.copyConstraints(parentConstraints);
				// if whitespace strategy was only a hint, null it out so
				// defaults are taken instead
				if (parentConstraints.isWhitespaceStrategyAHint())
					childConstraints.setWhitespaceStrategy(null);
			}
		}

		// set up constraints for direct children of document root
		Node parentNode = currentNode.getParentNode();
		if (parentNode != null && parentNode.getNodeType() == Node.DOCUMENT_NODE) {
			if (thisConstraints != null) {
				thisConstraints.setWhitespaceStrategy(XMLFormattingConstraints.IGNORE);
				thisConstraints.setIndentStrategy(XMLFormattingConstraints.NEW_LINE);
				thisConstraints.setIndentLevel(0);
			}
			if (childConstraints != null) {
				childConstraints.setWhitespaceStrategy(null);
				childConstraints.setIndentStrategy(null);
				childConstraints.setIndentLevel(0);
			}
		}

		// other conditions to check when setting up child constraints
		if (childConstraints != null) {
			XMLFormattingPreferences preferences = getFormattingPreferences();

			// if we're at document root, child tags should always just start
			// on a new line and have an indent level of 0
			if (currentNode.getNodeType() == Node.DOCUMENT_NODE) {
				childConstraints.setWhitespaceStrategy(XMLFormattingConstraints.IGNORE);
				childConstraints.setIndentStrategy(XMLFormattingConstraints.NEW_LINE);
				childConstraints.setIndentLevel(0);
			}
			else {
				// BUG108074 & BUG84688 - preserve whitespace in xsl:text &
				// xsl:attribute
				String nodeNamespaceURI = currentNode.getNamespaceURI();
				if (XSL_NAMESPACE.equals(nodeNamespaceURI)) {
					String nodeName = ((Element) currentNode).getLocalName();
					if (XSL_ATTRIBUTE.equals(nodeName) || XSL_TEXT.equals(nodeName)) {
						childConstraints.setWhitespaceStrategy(XMLFormattingConstraints.PRESERVE);
					}
				}
				else {
					// search within current tag for xml:space attribute
					ITextRegionList textRegions = currentRegion.getRegions();
					int i = 0;
					boolean xmlSpaceFound = false;
					boolean preserveFound = false;
					while (i < textRegions.size() && !xmlSpaceFound) {
						ITextRegion textRegion = textRegions.get(i);
						if (textRegion.getType() == DOMRegionContext.XML_TAG_ATTRIBUTE_NAME) {
							String regionText = currentRegion.getText(textRegion);
							if (XML_SPACE.equals(regionText)) {
								if ((i + 1) < textRegions.size()) {
									++i;
									textRegion = textRegions.get(i);
									if (textRegion.getType() == DOMRegionContext.XML_TAG_ATTRIBUTE_EQUALS && ((i + 1) < textRegions.size())) {
										++i;
										textRegion = textRegions.get(i);
										regionText = currentRegion.getText(textRegion);
										if (PRESERVE.equals(regionText) || PRESERVE_QUOTED.equals(regionText)) {
											preserveFound = true;
										}
									}
								}
								xmlSpaceFound = true;
							}
						}
						++i;
					}
					if (xmlSpaceFound) {
						if (preserveFound) {
							// preserve was found so set the strategy
							childConstraints.setWhitespaceStrategy(XMLFormattingConstraints.PRESERVE);
						}
						else {
							// xml:space was found but it was not collapse, so
							// just null
							childConstraints.setWhitespaceStrategy(null);
						}
					}
					else {
						// how to hande nodes that have nonwhitespace text
						// content
						NodeList nodeList = currentNode.getChildNodes();
						int length = nodeList.getLength();
						int index = 0;
						boolean textNodeFound = false;
						while (index < length && !textNodeFound) {
							Node childNode = nodeList.item(index);
							if (childNode.getNodeType() == Node.TEXT_NODE) {
								textNodeFound = !((IDOMText) childNode).isElementContentWhitespace();
							}
							++index;
						}
						if (textNodeFound) {
							if (length > 1) {
								// more in here than just text, so consider
								// this mixed content
								childConstraints.setWhitespaceStrategy(preferences.getMixedWhitespaceStrategy());
								childConstraints.setIndentStrategy(preferences.getMixedIndentStrategy());
							}
							else {
								// there's only text
								childConstraints.setWhitespaceStrategy(preferences.getTextWhitespaceStrategy());
								childConstraints.setIndentStrategy(preferences.getTextIndentStrategy());
							}
							childConstraints.setIsWhitespaceStrategyAHint(true);
							childConstraints.setIsIndentStrategyAHint(true);
						}

						// try referring to content model for information on
						// whitespace & indent strategy
						ModelQueryAdapter adapter = (ModelQueryAdapter) ((IDOMDocument) currentNode.getOwnerDocument()).getAdapterFor(ModelQueryAdapter.class);
						CMElementDeclaration elementDeclaration = (CMElementDeclaration) adapter.getModelQuery().getCMNode(currentNode);
						if (elementDeclaration != null) {
							// follow whitespace strategy preference for
							// pcdata content
							int contentType = elementDeclaration.getContentType();
							if (contentType == CMElementDeclaration.PCDATA) {
								childConstraints.setWhitespaceStrategy(preferences.getPCDataWhitespaceStrategy());
							}
							else if (contentType == CMElementDeclaration.ELEMENT) {
								childConstraints.setWhitespaceStrategy(XMLFormattingConstraints.IGNORE);
								childConstraints.setIndentStrategy(XMLFormattingConstraints.INDENT);
								childConstraints.setIsWhitespaceStrategyAHint(true);
								childConstraints.setIsIndentStrategyAHint(true);
							}
							else {
								CMDataType dataType = elementDeclaration.getDataType();
								if (dataType != null && STRING.equals(dataType.getDataTypeName()))
									childConstraints.setWhitespaceStrategy(XMLFormattingConstraints.PRESERVE);
								else {
									// look for xml:space in content model
									CMNamedNodeMap cmAttributes = elementDeclaration.getAttributes();
									// Check implied values from the DTD way.
									CMAttributeDeclaration attributeDeclaration = (CMAttributeDeclaration) cmAttributes.getNamedItem(XML_SPACE);
									if (attributeDeclaration != null) {
										// CMAttributeDeclaration found, check
										// it
										// out.
										String defaultValue = attributeDeclaration.getAttrType().getImpliedValue();

										// xml:space="preserve" means preserve
										// space,
										// everything else means back to
										// default.
										if (defaultValue.compareTo(PRESERVE) == 0)
											childConstraints.setWhitespaceStrategy(XMLFormattingConstraints.PRESERVE);
										else
											childConstraints.setWhitespaceStrategy(null);
									}
								}
							}
						}
					}
				}
			}
			// set default values according to preferences
			if (childConstraints.getWhitespaceStrategy() == null) {
				childConstraints.setWhitespaceStrategy(preferences.getElementWhitespaceStrategy());
			}
			if (childConstraints.getIndentStrategy() == null) {
				childConstraints.setIndentStrategy(preferences.getElementIndentStrategy());
			}
		}
	}

	/**
	 * Calculates the current available line width given fullText.
	 * 
	 * @param fullText
	 * @param availableLineWidth
	 * @param maxAvailableLineWidth
	 * @return
	 */
	private int updateLineWidthWithLastLine(String fullText, int availableLineWidth) {
		int maxAvailableLineWidth = getFormattingPreferences().getMaxLineWidth();
		int lineWidth = availableLineWidth;
		if (fullText != null) {
			int textLength = fullText.length();
			// update available line width
			// find last newline
			int lastLFOffset = fullText.lastIndexOf('\n');
			int lastCROffset = fullText.lastIndexOf('\r');
			// all text was on 1 line
			if (lastLFOffset == -1 && lastCROffset == -1) {
				// just subtract text length from current
				// available line width
				lineWidth -= fullText.length();
			}
			else {
				// calculate available line width of last line
				int lastNewLine = Math.max(lastLFOffset, lastCROffset);
				lineWidth = maxAvailableLineWidth - (textLength - lastNewLine);
			}
		}
		return lineWidth;
	}

	private String getLineDelimiter(IStructuredDocumentRegion currentRegion) {
		IStructuredDocument doc = currentRegion.getParentDocument();
		int line = doc.getLineOfOffset(currentRegion.getStartOffset());
		String lineDelimiter = doc.getLineDelimiter();
		try {
			if (line > 0) {
				lineDelimiter = doc.getLineDelimiter(line - 1);
			}
		}
		catch (BadLocationException e) {
			// log for now, unless we find reason not to
			Logger.log(Logger.INFO, e.getMessage());
		}
		// BUG115716: if cannot get line delimiter from current line, just
		// use default line delimiter
		if (lineDelimiter == null)
			lineDelimiter = doc.getLineDelimiter();
		return lineDelimiter;
	}

	void setProgressMonitor(IProgressMonitor monitor) {
		fProgressMonitor = monitor;
	}
}