package drawwkt.ui;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import drawwkt.reader.GeometryReader;
import wkt.Geometry;
import wkt.LineString;
import wkt.Point;
import wkt.Polygon;

public class GeoJSONReader extends GeometryReader
{
	final public int xCoord, yCoord;
    
    public GeoJSONReader(int xCoord, int yCoord)
    {
        this.xCoord = xCoord;
        this.yCoord = yCoord;
    }
    
    @Override
	public String toString()
	{
		return "GeoJSON " + ("XYZ".charAt(this.xCoord)) + ("XYZ".charAt(this.yCoord));
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
			
			if (geomWKT.token.trim().isEmpty())
				continue;
			
			try
			{
				shapes.addAll(parseGeometry(new JSONObject(new JSONTokener(geomWKT.token.toString()))));
			}
			catch(ParseException e)
			{
				throw new ParseException("Error reading geometry " + n + ": " + e.getMessage(), 0);
			}
		}
		
		return shapes.toArray(new Geometry[0]);
	}

	private Collection<? extends Geometry> parseGeometry(JSONObject object) throws ParseException
	{
		ArrayList<Geometry> geometries = new ArrayList<>();
		
		String type = object.getString("type");
		
		if ("FeatureCollection".equals(type))
		{
			for (Object feature : object.getJSONArray("features"))
				geometries.addAll(parseGeometry((JSONObject) feature));
		}
		else if ("Feature".equals(type))
		{
			geometries.addAll(parseGeometry(object.getJSONObject("geometry")));
		}
		else if ("Point".equals(type))
		{
			JSONArray coordinates = object.getJSONArray("coordinates");
			
			geometries.add(getPointFromCoordinates(coordinates));
		}
		else if ("LineString".equals(type))
		{
			JSONArray coordinatesArray = object.getJSONArray("coordinates");
			
			ArrayList<Point> points = new ArrayList<>();
			
			for (Object coordinates : coordinatesArray)
				points.add(getPointFromCoordinates((JSONArray) coordinates));
			
			geometries.add(new LineString(points.toArray(new Point[0])));
		}
		else if ("Polygon".equals(type))
		{
			JSONArray linearRingArray = object.getJSONArray("coordinates");
			
			ArrayList<LineString> linearRings = new ArrayList<>();
			
			for (Object linearRing : linearRingArray)
			{
				ArrayList<Point> points = new ArrayList<>();
				
				for (Object coordinates : (JSONArray) linearRing)
					points.add(getPointFromCoordinates((JSONArray) coordinates));
				
				linearRings.add(new LineString(points.toArray(new Point[0])));
			}
			
			LineString shell = linearRings.remove(0);
			
			geometries.add(new Polygon(shell, linearRings.toArray(new LineString[0])));
		}
		else
		{
			System.out.println("Unknown GeoJSON type: " + type);
		}
		
		return geometries;
	}

	private Point getPointFromCoordinates(JSONArray coordinates)
	{
		return new Point(coordinates.getDouble(this.xCoord), coordinates.getDouble(this.yCoord));
	}
}
