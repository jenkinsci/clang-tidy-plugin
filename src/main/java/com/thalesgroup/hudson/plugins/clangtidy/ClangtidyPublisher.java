package com.thalesgroup.hudson.plugins.clangtidy;

import com.thalesgroup.hudson.plugins.clangtidy.config.ClangtidyConfig;
import hudson.model.AbstractProject;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Publisher;
import hudson.tasks.Recorder;

/**
 * @author Gregory Boissinot
 * @author Mickael Germain
 */
@Deprecated
public class ClangtidyPublisher extends Recorder {

    private transient ClangtidyConfig clangtidyConfig;

    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.BUILD;
    }

    public static final ClangtidyDescriptor DESCRIPTOR = new ClangtidyDescriptor();

    /**
     * The Clangtidy Descriptor
     */
    public static final class ClangtidyDescriptor extends BuildStepDescriptor<Publisher> {

        @SuppressWarnings("deprecation")
        public ClangtidyDescriptor() {
            super(ClangtidyPublisher.class);
            load();
        }

        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return false;
        }

        @Override
        public String getDisplayName() {
            return "Publish Clangtidy results";
        }

    }


    @SuppressWarnings("unused")
    private Object readResolve() {

        org.jenkinsci.plugins.clangtidy.config.ClangtidyConfig config = new org.jenkinsci.plugins.clangtidy.config.ClangtidyConfig();
        config.setPattern(clangtidyConfig.getClangtidyReportPattern());
        config.setIgnoreBlankFiles(clangtidyConfig.isIgnoreBlankFiles());

        org.jenkinsci.plugins.clangtidy.config.ClangtidyConfigSeverityEvaluation configSeverityEvaluation = new org.jenkinsci.plugins.clangtidy.config.ClangtidyConfigSeverityEvaluation(
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
                true, true, true, true, true, true, true, true, true, true);
        config.setConfigSeverityEvaluation(configSeverityEvaluation);

        org.jenkinsci.plugins.clangtidy.config.ClangtidyConfigGraph configGraph = new org.jenkinsci.plugins.clangtidy.config.ClangtidyConfigGraph(
                clangtidyConfig.getConfigGraph().getXSize(),
                clangtidyConfig.getConfigGraph().getYSize(),
                0,
                clangtidyConfig.getConfigGraph().isDiplayAllError(),
                clangtidyConfig.getConfigGraph().isDisplaySeverityError(),
                clangtidyConfig.getConfigGraph().isDisplaySeverityPossibleError(),
                clangtidyConfig.getConfigGraph().isDisplaySeverityStyle(),
                clangtidyConfig.getConfigGraph().isDisplaySeverityPossibleStyle(),
                true, true, true, true, true, true, true, true, true, true);
        config.setConfigGraph(configGraph);

        org.jenkinsci.plugins.clangtidy.ClangtidyPublisher clangtidyPublisher = new org.jenkinsci.plugins.clangtidy.ClangtidyPublisher(config);
        return clangtidyPublisher;
    }

}