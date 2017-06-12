package org.scm4j.wf;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.scm4j.vcs.GitVCS;
import org.scm4j.vcs.GitVCSUtils;
import org.scm4j.vcs.api.IVCS;
import org.scm4j.vcs.api.workingcopy.IVCSRepositoryWorkspace;
import org.scm4j.vcs.api.workingcopy.IVCSWorkspace;
import org.scm4j.vcs.api.workingcopy.VCSWorkspace;
import org.scm4j.wf.exceptions.EWFNoConfig;
import org.scm4j.wf.model.VCSRepository;

import java.io.File;
import java.io.IOException;

@PrepareForTest(VCSRepository.class)
@RunWith(PowerMockRunner.class)
public class SCMWorkflowConfigTest {

	private IVCS vcs;
	private static final String TEST_WORKSPACE_DIR = System.getProperty("java.io.tmpdir") + "scm4j-wf-test";
	private static final String TEST_WORKSPACE_URL = "file:///" + TEST_WORKSPACE_DIR.replace("\\", "/");
	private static final String TEST_VCS_REPO_FILE_URL = TEST_WORKSPACE_URL + "/vcs-repo";
	private static final String TEST_REPO_DIR = new File(TEST_WORKSPACE_DIR, "repos").getPath();
	private IVCS unTillVCS;
	private IVCS ublVCS;
	private IVCS unTillDbVCS;


	@Before
	public void setUp() throws IOException {
		File workspaceDir = new File(TEST_WORKSPACE_DIR);
		if (workspaceDir.exists()) {
			FileUtils.deleteDirectory(workspaceDir);
		}
		workspaceDir.mkdirs();
		IVCSWorkspace localVCSWorkspace = new VCSWorkspace(TEST_WORKSPACE_DIR);

		File unTillRepoDir = new File(TEST_REPO_DIR, "unTill");
		File ublRepoDir = new File(TEST_REPO_DIR, "UBL");
		File unTillDbRepoDir = new File(TEST_REPO_DIR, "unTillDb");
		GitVCSUtils.createRepository(unTillRepoDir);
		GitVCSUtils.createRepository(ublRepoDir);
		GitVCSUtils.createRepository(unTillDbRepoDir);
		IVCSRepositoryWorkspace unTillVCSRepoWS = localVCSWorkspace.getVCSRepositoryWorkspace("file:///" + unTillRepoDir.getPath().replace("\\", "/"));
		IVCSRepositoryWorkspace ublVCSRepoWS = localVCSWorkspace.getVCSRepositoryWorkspace("file:///" + ublRepoDir.getPath().replace("\\", "/"));
		IVCSRepositoryWorkspace unTillDbVCSRepoWS = localVCSWorkspace.getVCSRepositoryWorkspace("file:///" + unTillDbRepoDir.getPath().replace("\\", "/"));
		unTillVCS = new GitVCS(unTillVCSRepoWS);
		ublVCS = new GitVCS(ublVCSRepoWS);
		unTillDbVCS = new GitVCS(unTillDbVCSRepoWS);

		unTillVCS.setFileContent(null, "ver", "1.123.3-SNAPSHOT", "ver file added");
		unTillVCS.setFileContent(null, "mdeps",
				"eu.untill:UBL:18.5-SNAPSHOT\r\n" +
						"eu.untill:unTillDb:2.59.1-SNAPSHOT\r\n", "mdeps file added");

		ublVCS.setFileContent(null, "ver", "1.18.5-SNAPSHOT", "ver file added");
		ublVCS.setFileContent(null, "mdeps",
				"eu.untill:unTillDb:2.59.1-SNAPSHOT\r\n", "mdeps file added");

		unTillDbVCS.setFileContent(null, "ver", "2.59.1-SNAPSHOT", "ver file added");
	}

	@After
	public void tearDown() {
		File testFolder = new File(TEST_WORKSPACE_DIR);
		if (testFolder.exists()) {
			testFolder.delete();
		}
	}

	@Test(expected=EWFNoConfig.class)
	public void testNoConfig() {
		new SCMWorkflow("eu.untill:unTill", TEST_WORKSPACE_DIR);
	}

	@Test(expected=EWFNoConfig.class)
	public void testNoRepos() {
		PowerMockito.mockStatic(System.class);
		PowerMockito.when(System.getenv(VCSRepository.CONFIG_ENV_VAR)).thenReturn("malformed url");
		new SCMWorkflow("eu.untill:unTill", TEST_WORKSPACE_DIR);
	}

	@Test
	public void testEmptyConfigs() throws IOException {
		File vcsRepos = new File(TEST_WORKSPACE_DIR, "vcs-repos");
		vcsRepos.createNewFile();
		PowerMockito.mockStatic(System.class);
		PowerMockito.when(System.getenv(VCSRepository.CONFIG_ENV_VAR)).thenReturn(TEST_VCS_REPO_FILE_URL);
		new SCMWorkflow("eu.untill:unTill", TEST_WORKSPACE_DIR);

	}
}