package edu.uiowa.lucene.biomedical;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.util.Version;

public class BiomedicalAnalyzer extends Analyzer {
	static Logger logger = LogManager.getLogger(BiomedicalAnalyzer.class);

	public static void main(String[] args) throws IOException {
		BiomedicalAnalyzer analyzer = new BiomedicalAnalyzer();

		analyzer.testing("Guanosine 5'-O-[S-(3-bromo-2-oxopropyl)]thiophosphate: a new reactive purine nucleotide analog labeling Met-169 and Tyr-262 in bovine liver glutamate ");
		analyzer.testing("IR-120 Al3+ TL-12 ");
		analyzer.testing("CaF2(Mn)-thermoluminescent-dosimete ");
		analyzer.testing("p,p'-DDT G+C--rich DNA ");
		analyzer.testing("cuprates R2Cu2O5 (R=Y,Ho,Er,Yb,Tm) ");
		analyzer.testing("erbium-doped cesium cadmium bromide (CsCdBr3:Er3+) ");
		analyzer.testing("2020;5:801-812. ");
		analyzer.testing("2020;10:e19-e23. ");
		analyzer.testing("2020;111(3):708-18. ");
		
		analyzer.close();
	}
	
	protected void testing(String testString) throws IOException {
		logger.info("testString: " + testString);
		TokenStream stream = this.tokenStream(null, new StringReader(testString));
		CharTermAttribute cattr = stream.addAttribute(CharTermAttribute.class);
		stream.reset();
		while (stream.incrementToken()) {
			logger.info("\ttoken: " + cattr.toString());
			cattr.setEmpty();
		}
		stream.end();
		stream.close();
	}

	@Override
	protected TokenStreamComponents createComponents(String arg0, Reader arg1) {
		BiomedicalTokenizer src = new BiomedicalTokenizer(arg1);
		TokenStream result = src; // new StandardFilter(Version.LUCENE_43, src);
		result = new LowerCaseFilter(Version.LUCENE_43, result);
		result = new StopFilter(Version.LUCENE_43, result, StandardAnalyzer.STOP_WORDS_SET);
		result = new PorterStemFilter(result);
		return new TokenStreamComponents(src, result);
	}

}
