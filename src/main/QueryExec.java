import org.apache.log4j.*;
import org.antlr.runtime.*;
import org.antlr.runtime.tree.*;
import org.antlr.stringtemplate.*;
import java.io.*;

public class QueryExec {
    public static void main(String[] args) throws Exception {
		try
		{
			RDFLiteral lit = new RDFLiteral("\"alice@work.example\"");
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
		catch (NoViableAltException e)
		{
			System.out.println("Недопустимое выражение: " +
				"строка=" + Integer.toString(e.line) +
				", индекс=" + Integer.toString(e.index) +
				", токен='" + e.token.getText() + "'.");
		}
		catch (Exception e)
		{
			System.out.println("Ошибка: " + e.getMessage());
			StringWriter errors = new StringWriter();
            e.printStackTrace(new PrintWriter(errors));
            System.out.println(errors.toString());
        }
    }
}