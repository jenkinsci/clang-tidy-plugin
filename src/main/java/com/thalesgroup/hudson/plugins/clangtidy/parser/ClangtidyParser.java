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

package com.thalesgroup.hudson.plugins.clangtidy.parser;

import com.thalesgroup.hudson.plugins.clangtidy.ClangtidyReport;
import com.thalesgroup.hudson.plugins.clangtidy.model.ClangtidyFile;
import org.jenkinsci.plugins.clangtidy.model.Errors;
import org.jenkinsci.plugins.clangtidy.model.Results;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class ClangtidyParser implements Serializable {

    private static final long serialVersionUID = 1L;

    public ClangtidyReport parse(final File file) throws IOException {

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
            } catch (JAXBException jxe1) {
                throw new IOException(jxe1);
            }

        }
        return report;
    }

    private ClangtidyReport getReportVersion1(com.thalesgroup.jenkinsci.plugins.clangtidy.model.Results results) {

        ClangtidyReport clangTidyReport = new ClangtidyReport();
        List<ClangtidyFile> everyErrors = new ArrayList<ClangtidyFile>();
        List<ClangtidyFile> styleSeverities = new ArrayList<ClangtidyFile>();
        List<ClangtidyFile> possibleStyleSeverities = new ArrayList<ClangtidyFile>();
        List<ClangtidyFile> errorSeverities = new ArrayList<ClangtidyFile>();
        List<ClangtidyFile> possibleErrorSeverities = new ArrayList<ClangtidyFile>();
        List<ClangtidyFile> noCategorySeverities = new ArrayList<ClangtidyFile>();

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
                possibleErrorSeverities.add(clangtidyFile);
            } else if ("style".equals(clangtidyFile.getSeverity())) {
                styleSeverities.add(clangtidyFile);
            } else if ("possible style".equals(clangtidyFile.getSeverity())) {
                possibleStyleSeverities.add(clangtidyFile);
            } else if ("error".equals(clangtidyFile.getSeverity())) {
                errorSeverities.add(clangtidyFile);
            } else {
                noCategorySeverities.add(clangtidyFile);
            }
            everyErrors.add(clangtidyFile);
        }

        clangTidyReport.setEverySeverities(everyErrors);
        clangTidyReport.setPossibleErrorSeverities(possibleErrorSeverities);
        clangTidyReport.setStyleSeverities(styleSeverities);
        clangTidyReport.setPossibleStyleSeverities(possibleStyleSeverities);
        clangTidyReport.setErrorSeverities(errorSeverities);
        clangTidyReport.setNoCategorySeverities(noCategorySeverities);

        return clangTidyReport;
    }

    private ClangtidyReport getReportVersion2(Results results) {

        ClangtidyReport clangTidyReport = new ClangtidyReport();
        List<ClangtidyFile> everyErrors = new ArrayList<ClangtidyFile>();
        List<ClangtidyFile> styleSeverities = new ArrayList<ClangtidyFile>();
        List<ClangtidyFile> possibleStyleSeverities = new ArrayList<ClangtidyFile>();
        List<ClangtidyFile> errorSeverities = new ArrayList<ClangtidyFile>();
        List<ClangtidyFile> possibleErrorSeverities = new ArrayList<ClangtidyFile>();
        List<ClangtidyFile> noCategorySeverities = new ArrayList<ClangtidyFile>();

        ClangtidyFile clangtidyFile;

        Errors errors = results.getErrors();

        if (errors != null) {
            for (int i = 0; i < errors.getError().size(); i++) {
                org.jenkinsci.plugins.clangtidy.model.Error error = errors.getError().get(i);
                clangtidyFile = new ClangtidyFile();

                //FileName and Line
                org.jenkinsci.plugins.clangtidy.model.Error.Location location = error.getLocation();
                if (location != null) {
                    clangtidyFile.setFileName(location.getFile());
                    String lineAtr;
                    if ((lineAtr = location.getLine()) != null) {
                        clangtidyFile.setLineNumber(Integer.parseInt(lineAtr));
                    }
                }

                clangtidyFile.setClangTidyId(error.getId());
                clangtidyFile.setSeverity(error.getSeverity());
                clangtidyFile.setMessage(error.getMsg());

                if ("possible error".equals(clangtidyFile.getSeverity())) {
                    possibleErrorSeverities.add(clangtidyFile);
                } else if ("warning".equals(clangtidyFile.getSeverity())) {
                    possibleErrorSeverities.add(clangtidyFile);
                } else if ("style".equals(clangtidyFile.getSeverity())) {
                    styleSeverities.add(clangtidyFile);
                } else if ("possible style".equals(clangtidyFile.getSeverity())) {
                    possibleStyleSeverities.add(clangtidyFile);
                } else if ("information".equals(clangtidyFile.getSeverity())) {
                    possibleStyleSeverities.add(clangtidyFile);
                } else if ("error".equals(clangtidyFile.getSeverity())) {
                    errorSeverities.add(clangtidyFile);
                } else {
                    noCategorySeverities.add(clangtidyFile);
                }
                everyErrors.add(clangtidyFile);
            }
        }

        clangTidyReport.setEverySeverities(everyErrors);
        clangTidyReport.setPossibleErrorSeverities(possibleErrorSeverities);
        clangTidyReport.setStyleSeverities(styleSeverities);
        clangTidyReport.setPossibleStyleSeverities(possibleStyleSeverities);
        clangTidyReport.setErrorSeverities(errorSeverities);
        clangTidyReport.setNoCategorySeverities(noCategorySeverities);
        return clangTidyReport;
    }


}
