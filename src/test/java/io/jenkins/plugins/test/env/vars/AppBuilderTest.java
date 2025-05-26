package io.jenkins.plugins.test.env.vars;

import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;
import hudson.model.Label;
import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.jenkinsci.plugins.workflow.job.WorkflowRun;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

public class AppBuilderTest {

    @Rule
    public JenkinsRule jenkins = new JenkinsRule();

    final String value = "BUILD_ID";

    @Test
    public void testConfigRoundtrip() throws Exception {
        FreeStyleProject project = jenkins.createFreeStyleProject();
        project.getBuildersList().add(new AppBuilder(value));
        project = jenkins.configRoundtrip(project);
        jenkins.assertEqualDataBoundBeans(
                new AppBuilder(value), project.getBuildersList().get(0));
    }

    @Test
    public void testConfigRoundtripFrench() throws Exception {
        FreeStyleProject project = jenkins.createFreeStyleProject();
        AppBuilder builder = new AppBuilder(value);
        project.getBuildersList().add(builder);
        project = jenkins.configRoundtrip(project);

        AppBuilder lhs = new AppBuilder(value);
        jenkins.assertEqualDataBoundBeans(lhs, project.getBuildersList().get(0));
    }

    @Test
    public void testBuild() throws Exception {
        FreeStyleProject project = jenkins.createFreeStyleProject();
        AppBuilder builder = new AppBuilder(value);
        project.getBuildersList().add(builder);

        FreeStyleBuild build = jenkins.buildAndAssertSuccess(project);
        jenkins.assertLogContains(value, build);
    }

    @Test
    public void testScriptedPipeline() throws Exception {
        String agentLabel = "my-agent";
        jenkins.createOnlineSlave(Label.get(agentLabel));
        WorkflowJob job = jenkins.createProject(WorkflowJob.class, "test-scripted-pipeline");
        String pipelineScript = "node {testEnvVars '" + value + "'}";
        job.setDefinition(new CpsFlowDefinition(pipelineScript, true));
        WorkflowRun completedBuild = jenkins.assertBuildStatusSuccess(job.scheduleBuild2(0));
        String expectedString = value;
        jenkins.assertLogContains(expectedString, completedBuild);
    }
}
