package drawwkt.tools;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import wkt.Point;
import drawwkt.ui.MainWindow;

public class NewPointTool extends GeometraTool
{
    final MainWindow frame;
    
    public NewPointTool(MainWindow frame)
    {
        super("Tool_New_Point");
        
        this.frame = frame;
    }
    
    @Override
    public void paint(Graphics2D g2, AffineTransform tx)
    {
        // Do nothing
    }

    @Override
    public void mouseClicked(Point p)
    {
        this.frame.addGeometries(p);
        
        this.frame.repaint();
    }
}
