/**
 *  Copyright (c) 2013, 2019 Angelo ZERR and others
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License 2.0
 *  which accompanies this distribution, and is available at
 *  https://www.eclipse.org/legal/epl-2.0/
 *
 *  SPDX-License-Identifier: EPL-2.0
 *
 *  Contributors:
 *  Angelo Zerr <angelo.zerr@gmail.com> - initial API and implementation
 *  Mickael Istria (Red Hat Inc.) - Extracted, refactored and moved to org.eclipse
 *  Nitin Dahyabhai (IBM Corporation) - improve performance finding matching resources
 */
package org.eclipse.wst.html.ui.internal.contentassist.resources;

import java.util.Locale;

import org.eclipse.core.runtime.content.IContentTypeManager;
import org.eclipse.wst.xml.ui.internal.contentassist.ContentAssistRequest;
import org.w3c.dom.Node;

public class HrefWebResourcesCompletionProposalComputer extends AbstractWebResourcesCompletionProposalComputer {

	ContentTypeSpecs fileMatcher = ContentTypeSpecs.createFor(IContentTypeManager.CT_TEXT);
	{
		fileMatcher.addFilenameExtension("txt");
	}

	@Override
	ContentTypeSpecs createFilenameMatcher() {
		return fileMatcher;
	}

	@Override
	boolean matchRequest(ContentAssistRequest contentAssistRequest) {
		Node node = contentAssistRequest.getNode();
		return "a".equals(node.getLocalName().toLowerCase(Locale.US)) && "href".equals(getCurrentAttributeName(contentAssistRequest).toLowerCase(Locale.US));
	}
}
