import java.util.*;
import org.antlr.runtime.*;
import org.antlr.runtime.tree.*;
import org.antlr.stringtemplate.*;
import java.lang.reflect.Method;
class SparqlExpression
{
    Hashtable<String, Object> vars = null;
    
    private static class BuildInCall
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
        
        public static Object STR(List<Object> args)
        {
            if (args.size() != 1) return null;
        	return args.get(0).toString();
        }
        
        public static Object LANG(List<Object> args)
        {
            if (args.size() != 1) return null;
        	
        	return args.get(0).toString();
        }
    
        public static Object LANGMATCHES(List<Object> args)
        {
            if (args.size() != 2) return null;
        	
        	return args.get(0).toString();
        }
    
        public static Object DATATYPE(List<Object> args)
        {
            if (args.size() != 1) return null;
        	
        	return args.get(0).toString();
        }
    
        public static Object BOUND(List<Object> args)
        {
            if (args.size() != 1) return null;
        	
        	return args.get(0).toString();
        }
    
        public static Object sameTerm(List<Object> args)
        {
            if (args.size() != 1) return null;
        	
        	return args.get(0).toString();
        }
    
        public static Object isIRI(List<Object> args)
        {
            if (args.size() != 1) return null;
        	
        	return args.get(0).toString();
        }
    
        public static Object isURI(List<Object> args)
        {
            if (args.size() != 1) return null;
        	
        	return args.get(0).toString();
        }
    
        public static Object isBLANK(List<Object> args)
        {
            if (args.size() != 1) return null;
        	
        	return args.get(0).toString();
        }
    
        public static Object isLITERAL(List<Object> args)
        {
            if (args.size() != 1) return null;
        	
        	return args.get(0).toString();
        }
    
