package org.jenkinsci.plugins.clangtidy;

import java.io.IOException;

import org.jenkinsci.plugins.clangtidy.util.AbstractClangtidyBuildAction;

import hudson.model.AbstractBuild;
import hudson.model.HealthReport;

import org.jenkinsci.plugins.clangtidy.config.ClangtidyConfig;
import org.jenkinsci.plugins.clangtidy.config.ClangtidyConfigSeverityEvaluation;
import org.jenkinsci.plugins.clangtidy.util.ClangtidyBuildHealthEvaluator;

/**
 * @author Gregory Boissinot
 * @author Mickael Germain
 */
public class ClangtidyBuildAction extends AbstractClangtidyBuildAction {

	public static final String URL_NAME = "clangtidyResult";

	public static int computeHealthReportPercentage(ClangtidyResult result,
			ClangtidyConfigSeverityEvaluation severityEvaluation) {
		try {
			return new ClangtidyBuildHealthEvaluator().evaluatBuildHealth(severityEvaluation,
					result.getNumberErrorsAccordingConfiguration(severityEvaluation, false));
		} catch (IOException e) {
			return -1;
		}
	}

	private ClangtidyResult result;

	/**
	 * The health report percentage.
	 *
	 * @since 1.15
	 */
	private int healthReportPercentage;

	public ClangtidyBuildAction(AbstractBuild<?, ?> owner, ClangtidyResult result, int healthReportPercentage) {
		super(owner);
		this.result = result;
		this.healthReportPercentage = healthReportPercentage;
	}

	AbstractBuild<?, ?> getBuild() {
		return owner;
	}

	@Override
	public HealthReport getBuildHealth() {
		if ((healthReportPercentage >= 0) && (healthReportPercentage <= 100)) {
			return new HealthReport(healthReportPercentage, Messages._clangtidy_BuildStability());
		} else {
			return null;
		}
	}

	@Override
	public String getDisplayName() {
		return Messages.clangtidy_ClangtidyResults();
	}

	@Override
	public String getIconFileName() {
		return "/plugin/clangtidy/icons/clangtidy-24.png";
	}

	public ClangtidyResult getResult() {
		return result;
	}

	@Override
	public String getSearchUrl() {
		return getUrlName();
	}

	@Override
	public Object getTarget() {
		return result;
	}

	@Override
	public String getUrlName() {
		return URL_NAME;
	}

}
