package org.jenkinsci.plugins.clangtidy.util;


import hudson.model.BuildListener;
import hudson.model.Result;

import org.jenkinsci.plugins.clangtidy.config.ClangtidyConfigSeverityEvaluation;

/**
 * @author Gregory Boissinot
 * @author Mickael Germain
 */
public class ClangtidyBuildResultEvaluator {
    public Result evaluateBuildResult(
            final BuildListener listener,
            int errorsCount,
            int newErrorsCount,
            ClangtidyConfigSeverityEvaluation severityEvaluation) {

        if (isErrorCountExceeded(errorsCount, severityEvaluation.getFailureThreshold())) {
            ClangtidyLogger.log(listener,
                    "Setting build status to FAILURE since total number of issues '"
                            + errorsCount + "' exceeds the threshold value '"
                            + severityEvaluation.getFailureThreshold() + "'.");
            return Result.FAILURE;
        }
        if (isErrorCountExceeded(newErrorsCount, severityEvaluation.getNewFailureThreshold())) {
            ClangtidyLogger.log(listener,
                    "Setting build status to FAILURE since number of new issues '"
                            + newErrorsCount + "' exceeds the threshold value '"
                            + severityEvaluation.getNewFailureThreshold() + "'.");
            return Result.FAILURE;
        }
        if (isErrorCountExceeded(errorsCount, severityEvaluation.getThreshold())) {
            ClangtidyLogger.log(listener,
                    "Setting build status to UNSTABLE since total number of issues '"
                            + errorsCount + "' exceeds the threshold value '"
                            + severityEvaluation.getThreshold() + "'.");
            return Result.UNSTABLE;
        }
        if (isErrorCountExceeded(newErrorsCount, severityEvaluation.getNewThreshold())) {
            ClangtidyLogger.log(listener,
                    "Setting build status to UNSTABLE since number of new issues '"
                            + newErrorsCount + "' exceeds the threshold value '"
                            + severityEvaluation.getNewThreshold() + "'.");
            return Result.UNSTABLE;
        }

        ClangtidyLogger.log(listener,
                "Not changing build status, since no threshold has been exceeded.");
        return Result.SUCCESS;
    }

    private boolean isErrorCountExceeded(final int errorCount, final String errorThreshold) {
        if (errorCount > 0 && ClangtidyMetricUtil.isValid(errorThreshold)) {
            return errorCount > ClangtidyMetricUtil.convert(errorThreshold);
        }
        return false;
    }
}
