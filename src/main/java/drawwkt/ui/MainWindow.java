package drawwkt.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextPane;
import javax.swing.JToggleButton;
import javax.swing.SwingWorker;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import wkt.Geometry;
import wkt.Point;
import wkt.WKTReader;
import drawwkt.Main;
import drawwkt.WKTHighlight;
import drawwkt.actions.DiagnoseMenu;
import drawwkt.actions.FileMenu;
import drawwkt.actions.HelpMenu;
import drawwkt.actions.NoTabContextMenu;
import drawwkt.actions.TabContextMenu;
import drawwkt.reader.GeometryReader;
import drawwkt.tools.DeleteTool;
import drawwkt.tools.NewPointTool;
import drawwkt.tools.NewPolygonTool;
import drawwkt.tools.NewPolylineTool;
import drawwkt.tools.GeometraTool;
import drawwkt.tools.SelectionTool;
import net.iharder.dnd.FileDrop;

@SuppressWarnings("serial")
public class MainWindow extends JFrame
{
    public static final String DEFAULT_TAB_NAME = "Unnamed";

    static final Icon HIDDEN_GEOMETRY_ICON;

    static
    {
        try (InputStream is = Main.class.getResourceAsStream("/drawwkt/icons/HiddenTab.png"))
        {
            HIDDEN_GEOMETRY_ICON = new ImageIcon(ImageIO.read(is));
        }
        catch(IOException e)
        {
            throw new RuntimeException(e);
        }
    }
    
	public static final GeometryReader[] geometryReaders = new GeometryReader[]
    {
        new WKTReader(0, 1),
        new WKTReader(1, 2),
        new WKTReader(2, 0),
        new GeoJSONReader(0, 1),
        new GeoJSONReader(1, 2),
        new GeoJSONReader(2, 0)
    };

    public static final Font DefaultFont = new Font("Courier New", Font.PLAIN, 12);
    
    static final JFileChooser fileChooser = new JFileChooser();

    final JTabbedPane tabbedPanel = new JTabbedPane();
    
	final private JCheckBox    showVertexesCheckBox = new JCheckBox("Show vertexes");
	final private JLabel       locationLabel        = new JLabel("Unknown location");
	final private DrawingPanel drawingPanel         = new DrawingPanel(this, this.locationLabel, this.showVertexesCheckBox);

