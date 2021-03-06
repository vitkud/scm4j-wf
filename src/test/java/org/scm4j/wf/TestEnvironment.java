package org.scm4j.wf;

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.scm4j.vcs.GitVCS;
import org.scm4j.vcs.GitVCSUtils;
import org.scm4j.vcs.api.IVCS;
import org.scm4j.vcs.api.workingcopy.IVCSRepositoryWorkspace;
import org.scm4j.vcs.api.workingcopy.IVCSWorkspace;
import org.scm4j.vcs.api.workingcopy.VCSWorkspace;
import org.scm4j.wf.conf.Version;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.UUID;

public class TestEnvironment {
	public static final String TEST_REPOS_FILE_NAME = "repos";
	public static final String TEST_CREDENTIALS_FILE_NAME = "credentials";
	public static final String TEST_ENVIRONMENT_DIR = new File(System.getProperty("java.io.tmpdir"), "scm4j-wf-test").getPath();
	public static final String TEST_ENVIRONMENT_URL = "file://localhost/" + TEST_ENVIRONMENT_DIR.replace("\\", "/");
	public static final String TEST_VCS_REPO_FILE_URL = "file://localhost/" + TEST_ENVIRONMENT_URL + "/vcs-repo";
	public static final String TEST_LOCAL_REPO_DIR = new File(TEST_ENVIRONMENT_DIR, "local-repos").getPath();
	public static final String TEST_REMOTE_REPO_DIR = new File(TEST_ENVIRONMENT_DIR, "remote-repos").getPath();
	public static final String TEST_FEATURE_FILE_NAME = "feature.txt";
	public static final String TEST_DUMMY_FILE_NAME = "dummy.txt";

	private IVCS unTillVCS;
	private IVCS ublVCS;
	private IVCS unTillDbVCS;
	private File credsFile;
	private File reposFile;
	private final Version unTillVer = new Version("1.123.3-SNAPSHOT");
	private final Version ublVer = new Version("1.18.5-SNAPSHOT");
	private final Version unTillDbVer = new Version("2.59.1-SNAPSHOT");
	

	public void generateTestEnvironment() throws IOException, GitAPIException {

		createTestEnvironmentFolder();

		createTestVCSRepos();

		uploadVCSConfigFiles();

		createCredentialsFile();

		createReposFile();

	}

	private void createReposFile() throws IOException {
		reposFile = new File(TEST_ENVIRONMENT_DIR, TEST_REPOS_FILE_NAME);
		reposFile.createNewFile();
		FileUtils.writeLines(reposFile,Arrays.asList(
				"[{\"name\": \"" + SCMWorkflowConfigTest.PRODUCT_UNTILL + "\",\"url\": \"" + unTillVCS.getRepoUrl() + "\"},",
				"{\"name\": \"" + SCMWorkflowConfigTest.PRODUCT_UBL + "\",\"url\": \"" + ublVCS.getRepoUrl() + "\"},",
				"{\"name\": \"" + SCMWorkflowConfigTest.PRODUCT_UNTILLDB  +"\",\"url\": \"" + unTillDbVCS.getRepoUrl() + "\"}]"));
	}

	private void createCredentialsFile() throws IOException {
		credsFile = new File(TEST_ENVIRONMENT_DIR, TEST_CREDENTIALS_FILE_NAME);
		credsFile.createNewFile();
	}

	private void uploadVCSConfigFiles() {
		unTillVCS.setFileContent(null, SCMWorkflow.VER_FILE_NAME, unTillVer.toString(), "ver file added");
		unTillVCS.setFileContent(null, SCMWorkflow.MDEPS_FILE_NAME,
				SCMWorkflowConfigTest.PRODUCT_UBL + ":" + ublVer.toString() + "\r\n" +
				SCMWorkflowConfigTest.PRODUCT_UNTILLDB + ":" + unTillDbVer.toString() + "\r\n", "mdeps file added");

		ublVCS.setFileContent(null, SCMWorkflow.VER_FILE_NAME, ublVer.toString(), "ver file added");
		ublVCS.setFileContent(null, SCMWorkflow.MDEPS_FILE_NAME,
				SCMWorkflowConfigTest.PRODUCT_UNTILLDB + ":" + unTillDbVer.toString() + "\r\n", "mdeps file added");

		unTillDbVCS.setFileContent(null, SCMWorkflow.VER_FILE_NAME, unTillDbVer.toString(), "ver file added");
	}

	private void createTestVCSRepos() throws GitAPIException {
		IVCSWorkspace localVCSWorkspace = new VCSWorkspace(TEST_ENVIRONMENT_DIR);
		File unTillRemoteRepoDir = new File(TEST_REMOTE_REPO_DIR, "unTill-" + UUID.randomUUID().toString() + ".git");
		File ublRemoteRepoDir = new File(TEST_REMOTE_REPO_DIR, "UBL-" + UUID.randomUUID().toString() + ".git");
		File unTillRemoteDbRepoDir = new File(TEST_REMOTE_REPO_DIR, "unTillDb-" + UUID.randomUUID().toString() + ".git");
		GitVCSUtils.createRepository(unTillRemoteRepoDir);
		GitVCSUtils.createRepository(ublRemoteRepoDir);
		GitVCSUtils.createRepository(unTillRemoteDbRepoDir);
		IVCSRepositoryWorkspace unTillVCSRepoWS = localVCSWorkspace
				.getVCSRepositoryWorkspace("file:///" + unTillRemoteRepoDir.getPath().replace("\\", "/"));
		IVCSRepositoryWorkspace ublVCSRepoWS = localVCSWorkspace
				.getVCSRepositoryWorkspace("file:///" + ublRemoteRepoDir.getPath().replace("\\", "/"));
		IVCSRepositoryWorkspace unTillDbVCSRepoWS = localVCSWorkspace
				.getVCSRepositoryWorkspace("file:///" + unTillRemoteDbRepoDir.getPath().replace("\\", "/"));
		unTillVCS = new GitVCS(unTillVCSRepoWS);
		ublVCS = new GitVCS(ublVCSRepoWS);
		unTillDbVCS = new GitVCS(unTillDbVCSRepoWS);
	}

	private void createTestEnvironmentFolder() throws IOException {
		File envDir = new File(TEST_ENVIRONMENT_DIR);
		if (envDir.exists()) {
			FileUtils.deleteDirectory(envDir);
		}
		envDir.mkdirs();
	}

	public IVCS getUnTillVCS() {
		return unTillVCS;
	}

	public IVCS getUblVCS() {
		return ublVCS;
	}

	public IVCS getUnTillDbVCS() {
		return unTillDbVCS;
	}

	public File getCredsFile() {
		return credsFile;
	}

	public File getReposFile() {
		return reposFile;
	}

	public void generateFeatureCommit(IVCS vcs, String commitMessage) {
		vcs.setFileContent(null, TEST_FEATURE_FILE_NAME, "feature content", commitMessage);
	}

	public void generateCommitWithVERTag(IVCS vcs) {
		vcs.setFileContent(null, TEST_DUMMY_FILE_NAME, "dummy content " + UUID.randomUUID().toString(), 
				SCMActionProductionRelease.VCS_TAG_SCM_VER);
	}

	public Version getUnTillVer() {
		return unTillVer;
	}

	public Version getUblVer() {
		return ublVer;
	}

	public Version getUnTillDbVer() {
		return unTillDbVer;
	}
	

}
