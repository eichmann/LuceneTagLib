/*
 * Created on Nov 2, 2009
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package edu.uiowa.lucene;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similar.MoreLikeThis;
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
    boolean similarity = true;
    
	public static SimpleFSLockFactory _LockFactory;
    private static final Log log =LogFactory.getLog(LuceneSearch.class);


    public int doStartTag() throws JspException {
    	log.debug("search called: " + queryString);
        try {
        	reader = IndexReader.open(FSDirectory.open(new File(lucenePath), _LockFactory), true);
            theSearcher = new IndexSearcher(reader);
            Query theQuery = null;
            
            if (similarity) {
            	MoreLikeThis mlt = new MoreLikeThis(reader);
            	mlt.setMinDocFreq(1);
            	mlt.setMinTermFreq(1);
            	mlt.setMaxQueryTerms(100);
            	mlt.setFieldNames(new String[] { label });
            	theQuery = mlt.like(new StringReader(queryString));
            } else {
                QueryParser theQueryParser = new QueryParser(org.apache.lucene.util.Version.LUCENE_30, label, new StandardAnalyzer(org.apache.lucene.util.Version.LUCENE_30));
            	theQuery = theQueryParser.parse(queryString);            	
            }
            
            theHits = theSearcher.search(theQuery, 1000);
            
            log.debug(theHits.totalHits);
            
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

	public int doEndTag() throws JspException {
        try {
	        theSearcher.close();
	        reader.close();
		} catch (IOException e) {
			log.error("Corruption Exception", e);
		}
    	return super.doEndTag();		
	}
	
    @SuppressWarnings("unused")
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

}
