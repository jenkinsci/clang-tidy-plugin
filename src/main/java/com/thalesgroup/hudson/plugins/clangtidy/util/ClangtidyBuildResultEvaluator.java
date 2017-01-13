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

import com.thalesgroup.hudson.plugins.clangtidy.ClangtidyMetricUtil;
import com.thalesgroup.hudson.plugins.clangtidy.config.ClangtidyConfig;
import hudson.model.BuildListener;
import hudson.model.Result;

public class ClangtidyBuildResultEvaluator {

    private boolean isErrorCountExceeded(final int errorCount, final String errorThreshold) {
        if (errorCount > 0 && ClangtidyMetricUtil.isValid(errorThreshold)) {
            return errorCount > ClangtidyMetricUtil.convert(errorThreshold);
        }
        return false;
    }

    public Result evaluateBuildResult(
            final BuildListener listener,
            int errorsCount,
            int newErrorsCount,
            ClangtidyConfig clangtidyConfig) {

        if (isErrorCountExceeded(errorsCount, clangtidyConfig.getConfigSeverityEvaluation().getFailureThreshold())) {
            ClangtidyLogger.log(listener, "Setting build status to FAILURE since total number of errors ("
                    + ClangtidyMetricUtil.getMessageSelectedSeverties(clangtidyConfig)
                    + ") exceeds the threshold value ;" + clangtidyConfig.getConfigSeverityEvaluation().getFailureThreshold() + "'.");
            return Result.FAILURE;
        }
        if (isErrorCountExceeded(newErrorsCount, clangtidyConfig.getConfigSeverityEvaluation().getNewFailureThreshold())) {
            ClangtidyLogger.log(listener, "Setting build status to FAILURE since total number of new errors ("
                    + ClangtidyMetricUtil.getMessageSelectedSeverties(clangtidyConfig)
                    + ") exceeds the threshold value '" + clangtidyConfig.getConfigSeverityEvaluation().getNewFailureThreshold() + "'.");
            return Result.FAILURE;
        }
        if (isErrorCountExceeded(errorsCount, clangtidyConfig.getConfigSeverityEvaluation().getThreshold())) {
            ClangtidyLogger.log(listener, "Setting build status to UNSTABLE since total number of errors ("
                    + ClangtidyMetricUtil.getMessageSelectedSeverties(clangtidyConfig)
                    + ") exceeds the threshold value '" + clangtidyConfig.getConfigSeverityEvaluation().getThreshold() + "'.");
            return Result.UNSTABLE;
        }
        if (isErrorCountExceeded(newErrorsCount, clangtidyConfig.getConfigSeverityEvaluation().getNewThreshold())) {
            ClangtidyLogger.log(listener, "Setting build status to UNSTABLE since total number of new errors ("
                    + ClangtidyMetricUtil.getMessageSelectedSeverties(clangtidyConfig)
                    + ") exceeds the threshold value '" + clangtidyConfig.getConfigSeverityEvaluation().getNewThreshold() + "'.");
            return Result.UNSTABLE;
        }

        ClangtidyLogger.log(listener, "Not changing build status, since no threshold has been exceeded");
        return Result.SUCCESS;
    }

}

