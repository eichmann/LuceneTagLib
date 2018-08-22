package edu.uiowa.lucene;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queries.mlt.MoreLikeThis;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.SimpleFSLockFactory;
import org.apache.lucene.util.Version;

import edu.uiowa.lucene.booleanSearch.*;
import edu.uiowa.lucene.conceptSearch.*;

@SuppressWarnings("serial")

public class LuceneSearch extends BodyTagSupport {
    TopDocs theHits = null;
    String label = null;
    String lucenePath = null;
    String queryString = null;
    IndexReader reader = null;
    IndexSearcher theSearcher = null;
    boolean similarity = true;
    String queryParserName = null;
    boolean useConjunctionByDefault = false;
    boolean useDateHack = false;

    public static SimpleFSLockFactory _LockFactory;
    private static final Log log = LogFactory.getLog(LuceneSearch.class);
    private static Pattern datePattern = Pattern.compile("([0-9]{4})-([0-9]{4})");

    public static void main(String[] args) {
	String originalQuery = "Robert Burton, Robert 1925-1984";
	log.info("originalQuery:" + originalQuery);
	log.info("asConjunctiveQuery: " + asConjunctiveQuery(originalQuery));
	log.info("asConjunctiveQuery w/ date hack: " + asConjunctiveQuery(originalQuery, true));
    }

    public static String asConjunctiveQuery(String originalQuery) {
	return asConjunctiveQuery(originalQuery, false);
    }

    public static String asConjunctiveQuery(String originalQuery, boolean useDateHack) {
	StringBuffer buffer = new StringBuffer();

	for (String term : originalQuery.split("[, ]+")) {
	    if (buffer.length() > 0)
		buffer.append(" & ");
	    if (useDateHack) {
		Matcher dateMatcher = datePattern.matcher(term);
		if (dateMatcher.matches()) {
		    buffer.append(dateMatcher.group(1) + " & " + dateMatcher.group(2));
		} else
		    buffer.append(term);
	    } else {
		buffer.append(term);
	    }
	}

	return buffer.toString().trim();
    }

    @SuppressWarnings("deprecation")
    public int doStartTag() throws JspException {
	log.info("search called: " + queryString);
	if (useConjunctionByDefault) {
	    queryString = asConjunctiveQuery(queryString, useDateHack);
	    log.info("rewriting query to: " + queryString);
	}
	log.info("queryParserName: " + queryParserName);
	try {
	    reader = DirectoryReader.open(FSDirectory.open(new File(lucenePath)));
	    theSearcher = new IndexSearcher(reader);
	    Query theQuery = null;

	    if ("concept".equals(queryParserName)) {
		// TODO insert magic here to allow for selection of nomenclature
		theQuery = (Query) (new ConceptParseCup(new conceptParseFlex(new StringReader(queryString)))).parse().value;
	    } else if ("boolean".equals(queryParserName)) {
		theQuery = (Query) (new BooleanParseCup(new booleanParseFlex(new StringReader(queryString)))).parse().value;
	    } else if (similarity) {
		MoreLikeThis mlt = new MoreLikeThis(reader);
		mlt.setMinDocFreq(1);
		mlt.setMinTermFreq(1);
		mlt.setMaxQueryTerms(100);
		mlt.setFieldNames(new String[] { label });
		mlt.setAnalyzer(new StandardAnalyzer(Version.LUCENE_43));
		theQuery = mlt.like(new StringReader(queryString), "content");
	    } else {
		org.apache.lucene.queryparser.classic.QueryParser theQueryParser = new QueryParser(org.apache.lucene.util.Version.LUCENE_30, label,
			new StandardAnalyzer(org.apache.lucene.util.Version.LUCENE_30));
		theQuery = theQueryParser.parse(queryString);
	    }

	    log.info("actual query: " + theQuery);

	    theHits = theSearcher.search(theQuery, 1000);

	    log.debug(theHits.totalHits);

	    return EVAL_BODY_INCLUDE;
	} catch (CorruptIndexException e) {
	    log.error("Corruption Exception", e);
	} catch (IOException e) {
	    log.error("IO Exception", e);
	} catch (Exception e) {
	    log.error("Problem Parsing" + queryString, e);
	}

	return SKIP_BODY;
    }

    public int doEndTag() throws JspException {
	try {
	    reader.close();
	} catch (IOException e) {
	    log.error("Corruption Exception", e);
	}
	clearServiceState();
	return super.doEndTag();
    }

    private void clearServiceState() {
	this.theHits = null;
	this.label = null;
	reader = null;
	theSearcher = null;
	lucenePath = null;
	queryString = null;
	similarity = true;
    }

    public String getLucenePath() {
	return lucenePath;
    }

    public void setLucenePath(String lucenePath) {
	this.lucenePath = lucenePath;
    }

    public String getQueryString() {
	return queryString;
    }

    public void setQueryString(String queryString) {
	this.queryString = queryString;
    }

    public String getLabel() {
	return label;
    }

    public void setLabel(String label) {
	this.label = label;
    }

    public boolean isSimilarity() {
	return similarity;
    }

    public void setSimilarity(boolean similarity) {
	this.similarity = similarity;
    }

    public String getQueryParserName() {
	return queryParserName;
    }

    public void setQueryParserName(String queryParserName) {
	this.queryParserName = queryParserName;
    }

    public boolean getUseConjunctionByDefault() {
	return useConjunctionByDefault;
    }

    public void setUseConjunctionByDefault(boolean useConjunctionByDefault) {
	this.useConjunctionByDefault = useConjunctionByDefault;
    }

    public boolean getUseDateHack() {
	return useDateHack;
    }

    public void setUseDateHack(boolean useDateHack) {
	this.useDateHack = useDateHack;
    }

}
