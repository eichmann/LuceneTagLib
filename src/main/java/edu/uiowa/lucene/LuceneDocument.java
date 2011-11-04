package edu.uiowa.lucene;

import java.io.File;
import java.io.IOException;

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

public class LuceneDocument extends BodyTagSupport {
	String lucenePath = null;
	//IndexWriter theWriter = null;
    Document theDocument = null;
    
	public static SimpleFSLockFactory _LockFactory;
    private static final Log log =LogFactory.getLog(LuceneDocument.class);

	private static synchronized void writeIndex(String lucenePath, Document theDocument) {
		log.debug("Starting Write Index");

		try {
			  
		 
			// IndexWriter theWriter = new IndexWriter(FSDirectory.open(new File(lucenePath), _LockFactory),
			IndexWriter theWriter = new IndexWriter(FSDirectory.open(new File(lucenePath)),
        		new StandardAnalyzer(org.apache.lucene.util.Version.LUCENE_30), false, IndexWriter.MaxFieldLength.LIMITED);

      
			theWriter.addDocument(theDocument);
	        theWriter.optimize();
	        theWriter.close();
		} catch (CorruptIndexException e) {
			log.error("Corruption Exception", e);
		} catch (IOException e) {
			log.error("IO Exception", e);
		} finally {
		//LuceneIndex.writeLock.unlock();
			//log.debug("Lucene lock released.");
		}
		log.debug("Done Write Index");
		
	}
	public int doStartTag() throws JspException {
		//LuceneIndex.writeLock.lock();
		//log.debug("Lucene lock acquired...");
//        try {

        //	theWriter = new IndexWriter(FSDirectory.open(new File(lucenePath), _LockFactory),
        //    		new StandardAnalyzer(org.apache.lucene.util.Version.LUCENE_30), false, IndexWriter.MaxFieldLength.LIMITED);
		theDocument = new Document();
//		} catch (CorruptIndexException e) {
//			log.error("Corruption Exception", e);
//			LuceneIndex.writeLock.unlock();
//			
//
//		} catch (LockObtainFailedException e) {
//			log.error("Failed to Obtain Lock", e);
//			LuceneIndex.writeLock.unlock();
//			
//
//		} catch (IOException e) {
//			log.error("IO Exception", e);
//			LuceneIndex.writeLock.unlock();
//			
//
//		}
//
		return EVAL_PAGE;
	}
	
	public int doEndTag() throws JspException {
		
		writeIndex( getLucenePath(), theDocument);
		
//		
//        try {
//			theWriter.addDocument(theDocument);
//	        theWriter.optimize();
//	        theWriter.close();
//		} catch (CorruptIndexException e) {
//			log.error("Corruption Exception", e);
//		} catch (IOException e) {
//			log.error("IO Exception", e);
//		} finally {
//			LuceneIndex.writeLock.unlock();
//			log.debug("Lucene lock released.");
//		}
    	return super.doEndTag();		
	}

	public String getLucenePath() {
		return lucenePath;
	}

	public void setLucenePath(String lucenePath) {
		this.lucenePath = lucenePath;
	}

}
