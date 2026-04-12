package drawwkt.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;

import drawwkt.ui.GeometryTextArea;
import drawwkt.ui.MainWindow;
import drawwkt.ui.Palette;

@SuppressWarnings("serial")
public class TabContextMenu extends JPopupMenu
{
    public TabContextMenu(final MainWindow mainWindow, final GeometryTextArea selectedTextArea)
    {
        JCheckBoxMenuItem setVisibleItem = new JCheckBoxMenuItem("Visible", selectedTextArea.isGeometryVisible());
        
        setVisibleItem.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                selectedTextArea.setGeomertryVisible(!selectedTextArea.isGeometryVisible());
                
                mainWindow.updateHiddenIcon(selectedTextArea);
                
                mainWindow.reloadGeometriesFromText();
            }
        });

        this.add(setVisibleItem);

        JMenuItem setAllVisibleItem = new JMenuItem("Set all visible but this");
        
        setAllVisibleItem.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                for (GeometryTextArea panel : mainWindow.getAllTextPanels())
                {
                    panel.setGeomertryVisible(selectedTextArea != panel);
                
                    mainWindow.updateHiddenIcon(panel);
                }
                
                mainWindow.reloadGeometriesFromText();
            }
        });

        this.add(setAllVisibleItem);

        JMenuItem setAllHiddenItem = new JMenuItem("Set all hidden but this");
        
        setAllHiddenItem.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                for (GeometryTextArea panel : mainWindow.getAllTextPanels())
                {
                    panel.setGeomertryVisible(selectedTextArea == panel);
                
                    mainWindow.updateHiddenIcon(panel);
                }
                
                mainWindow.reloadGeometriesFromText();
            }
        });

        this.add(setAllHiddenItem);

        this.addSeparator();
        
        JMenu colorMenu = new JMenu("Set color");
        
        final int N_COLORS = 10;
        
        for (int i = 0; i < N_COLORS; i ++)
        {
            final Palette palette = new Palette(1.0f * i / N_COLORS);
            
            JMenuItem newColorItem = new JMenuItem(palette.getIcon());
            
            newColorItem.addActionListener(new ActionListener()
            {
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    selectedTextArea.setPalette(palette);
                    
                    selectedTextArea.setGeomertryVisible(true);
                    
                    mainWindow.updateHiddenIcon(selectedTextArea);
                    
                    mainWindow.reloadGeometriesFromText();
                }
            });
            
            colorMenu.add(newColorItem);
        }
        
        this.add(colorMenu);

        JMenuItem centerItem = new JMenuItem("Center on this geometry");
        
        centerItem.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                mainWindow.initializeBoundingBox(selectedTextArea);
            }
        });
        
        this.add(centerItem);

        this.addSeparator();
        
        JMenuItem renameItem = new JMenuItem("Rename...");
        
        renameItem.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                String newName = JOptionPane.showInputDialog(mainWindow, "Enter new name:",
                        mainWindow.getGeometryTextAreaName(selectedTextArea));
                
                if (newName != null)
                    mainWindow.setGeometryTextAreaName(selectedTextArea, newName);
            }
        });

        this.add(renameItem);

        JMenuItem clearMenuItem = new JMenuItem("Clear");

        clearMenuItem.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                mainWindow.clearGeometries(selectedTextArea);
            }
        });
        
        this.add(clearMenuItem);
        
        JMenuItem appendItem = new JMenuItem("Append file...");
        
        appendItem.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                mainWindow.appendFile(selectedTextArea);
            }
        });

        this.add(appendItem);
        
        JMenuItem saveAsMenuItem = new JMenuItem("Save as...");

        saveAsMenuItem.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                mainWindow.save(selectedTextArea);
            }
        });
        
        this.add(saveAsMenuItem);
        
        JMenuItem closeItem = new JMenuItem("Close");

        closeItem.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                mainWindow.close(selectedTextArea);
            }
        });

        this.add(closeItem);
    }

}
