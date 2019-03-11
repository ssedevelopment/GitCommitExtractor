/*
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements. See the NOTICE
 * file distributed with this work for additional information regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package net.ssehub.comani.extraction.git;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import net.ssehub.comani.core.Logger.MessageType;
import net.ssehub.comani.data.ChangedArtifact;
import net.ssehub.comani.data.Commit;
import net.ssehub.comani.data.IExtractionQueue;
import net.ssehub.comani.extraction.AbstractCommitExtractor;
import net.ssehub.comani.extraction.ExtractionSetupException;
import net.ssehub.comani.utility.ProcessUtilities;
import net.ssehub.comani.utility.ProcessUtilities.ExecutionResult;

/**
 * The main class of this extractor. It extracts commits from Git repositories on any platform.
 * 
 * @author Christian Kroeher
 *
 */
public class GitCommitExtractor extends AbstractCommitExtractor {
    
    /**
     * The identifier of this class, e.g., for printing messages.
     */
    private static final String ID = "GitCommitExtractor";
    
    /**
     * The command for printing the installed Git version used to check whether Git is installed during 
     * {@link #prepare()}.<br>
     * <br>
     * Command: <code>git --version</code>
     */
    private static final String[] GIT_VERSION_COMMAND = {"git", "--version"};
    
    /**
     * The command for printing all commit numbers (SHAs) to console.<br>
     * <br>
     * Command: <code>git log --pretty=format:"%h"</code>
     */
    private static final String[] GIT_COMMITS_COMMAND = {"git", "log", "--pretty=format:%h"};
    
    /**
     * The constant part of the command for printing the committer date to console. The number of the commit, for which
     * the committer date shall be printed, must be appended with an additional whitespace as prefix.<br>
     * <br>
     * Command: <code>git show -s --format=%ci</code> 
     */
    private static final String[] GIT_COMMITTER_DATE_COMMAND = {"git", "show", "-s", "--format=%ci"};
    
    /**
     * The constant part of the command for printing the entire commit information, the content of the changed files
     * (100.000 lines of context including renamed files), and the changes to console. The number of the commit, for
     * which this information shall be printed, must be appended with an additional whitespace as prefix.<br>
     * <br>
     * Command: <code>git show -U100000 --no-renames</code>
     */
    private static final String[] GIT_COMMIT_CHANGES_COMMAND = {"git", "show", "-U100000", "--no-renames"};
    
    /**
     * The string identifying the start of a diff header in a commit. The first line of the diff header starts with this
     * string. Each diff header marks the beginning of an individual file being changed by the respective commit.
     */
    private static final String DIFF_HEADER_START_PATTERN = "diff --git";
    
    /**
     * The string identifying the end of a diff header in a commit. The last line of the diff header starts with this
     * string. After that line, the content of the changed artifact described by the diff header as well as the actual
     * changes to the artifact are listed. 
     * 
     * @see #DIFF_HEADER_START_PATTERN
     */
    private static final String DIFF_HEADER_END_PATTERN = "@@";
    
    /**
     * The {@link ProcessUtilities} for retrieving Git information, like the available commits and their data, via the
     * execution of external processes.
     */
    private ProcessUtilities processUtilities;

    /**
     * Constructs a new instance of this extractor, which extracts commits from Git repositories on any platform.
     * 
     * @param extractionProperties the properties of the properties file defining the extraction process and the
     *        configuration of the extractor in use; all properties, which start with the prefix "<tt>extraction.</tt>"
     *        as well as the properties defining the operating system and the version control system
     * @param commitQueue the {@link IExtractionQueue} for transferring commits from an extractor to an analyzer
     * @throws ExtractionSetupException if the extractor is not supporting the current operating or version control
     *         system
     */
    public GitCommitExtractor(Properties extractionProperties, IExtractionQueue commitQueue)
            throws ExtractionSetupException {
        super(extractionProperties, commitQueue);
        prepare();
        logger.log(ID, this.getClass().getName() + " created", null, MessageType.DEBUG);
    }
    
