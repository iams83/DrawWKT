package wkt;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.io.PrintWriter;
import java.util.Arrays;

public class Polygon extends Geometry
{
	final public LineString shell, holes[];

    public Polygon(LineString shell)
    {
        this.shell = shell;
        this.holes = new LineString[0];
    }

	public Polygon(LineString shell, LineString ... holes)
	{
		this.shell = shell;
		this.holes = holes;
	}

	@Override
	public void paintVertexes(Graphics2D g2, AffineTransform tx, Color fillingColor, Color borderColor)
	{
		this.shell.paintVertexes(g2, tx, fillingColor, borderColor);
		
		for (LineString p : this.holes)
			p.paintVertexes(g2, tx, fillingColor, borderColor);
	}
	
	@Override
	public void paint(Graphics2D g2, AffineTransform tx, Color fillingColor, Color borderColor)
	{
	    if (fillingColor != null)
	    {
    		Path2D.Double fillingShape = new Path2D.Double(Path2D.WIND_EVEN_ODD);
    		
    		this.shell.fillPath2D(fillingShape);
    		
    		for (LineString ls : this.holes)
    			ls.fillPath2D(fillingShape);
    		
    		g2.setColor(fillingColor);
    		fillingShape.transform(tx);
    		g2.fill(fillingShape);
	    }
	    
	    if (borderColor != null)
	    {
    		Path2D.Double border = this.shell.toPath2D();
    		
    		g2.setColor(borderColor);
    		
    		border.transform(tx);
    		g2.draw(border);
    
    		for (LineString ls : this.holes)
    		{
    			Path2D.Double hole = ls.toPath2D();
    			hole.transform(tx);
    			g2.draw(hole);
    		}
	    }
	}

	@Override
	public String toString()
	{
		StringBuffer holeString = new StringBuffer();
		
		for (LineString ls : this.holes)
			holeString.append(",(" + ls.toWKTStringPart() + ")");
		
		return "POLYGON((" + this.shell.toWKTStringPart() + ")" + holeString + ")";
	}
	
	@Override
	public Rectangle2D getBoundingBox()
	{
		return this.shell.toPath2D().getBounds2D();
	}

	@Override
	public void diagnoseProblems(PrintWriter pw, int indent)
	{
		char[] indentAsChars = new char[indent];
		Arrays.fill(indentAsChars, ' ');
		String indentString = new String(indentAsChars);
		
		pw.println(indentString + "Shell:");
		this.shell.diagnoseProblems(pw, indent + 4);
		
		int n = 0;
		
		for (LineString ls : this.holes)
		{
			n ++;
			pw.println(indentString + "Hole " + n + ":");
			ls.diagnoseProblems(pw, indent + 4);
		}
	}

    @Override
    public Point snapPoint(Point p, double distanceSq)
    {
        for (LineString ls : this.holes)
        {
            Point p0 = ls.snapPoint(p, distanceSq);
            
            if (p0 != null)
                return p0;
        }
        
        Point p0 = this.shell.snapPoint(p, distanceSq);
        
        if (p0 != null)
            return p0;
        
        return null;
    }

    @Override
    public Point snapLine(Point p, double distanceSq)
    {
        for (LineString ls : this.holes)
        {
            Point p0 = ls.snapLine(p, distanceSq);
            
            if (p0 != null)
                return p0;
        }
        
        Point p0 = this.shell.snapLine(p, distanceSq);
        
        if (p0 != null)
            return p0;
        
        return null;
    }

    private boolean shellContainsPoint(LineString ls, Point test)
    {
        int i;
        int j;
        boolean result = false;
        for (i = 0, j = ls.points.length - 1; i < ls.points.length; j = i++) {
          if ((ls.points[i].y > test.y) != (ls.points[j].y > test.y) &&
              (test.x < (ls.points[j].x - ls.points[i].x) * (test.y - ls.points[i].y) / (ls.points[j].y-ls.points[i].y) + ls.points[i].x)) {
            result = !result;
           }
        }
        return result;
    }
    
    @Override
    public boolean containsPoint(Point p)
    {
        if (this.shellContainsPoint(this.shell, p))
        {
            for (LineString ls : this.holes)
            {
                if (this.shellContainsPoint(ls, p))
                    return false;
            }
            
            return true;
        }
        
        return false;
    }
}
