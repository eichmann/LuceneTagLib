package edu.uiowa.lucene;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.store.SimpleFSLockFactory;

@SuppressWarnings("serial")

public class LuceneIndex extends BodyTagSupport {
	String lucenePath = null;
	IndexWriter theWriter = null;
    Document theDocument = null;
    boolean truncate = false;
    
	//public static SimpleFSLockFactory _LockFactory;
    //private static final Log log =LogFactory.getLog(LuceneIndex.class);
    //static Lock writeLock = new ReentrantLock();
	
	public int doStartTag() throws JspException {
		
		LuceneHelper.updateIndex(LuceneHelper.LUCENE_INDEX, lucenePath, theDocument, null, null, truncate);
	
		return EVAL_PAGE;
	}
	
	public int doEndTag() throws JspException {
    	return super.doEndTag();		
	}

	public String getLucenePath() {
		return lucenePath;
	}

	public void setLucenePath(String lucenePath) {
		this.lucenePath = lucenePath;
	}

    public boolean getTruncate() {
        return truncate;
    }

    public void setTruncate(boolean truncate) {
        this.truncate = truncate;
    }

}
