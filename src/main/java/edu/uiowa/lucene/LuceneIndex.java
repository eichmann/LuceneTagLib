package edu.uiowa.lucene;

import java.io.File;
import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

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
    
	public static SimpleFSLockFactory _LockFactory;

	
	public int doStartTag() throws JspException {
        try {
            if (truncate) {
                theWriter = new IndexWriter(FSDirectory.open(new File(lucenePath), _LockFactory), new StandardAnalyzer(org.apache.lucene.util.Version.LUCENE_30), true, IndexWriter.MaxFieldLength.LIMITED);
                for (int i = 0; i < theWriter.maxDoc(); i++)
                    theWriter.deleteAll();
                theWriter.close();
            }
            theWriter = new IndexWriter(FSDirectory.open(new File(lucenePath), _LockFactory), new StandardAnalyzer(org.apache.lucene.util.Version.LUCENE_30), true, IndexWriter.MaxFieldLength.LIMITED);
		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (LockObtainFailedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return EVAL_PAGE;
	}
	
	public int doEndTag() throws JspException {
        try {
	        theWriter.optimize();
	        theWriter.close();
		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
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
