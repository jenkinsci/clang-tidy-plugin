package org.jenkinsci.plugins.clangtidy;

import org.jenkinsci.plugins.clangtidy.model.ClangtidyFile;
import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Gregory Boissinot
 * @author Mickael Germain
 */
@ExportedBean
public class ClangtidyReport implements Serializable {

	private static final long serialVersionUID = 1L;

	private String version;

	private transient List<ClangtidyFile> allErrors = new ArrayList<ClangtidyFile>();
	private transient List<ClangtidyFile> errorSeverityList = new ArrayList<ClangtidyFile>();
	private transient List<ClangtidyFile> warningSeverityList = new ArrayList<ClangtidyFile>();
	private transient Set<String> versions = new HashSet<String>();

	private List<ClangtidyFile> boostWarningList = new ArrayList<ClangtidyFile>();
	private List<ClangtidyFile> certWarningList = new ArrayList<ClangtidyFile>();
	private List<ClangtidyFile> cppcoreguidelinesWarningList = new ArrayList<ClangtidyFile>();
	private List<ClangtidyFile> clangAnalyzerWarningList = new ArrayList<ClangtidyFile>();
	private List<ClangtidyFile> clangDiagnosticWarningList = new ArrayList<ClangtidyFile>();
	private List<ClangtidyFile> googleWarningList = new ArrayList<ClangtidyFile>();
	private List<ClangtidyFile> llvmWarningList = new ArrayList<ClangtidyFile>();
	private List<ClangtidyFile> miscWarningList = new ArrayList<ClangtidyFile>();
	private List<ClangtidyFile> modernizeWarningList = new ArrayList<ClangtidyFile>();
	private List<ClangtidyFile> mpiWarningList = new ArrayList<ClangtidyFile>();
	private List<ClangtidyFile> performanceWarningList = new ArrayList<ClangtidyFile>();
	private List<ClangtidyFile> readabilityWarningList = new ArrayList<ClangtidyFile>();

	public List<ClangtidyFile> getAllErrors() {
		return allErrors;
	}

	public List<ClangtidyFile> getBoostWarningList() {
		return boostWarningList;
	}

	public List<ClangtidyFile> getCertWarningList() {
		return certWarningList;
	}

	public List<ClangtidyFile> getClangAnalyzerWarningList() {
		return clangAnalyzerWarningList;
	}

	public List<ClangtidyFile> getClangDiagnosticWarningList() {
		return clangDiagnosticWarningList;
	}

	public List<ClangtidyFile> getCppcoreguidelinesWarningList() {
		return cppcoreguidelinesWarningList;
	}

	public List<ClangtidyFile> getErrorSeverityList() {
		return errorSeverityList;
	}

	public List<ClangtidyFile> getGoogleWarningList() {
		return googleWarningList;
	}

	public List<ClangtidyFile> getLlvmWarningList() {
		return llvmWarningList;
	}

	public List<ClangtidyFile> getMiscWarningList() {
		return miscWarningList;
	}

	public List<ClangtidyFile> getModernizeWarningList() {
		return modernizeWarningList;
	}

	public List<ClangtidyFile> getMpiWarningList() {
		return mpiWarningList;
	}

	@Exported
	public int getNumberBoostWarning() {
		return (boostWarningList == null) ? 0 : boostWarningList.size();
	}

	@Exported
	public int getNumberCertWarning() {
		return (certWarningList == null) ? 0 : certWarningList.size();
	}

	@Exported
	public int getNumberClangAnalyzerWarning() {
		return (clangAnalyzerWarningList == null) ? 0 : clangAnalyzerWarningList.size();
	}

	@Exported
	public int getNumberClangDiagnosticWarning() {
		return (clangDiagnosticWarningList == null) ? 0 : clangDiagnosticWarningList.size();
	}

	@Exported
	public int getNumberCppcoreguidelinesWarning() {
		return (cppcoreguidelinesWarningList == null) ? 0 : cppcoreguidelinesWarningList.size();
	}

	@Exported
	public int getNumberErrorSeverity() {
		return (errorSeverityList == null) ? 0 : errorSeverityList.size();
	}

	@Exported
	public int getNumberGoogleWarning() {
		return (googleWarningList == null) ? 0 : googleWarningList.size();
	}

	@Exported
	public int getNumberLlvmWarning() {
		return (llvmWarningList == null) ? 0 : llvmWarningList.size();
	}

