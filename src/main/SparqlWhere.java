import java.util.List;
import java.util.ArrayList;

public class SparqlWhere 
{
    
    private class Triplet
    {
        private String subject;
        private String subjectType;
        private String predicate; 
        private String predicateType; 
        private String object;
        private String objectType;
        
        public Triplet(String s, String st, String p, String pt, String o, String ot)
        {
            subject = s;
            subjectType = st;
            predicate = p;
            predicateType = pt;
            object = o;
            objectType = ot;
        }
    }
    
    private class Filter
    {
        private String type;
        private String value;
        
        public Filter(String v, String t)
        {
            value = v;
            type = t;
        }
    }
    
    String curSubject = "";
    String curSubjectType = "";
    String curPredicate = "";
    String curPredicateType = "";
    List<Triplet> triplets = new ArrayList<>();
    List<Filter> filters = new ArrayList<>();
    
    public void addTriplet(String s, String st, String p, String pt, String o, String ot)
    {
        Triplet t = new Triplet(s, st, p, pt, o, ot);
	    triplets.add(t);
    }
    
    public void addTwice(String p, String pt, String o, String ot)
    {
        Triplet t = new Triplet("NO", "NO", p, pt, o, ot);
	    triplets.add(t);
    }
    
    public void start(String v, String t)
    {
        curSubject = v;
        curSubjectType = t;
    }
    
    public void addPredicate(String v, String t)
    {
        curPredicate = v;
        curPredicateType = t;
    }
    
    public void addObject(String v, String t)
    {
        Triplet r = new Triplet(
            curSubject, curSubjectType, 
            curPredicate, curPredicateType, 
            v, t
        );
	    triplets.add(r);
    }
    
    public void finish()
    {
    }
    
    public void addFilter(String v, String t)
    {
        Filter f = new Filter(v, t);
	    filters.add(f);
    }
    
    public void info()
    {
        System.out.println("where:");
        if (triplets.isEmpty()) 
        {
            System.out.println("\ttriples: [NONE]");
        }
        else 
        {
            System.out.println("\ttriples:");
            for (Triplet t : triplets) 
                System.out.println("\t\t" +
                    t.subject + " (" + t.subjectType+ ") " +
                    t.predicate + " (" + t.predicateType+ ") " +
                    t.object + " (" + t.objectType+ ")"
                );
        }
        if (filters.isEmpty()) 
        {
            System.out.println("\tfilters: [NONE]");
        }
        else 
        {
            System.out.println("\tfilters:");
            for (Filter f : filters) 
                System.out.println("\t\t" + f.value + " (" + f.type+ ") ");
        }
    }
}