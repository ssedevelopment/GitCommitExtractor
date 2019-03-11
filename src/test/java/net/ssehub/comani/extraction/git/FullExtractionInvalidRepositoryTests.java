package net.ssehub.comani.extraction.git;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.BeforeClass;
import org.junit.Test;

import net.ssehub.comani.extraction.AllTests;
import net.ssehub.comani.extraction.ExtractionSetupException;

/**
 * Tests for {@link GitCommitExtractor#extract(java.io.File)} with non-existing /invalid repository path. 
 * 
 * @author Marcel
 */
public class FullExtractionInvalidRepositoryTests extends AbstractRepositoryTests {
   
    /**
     * A invalid path to an non existing repository. 
     */
    private static final File REPOSITORY_DIRECTORY = new File(AllTests.TESTDATA, "repositories/noRepoHere");
    
    /**
     * The expected number of commits extracted from the repository in the {@link #REPOSITORY_DIRECTORY}. As for these
     * tests there is no repository, the expected number of extracted commits is <tt>0</tt>.  
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
     * Tests if the extraction of the non existing repository fails. 
     */
    @Test
    public void testUnsuccessfulExtraction() {
        assertFalse("The extraction of commits from " + REPOSITORY_DIRECTORY.getAbsolutePath() + " should be "
                + "unsuccessful",
                extractionTerminatedSuccessful);
    }
    
    /**
     * Tests whether the number of extracted commits are equal to {@link #EXPECTED_COMMITS_COUNT}.
     */
    @Test
    public void testCommitNumbersEqual() {
        assertEquals("The number of extracted commits from " + REPOSITORY_DIRECTORY.getAbsolutePath() 
                + " is not correct", EXPECTED_COMMITS_COUNT, extractedCommits.size());
    }
    
    /**
     * Tests if the list of extracted commit is empty. There are no commits to extract in non existing repository.
     */
    @Test
    public void testExtractionCommitsOfEmpty() {
        assertTrue("The list of extracted commits is not empty but it should be ", extractedCommits.isEmpty());
    }

}
