
//----------------------------------------------------
// The following code was generated by CUP v0.11a beta 20060608
// Thu Aug 23 13:34:10 CDT 2018
//----------------------------------------------------

package edu.uiowa.lucene.conceptSearch;

import java.lang.reflect.Method;
import java.util.*;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/** CUP v0.11a beta 20060608 generated parser.
  * @version Thu Aug 23 13:34:10 CDT 2018
  */
public class ConceptParseCup extends java_cup.runtime.lr_parser {

  /** Default constructor. */
  public ConceptParseCup() {super();}

  /** Constructor which sets the default scanner. */
  public ConceptParseCup(java_cup.runtime.Scanner s) {super(s);}

  /** Constructor which sets the default scanner. */
  public ConceptParseCup(java_cup.runtime.Scanner s, java_cup.runtime.SymbolFactory sf) {super(s,sf);}

  /** Production table. */
  protected static final short _production_table[][] = 
    unpackFromStrings(new String[] {
    "\000\012\000\002\002\004\000\002\002\003\000\002\003" +
    "\002\000\002\003\005\000\002\003\003\000\002\003\005" +
    "\000\002\003\005\000\002\003\004\000\002\004\004\000" +
    "\002\004\003" });

  /** Access to production table. */
  public short[][] production_table() {return _production_table;}

  /** Parse-action table. */
  protected static final short[][] _action_table = 
    unpackFromStrings(new String[] {
    "\000\020\000\016\002\uffff\004\006\006\004\007\uffff\010" +
    "\uffff\011\007\001\002\000\014\002\ufff8\005\ufff8\006\ufff8" +
    "\007\ufff8\010\ufff8\001\002\000\004\002\022\001\002\000" +
    "\016\004\006\005\uffff\006\004\007\uffff\010\uffff\011\007" +
    "\001\002\000\020\002\uffff\004\006\005\uffff\006\004\007" +
    "\uffff\010\uffff\011\007\001\002\000\014\002\ufffd\005\ufffd" +
    "\006\016\007\ufffd\010\ufffd\001\002\000\010\002\000\007" +
    "\013\010\012\001\002\000\020\002\uffff\004\006\005\uffff" +
    "\006\004\007\uffff\010\uffff\011\007\001\002\000\020\002" +
    "\uffff\004\006\005\uffff\006\004\007\uffff\010\uffff\011\007" +
    "\001\002\000\012\002\ufffc\005\ufffc\007\ufffc\010\ufffc\001" +
    "\002\000\012\002\ufffb\005\ufffb\007\013\010\ufffb\001\002" +
    "\000\014\002\ufff9\005\ufff9\006\ufff9\007\ufff9\010\ufff9\001" +
    "\002\000\012\002\ufffa\005\ufffa\007\ufffa\010\ufffa\001\002" +
    "\000\010\005\021\007\013\010\012\001\002\000\012\002" +
    "\ufffe\005\ufffe\007\ufffe\010\ufffe\001\002\000\004\002\001" +
    "\001\002" });

  /** Access to parse-action table. */
  public short[][] action_table() {return _action_table;}

  /** <code>reduce_goto</code> table. */
  protected static final short[][] _reduce_table = 
    unpackFromStrings(new String[] {
    "\000\020\000\010\002\004\003\010\004\007\001\001\000" +
    "\002\001\001\000\002\001\001\000\006\003\017\004\007" +
    "\001\001\000\006\003\016\004\007\001\001\000\002\001" +
    "\001\000\002\001\001\000\006\003\014\004\007\001\001" +
    "\000\006\003\013\004\007\001\001\000\002\001\001\000" +
    "\002\001\001\000\002\001\001\000\002\001\001\000\002" +
    "\001\001\000\002\001\001\000\002\001\001" });

  /** Access to <code>reduce_goto</code> table. */
  public short[][] reduce_table() {return _reduce_table;}

  /** Instance of action encapsulation class. */
  protected CUP$ConceptParseCup$actions action_obj;

  /** Action encapsulation object initializer. */
  protected void init_actions()
    {
      action_obj = new CUP$ConceptParseCup$actions(this);
    }

  /** Invoke a user supplied parse action. */
  public java_cup.runtime.Symbol do_action(
    int                        act_num,
    java_cup.runtime.lr_parser parser,
    java.util.Stack            stack,
    int                        top)
    throws java.lang.Exception
  {
    /* call code in generated class */
    return action_obj.CUP$ConceptParseCup$do_action(act_num, parser, stack, top);
  }

  /** Indicates start state. */
  public int start_state() {return 0;}
  /** Indicates start production. */
  public int start_production() {return 0;}

