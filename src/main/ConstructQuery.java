import java.util.List;
import java.util.Hashtable;

public class ConstructQuery extends SparqlQuery {
    public ConstructQuery(List<String> bs, Hashtable<String, String> ps)
    {
        super(bs, ps);
    }
    
    protected void execute(List<Hashtable<String, String>> results)
    {   
        
    }
}