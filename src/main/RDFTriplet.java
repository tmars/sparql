import java.util.*;

public class RDFTriplet
{
    public RDFNode subject;
    public RDFNode predicate;
    public RDFNode object; 
    
    public RDFTriplet(RDFNode s, RDFNode p, RDFNode o)
    {
        subject = s;
        predicate = p;
        object = o;
    }
    
    public String toString()
    {
        return subject + " (" + subject.type + ") " +
            predicate + " (" + predicate.type + ") " +
            object + " (" + object.type + ") " ;
    }
    
    public List<String> getVars()
    {
        List<String> res = new ArrayList<>();
        if (subject.getType() == RDFNode.Type.VAR)
            res.add(subject.toString());
        if (subject.getType() == RDFNode.Type.VAR)
            res.add(predicate.toString());
        if (subject.getType() == RDFNode.Type.VAR)
            res.add(object.toString());
        return res;
    }
}