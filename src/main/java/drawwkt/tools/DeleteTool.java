package drawwkt.tools;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import wkt.Geometry;
import wkt.Point;
import drawwkt.ui.MainWindow;

public class DeleteTool extends GeometraTool
{
    final MainWindow frame;
    
    private Geometry[] highlightGeometries;
    
    public DeleteTool(MainWindow frame)
    {
        super("Tool_Delete");
        
        this.frame = frame;
    }
    
    @Override
    public void paint(Graphics2D g2, AffineTransform tx)
    {
        if (this.highlightGeometries != null)
        {
            for (Geometry g : this.highlightGeometries)
                g.paint(g2, tx, null, Color.white);
        }
    }

    @Override
    public void mouseMoved(Point p)
    {
        this.highlightGeometries = this.frame.getCurrentTextPanelGeometries(p);
        
        this.frame.repaint();
    }

    @Override
    public void mouseClicked(Point p)
    {
        this.frame.deleteGeometries(p);
        
        this.frame.repaint();
    }
}
