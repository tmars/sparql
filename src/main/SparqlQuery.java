import java.util.*;
import com.hp.hpl.jena.rdf.model.*;

public abstract class SparqlQuery {
    String dataset = "";
    int limit = -1;
    int offset = -1;
    SparqlWhere where = null;
    SparqlOrder order = null;
    
    public SparqlQuery()
    {
        where = new SparqlWhere();
        order = new SparqlOrder();
    }
    
    public void info()
    {
        System.out.println("type: "+getClass().getName());
        System.out.println("dataset: "+dataset);
        System.out.println("limit: "+limit);
        System.out.println("offset: "+offset);
        if (Config.getInstance().bases.isEmpty()) 
        {
            System.out.println("bases: [NONE]");
        } 
        else
        {
            System.out.println("bases:");
            for (String n : Config.getInstance().bases) 
            {
                System.out.println("\t" + n);
            }
        }
        if (Config.getInstance().prefixes.isEmpty()) 
        {
            System.out.println("prefixes: [NONE]");
        } 
        else
        {
            System.out.println("prefixes:");
            for (String pr : Config.getInstance().prefixes.keySet()) 
            {
                System.out.println("\t" + pr + " -> " + Config.getInstance().prefixes.get(pr));
            }
        }
        
        where.info();
        order.info();
    }
    
    public void setDataset(String d)
    {
        dataset = d;
    }
    
    public void setLimit(int v)
    {
        limit = v;
    }
    
    public void setOffset(int v)
    {
        offset = v;
    }
    
    public SparqlWhere getWhere()
    {
        return where;
    }
    
    public SparqlOrder getOrder()
    {
        return order;
    }
    
    protected abstract void execute(List<Hashtable<String, Object>> results, Model model);
    
    protected List<Hashtable<String, Object>> postFetch(List<Hashtable<String, Object>> results)
    {
        return results;    
    }
    
    public void getResult(String filename)
    {
        if (Config.getInstance().isDebug() == true)
        {
            info();
        }
        
        Model model = ModelFactory.createDefaultModel();
        model.read(filename);
       
        List<Hashtable<String, Object>> results = where.fetch(model);
        results = postFetch(results);
        results = order.sort(results);
        results = execOffset(results);
        results = execLimit(results);
        
        execute(results, model);
    }
    
    private List<Hashtable<String, Object>> execOffset(List<Hashtable<String, Object>> results)
    {
        if (offset > 0)
        {
            int i = 0;
            while (i < offset && results.size() > 0) 
            {
                results.remove(0);
                i++;
            }
        }
        return results;
    }
    
    private List<Hashtable<String, Object>> execLimit(List<Hashtable<String, Object>> results)
    {
        if (limit >= 0)
        {
            while (results.size() > limit) 
            {
                results.remove(results.size()-1);
            }
        }
        return results;
    }
}