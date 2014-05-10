import java.util.*;

public class DescribeQuery extends SparqlQuery {
    
    List<String> vars = new ArrayList<>();
    List<String> iriRefs = new ArrayList<>();
    private boolean allFields = false;
    
    public DescribeQuery()
    {
        super();
    }
    
    public void setAllFields(boolean f)
    {
        allFields = f;
    }
    
    public void addField(String name, String type)
    {
        if (type.equals("var"))
            vars.add(name);
        
        else if (type.equals("iri"))
            iriRefs.add(name);
            
        else if (type.equals("short_iri"))
            iriRefs.add(Config.getInstance().getRealIRI(name));
            
        //TODO exception
    }
    
    protected void execute(List<Hashtable<String, Object>> results)
    {   
        
    }
    
    public void info()
    {
        super.info();
        if (allFields) 
        {
            System.out.println("fields: [ALL]");
        }
        else 
        {
            System.out.println("fields");
            if (vars.isEmpty())
            {
                System.out.println("\tvars: [NONE]");
            }
            else
            {
                System.out.println("\tvars:");
                for (String n : vars) 
                    System.out.println("\t\t" + n);
            }
            if (iriRefs.isEmpty())
            {
                System.out.println("\tiri refs: [NONE]");
            }
            else
            {
                System.out.println("\tiri refs:");
                for (String n : iriRefs) 
                    System.out.println("\t\t" + n);
            }    
        }
    }
}