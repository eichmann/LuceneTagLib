package edu.uiowa.lucene.biomedical;

import java_cup.runtime.*;

%%


%public
%cup
%class biomedicalParseFlex
%eofval{
	return (new Symbol(BooleanParseSym.EOF, ""));
%eofval}

DIACRITICALS=[\u00a8\u00b8\u0300-\u036f]
OTHER_CHAR=[\u2103\u2460-\u24ff\u2600-\u26ff\u271c\uf062\uf067\uf06d\uf8fe]
ALPHA=[:letter:]|{DIACRITICALS}|{OTHER_CHAR}

DIGIT=[0-9\u2150-\u218f]
OTHER_NUMERIC=[\u00bc\u00bd\u00be\u2150-\u218f\uf031]
NUMERIC={DIGIT}|{OTHER_NUMERIC}

ALPHA_NUMERIC={ALPHA}|{NUMERIC}


%% 

"("				{ return (new Symbol(edu.uiowa.lucene.booleanSearch.BooleanParseSym.LPAREN, yytext()));
							}

")"				{ return (new Symbol(BooleanParseSym.RPAREN, yytext()));
							}

"&"				{ return  (new Symbol(BooleanParseSym.AND_OP, yytext()));
							}

"|"				{ return (new Symbol(BooleanParseSym.OR_OP, yytext()));
							}

"!"				{ return (new Symbol(BooleanParseSym.EOF, yytext()));
							}

[\r\n]			{ return (new Symbol(BooleanParseSym.EOF, yytext()));
							}

[^\(\)\&\|\! ]+				{ return (new Symbol(BooleanParseSym.String, yytext()));
							}


" "+ { }
