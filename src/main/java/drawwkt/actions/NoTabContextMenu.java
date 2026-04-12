package drawwkt.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import drawwkt.ui.MainWindow;

@SuppressWarnings("serial")
public class NoTabContextMenu extends JPopupMenu
{
    public NoTabContextMenu(final MainWindow mainWindow)
    {
        JMenuItem newItem = new JMenuItem("New");
        
        newItem.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                mainWindow.createNewGeometryPanel(MainWindow.DEFAULT_TAB_NAME);
            }
        });
        
        this.add(newItem);
        
        this.addSeparator();

        JMenuItem closeAllItem = new JMenuItem("Close all tabs");

        closeAllItem.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                mainWindow.closeAll();
            }
        });
        
        this.add(closeAllItem);
    }

}
