package edu.uiowa.lex;

import java.lang.System;
import org.apache.log4j.Logger;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

%%


%class biomedicalLexerFlex

%{
    static Logger logger = Logger.getLogger(biomedicalLexerFlex.class);
%} 

%unicode 9.0
%integer
%final
%public
%function getNextToken
%char
%buffer 255

DIACRITICALS=[\u00a8\u00b8\u0300-\u036f]
OTHER_CHAR=[\u2103\u2460-\u24ff\u2600-\u26ff\u271c\uf062\uf067\uf06d\uf8fe]
ALPHA=[:letter:]|{DIACRITICALS}|{OTHER_CHAR}
UPPER=[:uppercase:]
LOWER=[:lowercase:]

GREEK=[aA]"lpha" | [bB]"eta" | [gG]"amma" | [dD]"elta" | [eE]"psilon" | [\u03b1-\u03c9]

DIGIT=[0-9\u2150-\u218f]
OTHER_NUMERIC=[\u00bc\u00bd\u00be\u2150-\u218f\uf031]
EXPONENT="10(""-"?{DIGIT}+")"
NUMERIC={DIGIT}|{OTHER_NUMERIC}
NUMBER= {NUMERIC}+ | "-"?{DIGIT}*"."{DIGIT}+ | {EXPONENT}

ALPHA_NUMERIC={ALPHA}|{NUMERIC}

SAFE_ELEMENT="H"|"Li"|"Na"|"K"|"Rb"|"Cs"|"Fr"|"Be"|"Mg"|"Ca"|"Sr"|"Ba"|"Ra"|"Sc"|"Y"|"Lu"|"Lr"|"Ti"|"Zr"|"Hf"|"Rf"|"V"|"Nb"|"Ta"|"Db"|"Cr"|"Mo"|"W"|"Sg"|"Mn"|"Tc"|"Re"|"Bh"|"Fe"|"Ru"|"Os"|"Hs"|"Co"|"Rh"|"Ir"|"Mt"|"Ni"|"Pd"|"Pt"|"Ds"|"Cu"|"Ag"|"Au"|"Rg"|"Zn"|"Cd"|"Hg"|"Cn"|"Al"|"Ga"|"Tl"|"Uut"|"Si"|"Ge"|"Sn"|"Pb"|"Fl"|"N"|"Sb"|"Bi"|"Uup"|"O"|"Se"|"Te"|"Po"|"Lv"|"F"|"Cl"|"Br"|"Uus"|"He"|"Ne"|"Ar"|"Kr"|"Xe"|"Rn"|"Uuo"|"La"|"Ac"|"Ce"|"Th"|"Pr"|"Pa"|"Nd"|"U"|"Pm"|"Np"|"Sm"|"Pu"|"Eu"|"Am"|"Gd"|"Cm"|"Tb"|"Bk"|"Dy"|"Cf"|"Ho"|"Es"|"Er"|"Tm"|"Md"|"Yb"
ELEMENT="H"|"Li"|"Na"|"K"|"Rb"|"Cs"|"Fr"|"Be"|"Mg"|"Ca"|"Sr"|"Ba"|"Ra"|"Sc"|"Y"|"Lu"|"Lr"|"Ti"|"Zr"|"Hf"|"Rf"|"V"|"Nb"|"Ta"|"Db"|"Cr"|"Mo"|"W"|"Sg"|"Mn"|"Tc"|"Re"|"Bh"|"Fe"|"Ru"|"Os"|"Hs"|"Co"|"Rh"|"Ir"|"Mt"|"Ni"|"Pd"|"Pt"|"Ds"|"Cu"|"Ag"|"Au"|"Rg"|"Zn"|"Cd"|"Hg"|"Cn"|"B"|"Al"|"Ga"|"In"|"Tl"|"Uut"|"C"|"Si"|"Ge"|"Sn"|"Pb"|"Fl"|"N"|"P"|"As"|"Sb"|"Bi"|"Uup"|"O"|"S"|"Se"|"Te"|"Po"|"Lv"|"F"|"Cl"|"Br"|"I"|"At"|"Uus"|"He"|"Ne"|"Ar"|"Kr"|"Xe"|"Rn"|"Uuo"|"La"|"Ac"|"Ce"|"Th"|"Pr"|"Pa"|"Nd"|"U"|"Pm"|"Np"|"Sm"|"Pu"|"Eu"|"Am"|"Gd"|"Cm"|"Tb"|"Bk"|"Dy"|"Cf"|"Ho"|"Es"|"Er"|"Tm"|"Md"|"Yb"|"No"

