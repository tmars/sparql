import java.util.*;

class SparqlOrder
{
    public enum Sort { ASC, DESC }
    public Hashtable<String, Sort> vars = new Hashtable<String, Sort>();
    
    private class CustomComparator implements Comparator<Hashtable<String, Object>> {
        public SparqlOrder order;
        
        public CustomComparator(SparqlOrder o)
        {
            order = o;
        }
        
        @Override
        public int compare(Hashtable<String, Object> row1, Hashtable<String, Object> row2) 
        {
            int diff = 0;
            List<Object> keys = Arrays.asList(order.vars.keySet().toArray());
            Collections.reverse(keys);
            for (Object key : keys)
            {
                Object o1 = row1.get(key);
                Object o2 = row2.get(key);
                if (o1 == null || o2 == null)
                    continue;
                diff = o1.toString().compareTo(o2.toString());
                if (order.vars.get(key) == Sort.DESC)
                    diff = -diff;
                if (diff != 0)
                    break;
            }
            return diff;
        }
    }
    
    public List<Hashtable<String, Object>> sort(List<Hashtable<String, Object>> results)
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
            List<Object> keys = Arrays.asList(vars.keySet().toArray());
            Collections.reverse(keys);
            for (Object v : keys)
                System.out.println("\t\t" + v + ": "+vars.get(v));
        }
    }
}