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

	public static final String DELAULT_REPORT_MAVEN = "**/clangtidy-result.xml";

	private static void mergeReport(ClangtidyReport clangtidyReportResult, ClangtidyReport clangtidyReport) {
		clangtidyReportResult.getErrorSeverityList().addAll(clangtidyReport.getErrorSeverityList());
		clangtidyReportResult.getWarningSeverityList().addAll(clangtidyReport.getWarningSeverityList());
		clangtidyReportResult.getBoostWarningList().addAll(clangtidyReport.getBoostWarningList());
		clangtidyReportResult.getCertWarningList().addAll(clangtidyReport.getCertWarningList());
		clangtidyReportResult.getCppcoreguidelinesWarningList()
				.addAll(clangtidyReport.getCppcoreguidelinesWarningList());
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

	private final BuildListener listener;

	private final String clangtidyReportPattern;

	private final boolean ignoreBlankFiles;

	public ClangtidyParserResult(final BuildListener listener, String clangtidyReportPattern,
			boolean ignoreBlankFiles) {

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

	/**
	 * Return all clangtidy report files
	 *
	 * @param parentPath
	 *            parent
	 * @return an array of strings
	 */
	private String[] findClangtidyReports(File parentPath) {
		FileSet fs = Util.createFileSet(parentPath, clangtidyReportPattern);
		if (ignoreBlankFiles) {
			fs.add(new FileSelector() {
				@Override
				public boolean isSelected(File basedir, String filename, File file) throws BuildException {
					return (file != null) && (file.length() != 0);
				}
			});
		}
		DirectoryScanner ds = fs.getDirectoryScanner();
		return ds.getIncludedFiles();
	}

	public String getClangtidyReportPattern() {
		return clangtidyReportPattern;
	}

	@Override
	public ClangtidyReport invoke(java.io.File basedir, VirtualChannel channel) throws IOException {

		ClangtidyReport clangtidyReportResult = new ClangtidyReport();
		try {
			String[] clangtidyReportFiles = findClangtidyReports(basedir);
			if (clangtidyReportFiles.length == 0) {
				String msg = "No clangtidy test report file(s) were found with the pattern '" + clangtidyReportPattern
						+ "' relative to '" + basedir + "'."
						+ "  Did you enter a pattern relative to the correct directory?"
						+ "  Did you generate the XML report(s) for Clangtidy?";
				ClangtidyLogger.log(listener, msg);
				throw new IllegalArgumentException(msg);
			}

			ClangtidyLogger.log(listener, "Processing " + clangtidyReportFiles.length + " files with the pattern '"
					+ clangtidyReportPattern + "'.");

			for (String cppchecReportkFileName : clangtidyReportFiles) {
				ClangtidyReport clangtidyReport = new ClangtidyParser().parse(new File(basedir, cppchecReportkFileName),
						listener);
				mergeReport(clangtidyReportResult, clangtidyReport);

				ClangtidyLogger.log(listener,
						"Merged " + clangtidyReportResult.getNumberErrorSeverity() + " Error severities");
				ClangtidyLogger.log(listener,
						"Merged " + clangtidyReportResult.getNumberWarningSeverity() + " Warning severities");
				ClangtidyLogger.log(listener,
						"Merged " + clangtidyReportResult.getNumberBoostWarning() + " Boost warnings");
				ClangtidyLogger.log(listener,
						"Merged " + clangtidyReportResult.getNumberCertWarning() + " Cert warnings");
				ClangtidyLogger.log(listener, "Merged " + clangtidyReportResult.getNumberCppcoreguidelinesWarning()
						+ " Cppcoreguidelines warnings");
				ClangtidyLogger.log(listener,
						"Merged " + clangtidyReportResult.getNumberClangAnalyzerWarning() + " Clang-Analyzer warnings");
				ClangtidyLogger.log(listener, "Merged " + clangtidyReportResult.getNumberClangDiagnosticWarning()
						+ " Clang-Diagnostic warnings");
				ClangtidyLogger.log(listener,
						"Merged " + clangtidyReportResult.getNumberGoogleWarning() + " Google warnings");
				ClangtidyLogger.log(listener,
						"Merged " + clangtidyReportResult.getNumberLlvmWarning() + " LLVM warnings");
				ClangtidyLogger.log(listener,
						"Merged " + clangtidyReportResult.getNumberMiscWarning() + " Misc warnings");
				ClangtidyLogger.log(listener,
						"Merged " + clangtidyReportResult.getNumberModernizeWarning() + " Modernize warnings");
				ClangtidyLogger.log(listener,
						"Merged " + clangtidyReportResult.getNumberMpiWarning() + " MPI warnings");
				ClangtidyLogger.log(listener,
						"Merged " + clangtidyReportResult.getNumberPerformanceWarning() + " Performance warnings");
				ClangtidyLogger.log(listener,
						"Merged " + clangtidyReportResult.getNumberReadabilityWarning() + " Readability warnings");
				ClangtidyLogger.log(listener, "Merged " + clangtidyReportResult.getNumberTotal() + " All Errors");
				ClangtidyLogger.log(listener, "Merged " + clangtidyReportResult.getVersions().size() + " Versions");
			}
		} catch (Exception e) {
			ClangtidyLogger.log(listener, "Parsing throws exceptions. " + e.getMessage());
			StackTraceElement[] elements = e.getStackTrace();

			for (StackTraceElement element : elements) {
				ClangtidyLogger.log(listener, element.toString());
			}
			return null;
		}

		return clangtidyReportResult;
	}
}
