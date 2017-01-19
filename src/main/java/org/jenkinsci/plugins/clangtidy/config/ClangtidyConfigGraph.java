package org.jenkinsci.plugins.clangtidy.config;

import java.io.Serializable;

/**
 * @author Gregory Boissinot
 * @author Mickael Germain
 */
public class ClangtidyConfigGraph implements Serializable {
	/** Serial version UID. */
	private static final long serialVersionUID = 1L;

	public static final int DEFAULT_CHART_WIDTH = 500;
	public static final int DEFAULT_CHART_HEIGHT = 200;

	public static int getDefaultChartHeight() {
		return DEFAULT_CHART_HEIGHT;
	}

	public static int getDefaultChartWidth() {
		return DEFAULT_CHART_WIDTH;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	private int xSize = DEFAULT_CHART_WIDTH;
	private int ySize = DEFAULT_CHART_HEIGHT;
	private int numBuildsInGraph = 0; // numBuildsInGraph <= 1 means unlimited
	private boolean displayAllErrors = true;
	private boolean displayErrorSeverity = false;
	private boolean displayWarningSeverity = false;
	private boolean displayBoostWarning = true;
	private boolean displayCertWarning = true;
	private boolean displayCppcoreguidelinesWarning = true;
	private boolean displayClangAnalyzerWarning = true;
	private boolean displayClangDiagnosticWarning = true;
	private boolean displayGoogleWarning = true;
	private boolean displayLlvmWarning = true;
	private boolean displayMiscWarning = true;
	private boolean displayModernizeWarning = true;

	private boolean displayMpiWarning = true;

	private boolean displayPerformanceWarning = true;

	private boolean displayReadabilityWarning = true;

	public ClangtidyConfigGraph() {
	}

	public ClangtidyConfigGraph(int xSize, int ySize, int numBuildsInGraph, boolean displayAllErrors,
			boolean displayErrorSeverity, boolean displayWarningSeverity, boolean displayBoostWarning,
			boolean displayCertWarning, boolean displayCppcoreguidelinesWarning, boolean displayClangAnalyzerWarning,
			boolean displayClangDiagnosticWarning, boolean displayGoogleWarning, boolean displayLlvmWarning,
			boolean displayMiscWarning, boolean displayModernizeWarning, boolean displayMpiWarning,
			boolean displayPerformanceWarning, boolean displayReadabilityWarning) {
		this.xSize = xSize;
		this.ySize = ySize;
		this.numBuildsInGraph = numBuildsInGraph;
		this.displayAllErrors = displayAllErrors;
		this.displayErrorSeverity = displayErrorSeverity;
		this.displayWarningSeverity = displayWarningSeverity;
		this.displayBoostWarning = displayBoostWarning;
		this.displayCertWarning = displayCertWarning;
		this.displayCppcoreguidelinesWarning = displayCppcoreguidelinesWarning;
		this.displayClangAnalyzerWarning = displayClangAnalyzerWarning;
		this.displayClangDiagnosticWarning = displayClangDiagnosticWarning;
		this.displayGoogleWarning = displayGoogleWarning;
		this.displayLlvmWarning = displayLlvmWarning;
		this.displayMiscWarning = displayMiscWarning;
		this.displayModernizeWarning = displayModernizeWarning;
		this.displayMpiWarning = displayMpiWarning;
		this.displayPerformanceWarning = displayPerformanceWarning;
		this.displayReadabilityWarning = displayReadabilityWarning;
	}

	public int getNumBuildsInGraph() {
		return numBuildsInGraph;
	}

	public int getxSize() {
		return xSize;
	}

	public int getXSize() {
		return xSize;
	}

	public int getySize() {
		return ySize;
	}

	public int getYSize() {
		return ySize;
	}

	public boolean isDisplayAllErrors() {
		return displayAllErrors;
	}

	public boolean isDisplayBoostWarning() {
		return displayBoostWarning;
	}

	public boolean isDisplayCertWarning() {
		return displayCertWarning;
	}

	public boolean isDisplayClangAnalyzerWarning() {
		return displayClangAnalyzerWarning;
	}

	public boolean isDisplayClangDiagnosticWarning() {
		return displayClangDiagnosticWarning;
	}

	public boolean isDisplayCppcoreguidelinesWarning() {
		return displayCppcoreguidelinesWarning;
	}

	public boolean isDisplayErrorSeverity() {
		return displayErrorSeverity;
	}

	public boolean isDisplayGoogleWarning() {
		return displayGoogleWarning;
	}

	public boolean isDisplayLlvmWarning() {
		return displayLlvmWarning;
	}

	public boolean isDisplayMiscWarning() {
		return displayMiscWarning;
	}

	public boolean isDisplayModernizeWarning() {
		return displayModernizeWarning;
	}

	public boolean isDisplayMpiWarning() {
		return displayMpiWarning;
	}

	public boolean isDisplayPerformanceWarning() {
		return displayPerformanceWarning;
	}

	public boolean isDisplayReadabilityWarning() {
		return displayReadabilityWarning;
	}

	public boolean isDisplayWarningSeverity() {
		return displayWarningSeverity;
	}

}
