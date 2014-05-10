class WhereTriplet extends RDFTriplet
{
    public boolean isOptional = false;
    
    public WhereTriplet(RDFNode s, RDFNode p, RDFNode o, boolean isOpt)
    {
        super(s, p, o);
        isOptional = isOpt;
    }
    
    public static RDFNode getVarOrTerm(String v, String t)
    {
        RDFNode.Type type;
        if (t.equals("var"))
        {
            type = RDFNode.Type.VAR;
        }
        else if (t.equals("iri"))
        {
            type = RDFNode.Type.RESOURCE;
        }
        else if (t.equals("short_iri"))
        {
            v = Config.getInstance().getRealIRI(v);
            type = RDFNode.Type.RESOURCE;
        }
        else if (t.equals("rdf_lit"))
        {
            RDFLiteral lit = new RDFLiteral(v);
            v = lit.toString();
            type = RDFNode.Type.LITERAL;
        }
        else 
        {
            type = RDFNode.Type.OTHER;
        }
        return new RDFNode(v, type);
    }
    
    public static RDFNode getVarOrIRI(String v, String t)
    {
        RDFNode.Type type;
        if (t.equals("var"))
        {
            type = RDFNode.Type.VAR;
        }
        else if (t.equals("short_iri"))
        {
            v = Config.getInstance().getRealIRI(v);
            type = RDFNode.Type.RESOURCE;
        }
        else if (t.equals("iri"))
        {
            type = RDFNode.Type.RESOURCE;
        }
        else 
        {
            type = RDFNode.Type.OTHER;
        }
        return new RDFNode(v, type);
    }
}