package drawwkt.tools;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.util.LinkedList;

import wkt.Geometry;
import wkt.LineString;
import wkt.Point;
import drawwkt.ui.MainWindow;

public class NewPolylineTool extends GeometraTool
{
    final MainWindow frame;
    
    private Point point;
    
    private LinkedList<Point> measurements = new LinkedList<Point>();
    
    public NewPolylineTool(MainWindow frame)
    {
        super("Tool_Polyline");
        
        this.frame = frame;
    }

    @Override
    public Geometry getCurrentGeometry(boolean includeCurrent)
    {
        LinkedList<Point> measurements = new LinkedList<Point>(this.measurements);
        
        if (includeCurrent && this.point != null && 
                (measurements.isEmpty() || measurements.getLast() != this.point))
        {
            measurements.add(this.point);
        }

        if (measurements.isEmpty())
            return null;
        
        if (measurements.size() == 1)
            return measurements.getFirst();
        
        return new LineString(measurements.toArray(new Point[0]));
    }

    @Override
    public void paint(Graphics2D g2, AffineTransform tx)
    {
        Geometry g = this.getCurrentGeometry(true);
        
        if (g != null && !(g instanceof Point))
            g.paint(g2, tx, null, Color.black);
    }

    @Override
    public void mouseMoved(Point p)
    {
        this.point = p;
        
        this.frame.repaint();
    }

    @Override
    public void mouseClicked(Point p)
    {
        if (this.measurements.isEmpty() || this.measurements.getLast() != this.point)
        {
            this.measurements.add(p);
            this.point = p;
        
            this.frame.repaint();
        }
    }

    @Override
    public void mouseDoubleClicked(Point p)
    {
        this.point = null;

        Geometry g = this.getCurrentGeometry(true);
        
        if (g != null)
            this.frame.addGeometries(g);
        
        this.measurements.clear();
        
        this.frame.repaint();
    }

    @Override
    public void clear()
    {
        this.measurements.clear();
        this.point = null;
        
        this.frame.repaint();
    }
}
