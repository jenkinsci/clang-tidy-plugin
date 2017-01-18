package org.jenkinsci.plugins.clangtidy;

import hudson.model.AbstractBuild;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringEscapeUtils;

import org.jenkinsci.plugins.clangtidy.model.ClangtidyWorkspaceFile;

/**
 * Show all violations highlighted on a single page.
 *
 * @author Michal Turek
 * @author Mickael Germain
 */
public class ClangtidySourceAll {
	/** The related build. */
	private final AbstractBuild<?, ?> owner;

	/** The files to show. */
	private final Collection<ClangtidyWorkspaceFile> files;

	/** Number of lines to show before the highlighted line. */
	private final int linesBefore;

	/** Number of lines to show after the highlighted line. */
	private final int linesAfter;

	/**
	 * Constructor.
	 *
	 * @param owner
	 *            the related build
	 * @param files
	 *            the files to show
	 * @param linesBefore
	 *            number of lines to show before the highlighted line
	 * @param linesAfter
	 *            number of lines to show after the highlighted line
	 */
	public ClangtidySourceAll(AbstractBuild<?, ?> owner, Collection<ClangtidyWorkspaceFile> files, int linesBefore,
			int linesAfter) {
		this.owner = owner;
		this.files = files;
		this.linesBefore = linesBefore;
		this.linesAfter = linesAfter;
	}

	public Collection<ClangtidyWorkspaceFile> getFiles() {
		return files;
	}

	public int getLinesAfter() {
		return linesAfter;
	}

	public int getLinesBefore() {
		return linesBefore;
	}

	public AbstractBuild<?, ?> getOwner() {
		return owner;
	}

	/**
	 * Get specified lines from a stream.
	 *
	 * @param reader
	 *            the input stream
	 * @param lineNumber
	 *            the base line
	 * @return the lines with HTML formatting
	 * @throws IOException
	 *             if something fails
	 */
	private String getRelatedLines(BufferedReader reader, int lineNumber) throws IOException {
		final int start = (lineNumber > linesBefore) ? lineNumber - linesBefore : 1;
		final int end = lineNumber + linesAfter;
		final String numberFormat = "%0" + String.valueOf(end).length() + "d";

		StringBuilder builder = new StringBuilder();
		int current = 1;
		String line = "";

		while (((line = reader.readLine()) != null) && (current <= end)) {
			if (current >= start) {
				if (current == lineNumber) {
					builder.append("<div class=\"line highlighted\">");
				} else {
					builder.append("<div class=\"line\">");
				}

				builder.append("<span class=\"lineNumber\">");
				builder.append(String.format(numberFormat, current));
				builder.append("</span> ");// The space separates line number
											// and code
				builder.append(StringEscapeUtils.escapeHtml(line));
				builder.append("</div>\n");
			}

			++current;
		}

		return builder.toString();
	}

	/**
	 * Get specified lines of source code from the file.
	 *
	 * @param file
	 *            the input file
	 * @return the related lines of code with HTML formatting
	 */
	public String getSourceCode(ClangtidyWorkspaceFile file) {
		File tempFile = new File(file.getTempName(owner));

		if (!tempFile.exists()) {
			return "Can't read file: " + tempFile.getAbsolutePath();
		}

		BufferedReader reader = null;

		try {
			reader = new BufferedReader(new FileReader(tempFile));
			return getRelatedLines(reader, file.getClangtidyFile().getLineNumber());
		} catch (FileNotFoundException e) {
			return "Can't read file: " + e;
		} catch (IOException e) {
			return "Reading file failed: " + e;
		} finally {
			IOUtils.closeQuietly(reader);
		}
	}
}
