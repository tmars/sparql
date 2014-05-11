import java.util.*;
import com.hp.hpl.jena.rdf.model.*;
import dnl.utils.text.table.TextTable;

public class SelectQuery extends SparqlQuery {
    private boolean isDistinct = false;
    private boolean allFields = false;
    List<String> fields = new ArrayList<>();
    
    public SelectQuery()
    {
        super();
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
    
    private List<String> getColumns(Set<String> allColumns)
    {
        List<String> columns = new ArrayList<>();
        if (allFields) 
        {
            if (allColumns != null)
            {
                for (String v : allColumns)
                {
                    columns.add(v);
                }
            }
        }
        else
        {
            columns = fields;
        }
        return columns;
    }
    
    protected List<Hashtable<String, Object>> postFetch(List<Hashtable<String, Object>> results)
    {
        List<String> columns = getColumns(results.get(0) == null ? null : results.get(0).keySet());
        Config.getInstance().log("post start");
        Set<Integer> allHashes = new HashSet();
        if (isDistinct)
        {
            Iterator<Hashtable<String, Object>> iter = results.iterator();
            while (iter.hasNext()) {
                Hashtable<String, Object> row = iter.next(); 
                Set<Integer> rowHashes = new HashSet();
                for (String k : columns)
                {
                    if (row.get(k) != null)
                        rowHashes.add(row.get(k).toString().hashCode());
                }
                
                Integer rowHash = rowHashes.hashCode();
                if (allHashes.contains(rowHash))
                    iter.remove();
                else
                    allHashes.add(rowHash);
            }
        }
        Config.getInstance().log("post end");
        return results;
    }
    
    protected void execute(List<Hashtable<String, Object>> results, Model model)
    {   
        List<String> columns = getColumns(results.get(0) == null ? null : results.get(0).keySet());
        
        String[] columnNames = columns.toArray(new String[columns.size()]);
        String[][] data = new String[results.size()][fields.size()];
        
        int i = 0;
        for (Hashtable<String, Object> res: results) 
        {
            int j = 0;
            for (String v : columns) 
            {
                Object o = res.get(v);
                if (o == null)
                    data[i][j] = "";
                else
                    data[i][j] = o.toString();
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