import java.util.List;
import java.util.ArrayList;

public class SparqlWhere 
{
    public class WhereTriplet extends Triplet
    {
        public boolean isOptional = false;
        public WhereTriplet(String s, String st, String p, String pt, String o, String ot, boolean isOpt)
        {
            super(s, st, p, pt, o, ot);
            isOptional = isOpt;
        }
        
        public List<String> getVars()
        {
            List<String> res = new ArrayList<>();
            if (subjectType.equals("var"))
                res.add(subject);
            if (predicateType.equals("var"))
                res.add(predicate);
            if (objectType.equals("var"))
                res.add(object);
            return res;
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
    boolean isOptional = false;
    
    List<WhereTriplet> triplets = new ArrayList<>();
    List<Filter> filters = new ArrayList<>();
    
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
        WhereTriplet r = new WhereTriplet(
            curSubject, curSubjectType, 
            curPredicate, curPredicateType, 
            v, t,
            isOptional
        );
	    triplets.add(r);
    }
    
    public void setOptional(boolean f)
    {
        isOptional = f;
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
            for (WhereTriplet t : triplets) 
            {
                System.out.print("\t\t" +
                    t.subject + " (" + t.subjectType+ ") " +
                    t.predicate + " (" + t.predicateType+ ") " +
                    t.object + " (" + t.objectType+ ")"
                );
                if (t.isOptional)
                    System.out.print(" [OPTIONAL]");
                System.out.println();
            }
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