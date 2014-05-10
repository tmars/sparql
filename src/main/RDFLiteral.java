class RDFLiteral
{
    private String text = "";
    //private String datatype = "http://www.w3.org/2001/XMLSchema#string";
    private String datatype = "";
    private String language = "";
    
    public RDFLiteral(String t, String d, String l)
    {
        if (t != null) text = t;
        if (d != null) datatype = d;
        if (l != null) language = l;
    }
    
    public RDFLiteral(String t)
    {
        if (t == null)
        {
            text = "";
        }
        else if (t.contains("@"))
        {
            int sp = t.lastIndexOf("@");
            setText(t.substring(0, sp));
            language = t.substring(sp+1, t.length());
        }
        else if (t.contains("^^"))
        {
            int sp = t.lastIndexOf("^^");
            setText(t.substring(0, sp));
            datatype = t.substring(sp+2, t.length());
        }
        else
        {
            setText(t);
        }
    }
    
    public String getLanguage()
    {
        return language;
    }
    
    public String getDatatype()
    {
        return datatype;
    }
    
    public String getText()
    {
        return text;
    }
    
    private void setText(String t)
    {
        if (t.charAt(0) == '"' && t.charAt(t.length()-1) == '"')
            text = t.substring(1, t.length()-1);
        else
            text = t;
    }
    
    public String toString()
    {
        return "\"" + text + "\"" + 
            (language.equals("") ? (datatype.equals("") ? "" : "^^" + datatype) : "@" + language);
    }
    
    public Boolean equals(RDFLiteral r)
    {
        return (
            text.equals(r.text) &&
            BuildInCall._LANGMATCHES(language, r.language) &&
            (
                datatype.equals("") ||
                r.datatype.equals("") ||
                datatype.equals(r.datatype)
            ) 
        );
    }
}