package drawwkt.tools;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import drawwkt.ui.MainWindow;

public class SelectionTool extends GeometraTool
{
    final MainWindow frame;
    
    public SelectionTool(MainWindow frame)
    {
        super("Tool_Move");

        this.frame = frame;
    }
    
    @Override
    public void paint(Graphics2D g2, AffineTransform tx)
    {
        // Do nothing
    }
}
