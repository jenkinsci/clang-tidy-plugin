package org.jenkinsci.plugins.clangtidy.parser;

import com.thalesgroup.hudson.plugins.clangtidy.model.ClangtidyFile;
import hudson.model.BuildListener;
import org.jenkinsci.plugins.clangtidy.ClangtidyReport;
import org.jenkinsci.plugins.clangtidy.model.Errors;
import org.jenkinsci.plugins.clangtidy.model.Results;
import org.jenkinsci.plugins.clangtidy.util.ClangtidyLogger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Gregory Boissinot
 */
public class ClangtidyParser implements Serializable {

    private static final long serialVersionUID = 1L;

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
            jc.set(JAXBContext.newInstance(
                    org.jenkinsci.plugins.clangtidy.model.Error.class,
                    org.jenkinsci.plugins.clangtidy.model.Errors.class,
                    org.jenkinsci.plugins.clangtidy.model.Clangtidy.class,
                    org.jenkinsci.plugins.clangtidy.model.Results.class));
            Unmarshaller unmarshaller = jc.get().createUnmarshaller();
            org.jenkinsci.plugins.clangtidy.model.Results results = (org.jenkinsci.plugins.clangtidy.model.Results) unmarshaller.unmarshal(file);
            if (results.getClangtidy() == null) {
                throw new JAXBException("Test with versio 1");
            }
            report = getReportVersion2(results);
        } catch (JAXBException jxe) {
            try {
                jc.set(JAXBContext.newInstance(com.thalesgroup.jenkinsci.plugins.clangtidy.model.Error.class, com.thalesgroup.jenkinsci.plugins.clangtidy.model.Results.class));
                Unmarshaller unmarshaller = jc.get().createUnmarshaller();
                com.thalesgroup.jenkinsci.plugins.clangtidy.model.Results results = (com.thalesgroup.jenkinsci.plugins.clangtidy.model.Results) unmarshaller.unmarshal(file);
                report = getReportVersion1(results);

                ClangtidyLogger.log(listener, "WARNING: Legacy format of report file detected, "
                        + "please consider to pass additional argument '--xml-version=2' to Clangtidy. "
                        + "It often detects and reports more issues with the new format, "
                        + "so its usage is highly recommended.");
            } catch (JAXBException jxe1) {
                // Since Java 1.6
                // throw new IOException(jxe1);

                // Legacy constructor for compatibility with Java 1.5
                throw (IOException) new IOException(jxe1.toString()).initCause(jxe1);
            }
        }
        return report;
    }

    private ClangtidyReport getReportVersion1(com.thalesgroup.jenkinsci.plugins.clangtidy.model.Results results) {

        ClangtidyReport clangTidyReport = new ClangtidyReport();
        List<ClangtidyFile> allErrors = new ArrayList<ClangtidyFile>();
        List<ClangtidyFile> errorSeverityList = new ArrayList<ClangtidyFile>();
        List<ClangtidyFile> warningSeverityList = new ArrayList<ClangtidyFile>();
        List<ClangtidyFile> styleSeverityList = new ArrayList<ClangtidyFile>();
        List<ClangtidyFile> performanceSeverityList = new ArrayList<ClangtidyFile>();
        List<ClangtidyFile> informationSeverityList = new ArrayList<ClangtidyFile>();
        List<ClangtidyFile> noCategorySeverityList = new ArrayList<ClangtidyFile>();
        List<ClangtidyFile> portabilitySeverityList = new ArrayList<ClangtidyFile>();

        ClangtidyFile clangtidyFile;
        for (int i = 0; i < results.getError().size(); i++) {
            com.thalesgroup.jenkinsci.plugins.clangtidy.model.Error error = results.getError().get(i);
            clangtidyFile = new ClangtidyFile();

            clangtidyFile.setFileName(error.getFile());

            //line can be optional
            String lineAtr;
            if ((lineAtr = error.getLine()) != null) {
                clangtidyFile.setLineNumber(Integer.parseInt(lineAtr));
            }

            clangtidyFile.setClangTidyId(error.getId());
            clangtidyFile.setSeverity(error.getSeverity());
            clangtidyFile.setMessage(error.getMsg());

            if ("possible error".equals(clangtidyFile.getSeverity())) {
                warningSeverityList.add(clangtidyFile);
            } else if ("style".equals(clangtidyFile.getSeverity())) {
                styleSeverityList.add(clangtidyFile);
            } else if ("possible style".equals(clangtidyFile.getSeverity())) {
                performanceSeverityList.add(clangtidyFile);
            } else if ("error".equals(clangtidyFile.getSeverity())) {
                errorSeverityList.add(clangtidyFile);
            } else {
                noCategorySeverityList.add(clangtidyFile);
            }
            allErrors.add(clangtidyFile);
        }

        clangTidyReport.setAllErrors(allErrors);
        clangTidyReport.setErrorSeverityList(errorSeverityList);
        clangTidyReport.setInformationSeverityList(informationSeverityList);
        clangTidyReport.setNoCategorySeverityList(noCategorySeverityList);
        clangTidyReport.setPerformanceSeverityList(performanceSeverityList);
        clangTidyReport.setStyleSeverityList(styleSeverityList);
        clangTidyReport.setWarningSeverityList(warningSeverityList);
        clangTidyReport.setPortabilitySeverityList(portabilitySeverityList);


        return clangTidyReport;
    }

    private ClangtidyReport getReportVersion2(Results results) {

        ClangtidyReport clangTidyReport = new ClangtidyReport();
        List<ClangtidyFile> allErrors = new ArrayList<ClangtidyFile>();
        List<ClangtidyFile> errorSeverityList = new ArrayList<ClangtidyFile>();
        List<ClangtidyFile> warningSeverityList = new ArrayList<ClangtidyFile>();
        List<ClangtidyFile> styleSeverityList = new ArrayList<ClangtidyFile>();
        List<ClangtidyFile> performanceSeverityList = new ArrayList<ClangtidyFile>();
        List<ClangtidyFile> informationSeverityList = new ArrayList<ClangtidyFile>();
        List<ClangtidyFile> noCategorySeverityList = new ArrayList<ClangtidyFile>();
        List<ClangtidyFile> portabilitySeverityList = new ArrayList<ClangtidyFile>();

        ClangtidyFile clangtidyFile;

        Errors errors = results.getErrors();

        if (errors != null) {
            for (int i = 0; i < errors.getError().size(); i++) {
                org.jenkinsci.plugins.clangtidy.model.Error error = errors.getError().get(i);
                clangtidyFile = new ClangtidyFile();

                clangtidyFile.setClangTidyId(error.getId());
                clangtidyFile.setSeverity(error.getSeverity());
                clangtidyFile.setMessage(error.getMsg());
                clangtidyFile.setInconclusive((error.getInconclusive() != null)
                        ? error.getInconclusive() : false);

                // msg and verbose items have often the same text in XML report,
                // there is no need to store duplications
                if(error.getVerbose() != null
                        && !error.getMsg().equals(error.getVerbose())) {
                    clangtidyFile.setVerbose(error.getVerbose());
                }

                if ("warning".equals(clangtidyFile.getSeverity())) {
                    warningSeverityList.add(clangtidyFile);
                } else if ("style".equals(clangtidyFile.getSeverity())) {
                    styleSeverityList.add(clangtidyFile);
                } else if ("performance".equals(clangtidyFile.getSeverity())) {
                    performanceSeverityList.add(clangtidyFile);
                } else if ("error".equals(clangtidyFile.getSeverity())) {
                    errorSeverityList.add(clangtidyFile);
                } else if ("information".equals(clangtidyFile.getSeverity())) {
                    informationSeverityList.add(clangtidyFile);
                } else if ("portability".equals(clangtidyFile.getSeverity())) {
                    portabilitySeverityList.add(clangtidyFile);
                } else {
                    noCategorySeverityList.add(clangtidyFile);
                }
                allErrors.add(clangtidyFile);

                //FileName and Line
                org.jenkinsci.plugins.clangtidy.model.Error.Location location = error.getLocation();
                if (location != null) {
                    clangtidyFile.setFileName(location.getFile());
                    String lineAtr;
                    if ((lineAtr = location.getLine()) != null) {
                        clangtidyFile.setLineNumber(Integer.parseInt(lineAtr));
                    }
                }
            }
        }

        clangTidyReport.setAllErrors(allErrors);
        clangTidyReport.setErrorSeverityList(errorSeverityList);
        clangTidyReport.setInformationSeverityList(informationSeverityList);
        clangTidyReport.setNoCategorySeverityList(noCategorySeverityList);
        clangTidyReport.setPerformanceSeverityList(performanceSeverityList);
        clangTidyReport.setStyleSeverityList(styleSeverityList);
        clangTidyReport.setWarningSeverityList(warningSeverityList);
        clangTidyReport.setPortabilitySeverityList(portabilitySeverityList);

        if (results.getClangtidy() != null) {
            clangTidyReport.setVersion(results.getClangtidy().getVersion());
        }

        return clangTidyReport;
    }
}

