import java.util.*;
import org.antlr.runtime.*;
import org.antlr.runtime.tree.*;
import org.antlr.stringtemplate.*;
import com.hp.hpl.jena.rdf.model.*;

public class SparqlWhere 
{
	List<List<WhereTriplet>> tripletsSets = new ArrayList<>();
    List<CommonTree> filters = new ArrayList<>();
    
	String filterPrefix = "FILTER:";
	
    public SparqlWhere()
    {
    }
    
    public void addTriplets(List<WhereTriplet> triplets)
	{
		tripletsSets.add(triplets);
	}
    
    public void addFilter(Object t)
    {
        filters.add((CommonTree)t);
    }
    
    public List<Hashtable<String, Object>> fetch(Model model)
    {   
        List<Hashtable<String, Object>> results = new ArrayList();
        for (List<WhereTriplet> triplets : tripletsSets)
        {
            results.addAll(fetchTriplets(triplets, model));
        }
        
		if (Config.getInstance().isDebug())
		{
			// debug filter
			for (int j = 0; j < filters.size(); j++)
			{
				SparqlExpression expr = new SparqlExpression();
				for (int i = 0; i < results.size(); i++) 
				{
					expr.setVars(results.get(i));
					Object result = expr.exec(filters.get(j));
					if (result == null)
						result = false;
					results.get(i).put(
						this.filterPrefix+Integer.toString(j), 
						result.toString()
					);
				}
			}
        }
        else
		{
			// ���������
			for (int j = 0; j < filters.size(); j++)
			{
				List<Hashtable<String, Object>> curResults = new ArrayList();
				SparqlExpression expr = new SparqlExpression();
				for (int i = 0; i < results.size(); i++) 
				{
					expr.setVars(results.get(i));
					Object result = expr.exec(filters.get(j));
					if (result instanceof Boolean && result == true)
						curResults.add(results.get(i));
				}
				results = curResults;
			}
		}
        
        return results;
    }
    
    private List<Hashtable<String, Object>> fetchTriplets(List<WhereTriplet> triplets, Model model)
    {
        // ������� �������� � ���������
        Set<Integer> activeIndexes = new HashSet<Integer>();
            
        List<Hashtable<String, Object>> prevResults = null;
        List<Hashtable<String, Object>> curResults = null;
        
        // ������ ������ �� ���� ��������� ������� �������
        for (WhereTriplet whereTrp : triplets) 
        {
            activeIndexes.clear();
			curResults = new ArrayList<>();
				
            // ������ ����������� � ������
            StmtIterator iter = model.listStatements();
            while (iter.hasNext()) 
            {
                Hashtable<String, Object> curRes = new Hashtable<String, Object>();
                Statement stmt = iter.nextStatement();
				StatementTriplet dataTrp = new StatementTriplet(stmt);
                
                // subject
                if (whereTrp.subject.type == RDFNode.Type.VAR) 
                    curRes.put(whereTrp.subject.toString(), dataTrp.subject);
                else if (!whereTrp.subject.equals(dataTrp.subject))
                    continue; // next statemment
                
                // predicate
                if (whereTrp.predicate.type == RDFNode.Type.VAR) 
                    curRes.put(whereTrp.predicate.toString(), dataTrp.predicate);
                else if (!whereTrp.predicate.equals(dataTrp.predicate))
                    continue; // next statemment
                
                // object
                if (whereTrp.object.type == RDFNode.Type.VAR) 
                    curRes.put(whereTrp.object.toString(), dataTrp.object);
                else if (!whereTrp.object.equals(dataTrp.object))
                    continue; // next statemment
                
                // ���� ������������ � ����������� ������������
                if (prevResults != null) 
                {
                    // ������������� ���������� ����������
                    for (int i = 0; i < prevResults.size(); i++)
                    {
                        // ���������� ����������� �� ���������� � ���������
                        Set<String> intersection = new HashSet<String>(prevResults.get(i).keySet());
                        intersection.retainAll(curRes.keySet());
                        boolean isEquals = true;
                        for (String v : intersection) 
                        {
							String a = curRes.get(v).toString();
							String b = prevResults.get(i).get(v).toString();
                            if (!a.equals(b)) 
                            {
							    isEquals = false;
                                break;
                            }
                        }
                        // ������ �������������� �� ���������� � ��������� ���������
                        if (isEquals) 
                        {
                            Hashtable<String, Object> res = new Hashtable<String, Object>();
                            // ��������� ����������� �������� ����������
                            for (String v : prevResults.get(i).keySet()) 
                                res.put(v, prevResults.get(i).get(v));
                            for (String v : curRes.keySet()) 
                                res.put(v, curRes.get(v));
							curResults.add(res);
                            activeIndexes.add(i);
                        }
                    } 
                } 
                else
                {
                    curResults.add(curRes);
                }
            }
            
            // ���� ������������ ������� �� 
            // ��������� �� �������� ���������� � �������
            if (whereTrp.isOptional)
            {
                List<String> vars = whereTrp.getVars();
                // �� ���� ���������� ����������� �� ��������������� �������
                for (int i = 0; i < prevResults.size(); i++) if (!activeIndexes.contains(i)) 
                {
                    // ��������� ��������� � ��������
                    for (String v : vars) if (!prevResults.get(i).containsKey(v))
                        prevResults.get(i).put(v, "");
                    curResults.add(prevResults.get(i));
                }
            }
            prevResults = curResults;
        }
        return prevResults;
    }
    
    public void info()
    {
        System.out.println("where:");
        if (tripletsSets.isEmpty()) 
        {
            System.out.println("\ttriplets: [NONE]");
        }
        else 
        {
            for (List<WhereTriplet> triplets : tripletsSets)
            {
                System.out.println("\ttriplets:");
                for (WhereTriplet t : triplets) 
                {
                    System.out.print("\t\t" + t);
                    if (t.isOptional)
                        System.out.print(" [OPTIONAL]");
                    System.out.println();
                }
            }
        }
        if (filters.isEmpty()) 
        {
            System.out.println("\tfilters: [NONE]");
        }
        else 
        {
            System.out.println("\tfilters:");
            for (CommonTree f : filters) 
            {
                System.out.println("\t\t" + f.toStringTree());
                /*DOTTreeGenerator gen = new DOTTreeGenerator();
                StringTemplate st = gen.toDOT(f);
                System.out.println(st);*/
            }
        }
    }
}