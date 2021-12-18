package edu.uiowa.lucene.biomedical;

import java.lang.System;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import edu.uiowa.lex.biomedicalToken;

%%


%class biomedicalLexerFlex

%unicode
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
//POS_CLOSE_QUOTE=[\"\u2019\u201d\u201f]

DIGITP={DIGIT}|[pP]
PRIME=['\u00b4\u0374\u0384\u2032-\u2034]

SAFE_OPERATOR=[=<>#%*\\\^|~]|[-+]"/"[-+]|"++"|"<<"|">>"|"<="|">="|">/="|"</="|"<-""-"?|"-"?"->"|"<""-"?"-"?">"|("="{WHITE_SPACE_CHAR}+)?("less"|"greater"){WHITE_SPACE_CHAR}+"than"({WHITE_SPACE_CHAR}+"or"{WHITE_SPACE_CHAR}+"equal"{WHITE_SPACE_CHAR}+"to")?|[\u0084\u0091-\u009d\u00a9\u00ac\u00ae\u00b1\u00b7\u00d7\u00f7\u02dc\u0385\u0387\u05bf\u0903\u2016\u2020-\u2022\u2030\u2031\u203a\u203e\u2041\u2044\u204e\u2122\u2190-\u22ff\u2308\u2309\u25a0-\u25ff\u279d\u27c2\u29e7\u2a7d\u2a7e\ufe64\uff05\uff0b\uff1c-\uff1e\uff5e\uff65]
OPERATOR=[-+=/<>#%*\\\^|~]|[-+]"/"[-+]|"++"|"<<"|">>"|"<="|">="|">/="|[<>]{WHITE_SPACE_CHAR}*"or"{WHITE_SPACE_CHAR}*"="|"</="|"<--"|"-->"|("="{WHITE_SPACE_CHAR}+)?("less"|"greater"){WHITE_SPACE_CHAR}+"than"({WHITE_SPACE_CHAR}+"or"{WHITE_SPACE_CHAR}+"equal"{WHITE_SPACE_CHAR}+"to")?|[\u0084\u0091-\u009d\u00a9\u00ac\u00ae\u00b1\u00b7\u00d7\u00f7\u02dc\u0385\u0387\u05bf\u0903\u2016\u2020-\u2022\u2030\u2031\u203a\u203e\u2041\u2044\u204e\u2122\u2190-\u22ff\u2308\u2309\u25a0-\u25ff\u279d\u27c2\u29e7\u2a7d\u2a7e\ufe64\uff05\uff0b\uff1c-\uff1e\uff5e\uff65]
SUPERSCRIPT=[\u00af\u00b2\u00b3\u00b9\u02d9\u02da\u2070-\u208b]
SUBSCRIPT=[\u2080-\u2089]

COMP_CHAR_SEQ= ({GREEK}|{DIGIT}|"N"|"-"|{PRIME})+ ("," ({GREEK}|{DIGIT}|"N"|"-"|{PRIME})+ )*
//MOLECULE_PREFIX={ALPHA}+|{COMP_CHAR_SEQ}+|{ALPHA}*{OPEN_GROUPING}
//MOLECULE_SUFFIX={CLOSE_GROUPING}{ALPHA}*
MOLECULE=({ALPHA}+|{COMP_CHAR_SEQ}+|{ALPHA}*{OPEN_GROUPING}{NOT_CLOSE_GROUPING}+{CLOSE_GROUPING}{ALPHA}*)

CURRENCY=[$\u00a2-\u00a5\u20ac\uffe0\uffe1]

TERMINATION={PUNCTUATION}|{CLOSE_GROUPING}|{CLOSE_QUOTE}|"(ABSTRACT"
//POS_TERMINATION={PUNCTUATION}|{CLOSE_GROUPING}|{POS_CLOSE_QUOTE}|"(ABSTRACT"
//SAFE_TERMINATION={PUNCTUATION}|{CLOSE_QUOTE}|"(ABSTRACT"
//NOT_SAFE_TERMINATION=[^,]

NON_BREAKING_SPACE=[\u00a0]
WHITE_SPACE_CHAR=[\r\n\ \t\b\012\u2002-\u200e\u202f\u3000\uf020\ufeff]|{NON_BREAKING_SPACE}
NON_WHITE_SPACE_CHAR=[^\r\n\ \t\b\012]

NAME_TITLE_ABBREV=("Ch"|"No"|"Nos"|"no"|"nos"|"n"|"Sta"|"Dra"|"Dres"|"Dr"|"Drs"|"Ft"|"Ph"|"Pr"|"Prs"|"Prof"|"Profs"|"Pvt"|"Ms"|"Mt"|"Mrs"|"Mr"|"St"|"Co"|"Chr"|"Th"|"Fr"|"Rev"|"Ste"|"Messrs")"."
NAME_TITLE_SUFFIX=("MD"|"CNPq"|"PhD"|"III"|"IISc"|"Sr"|"FRNS"|"FRSQ"|"MSc"|"FACS"|"Sc")"."
NAME_INITIALS={UPPER}("."?"-"?{UPPER})*"."

ORG_ABBREV=("Inc"|"Ltd"|"LLC"|"GmbH"|"Dept"|"Assoc"|"Lab"|"SA"|"Corp"|"Lic"|"Mag"|"Govt"|"Biol"|"Pty"|"Inst")"."
TLD=("com"|"org"|"net"|"int"|"edu"|"gov"|"mil"|"arpa")
A_TLD=("ac"|"ad"|"ae"|"af"|"ag"|"ai"|"al"|"am"|"an"|"ao"|"aq"|"ar"|"as"|"at"|"au"|"aw"|"ax"|"az")
B_TLD=("ba"|"bb"|"bd"|"be"|"bf"|"bg"|"bh"|"bi"|"bj"|"bm"|"bn"|"bo"|"bq"|"br"|"bs"|"bt"|"bv"|"bw"|"by"|"bz")
C_TLD=("ca"|"cc"|"cd"|"cf"|"cg"|"ch"|"ci"|"ck"|"cl"|"cm"|"cn"|"co"|"cr"|"cs"|"cu"|"cv"|"cw"|"cx"|"cy"|"cz")
D_TLD=("dd"|"de"|"dj"|"dk"|"dm"|"do"|"dz")
E_TLD=("ec"|"ee"|"eg"|"eh"|"er"|"es"|"et"|"eu")
F_TLD=("fi"|"fj"|"fk"|"fm"|"fo"|"fr")
G_TLD=("ga"|"gb"|"gd"|"ge"|"gf"|"gg"|"gh"|"gi"|"gl"|"gm"|"gn"|"gp"|"gq"|"gr"|"gs"|"gt"|"gu"|"gw"|"gy")
H_TLD=("hk"|"hm"|"hn"|"hr"|"ht"|"hu")
I_TLD=("id"|"ie"|"il"|"im"|"in"|"io"|"iq"|"ir"|"is"|"it")
J_TLD=("je"|"jm"|"jo"|"jp")
K_TLD=("ke"|"kg"|"kh"|"ki"|"km"|"kn"|"kp"|"kr"|"krd"|"kw"|"ky"|"kz")
L_TLD=("la"|"lb"|"lc"|"li"|"lk"|"lr"|"ls"|"lt"|"lu"|"lv"|"ly")
M_TLD=("ma"|"mc"|"md"|"me"|"mg"|"mh"|"mk"|"ml"|"mm"|"mn"|"mo"|"mp"|"mq"|"mr"|"ms"|"mt"|"mu"|"mv"|"mw"|"mx"|"my"|"mz")
N_TLD=("na"|"nc"|"ne"|"nf"|"ng"|"ni"|"nl"|"no"|"np"|"nr"|"nu"|"nz")
O_TLD=("om")
P_TLD=("pa"|"pe"|"pf"|"pg"|"ph"|"pk"|"pl"|"pm"|"pn"|"pr"|"ps"|"pt"|"pw"|"py")
Q_TLD=("qa")
R_TLD=("re"|"ro"|"rs"|"ru"|"rw")
S_TLD=("sa"|"sb"|"sc"|"sd"|"se"|"sg"|"sh"|"si"|"sj"|"sk"|"sl"|"sm"|"sn"|"so"|"sr"|"ss"|"st"|"su"|"sv"|"sx"|"sy"|"sz")
T_TLD=("tc"|"td"|"tf"|"tg"|"th"|"tj"|"tk"|"tl"|"tm"|"tn"|"to"|"tp"|"tr"|"tt"|"tv"|"tw"|"tz")
U_TLD=("ua"|"ug"|"uk"|"us"|"uy"|"uz")
V_TLD=("va"|"vc"|"ve"|"vg"|"vi"|"vn"|"vu")
W_TLD=("wf"|"ws")
Y_TLD=("ye"|"yt"|"yu")
Z_TLD=("za"|"zm"|"zr"|"zw")
TOP_LEVEL_DOMAIN=({TLD}|{A_TLD}|{B_TLD}|{C_TLD}|{D_TLD}|{E_TLD}|{F_TLD}|{G_TLD}|{H_TLD}|{I_TLD}|{J_TLD}|{K_TLD}|{L_TLD}|{M_TLD}|{N_TLD}|{O_TLD}|{P_TLD}|{Q_TLD}|{R_TLD}|{S_TLD}|{T_TLD}|{U_TLD}|{V_TLD}|{W_TLD}|{Y_TLD}|{Z_TLD})

GRANT_TYPE=(A03|A11|A19|A22|A23|A24|B01|B08|B09|C06|D10|D14|D15|D18|D19|D21|D23|D24|D28|D30|D31|D32|D33|D34|D35|D36|D37|D38|D39|D42|D43|D71|DP1|DP2|DP3|DP4|DP5|DP7|E03|E10|E11|F05|F06|F15|F18|F19|F21|F30|F31|F32|F33|F34|F35|F36|F37|F38|G07|G08|G11|G12|G13|G19|G20|G94|H07|H13|H1N|H1S|H23|H25|H28|H2N|H50|H54|H57|H62|H64|H75|H79|H84|H86|H87|HD1|HD4|HD5|HD7|HD8|HR1|HR2|HS1|HS2|HS4|HS5|HS6|I01|I21|IK1|IK2|IP1|K01|K02|K04|K05|K06|K07|K08|K09|K10|K11|K12|K14|K15|K16|K17|K18|K20|K21|K22|K23|K24|K25|K26|K30|K99|KD1|KL1|KL2|KM1|L16|L17|L18|M01|N01|N02|N03|N43|N44|P01|P09|P20|P2C|P30|P40|P41|P42|P50|P51|P60|P76|PL1|PN1|PN2|R00|R01|R03|R04|R06|R09|R10|R12|R13|R15|R18|R19|R21|R22|R23|R24|R25|R28|R29|R30|R33|R34|R35|R36|R37|R41|R42|R43|R44|R49|R55|R56|R90|RC1|RC2|RC3|RC4|RF1|RL1|RL2|RL5|RL9|S03|S06|S07|S10|S11|S14|S15|S21|S22|SC1|SC2|SC3|T01|T02|T03|T06|T09|T14|T15|T16|T22|T23|T24|T32|T34|T35|T36|T37|T42|T90|TL1|TL4|TU2|U01|U09|U10|U11|U13|U14|U17|U18|U19|U1A|U1B|U1Q|U1S|U1V|U21|U22|U23|U24|U26|U27|U2C|U2G|U2R|U32|U34|U36|U38|U40|U41|U42|U43|U44|U45|U47|U48|U49|U50|U51|U52|U53|U54|U55|U56|U57|U58|U59|U60|U61|U62|U65|U66|U69|U75|U76|U79|U81|U82|U83|U84|U87|U88|U90|U94|U95|U96|U97|U98|UA1|UA5|UC1|UC2|UC4|UC6|UC7|UD1|UD3|UD5|UD6|UD7|UD8|UE1|UE2|UF1|UF2|UG1|UH1|UH2|UH3|UL1|UM1|UM2|UR1|UR3|UR6|UR8|US3|US4|VF1|X06|X98|X99|Y01|Y02|Z01|Z02|ZIA|ZIB|ZIC|ZID|ZIE|ZIF|ZIG|ZIH|ZII|ZIJ|ZIK)
IC_CODE=(AA|AD|AF|AG|AH|AI|AM|AO|AR|AT|BA|BB|BC|BD|BE|BF|BG|BH|BI|BJ|BK|BL|BM|BN|BO|BP|BQ|BR|BS|BT|BU|CA|CB|CC|CD|CE|CH|CI|CL|CM|CN|CO|CP|CT|DA|DC|DD|DE|DH|DK|DP|DS|EB|EH|EP|ES|EY|FD|FP|GD|GH|GM|HB|HC|HD|HG|HH|HI|HK|HL|HM|HO|HP|HR|HS|HV|IP|IS|JT|LM|MB|MD|MH|MN|NC|NR|NS|NU|OC|OD|OH|OR|PC|PE|PH|PR|PS|RG|RR|RS|SC|SH|SM|SO|SP|TI|TP|TR|TS|TW|VA|WC|WH)
GRANT_SEPARATOR=(" "|"/"|{HYPHEN})

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

<YYINITIAL> "(author's transl)" | "(ABSTRACT TRUNCATED AT 250 WORDS)" / {TERMINATION}|{WHITE_SPACE_CHAR} {
	}

<YYINITIAL> [35]{PRIME}"-"? / {TERMINATION}|{WHITE_SPACE_CHAR} {
	return biomedicalToken.COMPOUND_Token;
	}	

<YYINITIAL> ['\u2019]"s" / {WHITE_SPACE_CHAR} {
	return biomedicalToken.POS_Token;
	}

<YYINITIAL> "EC"{WHITE_SPACE_CHAR}*{DIGIT}+([-.]{DIGIT}+)+"."?"-"? / {TERMINATION}|{WHITE_SPACE_CHAR} {
	return biomedicalToken.WORD_Token;
	}

<YYINITIAL> "ORF"{WHITE_SPACE_CHAR}*{DIGIT}+ / {TERMINATION}|{WHITE_SPACE_CHAR} {
	return biomedicalToken.WORD_Token;
	}

<YYINITIAL> "UCL"{WHITE_SPACE_CHAR}*{DIGIT}+ / {TERMINATION}|{WHITE_SPACE_CHAR} {
	return biomedicalToken.WORD_Token;
	}

<YYINITIAL> "SB/"{DIGIT}+ / {TERMINATION}|{WHITE_SPACE_CHAR} {
	return biomedicalToken.WORD_Token;
	}

<YYINITIAL> "Sch"{WHITE_SPACE_CHAR}*{DIGIT}+ / {TERMINATION}|{WHITE_SPACE_CHAR} {
	return biomedicalToken.WORD_Token;
	}

<YYINITIAL> "RMI"{WHITE_SPACE_CHAR}+{DIGIT}+{WHITE_SPACE_CHAR}+{DIGIT}+ / {TERMINATION}|{WHITE_SPACE_CHAR} {
	return biomedicalToken.WORD_Token;
	}

<YYINITIAL> (([0-9]{GRANT_SEPARATOR}?)?{GRANT_TYPE}{GRANT_SEPARATOR}?)?{IC_CODE}{GRANT_SEPARATOR}?[0-9]{3,6}({GRANT_SEPARATOR}[0-9]{1,2})?({GRANT_SEPARATOR}?[AS][0-9])?(X[0-9])? / {TERMINATION}|{WHITE_SPACE_CHAR} {
	return biomedicalToken.GRANT_Token;
	}

<YYINITIAL> ("ftp"|("http"s?))"://"[-_a-zA-Z0-9]+("."[-_a-zA-Z0-9]+)+("/"[-_.a-zA-Z0-9]*[a-zA-Z0-9])* / {TERMINATION}|{WHITE_SPACE_CHAR} {
	return biomedicalToken.URL_Token;
	}

<YYINITIAL> [-_a-zA-Z0-9]+("."[-_a-zA-Z0-9]+)*("."{TOP_LEVEL_DOMAIN})("/"[-_.a-zA-Z0-9]*[a-zA-Z0-9])* / {TERMINATION}|{WHITE_SPACE_CHAR} {
	return biomedicalToken.URL_Token;
	}

<YYINITIAL> [-_.a-zA-Z0-9]+"@"[-_a-zA-Z0-9]+("."[-_a-zA-Z0-9]+)*"."{TOP_LEVEL_DOMAIN} / {TERMINATION}|{WHITE_SPACE_CHAR} {
	return biomedicalToken.EMAIL_Token;
	}

<YYINITIAL> {OPERATOR}? {NUMBER}({WHITE_SPACE_CHAR}*{OPERATOR}{WHITE_SPACE_CHAR}*{EXPONENT})? {WHITE_SPACE_CHAR}* ({UNIT}({WHITE_SPACE_CHAR}*"/"{WHITE_SPACE_CHAR}*{UNIT})*)? / {TERMINATION}|{WHITE_SPACE_CHAR} {
	return biomedicalToken.NUMBER_Token;
	}

<YYINITIAL> {NUMBER}"-"("times"|"fold"|{UNIT})"-old"? / {TERMINATION}|{WHITE_SPACE_CHAR} {
	return biomedicalToken.NUMBER_Token;
	}

<YYINITIAL> {DIGIT}+("/"|{HYPHEN}|":"){DIGIT}+ / {TERMINATION}|{WHITE_SPACE_CHAR} {
	return biomedicalToken.NUMBER_Token;
	}

<YYINITIAL> ("pH"{WHITE_SPACE_CHAR}*)?{DIGIT}+("."{DIGIT}+)?"%"?{WHITE_SPACE_CHAR}*("+/-"|"plus"{WHITE_SPACE_CHAR}+"or"{WHITE_SPACE_CHAR}+"minus"|{WHITE_SPACE_CHAR}"to"{WHITE_SPACE_CHAR}|"-"+){WHITE_SPACE_CHAR}*{DIGIT}+("."{DIGIT}+)?"%"? {WHITE_SPACE_CHAR}* (({UNIT}{WHITE_SPACE_CHAR}*)+("/"{WHITE_SPACE_CHAR}*{UNIT})*)? / {TERMINATION}|{WHITE_SPACE_CHAR} {
	return biomedicalToken.NUMBER_Token;
	}

<YYINITIAL> {DIGIT}{1,3}(","{DIGIT}{1,3})*("."{DIGIT}+)?("-"{DIGIT}+("."{DIGIT}+)?)?"%"? / {TERMINATION}|{WHITE_SPACE_CHAR} {
	return biomedicalToken.NUMBER_Token;
	}

<YYINITIAL> {OPEN_GROUPING}{NOT_CLOSE_GROUPING}+{CLOSE_GROUPING}"-"?{ALPHA_NUMERIC}+ / {TERMINATION}|{WHITE_SPACE_CHAR} {
	return biomedicalToken.COMPOUND_Token;
	}	

<YYINITIAL> {OPEN_GROUPING} / {ALPHA_NUMERIC}+("-"{ALPHA_NUMERIC}+)*"'s"?{TERMINATION}|{WHITE_SPACE_CHAR} {
	return biomedicalToken.GROUPING_Token;
	}

<YYINITIAL> {OPEN_GROUPING} / ({UPPER}"."({UPPER}".")+)","?|({LOWER}"."({LOWER}".")+)","? {
	return biomedicalToken.GROUPING_Token;
	}

<YYINITIAL> {OPEN_GROUPING} / {DIGIT}+"."{DIGIT}+{TERMINATION}|{WHITE_SPACE_CHAR} {
	return biomedicalToken.GROUPING_Token;
	}

<YYINITIAL> {OPEN_GROUPING} / {DIGIT}{1,3}(","{DIGIT}{1,3})*("."{DIGIT}+)?("-"{DIGIT}+("."{DIGIT}+)?)?"%"?{TERMINATION}|{WHITE_SPACE_CHAR} {
	return biomedicalToken.GROUPING_Token;
	}

<YYINITIAL> {OPEN_GROUPING} / {NOT_CLOSE_GROUPING}*{TERMINATION}|{WHITE_SPACE_CHAR} {
	return biomedicalToken.GROUPING_Token;
	}

<YYINITIAL> {CLOSE_GROUPING} {
	return biomedicalToken.GROUPING_Token;
	}

<YYINITIAL> {OPEN_QUOTE} / {ALPHA_NUMERIC}+{CLOSE_QUOTE}*{WHITE_SPACE_CHAR} {
	return biomedicalToken.QUOTE_Token;
	}

<YYINITIAL> {CLOSE_QUOTE} {
	return biomedicalToken.QUOTE_Token;
	}

<YYINITIAL> {NAME_INITIALS} / {TERMINATION}|{WHITE_SPACE_CHAR} {
	return biomedicalToken.WORD_Token;
	}

<YYINITIAL> {UNIT}("/"({UNIT}|{DIGIT}+("."{DIGIT}+)?))+  / {TERMINATION}|{WHITE_SPACE_CHAR} {
	return biomedicalToken.UNIT_Token;
	}

<YYINITIAL> {UNIT}"/"{ALPHA}+  / {TERMINATION}|{WHITE_SPACE_CHAR} {
	return biomedicalToken.UNIT_Token;
	}

<YYINITIAL> {ALPHA}+"/"{UNIT}  / {TERMINATION}|{WHITE_SPACE_CHAR} {
	return biomedicalToken.UNIT_Token;
	}

<YYINITIAL> {UNIT}("."{UNIT}("-1"|"(-1)")?)+  / {TERMINATION}|{WHITE_SPACE_CHAR} {
	return biomedicalToken.UNIT_Token;
	}

<YYINITIAL> ({SAFE_ELEMENT}({OPEN_GROUPING}{DIGIT}+[-+]?{CLOSE_GROUPING}|{DIGIT}+)?[-+]*)+ / {TERMINATION}|{WHITE_SPACE_CHAR} {
	return biomedicalToken.COMPOUND_Token;
	}	

<YYINITIAL> {DIGITP}+{PRIME}?([,:]{DIGITP}+{PRIME}?)*("-"{ALPHA}+)+ / {TERMINATION}|{WHITE_SPACE_CHAR} {
	return biomedicalToken.COMPOUND_Token;
	}	

<YYINITIAL> ({UPPER}"."({UPPER}".")+)|({LOWER}"."({LOWER}".")+)|"Ph.D."|"and/or"|"or/and"|"vs."|"etc."|"mol.wt."|"mol. wt."|"mol.wts."|"vol."|"approx."|"viz."|"cv."|"ca."|"b.i.d."|"resp."|"et""."?{WHITE_SPACE_CHAR}*"al." / {TERMINATION}|{WHITE_SPACE_CHAR} {
	return biomedicalToken.WORD_Token;
	}

<YYINITIAL> ((("f."{WHITE_SPACE_CHAR}+)?("subsp." | "ssp." | "sp." | "spp.")) | "var." | "chemovar." | "cf." | "aff." | "str." | "nov." | "bv." | "pv." | "et al." | "emend." | "subgen." | "corrig." | "genomsp." | "Sh." ) / {TERMINATION}|{WHITE_SPACE_CHAR} {
	return biomedicalToken.WORD_Token;
	}

<YYINITIAL> ([iI]"n")|([eE]"x") {WHITE_SPACE_CHAR}+ ( "vivo" | "vitro" | "silico" | "situ" | "utero" | "papyro" | "planta" | "natura" ) / {TERMINATION}|{WHITE_SPACE_CHAR} {
	return biomedicalToken.WORD_Token;
	}

<YYINITIAL> "non-"? ( "v/v" | "ob/ob" | "w/v" | "m/z" | "op/op" | "d"[pP]"/dt" ) / {TERMINATION}|{WHITE_SPACE_CHAR} {
	return biomedicalToken.WORD_Token;
	}

<YYINITIAL> {ALPHA}+ / "--"{ALPHA}+{TERMINATION}|{WHITE_SPACE_CHAR} {
	return biomedicalToken.WORD_Token;
	}	

<YYINITIAL> "--" / {ALPHA}+{TERMINATION}|{WHITE_SPACE_CHAR} {
	return biomedicalToken.PUNC_Token;
	}	

<YYINITIAL> {ALPHA_NUMERIC}+({HYPHEN}{ALPHA_NUMERIC}+)* / (['\u2019]s)?({TERMINATION}|{WHITE_SPACE_CHAR}) {
	return biomedicalToken.WORD_Token;
	}	

<YYINITIAL> {ALPHA} {ALPHA_NUMERIC}* [(\[{] [^(\[{)\]}]+ [)\]}] {ALPHA}* / {TERMINATION}|{WHITE_SPACE_CHAR} {
	return biomedicalToken.WORD_Token;
	}	

<YYINITIAL> {ALPHA_NUMERIC}+[-+] / {TERMINATION}|{WHITE_SPACE_CHAR} {
	return biomedicalToken.WORD_Token;
	}	

<YYINITIAL> {ALPHA}+"'"("t"|"d"|"m"|"ve"|"ll"|"re"|"nt"|"T"|"D"|"M"|"VE"|"LL"|"RE"|"NT") / {TERMINATION}|{WHITE_SPACE_CHAR} {
	return biomedicalToken.WORD_Token;
	}	

<YYINITIAL> ({ELEMENT}({OPEN_GROUPING}{DIGIT}+[-+]?{CLOSE_GROUPING}|{DIGIT}+)?[-+]*)+("-"{ALPHA}+)? / {TERMINATION}|{WHITE_SPACE_CHAR} {
	return biomedicalToken.COMPOUND_Token;
	}	

<YYINITIAL> ({ELEMENT}[-+]*{DIGIT}*{SUBSCRIPT}*[-+]*)+([-+:/@]{ELEMENT}[-+]*{DIGIT}*{SUBSCRIPT}*[-+]*)*([-+:/]{ALPHA}+)* / {TERMINATION}|{WHITE_SPACE_CHAR} {
	return biomedicalToken.COMPOUND_Token;
	}	

<YYINITIAL> {ELEMENT}(({OPEN_GROUPING}{DIGIT}*[-+]?{CLOSE_GROUPING}|{DIGIT}+)?[-+]*)?(([-\u22ef,]|"."+)({ELEMENT}(({OPEN_GROUPING}{DIGIT}*[-+]?{CLOSE_GROUPING}|{DIGIT}+)?[-+]*)?|"\u03c0"|[pP][iI]))+("-"{ALPHA}+)? / {TERMINATION}|{WHITE_SPACE_CHAR} {
	return biomedicalToken.COMPOUND_Token;
	}	

<YYINITIAL> {ALPHA}{1,2} {WHITE_SPACE_CHAR}* {SAFE_OPERATOR} {WHITE_SPACE_CHAR}* ({ALPHA}{1,2}|{NUMBER}+{WHITE_SPACE_CHAR}*{UNIT}?)  / {TERMINATION}|{WHITE_SPACE_CHAR} {
	return biomedicalToken.EQUATION_Token;
	}
/*
<YYINITIAL> ({NUMBER} {WHITE_SPACE_CHAR}*)? {ALPHA}{1,2} {WHITE_SPACE_CHAR}* {SAFE_OPERATOR}+ {WHITE_SPACE_CHAR}* ({ALPHA}{1,2}|{NUMBER}+{WHITE_SPACE_CHAR}*{UNIT}?) {WHITE_SPACE_CHAR}* ({OPERATOR}+ {WHITE_SPACE_CHAR}* ({ALPHA}{1,2}|{NUMBER}+{WHITE_SPACE_CHAR}*{UNIT}?))* / {TERMINATION}|{WHITE_SPACE_CHAR} {
	return biomedicalToken.EQUATION_Token;
	}
*/
<YYINITIAL> {OPERATOR} / {TERMINATION}|{WHITE_SPACE_CHAR} {
	return biomedicalToken.OP_Token;
	}

<YYINITIAL> {CURRENCY}{WHITE_SPACE_CHAR}*{DIGIT}{1,3}((","|{WHITE_SPACE_CHAR}+){DIGIT}{3,3})*("."{DIGIT}+)? / {WHITE_SPACE_CHAR} {
	return biomedicalToken.CURRENCY_Token;
	}

<YYINITIAL> {WHITE_SPACE_CHAR}+ {
	//return biomedicalToken.OTHER_Token," ",yyline));
	}

<YYINITIAL> {PUNCTUATION} {
	return biomedicalToken.PUNC_Token;
	}

<YYINITIAL> {SUPERSCRIPT} / {TERMINATION}|{WHITE_SPACE_CHAR} {
	return biomedicalToken.SUPERSCRIPT_Token;
	}

<YYINITIAL> {MOLECULE}("-"{MOLECULE})+ / {TERMINATION}|{WHITE_SPACE_CHAR} {
	return biomedicalToken.COMPOUND_Token;
	}

<YYINITIAL> {MOLECULE}("-"({ALPHA}+|{COMP_CHAR_SEQ}+|{ALPHA}*{OPEN_GROUPING}{MOLECULE}("-"{MOLECULE})+{CLOSE_GROUPING}{ALPHA}*))+ / {TERMINATION}|{WHITE_SPACE_CHAR} {
	return biomedicalToken.COMPOUND_Token;
	}

<YYINITIAL> {MOLECULE}("-"({ALPHA}+|{COMP_CHAR_SEQ}+|{ALPHA}*{OPEN_GROUPING}{MOLECULE}("-"({ALPHA}+|{COMP_CHAR_SEQ}+|{ALPHA}*{OPEN_GROUPING}{MOLECULE}("-"{MOLECULE})+{CLOSE_GROUPING}{ALPHA}*))+{CLOSE_GROUPING}{ALPHA}*))+ / {TERMINATION}|{WHITE_SPACE_CHAR} {
	return biomedicalToken.COMPOUND_Token;
	}

<YYINITIAL> {NUMBER}{WHITE_SPACE_CHAR}*"/"{WHITE_SPACE_CHAR}*{UNIT}("/"({UNIT}|{DIGIT}+("."{DIGIT}+)?))+ / {TERMINATION}|{WHITE_SPACE_CHAR} {
	return biomedicalToken.NUMBER_Token;
	}

<YYINITIAL> {NUMBER}{OPERATOR}{NUMBER} / {TERMINATION}|{WHITE_SPACE_CHAR} {
	return biomedicalToken.NUMBER_Token;
	}

<YYINITIAL> {COMP_CHAR_SEQ}"-"?{OPEN_GROUPING}{ELEMENT}+{CLOSE_GROUPING}{ALPHA_NUMERIC}* / {TERMINATION}|{WHITE_SPACE_CHAR} {
	return biomedicalToken.COMPOUND_Token;
	}

<YYINITIAL> {UPPER}?{LOWER}*([-/]{LOWER}+)+ / {TERMINATION}|{WHITE_SPACE_CHAR} {
	return biomedicalToken.WORD_Token;
	}

<YYINITIAL> ((O|l|L|d|D|dell)['\u2019])?{ALPHA}+ / (['\u2019]s)|{TERMINATION}|{WHITE_SPACE_CHAR} {
	return biomedicalToken.WORD_Token;
	}

<YYINITIAL> {NAME_TITLE_ABBREV}|{NAME_TITLE_SUFFIX}|{NAME_INITIALS}|{ORG_ABBREV} / {TERMINATION}|{WHITE_SPACE_CHAR} {
	return biomedicalToken.WORD_Token;
	}

<YYINITIAL> {ALPHA_NUMERIC}+ / {TERMINATION}|{WHITE_SPACE_CHAR} {
	return biomedicalToken.WORD_Token;
	}

<YYINITIAL> {ALPHA_NUMERIC}+([+-./&]{ALPHA_NUMERIC}+)+ / {TERMINATION}|{WHITE_SPACE_CHAR} {
	return biomedicalToken.WORD_Token;
	}

<YYINITIAL> ({ALPHA}[-+./&])+ {
	return biomedicalToken.WORD_Token;
	}

<YYINITIAL> [\u2116]|[nN][\u030a\u00b0\u2070\u00ba\u0366\u1d52] / {TERMINATION}|{WHITE_SPACE_CHAR} {
	return biomedicalToken.WORD_Token;
	}

<YYINITIAL> {DIGIT}+[;]{DIGIT}+([(\[]{DIGIT}+[)\]])?[:][e]?{DIGIT}+({HYPHEN}[e]?{DIGIT}+)? / {TERMINATION}|{WHITE_SPACE_CHAR} {
	return biomedicalToken.WORD_Token;
	}

<YYINITIAL> {NON_WHITE_SPACE_CHAR} {
	return biomedicalToken.OTHER_Token;
	}
