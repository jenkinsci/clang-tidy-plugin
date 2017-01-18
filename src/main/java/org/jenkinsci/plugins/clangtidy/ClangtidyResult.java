package org.jenkinsci.plugins.clangtidy;

import org.jenkinsci.plugins.clangtidy.ClangtidySource;
import org.jenkinsci.plugins.clangtidy.model.ClangtidyFile;
import org.jenkinsci.plugins.clangtidy.model.ClangtidyWorkspaceFile;

import hudson.XmlFile;
import hudson.model.AbstractBuild;
import hudson.model.Api;
import hudson.model.Item;

import org.apache.commons.lang.StringUtils;
import org.jenkinsci.plugins.clangtidy.config.ClangtidyConfigSeverityEvaluation;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;
import org.kohsuke.stapler.export.Exported;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;

/**
 * @author Gregory Boissinot
 * @author Mickael Germain
 */
public class ClangtidyResult implements Serializable {
	private static final long serialVersionUID = 2L;

	/**
	 * The build owner.
	 */
	private AbstractBuild<?, ?> owner;

	/**
	 * The Clangtidy report statistics.
	 *
	 * @since 1.15
	 */
	private ClangtidyStatistics statistics;

	/**
	 * Constructor.
	 *
	 * @param statistics
	 *            the Clangtidy report statistics
	 * @param owner
	 *            the build owner
	 *
	 * @since 1.15
	 */
	public ClangtidyResult(ClangtidyStatistics statistics, AbstractBuild<?, ?> owner) {
		this.statistics = statistics;
		this.owner = owner;
	}

	/**
	 * Compare current and previous source containers. The code first tries to
	 * find all exact matches (file, line, message) and then all approximate
	 * matches (file, message). It is possible the line number will change if a
	 * developer updates the source code somewhere above the issue. Move of the
	 * code to a different file e.g. during refactoring is not considered and
	 * one solved and one new issue will be highlighted in such case.
	 *
	 * @param filter
	 *            put only issues of these types to the output, null for all
	 * @return the result of the comparison
	 */
	public Collection<ClangtidyWorkspaceFile> diffCurrentAndPrevious(Set<ClangtidyDiffState> filter) {
		ClangtidySourceContainer cur = getClangtidySourceContainer();
		ClangtidyResult prevResult = getPreviousResult();
		List<ClangtidyWorkspaceFile> curValues = new ArrayList<ClangtidyWorkspaceFile>(cur.getInternalMap().values());

		if (prevResult == null) {
			for (ClangtidyWorkspaceFile file : curValues) {
				file.setDiffState(ClangtidyDiffState.UNCHANGED);
			}

			return filterDiffOutput(curValues, filter);
		}

		ClangtidySourceContainer prev = prevResult.getClangtidySourceContainer();
		Collection<ClangtidyWorkspaceFile> prevValues = prev.getInternalMap().values();

		// Exact match first
		for (ClangtidyWorkspaceFile curFile : curValues) {
			ClangtidyFile curCppFile = curFile.getClangtidyFile();

			for (ClangtidyWorkspaceFile prevFile : prevValues) {
				ClangtidyFile prevCppFile = prevFile.getClangtidyFile();

				if ((curCppFile.getLineNumber() == prevCppFile.getLineNumber())
						&& (curCppFile.getColumnNumber() == prevCppFile.getColumnNumber())
						&& curCppFile.getFileNameNotNull().equals(prevCppFile.getFileNameNotNull())
						&& curCppFile.getMessage().equals(prevCppFile.getMessage())) {

					curFile.setDiffState(ClangtidyDiffState.UNCHANGED);
					prevFile.setDiffState(ClangtidyDiffState.UNCHANGED);
					break;
				}
			}
		}

		// Approximate match of the rest (ignore line numbers)
		for (ClangtidyWorkspaceFile curFile : curValues) {
			if (curFile.getDiffState() != null) {
				continue;
			}

			ClangtidyFile curCppFile = curFile.getClangtidyFile();

			for (ClangtidyWorkspaceFile prevFile : prevValues) {
				if (prevFile.getDiffState() != null) {
					continue;
				}

				ClangtidyFile prevCppFile = prevFile.getClangtidyFile();

				if (curCppFile.getFileNameNotNull().equals(prevCppFile.getFileNameNotNull())
						&& curCppFile.getMessage().equals(prevCppFile.getMessage())) {
					curFile.setDiffState(ClangtidyDiffState.UNCHANGED);
					prevFile.setDiffState(ClangtidyDiffState.UNCHANGED);
					break;
				}
			}
		}

		// Label all new
		for (ClangtidyWorkspaceFile curFile : curValues) {
			if (curFile.getDiffState() != null) {
				continue;
			}

			curFile.setDiffState(ClangtidyDiffState.NEW);
		}

		// Add and label all solved
		for (ClangtidyWorkspaceFile prevFile : prevValues) {
			if (prevFile.getDiffState() != null) {
				continue;
			}

			prevFile.setDiffState(ClangtidyDiffState.SOLVED);
			prevFile.setSourceIgnored(true);
			curValues.add(prevFile);
		}

		// Sort according to the compare flag
		Collections.sort(curValues, new Comparator<ClangtidyWorkspaceFile>() {
			@Override
			public int compare(ClangtidyWorkspaceFile a, ClangtidyWorkspaceFile b) {
				return a.getDiffState().ordinal() - b.getDiffState().ordinal();
			}
		});

		return filterDiffOutput(curValues, filter);
	}

