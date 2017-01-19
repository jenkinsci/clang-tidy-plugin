package org.jenkinsci.plugins.clangtidy.parser;

import org.jenkinsci.plugins.clangtidy.model.ClangtidyFile;
import hudson.model.BuildListener;
import org.jenkinsci.plugins.clangtidy.ClangtidyReport;
import org.jenkinsci.plugins.clangtidy.model.Errors;
import org.jenkinsci.plugins.clangtidy.model.Results;
import org.jenkinsci.plugins.clangtidy.util.ClangtidyLogger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Gregory Boissinot
 * @author Mickael Germain
 */
public class ClangtidyParser implements Serializable {

	private static final long serialVersionUID = 1L;

	private ClangtidyReport getReport(Results results, BuildListener listener) {

		ClangtidyReport clangTidyReport = new ClangtidyReport();
		List<ClangtidyFile> allErrors = new ArrayList<ClangtidyFile>();
		List<ClangtidyFile> errorSeverityList = new ArrayList<ClangtidyFile>();
		List<ClangtidyFile> warningSeverityList = new ArrayList<ClangtidyFile>();
		List<ClangtidyFile> boostWarningList = new ArrayList<ClangtidyFile>();
		List<ClangtidyFile> certWarningList = new ArrayList<ClangtidyFile>();
		List<ClangtidyFile> cppcoreguidelinesWarningList = new ArrayList<ClangtidyFile>();
		List<ClangtidyFile> clangAnalyzerWarningList = new ArrayList<ClangtidyFile>();
		List<ClangtidyFile> clangDiagnosticWarningList = new ArrayList<ClangtidyFile>();
		List<ClangtidyFile> googleWarningList = new ArrayList<ClangtidyFile>();
		List<ClangtidyFile> llvmWarningList = new ArrayList<ClangtidyFile>();
		List<ClangtidyFile> miscWarningList = new ArrayList<ClangtidyFile>();
		List<ClangtidyFile> modernizeWarningList = new ArrayList<ClangtidyFile>();
		List<ClangtidyFile> mpiWarningList = new ArrayList<ClangtidyFile>();
		List<ClangtidyFile> performanceWarningList = new ArrayList<ClangtidyFile>();
		List<ClangtidyFile> readabilityWarningList = new ArrayList<ClangtidyFile>();

		ClangtidyFile clangtidyFile;

		Errors errors = results.getErrors();

		if (errors != null) {
			for (int i = 0; i < errors.getError().size(); i++) {
				org.jenkinsci.plugins.clangtidy.model.Error error = errors.getError().get(i);
				clangtidyFile = new ClangtidyFile();

				clangtidyFile.setType(error.getType());
				clangtidyFile.setId(error.getId());
				clangtidyFile.setSeverity(error.getSeverity());
				clangtidyFile.setMessage(error.getMessage());

				// TODO Use switch on 1.7
				String severity = clangtidyFile.getSeverity();
				if ("warning".equals(severity)) {
					warningSeverityList.add(clangtidyFile);
				} else if ("error".equals(severity)) {
					errorSeverityList.add(clangtidyFile);
				} else {
					ClangtidyLogger.log(listener,
							"WARNING: Clang tidy report contain unknown warning severity " + severity + ".");
				}

				// TODO Use switch on 1.7
				String type = clangtidyFile.getType();
				if ("boost".equals(type)) {
					boostWarningList.add(clangtidyFile);
				} else if ("cert".equals(type)) {
					certWarningList.add(clangtidyFile);
				} else if ("cppcoreguidelines".equals(type)) {
					cppcoreguidelinesWarningList.add(clangtidyFile);
				} else if ("clang-analyzer".equals(type)) {
					clangAnalyzerWarningList.add(clangtidyFile);
				} else if ("clang-diagnostic".equals(type)) {
					clangDiagnosticWarningList.add(clangtidyFile);
				} else if ("google".equals(type)) {
					googleWarningList.add(clangtidyFile);
				} else if ("llvm".equals(type)) {
					llvmWarningList.add(clangtidyFile);
				} else if ("misc".equals(type)) {
					miscWarningList.add(clangtidyFile);
				} else if ("modernize".equals(type)) {
					modernizeWarningList.add(clangtidyFile);
				} else if ("mpi".equals(type)) {
					mpiWarningList.add(clangtidyFile);
				} else if ("performance".equals(type)) {
					performanceWarningList.add(clangtidyFile);
				} else if ("readability".equals(type)) {
					readabilityWarningList.add(clangtidyFile);
				} else {
					ClangtidyLogger.log(listener,
							"WARNING: Clang tidy report contain unknown warning type " + type + ".");
				}

				allErrors.add(clangtidyFile);

				// FileName and Line
				org.jenkinsci.plugins.clangtidy.model.Error.Location location = error.getLocation();
				if (location != null) {
					clangtidyFile.setFileName(location.getFile());
					String lineAtr;
					if ((lineAtr = location.getLine()) != null) {
						clangtidyFile.setLineNumber(Integer.parseInt(lineAtr));
					}
					String columnAtr;
					if ((columnAtr = location.getColumn()) != null) {
						clangtidyFile.setColumnNumber(Integer.parseInt(columnAtr));
					}
				}
			}
		}

		clangTidyReport.setAllErrors(allErrors);

		clangTidyReport.setErrorSeverityList(errorSeverityList);
		clangTidyReport.setWarningSeverityList(warningSeverityList);

		clangTidyReport.setBoostWarningList(boostWarningList);
		clangTidyReport.setCertWarningList(certWarningList);
		clangTidyReport.setCppcoreguidelinesWarningList(cppcoreguidelinesWarningList);
		clangTidyReport.setClangAnalyzerWarningList(clangAnalyzerWarningList);
		clangTidyReport.setClangDiagnosticWarningList(clangDiagnosticWarningList);
		clangTidyReport.setGoogleWarningList(googleWarningList);
		clangTidyReport.setLlvmWarningList(llvmWarningList);
		clangTidyReport.setMiscWarningList(miscWarningList);
		clangTidyReport.setModernizeWarningList(modernizeWarningList);
		clangTidyReport.setMpiWarningList(mpiWarningList);
		clangTidyReport.setPerformanceWarningList(performanceWarningList);
		clangTidyReport.setReadabilityWarningList(readabilityWarningList);

		if (results.getClangtidy() != null) {
			clangTidyReport.setVersion(results.getClangtidy().getVersion());
		}

		return clangTidyReport;
	}

