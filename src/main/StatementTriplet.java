import com.hp.hpl.jena.rdf.model.*;

class StatementTriplet extends RDFTriplet
{
    public StatementTriplet(Statement stmt)
    {
        super(null, null, null);
        subject = new RDFNode(stmt.getSubject().toString(), RDFNode.Type.RESOURCE);
		predicate = new RDFNode(stmt.getPredicate().toString(), RDFNode.Type.RESOURCE);   // получить предикат
        // получить объект
        if (stmt.getObject() instanceof Resource) 
        {
			object = new RDFNode(stmt.getObject().toString(), RDFNode.Type.RESOURCE);
		}
		else // объект - литерал
        {
            Literal l = ((Literal) stmt.getObject().as(Literal.class));
            RDFLiteral lit = new RDFLiteral(l.getString(),
				l.getDatatypeURI(), l.getLanguage());
			object = new RDFNode(lit.toString(), RDFNode.Type.LITERAL);
		}
    }
}