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
import com.thalesgroup.hudson.plugins.clangtidy.model.ClangtidySourceContainer;
import com.thalesgroup.hudson.plugins.clangtidy.model.ClangtidyWorkspaceFile;
import hudson.model.AbstractBuild;
import hudson.model.Api;
import hudson.model.Item;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;
import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;

import java.io.IOException;
import java.io.Serializable;
import java.util.Map;

@ExportedBean
public class ClangtidyResult implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * The Clangtidy report
     */
    private ClangtidyReport report;

    /**
     * The Clangtidy container with all source files
     */
    private ClangtidySourceContainer clangtidySourceContainer;

    /**
     * The build owner
     */
    private AbstractBuild<?, ?> owner;

    public ClangtidyResult(ClangtidyReport report, ClangtidySourceContainer clangtidySourceContainer, AbstractBuild<?, ?> owner) {
        this.report = report;
        this.clangtidySourceContainer = clangtidySourceContainer;
        this.owner = owner;
    }

    /**
     * Gets the remote API for the build result.
     *
     * @return the remote API
     */
    public Api getApi() {
        return new Api(report);
    }

    @Exported
    public ClangtidyReport getReport() {
        return report;
    }

    public AbstractBuild<?, ?> getOwner() {
        return owner;
    }

    public ClangtidySourceContainer getClangtidySourceContainer() {
        return clangtidySourceContainer;
    }

    /**
     * Gets the dynamic result of the selection element.
     *
     * @param link     the link to identify the sub page to show
     * @param request  Stapler request
     * @param response Stapler response
     * @return the dynamic result of the analysis (detail page).
     * @throws java.io.IOException if an error occurs
     */
    public Object getDynamic(final String link, final StaplerRequest request, final StaplerResponse response) throws IOException {

        if (link.startsWith("source.")) {

            if (!owner.getProject().getACL().hasPermission(Item.WORKSPACE)) {
                response.sendRedirect2("nosourcepermission");
                return null;
            }

            Map<Integer, ClangtidyWorkspaceFile> agregateMap = clangtidySourceContainer.getInternalMap();
            if (agregateMap != null) {
                ClangtidyWorkspaceFile vClangtidyWorkspaceFile = agregateMap.get(Integer.parseInt(StringUtils.substringAfter(link, "source.")));
                if (vClangtidyWorkspaceFile == null) {
                    throw new IllegalArgumentException("Error for retrieving the source file with link:" + link);
                }
                return new ClangtidySource(owner, vClangtidyWorkspaceFile);
            }
        }
        return null;
    }


    /**
     * Renders the summary Clangtidy report for the build result.
     *
     * @return the HTML fragment of the summary Clangtidy report
     */
    public String getSummary() {
        return ClangtidySummary.createReportSummary(this);
    }

    /**
     * Renders the detailed summary Clangtidy report for the build result.
     *
     * @return the HTML fragment of the summary Clangtidy report
     */
    public String getDetails() {
        return ClangtidySummary.createReportSummaryDetails(this);
    }

    /**
     * Gets the previous Clangtidy result for the build result.
     *
     * @return the previous Clangtidy result
     */
    public ClangtidyResult getPreviousResult() {
        ClangtidyBuildAction previousAction = getPreviousAction();
        ClangtidyResult previousResult = null;
        if (previousAction != null) {
            previousResult = previousAction.getResult();
        }

        return previousResult;
    }

    /**
     * Gets the previous Action for the build result.
     *
     * @return the previous Clangtidy Build Action
     */
    private ClangtidyBuildAction getPreviousAction() {
        AbstractBuild<?, ?> previousBuild = owner.getPreviousBuild();
        if (previousBuild != null) {
            return previousBuild.getAction(ClangtidyBuildAction.class);
        }
        return null;
    }

    /**
     * Returns the number of new errors from the previous build result.
     *
     * @return the number of new errors
     */
    public int getNumberNewErrorsFromPreviousBuild() {
        ClangtidyResult previousClangtidyResult = getPreviousResult();
        if (previousClangtidyResult == null) {
            return 0;
        } else {
            int diff = this.report.getNumberTotal() - previousClangtidyResult.getReport().getNumberTotal();
            return (diff > 0) ? diff : 0;
        }
    }

    /**
     * Gets the number of errors according the selected severitiies form the configuration user object.
     *
     * @param cppecheckConfig the Clangtidy configuration object
     * @param checkNewError   true, if the request is for the number of new errors
     * @return the number of errors or new errors (if checkNewEroor is set to true) for the current configuration object
     * @throws java.io.IOException if an error occurs
     */
    public int getNumberErrorsAccordingConfiguration(ClangtidyConfig cppecheckConfig, boolean checkNewError) throws IOException {

        if (cppecheckConfig == null) {
            throw new IOException("[ERROR] - The clangtidy configuration file is missing. Could you save again your job configuration.");
        }

        int nbErrors = 0;
        int nbPreviousError = 0;
        ClangtidyResult previousResult = this.getPreviousResult();

        if (cppecheckConfig.getConfigSeverityEvaluation().isSeverityPossibleError()) {
            nbErrors = this.getReport().getPossibleErrorSeverities().size();
            if (previousResult != null) {
                nbPreviousError = previousResult.getReport().getPossibleErrorSeverities().size();
            }
        }

        if (cppecheckConfig.getConfigSeverityEvaluation().isSeverityStyle()) {
            nbErrors = nbErrors + this.getReport().getStyleSeverities().size();
            if (previousResult != null) {
                nbPreviousError = nbPreviousError + previousResult.getReport().getStyleSeverities().size();
            }

        }

        if (cppecheckConfig.getConfigSeverityEvaluation().isSeverityPossibleStyle()) {
            nbErrors = nbErrors + this.getReport().getPossibleStyleSeverities().size();
            if (previousResult != null) {
                nbPreviousError = nbPreviousError + previousResult.getReport().getPossibleStyleSeverities().size();
            }
        }

        if (cppecheckConfig.getConfigSeverityEvaluation().isSeverityError()) {
            nbErrors = nbErrors + this.getReport().getErrorSeverities().size();
            if (previousResult != null) {
                nbPreviousError = nbPreviousError + previousResult.getReport().getErrorSeverities().size();
            }
        }

        if (checkNewError) {
            if (previousResult != null) {
                return nbErrors - nbPreviousError;
            } else {
                return 0;
            }
        } else
            return nbErrors;
    }
}