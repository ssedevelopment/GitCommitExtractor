package net.ssehub.comani.extraction.git;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.BeforeClass;
import org.junit.Test;

import net.ssehub.comani.extraction.ExtractionSetupException;

/**
 * This class contains negative tests for the extraction of all commits using the
 * {@link net.ssehub.comani.extraction.git.GitCommitExtractor#extract(java.io.File)} method with the parameter value
 * <code>null</code>.
 * 
 * @author Christian Kröher
 *
 */
public class FullExtractionNullRepositoryTests extends AbstractRepositoryTests {
    
    /**
     * The expected number of commits extracted from the repository. As for these tests the value <code>null</code> 
     * instead of a valid repository is used, the expected number of extracted commits is <tt>0</tt>.  
     */
    private static final int EXPECTED_COMMITS_COUNT = 0;

    /**
     * Prepares the required attributes for the tests of this class by calling {@link #prepare(java.io.File)} of the
     * super-class with the value <code>null</code>.
     * 
     * @throws ExtractionSetupException if instantiating the commit extractor failed
     */
    @BeforeClass
    public static void prepare() throws ExtractionSetupException {
        /*
         * TODO The preparation ends-up in an infinite loop because passing "null" results in executing the git commands
         * in the directory of the current Java process, which in turn is a git repository with more than 10 commits.
         * Hence, the extractor extracts those commits and as we do not remove those commits from the queue, it will
         * block the addition of new commits after the 10th commit is added.
         * 
         * Blocking the addition of new commits is correct, but it is the question whether allowing "null" and, hence, 
         * the directory of the current Java process in the ProcessUtilities is really required?
         */
        prepare(null);
    }
    
    /**
     * Tests whether the extraction process failed when extracting all commits of an undefined repository.
     */
    @Test
    public void testExtractionFailed() {
        assertFalse("The extraction of commits from an undefined repository (null) should fail",
                extractionTerminatedSuccessful);
    }

    /**
     * Tests whether there were no commits extracted.
     */
    @Test
    public void testNoCommitsExtracted() {
        assertEquals("The number of extracted commits from an undefined repository (null) is not correct",
                EXPECTED_COMMITS_COUNT, extractedCommits.size());
    }
}
