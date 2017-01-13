/*******************************************************************************
 * Copyright (c) 2009 Thales Corporate Services SAS                             *
 * Author : Gregory Boissinot                                                   *
 *                                                                              *
 * Permission is hereby granted, free of charge, to any person obtaining a copy *
 * of this software and associated documentation files (the "Software"), to deal*
 * in the Software without restriction, including without limitation the rights *
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell    *
 * copies of the Software, and to permit persons to whom the Software is        *
 * furnished to do so, subject to the following conditions:                     *
 *                                                                              *
 * The above copyright notice and this permission notice shall be included in   *
 * all copies or substantial portions of the Software.                          *
 *                                                                              *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR   *
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,     *
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE  *
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER       *
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,*
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN    *
 * THE SOFTWARE.                                                                *
 *******************************************************************************/

package com.thalesgroup.hudson.plugins.clangtidy;

public class ClangtidySummary {

    private ClangtidySummary() {
    }


    /**
     * Creates an HTML Clangtidy summary.
     *
     * @param result the clangtidy result object
     * @return the HTML fragment representing the clangtidy report summary
     */
    public static String createReportSummary(ClangtidyResult result) {

        StringBuilder summary = new StringBuilder();
        int nbErrors = result.getReport().getNumberTotal();

        summary.append(Messages.clangtidy_Errors_ProjectAction_Name());
        summary.append(": ");
        if (nbErrors == 0) {
            summary.append(Messages.clangtidy_ResultAction_NoError());
        } else {
            summary.append("<a href=\"" + ClangtidyBuildAction.URL_NAME + "\">");

            if (nbErrors == 1) {
                summary.append(Messages.clangtidy_ResultAction_OneError());
            } else {
                summary.append(Messages.clangtidy_ResultAction_MultipleErrors(nbErrors));
            }
            summary.append("</a>");
        }
        summary.append(".");

        return summary.toString();
    }


    /**
     * Creates an HTML Clangtidy detailed summary.
     *
     * @param result the clangtidy result object
     * @return the HTML fragment representing the clangtidy report details summary
     */
    public static String createReportSummaryDetails(ClangtidyResult result) {

        StringBuilder builder = new StringBuilder();
        int nbNewErrors = result.getNumberNewErrorsFromPreviousBuild();

        builder.append("<li>");

        if (nbNewErrors == 0) {
            builder.append(Messages.clangtidy_ResultAction_Detail_NoNewError());
        } else if (nbNewErrors == 1) {
            builder.append(Messages.clangtidy_ResultAction_Detail_NewOneError());
        } else {
            builder.append(Messages.clangtidy_ResultAction_Detail_NewMultipleErrors());
            builder.append(": ");
            builder.append(nbNewErrors);
        }
        builder.append("</li>");

        return builder.toString();
    }
}
