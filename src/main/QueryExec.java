import org.apache.log4j.*;
import org.antlr.runtime.*;
import org.antlr.runtime.tree.*;
import org.antlr.stringtemplate.*;
import java.io.*;
import java.util.*;

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
			
			String filename = "";
			if (args.length > 1 && !args[1].equals("-d"))
				filename = args[1];
			
			if (Arrays.asList(args).contains("-d"))
				Config.getInstance().setDebug(true);
			else
				Config.getInstance().setDebug(false);
				
			for (SparqlQuery q : parser.queries)
				q.getResult(filename);
		}
		catch (MismatchedTokenException e)
		{
			System.out.println("Ошибка разбора скрипта: " +
				"строка=" + Integer.toString(e.line) +
				", индекс=" + Integer.toString(e.index) +
				", токен='" + e.token.getText() + "'.");
		}
		catch (NoViableAltException e)
		{
			System.out.println("Недопустимое выражение: " +
				"строка=" + Integer.toString(e.line) +
				", индекс=" + Integer.toString(e.index) +
				", токен='" + e.token.getText() + "'.");
		}
		catch (FileNotFoundException e)
		{
			System.out.println("Ошибка: файл со скриптом отсутсвует:" + e.getMessage());
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