UNIT_PREFIX=([pP]"eta"?) | ([tT]"era"?) | ([gG]"iga"?) | ([mM]"ega"?) | ([kK]"ilo"?) | ([cC]"enti"?) | ([dD]"eca"?) | ([dD]"eci"?) | ([mM]"illi"?) | "m"?[u\u00b5] | ([mM]"icro"?) | ([nN]"ano"?) | ([pP]"ico"?)

DURATION={UNIT_PREFIX}?("s"("ec""ond"?"s"?)?) | ([mM]"inute""s"?) | ([mM]"in") | ([hH]"our""s"?) | ([hH][rR]?[sS]?) | ([dD]("ay""s"?)?) | ([wW]"eek""s"?) | ([mM]"onth""s"?) | ([yY]"ear""s"?) | ([yY][rR][sS]?) | ([dD]"ecade""s"?) | ([cC]"entur"("y"|"ies")) | [mM]"illeni"("um"|"a")

LENGTH={UNIT_PREFIX}?( ("meter""s"?) | [mM] | ([mM]"icron""s"?) | ([aA]"ngstrom""s"?) | ([iI]"nch""es"?) | "foot" | "feet" | ([mM]"ile""s"?) | ([mM]"il""s"?) )

AREA={UNIT_PREFIX}?( [mM]"2" | [mM]"(2)" ) | ([sS]"quare"{WHITE_SPACE_CHAR}+{LENGTH}) | ([aA]"cre""s"?) | ([hH]"ectare""s"?)

VOLUME={UNIT_PREFIX}?( [mM]"3" | [mM]"(3)" | [lL]("iter""s"?)? ) | ([cC]"ubic"{WHITE_SPACE_CHAR}+{LENGTH})

MASS={UNIT_PREFIX}?([gG]([mM]|"ram")?"s"?) | [pP]"ound""s"? | [lL][bB][sS]? | [oO]"unce""s"? | [oO][zZ] | [sS]"lug"

SPEED=({LENGTH} {WHITE_SPACE_CHAR}* ("/"|"per") {WHITE_SPACE_CHAR}* {DURATION})

DENSITY={MASS} {WHITE_SPACE_CHAR}* ("/"|"per") {WHITE_SPACE_CHAR}* {LENGTH} {WHITE_SPACE_CHAR}*"2"

FORCE=([nN]"newton"|[nN][tT]|[dD]"yne""s"?|[kK][gG][wW][tT]|[lL][bB][wW][tT]|{MASS}{WHITE_SPACE_CHAR}+[wW]"eight"|(([sS]"hort"|[lL]"ong"|[mM]"etric"){WHITE_SPACE_CHAR}+)?[tT]"on")

ENERGY={UNIT_PREFIX}?( [jJ]"oule"?"s"? | [eE]"rg""s"? | [cC]"al""orie"?"s"? | [bB][tT][uU]"s"? | [wW][hH]"s"? | [wW]"att""s"? )

POWER={UNIT_PREFIX}?[wW]"att"?"s"? | [hH]"orsepower" | [hH][pP]

PRESSURE=([aA]"tmosphere""s"?|[aA][tT][mM]"s"?)

TEMPERATURE=([\u00b0] | "deg"("."|("ree""s"?))?) {WHITE_SPACE_CHAR}* ([fF]"arenheit"? | [cC]"elsius"? | [kK]"elvin"?)?

