package net.ssehub.comani.extraction.git;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.File;

import org.junit.BeforeClass;
import org.junit.Test;

import net.ssehub.comani.extraction.AllTests;
import net.ssehub.comani.extraction.ExtractionSetupException;

/**
 * This class contains negative tests for the extraction of all commits of a given, empty repository using the
 * {@link net.ssehub.comani.extraction.git.GitCommitExtractor#extract(java.io.File)} method.
 * 
 * @author Christian Kroeher
 *
 */
public class FullExtractionEmptyRepositoryTests extends AbstractRepositoryTests {

    /**
     * The directory denoting the repository from which the commits for the tests in this class shall be extracted.
     * Here, this repository is empty, which means that there are no commits available.
     */
    private static final File REPOSITORY_DIRECTORY = new File(AllTests.TESTDATA, "repositories/extractionEmptyRepo");
    
    /**
     * The expected number of commits extracted from the repository in the {@link #REPOSITORY_DIRECTORY}. As for these
     * tests the repository is empty, the expected number of extracted commits is <tt>0</tt>.  
     */
    private static final int EXPECTED_COMMITS_COUNT = 0;

    /**
     * Prepares the required attributes for the tests of this class by calling {@link #prepare(File)} of the super-class
     * with {@link #REPOSITORY_DIRECTORY}.
     * 
     * @throws ExtractionSetupException if instantiating the commit extractor failed
     */
    @BeforeClass
    public static void prepare() throws ExtractionSetupException {
        prepare(REPOSITORY_DIRECTORY);
    }
    
    /**
     * Tests whether the extraction process failed when extracting all commits of the empty repository.
     */
    @Test
    public void testExtractionFailed() {
        assertFalse("The extraction of commits from " + REPOSITORY_DIRECTORY.getAbsolutePath() + " should fail",
                extractionTerminatedSuccessful);
    }

    /**
     * Tests whether there were no commits extracted.
     */
    @Test
    public void testNoCommitsExtracted() {
        assertEquals("The number of extracted commits from " + REPOSITORY_DIRECTORY.getAbsolutePath() 
                + " is not correct", EXPECTED_COMMITS_COUNT, extractedCommits.size());
    }
}
