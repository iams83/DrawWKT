package drawwkt.ui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;

import drawwkt.tools.GeometraTool;
import wkt.Geometry;
import wkt.Point;

public class DrawingPanel extends GraphicsPanel
{
	private static final long serialVersionUID = 1L;

	private ArrayList<Geometry> geometries = new ArrayList<Geometry>();
	
	private boolean showVertexes;
	
	private GeometraTool currentAction = null;

    private Point snappingPoint;

	public DrawingPanel(JFrame frame, final JLabel label, final JCheckBox showVertexesCheckBox)
	{
        this.setFocusable(false);
        
	    this.addMouseWheelListener(new MouseWheelListener()
        {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e)
            {
                if (DrawingPanel.this.snappingPoint != null)
                {
                    DrawingPanel.this.snappingPoint = null;
                    
                    DrawingPanel.this.repaint();
                }
                
                Point2D p2D = DrawingPanel.this.mapToScene(e.getPoint());

                if (p2D != null)
                {
                    Point p = new Point(p2D.getX(), p2D.getY());

                    if (!e.isControlDown())
                        p = snapMousePoint(p);

                    label.setText("Location: " + p.xs + ", " + p.ys);
                }
            }
        });
	    
		this.addMouseMotionListener(new MouseMotionAdapter()
		{
			@Override
			public void mouseMoved(MouseEvent e)
			{
			    if (DrawingPanel.this.snappingPoint != null)
			    {
			        DrawingPanel.this.snappingPoint = null;
			        
			        DrawingPanel.this.repaint();
			    }
			    
			    Point2D p2D = DrawingPanel.this.mapToScene(e.getPoint());

				if (p2D != null)
				{
	                Point p = new Point(p2D.getX(), p2D.getY());

                    if (!e.isControlDown())
                        p = snapMousePoint(p);

                    label.setText("Location: " + p.xs + ", " + p.ys);
    
    				if (DrawingPanel.this.currentAction != null)
                        DrawingPanel.this.currentAction.mouseMoved(p);
				}
				else
				{
                    label.setText("Unknown location");
				}
			}
            
            @Override
            public void mouseDragged(MouseEvent e)
            {
                if (DrawingPanel.this.snappingPoint != null)
                {
                    DrawingPanel.this.snappingPoint = null;
                    
                    DrawingPanel.this.repaint();
                }
                
                if (e.getButton() != MouseEvent.BUTTON1)
                    return;
                
                Point2D p2D = DrawingPanel.this.mapToScene(e.getPoint());

                if (p2D != null)
                {
                    Point p = new Point(p2D.getX(), p2D.getY());
                    
                    if (!e.isControlDown())
                        p = snapMousePoint(p);

                    label.setText("Location: " + p.xs + ", " + p.ys);

                    if (DrawingPanel.this.currentAction != null)
                        DrawingPanel.this.currentAction.mouseDragged(p);
                }
                else
                {
                    label.setText("Unknown location");
                }
            }
		});
		
		this.addMouseListener(new MouseListener()
        {
		    @Override
            public void mousePressed(MouseEvent e)
            {
                if (DrawingPanel.this.snappingPoint != null)
                {
                    DrawingPanel.this.snappingPoint = null;
                    
                    DrawingPanel.this.repaint();
                }
                
                if (e.getButton() != MouseEvent.BUTTON1)
                    return;
                
                if (DrawingPanel.this.currentAction != null)
                {
                    Point2D p2D = DrawingPanel.this.mapToScene(e.getPoint());
                    
                    if (p2D == null)
                        return;
                    
                    Point p = new Point(p2D.getX(), p2D.getY());

                    if (!e.isControlDown())
                        p = snapMousePoint(p);
                    else
                        DrawingPanel.this.snappingPoint = null;

                    DrawingPanel.this.currentAction.mousePressed(p);
                }
            }
            
		    @Override
            public void mouseReleased(MouseEvent e)
            {
                if (DrawingPanel.this.snappingPoint != null)
                {
                    DrawingPanel.this.snappingPoint = null;
                    
                    DrawingPanel.this.repaint();
                }
                
                if (DrawingPanel.this.currentAction != null)
                {
                    Point2D p2D = DrawingPanel.this.mapToScene(e.getPoint());
                    
                    if (p2D == null)
                        return;
                    
                    Point p = new Point(p2D.getX(), p2D.getY());

                    if (!e.isControlDown())
                        p = snapMousePoint(p);
                    else
                        DrawingPanel.this.snappingPoint = null;

                    if (e.getButton() == MouseEvent.BUTTON1)
                        DrawingPanel.this.currentAction.mouseReleased(p);
                    else
                        DrawingPanel.this.currentAction.clear();
                }
            }
            
            @Override
            public void mouseClicked(MouseEvent e)
            {
                if (DrawingPanel.this.snappingPoint != null)
                {
                    DrawingPanel.this.snappingPoint = null;
                    
                    DrawingPanel.this.repaint();
                }
                
                if (e.getButton() != MouseEvent.BUTTON1)
                    return;
                
                if (DrawingPanel.this.currentAction != null)
                {
                    Point2D p2D = DrawingPanel.this.mapToScene(e.getPoint());
                    
                    if (p2D == null)
                        return;
                    
                    Point p = new Point(p2D.getX(), p2D.getY());
                    
                    if (!e.isControlDown())
                        p = snapMousePoint(p);
                    else
                        DrawingPanel.this.snappingPoint = null;

                    int clickCount = e.getClickCount();
                    
                    if (clickCount == 1)
                        DrawingPanel.this.currentAction.mouseClicked(p);
                    else
                        DrawingPanel.this.currentAction.mouseDoubleClicked(p);
                }
            }
            
            @Override
            public void mouseExited(MouseEvent e)
            {
                if (DrawingPanel.this.snappingPoint != null)
                {
                    DrawingPanel.this.snappingPoint = null;
                    
                    DrawingPanel.this.repaint();
                }
                
                if (DrawingPanel.this.currentAction != null)
                {
                    Point2D p2D = DrawingPanel.this.mapToScene(e.getPoint());
                    
                    if (p2D == null)
                        return;
                    
                    Point p = new Point(p2D.getX(), p2D.getY());

                    if (!e.isControlDown())
                        p = snapMousePoint(p);
                    else
                        DrawingPanel.this.snappingPoint = null;

                    DrawingPanel.this.currentAction.mouseExited(p);
                }
            }
            
            @Override
            public void mouseEntered(MouseEvent e)
            {
                if (DrawingPanel.this.snappingPoint != null)
                {
                    DrawingPanel.this.snappingPoint = null;
                    
                    DrawingPanel.this.repaint();
                }
                
                if (DrawingPanel.this.currentAction != null)
                {
                    Point2D p2D = DrawingPanel.this.mapToScene(e.getPoint());
                    
                    if (p2D == null)
                        return;
                    
                    Point p = new Point(p2D.getX(), p2D.getY());

                    if (!e.isControlDown())
                        p = snapMousePoint(p);
                    else
                        DrawingPanel.this.snappingPoint = null;

                    DrawingPanel.this.currentAction.mouseEntered(p);
                }
            }
        });
		
