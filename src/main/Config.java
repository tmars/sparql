import java.util.*;

class Config
{
    List<String> bases = new ArrayList<>();
    Hashtable<String, String> prefixes = new Hashtable<String, String>();
    private Boolean debug = false;
    
    private Config() {}
    
    private static class SingletonHolder
    {
        private static final Config INSTANCE = new Config();
    }
    
    public static Config getInstance()
    {
        return SingletonHolder.INSTANCE;
    }
    
    public String getRealIRI(String iri)
    {
        String[] parts = iri.split(":");
        if (parts.length == 2 && prefixes.containsKey(parts[0] + ":"))
            return prefixes.get(parts[0] + ":") + parts[1];
        return iri;
    }
    
    public Boolean isDebug()
    {
        return debug;
    }
    
    public void setDebug(Boolean d)
    {
        debug = d;
    }
    
    public void log(String s)
    {
        if (isDebug())
            System.out.println(s);
    }
}