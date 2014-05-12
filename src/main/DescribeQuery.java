import java.util.*;
import com.hp.hpl.jena.rdf.model.*;
import org.apache.jena.riot.*;

public class DescribeQuery extends SparqlQuery
{    
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
            iriRefs.add(name.substring(1, name.length()-1));
        
        else if (type.equals("short_iri"))
            iriRefs.add(Config.getInstance().getRealIRI(name));
            
        //TODO exception
    }
    
    protected void execute(List<Hashtable<String, Object>> results, Model model)
    {
        List<String> fields = new ArrayList<>();
        if (allFields) 
        {
            for (Hashtable<String, Object> res: results) 
            {
                for (String v : res.keySet()) 
                    fields.add(v);
                break;
            }
        }
        else
        {
            fields = vars;
        }
        
        for (Hashtable<String, Object> res: results) 
        {
            for (String v : fields) 
            {
                Object o = res.get(v);
                if (o instanceof RDFNode && ((RDFNode)o).getType() == RDFNode.Type.RESOURCE)
                {
                    iriRefs.add(o.toString());   
                }
            }
        }
        
        Model mout = ModelFactory.createDefaultModel();
        for (String r : iriRefs)
        {
            buildModel(model.getResource(r), model, mout);
        }
        
        RDFDataMgr.write(System.out, mout, RDFFormat.RDFXML) ;
    }
    
    private Resource buildModel(Resource resource, Model in, Model out)
    {
        Resource instance = out.createResource(resource.getURI());
        StmtIterator iter = resource.listProperties();
        while (iter.hasNext()) 
        {
            Statement stmt = iter.nextStatement();
            Property prop = ResourceFactory.createProperty(stmt.getPredicate().toString());
            if (stmt.getObject() instanceof Resource)
                instance.addProperty(prop, buildModel((Resource)stmt.getObject(), in, out));
            else
                instance.addProperty(prop, stmt.getObject());
        }
        return instance;
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