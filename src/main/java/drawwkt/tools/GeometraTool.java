package drawwkt.tools;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import wkt.Geometry;
import wkt.Point;

abstract public class GeometraTool
{
    final private String iconFileName;
    
    public GeometraTool(String iconFileName)
    {
        this.iconFileName = iconFileName;
    }

    final public ImageIcon getIcon() throws IOException
    {
        return new ImageIcon(ImageIO.read(NewPolylineTool.class.getResourceAsStream("/drawwkt/icons/" + this.iconFileName + ".png")));
    }

    public Geometry getCurrentGeometry(boolean includeCurrent) { return null; }
    
    abstract public void paint(Graphics2D g2, AffineTransform tx);

    public void mouseMoved(Point p) {}

    public void mouseDragged(Point p) {}

    public void mouseReleased(Point p) {}

    public void mousePressed(Point p) {}

    public void mouseExited(Point p) {}

    public void mouseEntered(Point p) {}

    public void mouseClicked(Point p) {}

    public void mouseDoubleClicked(Point p) {}

    public void clear() {}
}
