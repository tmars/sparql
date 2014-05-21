class RDFLiteral
{
    private String text = "";
    //private String datatype = "http://www.w3.org/2001/XMLSchema#string";
    private String datatype = "";
    private String language = "";
    
    public RDFLiteral(String t, String d, String l)
    {
        if (t != null) setText(t);
        if (d != null) datatype = d;
        if (l != null) language = l;
    }
    
    public RDFLiteral(String t)
    {
        if (t == null)
        {
            setText("");
        }
        else
        {
            int sp = t.lastIndexOf("\"");
            if (t.charAt(0) == '"' && sp > 0)
            {
                setText(t.substring(1, sp));
                String tail = t.substring(sp+1, t.length());
                if (tail.length() > 2)
                {
                    if (tail.substring(0, 1).equals("@"))
                        language = tail.substring(1, tail.length());
                
                    else if (tail.substring(0, 2).equals("^^"))
                        datatype = tail.substring(2, tail.length());
                }
            }
            else
            {
                if (t.contains("@"))
                {
                    sp = t.lastIndexOf("@");
                    setText(t.substring(0, sp));
                    language = t.substring(sp+1, t.length());
                }
                else if (t.contains("^^"))
                {
                    sp = t.lastIndexOf("^^");
                    setText(t.substring(0, sp));
                    datatype = t.substring(sp+2, t.length());
                }
                else
                {
                    setText(t);
                }    
            }
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
            BuiltInCall._LANGMATCHES(language, r.language) &&
            (
                datatype.equals("") ||
                r.datatype.equals("") ||
                datatype.equals(r.datatype)
            ) 
        );
    }
    
    public int compareTo(RDFLiteral r)
    {
        int res = this.getText().compareTo(r.getText());
        if (res == 0 && !this.language.equals("") && !r.language.equals(""))
        {
            res += this.language.compareTo(r.language);
        }
        if (res == 0 && !this.datatype.equals("") && !r.datatype.equals(""))
        {
            res += this.datatype.compareTo(r.datatype);
        }
        return res;
    }
}