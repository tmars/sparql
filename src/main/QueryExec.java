import java.io.FileInputStream;
import org.antlr.runtime.*;

public class QueryExec {
    public static void main(String[] args) throws Exception {
        ANTLRFileStream in = new ANTLRFileStream(args[0]);
    	SparqlLexer lexer = new SparqlLexer(in);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        SparqlParser parser = new SparqlParser(tokens);
        parser.query(); 
        parser.query.getResult(args[1]);
    }
}