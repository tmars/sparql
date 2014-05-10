public class Triplet
{
    public String subject;
    public String subjectType;
    public String predicate;
    public String predicateType; 
    public String object; 
    public String objectType;
    
    public Triplet(String s, String st, String p, String pt, String o, String ot)
    {
        subject = s;
        subjectType = st;
        predicate = p;
        predicateType = pt;
        object = o;
        objectType = ot;
    }
    
    public Triplet(String s, String p, String o)
    {
        subject = s;
        subjectType = "";
        predicate = p;
        predicateType = "";
        object = o;
        objectType = "";
    }
    
    public String toString()
    {
        return subject + " (" + subjectType + ") " +
            predicate + " (" + predicateType + ") " +
            object + " (" + objectType + ") " ;
    }
}