import java.util.List;
import java.util.Hashtable;

public class SparqlQuery {
    String dataset = "";
    int limit = -1;
    int offset = -1;
    List<String> bases = null;
    List<SparqlWhere> wheres = null;
    Hashtable<String, String> prefixes = null;
    
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
                    System.out.println("\t" + n);
        }
        if (prefixes.isEmpty()) 
        {
            System.out.println("prefixes: [NONE]");
        } 
        else
        {
            System.out.println("prefixes:");
            for (String pr : prefixes.keySet()) {
                System.out.println("\t" + pr + " -> " + prefixes.get(pr));
            }
        }
        if (wheres.isEmpty()) 
        {
            System.out.println("where: [NONE]");
        }
        else 
        {
            System.out.println("where: ");
            for (SparqlWhere w : wheres) 
                System.out.println(
                    w.getSubject() + "(" + w.getSubjectType()+ ") " +
                    w.getPredicate() + "(" + w.getPredicateType()+ ") " +
                    w.getObject() + "(" + w.getObjectType()+ ")"
                );
        }
        
        
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
    
    public void addWhere(SparqlWhere w)
    {
        wheres.add(w);
    }
}