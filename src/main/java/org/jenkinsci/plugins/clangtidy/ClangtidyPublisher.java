package org.jenkinsci.plugins.clangtidy;

import com.thalesgroup.hudson.plugins.clangtidy.model.ClangtidyWorkspaceFile;

import hudson.EnvVars;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.XmlFile;
import hudson.model.*;
import hudson.remoting.VirtualChannel;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Publisher;
import hudson.tasks.Recorder;

import org.jenkinsci.plugins.clangtidy.config.ClangtidyConfig;
import org.jenkinsci.plugins.clangtidy.config.ClangtidyConfigGraph;
import org.jenkinsci.plugins.clangtidy.config.ClangtidyConfigSeverityEvaluation;
import org.jenkinsci.plugins.clangtidy.util.ClangtidyBuildResultEvaluator;
import org.jenkinsci.plugins.clangtidy.util.ClangtidyLogger;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;

/**
 * @author Gregory Boissinot
 */
public class ClangtidyPublisher extends Recorder {
    /**
     * XML file with source container data. Lazy loading instead of data in build.xml.
     * 
     * @since 1.15
     */
    public static final String XML_FILE_DETAILS = "clangtidy_details.xml";

    private ClangtidyConfig clangtidyConfig;

    @DataBoundConstructor
    public ClangtidyPublisher(String pattern,
                             boolean ignoreBlankFiles, String threshold,
                             boolean allowNoReport,
                             String newThreshold, String failureThreshold,
                             String newFailureThreshold, String healthy, String unHealthy,
                             boolean severityError,
                             boolean severityWarning,
                             boolean severityStyle,
                             boolean severityPerformance,
                             boolean severityInformation,
                             boolean severityNoCategory,
                             boolean severityPortability,
                             int xSize, int ySize,
                             int numBuildsInGraph,
                             boolean displayAllErrors,
                             boolean displayErrorSeverity,
                             boolean displayWarningSeverity,
                             boolean displayStyleSeverity,
                             boolean displayPerformanceSeverity,
                             boolean displayInformationSeverity,
                             boolean displayNoCategorySeverity,
                             boolean displayPortabilitySeverity) {

        clangtidyConfig = new ClangtidyConfig();

        clangtidyConfig.setPattern(pattern);
        clangtidyConfig.setAllowNoReport(allowNoReport);
        clangtidyConfig.setIgnoreBlankFiles(ignoreBlankFiles);
        ClangtidyConfigSeverityEvaluation configSeverityEvaluation = new ClangtidyConfigSeverityEvaluation(
                threshold, newThreshold, failureThreshold, newFailureThreshold, healthy, unHealthy,
                severityError,
                severityWarning,
                severityStyle,
                severityPerformance,
                severityInformation,
                severityNoCategory,
                severityPortability);
        clangtidyConfig.setConfigSeverityEvaluation(configSeverityEvaluation);
        ClangtidyConfigGraph configGraph = new ClangtidyConfigGraph(
                xSize, ySize, numBuildsInGraph,
                displayAllErrors,
                displayErrorSeverity,
                displayWarningSeverity,
                displayStyleSeverity,
                displayPerformanceSeverity,
                displayInformationSeverity,
                displayNoCategorySeverity,
                displayPortabilitySeverity);
        clangtidyConfig.setConfigGraph(configGraph);
    }


    public ClangtidyPublisher(ClangtidyConfig clangtidyConfig) {
        this.clangtidyConfig = clangtidyConfig;
    }

    public ClangtidyConfig getClangtidyConfig() {
        return clangtidyConfig;
    }

    @Override
    public Action getProjectAction(AbstractProject<?, ?> project) {
        return new ClangtidyProjectAction(project, clangtidyConfig.getConfigGraph());
    }

