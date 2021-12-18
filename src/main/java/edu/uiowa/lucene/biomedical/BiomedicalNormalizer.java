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
import org.apache.lucene.analysis.core.WhitespaceTokenizer;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.util.Version;

public class BiomedicalNormalizer extends Analyzer {
	static Logger logger = LogManager.getLogger(BiomedicalNormalizer.class);

	public static void main(String[] args) throws IOException {
		BiomedicalNormalizer analyzer = new BiomedicalNormalizer();
		TokenStream stream = analyzer.tokenStream(null, new StringReader(
				"Guanosine 5'-O-[S-(3-bromo-2-oxopropyl)]thiophosphate: a new reactive purine nucleotide analog labeling Met-169 and Tyr-262 in bovine liver glutamate "));
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
		WhitespaceTokenizer src = new WhitespaceTokenizer(Version.LUCENE_43, arg1);
		TokenStream result = src; // new StandardFilter(Version.LUCENE_43, src);
		result = new LowerCaseFilter(Version.LUCENE_43, result);
		result = new StopFilter(Version.LUCENE_43, result, StandardAnalyzer.STOP_WORDS_SET);
		result = new PorterStemFilter(result);
		return new TokenStreamComponents(src, result);
	}

}
