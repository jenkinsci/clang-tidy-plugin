package org.jenkinsci.plugins.clangtidy;


import java.io.IOException;

import com.thalesgroup.hudson.plugins.clangtidy.util.AbstractClangtidyBuildAction;

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

    private ClangtidyResult result;

    /** 
     * The health report percentage.
     * 
     * @since 1.15
     */
    private int healthReportPercentage;

    public ClangtidyBuildAction(AbstractBuild<?, ?> owner, ClangtidyResult result,
            int healthReportPercentage) {
        super(owner);
        this.result = result;
        this.healthReportPercentage = healthReportPercentage;
    }

    public String getIconFileName() {
        return "/plugin/clangtidy/icons/clangtidy-24.png";
    }

    public String getDisplayName() {
        return Messages.clangtidy_ClangtidyResults();
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
        if(healthReportPercentage >= 0 && healthReportPercentage <= 100) {
            return new HealthReport(healthReportPercentage,
                    Messages._clangtidy_BuildStability());
        } else {
            return null;
        }
    }

    public static int computeHealthReportPercentage(ClangtidyResult result,
            ClangtidyConfigSeverityEvaluation severityEvaluation) {
        try {
            return new ClangtidyBuildHealthEvaluator().evaluatBuildHealth(severityEvaluation,
                    result.getNumberErrorsAccordingConfiguration(severityEvaluation,
                            false));
        } catch (IOException e) {
            return -1;
        }
    }

    // Backward compatibility
    @Deprecated
    private transient AbstractBuild<?, ?> build;

    /** Backward compatibility with version 1.14 and less. */
    @Deprecated
    private transient ClangtidyConfig clangtidyConfig;

    /**
     * Initializes members that were not present in previous versions of this plug-in.
     *
     * @return the created object
     */
    private Object readResolve() {
        if (build != null) {
            this.owner = build;
        }

        // Backward compatibility with version 1.14 and less
        if (clangtidyConfig != null) {
            healthReportPercentage = 100;
        }

        return this;
    }
}
