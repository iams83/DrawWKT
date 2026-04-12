package drawwkt.actions;

import drawwkt.AboutDialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import drawwkt.ui.MainWindow;

@SuppressWarnings("serial")
public class HelpMenu extends JMenu
{
    public HelpMenu(final MainWindow frame)
    {
        super("Help");
        
        JMenuItem aboutMenuItem = new JMenuItem("About...");
        
        aboutMenuItem.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                AboutDialog.showAboutDialog(frame);
            }
        });
        
        this.add(aboutMenuItem);
    }
}
