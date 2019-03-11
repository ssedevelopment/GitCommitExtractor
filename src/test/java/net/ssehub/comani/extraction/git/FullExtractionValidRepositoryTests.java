package net.ssehub.comani.extraction.git;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.BeforeClass;
import org.junit.Test;

import net.ssehub.comani.extraction.AllTests;
import net.ssehub.comani.extraction.ExtractionSetupException;

/**
 * This class contains positive tests for the extraction of all commits of a given, non-empty repository using the
 * {@link net.ssehub.comani.extraction.git.GitCommitExtractor#extract(java.io.File)} method.
 * 
 * @author Christian Kröher
 *
 */
public class FullExtractionValidRepositoryTests extends AbstractRepositoryTests {
    
    /**
     * The directory denoting the repository from which the commits for the tests in this class shall be extracted.
     * Here, this repository is contains 4 commits.
     */
    private static final File REPOSITORY_DIRECTORY = new File(AllTests.TESTDATA, "repositories/extractionRepo");
    
    /**
     * The set of expected IDs of the commits extracted from the repository in the {@link #REPOSITORY_DIRECTORY}. The
     * order or IDs in this set doesn't match with the order in which the commits are extracted.
     */
    private static final String[] EXPECTED_COMMIT_IDS = {"b38ba5d", "a9c1b95", "dfcd771", "7d485f6", "d169091", 
        "5ecf0bb"};
    
    /**
     * The expected number of commits extracted from the repository in the {@link #REPOSITORY_DIRECTORY}. As for these
     * tests the repository contains 4 commits, the expected number of extracted commits is <tt>4</tt>.  
     */
    private static final int EXPECTED_COMMITS_COUNT = EXPECTED_COMMIT_IDS.length;

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
     * Tests whether the extraction process terminated successfully when extracting all commits of the repository.
     */
    @Test
    public void testExtractionSuccessful() {
        assertTrue("The extraction of commits from " + REPOSITORY_DIRECTORY.getAbsolutePath() + " should be successful",
                extractionTerminatedSuccessful);
    }

    /**
     * Tests whether the number of extracted commits matches the number of available commits in the repository.
     */
    @Test
    public void testCommitNumbersEqual() {
        assertEquals("The number of extracted commits from " + REPOSITORY_DIRECTORY.getAbsolutePath() 
                + " is not correct", EXPECTED_COMMITS_COUNT, extractedCommits.size());
    }
    
    /**
     * Tests whether the ids of the extracted commits match the ids of the commits in the repository. 
     */
    @Test
    public void testExtractionOfCorrectCommits() {       
        for (int i = 0; i < EXPECTED_COMMIT_IDS.length; i++) {
            String expectedCommitId = EXPECTED_COMMIT_IDS[i];
            int extractedCommitCounter = 0;
            boolean expectedCommitFound = false;
            while (!expectedCommitFound && extractedCommitCounter < extractedCommits.size()) {
                expectedCommitFound = extractedCommits.get(extractedCommitCounter).getId().equals(expectedCommitId);
                extractedCommitCounter++;
            }
            assertTrue("Expected commit \"" + expectedCommitId + "\"  was not found in extracted commit list", 
                    expectedCommitFound);
        }
    }
}
