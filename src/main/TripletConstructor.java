import java.util.*;

class TripletConstructor
{
    RDFNode curSubject;
    RDFNode curPredicate;
    boolean curIsOptional = false;
    
    List<WhereTriplet> triplets = new ArrayList<>();
    
    public void start(String v, String t)
    {
		curSubject = WhereTriplet.getVarOrTerm(v, t);
    }
    
    public void addPredicate(String v, String t)
    {
		curPredicate = WhereTriplet.getVarOrIRI(v, t);
    }
    
    public void addObject(String v, String t)
    {
    	RDFNode object = WhereTriplet.getVarOrTerm(v, t);
        WhereTriplet r = new WhereTriplet(curSubject, curPredicate, object, curIsOptional);
	    triplets.add(r);
    }
    
    public void setOptional(boolean f)
    {
        curIsOptional = f;
    }
    
    public List<WhereTriplet> pop()
    {
        List<WhereTriplet> res = triplets;
        triplets = new ArrayList<>();
        return res;
    }
}