/*******************************************************************************
 * Copyright (c) 2009 Thales Corporate Services SAS                             *
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

import com.thalesgroup.hudson.plugins.clangtidy.config.ClangtidyConfig;
import com.thalesgroup.hudson.plugins.clangtidy.config.ClangtidyConfigGraph;
import com.thalesgroup.hudson.plugins.clangtidy.graph.ClangtidyGraph;
import com.thalesgroup.hudson.plugins.clangtidy.model.ClangtidyFile;
import com.thalesgroup.hudson.plugins.clangtidy.model.ClangtidySourceContainer;
import com.thalesgroup.hudson.plugins.clangtidy.util.AbstractClangtidyBuildAction;
import com.thalesgroup.hudson.plugins.clangtidy.util.ClangtidyBuildHealthEvaluator;
import hudson.model.AbstractBuild;
import hudson.model.HealthReport;
import hudson.util.ChartUtil;
import hudson.util.ChartUtil.NumberOnlyBuildLabel;
import hudson.util.DataSetBuilder;
import hudson.util.Graph;
import org.jenkinsci.plugins.clangtidy.config.ClangtidyConfigSeverityEvaluation;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;


public class ClangtidyBuildAction extends AbstractClangtidyBuildAction {

    public static final String URL_NAME = "clangtidyResult";

    private ClangtidyResult result;
    private ClangtidyConfig clangtidyConfig;

    public ClangtidyBuildAction(AbstractBuild<?, ?> owner, ClangtidyResult result, ClangtidyConfig clangtidyConfig) {
        super(owner);
        this.result = result;
        this.clangtidyConfig = clangtidyConfig;
    }

    public String getIconFileName() {
        return "/plugin/clangtidy/icons/clangtidy-24.png";
    }

    public String getDisplayName() {
        return "Clangtidy Result";
    }

    public String getUrlName() {
        return URL_NAME;
    }

    public String getSearchUrl() {
        return getUrlName();
    }

    public ClangtidyResult getResult() {
        return this.result;
    }

    AbstractBuild<?, ?> getBuild() {
        return this.owner;
    }

    public Object getTarget() {
        return this.result;
    }

    public HealthReport getBuildHealth() {
        try {
            return new ClangtidyBuildHealthEvaluator().evaluatBuildHealth(clangtidyConfig, result.getNumberErrorsAccordingConfiguration(clangtidyConfig, false));
        } catch (IOException ioe) {
            return new HealthReport();
        }
    }

    private DataSetBuilder<String, NumberOnlyBuildLabel> getDataSetBuilder() {
        DataSetBuilder<String, ChartUtil.NumberOnlyBuildLabel> dsb = new DataSetBuilder<String, ChartUtil.NumberOnlyBuildLabel>();

        for (ClangtidyBuildAction a = this; a != null; a = a.getPreviousResult()) {
            ChartUtil.NumberOnlyBuildLabel label = new ChartUtil.NumberOnlyBuildLabel(a.owner);

            //a.getResult().getOwner().getResult()

            ClangtidyReport report = a.getResult().getReport();

            ClangtidyConfigGraph configGraph = clangtidyConfig.getConfigGraph();

            if (configGraph.isDisplaySeverityStyle())
                dsb.add(report.getNumberSeverityStyle(), "Severity 'style'", label);
            if (configGraph.isDisplaySeverityPossibleStyle())
                dsb.add(report.getNumberSeverityPossibleStyle(), "Severity 'possibe style'", label);
            if (configGraph.isDisplaySeverityPossibleError())
                dsb.add(report.getNumberSeverityPossibleError(), "Severity 'possible error'", label);
            if (configGraph.isDisplaySeverityError())
                dsb.add(report.getNumberSeverityError(), "Severity 'error'", label);
            if (configGraph.isDiplayAllError())
                dsb.add(report.getNumberTotal(), "All errors", label);

        }
        return dsb;
    }

    public void doGraph(StaplerRequest req, StaplerResponse rsp) throws IOException {
        if (ChartUtil.awtProblemCause != null) {
            rsp.sendRedirect2(req.getContextPath() + "/images/headless.png");
            return;
        }

        Calendar timestamp = getBuild().getTimestamp();

        if (req.checkIfModified(timestamp, rsp)) return;

        Graph g = new ClangtidyGraph(getOwner(), getDataSetBuilder().build(),
                "Number of error", clangtidyConfig.getConfigGraph().getXSize(), clangtidyConfig.getConfigGraph().getYSize());
        g.doPng(req, rsp);
    }

    // Backward compatibility. Do not remove.
    // CPPCHECK:OFF
    @Deprecated
    private transient AbstractBuild<?, ?> build;

    /**
     * Initializes members that were not present in previous versions of this plug-in.
     *
     * @return the created object
     */
    @SuppressWarnings("deprecation")
    private Object readResolve() {
        if (build != null) {
            this.owner = build;
        }

        //Report
        ClangtidyReport report = result.getReport();
        org.jenkinsci.plugins.clangtidy.ClangtidyReport newReport = new org.jenkinsci.plugins.clangtidy.ClangtidyReport();
        if (report != null) {
            newReport.setAllErrors(report.getEverySeverities());
            newReport.setErrorSeverityList(report.getErrorSeverities());
            newReport.setWarningSeverityList(report.getPossibleErrorSeverities());
            newReport.setStyleSeverityList(report.getStyleSeverities());
            newReport.setPerformanceSeverityList(report.getPossibleStyleSeverities());
            newReport.setInformationSeverityList(report.getNoCategorySeverities());
            newReport.setNoCategorySeverityList(new ArrayList<ClangtidyFile>());
            newReport.setPortabilitySeverityList(new ArrayList<ClangtidyFile>());
        }

        //Result
        ClangtidySourceContainer sourceContainer = result.getClangtidySourceContainer();
        org.jenkinsci.plugins.clangtidy.ClangtidySourceContainer newSourceContainer = new org.jenkinsci.plugins.clangtidy.ClangtidySourceContainer(sourceContainer.getInternalMap());
        org.jenkinsci.plugins.clangtidy.ClangtidyResult newResult = new org.jenkinsci.plugins.clangtidy.ClangtidyResult(newReport, newSourceContainer, getOwner());

        //Config
        org.jenkinsci.plugins.clangtidy.config.ClangtidyConfig newConfig = new org.jenkinsci.plugins.clangtidy.config.ClangtidyConfig();

        newConfig.setPattern(clangtidyConfig.getClangtidyReportPattern());
        newConfig.setIgnoreBlankFiles(clangtidyConfig.isIgnoreBlankFiles());
        ClangtidyConfigSeverityEvaluation configSeverityEvaluation = new ClangtidyConfigSeverityEvaluation(
                clangtidyConfig.getConfigSeverityEvaluation().getThreshold(),
                clangtidyConfig.getConfigSeverityEvaluation().getNewThreshold(),
                clangtidyConfig.getConfigSeverityEvaluation().getFailureThreshold(),
                clangtidyConfig.getConfigSeverityEvaluation().getNewFailureThreshold(),
                clangtidyConfig.getConfigSeverityEvaluation().getHealthy(),
                clangtidyConfig.getConfigSeverityEvaluation().getUnHealthy(),
                clangtidyConfig.getConfigSeverityEvaluation().isSeverityError(),
                clangtidyConfig.getConfigSeverityEvaluation().isSeverityPossibleError(),
                clangtidyConfig.getConfigSeverityEvaluation().isSeverityStyle(),
                clangtidyConfig.getConfigSeverityEvaluation().isSeverityPossibleStyle(),
                true, true, true);
        newConfig.setConfigSeverityEvaluation(configSeverityEvaluation);
        org.jenkinsci.plugins.clangtidy.config.ClangtidyConfigGraph configGraph = new org.jenkinsci.plugins.clangtidy.config.ClangtidyConfigGraph(
                clangtidyConfig.getConfigGraph().getXSize(),
                clangtidyConfig.getConfigGraph().getYSize(),
                0,
                clangtidyConfig.getConfigGraph().isDiplayAllError(),
                clangtidyConfig.getConfigGraph().isDisplaySeverityError(),
                clangtidyConfig.getConfigGraph().isDisplaySeverityPossibleError(),
                clangtidyConfig.getConfigGraph().isDisplaySeverityStyle(),
                clangtidyConfig.getConfigGraph().isDisplaySeverityPossibleStyle(),
                true, true, true);
        newConfig.setConfigGraph(configGraph);


        return new org.jenkinsci.plugins.clangtidy.ClangtidyBuildAction(owner,
                newResult, 100);
    }
}
