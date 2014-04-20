import java.util.List;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Set;
import java.util.HashSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Statement;

public abstract class SparqlQuery {
    String dataset = "";
    int limit = -1;
    int offset = -1;
    List<String> bases = null;
    Hashtable<String, String> prefixes = null;
    SparqlWhere where = new SparqlWhere();
    SparqlOrder order = new SparqlOrder();
    
    public SparqlQuery(List<String> bs, Hashtable<String, String> ps)
    {
        bases = bs;
        prefixes = ps;
    }
    
    public void info()
    {
        System.out.println("type: "+getClass().getName());
        System.out.println("dataset: "+dataset);
        System.out.println("limit: "+limit);
        System.out.println("offset: "+offset);
        if (bases.isEmpty()) 
        {
            System.out.println("bases: [NONE]");
        } 
        else
        {
            System.out.println("bases:");
            for (String n : bases) 
            {
                System.out.println("\t" + n);
            }
        }
        if (prefixes.isEmpty()) 
        {
            System.out.println("prefixes: [NONE]");
        } 
        else
        {
            System.out.println("prefixes:");
            for (String pr : prefixes.keySet()) 
            {
                System.out.println("\t" + pr + " -> " + prefixes.get(pr));
            }
        }
        where.info();
        order.info();
    }
    
    public void setDataset(String d)
    {
        dataset = d;
    }
    
    public void setLimit(int v)
    {
        limit = v;
    }
    
    public void setOffset(int v)
    {
        offset = v;
    }
    
    public SparqlWhere getWhere()
    {
        return where;
    }
    
    public SparqlOrder getOrder()
    {
        return order;
    }
    
    protected abstract void execute(List<Hashtable<String, String>> results);
    
    protected void getResult(String filename)
    {
        Model model = ModelFactory.createDefaultModel();
        model.read(filename);
        
        // индексы попавших в результат
        Set<Integer> activeIndexes = new HashSet<Integer>();
            
        List<Hashtable<String, String>> prevResults = null;
        List<Hashtable<String, String>> curResults = null;
        
        // делаем отборы по всем триплетам условия выборки
        for (SparqlWhere.WhereTriplet whereTrp : where.triplets) 
        {
            activeIndexes.clear();
            curResults = new ArrayList<>();
        
            whereTrp.subject = getVarTermIRI(whereTrp.subject, whereTrp.subjectType);
            whereTrp.predicate = getVarTermIRI(whereTrp.predicate, whereTrp.predicateType);
            whereTrp.object = getVarTermIRI(whereTrp.object, whereTrp.objectType);
        
            // список утверждений в Модели
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
                
                // Ищем соответствие с предыдущими результатами
                if (prevResults != null) 
                {
                    boolean isFinded = false;
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
                            // Добавляем недостоющие значения переменных
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
                        prevResults.get(i).put(v, "[NONE]");
                    curResults.add(prevResults.get(i));
                }
            }
            prevResults = curResults;
        }
        prevResults = order.sort(prevResults);
        prevResults = makeOffset(prevResults);
        prevResults = makeLimit(prevResults);
        execute(prevResults);
    }
    
    private List<Hashtable<String, String>> makeOffset(List<Hashtable<String, String>> results)
    {
        if (offset > 0)
        {
            int i = 0;
            while (i < offset && results.size() > 0) 
            {
                results.remove(0);
                i++;
            }
        }
        return results;
    }
    
    private List<Hashtable<String, String>> makeLimit(List<Hashtable<String, String>> results)
    {
        if (limit >= 0)
        {
            while (results.size() > limit) 
            {
                results.remove(results.size()-1);
            }
        }
        return results;
    }
    
    private Triplet getTripletFromStatement(Statement stmt)
    {
        String s = stmt.getSubject().toString();     // получить субъект
        String p = stmt.getPredicate().toString();   // получить предикат
        String o;
        RDFNode to = stmt.getObject();      // получить объект
        if (to instanceof Resource) 
           o = to.toString();
        else // объект - литерал
            o = "\"" + to.toString() + "\"";
            
        return new Triplet(s, p, o);
    }
    
    private String getVarTermIRI(String value, String type)
    {
        String r = "";
        if (type.equals("short_iri"))
            r = getRealIRI(value);
        else // var | rdf_lit | iri | num_lit | bool_lit | blank
            r = value;
        return r;
    }
    
    private String getRealIRI(String iri)
    {
        String[] parts = iri.split(":");
        if (parts.length == 2 && prefixes.containsKey(parts[0] + ":"))
            return prefixes.get(parts[0] + ":") + parts[1];
        return "";
    }
}