package wkt;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.io.PrintWriter;

abstract public class Geometry
{
	private Color color;
	
	private int offset, length;

    abstract public void paintVertexes(Graphics2D g2, AffineTransform tx, Color fillingColor, Color borderColor);
	
	abstract public void paint(Graphics2D g2, AffineTransform tx, Color fillingColor, Color borderColor);

	abstract public Rectangle2D getBoundingBox();

	abstract public void diagnoseProblems(PrintWriter pw, int indent);

    abstract public Point snapPoint(Point p, double distanceSq);
    
    abstract public Point snapLine(Point p, double distanceSq);

    abstract public boolean containsPoint(Point p);
    
    public void setColor(Color givenColor)
    {
        this.color = givenColor;
    }

    public Color getColor()
    {
        return this.color;
    }

    public void setTextLocation(int offset, int length)
    {
        this.offset = offset;
        this.length = length;
    }

    public int getTextOffset()
    {
        return this.offset;
    }
    
    public int getTextLength()
    {
        return this.length;
    }
}
