package drawwkt;

import java.awt.Image;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import drawwkt.ui.MainWindow;

public class Main
{
    static public Image appIcon;
    
    static public String version = "1.7";
    
    static
    {
        try
        {
            Main.appIcon = ImageIO.read(Main.class.getResourceAsStream("/drawwkt/icons/Lavanda.png"));
        }
        catch(IOException e)
        {
            throw new RuntimeException(e);
        }
    }

	public static void main(String[] args) throws IOException
	{
	    try
        {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (ClassNotFoundException e)
        {
            e.printStackTrace(System.err);
        }
        catch (InstantiationException e)
        {
            e.printStackTrace(System.err);
        }
        catch (IllegalAccessException e)
        {
            e.printStackTrace(System.err);
        }
        catch (UnsupportedLookAndFeelException e)
        {
            e.printStackTrace(System.err);
        }
        
		final MainWindow frame = new MainWindow();
		frame.setIconImage(appIcon);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(600, 600);
		frame.setLocationByPlatform(true);
		frame.setVisible(true);

		SwingUtilities.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
                frame.initializeBoundingBox();
            }
        });
	}
}
