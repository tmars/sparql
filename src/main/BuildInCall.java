import java.util.*;
import java.lang.reflect.Method;

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
	public static Object STR(List<Object> args)
	{
		if (args.size() != 1) return null;
		
		String result = args.get(0).toString();
		
		return result;
	}
	
	//  http://www.w3.org/TR/sparql11-query/#func-lang
	public static String LANG(String text)
	{		
		String[] parts = text.split("@");
		if (parts.length == 2)
			return parts[1];
		
		return "";
	}
	
	public static Object LANG(List<Object> args)
	{
		if (args.size() != 1) return null;
		
		String text = args.get(0).toString();
		String result = LANG(text);
		
		return result;
	}

	
	//  http://www.w3.org/TR/sparql11-query/#func-langMatches
	public static Boolean LANGMATCHES(String text, String lang)
	{		
		String langA = LANG(text);
		String langB = lang;
		
		if (langB == "*" && langA != "")
			return true;
		else if (langB == langA)
			return true;
		
		return false;
	}

	
	public static Object LANGMATCHES(List<Object> args)
	{
		if (args.size() != 2) return null;
		
		String text = args.get(0).toString();
		String lang = args.get(1).toString();
		
		Boolean result = LANGMATCHES(text, lang);
		
		return result;
	}

	//  http://www.w3.org/TR/sparql11-query/#func-datatype
	//  for SPARQL 1.0 only!
	public static Object DATATYPE(List<Object> args)
	{
		if (args.size() != 1) return null;
		
		String text = args.get(0).toString();
		String[] parts = text.split("^^");
		if (parts.length == 2)
		{
			String result = parts[1];
			return result;
		}
		
		return "xsd:string";
	}

	//  http://www.w3.org/TR/sparql11-query/#func-bound
	public static Object BOUND(List<Object> args)
	{
		if (args.size() != 1) return null;
		
		if (args.get(0) != null && args.get(0).toString() != "")
			return true;
		
		return false;
	}

	//  http://www.w3.org/TR/sparql11-query/#func-sameTerm
	public static Object sameTerm(List<Object> args)
	{
		if (args.size() != 2) return null;
		
		String textA = args.get(0).toString();
		String textB = args.get(1).toString();
		Boolean result = textA == textB;
		
		return result;
	}

	
	//  http://www.w3.org/TR/sparql11-query/#func-isIRI
	public static Object isIRI(List<Object> args)
	{
		if (args.size() != 1) return null;
		
		//  TODO
		
		return true;
	}

	public static Object isURI(List<Object> args)
	{
		return isIRI(args);
	}

	
	//  http://www.w3.org/TR/sparql11-query/#func-isBlank
	public static Object isBLANK(List<Object> args)
	{
		if (args.size() != 1) return null;
		
		if (args.get(0).toString() == "_" || args.get(0).toString() == "_:")
			return true;
		
		return false;
	}

	
	//  http://www.w3.org/TR/sparql11-query/#func-isLiteral
	public static Object isLITERAL(List<Object> args)
	{
		Boolean result = (Boolean)isIRI(args);
		
		return !result;
	}

	
	//  http://www.w3.org/TR/sparql11-query/#func-regex
	//  http://www.w3.org/TR/xpath-functions/#regex-syntax
	public static Object REGEX(String text, String pattern)
	{
		//  TODO
		return true;
	}
		
	
	public static Object REGEX(String text, String pattern, String flags)
	{
		//  TODO
		return false;
	}
	
	
	public static Object REGEX(List<Object> args)
	{
		int size = args.size();
		
		if (2 == size)
		{
			return REGEX(args.get(0).toString(), args.get(1).toString());
		}
		else if (3 == size)
		{
			return REGEX(args.get(0).toString(), args.get(1).toString(), args.get(2).toString());
		}
		
		return null;
	}
	
}