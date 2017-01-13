/*******************************************************************************
 * Copyright (c) 2009 Thales Corporate Services SAS                             *
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

package com.thalesgroup.hudson.plugins.clangtidy;

import com.thalesgroup.hudson.plugins.clangtidy.model.ClangtidyFile;
import com.thalesgroup.hudson.plugins.clangtidy.model.ClangtidyWorkspaceFile;
import de.java2html.converter.JavaSource2HTMLConverter;
import de.java2html.javasource.JavaSource;
import de.java2html.javasource.JavaSourceParser;
import de.java2html.options.JavaSourceConversionOptions;
import hudson.model.AbstractBuild;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

import java.io.*;


public class ClangtidySource implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Offset of the source code generator. After this line the actual source file lines start.
     */
    protected static final int SOURCE_GENERATOR_OFFSET = 13;

    /**
     * Color for the first (primary) annotation range.
     */
    private static final String MESSAGE_COLOR = "#FCAF3E";

    /**
     * The current build as owner of this object.
     */
    private final AbstractBuild<?, ?> owner;

    /**
     * The clangtidy source file in the workspace to be shown.
     */
    private final ClangtidyWorkspaceFile clangtidyWorkspaceFile;

    /**
     * The rendered source file.
     */
    private String sourceCode = StringUtils.EMPTY;

    /**
     * Creates a new instance of this source code object.
     *
     * @param owner                 the current build as owner of this object
     * @param clangtidyWorkspaceFile the abstract workspace file
     */
    public ClangtidySource(final AbstractBuild<?, ?> owner, ClangtidyWorkspaceFile clangtidyWorkspaceFile) {
        this.owner = owner;
        this.clangtidyWorkspaceFile = clangtidyWorkspaceFile;
        buildFileContent();
    }


    /**
     * Builds the file content.
     */
    private void buildFileContent() {
        InputStream is = null;
        try {
            File tempFile = new File(clangtidyWorkspaceFile.getTempName(owner));
            if (tempFile.exists()) {
                is = new FileInputStream(tempFile);
            } else {
                // Reading real workspace file is more incorrect than correct,
                // but the code is left here for backward compatibility with
                // plugin version 1.14 and less
                if (clangtidyWorkspaceFile.getFileName() == null) {
                    throw new IOException("The file doesn't exist.");
                }

                File file = new File(clangtidyWorkspaceFile.getFileName());
                if (!file.exists()) {
                    throw new IOException("Can't access the file: " + file.toURI());
                }
                is = new FileInputStream(file);
            }

            splitSourceFile(highlightSource(is));
        } catch (IOException exception) {
            sourceCode = "Can't read file: " + exception.getLocalizedMessage();
        } catch (RuntimeException re) {
            sourceCode = "Problem for display the source code content: " + re.getLocalizedMessage();
        } finally {
            IOUtils.closeQuietly(is);
        }
    }


    /**
     * Splits the source code into three blocks: the line to highlight and the
     * source code before and after this line.
     *
     * @param sourceFile the source code of the whole file as rendered HTML string
     */
    private void splitSourceFile(final String sourceFile) {
        StringBuilder output = new StringBuilder(sourceFile.length());

        ClangtidyFile clangtidyFile = clangtidyWorkspaceFile.getClangtidyFile();
        LineIterator lineIterator = IOUtils.lineIterator(new StringReader(sourceFile));
        int lineNumber = 1;


        //---header
        while (lineNumber < SOURCE_GENERATOR_OFFSET) {
            copyLine(output, lineIterator);
            lineNumber++;
        }
        lineNumber = 1;

        //---iterate before the error line
        while (lineNumber < clangtidyFile.getLineNumber()) {
            copyLine(output, lineIterator);
            lineNumber++;
        }
        output.append("</code>\n");

        //---Error message
        output.append("</td></tr>\n");
        output.append("<tr><td bgcolor=\"");
        appendRangeColor(output);
        output.append("\">\n");

        output.append("<div tooltip=\"");

        outputEscaped(output, "<h3>");
        outputEscaped(output, clangtidyFile.getId());
        output.append(": ");
        outputEscaped(output, clangtidyFile.getMessage());
        outputEscaped(output, "</h3>");

        output.append("\" nodismiss=\"\">\n");
        output.append("<code><b>\n");

        //The current line error
        copyLine(output, lineIterator);
        lineNumber++;

        //End of the code
        output.append("</b></code>\n");
        output.append("</div>\n");
        output.append("</td></tr>\n");

        output.append("<tr><td>\n");
        output.append("<code>\n");
        while (lineIterator.hasNext()) {
            copyLine(output, lineIterator);
        }
        output.append("</code>\n");
        output.append("</td></tr>\n");

        sourceCode = output.toString();
    }


    /**
     * Writes the message to the output stream (with escaped HTML).
     *
     * @param output  the output to write to
     * @param message the message to write
     */
    private void outputEscaped(final StringBuilder output, final String message) {
        output.append(StringEscapeUtils.escapeHtml(message));
    }

    /**
     * Appends the message color.
     *
     * @param output the output to append the color
     */
    private void appendRangeColor(final StringBuilder output) {
        output.append(MESSAGE_COLOR);
    }

    /**
     * Copies the next line of the input to the output.
     *
     * @param output       output
     * @param lineIterator input
     */
    private void copyLine(final StringBuilder output, final LineIterator lineIterator) {
        output.append(lineIterator.nextLine());
        output.append("\n");
    }

    /**
     * Highlights the specified source and returns the result as an HTML string.
     *
     * @param file the source file to highlight
     * @return the source as an HTML string
     * @throws IOException
     */
    public final String highlightSource(final InputStream file) throws IOException {

        JavaSource source = new JavaSourceParser().parse(new InputStreamReader(file));
        JavaSource2HTMLConverter converter = new JavaSource2HTMLConverter();
        StringWriter writer = new StringWriter();
        JavaSourceConversionOptions options = JavaSourceConversionOptions.getDefault();
        options.setShowLineNumbers(true);
        options.setAddLineAnchors(true);
        converter.convert(source, options, writer);
        return writer.toString();
    }


    /**
     * Retrieve the source code for the clangtidy source file.
     *
     * @return the source code content as a String object
     */
    public String getSourceCode() {
        return sourceCode;
    }

    /**
     * Returns the abstract Clangtidy workspace file.
     *
     * @return the workspace file
     */
    public ClangtidyWorkspaceFile getClangtidyWorkspaceFile() {
        return clangtidyWorkspaceFile;
    }

    /**
     * Get the owner build.
     * 
     * @return the build
     */
    public AbstractBuild<?, ?> getOwner() {
        return owner;
    }
}

