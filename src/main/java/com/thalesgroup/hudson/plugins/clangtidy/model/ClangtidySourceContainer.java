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

package com.thalesgroup.hudson.plugins.clangtidy.model;

import com.thalesgroup.hudson.plugins.clangtidy.util.ClangtidyLogger;

import hudson.FilePath;
import hudson.model.BuildListener;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClangtidySourceContainer implements Serializable {
    /** Serial version UID. */
    private static final long serialVersionUID = 1L;

    private Map<Integer, ClangtidyWorkspaceFile> internalMap = new HashMap<Integer, ClangtidyWorkspaceFile>();

    public ClangtidySourceContainer(BuildListener listener, FilePath basedir, List<ClangtidyFile> files) throws IOException, InterruptedException {
        int key = 1;
        for (ClangtidyFile clangtidyFile : files) {

            ClangtidyWorkspaceFile clangtidyWorkspaceFile = new ClangtidyWorkspaceFile();

            String clangtidyFileName = clangtidyFile.getFileName();
            if (clangtidyFileName == null) {
                clangtidyWorkspaceFile.setFileName(null);
                clangtidyWorkspaceFile.setSourceIgnored(true);
            } else {
                FilePath sourceFilePath = new FilePath(basedir, clangtidyFileName);
                if (!sourceFilePath.exists()) {
                    ClangtidyLogger.log(listener, "[WARNING] - The source file '" + sourceFilePath.toURI() + "' doesn't exist on the slave. The ability to display its source code has been removed.");
                    clangtidyWorkspaceFile.setFileName(null);
                    clangtidyWorkspaceFile.setSourceIgnored(true);
                } else if (sourceFilePath.isDirectory()) {
                    clangtidyWorkspaceFile.setFileName(sourceFilePath.getRemote());
                    clangtidyWorkspaceFile.setSourceIgnored(true);
                } else {
                    clangtidyWorkspaceFile.setFileName(sourceFilePath.getRemote());
                    clangtidyWorkspaceFile.setSourceIgnored(false);
                }
            }

            //The key must be unique for all the files/errors through the merge
            clangtidyFile.setKey(key);
            clangtidyWorkspaceFile.setClangtidyFile(clangtidyFile);
            internalMap.put(key, clangtidyWorkspaceFile);
            ++key;
        }
    }

    public Map<Integer, ClangtidyWorkspaceFile> getInternalMap() {
        return internalMap;
    }
}