  /** <code>EOF</code> Symbol index. */
  public int EOF_sym() {return 0;}

  /** <code>error</code> Symbol index. */
  public int error_sym() {return 1;}



    static final Log log =LogFactory.getLog(ConceptParseCup.class);

        public static void main(String args[]) throws Exception {
                new ConceptParseCup(new conceptParseFlex(System.in)).parse();
        }

}

/** Cup generated class to encapsulate user supplied action code.*/
class CUP$ConceptParseCup$actions {
  private final ConceptParseCup parser;

  /** Constructor */
  CUP$ConceptParseCup$actions(ConceptParseCup parser) {
    this.parser = parser;
  }

  /** Method with the actual generated action code. */
  public final java_cup.runtime.Symbol CUP$ConceptParseCup$do_action(
    int                        CUP$ConceptParseCup$act_num,
    java_cup.runtime.lr_parser CUP$ConceptParseCup$parser,
    java.util.Stack            CUP$ConceptParseCup$stack,
    int                        CUP$ConceptParseCup$top)
    throws java.lang.Exception
    {
      /* Symbol object for return from actions */
      java_cup.runtime.Symbol CUP$ConceptParseCup$result;

      /* select the action based on the action number */
      switch (CUP$ConceptParseCup$act_num)
        {
          /*. . . . . . . . . . . . . . . . . . . .*/
          case 9: // TokenList ::= String 
            {
              Vector<String> RESULT =null;
		int pleft = ((java_cup.runtime.Symbol)CUP$ConceptParseCup$stack.peek()).left;
		int pright = ((java_cup.runtime.Symbol)CUP$ConceptParseCup$stack.peek()).right;
		Object p = (Object)((java_cup.runtime.Symbol) CUP$ConceptParseCup$stack.peek()).value;
			Vector<String> list = new Vector<String>();
	 								   		list.addElement((String)p);
	 								   		RESULT = list;
									   		ConceptParseCup.log.debug("\ttoken list first token: " + RESULT);
	 								 	
              CUP$ConceptParseCup$result = parser.getSymbolFactory().newSymbol("TokenList",2, ((java_cup.runtime.Symbol)CUP$ConceptParseCup$stack.peek()), ((java_cup.runtime.Symbol)CUP$ConceptParseCup$stack.peek()), RESULT);
            }
          return CUP$ConceptParseCup$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 8: // TokenList ::= TokenList String 
            {
              Vector<String> RESULT =null;
		int p1left = ((java_cup.runtime.Symbol)CUP$ConceptParseCup$stack.elementAt(CUP$ConceptParseCup$top-1)).left;
		int p1right = ((java_cup.runtime.Symbol)CUP$ConceptParseCup$stack.elementAt(CUP$ConceptParseCup$top-1)).right;
		Vector<String> p1 = (Vector<String>)((java_cup.runtime.Symbol) CUP$ConceptParseCup$stack.elementAt(CUP$ConceptParseCup$top-1)).value;
		int p2left = ((java_cup.runtime.Symbol)CUP$ConceptParseCup$stack.peek()).left;
		int p2right = ((java_cup.runtime.Symbol)CUP$ConceptParseCup$stack.peek()).right;
		Object p2 = (Object)((java_cup.runtime.Symbol) CUP$ConceptParseCup$stack.peek()).value;
			p1.addElement((String)p2);
									   		RESULT = p1;
									   		ConceptParseCup.log.debug("\ttoken list: " + RESULT);
	 								 	
              CUP$ConceptParseCup$result = parser.getSymbolFactory().newSymbol("TokenList",2, ((java_cup.runtime.Symbol)CUP$ConceptParseCup$stack.elementAt(CUP$ConceptParseCup$top-1)), ((java_cup.runtime.Symbol)CUP$ConceptParseCup$stack.peek()), RESULT);
            }
          return CUP$ConceptParseCup$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 7: // Pat ::= NOT_OP Pat 
            {
              Query RESULT =null;
		int pleft = ((java_cup.runtime.Symbol)CUP$ConceptParseCup$stack.peek()).left;
		int pright = ((java_cup.runtime.Symbol)CUP$ConceptParseCup$stack.peek()).right;
		Query p = (Query)((java_cup.runtime.Symbol) CUP$ConceptParseCup$stack.peek()).value;
			Query theQuery = new BooleanQuery();
						((BooleanQuery)theQuery).add(p, BooleanClause.Occur.MUST_NOT);
						RESULT = theQuery;
						ConceptParseCup.log.debug("\tnot query: " + RESULT);
					
              CUP$ConceptParseCup$result = parser.getSymbolFactory().newSymbol("Pat",1, ((java_cup.runtime.Symbol)CUP$ConceptParseCup$stack.elementAt(CUP$ConceptParseCup$top-1)), ((java_cup.runtime.Symbol)CUP$ConceptParseCup$stack.peek()), RESULT);
            }
          return CUP$ConceptParseCup$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 6: // Pat ::= Pat OR_OP Pat 
            {
              Query RESULT =null;
		int leftPatleft = ((java_cup.runtime.Symbol)CUP$ConceptParseCup$stack.elementAt(CUP$ConceptParseCup$top-2)).left;
		int leftPatright = ((java_cup.runtime.Symbol)CUP$ConceptParseCup$stack.elementAt(CUP$ConceptParseCup$top-2)).right;
		Query leftPat = (Query)((java_cup.runtime.Symbol) CUP$ConceptParseCup$stack.elementAt(CUP$ConceptParseCup$top-2)).value;
		int rightPatleft = ((java_cup.runtime.Symbol)CUP$ConceptParseCup$stack.peek()).left;
		int rightPatright = ((java_cup.runtime.Symbol)CUP$ConceptParseCup$stack.peek()).right;
		Query rightPat = (Query)((java_cup.runtime.Symbol) CUP$ConceptParseCup$stack.peek()).value;
			Query theQuery = new BooleanQuery();
											((BooleanQuery)theQuery).add(leftPat, BooleanClause.Occur.SHOULD);
											((BooleanQuery)theQuery).add(rightPat, BooleanClause.Occur.SHOULD);
											RESULT = theQuery;
											ConceptParseCup.log.debug("\tor query: " + RESULT);
										
              CUP$ConceptParseCup$result = parser.getSymbolFactory().newSymbol("Pat",1, ((java_cup.runtime.Symbol)CUP$ConceptParseCup$stack.elementAt(CUP$ConceptParseCup$top-2)), ((java_cup.runtime.Symbol)CUP$ConceptParseCup$stack.peek()), RESULT);
            }
          return CUP$ConceptParseCup$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 5: // Pat ::= Pat AND_OP Pat 
            {
              Query RESULT =null;
		int leftPatleft = ((java_cup.runtime.Symbol)CUP$ConceptParseCup$stack.elementAt(CUP$ConceptParseCup$top-2)).left;
		int leftPatright = ((java_cup.runtime.Symbol)CUP$ConceptParseCup$stack.elementAt(CUP$ConceptParseCup$top-2)).right;
		Query leftPat = (Query)((java_cup.runtime.Symbol) CUP$ConceptParseCup$stack.elementAt(CUP$ConceptParseCup$top-2)).value;
		int rightPatleft = ((java_cup.runtime.Symbol)CUP$ConceptParseCup$stack.peek()).left;
		int rightPatright = ((java_cup.runtime.Symbol)CUP$ConceptParseCup$stack.peek()).right;
		Query rightPat = (Query)((java_cup.runtime.Symbol) CUP$ConceptParseCup$stack.peek()).value;
			Query theQuery = new BooleanQuery();
											((BooleanQuery)theQuery).add(leftPat, BooleanClause.Occur.MUST);
											((BooleanQuery)theQuery).add(rightPat, BooleanClause.Occur.MUST);
											RESULT = theQuery;
											ConceptParseCup.log.debug("\tand query: " + RESULT);
										
              CUP$ConceptParseCup$result = parser.getSymbolFactory().newSymbol("Pat",1, ((java_cup.runtime.Symbol)CUP$ConceptParseCup$stack.elementAt(CUP$ConceptParseCup$top-2)), ((java_cup.runtime.Symbol)CUP$ConceptParseCup$stack.peek()), RESULT);
            }
          return CUP$ConceptParseCup$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 4: // Pat ::= TokenList 
            {
              Query RESULT =null;
		int pleft = ((java_cup.runtime.Symbol)CUP$ConceptParseCup$stack.peek()).left;
		int pright = ((java_cup.runtime.Symbol)CUP$ConceptParseCup$stack.peek()).right;
		Vector<String> p = (Vector<String>)((java_cup.runtime.Symbol) CUP$ConceptParseCup$stack.peek()).value;
			StringBuffer buffer = new StringBuffer();
						for (String e : p) {
							buffer.append(e + " ");
						}
						Class<?> theAnalyzerClass = Class.forName("edu.uiowa.emr.ConceptRecognizer");
						Object theAnalyzer = theAnalyzerClass.newInstance();
						Class[] paramString = new Class[1];	
						paramString[0] = String.class;
						Method method = theAnalyzerClass.getDeclaredMethod("analyzeSentenceAndClose", paramString);
						String resultBuffer = (String)method.invoke(theAnalyzer, buffer.toString());
						QueryParser theQueryParser = new QueryParser(org.apache.lucene.util.Version.LUCENE_30, "content", new StandardAnalyzer(org.apache.lucene.util.Version.LUCENE_30));
						Query theQuery = theQueryParser.parse(resultBuffer);            	
						RESULT = theQuery;
						ConceptParseCup.log.debug("\ttoken string: " + resultBuffer + "\tquery: " + RESULT);
					
              CUP$ConceptParseCup$result = parser.getSymbolFactory().newSymbol("Pat",1, ((java_cup.runtime.Symbol)CUP$ConceptParseCup$stack.peek()), ((java_cup.runtime.Symbol)CUP$ConceptParseCup$stack.peek()), RESULT);
            }
          return CUP$ConceptParseCup$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 3: // Pat ::= LPAREN Pat RPAREN 
            {
              Query RESULT =null;
		int pleft = ((java_cup.runtime.Symbol)CUP$ConceptParseCup$stack.elementAt(CUP$ConceptParseCup$top-1)).left;
		int pright = ((java_cup.runtime.Symbol)CUP$ConceptParseCup$stack.elementAt(CUP$ConceptParseCup$top-1)).right;
		Query p = (Query)((java_cup.runtime.Symbol) CUP$ConceptParseCup$stack.elementAt(CUP$ConceptParseCup$top-1)).value;
		 	RESULT = p;
									ConceptParseCup.log.info("\tgrouped query: " + RESULT);
								
              CUP$ConceptParseCup$result = parser.getSymbolFactory().newSymbol("Pat",1, ((java_cup.runtime.Symbol)CUP$ConceptParseCup$stack.elementAt(CUP$ConceptParseCup$top-2)), ((java_cup.runtime.Symbol)CUP$ConceptParseCup$stack.peek()), RESULT);
            }
          return CUP$ConceptParseCup$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 2: // Pat ::= 
            {
              Query RESULT =null;

              CUP$ConceptParseCup$result = parser.getSymbolFactory().newSymbol("Pat",1, ((java_cup.runtime.Symbol)CUP$ConceptParseCup$stack.peek()), ((java_cup.runtime.Symbol)CUP$ConceptParseCup$stack.peek()), RESULT);
            }
          return CUP$ConceptParseCup$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 1: // top ::= Pat 
            {
              Query RESULT =null;
		int pleft = ((java_cup.runtime.Symbol)CUP$ConceptParseCup$stack.peek()).left;
		int pright = ((java_cup.runtime.Symbol)CUP$ConceptParseCup$stack.peek()).right;
		Query p = (Query)((java_cup.runtime.Symbol) CUP$ConceptParseCup$stack.peek()).value;
		 RESULT = p;
					   ConceptParseCup.log.info("\ttop: " + RESULT);
					 
              CUP$ConceptParseCup$result = parser.getSymbolFactory().newSymbol("top",0, ((java_cup.runtime.Symbol)CUP$ConceptParseCup$stack.peek()), ((java_cup.runtime.Symbol)CUP$ConceptParseCup$stack.peek()), RESULT);
            }
          return CUP$ConceptParseCup$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 0: // $START ::= top EOF 
            {
              Object RESULT =null;
		int start_valleft = ((java_cup.runtime.Symbol)CUP$ConceptParseCup$stack.elementAt(CUP$ConceptParseCup$top-1)).left;
		int start_valright = ((java_cup.runtime.Symbol)CUP$ConceptParseCup$stack.elementAt(CUP$ConceptParseCup$top-1)).right;
		Query start_val = (Query)((java_cup.runtime.Symbol) CUP$ConceptParseCup$stack.elementAt(CUP$ConceptParseCup$top-1)).value;
		RESULT = start_val;
              CUP$ConceptParseCup$result = parser.getSymbolFactory().newSymbol("$START",0, ((java_cup.runtime.Symbol)CUP$ConceptParseCup$stack.elementAt(CUP$ConceptParseCup$top-1)), ((java_cup.runtime.Symbol)CUP$ConceptParseCup$stack.peek()), RESULT);
            }
          /* ACCEPT */
          CUP$ConceptParseCup$parser.done_parsing();
          return CUP$ConceptParseCup$result;

          /* . . . . . .*/
          default:
            throw new Exception(
               "Invalid action number found in internal parse table");

        }
    }
}

