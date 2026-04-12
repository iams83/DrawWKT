package drawwkt.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.Random;

import javax.swing.ImageIcon;

public class Palette
{
    private Random randomColor = new Random();

    private float baseColor;
    
    final private BufferedImage image = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
    
    final private ImageIcon imageIcon = new ImageIcon(this.image);

    public Palette()
    {
        this.randomize();
    }
    
    public Palette(float base)
    {
        this.baseColor = base;
        
        this.updateImageIcon();
    }

    public void randomize()
    {
        this.randomColor = new Random();
        
        this.baseColor = this.randomColor.nextFloat();
        
        this.updateImageIcon();
    }
    
    private void updateImageIcon()
    {
        Graphics g = this.image.createGraphics();
        
        g.setColor(Color.getHSBColor(this.baseColor, 1f, 1f));
        
        g.fillRect(0, 0, 10, 10);
        
        g.dispose();
    }

    public ImageIcon getIcon()
    {
        return imageIcon;
    }
    
    public void reset()
    {
        this.randomColor.setSeed(0);
    }
    
    public Color getNextColor()
    {
        float f = this.baseColor + -0.15f + 0.3f * randomColor.nextFloat();
        
        if (f < 0)
            f += 1.0f;
        
        if (f >= 1.0f)
            f -= 1.0f;
        
        return Color.getHSBColor(f, 1f, 1f);
    }
}
