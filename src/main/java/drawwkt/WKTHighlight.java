package drawwkt;

import java.awt.Color;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.SwingWorker;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

public class WKTHighlight
{
    public enum Type
    {
        Keyword                   (Color.blue, false), 
        Number                    (new Color(255, 169, 0), false),
        CurrentMatchingParenthesis(Color.green.darker(), false),
        MatchingParenthesis       (Color.green.darker(), false),
        WrongParenthesis          (Color.red, false);
        
        final private Color color;
        final private boolean underline;
        
        Type(Color color, boolean underline)
        {
            this.color = color;
            this.underline = underline;
        }
        
        public SimpleAttributeSet getSimpleAttributesSet()
        {
            SimpleAttributeSet simpleAttributeSet = new SimpleAttributeSet();
            
            StyleConstants.setForeground(simpleAttributeSet, color);
            StyleConstants.setUnderline(simpleAttributeSet, underline);
            StyleConstants.setBold(simpleAttributeSet, true);
            
            return simpleAttributeSet;
        }
    }

    final public int offset, length;
    
    final public Type type;
    
    public WKTHighlight(int offset, int length, Type type)
    {
        this.type = type;
        this.offset = offset;
        this.length = length;
    }

    static private Pattern numberPattern = Pattern.compile("[-+]?[0-9]*\\.?[0-9]+([eE][-+]?[0-9]+)?");

    static public class Worker extends SwingWorker<Void,WKTHighlight>
    {
    	final String text;
    	final int caretPosition;
    	
    	public Worker(final String text, final int caretPosition)
    	{
    		this.text = text;
    		this.caretPosition = caretPosition;
    	}
    	
		@Override
		protected Void doInBackground() throws Exception
		{
	        for (String keyword : new String[] { "POINT", "LINESTRING", "POLYGON", 
	                "MULTILINESTRING", "MULTIPOLYGON", "GEOMETRYCOLLECTION", ";", "," })
	        {
	            int lastIndex = -1;
	            
	            int nextIndex;
	            
	            while ((nextIndex = text.indexOf(keyword, lastIndex + 1)) != -1)
	            {
	                if ((nextIndex == 0 || 
	                        !Character.isLetter(text.charAt(nextIndex - 1))) &&
	                    (nextIndex + keyword.length() == text.length() || 
	                        !Character.isLetter(text.charAt(nextIndex + keyword.length()))))
	                {
	                    publish(new WKTHighlight(nextIndex, keyword.length(), WKTHighlight.Type.Keyword));
	                }
	                
	                lastIndex += nextIndex + keyword.length();
	            }
	        }

	        Matcher m = numberPattern.matcher(text);
	        
	        int lastIndex = 0;
	        
	        while (m.find(lastIndex))
	        {
	            int start = m.start();
	            int length = m.end() - start;
	            
	            publish(new WKTHighlight(start, length, WKTHighlight.Type.Number));
	            
	            lastIndex = start + length;
	        }
	        
	        LinkedList<Integer> openingParenthesis = new LinkedList<>();
	        
	        for (int n = 0; n < text.length(); n ++)
	        {
	            char c = text.charAt(n);
	            
	            if (c == '(')
	            {
	                openingParenthesis.add(n);
	            }
	            
	            else if (c == ')')
	            {
	                if (openingParenthesis.isEmpty())
	                {
	                	publish(new WKTHighlight(n, 1, WKTHighlight.Type.WrongParenthesis));
	                }
	                else
	                {
	                    int matchingParenthesis = openingParenthesis.removeLast().intValue();
	                    
	                    if (n == caretPosition || matchingParenthesis == caretPosition)
	                    {
	                    	publish(new WKTHighlight(matchingParenthesis, 1, WKTHighlight.Type.CurrentMatchingParenthesis));
	                        
	                    	publish(new WKTHighlight(n, 1, WKTHighlight.Type.CurrentMatchingParenthesis));
	                    }
	                    else
	                    {
	                    	publish(new WKTHighlight(matchingParenthesis, 1, WKTHighlight.Type.MatchingParenthesis));
	                        
	                    	publish(new WKTHighlight(n, 1, WKTHighlight.Type.MatchingParenthesis));
	                    }
	                }
	            }
	            
	            else if (c == ';')
	            {
	                while (!openingParenthesis.isEmpty())
	                {
	                    int matchingParenthesis = openingParenthesis.removeLast().intValue();
	                    
	                    publish(new WKTHighlight(matchingParenthesis, 1, WKTHighlight.Type.WrongParenthesis));
	                }
	            }
	        }

	        while (!openingParenthesis.isEmpty())
	        {
	            int matchingParenthesis = openingParenthesis.removeLast().intValue();
	            
	            publish(new WKTHighlight(matchingParenthesis, 1, WKTHighlight.Type.WrongParenthesis));
	        }
	        
	        return null;
		}
    }

}
