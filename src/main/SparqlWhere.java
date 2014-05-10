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
    
    List<List<WhereTriplet>> tripletsSets = new ArrayList<>();
    int curTripletsInd = 0;
    
    List<CommonTree> filters = new ArrayList<>();
    
    public SparqlWhere()
    {
        union();
    }
    
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
	    tripletsSets.get(curTripletsInd).add(r);
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
    
    public void union()
    {
        tripletsSets.add(new ArrayList());
        curTripletsInd = tripletsSets.size()-1;
    }
    
    private Triplet getTripletFromStatement(Statement stmt)
    {
        /*System.out.println(stmt.getLanguage());
        try{
        System.out.println(((Literal) stmt.getObject().as(Literal.class)).getDatatypeURI());
        }catch(Exception e){}
        if (stmt.getSubject()  instanceof Resource)
			Config.getInstance().log("subject: " + stmt.getSubject().toString());
		if (stmt.getObject()  instanceof Resource)
			Config.getInstance().log("object: " + stmt.getObject().toString());
		*/
		
        String s = stmt.getSubject().toString();     // получить субъект
        String p = stmt.getPredicate().toString();   // получить предикат
        String o;
        // получить объект
        if (stmt.getObject() instanceof Resource) 
        {
			o = stmt.getObject().toString();
		}
		else // объект - литерал
        {
            Literal l = ((Literal) stmt.getObject().as(Literal.class));
            RDFLiteral lit = new RDFLiteral(l.getString(),
				l.getDatatypeURI(), l.getLanguage());
			o = lit.toString();
		}    
        return new Triplet(s, p, o);
    }
    
    private Boolean compareParts(String a, String t, String b)
    {
        Boolean res = false;
        if (t.equals("short_iri"))
        {
            res = Config.getInstance().getRealIRI(a).equals(b);
        }
        else if (t.equals("rdf_lit"))
        {
			RDFLiteral al = new RDFLiteral(a);
			RDFLiteral bl = new RDFLiteral(b);
			
			res = al.equals(bl);
			//Config.getInstance().log(al + " - " + bl + " = " + res);
		}
        else // iri | num_lit | bool_lit | blank
        {
            res = a.equals(b);
        }
		// Config.getInstance().log(a + " (" + t + ") " + b + " = " + res.toString());
		
		return res;
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
					results.get(i).put(
						"[FILTER:"+Integer.toString(j)+"]", 
						result.toString()
					);
				}
			}
        }
        else
		{
			// Фильтруем
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
        // индексы попавших в результат
        Set<Integer> activeIndexes = new HashSet<Integer>();
            
        List<Hashtable<String, Object>> prevResults = null;
        List<Hashtable<String, Object>> curResults = null;
        
        // делаем отборы по всем триплетам условия выборки
        for (WhereTriplet whereTrp : triplets) 
        {
            activeIndexes.clear();
			curResults = new ArrayList<>();
				
            // список утверждений в Модели
            StmtIterator iter = model.listStatements();
            while (iter.hasNext()) 
            {
                Hashtable<String, Object> curRes = new Hashtable<String, Object>();
                Statement stmt = iter.nextStatement();
				Triplet dataTrp = getTripletFromStatement(stmt);
                
                // subject
                if (whereTrp.subjectType.equals("var")) 
                    curRes.put(whereTrp.subject, getTypedObject(stmt.getSubject()));
                else if (!compareParts(whereTrp.subject, whereTrp.subjectType, dataTrp.subject))
                    continue; // next statemment
                
                // predicate
                if (whereTrp.predicateType.equals("var")) 
                    curRes.put(whereTrp.predicate, getTypedObject(dataTrp.predicate, stmt.getPredicate()));
                else if (!compareParts(whereTrp.predicate, whereTrp.predicateType, dataTrp.predicate))
                    continue; // next statemment
                
                // object
                if (whereTrp.objectType.equals("var")) 
                    curRes.put(whereTrp.object, getTypedObject(dataTrp.object, stmt.getObject()));
                else if (!compareParts(whereTrp.object, whereTrp.objectType, dataTrp.object))
                    continue; // next statemment
                
                // Ищем соответствие с предыдущими результатами
                if (prevResults != null) 
                {
                    // Просматриваем предыдущие результаты
                    for (int i = 0; i < prevResults.size(); i++)
                    {
                        // Определяем соответсвия по переменным и значениям
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
                        // Найден пересекающийся по переменным и значениям результат
                        if (isEquals) 
                        {
                            Hashtable<String, Object> res = new Hashtable<String, Object>();
                            // Добавляем недостоющие значения переменных
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
            
            // Если опциональное условие то 
            // добавляем не попавшие предыдущие в текущие
            if (whereTrp.isOptional)
            {
                List<String> vars = whereTrp.getVars();
                // По всем предыдущим результатам не соответствующим текущим
                for (int i = 0; i < prevResults.size(); i++) if (!activeIndexes.contains(i)) 
                {
                    // Добавляем результат к текущему
                    for (String v : vars) if (!prevResults.get(i).containsKey(v))
                        prevResults.get(i).put(v, "");
                    curResults.add(prevResults.get(i));
                }
            }
            prevResults = curResults;
        }
        return prevResults;
    }
    
    private Object getTypedObject(String t, Object s)
    {
        Object res = t;
        try 
        {
            res = Double.parseDouble(t);
            res = Integer.parseInt(t);
        }
        catch (Exception e)
        {
            if (t.equals("true"))
                res = true;
            else if (t.equals("false"))
                res = false;
        }
		Config.getInstance().log("TEST: " + res + "(" + (s instanceof Resource) + ")");
        return res;
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