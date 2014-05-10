import java.util.*;
import com.hp.hpl.jena.rdf.model.*;
import dnl.utils.text.table.TextTable;

public class AskQuery extends SparqlQuery {
    public AskQuery()
    {
        super();
    }
    
    protected void execute(List<Hashtable<String, Object>> results, Model model)
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