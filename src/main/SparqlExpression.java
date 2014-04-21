import java.util.*;
import org.antlr.runtime.*;
import org.antlr.runtime.tree.*;
import org.antlr.stringtemplate.*;

class SparqlExpression
{
    Hashtable<String, String> vars = null;
    
    public void setVars(Hashtable<String, String> vs)
    {
        vars = vs;
    }
    
    public Object exec(CommonTree tree)
    {
        Object res = new Object();
        if (tree.getChildCount() == 0)
        {
            if (vars.containsKey(tree.getText()))
                res = vars.get(tree.getText());
            else
                res = tree.getText();
        }
        else if (tree.getText().equals("="))
        {
            if (exec((CommonTree)tree.getChild(0)).equals(
                exec((CommonTree)tree.getChild(1))
            ))
                res = true;
            else
                res = false;
        }
        else if (tree.getText().equals("!="))
        {
            if (exec((CommonTree)tree.getChild(0)).equals(
                exec((CommonTree)tree.getChild(1))
            ))
                res = false;
            else
                res = true;
        }
        return res;
    }
}