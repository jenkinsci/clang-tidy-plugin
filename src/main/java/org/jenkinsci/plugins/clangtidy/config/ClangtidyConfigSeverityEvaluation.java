package org.jenkinsci.plugins.clangtidy.config;

import java.io.Serializable;

/**
 * @author Gregory Boissinot
 * @author Mickael Germain
 */
public class ClangtidyConfigSeverityEvaluation implements Serializable {
	/** Serial version UID. */
	private static final long serialVersionUID = 1L;

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	private String threshold;

	private String newThreshold;

	private String failureThreshold;

	private String newFailureThreshold;

	private String healthy;

	private String unHealthy;

	private boolean severityError = false;

	private boolean severityWarning = false;

	private boolean warningBoost = true;

	private boolean warningCert = true;

	private boolean warningCppcoreguidelines = true;

	private boolean warningClangAnalyzer = true;

	private boolean warningClangDiagnostic = true;

	private boolean warningGoogle = true;

	private boolean warningLlvm = true;

	private boolean warningMisc = true;

	private boolean warningModernize = true;

	private boolean warningMpi = true;

	private boolean warningPerformance = true;

	private boolean warningReadability = true;

	public ClangtidyConfigSeverityEvaluation() {
	}

	public ClangtidyConfigSeverityEvaluation(String threshold, String newThreshold, String failureThreshold,
			String newFailureThreshold, String healthy, String unHealthy, boolean severityError,
			boolean severityWarning, boolean warningBoost, boolean warningCert, boolean warningCppcoreguidelines,
			boolean warningClangAnalyzer, boolean warningClangDiagnostic, boolean warningGoogle, boolean warningLlvm,
			boolean warningMisc, boolean warningModernize, boolean warningMpi, boolean warningPerformance,
			boolean warningReadability) {
		this.threshold = threshold;
		this.newThreshold = newThreshold;
		this.failureThreshold = failureThreshold;
		this.newFailureThreshold = newFailureThreshold;
		this.healthy = healthy;
		this.unHealthy = unHealthy;
		this.severityError = severityError;
		this.severityWarning = severityWarning;
		this.warningBoost = warningBoost;
		this.warningCert = warningCert;
		this.warningCppcoreguidelines = warningCppcoreguidelines;
		this.warningClangAnalyzer = warningClangAnalyzer;
		this.warningClangDiagnostic = warningClangDiagnostic;
		this.warningGoogle = warningGoogle;
		this.warningLlvm = warningLlvm;
		this.warningMisc = warningMisc;
		this.warningModernize = warningModernize;
		this.warningMpi = warningMpi;
		this.warningPerformance = warningPerformance;
		this.warningReadability = warningReadability;
	}

	public String getFailureThreshold() {
		return failureThreshold;
	}

	public String getHealthy() {
		return healthy;
	}

	public String getNewFailureThreshold() {
		return newFailureThreshold;
	}

	public String getNewThreshold() {
		return newThreshold;
	}

	public String getThreshold() {
		return threshold;
	}

	public String getUnHealthy() {
		return unHealthy;
	}

	public boolean isSeverityError() {
		return severityError;
	}

	public boolean isSeverityWarning() {
		return severityWarning;
	}

	public boolean isWarningBoost() {
		return warningBoost;
	}

	public boolean isWarningCert() {
		return warningCert;
	}

	public boolean isWarningClangAnalyzer() {
		return warningClangAnalyzer;
	}

	public boolean isWarningClangDiagnostic() {
		return warningClangDiagnostic;
	}

	public boolean isWarningCppcoreguidelines() {
		return warningCppcoreguidelines;
	}

	public boolean isWarningGoogle() {
		return warningGoogle;
	}

	public boolean isWarningLlvm() {
		return warningLlvm;
	}

	public boolean isWarningMisc() {
		return warningMisc;
	}

	public boolean isWarningModernize() {
		return warningModernize;
	}

	public boolean isWarningMpi() {
		return warningMpi;
	}

	public boolean isWarningPerformance() {
		return warningPerformance;
	}

	public boolean isWarningReadability() {
		return warningReadability;
	}
}
