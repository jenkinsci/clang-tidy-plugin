package org.jenkinsci.plugins.clangtidy;

import com.thalesgroup.hudson.plugins.clangtidy.ClangtidyReport;
import com.thalesgroup.hudson.plugins.clangtidy.model.ClangtidyFile;
import com.thalesgroup.hudson.plugins.clangtidy.parser.ClangtidyParser;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.List;

/**
 * @author Gregory Boissinot
 */
public class ClangtidyParserTest {


    ClangtidyParser clangtidyParser;

    @Before
    public void setUp() throws Exception {
        clangtidyParser = new ClangtidyParser();
    }

    @Test
    public void testclangtidy1Version2() throws Exception {
        processClangtidy("version2/testClangtidy.xml", 16, 1, 13, 0, 2, 0);
    }

    private void processClangtidy(String filename,
                                 int nbErrors,
                                 int nbSeveritiesPossibleError,
                                 int nbSeveritiesPossibleStyle,
                                 int nbStyleErrors,
                                 int nbSeveritiesError,
                                 int nbSeveritiesNoCategory) throws Exception {

        ClangtidyReport clangtidyReport = clangtidyParser.parse(new File(ClangtidyParserTest.class.getResource(filename).toURI()));

        List<ClangtidyFile> everyErrors = clangtidyReport.getEverySeverities();
        List<ClangtidyFile> possibileErrorSeverities = clangtidyReport.getPossibleErrorSeverities();
        List<ClangtidyFile> styleErrors = clangtidyReport.getStyleSeverities();
        List<ClangtidyFile> possibleStyleSeverities = clangtidyReport.getPossibleStyleSeverities();
        List<ClangtidyFile> errorSeverities = clangtidyReport.getErrorSeverities();
        List<ClangtidyFile> noCategorySeverities = clangtidyReport.getNoCategorySeverities();

        assert possibileErrorSeverities != null;
        assert possibleStyleSeverities != null;
        assert errorSeverities != null;
        assert everyErrors != null;
        assert styleErrors != null;
        assert noCategorySeverities != null;

        Assert.assertEquals("Wrong computing of list of errors", everyErrors.size(),
                noCategorySeverities.size() + possibleStyleSeverities.size() + errorSeverities.size() + possibileErrorSeverities.size() + styleErrors.size());

        Assert.assertEquals("Wrong total number of errors", nbErrors, everyErrors.size());
        Assert.assertEquals("Wrong total number of errors for the severity 'possible error'", nbSeveritiesPossibleError, possibileErrorSeverities.size());
        Assert.assertEquals("Wrong total number of errors for the severity 'possible style'", nbSeveritiesPossibleStyle, possibleStyleSeverities.size());
        Assert.assertEquals("Wrong total number of errors for the severity 'style'", nbStyleErrors, styleErrors.size());
        Assert.assertEquals("Wrong total number of errors for the severity 'error'", nbSeveritiesError, errorSeverities.size());
        Assert.assertEquals("Wrong total number of errors with no category", nbSeveritiesNoCategory, noCategorySeverities.size());
    }

}
