package net.ssehub.comani.extraction.git;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Arrays;

import org.junit.BeforeClass;
import org.junit.Test;

import net.ssehub.comani.extraction.AllTests;
import net.ssehub.comani.extraction.ExtractionSetupException;

/**
 * This class contains positive tests for the extraction of specific commit ids of a given, non-empty repository using 
 * the {@link net.ssehub.comani.extraction.git.GitCommitExtractor#extract(java.io.File, java.util.List)} method.
 * 
 * @author Marcel
 *
 */
public class PartialExtractionValidRepositoryTests extends AbstractRepositoryTests {
    /**
     * The directory denoting the repository from which the commits for the tests in this class shall be extracted.
     * Here, this repository is contains 4 commits.
     */
    private static final File REPOSITORY_DIRECTORY = new File(AllTests.TESTDATA, "repositories/extractionRepo");
    
    /**
     * The set of IDs which should be extracted from the repository in the {@link #REPOSITORY_DIRECTORY}.
     * It is the wanted commit id list as well.
     */
    private static final String[] COMMIT_EXTRACT_LIST = {"a9c1b95", "7d485f6", "b38ba5d"};
    
   /** 
    * The expected number of commits extracted from the repository in the {@link #REPOSITORY_DIRECTORY}. As for these
    * tests the repository contains 4 commits, the expected number of extracted commits is <tt>3</tt>.  
    */
    private static final int EXPECTED_COMMITS_COUNT = COMMIT_EXTRACT_LIST.length;
    
    /**
     * Prepares the required attributes for the test of this class by calling {@link #prepare(File, java.util.List)} of
     * the super-class with {@link #REPOSITORY_DIRECTORY} and {@link #COMMIT_EXTRACT_LIST}.
     * @throws ExtractionSetupException if instantiating the commit extractor failed
     */
    @BeforeClass
    public static void prepare() throws ExtractionSetupException {
        prepare(REPOSITORY_DIRECTORY, Arrays.asList(COMMIT_EXTRACT_LIST));
    }
    
    /**
     * Tests whether the extraction process terminated successfully when extracting specific commit id 
     * of the repository.
     */
    // TODO @CK this test succeed even though the given repository does not exists.
    @Test
    public void testExtractionSucessful() {
        String commits = "";
        for (String id : COMMIT_EXTRACT_LIST) {
            commits += id + ", ";
        }
        assertTrue("The extraction of commits " + commits + " from " + REPOSITORY_DIRECTORY.getAbsolutePath() 
            + " should be successful",
                extractionTerminatedSuccessful);
    }
    
   /**
    * Tests whether the number of extracted commits matches the number of wanted commits from the repository.
    */
    @Test
    public void testCommitNumberEquals() {
        assertEquals("The number of extracted commits from " + REPOSITORY_DIRECTORY.getAbsolutePath() 
            + " is not correct", EXPECTED_COMMITS_COUNT, extractedCommits.size());
    }
    
    /**
     * Tests whether the ids of the extracted commits match the ids of the wanted commits in the repository. 
     */
    @Test
    public void testExtractionOfCorrectCommits() {
        for (int i = 0; i < COMMIT_EXTRACT_LIST.length; i++) {
            String expectedCommitId = COMMIT_EXTRACT_LIST[i];
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
