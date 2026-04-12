package drawwkt.reader;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.LinkedList;

import wkt.Geometry;

abstract public class GeometryReader
{
    static public class StringToken
    {
        final public String token;
        
        final public int offset, length;
        
        public StringToken(String token, int offset)
        {
            this.token = token;
            
            this.offset = offset;
            
            this.length = token.length();
        }
    }
    
    protected static String removeEnclosingParenthesis(String geomWKT, String objectName) throws ParseException
    {
        geomWKT = geomWKT.trim();
        
        if (!geomWKT.startsWith("(") || !geomWKT.endsWith(")"))
            throw new ParseException(objectName + " does not have open/close parenthesis", 0);

        return geomWKT.substring(1, geomWKT.length() - 1);
    }
    
    public static StringToken[] breakTokens(String s, char separator)
    {
        ArrayList<StringToken> tokens = new ArrayList<>();
        
        StringBuffer currentToken = new StringBuffer();
        
        LinkedList<Character> nesting = new LinkedList<Character>();
        
        int offset = 0;
        
        for (char c : s.replaceAll("\r", "").toCharArray())
        {
            if (c == '(')
            {
                nesting.add(')');

                currentToken.append(c);
            }
            else if (c == '[')
            {
                nesting.add(']');

                currentToken.append(c);
            }
            else if (c == '{')
            {
                nesting.add('}');

                currentToken.append(c);
            }
            else if (!nesting.isEmpty() && c == nesting.getLast())
            {
                nesting.removeLast();

                currentToken.append(c);
            }
            else if (nesting.isEmpty() && c == separator)
            {
                tokens.add(new StringToken(currentToken.toString(), offset));
                
                offset += currentToken.length() + 1;
                
                currentToken.setLength(0);
            }
            else
            {
                currentToken.append(c);
            }
        }
        
        if (currentToken.length() > 0)
        {
            tokens.add(new StringToken(currentToken.toString(), offset));
        }
        
        return tokens.toArray(new StringToken[0]);
    }

	abstract public Geometry[] readString(String s) throws ParseException;
}