        public static Object REGEX(List<Object> args)
        {   
            if (args.size() < 2 || args.size() > 3) return null;
        	
        	return args.get(0).toString();
        }
        
    }
    
    public void setVars(Hashtable<String, Object> vs)
    {
        vars = vs;
    }
    
    private Object compare(Object a, Object b)
    {
        Object diff = null;
        //System.out.println("----------------");
        //System.out.println(a.toString() + " : " + a.getClass().getCanonicalName());
        //System.out.println(b.toString() + " : " + b.getClass().getCanonicalName());
        if (a instanceof Integer)
        {
            Integer aval = (Integer)a;
            if (b instanceof String)
            {
                String bval = (String)b;
                try 
                {
                    diff = aval - Double.parseDouble(bval);
                    diff = aval - Integer.parseInt(bval);
                }
                catch (Exception e1)
                {
                }
            }
            else if (b instanceof Integer)
            {
                diff = aval - (Integer)b;
            }
            else if (b instanceof Double)
            {
                diff = aval - (Double)b;
            }
        }
        else if (a instanceof Double)
        {
            Double aval = (Double)a;
            if (b instanceof String)
            {
                String bval = (String)b;
                try 
                {
                    diff = aval - Double.parseDouble(bval);
                    diff = aval - Integer.parseInt(bval);
                }
                catch (Exception e1)
                {
                }
            }
            else if (b instanceof Double || b instanceof Integer)
            {
                diff = aval - (Double)b;
            }
            else if (b instanceof Integer)
            {
                diff = aval - (Integer)b;
            }
        }
        else if (a instanceof String)
        {
            String aval = (String)a;
            if (b instanceof Double)
            {
                Double bval = (Double)b;
                try 
                {
                    diff = Double.parseDouble(aval) - bval;
                    diff = Integer.parseInt(aval) - bval;
                }
                catch (Exception e)
                {
                }
            }
            else if (b instanceof Integer)
            {
                Integer bval = (Integer)b;
                try 
                {
                    diff = Double.parseDouble(aval) - bval;
                    diff = Integer.parseInt(aval) - bval;
                }
                catch (Exception e)
                {
                }
            }
            else if (b instanceof Boolean)
            {
                Boolean bval = (Boolean)b;
                if (a.equals("true") && bval == true)
                    diff = 0;
                else if (a.equals("false") && bval == false)
                    diff = 0;
            }
            else if (b instanceof String)
                diff = aval.compareTo((String)b);
        }
        else if (a instanceof Boolean)
        {
            Boolean aval = (Boolean)a;
            if (b instanceof String)
            {
                if (b.equals("true") && aval == true)
                    diff = 0;
                else if (b.equals("false") && aval == false)
                    diff = 0;
            }
            else if (b instanceof Boolean)
            {
                if (aval == (Boolean)b)
                    diff = 0;
            }
        }
        //if (diff == null) System.out.println("null");
        //else System.out.println(diff.toString());
        return diff;
    }
    
    public Object exec(CommonTree root)
    {
        Object res = new Object();
        String text = root.getText();
        
        // NODE
        if (root.getChildCount() == 0)
        {   
            res = text;
        }
        // UNO
        else if (root.getChildCount() == 1)
        {
            String nval = ((CommonTree)root.getChild(0)).getText();
            // VAR 
            if (text.equals("VAR_RT"))
            {
                if (vars.containsKey(nval))
                    res = vars.get(nval);
                else
                    res = null;
                // todo: exception 
            }
            // STRING
            else if (text.equals("STRING_RT"))
            {
                // литерал преобразуем в двойные кавычки
                res = nval.substring(1, nval.length()-1);
            }
            // BOOL
            else if (text.equals("BOOL_RT"))
            {
                res = Boolean.parseBoolean(nval);
            }
            // INTEGER
            else if (text.equals("INTEGER_RT"))
            {
                res = Integer.parseInt(nval);
            }
            // DECIMAL
            else if (text.equals("FLOAT_RT"))
            {
                res = Double.parseDouble(nval);
            }
            // DOUBLE
            else if (text.equals("DOUBLE_RT"))
            {
                res = Double.parseDouble(nval);
            }
            // CALL
            else if (text.equals("CALL_RT"))
            {
                CommonTree node = (CommonTree)root.getChild(0);
                List<Object> args = new ArrayList();
                for (int i = 0; i < node.getChildCount(); i++)
                    args.add(exec((CommonTree)node.getChild(i)));
                res = BuildInCall.exec(nval, args);
            }
        }
        // BIN
        else if (root.getChildCount() == 2)
        {
            Object lval = exec((CommonTree)root.getChild(0));
            Object rval = exec((CommonTree)root.getChild(1));
            // =
            if (text.equals("="))
            {
                if (lval == null || rval == null)
                    res = false;
                else
                {
                    Object r = compare(lval, rval);
                    if (r == null) res = false;
                    else res = ((Integer)r) == 0;
                }
            }
            // !=
            else if (text.equals("!="))
            {
                if (lval == null || rval == null)
                    res = false;
                else
                {
                    Object r = compare(lval, rval);
                    if (r == null) res = false;
                    else res = ((Integer)r) != 0;
                }
            }
            // >
            else if (text.equals(">"))
            {
                if (lval == null || rval == null)
                    res = false;
                else
                {
                    Object r = compare(lval, rval);
                    if (r == null) res = false;
                    else res = ((Integer)r) > 0;
                }
            }
            // >=
            else if (text.equals(">="))
            {
                if (lval == null || rval == null)
                    res = false;
                else
                {
                    Object r = compare(lval, rval);
                    if (r == null) res = false;
                    else res = ((Integer)r) >= 0;
                }
            }
            // <
            else if (text.equals("<"))
            {
                if (lval == null || rval == null)
                    res = false;
                else
                {
                    Object r = compare(lval, rval);
                    if (r == null) res = false;
                    else res = ((Integer)r) < 0;
                }
            }
            // <=
            else if (text.equals("<="))
            {
                if (lval == null || rval == null)
                    res = false;
                else
                {
                    Object r = compare(lval, rval);
                    if (r == null) res = false;
                    else res = ((Integer)r) <= 0;
                }
            }
        }
        return res;
    }
}