import java.util.*;
import com.hp.hpl.jena.rdf.model.*;
import org.apache.jena.riot.*;

public class ConstructQuery extends SparqlQuery {
    private List<WhereTriplet> triplets = new ArrayList<>();
    
    public ConstructQuery()
    {
        super();
    }
    
    public void setTriplets(List<WhereTriplet> t)
    {
        triplets = t;
    }
    
    protected void execute(List<Hashtable<String, Object>> results, Model model)
    {   
        Model mout = ModelFactory.createDefaultModel();
        for (Hashtable<String, Object> res: results) 
        {
            for (WhereTriplet t : triplets)
            {
                String v = t.subject.getValue();
                if (t.subject.type == RDFNode.Type.VAR)
                {
                    if (res.get(v) == null)
                        continue;
                    else
                        v = res.get(v).toString();
                }
                Resource resource = mout.createResource(v);
                
                v = t.predicate.getValue();
                if (t.predicate.type == RDFNode.Type.VAR)
                {
                    if (res.get(v) == null)
                        continue;
                    else
                        v = res.get(v).toString();
                }
                Property property = ResourceFactory.createProperty(v);
                
                v = t.object.getValue();
                if (t.object.type == RDFNode.Type.VAR)
                {
                    if (res.get(v) == null)
                        continue;
                    else
                    {
                        RDFNode node = (RDFNode)res.get(v);
                        if (node.type == RDFNode.Type.LITERAL)
                        {
                            RDFLiteral lit = new RDFLiteral(node.getValue());
                            if (!lit.getLanguage().equals(""))
                                resource.addProperty(property, mout.createLiteral(lit.getText(), lit.getLanguage()));
                            else
                                resource.addProperty(property, lit.getText());
                            
                        }
                        else
                        {
                            resource.addProperty(property, node.getValue());
                        }
                    }
                }
                else
                {
                    resource.addProperty(property, v);
                }
            }
        }
        RDFDataMgr.write(System.out, mout, RDFFormat.RDFXML);
    }
    
    public void info()
    {
        super.info();
        if (triplets.isEmpty()) 
        {
            System.out.println("template: [NONE]");
        }
        else 
        {
            System.out.println("template:");
            for (WhereTriplet t : triplets) 
            {
                System.out.println("\t" + t);
            }
        }
    }
}