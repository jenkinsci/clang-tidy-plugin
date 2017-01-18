/*******************************************************************************
 * Copyright (c) 2009-2011 Thales Corporate Services SAS                        *
 * Copyright (c) 2017 PIXMAP                                                    *
 * Author : Gregory Boissinot                                                   *
 * Author : Mickael Germain                                                     *
 *                                                                              *
 * Permission is hereby granted, free of charge, to any person obtaining a copy *
 * of this software and associated documentation files (the "Software"), to deal*
 * in the Software without restriction, including without limitation the rights *
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell    *
 * copies of the Software, and to permit persons to whom the Software is        *
 * furnished to do so, subject to the following conditions:                     *
 *                                                                              *
 * The above copyright notice and this permission notice shall be included in   *
 * all copies or substantial portions of the Software.                          *
 *                                                                              *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR   *
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,     *
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE  *
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER       *
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,*
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN    *
 * THE SOFTWARE.                                                                *
 *******************************************************************************/

package org.jenkinsci.plugins.clangtidy.model;

import hudson.model.ModelObject;

import org.apache.commons.lang.StringEscapeUtils;
import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;

import java.io.Serializable;

@ExportedBean(defaultVisibility = 999)
public class ClangtidyFile implements ModelObject, Serializable {

	private static final long serialVersionUID = 3L;

	private Integer key;

	private String fileName;

	private int lineNumber;

	private int columnNumber;

	private String type;

	private String id;

	private String severity;

	private String message;

	@Exported
	public int getColumnNumber() {
		return columnNumber;
	}

	@Override
	public String getDisplayName() {
		return "clangtidyFile";
	}

	@Exported
	public String getFileName() {
		return fileName;
	}

	/**
	 * Get the filename.
	 *
	 * @return the filename or empty string if the filename is null
	 */
	public String getFileNameNotNull() {
		return (fileName != null) ? fileName : "";
	}

	@Exported
	public String getId() {
		return id;
	}

	@Exported
	public Integer getKey() {
		return key;
	}

	@Exported
	public int getLineNumber() {
		return lineNumber;
	}

	/**
	 * Get line number depending on availability of the file name.
	 *
	 * @return the line number or empty string if the file name is empty
	 */
	public String getLineNumberString() {
		return ("".equals(getFileNameNotNull())) ? "" : String.valueOf(lineNumber);
	}

	/**
	 * Returns the line number that should be shown on top of the source code
	 * view.
	 *
	 * @return the line number
	 */
	public int getLinkLineNumber() {
		return Math.max(1, lineNumber - 10);
	}

	@Exported
	public String getMessage() {
		return message;
	}

	public String getMessageHtml() {
		return StringEscapeUtils.escapeHtml(message);
	}

	@Exported
	public String getSeverity() {
		return severity;
	}

	@Exported
	public String getType() {
		return type;
	}

	public void setColumnNumber(int columnNumber) {
		this.columnNumber = columnNumber;
	}

	public void setFileName(String filename) {
		fileName = filename;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setKey(Integer key) {
		this.key = key;
	}

	public void setLineNumber(int lineNumber) {
		this.lineNumber = lineNumber;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void setSeverity(String severity) {
		this.severity = severity;
	}

	public void setType(String type) {
		this.type = type;
	}
}
