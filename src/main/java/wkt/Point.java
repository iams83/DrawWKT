package wkt;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class Point extends Geometry
{
	final public String xs, ys;
	final public double x, y;
	
    public Point(double x, double y)
    {
        DecimalFormat df = new DecimalFormat("0.00000", new DecimalFormatSymbols(Locale.ENGLISH));

        this.xs = df.format(x);
        this.ys = df.format(y);
        this.x = x;
        this.y = y;
    }
    
	public Point(String xs, String ys)
	{
		this.xs = xs;
		this.ys = ys;
		this.x = Double.parseDouble(xs);
		this.y = Double.parseDouble(ys);
	}
	
	public Point2D.Double toPoint2D()
	{
		return new Point2D.Double(this.x, this.y);
	}

	@Override
	public void paintVertexes(Graphics2D g2, AffineTransform tx, Color fillingColor, Color borderColor)
	{
		g2.setColor(borderColor);
		
		Point2D point = tx.transform(toPoint2D(), null);
		
		Ellipse2D.Double ellipse = new Ellipse2D.Double(point.getX() - 2, point.getY() - 2, 4, 4);
		
		g2.setColor(fillingColor);
		g2.fill(ellipse);

		g2.setColor(borderColor);
		g2.draw(ellipse);
	}

	@Override
	public void paint(Graphics2D g2, AffineTransform tx, Color fillingColor, Color borderColor)
	{
		g2.setColor(borderColor);
		
		Point2D point = tx.transform(toPoint2D(), null);
		
		Ellipse2D.Double ellipse = new Ellipse2D.Double(point.getX() - 5, point.getY() - 5, 10, 10);
		
		g2.setColor(fillingColor);
		g2.fill(ellipse);

		g2.setColor(borderColor);
		g2.draw(ellipse);
	}

	@Override
	public Rectangle2D getBoundingBox()
	{
		return new Rectangle2D.Double(this.x, this.y, 0, 0);
	}

	public String toWKTStringPart()
	{
		return this.xs + " " + this.ys;
	}
	
	@Override
	public String toString()
	{
		return "POINT(" + toWKTStringPart() + ")";
	}

	@Override
	public void diagnoseProblems(PrintWriter pw, int indent)
	{
	}

	@Override
	public boolean equals(Object other)
	{
		if (other == null || !(other instanceof Point))
			return false;
		
		Point otherPoint = (Point) other;
		
		return this.xs.equals(otherPoint.xs) &&
				this.ys.equals(otherPoint.ys);
	}
	
	public double distanceSq(Point other)
	{
		double dx = this.x - other.x;
		double dy = this.y - other.y;
		
		return dx * dx + dy * dy;
	}

    @Override
    public Point snapPoint(Point p, double distanceSq)
    {
        if (this.distanceSq(p) < distanceSq)
            return this;

        return null;
    }

    @Override
    public Point snapLine(Point p, double distanceSq)
    {
        return null;
    }

    @Override
    public boolean containsPoint(Point p)
    {
        return false;
    }
}
