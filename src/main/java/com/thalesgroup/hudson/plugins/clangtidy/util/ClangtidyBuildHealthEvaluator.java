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

package com.thalesgroup.hudson.plugins.clangtidy.util;

import org.jenkinsci.plugins.clangtidy.Messages;

import com.thalesgroup.hudson.plugins.clangtidy.ClangtidyMetricUtil;
import com.thalesgroup.hudson.plugins.clangtidy.config.ClangtidyConfig;

import hudson.model.HealthReport;

public class ClangtidyBuildHealthEvaluator {

    public HealthReport evaluatBuildHealth(ClangtidyConfig clangtidyConfig, int nbErrorForSeverity) {

        if (clangtidyConfig == null) {
            // no thresholds => no report
            return null;
        }

        if (isHealthyReportEnabled(clangtidyConfig)) {
            int percentage;

            if (nbErrorForSeverity < ClangtidyMetricUtil.convert(clangtidyConfig.getConfigSeverityEvaluation().getHealthy())) {
                percentage = 100;
            } else if (nbErrorForSeverity > ClangtidyMetricUtil.convert(clangtidyConfig.getConfigSeverityEvaluation().getUnHealthy())) {
                percentage = 0;
            } else {
                percentage = 100 - ((nbErrorForSeverity - ClangtidyMetricUtil.convert(clangtidyConfig.getConfigSeverityEvaluation().getHealthy())) * 100
                        / (ClangtidyMetricUtil.convert(clangtidyConfig.getConfigSeverityEvaluation().getUnHealthy()) - ClangtidyMetricUtil.convert(clangtidyConfig.getConfigSeverityEvaluation().getHealthy())));
            }

            return new HealthReport(percentage, Messages.clangtidy_BuildHealthEvaluatorDescription(ClangtidyMetricUtil.getMessageSelectedSeverties(clangtidyConfig)));
        }
        return null;
    }


    private boolean isHealthyReportEnabled(ClangtidyConfig clangtidyconfig) {
        if (ClangtidyMetricUtil.isValid(clangtidyconfig.getConfigSeverityEvaluation().getHealthy()) && ClangtidyMetricUtil.isValid(clangtidyconfig.getConfigSeverityEvaluation().getUnHealthy())) {
            int healthyNumber = ClangtidyMetricUtil.convert(clangtidyconfig.getConfigSeverityEvaluation().getHealthy());
            int unHealthyNumber = ClangtidyMetricUtil.convert(clangtidyconfig.getConfigSeverityEvaluation().getUnHealthy());
            return unHealthyNumber > healthyNumber;
        }
        return false;
    }
}
