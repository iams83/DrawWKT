package wkt;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.io.PrintWriter;
import java.util.Arrays;

public class LineString extends Geometry
{
	final public Point[] points;
	
	public LineString(Point ... points)
	{
		this.points = points;
	}

	@Override
	public void paintVertexes(Graphics2D g2, AffineTransform tx, Color fillingColor, Color borderColor)
	{
		int n = 0;
		
		for (Point p : this.points)
		{
			p.paintVertexes(g2, tx, n == 0 ? borderColor : fillingColor, borderColor);

			n ++;
		}
	}
	
	@Override
	public void paint(Graphics2D g2, AffineTransform tx, Color fillingColor, Color borderColor)
	{
		g2.setColor(borderColor);
		
		Path2D.Double p = toPath2D();
		
		p.transform(tx);
		
		g2.draw(p);
	}
	
	public void fillPath2D(Path2D.Double p)
	{
	    if (points.length > 0)
	        p.moveTo(points[0].x, points[0].y);
		
		for (int i = 1; i < points.length; i ++)
			p.lineTo(points[i].x, points[i].y);
	}
	
	public Path2D.Double toPath2D()
	{
		Path2D.Double p = new Path2D.Double();

		fillPath2D(p);

		return p;
	}

	@Override
	public Rectangle2D getBoundingBox()
	{
		return toPath2D().getBounds2D();
	}

	public String toWKTStringPart()
	{
		StringBuffer sb = new StringBuffer();
		
		for (Point p : this.points)
		{
			if (sb.length() != 0)
				sb.append(",");
			
			sb.append(p.toWKTStringPart());
		}
		
		return sb.toString();
	}
	
	@Override
	public String toString()
	{
		return "LINESTRING(" + toWKTStringPart() + ")";
	}

	private double getSignedArea()
	{
		double signedArea = 0;
    	
		for (int i = 0, j = this.points.length - 1; i < this.points.length; j = i ++)
			signedArea += this.points[i].x * this.points[j].y - this.points[j].x * this.points[i].y;
		
		return signedArea;
	}
    
    public boolean isCCW()
    {
		return getSignedArea() < 0;
    }

    public int getNumSelfIntersections()
	{
		int selfIntersections = 0;
		
		for (int i = 0, j = this.points.length - 1; i < this.points.length; j = i ++)
		{
			Point pi1 = this.points[i];
			Point pi2 = this.points[j];
			
			for (int k = i + 1; k < this.points.length - 1; k ++)
			{
				Point pj1 = this.points[k];
				Point pj2 = this.points[k + 1];
				
				if (!pi1.equals(pi2) && !pi1.equals(pj1) && !pi1.equals(pj2) &&
					                    !pi2.equals(pj1) && !pi2.equals(pj2) &&
					                                        !pj1.equals(pj2) &&
					Line2D.linesIntersect(pi1.x, pi1.y, pi2.x, pi2.y, pj1.x, pj1.y, pj2.x, pj2.y))
				{
					selfIntersections ++;
				}
			}
		}
		
		return selfIntersections;
	}
	
	@Override
	public void diagnoseProblems(PrintWriter pw, int indent)
	{
		char[] indentAsChars = new char[indent];
		Arrays.fill(indentAsChars, ' ');
		String indentString = new String(indentAsChars);
		
		pw.println(indentString + this.points.length + " points");
		
		double minDistanceSq = Double.MAX_VALUE;
		double maxDistanceSq = Double.MIN_VALUE;
		
		int equalConsecutivePairs = 0;
		
		for (int i = 0, j = this.points.length - 1; i < this.points.length; j = i ++)
		{
			if (this.points[i].equals(this.points[j]))
				equalConsecutivePairs ++;
			else
			{
				double dSq = this.points[i].distanceSq(this.points[j]);
				
				if (dSq < minDistanceSq)
					minDistanceSq = dSq;

				if (dSq > maxDistanceSq)
					maxDistanceSq = dSq;
			}
		}
		
		if (this.points[0].equals(this.points[this.points.length - 1]))
		{
			pw.println(indentString + "First and last point are equal.");
			equalConsecutivePairs --;
		}
		else
		{
			pw.println(indentString + "First and last point are not equal.");
		}
		
		pw.println(indentString + "Min distance between consecutive vertices: " + Math.sqrt(minDistanceSq));
		pw.println(indentString + "Max distance between consecutive vertices: " + Math.sqrt(maxDistanceSq));

		if (equalConsecutivePairs == 0)
			pw.println(indentString + "No consecutive points are equal.");
		else
			pw.println(indentString + equalConsecutivePairs + " consecutive points are equal.");

		pw.println(indentString + "Area: " + this.getSignedArea());
		pw.println(indentString + "Orientation: " + (this.isCCW() ? "Counter-clockwise" : "Clockwise"));
		pw.println(indentString + this.getNumSelfIntersections() + " self-intersections.");
	}

    @Override
    public Point snapPoint(Point p, double distanceSq)
    {
        for (Point v : this.points)
        {
            Point p0 = v.snapPoint(p, distanceSq);
            
            if (p0 != null)
                return p0;
        }
        
        return null;
    }

    private Point snapLine(Point pointA, Point pointB, Point c, double distanceSq)
    {
        double dx = pointB.x - pointA.x;
        double dy = pointB.y - pointA.y;
        
        double lengthSq = dx * dx + dy * dy;
        
        double t = ((c.x - pointA.x) * (pointB.x - pointA.x)
                  + (c.y - pointA.y) * (pointB.y - pointA.y)) / lengthSq;

        if (t < 0 || t > 1)
            return null;

        Point point = new Point(
                pointA.x + t * (pointB.x - pointA.x), 
                pointA.y + t * (pointB.y - pointA.y));
        
        if (point.distanceSq(c) < distanceSq)
            return point;

        return null;
    }
    
    @Override
    public Point snapLine(Point p, double distanceSq)
    {
        for (int i = 1; i < this.points.length; i ++)
        {
            Point pointA = this.points[i - 1];
            Point pointB = this.points[i];
            
            Point p0 = snapLine(pointA, pointB, p, distanceSq);
                    
            if (p0 != null)
                return p0;
        }
        
        return null;
    }

    @Override
    public boolean containsPoint(Point p)
    {
        return false;
    }

}
