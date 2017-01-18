package org.jenkinsci.plugins.clangtidy;

import org.kohsuke.stapler.DataBoundConstructor;

import hudson.Extension;
import hudson.model.Descriptor;
import hudson.model.Job;
import hudson.model.Run;
import hudson.plugins.view.dashboard.DashboardPortlet;

/**
 * Dashboard portlet that shows a sortable table with jobs and Clangtidy
 * statistics per severity type.
 *
 * @author Michal Turek
 */
public class ClangtidyTablePortlet extends DashboardPortlet {
	/**
	 * Extension point registration.
	 *
	 * @author Michal Turek
	 */
	@Extension(optional = true)
	public static class ClangtidyTableDescriptor extends Descriptor<DashboardPortlet> {
		@Override
		public String getDisplayName() {
			return Messages.clangtidy_PortletName();
		}
	}

	/**
	 * Constructor.
	 *
	 * @param name
	 *            the name of the portlet
	 */
	@DataBoundConstructor
	public ClangtidyTablePortlet(String name) {
		super(name);
	}

	/**
	 * Get latest available Clangtidy statistics of a job.
	 *
	 * @param job
	 *            the job
	 * @return the statistics, always non-null value
	 */
	public ClangtidyStatistics getStatistics(Job<?, ?> job) {
		Run<?, ?> build = job.getLastBuild();

		while (build != null) {
			ClangtidyBuildAction action = build.getAction(ClangtidyBuildAction.class);

			if (action != null) {
				ClangtidyResult result = action.getResult();

				if (result != null) {
					return result.getStatistics();
				}
			}

			build = build.getPreviousBuild();
		}

		return new ClangtidyStatistics();
	}
}
