package org.jenkinsci.plugins.clangtidy.util;

import org.jenkinsci.plugins.clangtidy.config.ClangtidyConfigSeverityEvaluation;

/**
 * @author Gregory Boissinot
 */
public class ClangtidyBuildHealthEvaluator {
	public int evaluatBuildHealth(ClangtidyConfigSeverityEvaluation severityEvaluation, int nbErrorForSeverity) {
		if (severityEvaluation == null) {
			// no thresholds => no report
			return -1;
		}

		if (isHealthyReportEnabled(severityEvaluation)) {
			int percentage;

			int healthyNumber = ClangtidyMetricUtil.convert(severityEvaluation.getHealthy());
			int unHealthyNumber = ClangtidyMetricUtil.convert(severityEvaluation.getUnHealthy());

			if (nbErrorForSeverity < healthyNumber) {
				percentage = 100;
			} else if (nbErrorForSeverity > unHealthyNumber) {
				percentage = 0;
			} else {
				percentage = 100 - (((nbErrorForSeverity - healthyNumber) * 100) / (unHealthyNumber - healthyNumber));
			}

			return percentage;
		}
		return -1;
	}

	private boolean isHealthyReportEnabled(ClangtidyConfigSeverityEvaluation severityEvaluation) {
		if (ClangtidyMetricUtil.isValid(severityEvaluation.getHealthy())
				&& ClangtidyMetricUtil.isValid(severityEvaluation.getUnHealthy())) {
			int healthyNumber = ClangtidyMetricUtil.convert(severityEvaluation.getHealthy());
			int unHealthyNumber = ClangtidyMetricUtil.convert(severityEvaluation.getUnHealthy());
			return unHealthyNumber > healthyNumber;
		}
		return false;
	}
}
