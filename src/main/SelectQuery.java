import java.util.*;
import dnl.utils.text.table.TextTable;

public class SelectQuery extends SparqlQuery {
    private boolean isDistinct = false;
    private boolean allFields = false;
    List<String> fields = new ArrayList<>();
    
    public SelectQuery(List<String> bs, Hashtable<String, String> ps)
    {
        super(bs, ps);
    }
    
    public void setIsDistinct(boolean f)
    {
        isDistinct = f;
    }
    
    public void setAllFields(boolean f)
    {
        allFields = f;
    }
    
    public void addField(String name)
    {
        fields.add(name);
    }
    
    protected void execute(List<Hashtable<String, Object>> results)
    {   
        if (allFields) 
        {
            for (Hashtable<String, Object> res: results) 
            {
                for (String v : res.keySet()) 
                    fields.add(v);
                break;
            }
        }
        String[] columnNames = fields.toArray(new String[fields.size()]);
        String[][] data = new String[results.size()][fields.size()];
        
        int i = 0;
        for (Hashtable<String, Object> res: results) 
        {
            int j = 0;
            for (String v : fields) 
            {
                data[i][j] = res.get(v).toString();
                j++;
            }
            i++;
        }
                                                                       
        TextTable tt = new TextTable(columnNames, data);         
        // this adds the numbering on the left      
         tt.setAddRowNumbering(true);      
        // sort by the first column                              
        //tt.setSort(0);                                                 
        tt.printTable();                                 
    }
    
    public void info()
    {
        super.info();
        System.out.println("distinct: "+isDistinct);
        if (allFields) 
        {
            System.out.println("fields: [ALL]");
        }
        else 
        {
            System.out.println("fields:");
            for (String n : fields) 
                System.out.println("\t" + n);
        }
    }
}