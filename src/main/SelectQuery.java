import java.util.ArrayList;
import java.util.List;
import java.util.Hashtable;

public class SelectQuery extends SparqlQuery {
    private boolean isDistinct = false;
    private boolean allFields = false;
    List<String> fields = new ArrayList<>();
    
    public SelectQuery(List<String> bs, Hashtable<String, String> ps)
    {
        super(bs, ps);
    }
    
    public void setIsDistinct(boolean f)
    {
        isDistinct = f;
    }
    
    public void setAllFields(boolean f)
    {
        allFields = f;
    }
    
    public void addField(String name)
    {
        fields.add(name);
    }
    
    public void info()
    {
        super.info();
        System.out.println("distinct: "+isDistinct);
        if (allFields) 
        {
            System.out.println("fields: [ALL]");
        }
        else 
        {
            System.out.println("fields:");
            for (String n : fields) 
                System.out.println("\t" + n);
        }
    }
}