package drawwkt.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import drawwkt.ui.GeometryTextArea;
import drawwkt.ui.MainWindow;

@SuppressWarnings("serial")
public class FileMenu extends JMenu
{
    public FileMenu(final MainWindow mainWindow)
    {
        super("File");

        JMenuItem newMenuItem = new JMenuItem("New");
        
        newMenuItem.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                mainWindow.createNewGeometryPanel(MainWindow.DEFAULT_TAB_NAME);
            }
        });
        
        this.add(newMenuItem);

        JMenuItem openMenuItem = new JMenuItem("Open file...");

        openMenuItem.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                mainWindow.openFile();
            }
        });

        this.add(openMenuItem);

        JMenuItem openSpainSampleMenuItem = new JMenuItem("World Map");
        JMenu openSampleMenu = new JMenu("Open Sample");
        openSampleMenu.add(openSpainSampleMenuItem);

        openSpainSampleMenuItem.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                try (InputStreamReader is = 
                        new InputStreamReader(MainWindow.class.getResourceAsStream("/drawwkt/samples/worldmap.wkt")))
                {
                    mainWindow.addGeometries(is, "World Map");
                }
                catch (IOException e1)
                {
                    JOptionPane.showMessageDialog(mainWindow, e1.getMessage());
                }
            }
        });

        this.add(openSampleMenu);

        this.addSeparator();
        
        JMenuItem clearMenuItem = new JMenuItem("Clear");

        clearMenuItem.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                mainWindow.clearGeometries(mainWindow.getCurrentTextArea());
            }
        });
        
        this.add(clearMenuItem);

        JMenuItem saveAsMenuItem = new JMenuItem("Save as...");

        saveAsMenuItem.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                GeometryTextArea selectedTextArea = mainWindow.getCurrentTextArea();
                
                mainWindow.save(selectedTextArea);
            }
        });
        
        this.add(saveAsMenuItem);

        this.addSeparator();

        JMenuItem setAllVisibleItem = new JMenuItem("Set all visible");
        
        setAllVisibleItem.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                for (GeometryTextArea panel : mainWindow.getAllTextPanels())
                {
                    panel.setGeomertryVisible(true);
                
                    mainWindow.updateHiddenIcon(panel);
                }
                
                mainWindow.reloadGeometriesFromText();
            }
        });

        this.add(setAllVisibleItem);

        JMenuItem setAllHiddenItem = new JMenuItem("Set all hidden");
        
        setAllHiddenItem.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                for (GeometryTextArea panel : mainWindow.getAllTextPanels())
                {
                    panel.setGeomertryVisible(false);
                
                    mainWindow.updateHiddenIcon(panel);
                }
                
                mainWindow.reloadGeometriesFromText();
            }
        });

        this.add(setAllHiddenItem);

        this.addSeparator();

        JMenuItem closeMenuItem = new JMenuItem("Close");

        closeMenuItem.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                GeometryTextArea selectedTextArea = mainWindow.getCurrentTextArea();
                
                mainWindow.close(selectedTextArea);
            }
        });

        this.add(closeMenuItem);

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
