import java.util.List;
import java.util.Hashtable;

public class DescribeQuery extends SparqlQuery {
    public DescribeQuery(List<String> bs, Hashtable<String, String> ps)
    {
        super(bs, ps);
    }
    
    protected void execute(List<Hashtable<String, String>> results)
    {   
        
    }
}