	public ClangtidyReport parse(final File file, BuildListener listener) throws IOException {

		if (file == null) {
			throw new IllegalArgumentException("File input is mandatory.");
		}

		if (!file.exists()) {
			throw new IllegalArgumentException("File input " + file.getName() + " must exist.");
		}

		ClangtidyReport report;
		AtomicReference<JAXBContext> jc = new AtomicReference<JAXBContext>();
		try {
			jc.set(JAXBContext.newInstance(org.jenkinsci.plugins.clangtidy.model.Error.class,
					org.jenkinsci.plugins.clangtidy.model.Errors.class,
					org.jenkinsci.plugins.clangtidy.model.Clangtidy.class,
					org.jenkinsci.plugins.clangtidy.model.Results.class));
			Unmarshaller unmarshaller = jc.get().createUnmarshaller();
			FileInputStream stream = new FileInputStream(file);
			org.jenkinsci.plugins.clangtidy.model.Results results = (org.jenkinsci.plugins.clangtidy.model.Results) unmarshaller.unmarshal(stream);
			if (results.getClangtidy() == null) {

				throw new JAXBException("Clang tidy not Found");
			}
			report = getReport(results, listener);
		} catch (JAXBException jxe) {
			ClangtidyLogger.log(listener, "WARNING: Bad report file detected.");
			throw (IOException) new IOException(jxe.toString()).initCause(jxe);
		}
		return report;
	}
}
