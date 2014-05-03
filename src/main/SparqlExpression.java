import java.util.*;
import org.antlr.runtime.*;
import org.antlr.runtime.tree.*;
import org.antlr.stringtemplate.*;
import java.lang.reflect.Method;
class SparqlExpression
{
    Hashtable<String, Object> vars = null;
    
    public void setVars(Hashtable<String, Object> vs)
    {
        vars = vs;
    }
    
    private Integer compare(Object a, Object b)
    {
        Integer diff = null;
        //System.out.println("----------------");
        //System.out.println(a.toString() + " : " + a.getClass().getCanonicalName());
        //System.out.println(b.toString() + " : " + b.getClass().getCanonicalName());
        if (a instanceof Integer || a instanceof Double)
        {
            Number aval = (Number)a;
            Number bval = null;
            
            if (b instanceof String)
            {
                try 
                {
                    bval = Double.parseDouble((String)b);
                }
                catch (Exception e1)
                {
                }
            }
            else if (b instanceof Integer || b instanceof Double)
            {
                bval = (Number)b;
            }
            
            if (bval != null)
            {
                if (aval.doubleValue() > bval.doubleValue()) diff = 1;
                else if (aval.doubleValue() < bval.doubleValue()) diff = -1;
                else diff = 0;
            }
        }
        else if (a instanceof String)
        {
            if (b instanceof Double || b instanceof Integer)
            {
                Number bval = (Number)b;
                try 
                {
                    Number aval = Double.parseDouble((String)a);
                    if (aval.doubleValue() > bval.doubleValue()) diff = 1;
                    else if (aval.doubleValue() < bval.doubleValue()) diff = -1;
                    else diff = 0;
                }
                catch (Exception e)
                {
                }
            }
            else if (b instanceof Boolean)
            {
                Boolean bval = (Boolean)b;
                try 
                {
                    if (Boolean.parseBoolean((String)a) == bval)
                        diff = 0;
                }
                catch (Exception e)
                {
                }
            }
            else if (b instanceof String)
                diff = ((String)a).compareTo((String)b);
        }
        else if (a instanceof Boolean)
        {
            Boolean aval = (Boolean)a;
            if (b instanceof String)
            {
                try 
                {
                    if (Boolean.parseBoolean((String)b) == aval)
                        diff = 0;
                }
                catch (Exception e)
                {
                }
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
    
    private Double toNumber(Object a)
    {
        Double res = null;
        if (a instanceof String)
        {
            String aval = (String)a;
            try 
            {
                res = Double.parseDouble(aval);
            }
            catch (Exception e1)
            {
                try
                {
                    if (Boolean.parseBoolean(aval) == true)
                        a = 1.0;
                }
                catch (Exception e2)
                {
                }
            }
        }
        else if (a instanceof Integer)
        {
            res = ((Integer)a).doubleValue();
        }
        
        return res;
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
            String[] rootOperators = {"VAR_RT", "STRING_RT", "BOOL_RT", 
                "INTEGER_RT", "FLOAT_RT", "DOUBLE_RT", "CALL_RT"};
            String[] unaryOperators = {"!", "+", "-"};
            
            if (Arrays.asList(rootOperators).contains(text))
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
                    try 
                    {
                        res = BuildInCall.exec(nval, args);
                    }
                    catch (Exception e)
                    {
                        System.out.println("Невозможно выполнить функцию: " + nval);
                        System.out.println(e.getMessage());
                    }
                }
            }
            if (Arrays.asList(unaryOperators).contains(text))
            {
                Object nval = exec((CommonTree)root.getChild(0));
                // !
                if (text.equals("!") && nval instanceof Boolean)
                {
                    res = !((Boolean)nval);
                }
                // +
                else if (text.equals("+") && nval instanceof Number)
                {
                    if (((Number)nval).doubleValue() > 0)
                        res = (Number)nval;
                    else
                        res = 0.0 - ((Number)nval).doubleValue();
                }
                // -
                else if (text.equals("-") && nval instanceof Number)
                {
                    res = 0.0 - ((Number)nval).doubleValue();
                }
            }
        }
        // BIN
        else if (root.getChildCount() == 2)
        {
            String[] compareOperators = {"=", "!=", ">", ">=", "<", "<="};
            String[] mathOperators = {"+", "-", "*", "/"};
            if (Arrays.asList(compareOperators).contains(text))
            {
                Object lval = exec((CommonTree)root.getChild(0));
                Object rval = exec((CommonTree)root.getChild(1));
                if (lval == null || rval == null)
                {
                    res = false;
                }
                else 
                {
                    Integer r = compare(lval, rval);
                    if (r == null) 
                        res = false;
                    
                    // =
                    else if (text.equals("="))
                        res = (r == 0);
                    
                    // !=
                    else if (text.equals("!="))
                        res = (r != 0);
                    
                    // >
                    else if (text.equals(">"))
                        res = (r > 0);
                    
                    // >=
                    else if (text.equals(">="))
                        res = (r >= 0);
                    
                    // <
                    else if (text.equals("<"))
                        res = (r < 0);
                    
                    // <=
                    else if (text.equals("<="))
                        res = (r <= 0);
                }
            }
            else if (Arrays.asList(mathOperators).contains(text))
            {
                Double lval = toNumber(exec((CommonTree)root.getChild(0)));
                Double rval = toNumber(exec((CommonTree)root.getChild(1)));
                if (lval == null) lval = 0.0;
                if (rval == null) rval = 0.0;
                    
                // +
                if (text.equals("+"))
                    res = lval + rval;
                
                // -
                else if (text.equals("-"))
                    res = lval - rval;
                    
                // *
                else if (text.equals("*"))
                    res = lval * rval;
                    
                // /
                else if (text.equals("/") && rval != 0.0)
                    res = lval / rval;
            }
        }
        return res;
    }
}