package org.jenkinsci.plugins.clangtidy.util;

import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Actionable;
import hudson.model.Action;

import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import java.io.IOException;

/**
 * @author Gregory Boissinot
 */
public abstract class AbstractClangtidyProjectAction extends Actionable implements Action {

	protected final AbstractProject<?, ?> project;

	public AbstractClangtidyProjectAction(AbstractProject<?, ?> project) {
		this.project = project;
	}

	public abstract void doGraph(StaplerRequest req, StaplerResponse rsp) throws IOException;

	public void doIndex(StaplerRequest req, StaplerResponse rsp) throws IOException {
		Integer buildNumber = getLastResultBuild();
		if (buildNumber == null) {
			rsp.sendRedirect2("nodata");
		} else {
			rsp.sendRedirect2("../" + buildNumber + "/" + getUrlName());
		}
	}

	@Override
	public String getIconFileName() {
		return "/plugin/clangtidy/icons/clangtidy-24.png";
	}

	protected abstract AbstractBuild<?, ?> getLastFinishedBuild();

	protected abstract Integer getLastResultBuild();

	public AbstractProject<?, ?> getProject() {
		return project;
	}

	@Override
	public String getSearchUrl() {
		return getUrlName();
	}
}
