import java.io.FileInputStream;
import org.antlr.runtime.*;
import org.antlr.runtime.tree.*;
import org.antlr.stringtemplate.*;

public class QueryExec {
    public static void main(String[] args) throws Exception {
        ANTLRFileStream in = new ANTLRFileStream(args[0]);
    	SparqlLexer lexer = new SparqlLexer(in);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        SparqlParser parser = new SparqlParser(tokens);
        parser.query(); 
        /*CommonTree tree = (CommonTree)parser.query().getTree();
        DOTTreeGenerator gen = new DOTTreeGenerator();
        StringTemplate st = gen.toDOT(tree);
        System.out.println(st);*/
        
        parser.query.getResult(args[1]);
    }
}