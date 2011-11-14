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

public class LuceneHelper {
	
    private static final Log log =LogFactory.getLog(LuceneHelper.class);

	/**
	 * @param lucenePath
	 * @param field
	 * @param value
	 */
	public static synchronized void deleteIndex(String lucenePath, String field, String value) {
		log.debug("Starting Delete Operation");
		try {
        	String normalizedValue = LuceneField.normalizeContent(value);

		    //Directory directory = FSDirectory.open(new File(lucenePath), _LockFactory);
			Directory directory = FSDirectory.open(new File(lucenePath));
		    IndexReader reader = IndexReader.open(directory, false); // we don't want read-only because we are about to delete
		    reader.deleteDocuments(new Term(field.trim(),normalizedValue.trim()));
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

	/**
	 * @param lucenePath
	 * @param truncate
	 */
	public static synchronized void updateIndex(String lucenePath, boolean truncate) {
		IndexWriter theWriter = null;
		log.debug("Starting Index Operation");
		    
		try {
			if (truncate) {

		        //theWriter = new IndexWriter(FSDirectory.open(new File(lucenePath), _LockFactory),
				theWriter = new IndexWriter(FSDirectory.open(new File(lucenePath)),
		        		new StandardAnalyzer(org.apache.lucene.util.Version.LUCENE_30), true, 
		        		IndexWriter.MaxFieldLength.LIMITED);
		        for (int i = 0; i < theWriter.maxDoc(); i++)
		            theWriter.deleteAll();
		        theWriter.close();
		    }
			//theWriter = new IndexWriter(FSDirectory.open(new File(lucenePath), _LockFactory),
		    theWriter = new IndexWriter(FSDirectory.open(new File(lucenePath)), 
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

	/**
	 * @param lucenePath
	 * @param theDocument
	 */
	public static synchronized void updateDocument(String lucenePath, Document theDocument) {
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
		} 
		log.debug("Done Write Index");
	}

}
