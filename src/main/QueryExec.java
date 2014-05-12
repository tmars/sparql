import org.apache.log4j.*;
import org.antlr.runtime.*;
import org.antlr.runtime.tree.*;
import org.antlr.stringtemplate.*;

public class QueryExec {
    public static void main(String[] args) throws Exception {
		try
		{
			LogManager.getRootLogger().setLevel(Level.OFF);
			ANTLRFileStream in = new ANTLRFileStream(args[0]);
			SparqlLexer lexer = new SparqlLexer(in);
			CommonTokenStream tokens = new CommonTokenStream(lexer);
			SparqlParser parser = new SparqlParser(tokens);
			parser.query(); 
			
			if (args.length > 2 && args[2].equals("-d"))
				Config.getInstance().setDebug(true);
			else
				Config.getInstance().setDebug(false);
			parser.query.getResult(args[1]);
		}
		catch (MismatchedTokenException e)
		{
			System.out.println("Ошибка разбора скрипта: " + e.getMessage());
		}
    }
}