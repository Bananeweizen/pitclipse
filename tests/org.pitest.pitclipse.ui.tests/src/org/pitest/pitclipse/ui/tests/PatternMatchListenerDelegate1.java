package org.pitest.pitclipse.ui.tests;

import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.ui.console.IPatternMatchListenerDelegate;
import org.eclipse.ui.console.PatternMatchEvent;
import org.eclipse.ui.console.TextConsole;

public class PatternMatchListenerDelegate1 implements IPatternMatchListenerDelegate {
    
    private TextConsole console;
    private IDocumentListener listener;

    @Override
    public void connect(TextConsole console) {
        this.console = console;
        listener = new IDocumentListener() {
            
            @Override
            public void documentChanged(DocumentEvent event) {
                System.out.println(event.fText);
            }
            
            @Override
            public void documentAboutToBeChanged(DocumentEvent event) {
                // TODO Auto-generated method stub
                
            }
        };
        console.getDocument().addDocumentListener(listener);
    }

    @Override
    public void disconnect() {
        this.console.getDocument().removeDocumentListener(listener);
        this.console = null;
    }

    @Override
    public void matchFound(PatternMatchEvent event) {
        
    }

}