OTHER={UNIT_PREFIX}? ( ("b""ase"? | "bp" | "base"[- ]"pair""s") | "f"?"mol""e"?"s"? | "d"("B"|"ecibel""s"?) | "cal""orie"?"s"? | [uU]("nit""s"?)? | [iI]"."?[uU]"."? | [oO]"sm" | [eE]"quivalents" | [eE]"q" | "equiv""."? | [nN]"ormal"? | "mol.wt." | "beat""s"? | "p"("."|"er"){WHITE_SPACE_CHAR}*"cent" | "%")

UNIT=({DURATION}|{LENGTH}|{AREA}|{VOLUME}|{MASS}|{SPEED}|{DENSITY}|{FORCE}|{ENERGY}|{POWER}|{PRESSURE}|{TEMPERATURE}|{OTHER})

HYPHEN=[-\u0381\u2010-\u2014\u2500\ue011\uf02d\uf8ff\uff0d\ufffd]|"--"
PUNCTUATION=[.,:;?!&@]|{HYPHEN}|"..."|[\u00a1\u00a7\u00ad\u00b6\u00bf\u037e\u2024-\u2026\ud954\udf08\ue2f6\uf02c\uff0c\uff1a]

OPEN_GROUPING=[(\[{\u00ab\u02c2\u2329\u27e8\u3008\u300e\ufe5b\uff3b]
CLOSE_GROUPING=[)\]}\u00bb\u02c3\u232a\u27e9\u3009\u300f\uf029\uff09\uff3d]
NOT_CLOSE_GROUPING=[^)\]}\u00bb\u02c3\u232a\u27e9\u3009\u300f\uf029\uff09\uff3d]

