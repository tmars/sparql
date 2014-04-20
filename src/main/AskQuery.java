import java.util.List;
import java.util.Hashtable;
import dnl.utils.text.table.TextTable;

public class AskQuery extends SparqlQuery {
    public AskQuery(List<String> bs, Hashtable<String, String> ps)
    {
        super(bs, ps);
    }
    
    protected void execute(List<Hashtable<String, String>> results)
    {   
        String[] columnNames = {"result"};
        String[][] data = {{""}};
        
        if (results.isEmpty() == false) 
        {
            data[0][0] = "true";
        }
        else
        {
            data[0][0] = "false";
        }
        TextTable tt = new TextTable(columnNames, data);         
        tt.printTable();   
    }
}