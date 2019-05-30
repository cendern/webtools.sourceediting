/*******************************************************************************
 * Copyright (c) 2004, 2019 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.wst.html.ui.internal.contentassist.resources;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.content.IContentType;

/**
 * Cache for the filenames and filename extensions registered for a content
 * type and all of its descendants
 */
public class ContentTypeSpecs {
	public static ContentTypeSpecs createFor(String contentTypeId) {
//		long startTime = System.currentTimeMillis();
		IContentType baseContentType = Platform.getContentTypeManager().getContentType(contentTypeId);
		String[] baseExtensions = baseContentType.getFileSpecs(IContentType.FILE_EXTENSION_SPEC);
		Arrays.sort(baseExtensions);
		Set<String> filenameExtensions = new HashSet<>();
		Set<String> filenames = new HashSet<>();
		IContentType[] contentTypes = Platform.getContentTypeManager().getAllContentTypes();
		for (int i = 0, length = contentTypes.length; i < length; i++) {
			if (contentTypes[i].isKindOf(baseContentType)) {
				String[] fileExtension = contentTypes[i].getFileSpecs(IContentType.FILE_EXTENSION_SPEC);
				for (int j = 0; j < fileExtension.length; j++) {
					filenameExtensions.add(fileExtension[j]);
				}
				String[] names = contentTypes[i].getFileSpecs(IContentType.FILE_NAME_SPEC);
				for (int j = 0; j < names.length; j++) {
					filenames.add(names[j]);
				}
			}
		}
		// move the extensions of the base type to the start to match quicker
		String[] stringExtensions = filenameExtensions.toArray(new String[filenameExtensions.size()]);
		for (int i = stringExtensions.length - 1; i > 0; i--) {
			if (Arrays.binarySearch(baseExtensions, stringExtensions[i]) >= 0) {
				stringExtensions[i] = stringExtensions[i - 1];
			}
		}
//		System.out.println("Discovered content types for " + contentTypeId + " in " + (System.currentTimeMillis() - startTime) + "ms");
		return new ContentTypeSpecs(filenames.toArray(new String[filenames.size()]), stringExtensions);
	}

	String[] fFilenames = new String[0];
	String[] fExtensions = new String[0];

	private ContentTypeSpecs(String[] fileNames, String[] extensions) {
		super();
		fFilenames = fileNames;
		fExtensions = extensions;
		Arrays.sort(fileNames);
	}

	public boolean matches(String filename) {
		if (Arrays.binarySearch(fFilenames, filename) >= 0) {
			return true;
		}
		for (int i = 0; i < fExtensions.length; i++) {
			if (filename.length() > fExtensions[i].length() + 1
					&& filename.charAt(filename.length() - fExtensions[i].length() - 1) == '.'
					&& filename.endsWith(fExtensions[i])) {
				return true;
			}
		}
		return false;
	}

	public void addFilename(String filename) {
		List<String> combinedList = new ArrayList<String>(fFilenames.length + 1);
		for (int i = 0; i < fFilenames.length; i++) {
			combinedList.add(fFilenames[i]);
		}
		combinedList.add(filename);
		String[] combinedArray = combinedList.toArray(new String[combinedList.size()]);
		Arrays.sort(combinedArray);
		fFilenames = combinedArray;
	}

	public void addFilenameExtension(String extension) {
		List<String> combinedList = new ArrayList<String>(fExtensions.length + 1);
		for (int i = 0; i < fExtensions.length; i++) {
			combinedList.add(fExtensions[i]);
		}
		combinedList.add(extension);
		String[] combinedArray = combinedList.toArray(new String[combinedList.size()]);
		fExtensions = combinedArray;
	}
}
