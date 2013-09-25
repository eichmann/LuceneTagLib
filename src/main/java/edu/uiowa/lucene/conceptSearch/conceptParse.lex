package edu.uiowa.lucene.conceptSearch;
import java_cup.runtime.*;

%%

%public
%cup
%class conceptParseFlex
%eofval{
	return (new Symbol(ConceptParseSym.EOF, ""));
%eofval}

%%

\(							{ return (new Symbol(edu.uiowa.lucene.conceptSearch.ConceptParseSym.LPAREN, yytext()));
							}

\)							{ return (new Symbol(ConceptParseSym.RPAREN, yytext()));
							}

\&							{ return (new Symbol(ConceptParseSym.AND_OP, yytext()));
							}

\|							{ return (new Symbol(ConceptParseSym.OR_OP, yytext()));
							}

\!							{ return (new Symbol(ConceptParseSym.NOT_OP, yytext()));
							}

[\r\n]						{ return (new Symbol(ConceptParseSym.EOF, yytext()));
							}

[^\(\)\&\|\! ]+				{ return (new Symbol(ConceptParseSym.String, yytext()));
							}

" "+ { }
