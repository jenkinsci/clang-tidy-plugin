package com.thalesgroup.hudson.plugins.clangtidy;

import com.thalesgroup.hudson.plugins.clangtidy.config.ClangtidyConfig;
import org.apache.commons.lang.StringUtils;

public class ClangtidyMetricUtil {

    public static int convert(String threshold) {
        if (isValid(threshold)) {
            if (StringUtils.isNotBlank(threshold)) {
                try {
                    return Integer.parseInt(threshold);
                } catch (NumberFormatException exception) {
                    // not valid
                }
            }
        }
        throw new IllegalArgumentException("Not a parsable integer value >= 0: " + threshold);
    }

    public static boolean isValid(final String threshold) {
        if (StringUtils.isNotBlank(threshold)) {
            try {
                return Integer.parseInt(threshold) >= 0;
            } catch (NumberFormatException exception) {
                // not valid
            }
        }
        return false;
    }

    public static String getMessageSelectedSeverties(ClangtidyConfig clangtidyConfig) {
        StringBuffer sb = new StringBuffer();

        if (clangtidyConfig.getConfigSeverityEvaluation().isAllSeverities()) {
            sb.append("with all severities");
            return sb.toString();
        }

        if (clangtidyConfig.getConfigSeverityEvaluation().isSeverityError()) {
            sb.append(" and ");
            sb.append("severity 'error'");
        }

        if (clangtidyConfig.getConfigSeverityEvaluation().isSeverityPossibleError()) {
            sb.append(" and ");
            sb.append("severity 'possible error'");
        }


        if (clangtidyConfig.getConfigSeverityEvaluation().isSeverityPossibleStyle()) {
            sb.append(" and ");
            sb.append("severity 'possible style'");
        }


        if (clangtidyConfig.getConfigSeverityEvaluation().isSeverityStyle()) {
            sb.append(" and ");
            sb.append("severity 'style'");
        }

        if (sb.length() != 0)
            sb.delete(0, 5);

        return sb.toString();
    }

}