	/**
	 * Filter result of comparison.
	 *
	 * @param files
	 *            input issues
	 * @param filter
	 *            put only issues of these types to the output, null for all
	 * @return filtered input
	 */
	private Collection<ClangtidyWorkspaceFile> filterDiffOutput(List<ClangtidyWorkspaceFile> files,
			Set<ClangtidyDiffState> filter) {
		if (filter == null) {
			return files;
		}

		Collection<ClangtidyWorkspaceFile> result = new ArrayList<ClangtidyWorkspaceFile>();

		for (ClangtidyWorkspaceFile file : files) {
			if (filter.contains(file.getDiffState())) {
				result.add(file);
			}
		}

		return result;
	}

	/**
	 * Gets the remote API for the build result.
	 *
	 * @return the remote API
	 */
	public Api getApi() {
		return new Api(getStatistics());
	}

	public ClangtidySourceContainer getClangtidySourceContainer() {
		return lazyLoadSourceContainer();
	}

	/**
	 * Get differences between current and previous statistics.
	 *
	 * @return the differences
	 */
	public ClangtidyStatistics getDiff() {
		ClangtidyStatistics current = getStatistics();
		ClangtidyResult previousResult = getPreviousResult();

		if (previousResult == null) {
			return new ClangtidyStatistics();
		}

		ClangtidyStatistics previous = previousResult.getStatistics();

		return new ClangtidyStatistics(current.getNumberErrorSeverity() - previous.getNumberErrorSeverity(),
				current.getNumberWarningSeverity() - previous.getNumberWarningSeverity(),
				current.getNumberBoostWarning() - previous.getNumberBoostWarning(),
				current.getNumberCertWarning() - previous.getNumberCertWarning(),
				current.getNumberCppcoreguidelinesWarning() - previous.getNumberCppcoreguidelinesWarning(),
				current.getNumberClangAnalyzerWarning() - previous.getNumberClangAnalyzerWarning(),
				current.getNumberClangDiagnosticWarning() - previous.getNumberClangDiagnosticWarning(),
				current.getNumberGoogleWarning() - previous.getNumberGoogleWarning(),
				current.getNumberLlvmWarning() - previous.getNumberLlvmWarning(),
				current.getNumberMiscWarning() - previous.getNumberMiscWarning(),
				current.getNumberModernizeWarning() - previous.getNumberModernizeWarning(),
				current.getNumberMpiWarning() - previous.getNumberMpiWarning(),
				current.getNumberPerformanceWarning() - previous.getNumberPerformanceWarning(),
				current.getNumberReadabilityWarning() - previous.getNumberReadabilityWarning(), current.getVersions());
	}

	/**
	 * Gets the dynamic result of the selection element.
	 *
	 * @param link
	 *            the link to identify the sub page to show
	 * @param request
	 *            Stapler request
	 * @param response
	 *            Stapler response
	 * @return the dynamic result of the analysis (detail page).
	 * @throws java.io.IOException
	 *             if an error occurs
	 */
	public Object getDynamic(final String link, final StaplerRequest request, final StaplerResponse response)
			throws IOException {
		if (link.equals("source.all")) {
			if (!owner.getProject().getACL().hasPermission(Item.WORKSPACE)) {
				response.sendRedirect2("nosourcepermission");
				return null;
			}

			Set<ClangtidyDiffState> filter = parseStatesFilter(request.getParameter("states"));
			Collection<ClangtidyWorkspaceFile> files = diffCurrentAndPrevious(filter);
			int before = parseIntWithDefault(request.getParameter("before"), 5);
			int after = parseIntWithDefault(request.getParameter("after"), 5);

			return new ClangtidySourceAll(owner, files, before, after);
		} else if (link.startsWith("source.")) {
			if (!owner.getProject().getACL().hasPermission(Item.WORKSPACE)) {
				response.sendRedirect2("nosourcepermission");
				return null;
			}

			Map<Integer, ClangtidyWorkspaceFile> agregateMap = getClangtidySourceContainer().getInternalMap();

			if (agregateMap != null) {
				ClangtidyWorkspaceFile vClangtidyWorkspaceFile = agregateMap
						.get(Integer.parseInt(StringUtils.substringAfter(link, "source.")));

				if (vClangtidyWorkspaceFile == null) {
					throw new IllegalArgumentException("Error for retrieving the source file with link:" + link);
				}

				return new ClangtidySource(owner, vClangtidyWorkspaceFile);
			}
		}
		return null;
	}

