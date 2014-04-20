grammar Sparql;

options {
    k=1;
}

@header{
    import java.util.ArrayList;
    import java.util.List;
    import java.util.Hashtable;
}

@members{
    List<String> bases = new ArrayList<>();
    Hashtable<String, String> prefixes = new Hashtable<String, String>();
    
    SparqlQuery query = null;

    private SelectQuery sq() {return (SelectQuery)query;}
    private AskQuery aq() {return (AskQuery)query;}
    private ConstructQuery cq() {return (ConstructQuery)query;}
    private DescribeQuery dq() {return (DescribeQuery)query;}
}

@rulecatch { }

// PARSER RULES

query
    : 
    prologue ( selectQuery | constructQuery | describeQuery | askQuery ) EOF { query.info(); }
    ;

prologue
    : baseDecl? prefixDecl*
    ;

baseDecl
    : 'BASE' R=IRI_REF {bases.add($R.text.substring(1, $R.text.length()-1));}
    ;

prefixDecl
    : 'PREFIX' P=PNAME_NS R=IRI_REF {prefixes.put($P.text, $R.text.substring(1, $R.text.length()-1));}
    ;

selectQuery
    : 'SELECT' {query = new SelectQuery(bases, prefixes);} 
        ( 'DISTINCT' {sq().setIsDistinct(true);})? 
        ( (var {sq().addField($var.text);})+ 
        | '*' {sq().setAllFields(true);}
        ) 
        datasetClause* whereClause solutionModifier 
    ;

constructQuery
    : 'CONSTRUCT' {query = new ConstructQuery(bases, prefixes);} 
        constructTemplate datasetClause* whereClause solutionModifier
    ;

describeQuery
    : 'DESCRIBE' {query = new DescribeQuery(bases, prefixes);} 
        ( varOrIRIref+ | '*' ) datasetClause* whereClause? solutionModifier 
    ;

askQuery
    : 'ASK' {query = new AskQuery(bases, prefixes);}
        datasetClause* whereClause 
    ;

datasetClause
    : 'FROM' ( defaultGraphClause | namedGraphClause )
    ;

defaultGraphClause
    : s=sourceSelector {query.setDataset($s.text);}
    ;

namedGraphClause
    : 'NAMED' s=sourceSelector {query.setDataset($s.text);}
    ;

sourceSelector
    : iriRef
    ;

whereClause
    : 'WHERE'? groupGraphPattern 
    ;

solutionModifier
    : orderClause? limitOffsetClauses?
    ;

limitOffsetClauses
    : ( limitClause offsetClause? | offsetClause limitClause? )
    ;

orderClause
    : 'ORDER' 'BY' orderCondition+
    ;

orderCondition
    : ( ( 'ASC' | 'DESC' ) brackettedExpression )
    | ( constraint | var )
    ;

limitClause
    : 'LIMIT' N=INTEGER {query.setLimit(Integer.parseInt($N.text));}
    ;

offsetClause
    : 'OFFSET' N=INTEGER {query.setOffset(Integer.parseInt($N.text));}
    ;

groupGraphPattern
    : '{' triplesBlock? ( ( graphPatternNotTriples | filter ) '.'? triplesBlock? )* '}'
    ;

triplesBlock
    : triplesSameSubject ( '.' triplesBlock? )?
    ;

graphPatternNotTriples
    : optionalGraphPattern | groupOrUnionGraphPattern | graphGraphPattern
    ;

optionalGraphPattern
    : 'OPTIONAL' {query.getWhere().setOptional(true);} groupGraphPattern {query.getWhere().setOptional(false);}
    ;

graphGraphPattern
    : 'GRAPH' varOrIRIref groupGraphPattern
    ;

groupOrUnionGraphPattern
    : groupGraphPattern ( 'UNION' groupGraphPattern )*
    ;

filter
    : 'FILTER' constraint 
    ;

constraint
    : v=brackettedExpression {query.getWhere().addFilter($v.value, "expr");} 
    | builtInCall 
    | functionCall
    ;

functionCall
    : iriRef argList
    ;

argList
    : ( NIL | '(' expression ( ',' expression )* ')' )
    ;

constructTemplate
    : '{' constructTriples? '}'
    ;

constructTriples
    : triplesSameSubject ( '.' constructTriples? )?
    ;

triplesSameSubject
    : s=varOrTerm {query.getWhere().start($s.value, $s.type);} 
    	propertyListNotEmpty {query.getWhere().finish();} 
    | triplesNode 
    	propertyList {query.getWhere().finish();} 
    ;

