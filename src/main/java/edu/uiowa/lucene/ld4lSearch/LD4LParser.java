package edu.uiowa.lucene.ld4lSearch;

import java.text.Normalizer;
import java.text.Normalizer.Form;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Query;

@SuppressWarnings("deprecation")

public class LD4LParser {
    private static final Log log = LogFactory.getLog(LD4LParser.class);

    public static Query parse(String queryString, String label) throws ParseException {
	StringBuffer buffer = new StringBuffer();
	
	// first, ditch characters involved in range specification, then handle diacriticals
	String[] tokens = queryString.replace('[', ' ').replace(']', ' ').replace('{', ' ').replace('}', ' ').split(" +");
	log.info("tokens: " + logArray(tokens));
	
	for (String token : tokens) {
	    buffer.append(token + " ");
	    String normalizedToken = removeAccents(token);
	    if (!token.equals(normalizedToken)) {
		buffer.append(normalizedToken + " ");
	    }
	}
	
	QueryParser ld4lParser = new QueryParser(org.apache.lucene.util.Version.LUCENE_30, label,
							new StandardAnalyzer(org.apache.lucene.util.Version.LUCENE_30));
	return ld4lParser.parse(buffer.toString());
    }
    
    public static String removeAccents(String text) {
	return text == null ? null : Normalizer.normalize(text, Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
    }
   
    private static String logArray(String[] array) {
	StringBuffer buffer = new StringBuffer();
	buffer.append('[');
	boolean first = true;
	for (String token : array) {
	    if (!first)
		buffer.append(", ");
	    first = false;
	    buffer.append(token);
	}
	buffer.append(']');
	
	return buffer.toString();
    }
}
