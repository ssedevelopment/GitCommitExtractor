package net.ssehub.comani.extraction.git;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import net.ssehub.comani.extraction.AllTests;
import net.ssehub.comani.extraction.ExtractionSetupException;

/**
 * This class contains negative tests "using" a non-existing repository path 
 * for {@link GitCommitExtractor#extract(java.io.File, java.util.List)}.
 * 
 * @author Marcel
 */
public class PartialExtractionInvalidRepositoryTests extends AbstractRepositoryTests {
    /**
     * A invalid path to an non existing repository. 
     */
    private static final File REPOSITORY_DIRECTORY = new File(AllTests.TESTDATA, "repositories/noRepoHere");
    
    /**
     * The set of IDs which should be extracted from the repository in the {@link #REPOSITORY_DIRECTORY}.
     * The list is empty because there can't be any commits in a non existing repository. 
     */
    private static final List<String> COMMIT_EXTRACT_LIST = new ArrayList<String>();
    
    /**
     * The expected number of commits extracted from the repository in the {@link #REPOSITORY_DIRECTORY}. As for these
     * tests there is no repository, the expected number of extracted commits is <tt>0</tt>.  
     */
    private static final int EXPECTED_COMMITS_COUNT = 0;
    
    /**
     * Prepares the required attributes for the tests of this class by calling {@link #prepare(File, List)}
     * of the super-class with {@link #REPOSITORY_DIRECTORY}.
     * 
     * @throws ExtractionSetupException if instantiating the commit extractor failed
     */
    @BeforeClass
    public static void prepare() throws ExtractionSetupException {
        prepare(REPOSITORY_DIRECTORY, COMMIT_EXTRACT_LIST);
    }
    
    /**
     * Tests if the extraction of the non existing repository fails. 
     */
    @Test
    @Ignore("TODO this should fail if no repository exists on the given path")
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