	/**
	 * Gets the number of errors according the selected severities form the
	 * configuration user object.
	 *
	 * @param severityEvaluation
	 *            the severity evaluation configuration object
	 * @param checkNewError
	 *            true, if the request is for the number of new errors
	 * @return the number of errors or new errors (if checkNewEroor is set to
	 *         true) for the current configuration object
	 * @throws java.io.IOException
	 *             if an error occurs
	 */
	public int getNumberErrorsAccordingConfiguration(ClangtidyConfigSeverityEvaluation severityEvaluation,
			boolean checkNewError) throws IOException {

		if (severityEvaluation == null) {
			throw new IOException(
					"[ERROR] - The clangtidy configuration file is missing. Could you save again your job configuration.");
		}

		int nbErrors = 0;
		int nbPreviousError = 0;
		ClangtidyResult previousResult = getPreviousResult();

		ClangtidyStatistics st = getStatistics();
		ClangtidyStatistics prev = (previousResult != null) ? previousResult.getStatistics() : null;

		// Error
		if (severityEvaluation.isSeverityError()) {
			nbErrors += st.getNumberErrorSeverity();
			if (previousResult != null) {
				nbPreviousError += prev.getNumberErrorSeverity();
			}
		}

		// Warnings
		if (severityEvaluation.isSeverityWarning()) {
			nbErrors += st.getNumberWarningSeverity();
			if (previousResult != null) {
				nbPreviousError += prev.getNumberWarningSeverity();
			}
		}

		if (checkNewError) {
			if (previousResult != null) {
				return nbErrors - nbPreviousError;
			} else {
				return 0;
			}
		} else {
			return nbErrors;
		}
	}

	/**
	 * Returns the number of new errors from the previous build result.
	 *
	 * @return the number of new errors
	 */
	public int getNumberNewErrorsFromPreviousBuild() {
		ClangtidyResult previousClangtidyResult = getPreviousResult();
		if (previousClangtidyResult == null) {
			return 0;
		} else {
			int diff = getReport().getNumberTotal() - previousClangtidyResult.getReport().getNumberTotal();
			return (diff > 0) ? diff : 0;
		}
	}

	public AbstractBuild<?, ?> getOwner() {
		return owner;
	}

	/**
	 * Gets the previous Action for the build result.
	 *
	 * @return the previous Clangtidy Build Action
	 */
	private ClangtidyBuildAction getPreviousAction() {
		AbstractBuild<?, ?> previousBuild = owner.getPreviousBuild();
		if (previousBuild != null) {
			return previousBuild.getAction(ClangtidyBuildAction.class);
		}
		return null;
	}

	/**
	 * Gets the previous Clangtidy result for the build result.
	 *
	 * @return the previous Clangtidy result or null
	 */
	public ClangtidyResult getPreviousResult() {
		ClangtidyBuildAction previousAction = getPreviousAction();
		ClangtidyResult previousResult = null;
		if (previousAction != null) {
			previousResult = previousAction.getResult();
		}

		return previousResult;
	}

	@Exported
	public ClangtidyStatistics getReport() {
		return getStatistics();
	}

	/**
	 * Get the statistics.
	 *
	 * @return the statistics, always non-null value should be returned
	 */
	@Exported
	public ClangtidyStatistics getStatistics() {
		return statistics;
	}

	/**
	 * Lazy load source container data if they are not already loaded.
	 *
	 * @return the loaded and parsed data or empty object on error
	 */
	private ClangtidySourceContainer lazyLoadSourceContainer() {

		XmlFile xmlSourceContainer = new XmlFile(new File(owner.getRootDir(), ClangtidyPublisher.XML_FILE_DETAILS));
		try {
			return (ClangtidySourceContainer) xmlSourceContainer.read();
		} catch (IOException e) {
			return new ClangtidySourceContainer(new HashMap<Integer, ClangtidyWorkspaceFile>());
		}
	}

	/**
	 * Parse integer.
	 *
	 * @param str
	 *            the input string
	 * @param defaultValue
	 *            the default value returned on error
	 * @return the parsed value or default value on error
	 * @see Integer#parseInt(String)
	 */
	private int parseIntWithDefault(String str, int defaultValue) {
		try {
			return Integer.parseInt(str);
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}

	/**
	 * Parse list of states.
	 *
	 * @param states
	 *            comma separated list of states (will be transformed to
	 *            uppercase)
	 * @return the parsed value or null if input is null
	 */
	private Set<ClangtidyDiffState> parseStatesFilter(String states) {
		if (states == null) {
			return null;
		}

		Set<ClangtidyDiffState> result = new HashSet<ClangtidyDiffState>();

		for (String state : states.toUpperCase().split(",")) {
			try {
				result.add(ClangtidyDiffState.valueOf(state));
			} catch (IllegalArgumentException e) {
				// Ignore, input was broken
			}
		}

		return result;
	}

	/**
	 * Convert legacy data in format of clangtidy plugin version 1.14 to the new
	 * one that uses statistics.
	 *
	 * @return this with optionally updated data
	 */
	private Object readResolve() {

		// Just for sure
		if (statistics == null) {
			statistics = new ClangtidyStatistics();
		}

		return this;
	}
}
