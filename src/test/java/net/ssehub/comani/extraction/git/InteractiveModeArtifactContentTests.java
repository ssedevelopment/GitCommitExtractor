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
 * This class tests contents of all changed artifacts through the interactive mode 
 * {@link GitCommitExtractor#extract(String)}.<br>
 * The class is designed to take one commit and tests all changed artifacts path, names, content and diff-headers.
 * So all kinds of commits can tested through this test class (merge, permissions, and normal commits with multiple
 * changed artifacts).  
 * <br>
 * This class runs with {@link Parameterized}. 
 * 
 * @author Marcel
 */
@RunWith(Parameterized.class)
public class InteractiveModeArtifactContentTests extends AbstractInteractiveModeTests {
   
    /**
     * Ordered array of file names which contains the expected diff-header content of the current extracted commit.
     * The order needs to be the same as the changed artifacts of the extracted commit. <br>
     * For example: <br>
     * [0] - file name of the expected diff-header content of changed artifact 1<br>
     * [n] - ... expected diff-header content of changed artifact n <br>
     * This attribute is set in {@link #InteractiveModeArtifactContentTests} with test parameters.
     * 
     * @see #testData()
     */
    private String[] artifactDiffHeaderPaths;
    
    /**
     * Ordered array of file names which contains the expected content of a single changed artifacts. 
     * The order of changed artifacts must match with the order of this expected contents.
     * In test cases this files can read and compared to the extracted changed artifact content.
     * This attribute is set in {@link #InteractiveModeArtifactContentTests} with test parameters. 
     * 
     * @see #testData()
     */
    private String[] artifactContentPaths;
    
    /**
     * Ordered array of expected artifact names (the names of changed artifacts inside extracted commit). 
     * Set in {@link #InteractiveModeArtifactContentTests} with test parameters. 
     * 
     * @see #testData()
     */
    private String[] expectedArtifactNames;
    
    /**
     * Ordered array of expected artifacts paths for the current extraction process.
     * @see #testData()
     */
    private String[] expectedArtifactPaths;
    
    /**
     * The parent directory of {@link CommitTestData#commitFile}. 
     * All files defined in {@link #expectedArtifactNames} and {@link #expectedArtifactPaths} should be stored in the 
     * same directory. This attribute is set in 
     * {@link #InteractiveModeArtifactContentTests(CommitTestData, String[], String[], String[], String[])} 
     * through test parameters.
     * @see #testData()
     */
    private File commitCheckDir;
    private String expectedCommitId;
       

    /**
     * Prepares one test-run with given parameters and calls super constructor which extracts the given commit.
     * 
     * @param commitData a {@link CommitTestData} object with specifies the desired commit for extraction
     * @param artifactContentPaths the files which contains the expected artifact contents
     * @param artifactDiffHeaderPaths the file names which contains the expected artifact diff headers
     * @param artifactNames the names of changed artifacts
     * @param artifactPaths the paths of changed artifacts
     * @throws ExtractionSetupException Unwanted. if the extraction of the commit goes wrong
     * @see {@link #testData()}
     * @see {@link AbstractInteractiveModeTests#AbstractInteractiveModeTests(File)}
     */
    public InteractiveModeArtifactContentTests(
            CommitTestData commitData, 
            String[] artifactContentPaths,
            String[] artifactDiffHeaderPaths,
            String[] artifactNames,
            String[] artifactPaths) throws ExtractionSetupException {
        
        super(commitData.getCommitFile());
        this.commitCheckDir = commitData.getCommitFile().getParentFile();
        this.expectedCommitId = commitData.getCommitId();
        this.artifactContentPaths = artifactContentPaths;
        this.artifactDiffHeaderPaths = artifactDiffHeaderPaths;
        this.expectedArtifactPaths = artifactPaths;
        this.expectedArtifactNames = artifactNames;
    }
    
    /**
     * Sets the parameter for parameterized tests. <br>
     * {0} {@link CommitTestData} object which contains commit file and commit ID. <br>
     * {1} String array contains the file names which contains the expected artifact contents. The list order must match
     * with the order of changed artifact inside the test commit. It must be in same directory as the commit file <br> 
     * {2} String array contains the file names which contains the expected artifact diff-headers. The list order must
     * match with the order of changed artifact inside the test commit. It must be in same directory as the commit file.
     *  <br>
     * {3} String array. The names of the changed artifacts in same order as changed artifacts in test commit.<br>
     * {4} String array. The paths of changed artifacts in same order as changed artifacts in test commit. <br>
     *
     * @return iterable parameter for test cases 
     */
    @Parameters(name = "Run {index}")
    public static Iterable<Object[]> testData() {
        return Arrays.asList(new Object[][] {
            {
                new CommitTestData(
                        new File(AllTests.TESTDATA, "/commitFiles/artifactsCommits/simpleCommit/Commit.txt"),
                        "87d1eb2f72a8af00327aacccd5c1762bb59d602e"
                ),
                new String[] {"ChangedArtifact_1_Content.txt"},
                new String[] {"ChangedArtifact_1_DiffHeader.txt"},
                new String[] {"Kconfig"},
                new String[] {"/drivers/staging/media/mt9t031/Kconfig"}
            }, {
                new CommitTestData(
                        new File(AllTests.TESTDATA, "/commitFiles/artifactsCommits/multiArtifactCommit/Commit.txt"),
                        "b38ba5dd0819f849855dd139189ac0cfb964c901"),
                new String[] {"ChangedArtifact_1_Content.txt", "ChangedArtifact_2_Content.txt"},
                new String[] {"ChangedArtifact_1_DiffHeader.txt", "ChangedArtifact_2_DiffHeader.txt"},
                new String[] {"file1.txt", "file2.txt"},
                new String[] {"/file1.txt", "/file2.txt"}
            }, {
                new CommitTestData(
                        new File(AllTests.TESTDATA, "/commitFiles/artifactsCommits/permissionCommit/Commit.txt"),
                        "5ecf0bbbfe8adab9134a9ec456a30cbb20f7b47f"),
                new String[] {/* empty, no commit content for permission commit */},
                new String[] {"ChangedArtifact_1_DiffHeader.txt"},
                new String[] {"file1.txt"},
                new String[] {"/file1.txt"}
            }, {
                new CommitTestData(
                        new File(AllTests.TESTDATA, "/commitFiles/artifactsCommits/multiPermissionCommit/Commit.txt"),
                        "d16909177f80328ebc4cd6edea5147a16453a1bb"),
                new String[] {/* no content */"", /* no content */"", "ChangedArtifact_3_Content.txt"}, 
                new String[] {
                    "ChangedArtifact_1_DiffHeader.txt", 
                    "ChangedArtifact_2_DiffHeader.txt", 
                    "ChangedArtifact_3_DiffHeader.txt"},
                new String[] {"file1.txt", "file2.txt", "file1.txt"},
                new String[] {"/file1.txt", "/file2.txt", "/file1.txt"}
            }
            
        });
    }

    /**
     * Tests if all files out of {@link #artifactDiffHeaderPaths} are present.. 
     * This is a necessary precondition-test. In case that these files are not existing (or there is a read-write error)
     * all test will succeed without testing anything. 
     */
    @Test
    public void preconditionTestDiffHeaderFileExists() {
        for (String singleFile : artifactDiffHeaderPaths) {
            assertTrue("Precondition failed. The file " + commitCheckDir.toString() + singleFile + " does not exists. "
                    + "All other tests in this for diff-header of this artifact are obsolete now.",
                    new File(commitCheckDir, singleFile).exists());
        }
    }
    
    /**
     * Tests if all files out of {@link #artifactContentPaths} are present or empty. 
     * This is a necessary precondition-test. In case that these files are not existing (or there is a read-write error)
     * all test will succeed without testing anything. 
     * An empty array is valid. So test for commits with no content are possible.
     */
    @Test
    public void preconditionTestContentFileExists() {
        for (String singleFile : artifactContentPaths) {
            assertTrue("Precondition failed. The file " + commitCheckDir.toString() + singleFile + " does not exists. "
                    + "All other tests in this run for the artifact content are obsolete now.",
                    new File(commitCheckDir, singleFile).exists() || singleFile.isEmpty());
        }
    }
    
    /**
     * Test the expected commit id {@link #expectedCommitId} for equality with extracted commit id from 
     * {@link AbstractInteractiveModeTests#extractedCommit}.
     */
    @Test
    public void testCommitId() {
        assertEquals("Wrong commit id in extracted commit", expectedCommitId, extractedCommit.getId());
    }
    
    /**
     * Tests the size of the total number of extracted diff header against the given number in properties.
     * This is only done for diff headers and not for the total number of content. A diff header is always present
     * but a content not (ex. merge commits).
     */
    @Test
    public void testTotalArtifactNumber() {
        int artifactCount = artifactDiffHeaderPaths.length;
        assertEquals("The number of extracted commits is false", 
                artifactCount, extractedCommit.getChangedArtifacts().size());
    }
    
    /**
     * Test the expected length of artifact content for equality with extracted length of artifact content. 
     * 
     * This test wont run if no expected content files were given. This is expected behavior and is useful for special
     * commit like "permission commits".
     */
    @Test
    public void testArtifactContentLength() {
        List<String> expectedContent = null;
        for (int i = 0; i <  artifactContentPaths.length; i++) {
            expectedContent = loadContent(new File(commitCheckDir, artifactContentPaths[i]));
            int extractedContentLength = extractedCommit.getChangedArtifacts().get(i).getContent().size();
            assertEquals("The length of the content of artifact " + (i + 1) + " does not match with the length of "
                    + artifactContentPaths[i],  expectedContent.size(), extractedContentLength);
        }
    }
    
    
    /**
     * Tests the content of all changed artifacts of the extracted commit for correctness. 
     * 
     * This test wont run if no expected content files were given. This is expected behavior and is useful for special
     * commit like "permission commits".
     */
    @Test
    public void testArtifactContentEquals() {
        List<String> expectedContent = null;
        for (int i = 0; i <  artifactContentPaths.length; i++) {
            expectedContent = loadContent(new File(commitCheckDir, artifactContentPaths[i]));
            List<String> extractedContent = extractedCommit.getChangedArtifacts().get(i).getContent();
            
            expectedContent.forEach(
                line -> assertTrue("\"" + line + "\" is missing", extractedContent.contains(line))
            );
        }
    }
    
    /**
     * Tests the number of content lines for correctness for each changed artifacts.
     * 
     * This test wont run if no expected content files were given. This is expected behavior and is useful for special
     * commit like "permission commits".
     */
    @Test
    public void testArtifactDiffHeaderLength() {
        List<String> expectedHeader = null;
        for (int i = 0; i <  artifactDiffHeaderPaths.length; i++) {
            expectedHeader = loadContent(new File(commitCheckDir, artifactDiffHeaderPaths[i]));
            int extractedContentLength = extractedCommit.getChangedArtifacts().get(i).getDiffHeader().size();
            assertEquals("The length of the diff header of artifact " + (i + 1) + " does not match with the length of "
                    + artifactDiffHeaderPaths[i],  expectedHeader.size(), extractedContentLength);
        }
    }
    
    /**
     * Tests the diff-header of all changed artifacts of the extracted commit for correctness.
     * 
     * This test wont run if no expected content files were given. This is expected behavior and is useful for special
     * commit like "permission commits".
     */
    @Test
    public void testArtifactDiffHeaderEquals() {
        List<String> expectedDiffHeader = null;
        for (int i = 0; i <  artifactDiffHeaderPaths.length; i++) {            
            expectedDiffHeader = loadContent(new File(commitCheckDir, artifactDiffHeaderPaths[i]));
            List<String> extractedDiffHeader = extractedCommit.getChangedArtifacts().get(i).getDiffHeader();
            
            expectedDiffHeader.forEach(
                line -> assertTrue("\"" + line + "\" is missing", extractedDiffHeader.contains(line))
            );
        }
    }
    
    /**
     * Tests all artifacts names of the extracted commit. 
     */
    @Test
    public void testArtifactNames() {
        for (int i = 0; i < expectedArtifactNames.length; i++) {
            assertEquals("", expectedArtifactNames[i], extractedCommit.getChangedArtifacts().get(i).getArtifactName());
        }
    }
    
    
    /**
     * Tests all artifacts paths of the extracted commit.  
     */
    @Test
    public void testArtifactPaths() {
        for (int i = 0; i < expectedArtifactPaths.length; i++) {
            assertEquals("", expectedArtifactPaths[i], extractedCommit.getChangedArtifacts().get(i).getArtifactPath());
        }
    }
}

