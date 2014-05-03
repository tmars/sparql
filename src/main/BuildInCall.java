import java.util.*;
import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class BuildInCall
{
  
	public static Object exec(String call, List<Object> args)
	{
		Object res = null;
		Method[] methods = BuildInCall.class.getMethods();
		try 
		{
			for (Method m : methods)
			{
				if (m.getName().equals(call)) 
				{
					res = m.invoke(BuildInCall.class, args);
					break;
				}
			}
		}
		catch (Exception e)
		{
		}
		return res;
	}
	
	//  http://www.w3.org/TR/sparql11-query/#func-str
	public static Object STR(List<Object> args) throws Exception
	{
		if (args.size() != 1) {
      throw new Exception("Wrong number of arguments");
    }
		
		String result = args.get(0).toString();
		
		return result;
	}
	
	//  http://www.w3.org/TR/sparql11-query/#func-lang
	public static String _LANG(String text)
	{		
		String[] parts = text.split("@");
		if (parts.length == 2)
    {
      System.out.println("**********");
      System.out.println(parts[1]);
      System.out.println("**********");
			return parts[1];
    }
		
		return "";
	}
	
	public static Object LANG(List<Object> args) throws Exception
	{
		if (args.size() != 1) {
      throw new Exception("Wrong number of arguments");
    }
		
		String text = args.get(0).toString();
		String result = _LANG(text);
		
		return result;
	}

	
	//  http://www.w3.org/TR/sparql11-query/#func-langMatches
	public static Boolean _LANGMATCHES(String text, String lang)
	{		
		String langA = _LANG(text);
		String langB = lang;
		
		if (langB == "*" && langA != "")
			return true;
		else if (langB == langA)
			return true;
		
		return false;
	}

	
	public static Object LANGMATCHES(List<Object> args) throws Exception
	{
		if (args.size() != 2) {
      throw new Exception("Wrong number of arguments");
    }
		
		String text = args.get(0).toString();
		String lang = args.get(1).toString();
		
		Boolean result = _LANGMATCHES(text, lang);
		
		return result;
	}

	//  http://www.w3.org/TR/sparql11-query/#func-datatype
	//  for SPARQL 1.0 only!
	public static Object DATATYPE(List<Object> args) throws Exception
	{
		if (args.size() != 1) {
      throw new Exception("Wrong number of arguments");
    }
		
		String text = args.get(0).toString();
		String[] parts = text.split("^^");
		if (parts.length == 2)
		{
			String result = parts[1];
      System.out.println("**********");
      System.out.println(result);
      System.out.println("**********");
			return result;
		}
		
		return "xsd:string";
	}

	//  http://www.w3.org/TR/sparql11-query/#func-bound
	public static Object BOUND(List<Object> args) throws Exception
	{
		if (args.size() != 1) {
      throw new Exception("Wrong number of arguments");
    }
		
		if (args.get(0) != null && args.get(0).toString() != "")
			return true;
		
		return false;
	}

	//  http://www.w3.org/TR/sparql11-query/#func-sameTerm
	public static Object sameTerm(List<Object> args) throws Exception
	{
		if (args.size() != 2) {
      throw new Exception("Wrong number of arguments");
    }
		
		String textA = args.get(0).toString();
		String textB = args.get(1).toString();
		Boolean result = textA == textB;
		
		return result;
	}

	
	//  http://www.w3.org/TR/sparql11-query/#func-isIRI
	public static Object isIRI(List<Object> args) throws Exception
	{
		if (args.size() != 1) {
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
		if (args.size() != 1) {
      throw new Exception("Wrong number of arguments");
    }
		
		if (args.get(0).toString() == "_" || args.get(0).toString() == "_:")
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
		
		if (2 == size)
		{
            String text = args.get(0).toString();
            String pattern = args.get(1).toString();
            Object r = _REGEX(text, pattern);
            System.out.println("**********");
            System.out.println(text);
            System.out.println(pattern);
            System.out.println(r);
            System.out.println("**********");
            return r;
		}
		else if (3 == size)
		{
            String text = args.get(0).toString();
            String pattern = args.get(1).toString();
            String flags = args.get(2).toString();
            Object r = _REGEX(text, pattern, flags);
            System.out.println("**********");
            System.out.println(text);
            System.out.println(pattern);
            System.out.println(flags);
            System.out.println(r);
            System.out.println("**********");
            return r;
		}
      
    throw new Exception("Wrong number of arguments");
	}
}