    /**
     * Prepares this extractor for execution, e.g., reading and setting the properties as well as creating required
     * utilities.
     * 
     * @throws ExtractionSetupException if setting-up the necessary elements of this extractor failed
     */
    private void prepare() throws ExtractionSetupException {
        processUtilities = ProcessUtilities.getInstance();
        // Check if Git is installed and available
        ExecutionResult executionResult = processUtilities.executeCommand(GIT_VERSION_COMMAND, null);
        if (!executionResult.executionSuccessful()) {
            throw new ExtractionSetupException("Testing Git availability failed.\n" 
                    + executionResult.getErrorOutputData());
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean extract(File repository) {
        logger.log(ID, "Full extraction of all available commits in repository", null, MessageType.DEBUG);
        boolean extractionSuccessful = false;
        String[] commitNumbers = getCommitNumbers(repository);
        if (commitNumbers != null) {
            extractionSuccessful = extract(commitNumbers, repository);
        }
        return extractionSuccessful;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean extract(File repository, List<String> commitList) {
        logger.log(ID, "Selective extraction based on commit list file", null, MessageType.DEBUG);
        String[] commitNumbers;
        commitNumbers = commitList.toArray(new String[0]);
        return extract(commitNumbers, repository);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean extract(String commit) {
        logger.log(ID, "Extraction (parsing) of single commit", null, MessageType.DEBUG);
        boolean extractionSuccessful = false;
        // Get the commit id from the first line of the given commit string
        String commitId = null;
        if (commit.startsWith("commit ")) {
            int commitIdStartIndex = commit.indexOf(" ") + 1;
            int commitIdEndIndex = commitIdStartIndex;
            int indexCounter = commitIdStartIndex + 1;
            while (commitIdEndIndex == commitIdStartIndex && indexCounter < commit.length()) {
                char commitCharAtIndex = commit.charAt(indexCounter);
                if (commitCharAtIndex == ' ' || commitCharAtIndex == '\n' || indexCounter + 1 == commit.length()) {
                    commitIdEndIndex = indexCounter;
                }
                indexCounter++;
            }
            commitId = commit.substring(commitIdStartIndex, commitIdEndIndex);
        }
        // Create the commit object
        if (commitId != null && !commitId.isEmpty()) {            
            Commit commitObject = createCommit(commitId, "<no_date>", commit);
            if (commitObject != null) {
                while (!commitQueue.addCommit(commitObject)) {
                    logger.log(ID, "Waiting to add commit to queue", null, MessageType.DEBUG);
                }
                extractionSuccessful = true;
            }
        } else {
            logger.log(ID, "Identifying the commit id failed",
                    "The given string does not start with \"commit <ID> ...\"", MessageType.ERROR);
        }
        return extractionSuccessful;
    }
    
    /**
     * Extracts all commits from the defined repository included in the given set of commit numbers.
     * 
     * @param commitNumbers the set of commits numbers to be extracted
     * @param repositoryDirectory the {@link File} defining the repository from which the commits will be extracted
     * @return <code>true</code> if extracting the commits was successful; <code>false</code> otherwise
     */
    private boolean extract(String[] commitNumbers, File repositoryDirectory) {
        boolean extractionSuccessful = false;
        if (commitNumbers != null) {
            String commitNumber = null;
            String[] command = null;
            String committerDate = null;
            String commitContent = null;
            for (int i = 0; i < commitNumbers.length; i++) {
                logger.log(ID, "Extracting commit " + commitNumbers[i], null, MessageType.DEBUG);
                commitNumber = commitNumbers[i];
                /*
                 * We assume that the standard output streams of the processes executed below contain the commit date
                 * and content.
                 */
                command = processUtilities.extendCommand(GIT_COMMITTER_DATE_COMMAND, commitNumber);
                committerDate = getCommitInformation(command, repositoryDirectory);

                command = processUtilities.extendCommand(GIT_COMMIT_CHANGES_COMMAND, commitNumber);
                commitContent = getCommitInformation(command, repositoryDirectory);

                if (committerDate != null) {
                    if (commitContent != null) {                        
                        Commit commit = createCommit(commitNumber, committerDate, commitContent);
                        while (!commitQueue.addCommit(commit)) {
                            logger.log(ID, "Waiting to add commit to queue", null, MessageType.DEBUG);
                        }
                    } else {
                        logger.log(ID, "Commit content not available for commit " + commitNumber,
                                "Executing git command was not successful", MessageType.WARNING);
                    }
                } else {
                    logger.log(ID, "Committer date not available for commit " + commitNumber,
                            "Executing git command was not successful", MessageType.WARNING);
                }
            }
            extractionSuccessful = true;
        }
        return extractionSuccessful;
    }
    
    /**
     * Retrieves commit information by executing the given command in the given working directory.
     * 
     * @param command the command to be executed to retrieve the desired commit information
     * @param workingDirectory the directory in which the process with the given command shall be executed
     * @return the commit information as provided by the standard output stream of the process executing the given
     *         command or <code>null</code>, if the execution was not successful
     */
    private String getCommitInformation(String[] command, File workingDirectory) {
        String commitInformation = null;
        ExecutionResult executionResult = processUtilities.executeCommand(command, workingDirectory);
        if (executionResult.executionSuccessful()) {
            commitInformation = executionResult.getStandardOutputData();
        }
        return commitInformation;
    }
    
    /**
     * Creates a new {@link Commit} based on the given committer date and commit content.
     * 
     * @param commitNumber the commit numbers (SHAs) of the commit to be created
     * @param committerDate the committer date of the commit to be created
     * @param commitContent the commit content of the commit to be created
     * @return the commit created by processing the given date and content; may be <code>null</code> or <i>empty</i>
     */
    private Commit createCommit(String commitNumber, String committerDate, String commitContent) {
        logger.log(ID, "Creating commit object for commit " + commitNumber, null, MessageType.DEBUG);
        String[] commitContentLines = commitContent.split("\n", -1);
        int linesCounter = getIndexLineStartsWith(commitContentLines, DIFF_HEADER_START_PATTERN);
        String[] commitHeader = null;
        List<ChangedArtifact> changedArtifacts = null;
        if (linesCounter > 0) {
            commitHeader = Arrays.copyOfRange(commitContentLines, 0, linesCounter);
            changedArtifacts = new ArrayList<ChangedArtifact>();
            ChangedArtifact changedArtifact = null;
            String changedArtifactPath = null;
            String commitContentLine = null;
            boolean artifactContentReached = false;
            while (linesCounter < commitContentLines.length) {
                commitContentLine = commitContentLines[linesCounter];
                if (commitContentLine.startsWith(DIFF_HEADER_START_PATTERN)) {
                    artifactContentReached = false;
                    if (changedArtifact != null) {
                        changedArtifacts.add(changedArtifact);
                    }
                    changedArtifact = new ChangedArtifact();
                    changedArtifact.addDiffHeaderLine(commitContentLine);
                    changedArtifactPath = getArtifactPath(commitContentLine);
                    changedArtifact.addArtifactPath(changedArtifactPath);
                    changedArtifact.addArtifactName(getArtifactName(changedArtifactPath));
                    
                } else {
                    if (artifactContentReached) {
                        changedArtifact.addContentLine(commitContentLine);
                    } else {
                        artifactContentReached = commitContentLine.startsWith(DIFF_HEADER_END_PATTERN);
                        changedArtifact.addDiffHeaderLine(commitContentLine);
                    }
                }
                linesCounter++;
                if (linesCounter == commitContentLines.length) {
                    // End of changes, add last changed artifact to list
                    changedArtifacts.add(changedArtifact);
                }
            }
        } else {
            // In case there are no explicit artifact content changes, treat everything as commit header
            // Test: Coreboot commit 118b382e7d
            commitHeader = Arrays.copyOfRange(commitContentLines, 0, commitContentLines.length);
        }
        return new Commit(commitNumber, committerDate, commitHeader, changedArtifacts);
    }
    
    /**
     * Returns the relative path to the changed artifact in the repository based on the information of the first
     * diff header line.
     * 
     * @param firstDiffHeaderLine the first line starting with {@link #DIFF_HEADER_START_PATTERN}, which typically
     *        contains the relative path to the changed artifact; should never be <code>null</code>
     * @return the relative path to the changed artifact in the repository; never <code>null</code> but may be
     *         <i>empty</i>
     */
    private String getArtifactPath(String firstDiffHeaderLine) {
        String artifactPath = "";
        String[] firstDiffHeaderLineParts = firstDiffHeaderLine.split("\\s+");
        // 1. "diff", 2. "--git", 3. "a/...", 4. "b/..."
        if (firstDiffHeaderLineParts.length >= 4) {
            String firstDiffHeaderLinePart = null;
            // As we search for the second (and typically last) path entry, start from end of array
            int partsCounter = firstDiffHeaderLineParts.length - 1;
            while (artifactPath.isEmpty() && partsCounter >= 0) {
                firstDiffHeaderLinePart = firstDiffHeaderLineParts[partsCounter];
                if (firstDiffHeaderLinePart.startsWith("b/")) {
                    artifactPath = firstDiffHeaderLinePart.substring(1); // Exclude leading "b"
                }
                partsCounter--;
            }
        }
        return artifactPath;
    }
    
    /**
     * Returns the name of the changed artifact based on the given relative path to the artifact as provided by 
     * {@link #getArtifactPath(String)}.
     * 
     * @param changedArtifactPath the relative path to the changed artifact in the repository; should never be
     *        <code>null</code>
     * @return the name of the changed artifact (the file it represents); never <code>null</code> but may be
     *         <i>empty</i>
     */
    private String getArtifactName(String changedArtifactPath) {
        String artifactName = "";
        String[] changedArtifactPathParts = changedArtifactPath.split("/");
        artifactName = changedArtifactPathParts[changedArtifactPathParts.length - 1];
        return artifactName;
    }
    
    /**
     * Returns the index of the first line (string) in the given array, which starts with the given pattern.
     * 
     * @param lines the array of lines in which the first line starting with the given pattern shall be found
     * @param pattern the pattern with which a particular line in the array shall be found
     * @return the index of the first line (string) in the given array or <tt>-1</tt> if no line starts with the given
     *         pattern
     */
    private int getIndexLineStartsWith(String[] lines, String pattern) {
        int index = -1;
        int lineCounter = 0;
        boolean patternFound = false;
        while (!patternFound && lineCounter < lines.length) {
            patternFound = lines[lineCounter].startsWith(pattern);
            if (patternFound) {
                index = lineCounter;
            }
            lineCounter++;
        }
        return index;
    }
       
    /**
     * Extracts the commit numbers (SHAs) from the commit log as returned by the {@link #GIT_COMMITS_COMMAND} command,
     * which this method executes at the location of the given repository.
     * 
     * @param repository the {@link File} representing the repository directory; never <code>null</code> and always a
     *        directory
     * @return the set of commit numbers or <code>null</code>, if extracting the commit numbers failed
     */
    private String[] getCommitNumbers(File repository) {
        logger.log(ID, "Extracting commit numbers from log", null, MessageType.DEBUG);
        String[] commitNumbers = null;
        ExecutionResult executionResult = processUtilities.executeCommand(GIT_COMMITS_COMMAND, repository);
        if (executionResult.executionSuccessful()) {
            // We assume that the standard output stream of the process executed above contains the commit numbers
            String commitLog = executionResult.getStandardOutputData();
            if (commitLog != null && !commitLog.isEmpty()) {
                commitNumbers = commitLog.split("\n");
            } else {
                logger.log(ID, "Commit log is empty", "No commit numbers to extract", MessageType.ERROR);
            }
        } else {
            logger.log(ID, "Extracting the available commit numbers failed", "Executing the command \"" 
                    + processUtilities.getCommandString(GIT_COMMITS_COMMAND) + "\" was not successful: " 
                    + executionResult.getErrorOutputData(), MessageType.ERROR);
        }
        return commitNumbers;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean operatingSystemSupported(String operatingSystem) {
        // This extractor is OS-independent
        logger.log(ID, "Supported operating systems: all", null, MessageType.DEBUG);
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean versionControlSystemSupported(String versionControlSystem) {
        String supportedVCS = "git";
        logger.log(ID, "Supported version control system: " + supportedVCS, null, MessageType.DEBUG);
        return versionControlSystem.equalsIgnoreCase(supportedVCS);
    }
}