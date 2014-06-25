package edu.uiowa.lucene.booleanSearch;
import java_cup.runtime.*;

%%

%public
%cup
%class booleanParseFlex
%eofval{
	return (new Symbol(BooleanParseSym.EOF, ""));
%eofval}

%%

\(							{ return (new Symbol(edu.uiowa.lucene.booleanSearch.BooleanParseSym.LPAREN, yytext()));
							}

\)							{ return (new Symbol(BooleanParseSym.RPAREN, yytext()));
							}

\&							{ return (new Symbol(BooleanParseSym.AND_OP, yytext()));
							}

\|							{ return (new Symbol(BooleanParseSym.OR_OP, yytext()));
							}

\!							{ return (new Symbol(BooleanParseSym.NOT_OP, yytext()));
							}

[\r\n]						{ return (new Symbol(BooleanParseSym.EOF, yytext()));
							}

[^\(\)\&\|\! ]+				{ return (new Symbol(BooleanParseSym.String, yytext()));
							}

" "+ { }
