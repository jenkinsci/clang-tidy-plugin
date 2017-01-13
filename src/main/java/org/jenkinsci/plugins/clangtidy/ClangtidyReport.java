package org.jenkinsci.plugins.clangtidy;

import com.thalesgroup.hudson.plugins.clangtidy.model.ClangtidyFile;
import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Gregory Boissinot
 */
@ExportedBean
public class ClangtidyReport implements Serializable {

    private String version;

    private transient List<ClangtidyFile> allErrors = new ArrayList<ClangtidyFile>();
    private transient Set<String> versions = new HashSet<String>();

    private List<ClangtidyFile> errorSeverityList = new ArrayList<ClangtidyFile>();
    private List<ClangtidyFile> warningSeverityList = new ArrayList<ClangtidyFile>();
    private List<ClangtidyFile> styleSeverityList = new ArrayList<ClangtidyFile>();
    private List<ClangtidyFile> performanceSeverityList = new ArrayList<ClangtidyFile>();
    private List<ClangtidyFile> informationSeverityList = new ArrayList<ClangtidyFile>();
    private List<ClangtidyFile> noCategorySeverityList = new ArrayList<ClangtidyFile>();
    private List<ClangtidyFile> portabilitySeverityList = new ArrayList<ClangtidyFile>();

    public String getVersion() {
        return version;
    }

    public Set<String> getVersions() {
        return versions;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public List<ClangtidyFile> getAllErrors() {
        return allErrors;
    }

    public void setAllErrors(List<ClangtidyFile> allErrors) {
        this.allErrors = allErrors;
    }

    public List<ClangtidyFile> getErrorSeverityList() {
        return errorSeverityList;
    }

    public void setErrorSeverityList(List<ClangtidyFile> errorSeverityList) {
        this.errorSeverityList = errorSeverityList;
    }

    public List<ClangtidyFile> getWarningSeverityList() {
        return warningSeverityList;
    }

    public void setWarningSeverityList(List<ClangtidyFile> warningSeverityList) {
        this.warningSeverityList = warningSeverityList;
    }

    public List<ClangtidyFile> getStyleSeverityList() {
        return styleSeverityList;
    }

    public void setStyleSeverityList(List<ClangtidyFile> styleSeverityList) {
        this.styleSeverityList = styleSeverityList;
    }

    public List<ClangtidyFile> getPerformanceSeverityList() {
        return performanceSeverityList;
    }

    public void setPerformanceSeverityList(List<ClangtidyFile> performanceSeverityList) {
        this.performanceSeverityList = performanceSeverityList;
    }

    public List<ClangtidyFile> getInformationSeverityList() {
        return informationSeverityList;
    }

    public void setInformationSeverityList(List<ClangtidyFile> informationSeverityList) {
        this.informationSeverityList = informationSeverityList;
    }

    public List<ClangtidyFile> getNoCategorySeverityList() {
        return noCategorySeverityList;
    }

    public void setNoCategorySeverityList(List<ClangtidyFile> noCategorySeverityList) {
        this.noCategorySeverityList = noCategorySeverityList;
    }

    public List<ClangtidyFile> getPortabilitySeverityList() {
        return portabilitySeverityList;
    }

    public void setPortabilitySeverityList(List<ClangtidyFile> portabilitySeverityList) {
        this.portabilitySeverityList = portabilitySeverityList;
    }

    @Exported
    public int getNumberTotal() {
        return (allErrors == null) ? 0 : allErrors.size();
    }

    @Exported
    public int getNumberErrorSeverity() {
        return (errorSeverityList == null) ? 0 : errorSeverityList.size();
    }

    @Exported
    public int getNumberWarningSeverity() {
        return (warningSeverityList == null) ? 0 : warningSeverityList.size();
    }

    @Exported
    public int getNumberStyleSeverity() {
        return (styleSeverityList == null) ? 0 : styleSeverityList.size();
    }

    @Exported
    public int getNumberPerformanceSeverity() {
        return (performanceSeverityList == null) ? 0 : performanceSeverityList.size();
    }

    @Exported
    public int getNumberInformationSeverity() {
        return (informationSeverityList == null) ? 0 : informationSeverityList.size();
    }

    @Exported
    public int getNumberNoCategorySeverity() {
        return (noCategorySeverityList == null) ? 0 : noCategorySeverityList.size();
    }

    @Exported
    public int getNumberPortabilitySeverity() {
        return (portabilitySeverityList == null) ? 0 : portabilitySeverityList.size();
    }

    private Object readResolve() {
        this.allErrors = new ArrayList<ClangtidyFile>();
        this.allErrors.addAll(errorSeverityList);
        this.allErrors.addAll(warningSeverityList);
        this.allErrors.addAll(styleSeverityList);
        this.allErrors.addAll(performanceSeverityList);
        this.allErrors.addAll(informationSeverityList);
        this.allErrors.addAll(noCategorySeverityList);

        // Backward compatibility with version 1.14 and less
        if(portabilitySeverityList == null)
        {
            portabilitySeverityList = new ArrayList<ClangtidyFile>();
        }

        this.allErrors.addAll(portabilitySeverityList);

        return this;
    }

    /**
     * Get statistics for this report.
     * 
     * @return the statistics
     */
    public ClangtidyStatistics getStatistics() {
        return new ClangtidyStatistics(getNumberErrorSeverity(),
                getNumberWarningSeverity(), getNumberStyleSeverity(),
                getNumberPerformanceSeverity(), getNumberInformationSeverity(),
                getNumberNoCategorySeverity(), getNumberPortabilitySeverity(),
                versions);
    }
}
