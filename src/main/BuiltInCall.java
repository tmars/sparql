import java.util.*;
import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class BuiltInCall
{
  
	public static Object exec(String call, List<Object> args)
	{
		Object res = null;
		Method[] methods = BuiltInCall.class.getMethods();
		try 
		{
			for (Method m : methods)
			{
				if (m.getName().equals(call)) 
				{
					res = m.invoke(BuiltInCall.class, args);
					if (Config.getInstance().isDebug())
					{
						String str = "";
						for (Object t : args) str += t.toString() + ", ";
						Config.getInstance().log("BuiltInCall::" + call + "(" + str + ") = " + res);
					}
					break;
				}
			}
		}
		catch (Exception e)
		{
			Config.getInstance().log("Ошибка выполнения встроенной функции " +
				call + ": " + e.getMessage());
			Config.getInstance().printStackTrace(e);
        }
		return res;
	}
	
	//  http://www.w3.org/TR/sparql11-query/#func-str
	public static Object STR(List<Object> args) throws Exception
	{
		if (args.size() != 1) 
        {
            throw new Exception("Wrong number of arguments");
        }
		if (args.get(0) instanceof RDFNode)
		{
			RDFLiteral lit = new RDFLiteral(args.get(0).toString(), "", "");
			return lit.toString();
		}
		else
		{
			return args.get(0).toString();
		}
	}
    
	//  http://www.w3.org/TR/sparql11-query/#func-lang
	public static String _LANG(String text)
	{
		RDFLiteral lit = new RDFLiteral(text);
		return lit.getLanguage();
	}
	
	public static Object LANG(List<Object> args) throws Exception
	{
		if (args.size() != 1)
		{
            throw new Exception("Wrong number of arguments");
        }
		
		String text = args.get(0).toString();
		String result = _LANG(text);
		
		return result;
	}

	
	//  http://www.w3.org/TR/sparql11-query/#func-langMatches
	public static Boolean _LANGMATCHES(String langA, String langB)
	{		
        if (langB.equals("*") && !langA.equals(""))
		{
			return true;
		}
		else if (langA.equals("") || langB.equals(""))
		{
			return true;
		}
        else if (langA.toLowerCase().contains(langB.toLowerCase()))
		{
			return true;
		}
		else if (langB.toLowerCase().contains(langA.toLowerCase()))
		{
			return true;
		}
		return false;
	}
    
	public static Object LANGMATCHES(List<Object> args) throws Exception
	{
		if (args.size() != 2)
		{
            throw new Exception("Wrong number of arguments");
        }
		
		String text = args.get(0).toString();
		String lang = args.get(1).toString();
		
		Boolean result = _LANGMATCHES(text, lang);
		
		return result;
	}

	//  http://www.w3.org/TR/sparql11-query/#func-datatype
	//  for SPARQL 1.0 only!
    public static String _DATATYPE(String text)
	{
		RDFLiteral lit = new RDFLiteral(text);
		String l = lit.getDatatype();
		return l.equals("") ? "http://www.w3.org/2001/XMLSchema#string" : l;
	}
  
	public static Object DATATYPE(List<Object> args) throws Exception
	{
		if (args.size() != 1)
		{
            throw new Exception("Wrong number of arguments");
        }
		
		Object result = _DATATYPE(args.get(0).toString());
		
		return result;
	}

	//  http://www.w3.org/TR/sparql11-query/#func-bound
	public static Object BOUND(List<Object> args) throws Exception
	{
		if (args.size() != 1)
		{
			throw new Exception("Wrong number of arguments");
		}
		
		if (args.get(0) != null && !args.get(0).toString().equals(""))
			return true;
		
		return false;
	}

	//  http://www.w3.org/TR/sparql11-query/#func-sameTerm
	public static Object sameTerm(List<Object> args) throws Exception
	{
		if (args.size() != 2)
		{
			throw new Exception("Wrong number of arguments");
		}
		
		String textA = args.get(0).toString();
		String textB = args.get(1).toString();
		Boolean result = textA.equals(textB);
			
		return result;
	}

	
	//  http://www.w3.org/TR/sparql11-query/#func-isIRI
	public static Object isIRI(List<Object> args) throws Exception
	{
		if (args.size() != 1)
		{
			throw new Exception("Wrong number of arguments");
		}
		
		//  TODO
		String text = args.get(0).toString();
		Boolean result = text.matches("'<' ([^<>\"{}|^`\\]-[#x00-#x20])* '>'");
		
		return result;
	}

	public static Object isURI(List<Object> args) throws Exception
	{
		return isIRI(args);
	}
	
	//  http://www.w3.org/TR/sparql11-query/#func-isBlank
	public static Object isBLANK (List<Object> args) throws Exception
	{
		if (args.size() != 1)
		{
			throw new Exception("Wrong number of arguments");
		}
		
		if (args.get(0).toString().equals("_") || args.get(0).toString().equals("_:"))
			return true;
		
		return false;
	}

	
	//  http://www.w3.org/TR/sparql11-query/#func-isLiteral
	public static Object isLITERAL(List<Object> args) throws Exception
	{
		Boolean result = (Boolean)isIRI(args);
		
		return !result;
	}


	//  http://www.w3.org/TR/sparql11-query/#func-regex
	//  http://www.w3.org/TR/xpath-functions/#regex-syntax
	public static Object _REGEX(String text, String pattern)
  {
		Boolean result = text.matches(pattern);
    
		return result;
	}
		
	
	public static Object _REGEX(String text, String pattern, String flags)
	{
		//  s  Pattern.DOTALL
        //  m  Pattern.MULTILINE
        //  i  Pattern.CASE_INSENSITIVE
        //  x  Pattern.COMMENTS
        
        pattern = ".*" + pattern + ".*";
        
        if (flags.contains("s"))
        {
            Pattern p = Pattern.compile(pattern, Pattern.DOTALL);
            Matcher m = p.matcher(text);
            Boolean result = m.matches();
            return result;
        }
        else if (flags.contains("m"))
        {
            Pattern p = Pattern.compile(pattern, Pattern.MULTILINE);
            Matcher m = p.matcher(text);
            Boolean result = m.matches();
            return result;
        }
        else if (flags.contains("i"))
        {
            Pattern p = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
            Matcher m = p.matcher(text);
            Boolean result = m.matches();
            return result;
        }
        else if (flags.contains("x"))
        {
            Pattern p = Pattern.compile(pattern, Pattern.COMMENTS);
            Matcher m = p.matcher(text);
            Boolean result = m.matches();
            return result;
        }
    
		return _REGEX(text, pattern);
	}
	
	
	public static Object REGEX(List<Object> args) throws Exception
	{
		int size = args.size();
		if (size != 2 && size != 3)
		{
			throw new Exception("Wrong number of arguments");
		}
		RDFLiteral lit = new RDFLiteral(args.get(0).toString());
		String text = lit.getText();
        String pattern = args.get(1).toString();
        Object r = false;
		if (2 == size)
		{
            r = _REGEX(text, pattern);
		}
		else if (3 == size)
		{
            String flags = args.get(2).toString();
            r = _REGEX(text, pattern, flags);
		}
		return r;
    }
}