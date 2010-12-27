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

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;

@SuppressWarnings("serial")

public class LuceneSearch extends BodyTagSupport {
    TopDocs theHits = null;
    String label = null;
    String lucenePath = null;
    String queryString = null;
    IndexReader reader = null;
    IndexSearcher theSearcher = null;

    public int doStartTag() throws JspException {
//        System.out.println("search called: " + queryString);
        try {
        	reader = IndexReader.open(FSDirectory.open(new File(lucenePath)), true);
            theSearcher = new IndexSearcher(reader);
            QueryParser theQueryParser = new QueryParser(org.apache.lucene.util.Version.LUCENE_30, label, new StandardAnalyzer(org.apache.lucene.util.Version.LUCENE_30));
            Query theQuery = theQueryParser.parse(queryString);
            
            theHits = theSearcher.search(theQuery, 1000);
            return EVAL_BODY_INCLUDE;
        } catch (CorruptIndexException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }  catch (ParseException e) {
            e.printStackTrace();
        }

        return SKIP_BODY;
    }

	public int doEndTag() throws JspException {
        try {
	        theSearcher.close();
	        reader.close();
		} catch (IOException e) {
			e.printStackTrace();
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
