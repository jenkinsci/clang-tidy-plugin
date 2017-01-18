package org.jenkinsci.plugins.clangtidy;

import static org.mockito.Mockito.mock;

import java.io.File;
import java.util.List;

import org.jenkinsci.plugins.clangtidy.parser.ClangtidyParser;
import org.junit.Before;
import org.junit.Test;

import com.thalesgroup.hudson.plugins.clangtidy.model.ClangtidyFile;

import hudson.model.BuildListener;
import junit.framework.Assert;

/**
 * @author Gregory Boissinot
 * @author Mickael Germain
 */
public class ClangtidyParserTest {

	ClangtidyParser clangtidyParser;

	private void processClangtidy(String filename, int nbAllErrors, int nbErrorSeverity, int nbWarningSeverity,
			int nbBoostWarning, int nbCertWarning, int nbClangAnalyzerWarning, int nbClangDiagnosticWarning,
			int nbCppcoreguidelinesWarning, int nbGoogleWarning, int nbLlvmWarning, int nbMiscWarning,
			int nbModernizeWarning, int nbMpiWarning, int nbPerformanceWarning, int nbReadabilityWarning)
			throws Exception {

		BuildListener listener = mock(BuildListener.class);
		ClangtidyReport clangtidyReport = clangtidyParser
				.parse(new File(ClangtidyParserTest.class.getResource(filename).toURI()), listener);

		List<ClangtidyFile> allErrors = clangtidyReport.getAllErrors();

		List<ClangtidyFile> errorSeverityList = clangtidyReport.getErrorSeverityList();
		List<ClangtidyFile> warningSeverityList = clangtidyReport.getWarningSeverityList();

		List<ClangtidyFile> boostWarningList = clangtidyReport.getBoostWarningList();
		List<ClangtidyFile> certWarningList = clangtidyReport.getCertWarningList();
		List<ClangtidyFile> clangAnalyzerWarningList = clangtidyReport.getClangAnalyzerWarningList();
		List<ClangtidyFile> clangDiagnosticWarningList = clangtidyReport.getClangDiagnosticWarningList();
		List<ClangtidyFile> cppcoreguidelinesWarningList = clangtidyReport.getCppcoreguidelinesWarningList();
		List<ClangtidyFile> googleWarningList = clangtidyReport.getGoogleWarningList();
		List<ClangtidyFile> llvmWarningList = clangtidyReport.getLlvmWarningList();
		List<ClangtidyFile> miscWarningList = clangtidyReport.getMiscWarningList();
		List<ClangtidyFile> modernizeWarningList = clangtidyReport.getModernizeWarningList();
		List<ClangtidyFile> mpiWarningList = clangtidyReport.getMpiWarningList();
		List<ClangtidyFile> performanceWarningList = clangtidyReport.getPerformanceWarningList();
		List<ClangtidyFile> readabilityWarningList = clangtidyReport.getReadabilityWarningList();

		Assert.assertEquals("Wrong computing of list of errors according to severity classes", allErrors.size(),
				errorSeverityList.size() + warningSeverityList.size());
		Assert.assertEquals("Wrong computing of list of errors according to warning classes", allErrors.size(),
				boostWarningList.size() + certWarningList.size() + clangAnalyzerWarningList.size()
						+ clangDiagnosticWarningList.size() + cppcoreguidelinesWarningList.size()
						+ googleWarningList.size() + llvmWarningList.size() + miscWarningList.size()
						+ modernizeWarningList.size() + mpiWarningList.size() + performanceWarningList.size()
						+ readabilityWarningList.size());

		Assert.assertEquals("Wrong total number of errors", nbAllErrors, allErrors.size());
		Assert.assertEquals("Wrong total number of errors for the severity 'error'", nbErrorSeverity,
				errorSeverityList.size());
		Assert.assertEquals("Wrong total number of errors for the severity 'warning'", nbWarningSeverity,
				warningSeverityList.size());
		Assert.assertEquals("Wrong total number of errors for the warning 'boost'", nbBoostWarning,
				boostWarningList.size());
		Assert.assertEquals("Wrong total number of errors for the warning 'cert'", nbCertWarning,
				certWarningList.size());
		Assert.assertEquals("Wrong total number of errors for the warning 'clang-analyzer'", nbClangAnalyzerWarning,
				clangAnalyzerWarningList.size());
		Assert.assertEquals("Wrong total number of errors for the warning 'clang-diagnostic'", nbClangDiagnosticWarning,
				clangDiagnosticWarningList.size());
		Assert.assertEquals("Wrong total number of errors for the warning 'cppcoreguidelines'",
				nbCppcoreguidelinesWarning, cppcoreguidelinesWarningList.size());
		Assert.assertEquals("Wrong total number of errors for the warning 'google'", nbGoogleWarning,
				googleWarningList.size());
		Assert.assertEquals("Wrong total number of errors for the warning 'llvm'", nbLlvmWarning,
				llvmWarningList.size());
		Assert.assertEquals("Wrong total number of errors for the warning 'misc'", nbMiscWarning,
				miscWarningList.size());
		Assert.assertEquals("Wrong total number of errors for the warning 'modernize'", nbModernizeWarning,
				modernizeWarningList.size());
		Assert.assertEquals("Wrong total number of errors for the warning 'mpi'", nbMpiWarning, mpiWarningList.size());
		Assert.assertEquals("Wrong total number of errors for the warning 'performance'", nbPerformanceWarning,
				performanceWarningList.size());
		Assert.assertEquals("Wrong total number of errors for the warning 'readability'", nbReadabilityWarning,
				readabilityWarningList.size());
	}

	@Before
	public void setUp() throws Exception {
		clangtidyParser = new ClangtidyParser();
	}

	@Test
	public void testClangtidyCppcheckOutput() throws Exception {
		processClangtidy("testClangtidyParser_cppcheckOutput.xml", 67, 0, 67, 0, 2, 0, 0, 2, 38, 1, 0, 14, 0, 0, 10);
	}

	@Test
	public void testClangtidyRandom() throws Exception {
		processClangtidy("testClangtidyParser_random.xml", 16, 15, 1, 0, 0, 14, 0, 1, 0, 0, 0, 0, 1, 0, 0);
	}

}