    public MainWindow() throws IOException
	{
		super("DrawWKT " + Main.version);
		
		JButton drawButton = new JButton("Draw");
		
		drawButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
	            MainWindow.this.reloadGeometriesFromText();
			}
		});
		
		drawButton.setFocusable(false);
		
		JPanel toolsPanel = new JPanel(new FlowLayout(FlowLayout.LEADING, 2, 0));
		
		final ButtonGroup toolsGroup = new ButtonGroup();
		
		boolean first = true;
		
		final GeometraTool[] geometraTools = new GeometraTool[] {
                                                                new SelectionTool(this),
                                                                new NewPointTool(this),
                                                                new NewPolylineTool(this),
                                                                new NewPolygonTool(this),
                                                                new DeleteTool(this)
                                                            };
        
		for (final GeometraTool action : geometraTools)
		{
		    JToggleButton button = new JToggleButton(action.getIcon());
		    
            button.setFocusable(false);
		    button.setPreferredSize(new Dimension(38, 38));
		    button.addActionListener(new ActionListener()
            {
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    MainWindow.this.drawingPanel.setCurrentAction(action);
                    
                    for (GeometryTextArea textPanel : MainWindow.this.getAllTextPanels())
                        textPanel.setCurrentAction(action);
                }
            });
		    
		    toolsPanel.add(button);
		    toolsGroup.add(button);

		    button.setSelected(first);
		    
		    first = false;
		}
		
		this.tabbedPanel.setFocusable(false);
		
		JPanel upperPanel = new JPanel(new BorderLayout());
		upperPanel.add(this.tabbedPanel);
		upperPanel.add(drawButton, BorderLayout.EAST);
		upperPanel.setMinimumSize(new Dimension(50, 50));
		
		JPanel basePanel = new JPanel(new BorderLayout());
		basePanel.add(this.locationLabel, BorderLayout.EAST);
		basePanel.add(this.showVertexesCheckBox, BorderLayout.WEST);
		
		final JComboBox<GeometryReader> parseComboBox = new JComboBox<GeometryReader>();
		
		for (GeometryReader reader : geometryReaders)
		    parseComboBox.addItem(reader);
		
		parseComboBox.addItemListener(new ItemListener()
        {
            @Override
            public void itemStateChanged(ItemEvent e)
            {
                GeometryTextArea currentTextArea = MainWindow.this.getCurrentTextArea();
                
                currentTextArea.setReader((GeometryReader) parseComboBox.getSelectedItem());
                
                MainWindow.this.drawingPanel.clearGeometries();
                
                try
                {
                    MainWindow.this.drawingPanel.addGeometries(currentTextArea.getGeometries());
                }
                catch (ParseException ex)
                {
                    JOptionPane.showMessageDialog(MainWindow.this, ex.getMessage());
                }
                
                MainWindow.this.drawingPanel.initializeBoundingBox();
                
                Enumeration<AbstractButton> en = toolsGroup.getElements();
                
                MainWindow.this.drawingPanel.setCurrentAction(geometraTools[0]);
                
                for (GeometryTextArea textPanel : MainWindow.this.getAllTextPanels())
                    textPanel.setCurrentAction(geometraTools[0]);
                
                for (int i = 0; en.hasMoreElements(); i ++)
                {
                    AbstractButton tool = en.nextElement();
                    
                    if (i == 0)
                        tool.setSelected(true);
                    
                    tool.setEnabled(parseComboBox.getSelectedIndex() == 0);
                }
            }
        });
		
		this.showVertexesCheckBox.setFocusable(false);
		
		JPanel graphicPanel = new JPanel(new BorderLayout());
		JPanel topPanel = new JPanel(new BorderLayout());
		topPanel.add(toolsPanel);
		topPanel.add(parseComboBox, BorderLayout.EAST);
		
		
		graphicPanel.add(topPanel, BorderLayout.NORTH);
		graphicPanel.add(this.drawingPanel);
		
		this.add(new JSplitPane(JSplitPane.VERTICAL_SPLIT, upperPanel, graphicPanel));
		this.add(basePanel, BorderLayout.SOUTH);

		JMenuBar menuBar = new JMenuBar();
		menuBar.add(new FileMenu(this));
		menuBar.add(new DiagnoseMenu(this));
		menuBar.add(new HelpMenu(this));
		this.setJMenuBar(menuBar);

		new FileDrop(this, new FileDrop.Listener() 
        {
            @Override
            public void filesDropped(final File[] files)
            {
                for (File file : files)
                {
                    try (FileReader fr = new FileReader(file))
                    {
                        MainWindow.this.addGeometries(fr, file.getName());
                    }
                    catch (IOException e)
                    {
                        JOptionPane.showMessageDialog(MainWindow.this, e.getMessage());
                    }
                }
            }
        });
		
		this.tabbedPanel.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseReleased(MouseEvent e)
            {
                if (e.getButton() == MouseEvent.BUTTON3)
                {
                    BasicTabbedPaneUI tabbedPaneUI = (BasicTabbedPaneUI) MainWindow.this.tabbedPanel.getUI();
                    
                    int tab = tabbedPaneUI.tabForCoordinate(MainWindow.this.tabbedPanel, e.getX(), e.getY());
                    
                    if (tab == -1)
                    {
                        new NoTabContextMenu(MainWindow.this).show(e.getComponent(), e.getX(), e.getY());
                    }
                    else
                    {
                        GeometryTextArea selectedTextArea = (GeometryTextArea) MainWindow.this.tabbedPanel.getComponentAt(tab);
                    
                        new TabContextMenu(MainWindow.this, selectedTextArea).show(e.getComponent(), e.getX(), e.getY());
                    }
                }
            }
        });

        this.createNewGeometryPanel(DEFAULT_TAB_NAME);
	}

    public GeometryTextArea createNewGeometryPanel(String name)
    {
        GeometryTextArea currentTextArea = this.getCurrentTextArea();
        
        if (currentTextArea == null || 
            !currentTextArea.isEmpty() || 
            !this.getGeometryTextAreaName(currentTextArea).equals(DEFAULT_TAB_NAME))
        {
            currentTextArea = new GeometryTextArea(geometryReaders[0]);
            
            new FileDrop(currentTextArea, new FileDrop.Listener() 
            {
                @Override
                public void filesDropped(final File[] files)
                {
                    for (File file : files)
                    {
                        try (FileReader fr = new FileReader(file))
                        {
                            MainWindow.this.addGeometries(fr, file.getName());
                        }
                        catch (IOException e)
                        {
                            JOptionPane.showMessageDialog(MainWindow.this, e.getMessage());
                        }
                    }
                }
            });

            this.tabbedPanel.add(currentTextArea, name);
            
            int index = this.tabbedPanel.getTabCount() - 1;
            
            this.tabbedPanel.setSelectedIndex(index);
            
            this.tabbedPanel.setIconAt(index, currentTextArea.getIcon());
        }
        else
        {
            this.tabbedPanel.setTitleAt(this.tabbedPanel.getSelectedIndex(), name);
        }
        
        return currentTextArea;
    }

    public void close(GeometryTextArea geometryTextArea)
    {
        for (int i = 0; i < this.tabbedPanel.getTabCount(); i ++)
        {
            if (this.tabbedPanel.getComponentAt(i) == geometryTextArea)
            {
                this.tabbedPanel.remove(i);
                break;
            }
        }
        
        if (this.tabbedPanel.getTabCount() == 0)
            this.createNewGeometryPanel(DEFAULT_TAB_NAME);
        
        this.reloadGeometriesFromText();
    }

    public GeometryTextArea[] getAllTextPanels()
    {
        GeometryTextArea[] allTextAreas = new GeometryTextArea[this.tabbedPanel.getTabCount()];
        
        for (int i = 0; i < this.tabbedPanel.getTabCount(); i ++)
            allTextAreas[i] = (GeometryTextArea) this.tabbedPanel.getComponentAt(i);
        
        return allTextAreas;
    }

    public GeometryTextArea getCurrentTextArea()
    {
        return (GeometryTextArea) this.tabbedPanel.getSelectedComponent();
    }

    public String getGeometryTextAreaName(GeometryTextArea geometryTextArea)
    {
        for (int i = 0; i < this.tabbedPanel.getTabCount(); i ++)
        {
            if (this.tabbedPanel.getComponentAt(i) == geometryTextArea)
                return this.tabbedPanel.getTitleAt(i);
        }
        
        return null;
    }

    public void setGeometryTextAreaName(GeometryTextArea geometryTextArea, String newName)
    {
        for (int i = 0; i < this.tabbedPanel.getTabCount(); i ++)
        {
            if (this.tabbedPanel.getComponentAt(i) == geometryTextArea)
                this.tabbedPanel.setTitleAt(i, newName);
        }
    }

    public void clearGeometries(GeometryTextArea geometryTextArea)
    {
        geometryTextArea.clearGeometries();
        
        this.reloadGeometriesFromText();
    }

    public void addGeometries(Geometry ... geometries)
    {
        this.getCurrentTextArea().addGeometries(geometries);

        this.reloadGeometriesFromText();
    }

    public void addGeometries(java.io.Reader reader, String name)
    {
        try
        {
            GeometryTextArea currentTextArea = createNewGeometryPanel(name);
            
            this.addGeometries(reader, currentTextArea);
        }
        catch(IOException ex)
        {
            JOptionPane.showMessageDialog(MainWindow.this, ex.getMessage());
        }
    }

    public void addGeometries(java.io.Reader reader, GeometryTextArea currentTextArea) throws IOException
    {
        currentTextArea.setGeometries(reader, true);
      
        this.reloadGeometriesFromText();
    }

    public void deleteGeometries(Point p)
    {
        double distance = this.drawingPanel.getScreenFactor();
        
        double distanceSq = distance * distance;
        
        GeometryTextArea currentTextArea = this.getCurrentTextArea();
        
        currentTextArea.deleteGeometries(p, distanceSq);
        
        this.reloadGeometriesFromText();
    }
    
    public Geometry[] getCurrentTextPanelGeometries(Point p)
    {
        double distance = this.drawingPanel.getScreenFactor();
        
        double distanceSq = distance * distance;
        
        return this.getCurrentTextArea().getGeometries(p, distanceSq);
    }
    
    public void reloadGeometriesFromText()
    {
        try
        {
            this.drawingPanel.clearGeometries();
            
            for (GeometryTextArea textArea : this.getAllTextPanels())
            {
                if (textArea.isGeometryVisible())
                {
                    for (final Geometry geometry : textArea.getGeometries())
                    {
                        this.drawingPanel.addGeometries(geometry);

                        final JTextPane textPanel = textArea.textPanel;
                        
                        final String text = textPanel.getText();
                        
                        SwingWorker<Void, WKTHighlight> highlights = new WKTHighlight.Worker(text.substring(
                                geometry.getTextOffset(), geometry.getTextOffset() + geometry.getTextLength()), 
                                textPanel.getCaretPosition() - geometry.getTextLength())
                		{
                        	@Override
                        	public void process(List<WKTHighlight> highlights)
                        	{
                        		for (WKTHighlight highlight : highlights)
                                {
                                    SimpleAttributeSet simpleAttributeSet = highlight.type.getSimpleAttributesSet();
                                    
                                    StyleConstants.setBackground(simpleAttributeSet, geometry.getColor());

                                    MainWindow.this.getCurrentTextArea().textPanel.getStyledDocument().setCharacterAttributes(
                                        geometry.getTextOffset() + highlight.offset, highlight.length, simpleAttributeSet, true);
                                }
                        	}
                		};
                        
                		//highlights.run();
                    }
                }
            }
            
            this.drawingPanel.initializeBoundingBox();
        }
        catch(ParseException ex)
        {
            JOptionPane.showMessageDialog(MainWindow.this, ex.getMessage());
        }
    }
    
    public Geometry[] getGeometries()
    {
        return this.drawingPanel.getGeometries();
    }

    public void openFile()
    {
        fileChooser.setMultiSelectionEnabled(true);
        
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
        {
            for (File file : fileChooser.getSelectedFiles())
            {
                try (FileReader fr = new FileReader(file))
                {
                    this.addGeometries(fr, file.getName());
                }
                catch (IOException e)
                {
                    JOptionPane.showMessageDialog(this, e.getMessage());
                }
            }
        }
    }

    public void appendFile(GeometryTextArea selectedTextArea)
    {
        fileChooser.setMultiSelectionEnabled(true);
        
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
        {
            for (File file : fileChooser.getSelectedFiles())
            {
                try (FileReader fr = new FileReader(file))
                {
                    this.addGeometries(fr, selectedTextArea);
                }
                catch (IOException e1)
                {
                    JOptionPane.showMessageDialog(this, e1.getMessage());
                }
            }
        }
    }
    
    public void save(GeometryTextArea selectedTextArea)
    {
        fileChooser.setMultiSelectionEnabled(false);
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION)
        {
            try (FileWriter fw = new FileWriter(fileChooser.getSelectedFile()))
            {
                fw.write(selectedTextArea.getCurrentText());
                
                fw.close();
            }
            catch (IOException ex)
            {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
        }
    }
    
    public void initializeBoundingBox()
    {
        this.drawingPanel.initializeBoundingBox();
    }


    public void initializeBoundingBox(GeometryTextArea selectedTextArea)
    {
        try
        {
            this.drawingPanel.initializeBoundingBox(selectedTextArea.getGeometries());
        }
        catch (ParseException e)
        {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    public void closeAll()
    {
        this.tabbedPanel.removeAll();
        
        this.reloadGeometriesFromText();
    }

    public void updateHiddenIcon(GeometryTextArea selectedTextArea)
    {
        for (int i = 0; i < this.tabbedPanel.getTabCount(); i ++)
        {
            if (this.tabbedPanel.getComponentAt(i) == selectedTextArea)
                this.tabbedPanel.setIconAt(i, selectedTextArea.isGeometryVisible() ? 
                        selectedTextArea.getIcon() : HIDDEN_GEOMETRY_ICON);
        }
    }
}
