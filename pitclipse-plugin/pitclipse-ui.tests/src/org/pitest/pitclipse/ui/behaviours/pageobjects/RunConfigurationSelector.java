package org.pitest.pitclipse.ui.behaviours.pageobjects;

import static com.google.common.collect.ImmutableList.builder;

import java.util.List;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.pitest.pitclipse.ui.behaviours.pageobjects.PitRunConfiguration.Builder;

import com.google.common.collect.ImmutableList;

public class RunConfigurationSelector {

	private final SWTWorkbenchBot bot;

	public RunConfigurationSelector(SWTWorkbenchBot bot) {
		this.bot = bot;
	}

	public PitRunConfiguration getConfiguration(String configName) {
		activateShell();
		Builder builder = new PitRunConfiguration.Builder();
		return builder.build();
	}

	public List<PitRunConfiguration> getConfigurations() {
		activateShell();
		ImmutableList.Builder<PitRunConfiguration> builder = builder();
		SWTBotTreeItem[] configurations = activateShell().getItems();
		for (SWTBotTreeItem treeItem : configurations) {
			treeItem.select();
			builder.add(getPitConfiguration(treeItem));
		}
		return builder.build();
	}

	private PitRunConfiguration getPitConfiguration(SWTBotTreeItem treeItem) {
		String name = treeItem.getText();
		String project = bot.textWithLabel("Project to mutate:").getText();
		Builder builder = new Builder().withName(name).withProjects(project);
		return builder.build();
	}

	private SWTBotTreeItem activateShell() {
		SWTBotShell shell = bot.shell("Run Configurations");
		shell.activate();
		for (SWTBotTreeItem treeItem : bot.tree().getAllItems()) {
			if ("PIT Mutation Test".equals(treeItem.getText())) {
				treeItem.select();
				treeItem.expand();
				return treeItem;
			}
		}
		return null;
	}
}