	@Exported
	public int getNumberMiscWarning() {
		return (miscWarningList == null) ? 0 : miscWarningList.size();
	}

	@Exported
	public int getNumberModernizeWarning() {
		return (modernizeWarningList == null) ? 0 : modernizeWarningList.size();
	}

	@Exported
	public int getNumberMpiWarning() {
		return (mpiWarningList == null) ? 0 : mpiWarningList.size();
	}

	@Exported
	public int getNumberPerformanceWarning() {
		return (performanceWarningList == null) ? 0 : performanceWarningList.size();
	}

	@Exported
	public int getNumberReadabilityWarning() {
		return (readabilityWarningList == null) ? 0 : readabilityWarningList.size();
	}

	@Exported
	public int getNumberTotal() {
		return (allErrors == null) ? 0 : allErrors.size();
	}

	@Exported
	public int getNumberWarningSeverity() {
		return (warningSeverityList == null) ? 0 : warningSeverityList.size();
	}

	public List<ClangtidyFile> getPerformanceWarningList() {
		return performanceWarningList;
	}

	public List<ClangtidyFile> getReadabilityWarningList() {
		return readabilityWarningList;
	}

	/**
	 * Get statistics for this report.
	 *
	 * @return the statistics
	 */
	public ClangtidyStatistics getStatistics() {
		return new ClangtidyStatistics(getNumberErrorSeverity(), getNumberWarningSeverity(), getNumberBoostWarning(),
				getNumberCertWarning(), getNumberCppcoreguidelinesWarning(), getNumberClangAnalyzerWarning(),
				getNumberClangDiagnosticWarning(), getNumberGoogleWarning(), getNumberLlvmWarning(),
				getNumberMiscWarning(), getNumberModernizeWarning(), getNumberMpiWarning(),
				getNumberPerformanceWarning(), getNumberReadabilityWarning(), versions);
	}

	public String getVersion() {
		return version;
	}

	public Set<String> getVersions() {
		return versions;
	}

	public List<ClangtidyFile> getWarningSeverityList() {
		return warningSeverityList;
	}

	private Object readResolve() {
		allErrors = new ArrayList<ClangtidyFile>();
		allErrors.addAll(errorSeverityList);
		allErrors.addAll(warningSeverityList);

		return this;
	}

	public void setAllErrors(List<ClangtidyFile> allErrors) {
		this.allErrors = allErrors;
	}

	public void setBoostWarningList(List<ClangtidyFile> boostWarningList) {
		this.boostWarningList = boostWarningList;
	}

	public void setCertWarningList(List<ClangtidyFile> certWarningList) {
		this.certWarningList = certWarningList;
	}

	public void setClangAnalyzerWarningList(List<ClangtidyFile> clangAnalyzerWarningList) {
		this.clangAnalyzerWarningList = clangAnalyzerWarningList;
	}

	public void setClangDiagnosticWarningList(List<ClangtidyFile> clangDiagnosticWarningList) {
		this.clangDiagnosticWarningList = clangDiagnosticWarningList;
	}

	public void setCppcoreguidelinesWarningList(List<ClangtidyFile> cppcoreguidelinesWarningList) {
		this.cppcoreguidelinesWarningList = cppcoreguidelinesWarningList;
	}

	public void setErrorSeverityList(List<ClangtidyFile> errorSeverityList) {
		this.errorSeverityList = errorSeverityList;
	}

	public void setGoogleWarningList(List<ClangtidyFile> googleWarningList) {
		this.googleWarningList = googleWarningList;
	}

	public void setLlvmWarningList(List<ClangtidyFile> llvmWarningList) {
		this.llvmWarningList = llvmWarningList;
	}

	public void setMiscWarningList(List<ClangtidyFile> miscWarningList) {
		this.miscWarningList = miscWarningList;
	}

	public void setModernizeWarningList(List<ClangtidyFile> modernizeWarningList) {
		this.modernizeWarningList = modernizeWarningList;
	}

	public void setMpiWarningList(List<ClangtidyFile> mpiWarningList) {
		this.mpiWarningList = mpiWarningList;
	}

	public void setPerformanceWarningList(List<ClangtidyFile> performanceWarningList) {
		this.performanceWarningList = performanceWarningList;
	}

	public void setReadabilityWarningList(List<ClangtidyFile> readabilityWarningList) {
		this.readabilityWarningList = readabilityWarningList;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public void setWarningSeverityList(List<ClangtidyFile> warningSeverityList) {
		this.warningSeverityList = warningSeverityList;
	}

}
