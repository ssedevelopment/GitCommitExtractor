package net.ssehub.comani.extraction;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.rules.Timeout;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import net.ssehub.comani.extraction.git.FullExtractionEmptyRepositoryTests;
import net.ssehub.comani.extraction.git.FullExtractionInvalidRepositoryTests;
import net.ssehub.comani.extraction.git.FullExtractionNullRepositoryTests;
import net.ssehub.comani.extraction.git.FullExtractionValidRepositoryTests;
import net.ssehub.comani.extraction.git.InteractiveExtractionMergeCommitTests;
import net.ssehub.comani.extraction.git.InteractiveModeArtifactContentTests;
import net.ssehub.comani.extraction.git.PartialExtractionEmptyRepositoryTests;
import net.ssehub.comani.extraction.git.PartialExtractionInvalidRepositoryTests;
import net.ssehub.comani.extraction.git.PartialExtractionValidRepositoryTests;

/**
 * Runs all unit tests for net.ssehub.comani.extraction.
 * 
 * @author Marcel
 */
@RunWith(Suite.class)
@SuiteClasses({ 
    FullExtractionEmptyRepositoryTests.class,
    FullExtractionInvalidRepositoryTests.class,
//    FullExtractionNullRepositoryTests.class, TODO
    FullExtractionValidRepositoryTests.class,
    PartialExtractionInvalidRepositoryTests.class,
    PartialExtractionEmptyRepositoryTests.class,
    PartialExtractionValidRepositoryTests.class,
    InteractiveExtractionMergeCommitTests.class,
    InteractiveModeArtifactContentTests.class,
    })
public class AllTests {
    @ClassRule
    public static Timeout classTimeout = new Timeout(10, TimeUnit.SECONDS);

    /**
     * Defines the resource directory for all test cases.
     */
    public static final File TESTDATA = new File("src/test/resources");
    /**
     * The location of a prepared git repository which is proposed for testing.
     */
    public static final Path TEST_REPO = Paths.get(TESTDATA.toString(), "testRepo");
    
    public static final Path TEST_COMMIT_FILES = Paths.get(TESTDATA.toString(), "commitFiles");
    
    public static final String LINE_BREAK = "\n";
    
    @Rule
    public Timeout globalTimeout = Timeout.seconds(10); // 10 seconds max per method tested

    /**
     * The "extractionRepo" is used in several test cases and is needed for testing the extraction. 
     * The problem is, git doesn't allow an embedded ".git" directory without adding it as a submodule. 
     * 
     * As a workaround, a symbolic link is created of "/repositories/extractionRepo/git" to 
     * ".git". Now it behaves like a normal git repository. 
     */
    @BeforeClass
    public static void setSymlink() {
        Path target = Paths.get(TESTDATA.getAbsolutePath(), "/repositories/extractionRepo/git");
        Path link = Paths.get(target.getParent().toString(), "/.git");
        if (!link.toFile().exists()) {
            try {
                Files.createSymbolicLink(link, target);
            } catch (IOException x) {
                System.err.println(x);
            } catch (UnsupportedOperationException x) {
                // Some file systems do not support symbolic links.
                System.err.println(x);
            }
        }
    }

}
