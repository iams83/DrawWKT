
package drawwkt;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;


public class AboutDialog
{
    static final Dimension DEFAULT_SIZE = new Dimension(500, 300);
    
    static public void showAboutDialog(final JFrame owner)
    {
        final JDialog dialog = new JDialog(owner, "About Geometra...", true);
        
        dialog.setResizable(false);
        
        JLabel appTitle = new JLabel(" DrawWKT", JLabel.CENTER);
        appTitle.setBorder(new EmptyBorder(30, 0, 10, 0));
        appTitle.setIcon(new ImageIcon(Main.appIcon));
        appTitle.setFont(new Font("Arial", Font.BOLD, 32));
        dialog.add(appTitle, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridLayout(0, 1));
        centerPanel.add(new JLabel("Version: " + Main.version, JLabel.CENTER));
        centerPanel.add(new JLabel("Written by: Ismael Aguilera", JLabel.CENTER));
        centerPanel.add(new JLabel("Released at December 1st, 2013", JLabel.CENTER));
        centerPanel.add(new JLabel("This product includes icons from GeoGebra (http://www.geogebra.org)", JLabel.CENTER));
        dialog.add(centerPanel);

        JPanel buttonsPanel = new JPanel(new FlowLayout());
        JButton okButton = new JButton("Close");
        buttonsPanel.add(okButton);
        
        dialog.add(buttonsPanel, BorderLayout.SOUTH);
        buttonsPanel.setBorder(new EmptyBorder(20, 0, 10, 0));
        
        okButton.addActionListener(new ActionListener() 
        {
            public void actionPerformed(ActionEvent e)
            {
                dialog.setVisible(false);
            }
            
        });
        
        dialog.setSize(DEFAULT_SIZE);
        dialog.setLocationByPlatform(true);

        dialog.setLocation(owner.getX() + (owner.getWidth()  - DEFAULT_SIZE.width) / 2,
                owner.getY() + (owner.getHeight() - DEFAULT_SIZE.height) / 2);

        dialog.setVisible(true);
    }
}
