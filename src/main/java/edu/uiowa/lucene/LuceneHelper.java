package edu.uiowa.lucene;

import java.io.File;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.store.SimpleFSLockFactory;

public class LuceneHelper {
	
	static final int LUCENE_DOCUMENT = 1;
	static final int LUCENE_INDEX = 2;

	static final int LUCENE_DELETE = 3;

	public static SimpleFSLockFactory _LockFactory;
    private static final Log log =LogFactory.getLog(LuceneHelper.class);
    
    public static synchronized void updateIndex(int operation, String lucenePath, Document theDocument, String field, String value,  boolean truncate) {
	    
	    if (LUCENE_DOCUMENT == operation)	{
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
	    	
	    	
	    if (LUCENE_INDEX == operation)	{
	    		
		    IndexWriter theWriter = null;
		    log.debug("Starting Index Operation");
	            
			try {
		    	if (truncate) {
		
		            theWriter = new IndexWriter(FSDirectory.open(new File(lucenePath), _LockFactory), 
		            		new StandardAnalyzer(org.apache.lucene.util.Version.LUCENE_30), true, 
		            		IndexWriter.MaxFieldLength.LIMITED);
		            for (int i = 0; i < theWriter.maxDoc(); i++)
		                theWriter.deleteAll();
		            theWriter.close();
		        }
		        theWriter = new IndexWriter(FSDirectory.open(new File(lucenePath), _LockFactory), 
		        		new StandardAnalyzer(org.apache.lucene.util.Version.LUCENE_30), true, 
		        		IndexWriter.MaxFieldLength.LIMITED);
		        
		        theWriter.optimize();
		        theWriter.close();
			} catch (CorruptIndexException e) {
				
				
				log.error("Corruption Exception", e);
			} catch (LockObtainFailedException e) {
				log.error("Failed to Obtain Lock", e);
			} catch (IOException e) {
				log.error("IO Exception", e);
			}
			log.debug("Done Index Operation");
	        
	    }
	    
	    if (LUCENE_DELETE == operation)	{
			log.debug("Starting Delete Operation");
	        try {

	            //Directory directory = FSDirectory.open(new File(lucenePath), _LockFactory);
	        	Directory directory = FSDirectory.open(new File(lucenePath));
	            IndexReader reader = IndexReader.open(directory, false); // we don't want read-only because we are about to delete
	            reader.deleteDocuments(new Term(field.trim(),value.trim()));
	            log.debug("Deleting Term (Field,value):" + field + "," + value );
	            reader.flush();
	            reader.close();
	            directory.close();
			} catch (CorruptIndexException e) {
				log.error("Corruption Exception", e);
			} catch (LockObtainFailedException e) {
				log.error("Failed to obtain Lock", e);
			} catch (Exception e) {
				log.error("Exception", e);
			}
	        log.debug("Done Delete Operation");

	    }

    }

}