		this.showVertexes = showVertexesCheckBox.isSelected();
		
		showVertexesCheckBox.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				DrawingPanel.this.showVertexes = showVertexesCheckBox.isSelected();
				
				DrawingPanel.this.repaint();
			}
		});
	}
	
	private Point snapMousePoint(Point p)
	{
	    this.snappingPoint = null;
	    
	    if (this.geometries != null)
	    {
	        double distance = 6 * this.getScreenFactor();
	        
            double distanceSq = distance * distance;
            
	        for (Geometry g : this.geometries)
	        {
                Point p0 = g.snapPoint(p, distanceSq);
                
                if (p0 != null)
                    return this.snappingPoint = p0;
	        }

	        if (this.currentAction != null)
	        {
	            Geometry g = this.currentAction.getCurrentGeometry(false);
	            
	            if (g != null)
	            {
        	        Point p0 = g.snapPoint(p, distanceSq);
                    
                    if (p0 != null)
                        return this.snappingPoint = p0;
	            }
	        }
	        
            for (Geometry g : this.geometries)
            {
                Point p0 = g.snapLine(p, distanceSq);
                
                if (p0 != null)
                    return this.snappingPoint = p0;
            }

            if (this.currentAction != null)
            {
                Geometry g = this.currentAction.getCurrentGeometry(false);
                
                if (g != null)
                {
                    Point p0 = g.snapLine(p, distanceSq);
                    
                    if (p0 != null)
                        return this.snappingPoint = p0;
                }
            }
	    }
	        
	    return p;
	}
	
	public void clearGeometries()
	{
	    this.geometries.clear();
	    
	    this.repaint();
	}
	
    public void addGeometries(Geometry ... geometries)
	{
		this.geometries.addAll(Arrays.asList(geometries));
		
		this.repaint();
	}

    public Geometry[] getGeometries()
    {
        return this.geometries.toArray(new Geometry[0]);
    }

	public void initializeBoundingBox()
	{
	    this.initializeBoundingBox(this.getGeometries());
	}
	
	public void initializeBoundingBox(Geometry[] geometries)
	{
	    if (geometries.length == 0)
	    {
	        this.initializeBoundingBox(
                new Rectangle2D.Double(0, 0, this.getWidth(), this.getHeight()));
	    }
	    else
	    {
    		Rectangle2D boundingBox = null;
    
    		for (Geometry geometry : geometries)
    		{
    			if (boundingBox == null)
    				boundingBox = geometry.getBoundingBox();
    			else
    				boundingBox.add(geometry.getBoundingBox());
    		}
    		
    		double expand = 0.1 * Math.max(boundingBox.getWidth(), boundingBox.getHeight());
    		
    		boundingBox.add(boundingBox.getMinX() - expand, boundingBox.getMinY() - expand);
    		boundingBox.add(boundingBox.getMaxX() + expand, boundingBox.getMaxY() + expand);
    
    		this.initializeBoundingBox(boundingBox);
	    }
	}
	
	@Override
	public void paint(Graphics2D g2, AffineTransform tx)
	{
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		g2.clearRect(0, 0, getWidth(), getHeight());
		
		for (Geometry shape : this.geometries)
		{
			Color color = shape.getColor();
			
			Color lightColor = null;
			
			if (color != null)
			    lightColor = new Color(color.getRed(), color.getGreen(), color.getBlue(), 60);
			
			shape.paint(g2, tx, lightColor, color);
		}

		if (this.showVertexes)
			for (Geometry shape : this.geometries)
				shape.paintVertexes(g2, tx, new Color(0, 0, 0, 0), Color.black);
		
		if (this.snappingPoint != null)
        {
            Point2D point = tx.transform(this.snappingPoint.toPoint2D(), null);
            
            Ellipse2D.Double ellipse = new Ellipse2D.Double(point.getX() - 5, point.getY() - 5, 10, 10);
            
            g2.setColor(Color.black);
            g2.draw(ellipse);
        }

        if (this.currentAction != null)
		    this.currentAction.paint(g2, tx);
	}

    public void setCurrentAction(GeometraTool action)
    {
        this.currentAction = action;
        
        this.repaint();
    }
}
