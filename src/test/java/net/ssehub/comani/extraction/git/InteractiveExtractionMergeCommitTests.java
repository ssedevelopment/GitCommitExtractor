package net.ssehub.comani.extraction.git;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import net.ssehub.comani.extraction.AllTests;
import net.ssehub.comani.extraction.ExtractionSetupException;

/**
 * This class tests "merge" commits through the interactive mode {@link GitCommitExtractor#extract(String)}. <br>
 * This class runs with {@link Parameterized}.
 * 
 * @author Marcel
 */
@RunWith(Parameterized.class)
public class InteractiveExtractionMergeCommitTests extends AbstractInteractiveModeTests {
    
    /**
     * Contains the expected commit header as a string-list read from a file where the informations are stored. 
     * 
     * @see #InteractiveExtractionMergeCommitTests(CommitTestData, String, String)
     */
    private List<String> expectedCommitHeader;
    
    /**
     * Expected commit date which is compared to the extracted commit date. 
     * Should be given through a parameter. <br>
     * Currently not very useful -> in interactive mode, the commit date is always <code> no_date </code>
     * 
     * @see #testData()
     */
    private String expectedCommitDate;
    
    /**
     * Expected commit id which is compared to the extracted commit id. 
     * Should be given through a parameter.
     * 
     * @see #testData()
     */
    private String expectedCommitId;
    
    /**
     * Prepares one test-run with given parameters and calls super constructor which extracts the given commit.
     * 
     * @param commitData a {@link CommitTestData} object with specifies the desired commit for extraction 
     * @param expectedHeaderName the expected header of the commit
     * @param expectedCommitDate the expected commit date 
     * @throws ExtractionSetupException if the extraction of the commit goes wrong
     * @see {@link #testData()}
     * @see {@link AbstractInteractiveModeTests#AbstractInteractiveModeTests(File)}
     */
    public InteractiveExtractionMergeCommitTests(
            CommitTestData commitData, 
            String expectedHeaderName,
            String expectedCommitDate) 
            throws ExtractionSetupException {
        
        super(commitData.getCommitFile());
        
        File headerFile = new File(commitData.getCommitFile().getParent(), expectedHeaderName);
        this.expectedCommitHeader = loadContent(headerFile);
        this.expectedCommitId = commitData.getCommitId();
        this.expectedCommitDate = expectedCommitDate;
    }
    
    /**
     * Sets the parameter for parameterized tests. <br>
     * {0} {@link CommitTestData} object which contains path and commit id <br>
     * {1} filename which contains the expected header of the merge commit <br>
     * {2} expected date of the commit
     * @return the parameter for test cases
     */
    @Parameters(name = "Run {index}")
    public static Iterable<Object[]> testData() {
        return Arrays.asList(new Object[][] {
            {
                new CommitTestData(new File(AllTests.TESTDATA + "/commitFiles/mergeCommits/5d05dfd13f20/Commit.txt"), 
                "5d05dfd13f20b01a3cd5d293058baa7d5c1583b6"),
                "CommitHeader.txt",
                "<no_date>"
            }, {
                new CommitTestData(new File(AllTests.TESTDATA + "/commitFiles/mergeCommits/longMerge/Commit.txt"), 
                "5d05dfd13f"),
                "CommitHeader.txt",
                "<no_date>"
            }, {
                new CommitTestData(new File(AllTests.TESTDATA + "/commitFiles/mergeCommits/specialCharakter/Commit.txt"
                        ), 
                "f21f7fa263ac"),
                "CommitHeader.txt",
                "<no_date>"
            }
        });
    }

    /**
     * Tests if the extraction is successful. 
     */
    @Test
    public void testExtractionSuccessful() {
        assertTrue("The interactive commit extraction should be successful", extractionTerminatedSuccessful);
    }
    
    /**
     * Tests if the extracted commit header is equals to the expected one. 
     */
    @Test
    public void testCommitHeaderEquals() {
        String[] header = extractedCommit.getCommitHeader();
        for (int i = 0; i < expectedCommitHeader.size(); i++) {
            System.out.println(header[i]);
            assertEquals("Content of commit header is not as expected", expectedCommitHeader.get(i), header[i]);
        }
    }

    /**
     * Tests if the extracted header does have the expected length. 
     */
    @Test
    public void testHeaderLength() {
        assertEquals("The length of extracted commit header is not as expected", 
                expectedCommitHeader.size(), extractedCommit.getCommitHeader().length);
    }
    
    /**
     * Tests if the number of changed artifacts is zero. Permission commits doesn't have any changed artifacts. 
     */
    @Test
    public void testChangedArtifactsEmpty() {
        assertTrue("There are no changed artifacts inside a merge commit", 
                extractedCommit.getChangedArtifacts().isEmpty());
    }
    
    /**
     * Test if the extracted commit id against the expected id.
     */
    @Test
    public void testCommitId() {
        assertEquals("The id of the extracted commit is not as expected", expectedCommitId, extractedCommit.getId());
    }
    
    /**
     * Tests the extracted committer date against the expected date.
     */
    @Test
    public void testCommitDate() {
        assertEquals("The date of the extracted commit is not as expected", expectedCommitDate, 
                extractedCommit.getDate());
    }
    
}
