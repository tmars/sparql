import java.util.ArrayList;
import java.util.List;
import java.util.Hashtable;
import java.util.Comparator;
import java.util.Collections;
import java.util.Enumeration;

class SparqlOrder
{
    public enum Sort { ASC, DESC }
    public Hashtable<String, Sort> vars = new Hashtable<String, Sort>();
    
    private class CustomComparator implements Comparator<Hashtable<String, String>> {
        public SparqlOrder order;
        
        public CustomComparator(SparqlOrder o)
        {
            order = o;
        }
        
        @Override
        public int compare(Hashtable<String, String> o1, Hashtable<String, String> o2) 
        {
            // fixme: порядок заполнения переменных не соответствует запросу
            int diff = 0;
            Enumeration en = order.vars.keys();
            while (diff == 0 && en.hasMoreElements())
            {
                String key = en.nextElement().toString();
                diff = o1.get(key).compareTo(o2.get(key));
                if (order.vars.get(key) == Sort.DESC)
                    diff = -diff;
            }
            return diff;
        }
    }
    
    public List<Hashtable<String, String>> sort(List<Hashtable<String, String>> results)
    {
        Collections.sort(results, new CustomComparator(this));
        return results;
    }
    
    public SparqlOrder()
    {
    }
    
    public void addVar(String var, Sort mode)
    {
        vars.put(var, mode);
    }
    
    public void info()
    {
        System.out.println("order:");
        if (vars.isEmpty())
        {
            System.out.println("\tvars: [NONE]");
        } 
        else
        {
            System.out.println("\tvars:");
            for (String v : vars.keySet())
                System.out.println("\t\t" + v + ": "+vars.get(v));
        }
    }
}