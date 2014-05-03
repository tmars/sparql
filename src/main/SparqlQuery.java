import java.util.*;
import com.hp.hpl.jena.rdf.model.*;

public abstract class SparqlQuery {
    String dataset = "";
    int limit = -1;
    int offset = -1;
    List<String> bases = null;
    Hashtable<String, String> prefixes = null;
    SparqlWhere where = new SparqlWhere();
    SparqlOrder order = new SparqlOrder();
    
    public SparqlQuery(List<String> bs, Hashtable<String, String> ps)
    {
        bases = bs;
        prefixes = ps;
    }
    
    public void info()
    {
        System.out.println("type: "+getClass().getName());
        System.out.println("dataset: "+dataset);
        System.out.println("limit: "+limit);
        System.out.println("offset: "+offset);
        if (bases.isEmpty()) 
        {
            System.out.println("bases: [NONE]");
        } 
        else
        {
            System.out.println("bases:");
            for (String n : bases) 
            {
                System.out.println("\t" + n);
            }
        }
        if (prefixes.isEmpty()) 
        {
            System.out.println("prefixes: [NONE]");
        } 
        else
        {
            System.out.println("prefixes:");
            for (String pr : prefixes.keySet()) 
            {
                System.out.println("\t" + pr + " -> " + prefixes.get(pr));
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
    
    protected abstract void execute(List<Hashtable<String, Object>> results);
    
    protected void getResult(String filename)
    {
        Model model = ModelFactory.createDefaultModel();
        model.read(filename);
       
        List<Hashtable<String, Object>> results = where.fetch(model, this);
        results = order.sort(results);
        results = makeOffset(results);
        results = makeLimit(results);
        
        execute(results);
    }
    
    private List<Hashtable<String, Object>> makeOffset(List<Hashtable<String, Object>> results)
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
    
    private List<Hashtable<String, Object>> makeLimit(List<Hashtable<String, Object>> results)
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
    
    public String getVarTermIRI(String value, String type)
    {
        String r = "";
        if (type.equals("short_iri"))
            r = getRealIRI(value);
        else // var | rdf_lit | iri | num_lit | bool_lit | blank
            r = value;
        return r;
    }
    
    private String getRealIRI(String iri)
    {
        String[] parts = iri.split(":");
        if (parts.length == 2 && prefixes.containsKey(parts[0] + ":"))
            return prefixes.get(parts[0] + ":") + parts[1];
        return "";
    }
}