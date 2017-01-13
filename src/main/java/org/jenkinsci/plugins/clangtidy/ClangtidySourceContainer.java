package org.jenkinsci.plugins.clangtidy;

import com.thalesgroup.hudson.plugins.clangtidy.model.ClangtidyFile;
import com.thalesgroup.hudson.plugins.clangtidy.model.ClangtidyWorkspaceFile;
import hudson.FilePath;
import hudson.model.BuildListener;
import org.jenkinsci.plugins.clangtidy.util.ClangtidyLogger;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Gregory Boissinot
 */
public class ClangtidySourceContainer {

    private Map<Integer, ClangtidyWorkspaceFile> internalMap = new HashMap<Integer, ClangtidyWorkspaceFile>();

    public ClangtidySourceContainer(Map<Integer, ClangtidyWorkspaceFile> internalMap) {
        this.internalMap = internalMap;
    }

    public ClangtidySourceContainer(BuildListener listener,
                                   FilePath workspace,
                                   FilePath scmRootDir,
                                   List<ClangtidyFile> files) throws IOException, InterruptedException {

        int key = 1;
        for (ClangtidyFile clangtidyFile : files) {
            ClangtidyWorkspaceFile clangtidyWorkspaceFile = getClangtidyWorkspaceFile(listener, workspace, scmRootDir, clangtidyFile);
            //The key must be unique for all the files/errors through the merge
            clangtidyFile.setKey(key);
            clangtidyWorkspaceFile.setClangtidyFile(clangtidyFile);
            internalMap.put(key, clangtidyWorkspaceFile);
            ++key;
        }
    }

    private ClangtidyWorkspaceFile getClangtidyWorkspaceFile(BuildListener listener,
                                                           FilePath workspace,
                                                           FilePath scmRootDir,
                                                           ClangtidyFile clangtidyFile) throws IOException, InterruptedException {

        String clangtidyFileName = clangtidyFile.getFileName();

        if (clangtidyFileName == null) {
            ClangtidyWorkspaceFile clangtidyWorkspaceFile = new ClangtidyWorkspaceFile();
            clangtidyWorkspaceFile.setFileName(null);
            clangtidyWorkspaceFile.setSourceIgnored(true);
            return clangtidyWorkspaceFile;
        }

        ClangtidyWorkspaceFile clangtidyWorkspaceFile = new ClangtidyWorkspaceFile();
        FilePath sourceFilePath = getSourceFile(workspace, scmRootDir, clangtidyFileName);
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

        return clangtidyWorkspaceFile;
    }

    private FilePath getSourceFile(FilePath workspace, FilePath scmRootDir, String clangtidyFileName) throws IOException, InterruptedException {
        FilePath sourceFilePath = new FilePath(scmRootDir, clangtidyFileName);
        if (!sourceFilePath.exists()) {
            //try from workspace
            sourceFilePath = new FilePath(workspace, clangtidyFileName);
        }
        return sourceFilePath;
    }


    public Map<Integer, ClangtidyWorkspaceFile> getInternalMap() {
        return internalMap;
    }

}
