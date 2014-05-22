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
        
        Pattern patternIRI = Pattern.compile("^(?:[a-z](?:[-a-z0-9\\+\\.])*:(?:\\/\\/(?:(?:%[0-9a-f][0-9a-f]|[-a-z0-9\\._~\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF\\uDC00\\uD800-\\uDFFD\\uD83F\\uDC00\\uD840-\\uDFFD\\uD87F\\uDC00\\uD880-\\uDFFD\\uD8BF\\uDC00\\uD8C0-\\uDFFD\\uD8FF\\uDC00\\uD900-\\uDFFD\\uD93F\\uDC00\\uD940-\\uDFFD\\uD97F\\uDC00\\uD980-\\uDFFD\\uD9BF\\uDC00\\uD9C0-\\uDFFD\\uD9FF\\uDC00\\uDA00-\\uDFFD\\uDA3F\\uDC00\\uDA40-\\uDFFD\\uDA7F\\uDC00\\uDA80-\\uDFFD\\uDABF\\uDC00\\uDAC0-\\uDFFD\\uDAFF\\uDC00\\uDB00-\\uDFFD\\uDB3F\\uDC00\\uDB44-\\uDFFD\\uDB7F!\\$&'\\(\\)\\*\\+,;=:])*@)?(?:\\[(?:(?:(?:[0-9a-f]{1,4}:){6}(?:[0-9a-f]{1,4}:[0-9a-f]{1,4}|(?:[0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])(?:\\.(?:[0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])){3})|::(?:[0-9a-f]{1,4}:){5}(?:[0-9a-f]{1,4}:[0-9a-f]{1,4}|(?:[0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])(?:\\.(?:[0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])){3})|(?:[0-9a-f]{1,4})?::(?:[0-9a-f]{1,4}:){4}(?:[0-9a-f]{1,4}:[0-9a-f]{1,4}|(?:[0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])(?:\\.(?:[0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])){3})|(?:[0-9a-f]{1,4}:[0-9a-f]{1,4})?::(?:[0-9a-f]{1,4}:){3}(?:[0-9a-f]{1,4}:[0-9a-f]{1,4}|(?:[0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])(?:\\.(?:[0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])){3})|(?:(?:[0-9a-f]{1,4}:){0,2}[0-9a-f]{1,4})?::(?:[0-9a-f]{1,4}:){2}(?:[0-9a-f]{1,4}:[0-9a-f]{1,4}|(?:[0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])(?:\\.(?:[0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])){3})|(?:(?:[0-9a-f]{1,4}:){0,3}[0-9a-f]{1,4})?::[0-9a-f]{1,4}:(?:[0-9a-f]{1,4}:[0-9a-f]{1,4}|(?:[0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])(?:\\.(?:[0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])){3})|(?:(?:[0-9a-f]{1,4}:){0,4}[0-9a-f]{1,4})?::(?:[0-9a-f]{1,4}:[0-9a-f]{1,4}|(?:[0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])(?:\\.(?:[0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])){3})|(?:(?:[0-9a-f]{1,4}:){0,5}[0-9a-f]{1,4})?::[0-9a-f]{1,4}|(?:(?:[0-9a-f]{1,4}:){0,6}[0-9a-f]{1,4})?::)|v[0-9a-f]+[-a-z0-9\\._~!\\$&'\\(\\)\\*\\+,;=:]+)\\]|(?:[0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])(?:\\.(?:[0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])){3}|(?:%[0-9a-f][0-9a-f]|[-a-z0-9\\._~\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF\\uDC00\\uD800-\\uDFFD\\uD83F\\uDC00\\uD840-\\uDFFD\\uD87F\\uDC00\\uD880-\\uDFFD\\uD8BF\\uDC00\\uD8C0-\\uDFFD\\uD8FF\\uDC00\\uD900-\\uDFFD\\uD93F\\uDC00\\uD940-\\uDFFD\\uD97F\\uDC00\\uD980-\\uDFFD\\uD9BF\\uDC00\\uD9C0-\\uDFFD\\uD9FF\\uDC00\\uDA00-\\uDFFD\\uDA3F\\uDC00\\uDA40-\\uDFFD\\uDA7F\\uDC00\\uDA80-\\uDFFD\\uDABF\\uDC00\\uDAC0-\\uDFFD\\uDAFF\\uDC00\\uDB00-\\uDFFD\\uDB3F\\uDC00\\uDB44-\\uDFFD\\uDB7F!\\$&'\\(\\)\\*\\+,;=@])*)(?::[0-9]*)?(?:\\/(?:(?:%[0-9a-f][0-9a-f]|[-a-z0-9\\._~\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF\\uDC00\\uD800-\\uDFFD\\uD83F\\uDC00\\uD840-\\uDFFD\\uD87F\\uDC00\\uD880-\\uDFFD\\uD8BF\\uDC00\\uD8C0-\\uDFFD\\uD8FF\\uDC00\\uD900-\\uDFFD\\uD93F\\uDC00\\uD940-\\uDFFD\\uD97F\\uDC00\\uD980-\\uDFFD\\uD9BF\\uDC00\\uD9C0-\\uDFFD\\uD9FF\\uDC00\\uDA00-\\uDFFD\\uDA3F\\uDC00\\uDA40-\\uDFFD\\uDA7F\\uDC00\\uDA80-\\uDFFD\\uDABF\\uDC00\\uDAC0-\\uDFFD\\uDAFF\\uDC00\\uDB00-\\uDFFD\\uDB3F\\uDC00\\uDB44-\\uDFFD\\uDB7F!\\$&'\\(\\)\\*\\+,;=:@]))*)*|\\/(?:(?:(?:(?:%[0-9a-f][0-9a-f]|[-a-z0-9\\._~\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF\\uDC00\\uD800-\\uDFFD\\uD83F\\uDC00\\uD840-\\uDFFD\\uD87F\\uDC00\\uD880-\\uDFFD\\uD8BF\\uDC00\\uD8C0-\\uDFFD\\uD8FF\\uDC00\\uD900-\\uDFFD\\uD93F\\uDC00\\uD940-\\uDFFD\\uD97F\\uDC00\\uD980-\\uDFFD\\uD9BF\\uDC00\\uD9C0-\\uDFFD\\uD9FF\\uDC00\\uDA00-\\uDFFD\\uDA3F\\uDC00\\uDA40-\\uDFFD\\uDA7F\\uDC00\\uDA80-\\uDFFD\\uDABF\\uDC00\\uDAC0-\\uDFFD\\uDAFF\\uDC00\\uDB00-\\uDFFD\\uDB3F\\uDC00\\uDB44-\\uDFFD\\uDB7F!\\$&'\\(\\)\\*\\+,;=:@]))+)(?:\\/(?:(?:%[0-9a-f][0-9a-f]|[-a-z0-9\\._~\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF\\uDC00\\uD800-\\uDFFD\\uD83F\\uDC00\\uD840-\\uDFFD\\uD87F\\uDC00\\uD880-\\uDFFD\\uD8BF\\uDC00\\uD8C0-\\uDFFD\\uD8FF\\uDC00\\uD900-\\uDFFD\\uD93F\\uDC00\\uD940-\\uDFFD\\uD97F\\uDC00\\uD980-\\uDFFD\\uD9BF\\uDC00\\uD9C0-\\uDFFD\\uD9FF\\uDC00\\uDA00-\\uDFFD\\uDA3F\\uDC00\\uDA40-\\uDFFD\\uDA7F\\uDC00\\uDA80-\\uDFFD\\uDABF\\uDC00\\uDAC0-\\uDFFD\\uDAFF\\uDC00\\uDB00-\\uDFFD\\uDB3F\\uDC00\\uDB44-\\uDFFD\\uDB7F!\\$&'\\(\\)\\*\\+,;=:@]))*)*)?|(?:(?:(?:%[0-9a-f][0-9a-f]|[-a-z0-9\\._~\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF\\uDC00\\uD800-\\uDFFD\\uD83F\\uDC00\\uD840-\\uDFFD\\uD87F\\uDC00\\uD880-\\uDFFD\\uD8BF\\uDC00\\uD8C0-\\uDFFD\\uD8FF\\uDC00\\uD900-\\uDFFD\\uD93F\\uDC00\\uD940-\\uDFFD\\uD97F\\uDC00\\uD980-\\uDFFD\\uD9BF\\uDC00\\uD9C0-\\uDFFD\\uD9FF\\uDC00\\uDA00-\\uDFFD\\uDA3F\\uDC00\\uDA40-\\uDFFD\\uDA7F\\uDC00\\uDA80-\\uDFFD\\uDABF\\uDC00\\uDAC0-\\uDFFD\\uDAFF\\uDC00\\uDB00-\\uDFFD\\uDB3F\\uDC00\\uDB44-\\uDFFD\\uDB7F!\\$&'\\(\\)\\*\\+,;=:@]))+)(?:\\/(?:(?:%[0-9a-f][0-9a-f]|[-a-z0-9\\._~\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF\\uDC00\\uD800-\\uDFFD\\uD83F\\uDC00\\uD840-\\uDFFD\\uD87F\\uDC00\\uD880-\\uDFFD\\uD8BF\\uDC00\\uD8C0-\\uDFFD\\uD8FF\\uDC00\\uD900-\\uDFFD\\uD93F\\uDC00\\uD940-\\uDFFD\\uD97F\\uDC00\\uD980-\\uDFFD\\uD9BF\\uDC00\\uD9C0-\\uDFFD\\uD9FF\\uDC00\\uDA00-\\uDFFD\\uDA3F\\uDC00\\uDA40-\\uDFFD\\uDA7F\\uDC00\\uDA80-\\uDFFD\\uDABF\\uDC00\\uDAC0-\\uDFFD\\uDAFF\\uDC00\\uDB00-\\uDFFD\\uDB3F\\uDC00\\uDB44-\\uDFFD\\uDB7F!\\$&'\\(\\)\\*\\+,;=:@]))*)*|(?!(?:%[0-9a-f][0-9a-f]|[-a-z0-9\\._~\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF\\uDC00\\uD800-\\uDFFD\\uD83F\\uDC00\\uD840-\\uDFFD\\uD87F\\uDC00\\uD880-\\uDFFD\\uD8BF\\uDC00\\uD8C0-\\uDFFD\\uD8FF\\uDC00\\uD900-\\uDFFD\\uD93F\\uDC00\\uD940-\\uDFFD\\uD97F\\uDC00\\uD980-\\uDFFD\\uD9BF\\uDC00\\uD9C0-\\uDFFD\\uD9FF\\uDC00\\uDA00-\\uDFFD\\uDA3F\\uDC00\\uDA40-\\uDFFD\\uDA7F\\uDC00\\uDA80-\\uDFFD\\uDABF\\uDC00\\uDAC0-\\uDFFD\\uDAFF\\uDC00\\uDB00-\\uDFFD\\uDB3F\\uDC00\\uDB44-\\uDFFD\\uDB7F!\\$&'\\(\\)\\*\\+,;=:@])))(?:\\?(?:(?:%[0-9a-f][0-9a-f]|[-a-z0-9\\._~\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF\\uDC00\\uD800-\\uDFFD\\uD83F\\uDC00\\uD840-\\uDFFD\\uD87F\\uDC00\\uD880-\\uDFFD\\uD8BF\\uDC00\\uD8C0-\\uDFFD\\uD8FF\\uDC00\\uD900-\\uDFFD\\uD93F\\uDC00\\uD940-\\uDFFD\\uD97F\\uDC00\\uD980-\\uDFFD\\uD9BF\\uDC00\\uD9C0-\\uDFFD\\uD9FF\\uDC00\\uDA00-\\uDFFD\\uDA3F\\uDC00\\uDA40-\\uDFFD\\uDA7F\\uDC00\\uDA80-\\uDFFD\\uDABF\\uDC00\\uDAC0-\\uDFFD\\uDAFF\\uDC00\\uDB00-\\uDFFD\\uDB3F\\uDC00\\uDB44-\\uDFFD\\uDB7F!\\$&'\\(\\)\\*\\+,;=:@])|[\\uE000-\\uF8FF\\uDC00\\uDB80-\\uDFFD\\uDBBF|\\uDC00\\uDBC0-\\uDFFD\\uDBFF\\/\\?])*)?(?:\\#(?:(?:%[0-9a-f][0-9a-f]|[-a-z0-9\\._~\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF\\uDC00\\uD800-\\uDFFD\\uD83F\\uDC00\\uD840-\\uDFFD\\uD87F\\uDC00\\uD880-\\uDFFD\\uD8BF\\uDC00\\uD8C0-\\uDFFD\\uD8FF\\uDC00\\uD900-\\uDFFD\\uD93F\\uDC00\\uD940-\\uDFFD\\uD97F\\uDC00\\uD980-\\uDFFD\\uD9BF\\uDC00\\uD9C0-\\uDFFD\\uD9FF\\uDC00\\uDA00-\\uDFFD\\uDA3F\\uDC00\\uDA40-\\uDFFD\\uDA7F\\uDC00\\uDA80-\\uDFFD\\uDABF\\uDC00\\uDAC0-\\uDFFD\\uDAFF\\uDC00\\uDB00-\\uDFFD\\uDB3F\\uDC00\\uDB44-\\uDFFD\\uDB7F!\\$&'\\(\\)\\*\\+,;=:@])|[\\/\\?])*)?|(?:\\/\\/(?:(?:%[0-9a-f][0-9a-f]|[-a-z0-9\\._~\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF\\uDC00\\uD800-\\uDFFD\\uD83F\\uDC00\\uD840-\\uDFFD\\uD87F\\uDC00\\uD880-\\uDFFD\\uD8BF\\uDC00\\uD8C0-\\uDFFD\\uD8FF\\uDC00\\uD900-\\uDFFD\\uD93F\\uDC00\\uD940-\\uDFFD\\uD97F\\uDC00\\uD980-\\uDFFD\\uD9BF\\uDC00\\uD9C0-\\uDFFD\\uD9FF\\uDC00\\uDA00-\\uDFFD\\uDA3F\\uDC00\\uDA40-\\uDFFD\\uDA7F\\uDC00\\uDA80-\\uDFFD\\uDABF\\uDC00\\uDAC0-\\uDFFD\\uDAFF\\uDC00\\uDB00-\\uDFFD\\uDB3F\\uDC00\\uDB44-\\uDFFD\\uDB7F!\\$&'\\(\\)\\*\\+,;=:])*@)?(?:\\[(?:(?:(?:[0-9a-f]{1,4}:){6}(?:[0-9a-f]{1,4}:[0-9a-f]{1,4}|(?:[0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])(?:\\.(?:[0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])){3})|::(?:[0-9a-f]{1,4}:){5}(?:[0-9a-f]{1,4}:[0-9a-f]{1,4}|(?:[0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])(?:\\.(?:[0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])){3})|(?:[0-9a-f]{1,4})?::(?:[0-9a-f]{1,4}:){4}(?:[0-9a-f]{1,4}:[0-9a-f]{1,4}|(?:[0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])(?:\\.(?:[0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])){3})|(?:[0-9a-f]{1,4}:[0-9a-f]{1,4})?::(?:[0-9a-f]{1,4}:){3}(?:[0-9a-f]{1,4}:[0-9a-f]{1,4}|(?:[0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])(?:\\.(?:[0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])){3})|(?:(?:[0-9a-f]{1,4}:){0,2}[0-9a-f]{1,4})?::(?:[0-9a-f]{1,4}:){2}(?:[0-9a-f]{1,4}:[0-9a-f]{1,4}|(?:[0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])(?:\\.(?:[0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])){3})|(?:(?:[0-9a-f]{1,4}:){0,3}[0-9a-f]{1,4})?::[0-9a-f]{1,4}:(?:[0-9a-f]{1,4}:[0-9a-f]{1,4}|(?:[0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])(?:\\.(?:[0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])){3})|(?:(?:[0-9a-f]{1,4}:){0,4}[0-9a-f]{1,4})?::(?:[0-9a-f]{1,4}:[0-9a-f]{1,4}|(?:[0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])(?:\\.(?:[0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])){3})|(?:(?:[0-9a-f]{1,4}:){0,5}[0-9a-f]{1,4})?::[0-9a-f]{1,4}|(?:(?:[0-9a-f]{1,4}:){0,6}[0-9a-f]{1,4})?::)|v[0-9a-f]+[-a-z0-9\\._~!\\$&'\\(\\)\\*\\+,;=:]+)\\]|(?:[0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])(?:\\.(?:[0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])){3}|(?:%[0-9a-f][0-9a-f]|[-a-z0-9\\._~\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF\\uDC00\\uD800-\\uDFFD\\uD83F\\uDC00\\uD840-\\uDFFD\\uD87F\\uDC00\\uD880-\\uDFFD\\uD8BF\\uDC00\\uD8C0-\\uDFFD\\uD8FF\\uDC00\\uD900-\\uDFFD\\uD93F\\uDC00\\uD940-\\uDFFD\\uD97F\\uDC00\\uD980-\\uDFFD\\uD9BF\\uDC00\\uD9C0-\\uDFFD\\uD9FF\\uDC00\\uDA00-\\uDFFD\\uDA3F\\uDC00\\uDA40-\\uDFFD\\uDA7F\\uDC00\\uDA80-\\uDFFD\\uDABF\\uDC00\\uDAC0-\\uDFFD\\uDAFF\\uDC00\\uDB00-\\uDFFD\\uDB3F\\uDC00\\uDB44-\\uDFFD\\uDB7F!\\$&'\\(\\)\\*\\+,;=@])*)(?::[0-9]*)?(?:\\/(?:(?:%[0-9a-f][0-9a-f]|[-a-z0-9\\._~\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF\\uDC00\\uD800-\\uDFFD\\uD83F\\uDC00\\uD840-\\uDFFD\\uD87F\\uDC00\\uD880-\\uDFFD\\uD8BF\\uDC00\\uD8C0-\\uDFFD\\uD8FF\\uDC00\\uD900-\\uDFFD\\uD93F\\uDC00\\uD940-\\uDFFD\\uD97F\\uDC00\\uD980-\\uDFFD\\uD9BF\\uDC00\\uD9C0-\\uDFFD\\uD9FF\\uDC00\\uDA00-\\uDFFD\\uDA3F\\uDC00\\uDA40-\\uDFFD\\uDA7F\\uDC00\\uDA80-\\uDFFD\\uDABF\\uDC00\\uDAC0-\\uDFFD\\uDAFF\\uDC00\\uDB00-\\uDFFD\\uDB3F\\uDC00\\uDB44-\\uDFFD\\uDB7F!\\$&'\\(\\)\\*\\+,;=:@]))*)*|\\/(?:(?:(?:(?:%[0-9a-f][0-9a-f]|[-a-z0-9\\._~\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF\\uDC00\\uD800-\\uDFFD\\uD83F\\uDC00\\uD840-\\uDFFD\\uD87F\\uDC00\\uD880-\\uDFFD\\uD8BF\\uDC00\\uD8C0-\\uDFFD\\uD8FF\\uDC00\\uD900-\\uDFFD\\uD93F\\uDC00\\uD940-\\uDFFD\\uD97F\\uDC00\\uD980-\\uDFFD\\uD9BF\\uDC00\\uD9C0-\\uDFFD\\uD9FF\\uDC00\\uDA00-\\uDFFD\\uDA3F\\uDC00\\uDA40-\\uDFFD\\uDA7F\\uDC00\\uDA80-\\uDFFD\\uDABF\\uDC00\\uDAC0-\\uDFFD\\uDAFF\\uDC00\\uDB00-\\uDFFD\\uDB3F\\uDC00\\uDB44-\\uDFFD\\uDB7F!\\$&'\\(\\)\\*\\+,;=:@]))+)(?:\\/(?:(?:%[0-9a-f][0-9a-f]|[-a-z0-9\\._~\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF\\uDC00\\uD800-\\uDFFD\\uD83F\\uDC00\\uD840-\\uDFFD\\uD87F\\uDC00\\uD880-\\uDFFD\\uD8BF\\uDC00\\uD8C0-\\uDFFD\\uD8FF\\uDC00\\uD900-\\uDFFD\\uD93F\\uDC00\\uD940-\\uDFFD\\uD97F\\uDC00\\uD980-\\uDFFD\\uD9BF\\uDC00\\uD9C0-\\uDFFD\\uD9FF\\uDC00\\uDA00-\\uDFFD\\uDA3F\\uDC00\\uDA40-\\uDFFD\\uDA7F\\uDC00\\uDA80-\\uDFFD\\uDABF\\uDC00\\uDAC0-\\uDFFD\\uDAFF\\uDC00\\uDB00-\\uDFFD\\uDB3F\\uDC00\\uDB44-\\uDFFD\\uDB7F!\\$&'\\(\\)\\*\\+,;=:@]))*)*)?|(?:(?:%[0-9a-f][0-9a-f]|[-a-z0-9\\._~\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF\\uDC00\\uD800-\\uDFFD\\uD83F\\uDC00\\uD840-\\uDFFD\\uD87F\\uDC00\\uD880-\\uDFFD\\uD8BF\\uDC00\\uD8C0-\\uDFFD\\uD8FF\\uDC00\\uD900-\\uDFFD\\uD93F\\uDC00\\uD940-\\uDFFD\\uD97F\\uDC00\\uD980-\\uDFFD\\uD9BF\\uDC00\\uD9C0-\\uDFFD\\uD9FF\\uDC00\\uDA00-\\uDFFD\\uDA3F\\uDC00\\uDA40-\\uDFFD\\uDA7F\\uDC00\\uDA80-\\uDFFD\\uDABF\\uDC00\\uDAC0-\\uDFFD\\uDAFF\\uDC00\\uDB00-\\uDFFD\\uDB3F\\uDC00\\uDB44-\\uDFFD\\uDB7F!\\$&'\\(\\)\\*\\+,;=@])+)(?:\\/(?:(?:%[0-9a-f][0-9a-f]|[-a-z0-9\\._~\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF\\uDC00\\uD800-\\uDFFD\\uD83F\\uDC00\\uD840-\\uDFFD\\uD87F\\uDC00\\uD880-\\uDFFD\\uD8BF\\uDC00\\uD8C0-\\uDFFD\\uD8FF\\uDC00\\uD900-\\uDFFD\\uD93F\\uDC00\\uD940-\\uDFFD\\uD97F\\uDC00\\uD980-\\uDFFD\\uD9BF\\uDC00\\uD9C0-\\uDFFD\\uD9FF\\uDC00\\uDA00-\\uDFFD\\uDA3F\\uDC00\\uDA40-\\uDFFD\\uDA7F\\uDC00\\uDA80-\\uDFFD\\uDABF\\uDC00\\uDAC0-\\uDFFD\\uDAFF\\uDC00\\uDB00-\\uDFFD\\uDB3F\\uDC00\\uDB44-\\uDFFD\\uDB7F!\\$&'\\(\\)\\*\\+,;=:@]))*)*|(?!(?:%[0-9a-f][0-9a-f]|[-a-z0-9\\._~\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF\\uDC00\\uD800-\\uDFFD\\uD83F\\uDC00\\uD840-\\uDFFD\\uD87F\\uDC00\\uD880-\\uDFFD\\uD8BF\\uDC00\\uD8C0-\\uDFFD\\uD8FF\\uDC00\\uD900-\\uDFFD\\uD93F\\uDC00\\uD940-\\uDFFD\\uD97F\\uDC00\\uD980-\\uDFFD\\uD9BF\\uDC00\\uD9C0-\\uDFFD\\uD9FF\\uDC00\\uDA00-\\uDFFD\\uDA3F\\uDC00\\uDA40-\\uDFFD\\uDA7F\\uDC00\\uDA80-\\uDFFD\\uDABF\\uDC00\\uDAC0-\\uDFFD\\uDAFF\\uDC00\\uDB00-\\uDFFD\\uDB3F\\uDC00\\uDB44-\\uDFFD\\uDB7F!\\$&'\\(\\)\\*\\+,;=:@])))(?:\\?(?:(?:%[0-9a-f][0-9a-f]|[-a-z0-9\\._~\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF\\uDC00\\uD800-\\uDFFD\\uD83F\\uDC00\\uD840-\\uDFFD\\uD87F\\uDC00\\uD880-\\uDFFD\\uD8BF\\uDC00\\uD8C0-\\uDFFD\\uD8FF\\uDC00\\uD900-\\uDFFD\\uD93F\\uDC00\\uD940-\\uDFFD\\uD97F\\uDC00\\uD980-\\uDFFD\\uD9BF\\uDC00\\uD9C0-\\uDFFD\\uD9FF\\uDC00\\uDA00-\\uDFFD\\uDA3F\\uDC00\\uDA40-\\uDFFD\\uDA7F\\uDC00\\uDA80-\\uDFFD\\uDABF\\uDC00\\uDAC0-\\uDFFD\\uDAFF\\uDC00\\uDB00-\\uDFFD\\uDB3F\\uDC00\\uDB44-\\uDFFD\\uDB7F!\\$&'\\(\\)\\*\\+,;=:@])|[\\uE000-\\uF8FF\\uDC00\\uDB80-\\uDFFD\\uDBBF|\\uDC00\\uDBC0-\\uDFFD\\uDBFF\\/\\?])*)?(?:\\#(?:(?:%[0-9a-f][0-9a-f]|[-a-z0-9\\._~\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF\\uDC00\\uD800-\\uDFFD\\uD83F\\uDC00\\uD840-\\uDFFD\\uD87F\\uDC00\\uD880-\\uDFFD\\uD8BF\\uDC00\\uD8C0-\\uDFFD\\uD8FF\\uDC00\\uD900-\\uDFFD\\uD93F\\uDC00\\uD940-\\uDFFD\\uD97F\\uDC00\\uD980-\\uDFFD\\uD9BF\\uDC00\\uD9C0-\\uDFFD\\uD9FF\\uDC00\\uDA00-\\uDFFD\\uDA3F\\uDC00\\uDA40-\\uDFFD\\uDA7F\\uDC00\\uDA80-\\uDFFD\\uDABF\\uDC00\\uDAC0-\\uDFFD\\uDAFF\\uDC00\\uDB00-\\uDFFD\\uDB3F\\uDC00\\uDB44-\\uDFFD\\uDB7F!\\$&'\\(\\)\\*\\+,;=:@])|[\\/\\?])*)?)$", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
        
        String text = args.get(0).toString();
        int len = text.length();
        if (2 < len && '<' == text.charAt(0) && text.charAt(len - 1) == '>') {
            text = text.substring(1, len - 1);
        }
        Matcher matcher = patternIRI.matcher(text);
        Boolean result = matcher.matches();
		
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
		
		if ( ((RDFNode)args.get(0)).getType() == RDFNode.Type.RESOURCE ) 
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