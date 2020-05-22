package edu.uiowa.lucene.ld4lSearch;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.miscellaneous.ASCIIFoldingFilter;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.util.Version;

public class LD4LAnalyzer extends Analyzer {
    static Logger logger = Logger.getLogger(LD4LAnalyzer.class);
    static LD4LAnalyzer analyzer = null;
    
    boolean doStemming = false;
    
    public static void main(String[] args) throws IOException {
        PropertyConfigurator.configure("/Users/eichmann/Documents/Components/log4j.info");
        analyzer = new LD4LAnalyzer();

        analyze("Grulke, Markus");
        analyze("Petrović, Dijana, 1950-");
        analyze("Ehemalige Karmelitenkirche (Munich, Germany)");
        analyze("Drausch, Valentin, 1546-1610?");
        analyze("Research resources (Brooklyn, N.Y.)");
        analyze("Colecção Cadernos de investigação");
        analyze("AMS Special Session on Topology in Dynamics (1998 : Winston-Salem, N.C.)");
        analyze("Alexander-Marrack, P.");
        analyze("Vermeulen-Breedt, Marié, 1954-");

        analyzer.close();
    }
    
    public LD4LAnalyzer() {
	super();
    }
    
    public LD4LAnalyzer(boolean doStemming) {
	super();
	this.doStemming = doStemming;
    }
    
    static void analyze(String input) throws IOException {
    	logger.info("input:" + input);
	TokenStream stream = analyzer.tokenStream(null, new StringReader(input));
	CharTermAttribute cattr = stream.addAttribute(CharTermAttribute.class);
	stream.reset();
	while (stream.incrementToken()) {
	  logger.info("\ttoken: " + cattr.toString());
	  cattr.setEmpty();
	}
	stream.end();
	stream.close();
    }
    
    protected TokenStreamComponents createComponents(String arg0, Reader arg1) {
	LD4LTokenizer src = new LD4LTokenizer(arg1);
        TokenStream result = src; //new StandardFilter(Version.LUCENE_43, src);
        result = new ASCIIFoldingFilter(result); //note later versions than 4.3 include an additional boolean argument to flag whether to preserve the original token
        result = new LowerCaseFilter(Version.LUCENE_43, result);
        result = new StopFilter(Version.LUCENE_43, result,  StandardAnalyzer.STOP_WORDS_SET);
        if (doStemming)
            result = new PorterStemFilter(result);
        return new TokenStreamComponents(src, result);
    }
}
