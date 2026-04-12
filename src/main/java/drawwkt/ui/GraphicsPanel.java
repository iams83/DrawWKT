package drawwkt.ui;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JPanel;

abstract public class GraphicsPanel extends JPanel
{
	private static final long serialVersionUID = 1L;

	private AffineTransform tx = new AffineTransform();

	private Point draggedPoint;

	public GraphicsPanel()
	{
		this.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent e)
			{
				GraphicsPanel.this.draggedPoint = e.getPoint();
			}
		});
		
		this.addMouseMotionListener(new MouseMotionAdapter()
		{
			@Override
			public void mouseDragged(MouseEvent e)
			{
			    if (GraphicsPanel.this.draggedPoint == null)
			        return;
			    
				AffineTransform newTx = new AffineTransform();
				
				newTx.translate(
						e.getX() - GraphicsPanel.this.draggedPoint.x, 
						e.getY() - GraphicsPanel.this.draggedPoint.y);

				GraphicsPanel.this.draggedPoint = e.getPoint();
				
				GraphicsPanel.this.tx.preConcatenate(newTx);
				
				repaint();
			}
		});
		
		this.addMouseWheelListener(new MouseWheelListener()
		{
			@Override
			public void mouseWheelMoved(MouseWheelEvent e)
			{
				AffineTransform newTx = new AffineTransform();
				
				double scale = Math.pow(1.1, -e.getWheelRotation());

				newTx.translate(e.getX(), e.getY());
				newTx.scale(scale, scale);
				newTx.translate(-e.getX(), -e.getY());
				
				GraphicsPanel.this.draggedPoint = e.getPoint();
				
				GraphicsPanel.this.tx.preConcatenate(newTx);
				
				repaint();
			}
		});
	}
	
	public Point2D sceneToMap(Point2D point)
	{
		return this.tx.transform(point, null);
	}

	public Point2D mapToScene(Point point)
	{
		try
		{
			return this.tx.inverseTransform(point, null);
		}
		catch(NoninvertibleTransformException ex)
		{
			return null;
		}
	}
	
	public void initializeBoundingBox(Rectangle2D boundingBox)
	{
		double scale = Math.min(
				getWidth()  / boundingBox.getWidth(), 
				getHeight() / boundingBox.getHeight());

		this.tx.setToIdentity();

		this.tx.translate(getWidth() / 2, getHeight() / 2);
		this.tx.scale(scale, -scale);
		this.tx.translate(- boundingBox.getMinX() - boundingBox.getWidth() / 2, 
				 	 - boundingBox.getMinY() - boundingBox.getHeight() / 2);
        
        this.repaint();
	}
	
	@Override
	public void paint(Graphics g)
	{
		this.paint((Graphics2D) g, this.tx);
	}

    public double getScreenFactor()
    {
        return 1.0 / this.tx.deltaTransform(new Point2D.Double(1, 0), null).getX();
    }

	abstract protected void paint(Graphics2D g2, AffineTransform tx2);
}
