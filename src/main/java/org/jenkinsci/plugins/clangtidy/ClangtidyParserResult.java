package org.jenkinsci.plugins.clangtidy;


import hudson.FilePath;
import hudson.Util;
import hudson.model.BuildListener;
import hudson.remoting.VirtualChannel;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.selectors.FileSelector;
import org.jenkinsci.plugins.clangtidy.parser.ClangtidyParser;
import org.jenkinsci.plugins.clangtidy.util.ClangtidyLogger;

import java.io.File;
import java.io.IOException;

/**
 * @author Gregory Boissinot
 * @author Mickael Germain
 */
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
                ClangtidyLogger.log(listener, msg);
                throw new IllegalArgumentException(msg);
            }

            ClangtidyLogger.log(listener, "Processing " + clangtidyReportFiles.length + " files with the pattern '" + clangtidyReportPattern + "'.");

            for (String cppchecReportkFileName : clangtidyReportFiles) {
                ClangtidyReport clangtidyReport = new ClangtidyParser().parse(new File(basedir, cppchecReportkFileName), listener);
                mergeReport(clangtidyReportResult, clangtidyReport);
            }
        } catch (Exception e) {
            ClangtidyLogger.log(listener, "Parsing throws exceptions. " + e.getMessage());
            return null;
        }

        return clangtidyReportResult;
    }


    private static void mergeReport(ClangtidyReport clangtidyReportResult, ClangtidyReport clangtidyReport) {
        clangtidyReportResult.getErrorSeverityList().addAll(clangtidyReport.getErrorSeverityList());
        clangtidyReportResult.getWarningSeverityList().addAll(clangtidyReport.getWarningSeverityList());
        clangtidyReportResult.getBoostWarningList().addAll(clangtidyReport.getBoostWarningList());
        clangtidyReportResult.getCertWarningList().addAll(clangtidyReport.getCertWarningList());
        clangtidyReportResult.getCppcoreguidelinesWarningList().addAll(clangtidyReport.getCppcoreguidelinesWarningList());
        clangtidyReportResult.getClangAnalyzerWarningList().addAll(clangtidyReport.getClangAnalyzerWarningList());
        clangtidyReportResult.getClangDiagnosticWarningList().addAll(clangtidyReport.getClangDiagnosticWarningList());
        clangtidyReportResult.getGoogleWarningList().addAll(clangtidyReport.getGoogleWarningList());
        clangtidyReportResult.getLlvmWarningList().addAll(clangtidyReport.getLlvmWarningList());
        clangtidyReportResult.getMiscWarningList().addAll(clangtidyReport.getMiscWarningList());
        clangtidyReportResult.getModernizeWarningList().addAll(clangtidyReport.getModernizeWarningList());
        clangtidyReportResult.getMpiWarningList().addAll(clangtidyReport.getMpiWarningList());
        clangtidyReportResult.getPerformanceWarningList().addAll(clangtidyReport.getPerformanceWarningList());
        clangtidyReportResult.getReadabilityWarningList().addAll(clangtidyReport.getReadabilityWarningList());
        clangtidyReportResult.getAllErrors().addAll(clangtidyReport.getAllErrors());
        clangtidyReportResult.getVersions().add(clangtidyReport.getVersion());
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
