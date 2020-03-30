package edu.uiowa.lucene.biomedical;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.miscellaneous.CapitalizationFilter;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.util.Version;

public class BiomedicalAnalyzer extends Analyzer {
    static Logger logger = Logger.getLogger(BiomedicalAnalyzer.class);
    
    public static void main(String[] args) throws IOException {
        PropertyConfigurator.configure("/Users/eichmann/Documents/Components/log4j.info");
	BiomedicalAnalyzer analyzer = new BiomedicalAnalyzer();
	TokenStream stream = analyzer.tokenStream(null, new StringReader("Eventually Bringing a tests"));
	CharTermAttribute cattr = stream.addAttribute(CharTermAttribute.class);
	stream.reset();
	while (stream.incrementToken()) {
	  logger.info("token: " + cattr.toString());
	  cattr.setEmpty();
	}
	stream.end();
	stream.close();
	analyzer.close();
    }

    @Override
    protected TokenStreamComponents createComponents(String arg0, Reader arg1) {
        BiomedicalTokenizer src = new BiomedicalTokenizer(arg1);
        TokenStream result = src; //new StandardFilter(Version.LUCENE_43, src);
        result = new LowerCaseFilter(Version.LUCENE_43, result);
//        result = new StopFilter(Version.LUCENE_43, result,  StandardAnalyzer.STOP_WORDS_SET);
        result = new PorterStemFilter(result);
        return new TokenStreamComponents(src, result);
    }

}