propertyListNotEmpty
    : p=verb {query.getWhere().addPredicate($p.value, $p.type);} 
    	objectList 
    	( ';' ( p=verb {query.getWhere().addPredicate($p.value, $p.type);} objectList )? )* 
    ;

propertyList
    : propertyListNotEmpty? 
    ;

objectList
    : object ( ',' object )* 
    ;

object
    : o=graphNode {query.getWhere().addObject($o.value, $o.type);}
    ;

verb returns [String type, String value]
    : v=varOrIRIref {$type = $v.type; $value = $v.value;}
    | 'a' {$type = "iri_ref"; $value = "rdf:type";}
    ;

triplesNode
    : collection
    | blankNodePropertyList
    ;

blankNodePropertyList
    : '[' propertyListNotEmpty ']'
    ;

collection
    : '(' graphNode+ ')'
    ;

graphNode returns [String type, String value]
    : o=varOrTerm {$type = $o.type; $value = $o.value;}
    | triplesNode {$type = ""; $value = "";}
    ;

varOrTerm returns [String type, String value]
    : v=var {$type="var"; $value=$v.text;}
    | g=graphTerm {$type=$g.type; $value=$g.value;}
    ;

varOrIRIref returns [String type, String value]
    : v=var {$type="var"; $value=$v.text;}
    | g=iriRef {$type=$g.type; $value=$g.value;}
    ;

var
    : VAR1
    | VAR2
    ;

graphTerm returns [String type, String value]
    : a=iriRef {$type=$a.type; $value=$a.value;}
    | b=rdfLiteral {$type="rdf_lit"; $value=$b.text;}
    | c=numericLiteral {$type="num_lit"; $value=$c.text;}
    | d=booleanLiteral {$type="bool_lit"; $value=$d.text;}
    | e=blankNode {$type="blank"; $value=$e.text;}
    | NIL
    ;

expression
    : conditionalOrExpression
    ;

conditionalOrExpression
    : conditionalAndExpression ( '||' conditionalAndExpression )*
    ;

conditionalAndExpression
    : valueLogical ( '&&' valueLogical )*
    ;

valueLogical
    : relationalExpression
    ;

relationalExpression
    : numericExpression ( '=' numericExpression | '!=' numericExpression | '<' numericExpression | '>' numericExpression | '<=' numericExpression | '>=' numericExpression )?
    ;

numericExpression
    : additiveExpression
    ;

additiveExpression
    : multiplicativeExpression ( '+' multiplicativeExpression | '-' multiplicativeExpression | numericLiteralPositive | numericLiteralNegative )*
    ;

multiplicativeExpression
    : unaryExpression ( '*' unaryExpression | '/' unaryExpression )*
    ;

unaryExpression
    :  '!' primaryExpression
    | '+' primaryExpression
    | '-' primaryExpression
    | primaryExpression
    ;

primaryExpression
    : brackettedExpression | builtInCall | iriRefOrFunction | rdfLiteral | numericLiteral | booleanLiteral | var
    ;

brackettedExpression returns [String value]
    : '(' e=expression ')' {$value = $e.text;}
    ;

builtInCall
    : 'STR' '(' expression ')'
    | 'LANG' '(' expression ')'
    | 'LANGMATCHES' '(' expression ',' expression ')'
    | 'DATATYPE' '(' expression ')'
    | 'BOUND' '(' var ')'
    | 'sameTerm' '(' expression ',' expression ')'
    | 'isIRI' '(' expression ')'
    | 'isURI' '(' expression ')'
    | 'isBLANK' '(' expression ')'
    | 'isLITERAL' '(' expression ')'
    | regexExpression
    ;

regexExpression
    : 'REGEX' '(' expression ',' expression ( ',' expression )? ')'
    ;

iriRefOrFunction
    : iriRef argList?
    ;

rdfLiteral
    : string ( LANGTAG | ( '^^' iriRef ) )?
    ;

numericLiteral
    : numericLiteralUnsigned | numericLiteralPositive | numericLiteralNegative
    ;

numericLiteralUnsigned
    : INTEGER
    | DECIMAL
    | DOUBLE
    ;

numericLiteralPositive
    : INTEGER_POSITIVE
    | DECIMAL_POSITIVE
    | DOUBLE_POSITIVE
    ;

numericLiteralNegative
    : INTEGER_NEGATIVE
    | DECIMAL_NEGATIVE
    | DOUBLE_NEGATIVE
    ;

booleanLiteral
    : 'true'
    | 'false'
    ;

string
    : STRING_LITERAL1
    | STRING_LITERAL2
    /* | STRING_LITERAL_LONG('0'..'9') | STRING_LITERAL_LONG('0'..'9')*/
    ;

