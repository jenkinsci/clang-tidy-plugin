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
	private boolean stableBuild;
	private ClangtidyConfigSeverityEvaluation configSeverityEvaluation = new ClangtidyConfigSeverityEvaluation();
	private ClangtidyConfigGraph configGraph = new ClangtidyConfigGraph();

	/*
	 * Backward compatibility
	 */
	private transient String clangtidyReportPattern;

	private transient boolean useWorkspaceAsRootPath;

	public boolean getAllowNoReport() {
		return allowNoReport;
	}

	public ClangtidyConfigGraph getConfigGraph() {
		return configGraph;
	}

	public ClangtidyConfigSeverityEvaluation getConfigSeverityEvaluation() {
		return configSeverityEvaluation;
	}

	public String getPattern() {
		return pattern;
	}

	public boolean getStableBuild() {
		return stableBuild;
	}

	public boolean isIgnoreBlankFiles() {
		return ignoreBlankFiles;
	}

	public boolean isUseWorkspaceAsRootPath() {
		return useWorkspaceAsRootPath;
	}

	private Object readResolve() {
		if (clangtidyReportPattern != null) {
			pattern = clangtidyReportPattern;
		}
		return this;
	}

	public void setAllowNoReport(boolean allowNoReport) {
		this.allowNoReport = allowNoReport;
	}

	public void setClangtidyReportPattern(String clangtidyReportPattern) {
		this.clangtidyReportPattern = clangtidyReportPattern;
	}

	public void setConfigGraph(ClangtidyConfigGraph configGraph) {
		this.configGraph = configGraph;
	}

	public void setConfigSeverityEvaluation(ClangtidyConfigSeverityEvaluation configSeverityEvaluation) {
		this.configSeverityEvaluation = configSeverityEvaluation;
	}

	public void setIgnoreBlankFiles(boolean ignoreBlankFiles) {
		this.ignoreBlankFiles = ignoreBlankFiles;
	}

	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	public void setStableBuild(boolean stableBuild) {
		this.stableBuild = stableBuild;
	}

	public void setUseWorkspaceAsRootPath(boolean useWorkspaceAsRootPath) {
		this.useWorkspaceAsRootPath = useWorkspaceAsRootPath;
	}
}
