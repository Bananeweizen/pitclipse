/*******************************************************************************
 * Copyright 2012-2019 Phil Glover and contributors
 *  
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *  
 *  http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/

package org.pitest.pitclipse.ui.behaviours.pageobjects;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.pitest.pitclipse.ui.behaviours.StepException;
import org.pitest.pitclipse.ui.swtbot.PitNotifier;
import org.pitest.pitclipse.ui.swtbot.PitResultsView;

public class PitSummaryView {

    private PitResultsView lastResults = null;

    public PitSummaryView() {
    }

    public void waitForUpdate() {
        try {
            System.out.println("SUMMARY VIEW IS WAITING FOR UPDATE");
            
            for (SWTBotShell shell : new SWTWorkbenchBot().shells()) {
                System.out.println("    SHELL: " + shell.getId() + " " + shell.getText() + " ; " + shell);
                
                if (shell.getText().contains("Errors in Workspace")) {
                    System.out.println("       => Seems like compilation problems prevent the run :(");
                    
                    SWTBotView view = new SWTWorkbenchBot().viewByPartName("Problems");
                    view.show();
                    SWTBotTree tree = view.bot().tree();
                    String category = "Errors";
                    int nbOfErrors = 0;
                    for (SWTBotTreeItem item : tree.getAllItems()) {
                        String text = item.getText();
                        System.out.println("         PROBLEM: " + text);
                        if (text != null && text.startsWith(category)) {
                            item.expand();

                            for (String problem : item.getNodes()) {
                                System.out.println("         PROBLEM: " + problem);
                            }
                            break;
                        }
                    }
                }
            }
            lastResults = PitNotifier.INSTANCE.getResults();
            System.out.println("SUMMARY VIEW HAS BEEN UPDATED");
        } catch (InterruptedException e) {
            throw new StepException(e);
        }
    }

    public int getClassesTested() {
        return lastResults.getClassesTested();
    }

    public double getOverallCoverage() {
        return lastResults.getTotalCoverage();
    }

    public double getMutationCoverage() {
        return lastResults.getMutationCoverage();
    }
}
