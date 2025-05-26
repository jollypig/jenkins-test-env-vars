package io.jenkins.plugins.test.env.vars;

import hudson.EnvVars;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractProject;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import hudson.util.FormValidation;
import java.io.IOException;
import java.util.Arrays;
import javax.servlet.ServletException;

import jenkins.tasks.SimpleBuildStep;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

public class AppBuilder extends Builder implements SimpleBuildStep {

    private final String value;

    @DataBoundConstructor
    public AppBuilder(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    private String envy(String sentence, EnvVars env) {
        return sentence != null ? env.get( sentence ) : null;
    }

    @Override
    public void perform(Run<?, ?> run, FilePath workspace, EnvVars env, Launcher launcher, TaskListener listener)
            throws InterruptedException, IOException {
        if (value != null) {
            Arrays.stream(value.split(",")).map(v -> v + ": " + envy(v, env)).forEach(v -> {
                listener.getLogger().println(" =========> " + v);
            });
        }
    }

    @Symbol("testEnvVars")
    @Extension
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {

        public FormValidation doCheckValue(@QueryParameter String value)
                throws IOException, ServletException {
            if (value.length() == 0)
                return FormValidation.error(Messages.HelloWorldBuilder_DescriptorImpl_errors_missingName());
            return FormValidation.ok();
        }

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            return true;
        }

        @Override
        public String getDisplayName() {
            return Messages.HelloWorldBuilder_DescriptorImpl_DisplayName();
        }
    }
}
