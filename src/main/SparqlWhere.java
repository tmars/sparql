import java.util.*;
import org.antlr.runtime.*;
import org.antlr.runtime.tree.*;
import org.antlr.stringtemplate.*;
import com.hp.hpl.jena.rdf.model.*;

public class SparqlWhere 
{
    public class WhereTriplet extends Triplet
    {
        public boolean isOptional = false;
        public WhereTriplet(String s, String st, String p, String pt, String o, String ot, boolean isOpt)
        {
            super(s, st, p, pt, o, ot);
            isOptional = isOpt;
        }
        
        public List<String> getVars()
        {
            List<String> res = new ArrayList<>();
            if (subjectType.equals("var"))
                res.add(subject);
            if (predicateType.equals("var"))
                res.add(predicate);
            if (objectType.equals("var"))
                res.add(object);
            return res;
        }
    }
    
    String curSubject = "";
    String curSubjectType = "";
    String curPredicate = "";
    String curPredicateType = "";
    boolean isOptional = false;
    
    List<WhereTriplet> triplets = new ArrayList<>();
    List<CommonTree> filters = new ArrayList<>();
    
    public void start(String v, String t)
    {
        curSubject = v;
        curSubjectType = t;
    }
    
    public void addPredicate(String v, String t)
    {
        curPredicate = v;
        curPredicateType = t;
    }
    
    public void addObject(String v, String t)
    {
        WhereTriplet r = new WhereTriplet(
            curSubject, curSubjectType, 
            curPredicate, curPredicateType, 
            v, t,
            isOptional
        );
	    triplets.add(r);
    }
    
    public void setOptional(boolean f)
    {
        isOptional = f;
    }
    
    public void finish()
    {
    }
    
    public void addFilter(Object t)
    {
        filters.add((CommonTree)t);
    }
    
    private Triplet getTripletFromStatement(Statement stmt)
    {
        String s = stmt.getSubject().toString();     // �������� �������
        String p = stmt.getPredicate().toString();   // �������� ��������
        String o;
        RDFNode to = stmt.getObject();      // �������� ������
        if (to instanceof Resource) 
           o = to.toString();
        else // ������ - �������
            o = "\"" + to.toString() + "\"";
            
        return new Triplet(s, p, o);
    }
    
    public List<Hashtable<String, String>> fetch(Model model, SparqlQuery query)
    {   
        // ������� �������� � ���������
        Set<Integer> activeIndexes = new HashSet<Integer>();
            
        List<Hashtable<String, String>> prevResults = null;
        List<Hashtable<String, String>> curResults = null;
        
        // ������ ������ �� ���� ��������� ������� �������
        for (SparqlWhere.WhereTriplet whereTrp : triplets) 
        {
            activeIndexes.clear();
            curResults = new ArrayList<>();
        
            whereTrp.subject = query.getVarTermIRI(whereTrp.subject, whereTrp.subjectType);
            whereTrp.predicate = query.getVarTermIRI(whereTrp.predicate, whereTrp.predicateType);
            whereTrp.object = query.getVarTermIRI(whereTrp.object, whereTrp.objectType);
        
            // ������ ����������� � ������
            StmtIterator iter = model.listStatements();
            while (iter.hasNext()) 
            {
                Hashtable<String, String> curRes = new Hashtable<String, String>();
                Triplet dataTrp = getTripletFromStatement(iter.nextStatement());
               
                // subject
                if (whereTrp.subjectType.equals("var")) 
                    curRes.put(whereTrp.subject, dataTrp.subject);
                else if (!whereTrp.subject.equals(dataTrp.subject))
                    continue; // next statemment
                
                // predicate
                if (whereTrp.predicateType.equals("var")) 
                    curRes.put(whereTrp.predicate, dataTrp.predicate);
                else if (!whereTrp.predicate.equals(dataTrp.predicate))
                    continue; // next statemment
                
                // object
                if (whereTrp.objectType.equals("var")) 
                    curRes.put(whereTrp.object, dataTrp.object);
                else if (!whereTrp.object.equals(dataTrp.object))
                    continue; // next statemment
                
                // ���� ������������ � ����������� ������������
                if (prevResults != null) 
                {
                    boolean isFinded = false;
                    // ������������� ���������� ����������
                    for (int i = 0; i < prevResults.size(); i++)
                    {
                        // ���������� ����������� �� ���������� � ���������
                        Set<String> intersection = new HashSet<String>(prevResults.get(i).keySet());
                        intersection.retainAll(curRes.keySet());
                        boolean isEquals = true;
                        for (String v : intersection) 
                        {
                            if (!curRes.get(v).equals(prevResults.get(i).get(v))) 
                            {
                                isEquals = false;
                                break;
                            }
                        }
                        // ������ �������������� �� ���������� � ��������� ���������
                        if (isEquals) 
                        {
                            // ��������� ����������� �������� ����������
                            for (String v : prevResults.get(i).keySet()) 
                                curRes.put(v, prevResults.get(i).get(v));
                            isFinded = true;
                            activeIndexes.add(i);
                            break;
                        }
                    } 
                    if (!isFinded)
                        continue; // next statemment
                } 
                
                curResults.add(curRes);
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
                        prevResults.get(i).put(v, "[NONE]");
                    curResults.add(prevResults.get(i));
                }
            }
            prevResults = curResults;
        }

        // debug filter
        /*for (int j = 0; j < filters.size(); j++)
        {
            SparqlExpression expr = new SparqlExpression();
            for (int i = 0; i < prevResults.size(); i++) 
            {
                expr.setVars(prevResults.get(i));
                Object result = expr.exec(filters.get(j));
                prevResults.get(i).put(
                    "FILTER:"+Integer.toString(j), 
                    result.toString()
                );
            }
        }*/
        
        // ���������
        for (int j = 0; j < filters.size(); j++)
        {
            curResults = new ArrayList();
            SparqlExpression expr = new SparqlExpression();
            for (int i = 0; i < prevResults.size(); i++) 
            {
                expr.setVars(prevResults.get(i));
                Object result = expr.exec(filters.get(j));
                if (result instanceof Boolean && result == true)
                    curResults.add(prevResults.get(i));
            }
            prevResults = curResults;
        }
        return prevResults;
    }
    
    public void info()
    {
        System.out.println("where:");
        if (triplets.isEmpty()) 
        {
            System.out.println("\ttriples: [NONE]");
        }
        else 
        {
            System.out.println("\ttriples:");
            for (WhereTriplet t : triplets) 
            {
                System.out.print("\t\t" +
                    t.subject + " (" + t.subjectType+ ") " +
                    t.predicate + " (" + t.predicateType+ ") " +
                    t.object + " (" + t.objectType+ ")"
                );
                if (t.isOptional)
                    System.out.print(" [OPTIONAL]");
                System.out.println();
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
                DOTTreeGenerator gen = new DOTTreeGenerator();
                StringTemplate st = gen.toDOT(f);
                System.out.println(st);
            }
        }
    }
}