package org.jenkinsci.plugins.clangtidy.config;


import java.io.Serializable;

/**
 * @author Gregory Boissinot
 */
public class ClangtidyConfig implements Serializable {
    /** Serial version UID. */
    private static final long serialVersionUID = 1L;

    private String pattern;
    private boolean ignoreBlankFiles;
    private boolean allowNoReport;
    private ClangtidyConfigSeverityEvaluation configSeverityEvaluation = new ClangtidyConfigSeverityEvaluation();
    private ClangtidyConfigGraph configGraph = new ClangtidyConfigGraph();

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public void setIgnoreBlankFiles(boolean ignoreBlankFiles) {
        this.ignoreBlankFiles = ignoreBlankFiles;
    }

    public void setAllowNoReport(boolean allowNoReport) {
        this.allowNoReport = allowNoReport;
    }

    public void setConfigSeverityEvaluation(ClangtidyConfigSeverityEvaluation configSeverityEvaluation) {
        this.configSeverityEvaluation = configSeverityEvaluation;
    }

    public void setConfigGraph(ClangtidyConfigGraph configGraph) {
        this.configGraph = configGraph;
    }

    public void setClangtidyReportPattern(String clangtidyReportPattern) {
        this.clangtidyReportPattern = clangtidyReportPattern;
    }

    public void setUseWorkspaceAsRootPath(boolean useWorkspaceAsRootPath) {
        this.useWorkspaceAsRootPath = useWorkspaceAsRootPath;
    }

    public String getPattern() {
        return pattern;
    }

    @Deprecated
    public String getClangtidyReportPattern() {
        return clangtidyReportPattern;
    }

    public boolean isUseWorkspaceAsRootPath() {
        return useWorkspaceAsRootPath;
    }

    public boolean isIgnoreBlankFiles() {
        return ignoreBlankFiles;
    }

    public boolean getAllowNoReport() {
        return allowNoReport;
    }

    public ClangtidyConfigSeverityEvaluation getConfigSeverityEvaluation() {
        return configSeverityEvaluation;
    }

    public ClangtidyConfigGraph getConfigGraph() {
        return configGraph;
    }

    /*
    Backward compatibility
     */
    private transient String clangtidyReportPattern;
    private transient boolean useWorkspaceAsRootPath;

    private Object readResolve() {
        if (this.clangtidyReportPattern != null) {
            this.pattern = clangtidyReportPattern;
        }
        return this;
    }
}