    protected boolean canContinue(final Result result) {
        return result != Result.ABORTED && result != Result.FAILURE;
    }

    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.BUILD;
    }

    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher,
            BuildListener listener) throws InterruptedException, IOException {

        if (this.canContinue(build.getResult())) {
            ClangtidyLogger.log(listener, "Starting the clangtidy analysis.");
            
            EnvVars env = build.getEnvironment(listener);
            String expandedPattern = env.expand(clangtidyConfig.getPattern());
            

            ClangtidyParserResult parser = new ClangtidyParserResult(listener,
            		expandedPattern, clangtidyConfig.isIgnoreBlankFiles());
            ClangtidyReport clangtidyReport;
            try {
                clangtidyReport = build.getWorkspace().act(parser);
            } catch (Exception e) {
                ClangtidyLogger.log(listener, "Error on clangtidy analysis: " + e);
                build.setResult(Result.FAILURE);
                return false;
            }

            if (clangtidyReport == null) {
                // Check if we're configured to allow not having a report
                if (clangtidyConfig.getAllowNoReport()) {
                    return true;
                } else {
                    build.setResult(Result.FAILURE);
                    return false;
                }
            }

            ClangtidySourceContainer clangtidySourceContainer
                    = new ClangtidySourceContainer(listener, build.getWorkspace(),
                            build.getModuleRoot(), clangtidyReport.getAllErrors());

            ClangtidyResult result = new ClangtidyResult(clangtidyReport.getStatistics(), build);
            ClangtidyConfigSeverityEvaluation severityEvaluation
                    = clangtidyConfig.getConfigSeverityEvaluation();

            Result buildResult = new ClangtidyBuildResultEvaluator().evaluateBuildResult(
                    listener, result.getNumberErrorsAccordingConfiguration(severityEvaluation, false),
                    result.getNumberErrorsAccordingConfiguration(severityEvaluation, true),
                    severityEvaluation);

            if (buildResult != Result.SUCCESS) {
                build.setResult(buildResult);
            }

            ClangtidyBuildAction buildAction = new ClangtidyBuildAction(build, result,
                    ClangtidyBuildAction.computeHealthReportPercentage(result, severityEvaluation));

            build.addAction(buildAction);

            XmlFile xmlSourceContainer = new XmlFile(new File(build.getRootDir(),
                    XML_FILE_DETAILS));
            xmlSourceContainer.write(clangtidySourceContainer);

            copyFilesToBuildDirectory(build.getRootDir(), launcher.getChannel(),
                    clangtidySourceContainer.getInternalMap().values());

            ClangtidyLogger.log(listener, "Ending the clangtidy analysis.");
        }
        return true;
    }


    /**
     * Copies all the source files from the workspace to the build folder.
     *
     * @param rootDir      directory to store the copied files in
     * @param channel      channel to get the files from
     * @param sourcesFiles the sources files to be copied
     * @throws IOException                   if the files could not be written
     * @throws java.io.FileNotFoundException if the files could not be written
     * @throws InterruptedException          if the user cancels the processing
     */
    private void copyFilesToBuildDirectory(final File rootDir,
            final VirtualChannel channel,
            final Collection<ClangtidyWorkspaceFile> sourcesFiles)
            throws IOException, InterruptedException {

        File directory = new File(rootDir, ClangtidyWorkspaceFile.DIR_WORKSPACE_FILES);
        if (!directory.exists() && !directory.mkdir()) {
            throw new IOException("Can't create directory for copy of workspace files: "
                    + directory.getAbsolutePath());
        }

        for (ClangtidyWorkspaceFile file : sourcesFiles) {
            if (!file.isSourceIgnored()) {
                File masterFile = new File(directory, file.getTempName());
                if (!masterFile.exists()) {
                    FileOutputStream outputStream = new FileOutputStream(masterFile);
                    new FilePath(channel, file.getFileName()).copyTo(outputStream);
                }
            }
        }
    }

    @Extension
    public static final class ClangtidyDescriptor extends BuildStepDescriptor<Publisher> {

        public ClangtidyDescriptor() {
            super(ClangtidyPublisher.class);
            load();
        }

        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return true;
        }

        @Override
        public String getDisplayName() {
            return Messages.clangtidy_PublishResults();
        }

        @Override
        public final String getHelpFile() {
            return getPluginRoot() + "help.html";
        }

        public String getPluginRoot() {
            return "/plugin/clangtidy/";
        }

        public ClangtidyConfig getConfig() {
            return new ClangtidyConfig();
        }
    }
}
