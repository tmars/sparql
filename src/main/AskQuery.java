import java.util.List;
import java.util.Hashtable;

public class AskQuery extends SparqlQuery {
    public AskQuery(List<String> bs, Hashtable<String, String> ps)
    {
        super(bs, ps);
    }
    
    protected void execute(List<Hashtable<String, String>> results)
    {   
        
    }
}