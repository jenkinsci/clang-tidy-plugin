package org.jenkinsci.plugins.clangtidy.util;

import hudson.model.BuildListener;

import java.io.Serializable;

/**
 * @author Gregory Boissinot
 */
public class ClangtidyLogger implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Log output to the given logger, using the Clangtidy identifier
	 *
	 * @param listener
	 *            The current listener
	 * @param message
	 *            The message to be outputted
	 */
	public static void log(BuildListener listener, final String message) {
		listener.getLogger().println("[Clangtidy] " + message);
	}

}