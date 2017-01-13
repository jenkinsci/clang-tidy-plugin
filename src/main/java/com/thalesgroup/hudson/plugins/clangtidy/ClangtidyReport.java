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

import com.thalesgroup.hudson.plugins.clangtidy.model.ClangtidyFile;
import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@ExportedBean
public class ClangtidyReport implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<ClangtidyFile> everySeverities = new ArrayList<ClangtidyFile>();

    private List<ClangtidyFile> errorSeverities = new ArrayList<ClangtidyFile>();

    private List<ClangtidyFile> possibleErrorSeverities = new ArrayList<ClangtidyFile>();

    private List<ClangtidyFile> styleSeverities = new ArrayList<ClangtidyFile>();

    private List<ClangtidyFile> possibleStyleSeverities = new ArrayList<ClangtidyFile>();

    private List<ClangtidyFile> noCategorySeverities = new ArrayList<ClangtidyFile>();

    @Exported
    public List<ClangtidyFile> getEverySeverities() {
        return everySeverities;
    }

    public List<ClangtidyFile> getPossibleErrorSeverities() {
        return possibleErrorSeverities;
    }

    public void setPossibleErrorSeverities(List<ClangtidyFile> possibleErrorSeverities) {
        this.possibleErrorSeverities = possibleErrorSeverities;
    }

    public List<ClangtidyFile> getStyleSeverities() {
        return styleSeverities;
    }

    public void setStyleSeverities(List<ClangtidyFile> styleSeverities) {
        this.styleSeverities = styleSeverities;
    }

    public List<ClangtidyFile> getPossibleStyleSeverities() {
        return possibleStyleSeverities;
    }

    public void setPossibleStyleSeverities(List<ClangtidyFile> possibleStyleSeverities) {
        this.possibleStyleSeverities = possibleStyleSeverities;
    }

    public List<ClangtidyFile> getErrorSeverities() {
        return errorSeverities;
    }

    public void setErrorSeverities(List<ClangtidyFile> errorSeverities) {
        this.errorSeverities = errorSeverities;
    }

    public List<ClangtidyFile> getNoCategorySeverities() {
        return noCategorySeverities;
    }

    public void setNoCategorySeverities(List<ClangtidyFile> noCategorySeverities) {
        this.noCategorySeverities = noCategorySeverities;
    }

    public void setEverySeverities(List<ClangtidyFile> everySeverities) {
        this.everySeverities = everySeverities;
    }

    @Exported
    public int getNumberTotal() {
        return (everySeverities == null) ? 0 : everySeverities.size();
    }

    @Exported
    public int getNumberSeverityStyle() {
        return (styleSeverities == null) ? 0 : styleSeverities.size();
    }

    @Exported
    public int getNumberSeverityPossibleStyle() {
        return (possibleStyleSeverities == null) ? 0 : possibleStyleSeverities.size();
    }

    @Exported
    public int getNumberSeverityError() {
        return (errorSeverities == null) ? 0 : errorSeverities.size();
    }

    @Exported
    public int getNumberSeverityPossibleError() {
        return (possibleErrorSeverities == null) ? 0 : possibleErrorSeverities.size();
    }

    @Exported
    public int getNumberSeverityNoCategory() {
        return (noCategorySeverities == null) ? 0 : noCategorySeverities.size();
    }


    // Backward compatibility. Do not remove.
    // CPPCHECK:OFF
    @Deprecated
    private transient Map<Integer, ClangtidyFile> internalMap;

    /**
     * Initializes members that were not present in previous versions of this plug-in.
     *
     * @return the created object
     */
    private Object readResolve() {

        //For old compatibilty
        if (internalMap != null) {
            for (Map.Entry<Integer, ClangtidyFile> entry : internalMap.entrySet()) {

                ClangtidyFile clangtidyFile = entry.getValue();
                if ("possible error".equals(clangtidyFile.getSeverity())) {
                    possibleErrorSeverities.add(clangtidyFile);
                } else if ("style".equals(clangtidyFile.getSeverity())) {
                    styleSeverities.add(clangtidyFile);
                } else if ("possible style".equals(clangtidyFile.getSeverity())) {
                    possibleStyleSeverities.add(clangtidyFile);
                } else if ("error".equals(clangtidyFile.getSeverity())) {
                    errorSeverities.add(clangtidyFile);
                } else {
                    noCategorySeverities.add(clangtidyFile);
                }
                everySeverities.add(clangtidyFile);
            }
        }

        return this;
    }


}
