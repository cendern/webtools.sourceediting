/**
 *  Copyright (c) 2013-2015 Angelo ZERR.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License 2.0
 *  which accompanies this distribution, and is available at
 *  https://www.eclipse.org/legal/epl-2.0/
 *
 *  SPDX-License-Identifier: EPL-2.0
 *
 *  Contributors:
 *  Angelo Zerr <angelo.zerr@gmail.com> - initial API and implementation
 */
package org.eclipse.wst.json.npm.ui.internal.contentassist;

import java.text.MessageFormat;

import org.eclipse.json.provisonnal.com.eclipsesource.json.JsonArray;
import org.eclipse.json.provisonnal.com.eclipsesource.json.JsonObject;
import org.eclipse.json.provisonnal.com.eclipsesource.json.JsonValue;
import org.eclipse.json.schema.JSONSchemaType;
import org.eclipse.swt.graphics.Image;
import org.eclipse.wst.json.npm.ui.internal.editor.NPMEditorPluginImageHelper;
import org.eclipse.wst.json.npm.ui.internal.editor.NPMEditorPluginImages;
import org.eclipse.wst.json.ui.contentassist.ContentAssistHelper;
import org.eclipse.wst.json.ui.contentassist.ContentAssistRequest;
import org.eclipse.wst.json.ui.contentassist.HttpCompletionProposalCollector;
import org.eclipse.wst.json.ui.contentassist.JSONKeyCompletionProposal;
import org.eclipse.wst.json.ui.contentassist.JSONRelevanceConstants;
import org.eclipse.wst.sse.ui.contentassist.CompletionProposalInvocationContext;

public class NPMDependencyCompletionProposalCollector extends
		HttpCompletionProposalCollector {

	public final String searchUrl = "https://typeahead.npmjs.com/search?q={0}";
	public final String packageUrl = "http://registry.npmjs.org/{0}/latest";

	@Override
	protected String getUrl(ContentAssistRequest contentAssistRequest,
			TargetType target) {
		String keyword = contentAssistRequest.getMatchString();
		return MessageFormat.format(searchUrl, keyword);
	}

	protected void addProposals(JsonValue json,
			ContentAssistRequest contentAssistRequest,
			CompletionProposalInvocationContext context, TargetType target) {
		if (json.isArray()) {
			// Ex :
			// [{"value":"express"},{"value":"lodash"},{"value":"gulp"}]
			String dependency = null;
			String replacementString = null;
			JsonArray values = (JsonArray) json;
			for (JsonValue value : values) {
				if (value.isObject()) {
					dependency = ((JsonObject) value).get("value").asString();
					replacementString = ContentAssistHelper.getRequiredName(
							dependency, JSONSchemaType.String);
					Image icon = NPMEditorPluginImageHelper.getInstance()
							.getImage(NPMEditorPluginImages.IMG_OBJ_NPM);
					JSONKeyCompletionProposal proposal = new JSONKeyCompletionProposal(
							replacementString,
							contentAssistRequest.getReplacementBeginPosition(),
							contentAssistRequest.getReplacementLength(),
							replacementString.length() - 2, icon, dependency,
							null, null, JSONRelevanceConstants.R_OBJECT_KEY);
					contentAssistRequest.addProposal(proposal);
				}
			}
		}
		//System.err.println(json);
	}
}