iriRef returns [String type, String value]
    : t=IRI_REF {$type="iri"; $value=$t.text.substring(1,$t.text.length()-1);}
    | p=prefixedName  {$type="short_iri"; $value=$p.text;}
    ;

prefixedName
    : PNAME_LN
    | PNAME_NS
    ;

blankNode
    : BLANK_NODE_LABEL
    | ANON
    ;

// LEXER RULES

IRI_REF
    : '<' ( options {greedy=false;} : ~('<' | '>' | '"' | '{' | '}' | '|' | '^' | '\\' | '`') | (PN_CHARS))* '>'
    ;

PNAME_NS
    : p=PN_PREFIX? ':'
    ;

PNAME_LN
    : n=PNAME_NS l=PN_LOCAL
    ;

BLANK_NODE_LABEL
    : '_:' PN_LOCAL
    ;

VAR1
    : '?' v=VARNAME { setText($v.text); }
    ;

VAR2
    : '$' v=VARNAME { setText($v.text); }
    ;

LANGTAG
    : '@' PN_CHARS_BASE+ ('-' (PN_CHARS_BASE DIGIT)+)*
    ;

INTEGER
    : DIGIT+
    ;

DECIMAL
    : DIGIT+ '.' DIGIT*
    | '.' DIGIT+
    ;

DOUBLE
    : DIGIT+ '.' DIGIT* EXPONENT
    | '.' DIGIT+ EXPONENT
    | DIGIT+ EXPONENT
    ;

INTEGER_POSITIVE
    : '+' INTEGER
    ;

DECIMAL_POSITIVE
    : '+' DECIMAL
    ;

DOUBLE_POSITIVE
    : '+' DOUBLE
    ;

INTEGER_NEGATIVE
    : '-' INTEGER
    ;

DECIMAL_NEGATIVE
    : '-' DECIMAL
    ;

DOUBLE_NEGATIVE
    : '-' DOUBLE
    ;

EXPONENT
    : ('e'|'E') ('+'|'-')? DIGIT+
    ;

STRING_LITERAL1
    : '\'' ( options {greedy=false;} : ~('\u0027' | '\u005C' | '\u000A' | '\u000D') | ECHAR )* '\''
    ;

STRING_LITERAL2
    : '"'  ( options {greedy=false;} : ~('\u0022' | '\u005C' | '\u000A' | '\u000D') | ECHAR )* '"'
    ;

STRING_LITERAL_LONG1
    : '\'\'\'' ( options {greedy=false;} : ( '\'' | '\'\'' )? (~('\'' | '\\') | ECHAR ) )* '\'\'\''
    ;

STRING_LITERAL_LONG2
    : '"""' ( options {greedy=false;} : ( '"' | '""' )? ( ~('\'' | '\\') | ECHAR ) )* '"""'
    ;

ECHAR
    : '\\' ('t' | 'b' | 'n' | 'r' | 'f' | '"' | '\'')
    ;

NIL
    : '(' WS* ')'
    ;

ANON
    : '[' WS* ']'
    ;

PN_CHARS_U
    : PN_CHARS_BASE | '_'
    ;

VARNAME
    : ( PN_CHARS_U | DIGIT ) ( PN_CHARS_U | DIGIT | '\u00B7' | ('\u0300'..'\u036F') | ('\u203F'..'\u2040') )*
    ;

fragment
PN_CHARS
    : PN_CHARS_U
    | '-'
    | DIGIT
    /*| '\u00B7'
    | '\u0300'..'\u036F'
    | '\u203F'..'\u2040'*/
    ;

PN_PREFIX
    : PN_CHARS_BASE ((PN_CHARS|'.')* PN_CHARS)?
    ;

PN_LOCAL
    : ( PN_CHARS_U | DIGIT ) ((PN_CHARS|'.')* PN_CHARS)?
    ;

fragment
PN_CHARS_BASE
    : 'A'..'Z'
    | 'a'..'z'
    | '\u00C0'..'\u00D6'
    | '\u00D8'..'\u00F6'
    | '\u00F8'..'\u02FF'
    | '\u0370'..'\u037D'
    | '\u037F'..'\u1FFF'
    | '\u200C'..'\u200D'
    | '\u2070'..'\u218F'
    | '\u2C00'..'\u2FEF'
    | '\u3001'..'\uD7FF'
    | '\uF900'..'\uFDCF'
    | '\uFDF0'..'\uFFFD'
    ;

fragment
DIGIT
    : '0'..'9'
    ;

WS
    : (' '
    | '\t'
    | '\n'
    | '\r')+ { $channel=HIDDEN; }
    ;
