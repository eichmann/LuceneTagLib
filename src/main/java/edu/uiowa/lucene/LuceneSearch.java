/*
 * Created on Nov 2, 2009
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package edu.uiowa.lucene;

import java.io.File;
import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.SimpleFSLockFactory;

@SuppressWarnings("serial")

public class LuceneSearch extends BodyTagSupport {
    TopDocs theHits = null;
    String label = null;
    String lucenePath = null;
    String queryString = null;
    IndexReader reader = null;
    IndexSearcher theSearcher = null;
    
	public static SimpleFSLockFactory _LockFactory;
    private static final Log log =LogFactory.getLog(LuceneSearch.class);


    public int doStartTag() throws JspException {
    	log.debug("search called: " + queryString);
        try {

        	reader = IndexReader.open(FSDirectory.open(new File(lucenePath), _LockFactory), true);
            theSearcher = new IndexSearcher(reader);
            QueryParser theQueryParser = new QueryParser(org.apache.lucene.util.Version.LUCENE_30, label, new StandardAnalyzer(org.apache.lucene.util.Version.LUCENE_30));
            Query theQuery = theQueryParser.parse(queryString);
            
            theHits = searchIndex(theSearcher, theQuery, 1000);
            
            return EVAL_BODY_INCLUDE;
        } catch (CorruptIndexException e) {
			log.error("Corruption Exception", e);
        } catch (IOException e) {
			log.error("IO Exception", e);
        }  catch (ParseException e) {
			log.error("Problem Parseing" + queryString, e);
        }

        return SKIP_BODY;
    }

	private static TopDocs searchIndex(IndexSearcher searcher, Query query, int n) throws IOException {
		TopDocs myHits = null;
		boolean retry = true;
		while (retry) {
			try {
				retry = false;
				myHits = searcher.search(query, n);
				return myHits;
			} catch (BooleanQuery.TooManyClauses e) {
				// Double the number of boolean queries allowed.
				// The default is in org.apache.lucene.search.BooleanQuery and
				// is 1024.
				String defaultQueries = Integer.toString(BooleanQuery.getMaxClauseCount());
				int oldQueries = Integer.parseInt(System.getProperty("org.apache.lucene.maxClauseCount", defaultQueries));
				int newQueries = oldQueries * 2;
				log.error("Too many hits for query: " + oldQueries + ".  Increasing to " + newQueries, e);
				System.setProperty("org.apache.lucene.maxClauseCount",
				Integer.toString(newQueries));
				BooleanQuery.setMaxClauseCount(newQueries);
				retry = true;
			}
		}
		return myHits;
	}
    
	public int doEndTag() throws JspException {
        try {
	        theSearcher.close();
	        reader.close();
		} catch (IOException e) {
			log.error("Corruption Exception", e);
		}
    	return super.doEndTag();		
	}
	
    private void clearServiceState() {
        this.theHits = null;
        this.label = null;
        reader = null;
        theSearcher = null;
        lucenePath = null;
        queryString = null;
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

}