OPEN_QUOTE=[\'\"`\u2018\u201c]
CLOSE_QUOTE=[\'\"\u2019\u201d\u201f]
POS_CLOSE_QUOTE=[\"\u2019\u201d\u201f]

PRIME=['\u00b4\u0374\u0384\u2032-\u2034]

SAFE_OPERATOR=[=<>#%*\\\^|~]|[-+]"/"[-+]|"++"|"<<"|">>"|"<="|">="|">/="|"</="|"<-""-"?|"-"?"->"|"<""-"?"-"?">"|("="{WHITE_SPACE_CHAR}+)?("less"|"greater"){WHITE_SPACE_CHAR}+"than"({WHITE_SPACE_CHAR}+"or"{WHITE_SPACE_CHAR}+"equal"{WHITE_SPACE_CHAR}+"to")?|[\u0084\u0091-\u009d\u00a9\u00ac\u00ae\u00b1\u00b7\u00d7\u00f7\u02dc\u0385\u0387\u05bf\u0903\u2016\u2020-\u2022\u2030\u2031\u203a\u203e\u2041\u2044\u204e\u2122\u2190-\u22ff\u2308\u2309\u25a0-\u25ff\u279d\u27c2\u29e7\u2a7d\u2a7e\ufe64\uff05\uff0b\uff1c-\uff1e\uff5e\uff65]
OPERATOR=[-+=/<>#%*\\\^|~]|[-+]"/"[-+]|"++"|"<<"|">>"|"<="|">="|">/="|[<>]{WHITE_SPACE_CHAR}*"or"{WHITE_SPACE_CHAR}*"="|"</="|"<--"|"-->"|("="{WHITE_SPACE_CHAR}+)?("less"|"greater"){WHITE_SPACE_CHAR}+"than"({WHITE_SPACE_CHAR}+"or"{WHITE_SPACE_CHAR}+"equal"{WHITE_SPACE_CHAR}+"to")?|[\u0084\u0091-\u009d\u00a9\u00ac\u00ae\u00b1\u00b7\u00d7\u00f7\u02dc\u0385\u0387\u05bf\u0903\u2016\u2020-\u2022\u2030\u2031\u203a\u203e\u2041\u2044\u204e\u2122\u2190-\u22ff\u2308\u2309\u25a0-\u25ff\u279d\u27c2\u29e7\u2a7d\u2a7e\ufe64\uff05\uff0b\uff1c-\uff1e\uff5e\uff65]
SUPERSCRIPT=[\u00af\u00b2\u00b3\u00b9\u02d9\u02da\u2070-\u208b]

COMP_CHAR_SEQ= ({GREEK}|{DIGIT}|"N"|"-"|{PRIME})+ ("," ({GREEK}|{DIGIT}|"N"|"-"|{PRIME})+ )*
//MOLECULE_PREFIX={ALPHA}+|{COMP_CHAR_SEQ}+|{ALPHA}*{OPEN_GROUPING}
//MOLECULE_SUFFIX={CLOSE_GROUPING}{ALPHA}*
MOLECULE=({ALPHA}+|{COMP_CHAR_SEQ}+|{ALPHA}*{OPEN_GROUPING}{NOT_CLOSE_GROUPING}+{CLOSE_GROUPING}{ALPHA}*)

CURRENCY=[$\u00a2-\u00a5\u20ac\uffe0\uffe1]

TERMINATION={PUNCTUATION}|{CLOSE_GROUPING}|{CLOSE_QUOTE}|"(ABSTRACT"
POS_TERMINATION={PUNCTUATION}|{CLOSE_GROUPING}|{POS_CLOSE_QUOTE}|"(ABSTRACT"
SAFE_TERMINATION={PUNCTUATION}|{CLOSE_QUOTE}|"(ABSTRACT"
NOT_SAFE_TERMINATION=[^,]

NON_BREAKING_SPACE=[\u00a0]
WHITE_SPACE_CHAR=[\r\n\ \t\b\012\u2002-\u200e\u202f\u3000\uf020\ufeff]|{NON_BREAKING_SPACE}
NON_WHITE_SPACE_CHAR=[^\r\n\ \t\b\012]

//NAME_TITLE_ABBREV=("Ch"|"No"|"Nos"|"no"|"nos"|"n"|"Sta"|"Dra"|"Dres"|"Dr"|"Drs"|"Ft"|"Ph"|"Pr"|"Prs"|"Prof"|"Profs"|"Pvt"|"Ms"|"Mt"|"Mrs"|"Mr"|"St"|"Co"|"Chr"|"Th"|"Fr"|"Rev"|"Ste"|"Messrs")"."
//NAME_TITLE_SUFFIX=("MD"|"CNPq"|"PhD"|"III"|"IISc"|"Sr"|"FRNS"|"FRSQ"|"MSc"|"FACS"|"Sc")"."
NAME_INITIALS={UPPER}("."?"-"?{UPPER})*"."

//ORG_ABBREV=("Inc"|"Ltd"|"LLC"|"GmbH"|"Dept"|"Assoc"|"Lab"|"SA"|"Corp"|"Lic"|"Mag"|"Govt"|"Biol"|"Pty"|"Inst")"."

%state DISABLED

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

<YYINITIAL> "<<EoF>>" {
	// quietly eat this as a means of decoupling from the look-ahead constraints
	// not a pretty solution, but I'll worry about that 'tomorrow'
	}

<YYINITIAL> "(author's transl)" | "(ABSTRACT TRUNCATED AT 250 WORDS)" / {TERMINATION}*{WHITE_SPACE_CHAR} {
	}

<YYINITIAL> [35]{PRIME}"-"? / {TERMINATION}*{WHITE_SPACE_CHAR} {
	return biomedicalToken.COMPOUND_Token;
	}	

<YYINITIAL> "'s" / {POS_TERMINATION}*{WHITE_SPACE_CHAR} {
	return biomedicalToken.POS_Token;
	}

<YYINITIAL> "EC"{WHITE_SPACE_CHAR}*{DIGIT}+([-.]{DIGIT}+)+"."?"-"? / {TERMINATION}*{WHITE_SPACE_CHAR} {
	return biomedicalToken.WORD_Token;
	}

<YYINITIAL> "ORF"{WHITE_SPACE_CHAR}*{DIGIT}+ / {TERMINATION}*{WHITE_SPACE_CHAR} {
	return biomedicalToken.WORD_Token;
	}

<YYINITIAL> "UCL"{WHITE_SPACE_CHAR}*{DIGIT}+ / {TERMINATION}*{WHITE_SPACE_CHAR} {
	return biomedicalToken.WORD_Token;
	}

<YYINITIAL> "SB/"{DIGIT}+ / {TERMINATION}*{WHITE_SPACE_CHAR} {
	return biomedicalToken.WORD_Token;
	}

<YYINITIAL> "Sch"{WHITE_SPACE_CHAR}*{DIGIT}+ / {TERMINATION}*{WHITE_SPACE_CHAR} {
	return biomedicalToken.WORD_Token;
	}

<YYINITIAL> "RMI"{WHITE_SPACE_CHAR}+{DIGIT}+{WHITE_SPACE_CHAR}+{DIGIT}+ / {TERMINATION}*{WHITE_SPACE_CHAR} {
	return biomedicalToken.WORD_Token;
	}

<YYINITIAL> "http://"[-_a-zA-Z0-9]+("."[-_a-zA-Z0-9]+)+("/"[-_.a-zA-Z0-9]*)* / {TERMINATION}*{WHITE_SPACE_CHAR} {
	return biomedicalToken.URL_Token;
	}

<YYINITIAL> {OPERATOR}? {NUMBER}({WHITE_SPACE_CHAR}*{OPERATOR}{WHITE_SPACE_CHAR}*{EXPONENT})? {WHITE_SPACE_CHAR}* ({UNIT}({WHITE_SPACE_CHAR}*"/"{WHITE_SPACE_CHAR}*{UNIT})*)? / {TERMINATION}*{WHITE_SPACE_CHAR} {
	return biomedicalToken.NUMBER_Token;
	}

<YYINITIAL> {NUMBER}"-"("times"|"fold"|{UNIT})"-old"? / {TERMINATION}*{WHITE_SPACE_CHAR} {
	return biomedicalToken.NUMBER_Token;
	}

<YYINITIAL> {DIGIT}+("/"|{HYPHEN}|":"){DIGIT}+ / {TERMINATION}*{WHITE_SPACE_CHAR} {
	return biomedicalToken.NUMBER_Token;
	}

<YYINITIAL> ("pH"{WHITE_SPACE_CHAR}*)?{DIGIT}+("."{DIGIT}+)?"%"?{WHITE_SPACE_CHAR}*("+/-"|"plus"{WHITE_SPACE_CHAR}+"or"{WHITE_SPACE_CHAR}+"minus"|{WHITE_SPACE_CHAR}"to"{WHITE_SPACE_CHAR}|"-"+){WHITE_SPACE_CHAR}*{DIGIT}+("."{DIGIT}+)?"%"? {WHITE_SPACE_CHAR}* (({UNIT}{WHITE_SPACE_CHAR}*)+("/"{WHITE_SPACE_CHAR}*{UNIT})*)? / {TERMINATION}*{WHITE_SPACE_CHAR} {
	return biomedicalToken.NUMBER_Token;
	}

<YYINITIAL> {DIGIT}{1,3}(","{DIGIT}{1,3})*("."{DIGIT}+)?("-"{DIGIT}+("."{DIGIT}+)?)?"%"? / {TERMINATION}*{WHITE_SPACE_CHAR} {
	return biomedicalToken.NUMBER_Token;
	}

<YYINITIAL> {OPEN_GROUPING}{NOT_CLOSE_GROUPING}+{CLOSE_GROUPING}"-"?{ALPHA_NUMERIC}+ / {TERMINATION}*{WHITE_SPACE_CHAR} {
	return biomedicalToken.COMPOUND_Token;
	}	

<YYINITIAL> {OPEN_GROUPING} / {ALPHA_NUMERIC}+("-"{ALPHA_NUMERIC}+)*"'s"?{TERMINATION}*{WHITE_SPACE_CHAR} {
	return biomedicalToken.GROUPING_Token;
	}

<YYINITIAL> {OPEN_GROUPING} / ({UPPER}"."({UPPER}".")+)","?|({LOWER}"."({LOWER}".")+)","? {
	return biomedicalToken.GROUPING_Token;
	}

<YYINITIAL> {OPEN_GROUPING} / {DIGIT}+"."{DIGIT}+{TERMINATION}*{WHITE_SPACE_CHAR} {
	return biomedicalToken.GROUPING_Token;
	}

<YYINITIAL> {OPEN_GROUPING} / {DIGIT}{1,3}(","{DIGIT}{1,3})*("."{DIGIT}+)?("-"{DIGIT}+("."{DIGIT}+)?)?"%"?{TERMINATION}*{WHITE_SPACE_CHAR} {
	return biomedicalToken.GROUPING_Token;
	}

<YYINITIAL> {OPEN_GROUPING} / {NOT_CLOSE_GROUPING}*{TERMINATION}*{WHITE_SPACE_CHAR} {
	return biomedicalToken.GROUPING_Token;
	}

<YYINITIAL> {CLOSE_GROUPING} / {TERMINATION}*{WHITE_SPACE_CHAR} {
	return biomedicalToken.GROUPING_Token;
	}

<YYINITIAL> {OPEN_QUOTE} / {ALPHA_NUMERIC}+{CLOSE_QUOTE}*{WHITE_SPACE_CHAR} {
	return biomedicalToken.QUOTE_Token;
	}

<YYINITIAL> {CLOSE_QUOTE} / {TERMINATION}*{WHITE_SPACE_CHAR} {
	return biomedicalToken.QUOTE_Token;
	}

<YYINITIAL> {NAME_INITIALS} / {TERMINATION}?{WHITE_SPACE_CHAR} {
	return biomedicalToken.WORD_Token;
	}

<YYINITIAL> {UNIT}("/"({UNIT}|{DIGIT}+("."{DIGIT}+)?))+  / {TERMINATION}*{WHITE_SPACE_CHAR} {
	return biomedicalToken.UNIT_Token;
	}

<YYINITIAL> {UNIT}"/"{ALPHA}+  / {TERMINATION}*{WHITE_SPACE_CHAR} {
	return biomedicalToken.UNIT_Token;
	}

<YYINITIAL> {ALPHA}+"/"{UNIT}  / {TERMINATION}*{WHITE_SPACE_CHAR} {
	return biomedicalToken.UNIT_Token;
	}

<YYINITIAL> {UNIT}("."{UNIT}("-1"|"(-1)")?)+  / {TERMINATION}*{WHITE_SPACE_CHAR} {
	return biomedicalToken.UNIT_Token;
	}

<YYINITIAL> ({SAFE_ELEMENT}({OPEN_GROUPING}{DIGIT}+[-+]?{CLOSE_GROUPING}|{DIGIT}+)?[-+]*)+ / {TERMINATION}*{WHITE_SPACE_CHAR} {
	return biomedicalToken.COMPOUND_Token;
	}	

<YYINITIAL> {DIGIT}+{PRIME}?([,:]{DIGIT}+{PRIME}?)*("-"{ALPHA}+)+ / {TERMINATION}*{WHITE_SPACE_CHAR} {
	return biomedicalToken.COMPOUND_Token;
	}	

<YYINITIAL> ({UPPER}"."({UPPER}".")+)|({LOWER}"."({LOWER}".")+)|"Ph.D."|"and/or"|"or/and"|"vs."|"etc."|"mol.wt."|"mol. wt."|"mol.wts."|"vol."|"approx."|"viz."|"cv."|"ca."|"b.i.d."|"resp."|"et""."?{WHITE_SPACE_CHAR}*"al." / {TERMINATION}*{WHITE_SPACE_CHAR} {
	return biomedicalToken.WORD_Token;
	}

<YYINITIAL> ((("f."{WHITE_SPACE_CHAR}+)?("subsp." | "ssp." | "sp." | "spp.")) | "var." | "chemovar." | "cf." | "aff." | "str." | "nov." | "bv." | "pv." | "et al." | "emend." | "subgen." | "corrig." | "genomsp." | "Sh." ) / {TERMINATION}*{WHITE_SPACE_CHAR} {
	return biomedicalToken.WORD_Token;
	}

<YYINITIAL> ([iI]"n")|([eE]"x") {WHITE_SPACE_CHAR}+ ( "vivo" | "vitro" | "silico" | "situ" | "utero" | "papyro" | "planta" | "natura" ) / {TERMINATION}*{WHITE_SPACE_CHAR} {
	return biomedicalToken.WORD_Token;
	}

<YYINITIAL> "non-"? ( "v/v" | "ob/ob" | "w/v" | "m/z" | "op/op" | "d"[pP]"/dt" ) / {TERMINATION}*{WHITE_SPACE_CHAR} {
	return biomedicalToken.WORD_Token;
	}

<YYINITIAL> {ALPHA}+ / "--"{ALPHA}+{TERMINATION}*{WHITE_SPACE_CHAR} {
	return biomedicalToken.WORD_Token;
	}	

<YYINITIAL> "--" / {ALPHA}+{TERMINATION}*{WHITE_SPACE_CHAR} {
	return biomedicalToken.PUNC_Token;
	}	

<YYINITIAL> {ALPHA_NUMERIC}+("-"{ALPHA_NUMERIC}+)* / "'s"?{TERMINATION}*{WHITE_SPACE_CHAR} {
	return biomedicalToken.WORD_Token;
	}	

<YYINITIAL> {ALPHA} {ALPHA_NUMERIC}* [(\[{] [^(\[{)\]}]+ [)\]}] {ALPHA}* / {TERMINATION}*{WHITE_SPACE_CHAR} {
	return biomedicalToken.WORD_Token;
	}	

<YYINITIAL> {ALPHA_NUMERIC}+[-+] / {TERMINATION}*{WHITE_SPACE_CHAR} {
	return biomedicalToken.WORD_Token;
	}	

<YYINITIAL> {ALPHA}+"'"("t"|"d"|"m"|"ve"|"ll"|"re"|"nt"|"T"|"D"|"M"|"VE"|"LL"|"RE"|"NT") / {TERMINATION}*{WHITE_SPACE_CHAR} {
	return biomedicalToken.WORD_Token;
	}	

<YYINITIAL> ({ELEMENT}({OPEN_GROUPING}{DIGIT}+[-+]?{CLOSE_GROUPING}|{DIGIT}+)?[-+]*)+("-"{ALPHA}+)? / {TERMINATION}*{WHITE_SPACE_CHAR} {
	return biomedicalToken.COMPOUND_Token;
	}	

<YYINITIAL> {ELEMENT}[-+]*{DIGIT}*[-+]*([-+:/]{ELEMENT}[-+]*{DIGIT}*[-+]*)*([-+:/]{ALPHA}+)* / {TERMINATION}*{WHITE_SPACE_CHAR} {
	return biomedicalToken.COMPOUND_Token;
	}	

<YYINITIAL> {ELEMENT}(({OPEN_GROUPING}{DIGIT}*[-+]?{CLOSE_GROUPING}|{DIGIT}+)?[-+]*)?(([-\u22ef,]|"."+)({ELEMENT}(({OPEN_GROUPING}{DIGIT}*[-+]?{CLOSE_GROUPING}|{DIGIT}+)?[-+]*)?|"\u03c0"|[pP][iI]))+("-"{ALPHA}+)? / {TERMINATION}*{WHITE_SPACE_CHAR} {
	return biomedicalToken.COMPOUND_Token;
	}	

<YYINITIAL> {ALPHA}{1,2} {WHITE_SPACE_CHAR}* {SAFE_OPERATOR} {WHITE_SPACE_CHAR}* ({ALPHA}{1,2}|{NUMBER}+{WHITE_SPACE_CHAR}*{UNIT}?)  / {TERMINATION}*{WHITE_SPACE_CHAR} {
	return biomedicalToken.EQUATION_Token;
	}
/*
<YYINITIAL> ({NUMBER} {WHITE_SPACE_CHAR}*)? {ALPHA}{1,2} {WHITE_SPACE_CHAR}* {SAFE_OPERATOR}+ {WHITE_SPACE_CHAR}* ({ALPHA}{1,2}|{NUMBER}+{WHITE_SPACE_CHAR}*{UNIT}?) {WHITE_SPACE_CHAR}* ({OPERATOR}+ {WHITE_SPACE_CHAR}* ({ALPHA}{1,2}|{NUMBER}+{WHITE_SPACE_CHAR}*{UNIT}?))* / {TERMINATION}*{WHITE_SPACE_CHAR} {
	return biomedicalToken.EQUATION_Token;
	}
*/
<YYINITIAL> {OPERATOR} / {TERMINATION}*{WHITE_SPACE_CHAR} {
	return biomedicalToken.OP_Token;
	}

<YYINITIAL> {CURRENCY}{WHITE_SPACE_CHAR}*{DIGIT}{1,3}((","|{WHITE_SPACE_CHAR}+){DIGIT}{3,3})*("."{DIGIT}+)? / {WHITE_SPACE_CHAR} {
	return biomedicalToken.CURRENCY_Token;
	}

<YYINITIAL> {WHITE_SPACE_CHAR}+ {
	//return biomedicalToken.OTHER_Token," ",yyline));
	}

<YYINITIAL> {PUNCTUATION} / {TERMINATION}*{WHITE_SPACE_CHAR} {
	return biomedicalToken.PUNC_Token;
	}

<YYINITIAL> {SUPERSCRIPT} / {TERMINATION}*{WHITE_SPACE_CHAR} {
	return biomedicalToken.SUPERSCRIPT_Token;
	}

<YYINITIAL> {MOLECULE}("-"{MOLECULE})+ / {TERMINATION}*{WHITE_SPACE_CHAR} {
	return biomedicalToken.COMPOUND_Token;
	}

<YYINITIAL> {MOLECULE}("-"({ALPHA}+|{COMP_CHAR_SEQ}+|{ALPHA}*{OPEN_GROUPING}{MOLECULE}("-"{MOLECULE})+{CLOSE_GROUPING}{ALPHA}*))+ / {TERMINATION}*{WHITE_SPACE_CHAR} {
	return biomedicalToken.COMPOUND_Token;
	}

<YYINITIAL> {MOLECULE}("-"({ALPHA}+|{COMP_CHAR_SEQ}+|{ALPHA}*{OPEN_GROUPING}{MOLECULE}("-"({ALPHA}+|{COMP_CHAR_SEQ}+|{ALPHA}*{OPEN_GROUPING}{MOLECULE}("-"{MOLECULE})+{CLOSE_GROUPING}{ALPHA}*))+{CLOSE_GROUPING}{ALPHA}*))+ / {TERMINATION}*{WHITE_SPACE_CHAR} {
	return biomedicalToken.COMPOUND_Token;
	}

<YYINITIAL> {NUMBER}{WHITE_SPACE_CHAR}*"/"{WHITE_SPACE_CHAR}*{UNIT}("/"({UNIT}|{DIGIT}+("."{DIGIT}+)?))+ / {TERMINATION}*{WHITE_SPACE_CHAR} {
	return biomedicalToken.NUMBER_Token;
	}

<YYINITIAL> {NUMBER}{OPERATOR}{NUMBER} / {TERMINATION}*{WHITE_SPACE_CHAR} {
	return biomedicalToken.NUMBER_Token;
	}

<YYINITIAL> {COMP_CHAR_SEQ}"-"?{OPEN_GROUPING}{ELEMENT}+{CLOSE_GROUPING}{ALPHA_NUMERIC}* / {TERMINATION}*{WHITE_SPACE_CHAR} {
	return biomedicalToken.COMPOUND_Token;
	}

<YYINITIAL> {UPPER}?{LOWER}*([-/]{LOWER}+)+ / {TERMINATION}*{WHITE_SPACE_CHAR} {
	return biomedicalToken.WORD_Token;
	}

<YYINITIAL> {NON_WHITE_SPACE_CHAR}*{NOT_SAFE_TERMINATION} / {SAFE_TERMINATION}*{WHITE_SPACE_CHAR} {
	return biomedicalToken.OTHER_Token;
	}

