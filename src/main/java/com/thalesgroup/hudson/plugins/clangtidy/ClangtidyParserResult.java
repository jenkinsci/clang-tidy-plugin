/*******************************************************************************
 * Copyright (c) 2009-2011 Thales Corporate Services SAS                        *
 * Author : Gregory Boissinot                                                   *
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

import com.thalesgroup.hudson.plugins.clangtidy.parser.ClangtidyParser;
import com.thalesgroup.hudson.plugins.clangtidy.util.ClangtidyLogger;
import hudson.FilePath;
import hudson.Util;
import hudson.model.BuildListener;
import hudson.remoting.VirtualChannel;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.selectors.FileSelector;

import java.io.File;
import java.io.IOException;

public class ClangtidyParserResult implements FilePath.FileCallable<ClangtidyReport> {

    private static final long serialVersionUID = 1L;

    private final BuildListener listener;

    private final String clangtidyReportPattern;

    private final boolean ignoreBlankFiles;

    public static final String DELAULT_REPORT_MAVEN = "**/clangtidy-result.xml";

    public ClangtidyParserResult(final BuildListener listener, String clangtidyReportPattern, boolean ignoreBlankFiles) {

        if (clangtidyReportPattern == null) {
            clangtidyReportPattern = DELAULT_REPORT_MAVEN;
        }

        if (clangtidyReportPattern.trim().length() == 0) {
            clangtidyReportPattern = DELAULT_REPORT_MAVEN;
        }

        this.listener = listener;
        this.clangtidyReportPattern = clangtidyReportPattern;
        this.ignoreBlankFiles = ignoreBlankFiles;
    }

    public ClangtidyReport invoke(java.io.File basedir, VirtualChannel channel) throws IOException {

        ClangtidyReport clangtidyReportResult = new ClangtidyReport();
        try {
            String[] clangtidyReportFiles = findClangtidyReports(basedir);
            if (clangtidyReportFiles.length == 0) {
                String msg = "No clangtidy test report file(s) were found with the pattern '"
                        + clangtidyReportPattern + "' relative to '"
                        + basedir + "'."
                        + "  Did you enter a pattern relative to the correct directory?"
                        + "  Did you generate the XML report(s) for Clangtidy?";
                throw new IllegalArgumentException(msg);
            }

            ClangtidyLogger.log(listener, "Processing " + clangtidyReportFiles.length + " files with the pattern '" + clangtidyReportPattern + "'.");

            for (String cppchecReportkFileName : clangtidyReportFiles) {
                ClangtidyReport clangtidyReport = new ClangtidyParser().parse(new File(basedir, cppchecReportkFileName));
                mergeReport(clangtidyReportResult, clangtidyReport);
            }
        } catch (Exception e) {
            ClangtidyLogger.log(listener, "Parsing throws exceptions. " + e.getMessage());
            return null;
        }

        return clangtidyReportResult;
    }


    private static void mergeReport(ClangtidyReport clangtidyReportResult, ClangtidyReport clangtidyReport) {
        clangtidyReportResult.getPossibleErrorSeverities().addAll(clangtidyReport.getPossibleErrorSeverities());
        clangtidyReportResult.getPossibleStyleSeverities().addAll(clangtidyReport.getPossibleStyleSeverities());
        clangtidyReportResult.getErrorSeverities().addAll(clangtidyReport.getErrorSeverities());
        clangtidyReportResult.getEverySeverities().addAll(clangtidyReport.getEverySeverities());
        clangtidyReportResult.getNoCategorySeverities().addAll(clangtidyReport.getNoCategorySeverities());
        clangtidyReportResult.getStyleSeverities().addAll(clangtidyReport.getStyleSeverities());
    }

    /**
     * Return all clangtidy report files
     *
     * @param parentPath parent
     * @return an array of strings
     */
    private String[] findClangtidyReports(File parentPath) {
        FileSet fs = Util.createFileSet(parentPath, this.clangtidyReportPattern);
        if (this.ignoreBlankFiles) {
            fs.add(new FileSelector() {
                public boolean isSelected(File basedir, String filename, File file) throws BuildException {
                    return file != null && file.length() != 0;
                }
            });
        }
        DirectoryScanner ds = fs.getDirectoryScanner();
        return ds.getIncludedFiles();
    }

    public String getClangtidyReportPattern() {
        return clangtidyReportPattern;
    }

}
