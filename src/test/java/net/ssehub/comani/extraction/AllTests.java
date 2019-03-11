package net.ssehub.comani.extraction;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

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
}
