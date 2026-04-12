package drawwkt.actions;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import drawwkt.ui.MainWindow;
import wkt.Geometry;

@SuppressWarnings("serial")
public class DiagnoseMenu extends JMenu
{
    final private MainWindow frame;
    
    public DiagnoseMenu(MainWindow frame)
    {
        super("Diagnostics");
        
        this.frame = frame;

        JMenuItem diagnoseProblemMenuItem = new JMenuItem("Diagnose problems...");
        
        diagnoseProblemMenuItem.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                DiagnoseMenu.this.diagnose();
            }
        });
        
        this.add(diagnoseProblemMenuItem);
    }

    private void diagnose()
    {
        final JDialog dialog = new JDialog(this.frame, "Diagnose problems...");
        
        final JTextArea diagnosticPanel = new JTextArea();
        diagnosticPanel.setRows(8);
        diagnosticPanel.setTabSize(4);
        diagnosticPanel.setLineWrap(true);
        diagnosticPanel.setWrapStyleWord(true);
        diagnosticPanel.setFont(MainWindow.DefaultFont);
        diagnosticPanel.setEditable(false);

        StringWriter sw = new StringWriter();
        
        PrintWriter pw = new PrintWriter(sw);
        
        Geometry[] geometries = this.frame.getGeometries();
        
        if (geometries.length == 0)
            sw.write("No geometries found.");
        
        int n = 0;
        
        for (Geometry geometry : geometries)
        {
            n ++;
            
            pw.println("Geometry " + n + ": " + geometry.getClass().getSimpleName());
            
            geometry.diagnoseProblems(pw, 4);
            
            pw.println();
        }
        
        diagnosticPanel.setText(sw.toString());

        JButton cancelButton = new JButton("Close");
        cancelButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                dialog.setVisible(false);
            }
        });
        
        JPanel closePanel = new JPanel();
        closePanel.add(cancelButton);
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JScrollPane(diagnosticPanel));
        panel.add(closePanel, BorderLayout.SOUTH);
        
        dialog.add(panel);
        dialog.setSize(800, 300);
        dialog.setLocationRelativeTo(this.frame);
        dialog.setVisible(true);
    }

}
