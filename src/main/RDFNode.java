class RDFNode
{
    public enum Type
    {
        VAR,
        LITERAL,
        RESOURCE,
        OTHER;
    }
    
    String value;
    Type type;
    
    public RDFNode(String v, Type t)
    {
        value = v;
        type = t;
    }
    
    public String getValue()
    {
        return value;
    }
    
    public String toString()
    {
        return getValue();
    }
    
    public Type getType()
    {
        return type;
    }
    
    public Boolean equals(RDFNode r)
    {
        
        Boolean res = false;
        if (type == r.type && type == Type.LITERAL)
        {
            RDFLiteral a = new RDFLiteral(value);
            RDFLiteral b = new RDFLiteral(r.value);
        
            res = a.equals(b);
            //Config.getInstance().log("RDFLiteral.equals: " + a + " - " + b + " = " + res);
        }
        else
        {
            res = value.equals(r.value);
            //Config.getInstance().log("RDFNode.equals: " + value + " - " + r.value + " = " + res);
        }
        return res;
    }
}