package wkt;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;

import drawwkt.reader.GeometryReader;

public class WKTReader extends GeometryReader
{
    final public int xCoord, yCoord;
    
    public WKTReader(int xCoord, int yCoord)
    {
        this.xCoord = xCoord;
        this.yCoord = yCoord;
    }
    
    @Override
    public String toString()
    {
        return "WKT " + ("XYZ".charAt(this.xCoord)) + ("XYZ".charAt(this.yCoord));
    }
    
	private Point parsePointToken(String point) throws ParseException
	{
		String[] tokens = point.trim().split(" ");
		
		int minCoords = Math.max(this.xCoord, this.yCoord) + 1;
		
		if (tokens.length < minCoords)
			throw new ParseException("Point must contain at least " + minCoords + " numbers separated by ' '", 0);
		
		try
		{
			return new Point(tokens[this.xCoord], tokens[this.yCoord]);
		}
		catch(NumberFormatException e)
		{
			throw new ParseException("Could not parse point coordinate. " + e.getMessage(), 0);
		}
	}

	private Collection<Point> parsePoint(String geomWKT) throws ParseException
	{
        if (geomWKT.trim().equals("EMPTY"))
            return new LinkedList<Point>();
        
		geomWKT = removeEnclosingParenthesis(geomWKT, "Point");
		
		return Arrays.asList(parsePointToken(geomWKT));
	}

	private Collection<LineString> parseLineString(String geomWKT) throws ParseException
	{
        if (geomWKT.trim().equals("EMPTY"))
            return new LinkedList<LineString>();
        
		geomWKT = removeEnclosingParenthesis(geomWKT, "LineString");
		
		ArrayList<Point> points = new ArrayList<Point>();
		
		for (StringToken token : breakTokens(geomWKT, ','))
			points.add(parsePointToken(token.token));
		
		return Arrays.asList(new LineString(points.toArray(new Point[0])));
	}

	private Collection<Polygon> parsePolygon(String geomWKT) throws ParseException
	{
        if (geomWKT.trim().equals("EMPTY"))
            return new LinkedList<Polygon>();
        
		geomWKT = removeEnclosingParenthesis(geomWKT, "Polygon");
		
		ArrayList<LineString> shapes = new ArrayList<LineString>();
		
		for (StringToken token : breakTokens(geomWKT, ','))
			shapes.addAll(parseLineString(token.token));
		
		LineString shell = shapes.remove(0);
		
		return Arrays.asList(new Polygon(shell, shapes.toArray(new LineString[0])));
	}

	private Collection<LineString> parseMultiLineString(String geomWKT) throws ParseException
	{
        if (geomWKT.trim().equals("EMPTY"))
            return new LinkedList<LineString>();
        
		geomWKT = removeEnclosingParenthesis(geomWKT, "MultiLineString");
		
		ArrayList<LineString> shapes = new ArrayList<LineString>();
		
		for (StringToken token : breakTokens(geomWKT, ','))
			shapes.addAll(parseLineString(token.token));
		
		return shapes;
	}

	private Collection<Polygon> parseMultiPolygon(String geomWKT) throws ParseException
	{
        if (geomWKT.trim().equals("EMPTY"))
            return new LinkedList<Polygon>();
        
		geomWKT = removeEnclosingParenthesis(geomWKT, "MultiPolygon");
		
		ArrayList<Polygon> shapes = new ArrayList<Polygon>();
		
		for (StringToken token : breakTokens(geomWKT, ','))
			shapes.addAll(parsePolygon(token.token));
		
		return shapes;
	}

    private Collection<Geometry> parseGeometryCollection(String geomWKT) throws ParseException
    {
        if (geomWKT.trim().equals("EMPTY"))
            return new LinkedList<Geometry>();
        
        geomWKT = removeEnclosingParenthesis(geomWKT, "GeometryCollection");
        
        ArrayList<Geometry> shapes = new ArrayList<Geometry>();
        
        for (StringToken token : breakTokens(geomWKT, ','))
            shapes.addAll(parseGeometry(token));
        
        return shapes;
    }
    
	public Collection<Geometry> parseGeometry(StringToken geomToken) throws ParseException
	{
		ArrayList<Geometry> shapes = new ArrayList<Geometry>();
		
		String geomWKT = geomToken.token.trim();
		
		if (!geomWKT.isEmpty())
		{
			String upperGeomWKT = geomWKT.toUpperCase();
			
			if (upperGeomWKT.length() > 80)
				upperGeomWKT = upperGeomWKT.substring(0, 80);
			
			Collection<? extends Geometry> newGeometries;
			
			if (upperGeomWKT.startsWith("POINT"))
			    newGeometries = parsePoint(geomWKT.substring("POINT".length()));

			else if (upperGeomWKT.startsWith("LINESTRING"))
			    newGeometries = parseLineString(geomWKT.substring("LINESTRING".length()));

			else if (upperGeomWKT.startsWith("POLYGON"))
			    newGeometries = parsePolygon(geomWKT.substring("POLYGON".length()));

			else if (upperGeomWKT.startsWith("MULTILINESTRING"))
			    newGeometries = parseMultiLineString(geomWKT.substring("MULTILINESTRING".length()));

			else if (upperGeomWKT.startsWith("MULTIPOLYGON"))
			    newGeometries = parseMultiPolygon(geomWKT.substring("MULTIPOLYGON".length()));
			
            else if (upperGeomWKT.startsWith("GEOMETRYCOLLECTION"))
                newGeometries = parseGeometryCollection(geomWKT.substring("GEOMETRYCOLLECTION".length()));
            
			else
				throw new ParseException("Could not understand WKT keyword: " + upperGeomWKT, 0);
			
			for (Geometry geometry : newGeometries)
			    geometry.setTextLocation(geomToken.offset, geomToken.length);
			
			shapes.addAll(newGeometries);
		}
		
		return shapes;
	}

	@Override
	public Geometry[] readString(String wkt) throws ParseException
	{
		ArrayList<Geometry> shapes = new ArrayList<Geometry>();
		
		int n = 0;
		
		for (StringToken geomWKT : breakTokens(wkt, ';'))
		{
		    System.out.println(geomWKT.offset + ":" + geomWKT.length);
		    
			n ++;
			
			try
			{
				shapes.addAll(parseGeometry(geomWKT));
			}
			catch(ParseException e)
			{
				throw new ParseException("Error reading geometry " + n + ": " + e.getMessage(), 0);
			}
		}
		
		return shapes.toArray(new Geometry[0]);
	}

}
