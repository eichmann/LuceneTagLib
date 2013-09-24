package edu.uiowa.lucene.conceptSearch;
import java_cup.runtime.*;

%%

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

[^\(\)\&\| ]+				{ return (new Symbol(ConceptParseSym.String, yytext()));
							}

" "+ { }
