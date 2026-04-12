package drawwkt.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Document;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

import drawwkt.reader.GeometryReader;
import drawwkt.reader.GeometryReader.StringToken;
import drawwkt.tools.GeometraTool;
import wkt.Geometry;
import wkt.Point;

@SuppressWarnings("serial")
public class GeometryTextArea extends JPanel
{
    @SuppressWarnings("deprecation")
	private static final int MASK = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();

    private Palette palette = new Palette();

    final public JTextPane textPanel = new JTextPane(new DefaultStyledDocument());

	private UndoManager undoManager = new UndoManager();

    private boolean isGeometryVisible = true;
    
    private GeometraTool currentAction = null;
    
    public GeometryReader reader;

    public GeometryTextArea(GeometryReader wktReader)
    {       
        this.reader = wktReader;

        this.setPreferredSize(new Dimension(100, 100));
        
        this.textPanel.setFont(MainWindow.DefaultFont);
        
        this.textPanel.addKeyListener(new KeyAdapter()
        {
            @Override
            public void keyReleased(KeyEvent e)
            {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
                {
                    if (GeometryTextArea.this.currentAction != null)
                        GeometryTextArea.this.currentAction.clear();
                }
            }
        });
        
        Document doc = this.textPanel.getDocument();
        doc.addUndoableEditListener(new UndoableEditListener()
        {
            @Override
            public void undoableEditHappened(UndoableEditEvent e)
            {
                undoManager.addEdit(e.getEdit());
            }
        });
        
        this.textPanel.getActionMap().put("Undo", new AbstractAction("Undo")
        {
            @Override
            public void actionPerformed(ActionEvent evt)
            {
                try 
                {
                    if (undoManager.canUndo())
                        undoManager.undo();

                }
                catch (CannotUndoException e)
                {
                    System.out.println(e);
                }
            }
        });
        
        this.textPanel.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, MASK), "Undo");
        
        this.textPanel.getActionMap().put("Redo", new AbstractAction("Redo")
        {
            @Override
            public void actionPerformed(ActionEvent evt)
            {
                try 
                {
                    if (undoManager.canRedo())
                        undoManager.redo();

                } 
                catch (CannotRedoException e)
                {
                    System.out.println(e);
                }
            }
        });
        
        this.textPanel.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_Y, MASK), "Redo");
        
        this.setLayout(new BorderLayout());
        this.add(new JScrollPane(this.textPanel));
    }
    
    public void clearGeometries()
    {
        this.textPanel.setText("");
    }

    public void setGeometries(Reader reader, boolean append) throws IOException
    {
        BufferedReader br = new BufferedReader(reader);

        String line = null;
        
        StringBuffer sb = new StringBuffer();
        
        while ((line = br.readLine()) != null)
            sb.append(line + "\n");
        
        if (append && !this.textPanel.getText().isEmpty())
            this.textPanel.setText(this.textPanel.getText() + ";\n\n" + sb.toString());
        else
            this.textPanel.setText(sb.toString());
    }

    public void deleteGeometries(Point p, double distanceSq)
    {
        StringBuffer sb = new StringBuffer();
        
        boolean first = true;
        
        for (StringToken geomWKT : GeometryReader.breakTokens(this.textPanel.getText(), ';'))
        {
            boolean remove = false;
            
            try
            {
                for (Geometry g : this.reader.readString(geomWKT.token))
                {
                    if (g.snapPoint(p, distanceSq) != null || 
                        g.snapLine(p, distanceSq) != null ||
                        g.containsPoint(p))
                    {
                        remove = true;
                        break;
                    }
                }
            }
            catch(ParseException e)
            {
                // Do nothing
            }
            
            if (!remove)
            {
                if (first)
                    first = false;
                else
                    sb.append(";");
                
                sb.append(geomWKT.token);
            }
        }

        this.textPanel.setText(sb.toString());
    }

    public Geometry[] getGeometries(Point p, double distanceSq)
    {
        LinkedList<Geometry> geometries = new LinkedList<Geometry>();
        
        for (StringToken geomWKT : GeometryReader.breakTokens(this.textPanel.getText(), ';'))
        {
            try
            {
                boolean found = false;
                
                Collection<Geometry> geom = Arrays.asList(this.reader.readString(geomWKT.token));
                
                for (Geometry g : geom)
                {
                    if (g.snapPoint(p, distanceSq) != null || 
                        g.snapLine(p, distanceSq) != null ||
                        g.containsPoint(p))
                    {
                        found = true;
                        break;
                    }
                }
                
                if (found)
                {
                    geometries.addAll(geom);
                }
            }
            catch(ParseException e)
            {
                // Do nothing
            }
        }

        return geometries.toArray(new Geometry[0]);
    }

    public void addGeometries(Geometry ... geometries)
    {
        String text = this.textPanel.getText(); 
        
        if (text.trim().isEmpty())
            this.textPanel.setText(this.textPanel.getText() + "");

        else if (text.endsWith(";\n\n"))
            this.textPanel.setText(this.textPanel.getText() + "");
        
        else if (text.endsWith(";\n"))
            this.textPanel.setText(this.textPanel.getText() + "\n"); 
        
        else if (text.endsWith(";"))
            this.textPanel.setText(this.textPanel.getText() + "\n\n");
        
        else
            this.textPanel.setText(this.textPanel.getText() + ";\n\n");
        
        StringBuffer wktToReturn = new StringBuffer();
        
        if (geometries != null && geometries.length > 0)
        {
            for (Geometry geometry : geometries)
                wktToReturn.append(geometry + ";\n");
        }
        
        this.textPanel.setText(this.textPanel.getText() + wktToReturn.toString());
    }

    public Geometry[] getGeometries() throws ParseException
    {
        Geometry[] geometries = this.reader.readString(this.textPanel.getText());
        
        this.palette.reset();
        
        for (Geometry geometry : geometries)
            geometry.setColor(this.palette.getNextColor());
        
        return geometries;
    }

    public String getCurrentText()
    {
        return this.textPanel.getText();
    }

    public void setCurrentAction(GeometraTool action)
    {
        this.currentAction = action;
        
        this.repaint();
    }

    public void setReader(GeometryReader reader)
    {
        this.reader = reader;
    }

    public boolean isEmpty()
    {
        return this.textPanel.getText().trim().isEmpty();
    }

    public boolean isGeometryVisible()
    {
        return this.isGeometryVisible;
    }

    public void setGeomertryVisible(boolean b)
    {
        this.isGeometryVisible = b;
    }

    public Icon getIcon()
    {
        return this.palette.getIcon();
    }

    public void randomizeColor()
    {
        this.palette.randomize();
    }

    public void setPalette(Palette palette)
    {
        this.palette = palette;
    }
}
