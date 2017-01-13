package org.jenkinsci.plugins.clangtidy;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;

/**
 * Statistics for a report to be stored in build.xml file. Details are lazy
 * loaded from external file after they are needed.
 * 
 * @author Michal Turek
 * @author Mickael Germain
 */
@ExportedBean
public class ClangtidyStatistics implements Serializable {
    /** Serial version UID. */
    private static final long serialVersionUID = 1L;

    /** Count of error issues. */
    private final int errorSeverityCount;

    /** Count of warning issues. */
    private final int warningSeverityCount;

    /** Count of boost issues. */
    private final int boostWarningCount;

    /** Count of cert issues. */
    private final int certWarningCount;

    /** Count of cppcoreguidelines issues. */
    private final int cppcoreguidelinesWarningCount;

    /** Count of clang-analyzer issues. */
    private final int clangAnalyzerWarningCount;

    /** Count of clang-diagnostic issues. */
    private final int clangDiagnosticWarningCount;
    
    /** Count of google issues. */
    private final int googleWarningCount;
    
    /** Count of llvm issues. */
    private final int llvmWarningCount;
    
    /** Count of misc issues. */
    private final int miscWarningCount;
    
    /** Count of modernize issues. */
    private final int modernizeWarningCount;
    
    /** Count of mpi issues. */
    private final int mpiWarningCount;
    
    /** Count of performance issues. */
    private final int performanceWarningCount;
    
    /** Count of google issues. */
    private final int readabilityWarningCount;
    

    /** Clangtidy versions used for generating of the report. */
    private final Set<String> versions;

    /**
     * Constructor, create an empty object.
     */
    public ClangtidyStatistics() {
        this(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, Collections.<String> emptySet());
    }

    /**
     * @param errorSeverityCount
     *            count of error issues
     * @param warningSeverityCount
     *            count of warning issues
     * @param boostWarningCount
     *            count of boost issues
     * @param certWarningCount
     *            count of cert issues
     * @param cppcoreguidelinesWarningCount
     *            count of cppcoreguidelines issues
     * @param clangAnalyzerWarningCount
     *            count of clang-analyzer with no category
     * @param googleWarningCount
     *            count of google issues
     * @param llvmWarningCount
     *            count of llvm issues
     * @param miscWarningCount
     *            count of misc issues
     * @param modernizeWarningCount
     *            count of modernize issues
     * @param mpiWarningCount
     *            count of mpi issues
     * @param performanceWarningCount
     *            count of performance issues
     * @param readabilityWarningCount
     *            count of readabilty issues
     * @param versions
     *            Clangtidy versions used for generating of the report
     */
    public ClangtidyStatistics(int errorSeverityCount, int warningSeverityCount, int boostWarningCount,
            int certWarningCount, int cppcoreguidelinesWarningCount, int clangAnalyzerWarningCount,
            int clangDiagnosticWarningCount, int googleWarningCount, int llvmWarningCount, 
            int miscWarningCount, int modernizeWarningCount, int mpiWarningCount, 
            int performanceWarningCount, int readabilityWarningCount, Set<String> versions) {
        this.errorSeverityCount = errorSeverityCount;
        this.warningSeverityCount = warningSeverityCount;
        this.boostWarningCount = boostWarningCount;
        this.certWarningCount = certWarningCount;
        this.cppcoreguidelinesWarningCount = cppcoreguidelinesWarningCount;
        this.clangAnalyzerWarningCount = clangAnalyzerWarningCount;
        this.clangDiagnosticWarningCount = clangDiagnosticWarningCount;
        this.googleWarningCount = googleWarningCount;
        this.llvmWarningCount = llvmWarningCount;
        this.miscWarningCount = miscWarningCount;
        this.modernizeWarningCount = modernizeWarningCount;
        this.mpiWarningCount = mpiWarningCount;
        this.performanceWarningCount = performanceWarningCount;
        this.readabilityWarningCount = readabilityWarningCount;
        this.versions = (versions != null) ? new HashSet<String>(versions)
                : null;
    }

    /**
     * Get total count of all issues.
     * 
     * @return the sum of issues of all severities
     */
    @Exported
    public int getNumberTotal() {
        return errorSeverityCount + warningSeverityCount;
    }

    @Exported
    public int getNumberErrorSeverity() {
        return errorSeverityCount;
    }

    @Exported
    public int getNumberWarningSeverity() {
        return warningSeverityCount;
    }
    
    @Exported
    public int getNumberBoostWarning() {
        return boostWarningCount;
    }
    
    @Exported
    public int getNumberCertWarning() {
        return certWarningCount;
    }

    @Exported
    public int getNumberCppcoreguidelinesWarning() {
        return cppcoreguidelinesWarningCount;
    }

    @Exported
    public int getNumberClangAnalyzerWarning() {
        return clangAnalyzerWarningCount;
    }

    @Exported
    public int getNumberClangDiagnosticWarning() {
        return clangDiagnosticWarningCount;
    }

    @Exported
    public int getNumberGoogleWarning() {
        return googleWarningCount;
    }

    @Exported
    public int getNumberLlvmWarning() {
        return llvmWarningCount;
    }

    @Exported
    public int getNumberMiscWarning() {
        return miscWarningCount;
    }

    @Exported
    public int getNumberModernizeWarning() {
        return modernizeWarningCount;
    }
    
    @Exported
    public int getNumberMpiWarning() {
        return mpiWarningCount;
    }

    @Exported
    public int getNumberPerformanceWarning() {
        return performanceWarningCount;
    }

    @Exported
    public int getNumberReadabilityWarning() {
        return readabilityWarningCount;
    }
    
    public Set<String> getVersions() {
        return (versions != null) ? Collections.unmodifiableSet(versions)
                : null;
    }

    public String formatDiff(int value) {
        if (value == 0) {
            return "";
        }

        return String.format("%+d", value);
    }
}
