public class SparqlWhere 
{
    private String subject;
    private String subjectType;
    private String predicate; 
    private String predicateType; 
    private String object;
    private String objectType;
     
    public void setSubject(String t, String s)
    {
        subjectType = t;
        subject = s;
    }
    
    public void setPredicate(String t, String s)
    {
        predicateType = t;
        predicate = s;
    }
    
    public void setObject(String t, String s)
    {
        objectType = t;
        object = s;
    }
    
    public String getSubject()
    {
        return subject;
    }
    
    public String getPredicate()
    {
        return predicate;
    }
    
    public String getObject()
    {
        return object;
    }
    
    public String getSubjectType()
    {
        return subjectType;
    }
    
    public String getPredicateType()
    {
        return predicateType;
    }
    
    public String getObjectType()
    {
        return objectType;
    }
}