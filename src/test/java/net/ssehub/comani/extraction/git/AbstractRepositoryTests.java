package net.ssehub.comani.extraction.git;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import net.ssehub.comani.data.Commit;
import net.ssehub.comani.data.CommitQueue;
import net.ssehub.comani.data.CommitQueue.QueueState;
import net.ssehub.comani.extraction.ExtractionSetupException;
import net.ssehub.comani.extraction.git.GitCommitExtractor;

/**
 * This class provides the common attributes and preparation method for repository-based extractor tests. 
 * 
 * @author Christian Kröher
 *
 */
public abstract class AbstractRepositoryTests {
    
    /*
     * TODO Implement tests with the following repo-parameters (one class for each parameter variant):
     *  - With a valid repository, which contains one or more commits --> done: FullExtractionValidRepositoryTests
     *  - With an empty repository, which does not contain any commits --> done: FullExtractionEmptyRepositoryTests
     *  - With a non-existing repository, which means that the File-object does not denote a directory but a file
     *  - With a non-existing repository, which means that the File-object does not denote an existing file or
     *    directory
     *  - With "null" as parameter
     */
    
    /**
     * The return value of the extraction process indicating whether the process terminated successfully
     * (<code>true</code>) or not (<code>false</code>), which is the default value, if the preparation method of this
     * class is not executed.
     * 
     * @see #prepare(File)
     */
    protected static boolean extractionTerminatedSuccessful = false;
    
    /**
     * The list of commits extracted as part of the test preparation. The default value is <code>null</code>, if the
     * preparation method of this class is not executed.
     * 
     * @see #prepare(File)
     */
    protected static List<Commit> extractedCommits = null;

    /**
     * Instantiates the {@link #commitExtractor}, performs the commit extraction, and sets the values of 
     * {@link #extractionTerminatedSuccessful} as well as {@link #extractedCommits} for their use in the tests of the
     * extending classes.
     * 
     * @param repository the {@link File} denoting the repository (directory) from which the commits shall be extracted
     * @throws ExtractionSetupException if instantiating the commit extractor failed
     */
    protected static void prepare(File repository) throws ExtractionSetupException {
        Properties extractionProperties = new Properties();
        extractionProperties.setProperty("core.version_control_system", "git");
        
        CommitQueue commitQueue = new CommitQueue(10);
        commitQueue.setState(QueueState.OPEN);
        
        GitCommitExtractor commitExtractor = new GitCommitExtractor(extractionProperties, commitQueue);
        extractionTerminatedSuccessful = commitExtractor.extract(repository);
        
        commitQueue.setState(QueueState.CLOSED); // Actual closing after getting all commits below
        
        extractedCommits = new ArrayList<Commit>();
        while (commitQueue.isOpen()) {
            Commit commit = commitQueue.getCommit();
            if (commit != null) {
                extractedCommits.add(commit);
            }
        }
    }
    
    /**
     * Instantiates a {@link GitCommitExtractor}, performs the extraction of a specific commit id list 
     * and sets the value of {@link #extractionTerminatedSuccessful} as well as {@link #extractedCommits} for their use
     * in the tests of the extending classes. 
     * 
     * @param repository the {@link File} denoting the repository (directory) from which the specific commits shall be
     * extracted
     * @param commitIdList the String {@link List} of commits which shall be extracted from the given repository
     * @throws ExtractionSetupException if instantiating the commit extractor failed
     */
    protected static void prepare(File repository, List<String> commitIdList) throws ExtractionSetupException {
        Properties extractionProperties = new Properties();
        extractionProperties.setProperty("core.version_control_system", "git");
        
        CommitQueue commitQueue = new CommitQueue(10);
        commitQueue.setState(QueueState.OPEN);
        
        GitCommitExtractor commitExtractor = new GitCommitExtractor(extractionProperties, commitQueue);
        extractionTerminatedSuccessful = commitExtractor.extract(repository, commitIdList);
        
        commitQueue.setState(QueueState.CLOSED); // Actual closing after getting all commits below
        
        extractedCommits = new ArrayList<Commit>();
        while (commitQueue.isOpen()) {
            Commit commit = commitQueue.getCommit();
            if (commit != null) {
                extractedCommits.add(commit);
            }
        }
    }    
}
