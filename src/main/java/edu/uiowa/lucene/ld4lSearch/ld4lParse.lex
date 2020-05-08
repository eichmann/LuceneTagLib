package edu.uiowa.lucene.ld4lSearch;
import java.lang.System;
import org.apache.log4j.Logger;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

%%


%class ld4lLexerFlex

%{
    static Logger logger = Logger.getLogger(ld4lLexerFlex.class);
%} 

%unicode
%integer
%final
%public
%function getNextToken
%char
%buffer 255

ALPHA=[:letter:]
NUMERIC=[0-9\u2150-\u218f]
ALPHANUM={ALPHA}|{NUMERIC}

%{
  /** Character count processed so far */
  public final int yychar()
  {
    return yychar;
  }

  /**
   * Fills CharTermAttribute with the current token text.
   */
  public final void getText(CharTermAttribute t) {
    t.copyBuffer(zzBuffer, zzStartRead, zzMarkedPos-zzStartRead);
  }
  
  /**
   * Sets the scanner buffer size in chars
   */
   public final void setBufferSize(int numChars) {
     ZZ_BUFFERSIZE = numChars;
     char[] newZzBuffer = new char[ZZ_BUFFERSIZE];
     System.arraycopy(zzBuffer, 0, newZzBuffer, 0, Math.min(zzBuffer.length, ZZ_BUFFERSIZE));
     zzBuffer = newZzBuffer;
   }
%}
%%

{NUMERIC}+					{ return ld4lParseSym.EOF;
							}

{ALPHA}+([-]{ALPHANUM}+)* { return ld4lParseSym.String;
							}

. { }
