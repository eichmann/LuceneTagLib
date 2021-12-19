package edu.uiowa.lucene;

import java.io.File;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.util.Version;

public class LuceneHelper {

	static Logger logger = LogManager.getLogger(LuceneHelper.class);

    /**
     * @param lucenePath
     * @param field
     * @param value
     */
    public static synchronized void deleteIndex(String lucenePath, String field, String value) {
    	logger.debug("Starting Delete Operation");
	try {
	    String normalizedValue = LuceneField.normalizeContent(value);
	    Directory directory = FSDirectory.open(new File(lucenePath));
	    IndexWriter indexWriter = new IndexWriter(directory,
		    new IndexWriterConfig(Version.LUCENE_43, new StandardAnalyzer(org.apache.lucene.util.Version.LUCENE_43)));
	    indexWriter.deleteDocuments(new Term(field.trim(), normalizedValue.trim()));
	    logger.debug("Deleting Term (Field,value):" + field + "," + value);
	    indexWriter.close();
	    directory.close();
	} catch (CorruptIndexException e) {
		logger.error("Corruption Exception", e);
	} catch (LockObtainFailedException e) {
		logger.error("Failed to obtain Lock", e);
	} catch (Exception e) {
		logger.error("Exception", e);
	}
	logger.debug("Done Delete Operation");
    }

    /**
     * @param lucenePath
     * @param truncate
     */
    public static synchronized void updateIndex(String lucenePath, boolean truncate) {
	IndexWriter theWriter = null;
	logger.debug("Starting Index Operation");

	try {
	    if (truncate) {
		Directory directory = FSDirectory.open(new File(lucenePath));
		theWriter = new IndexWriter(directory,
			new IndexWriterConfig(Version.LUCENE_43, new StandardAnalyzer(org.apache.lucene.util.Version.LUCENE_43)));
		for (int i = 0; i < theWriter.maxDoc(); i++)
		    theWriter.deleteAll();
		theWriter.close();
	    }
	    Directory directory = FSDirectory.open(new File(lucenePath));
	    theWriter = new IndexWriter(directory, new IndexWriterConfig(Version.LUCENE_43, new StandardAnalyzer(org.apache.lucene.util.Version.LUCENE_43)));

	    theWriter.close();
	} catch (CorruptIndexException e) {

		logger.error("Corruption Exception", e);
	} catch (LockObtainFailedException e) {
		logger.error("Failed to Obtain Lock", e);
	} catch (IOException e) {
		logger.error("IO Exception", e);
	}
	logger.debug("Done Index Operation");
    }

    /**
     * @param lucenePath
     * @param theDocument
     */
    public static synchronized void updateDocument(String lucenePath, Document theDocument) {
    	logger.debug("Starting Write Index");
	IndexWriter theWriter = null;

	try {
	    Directory directory = FSDirectory.open(new File(lucenePath));
	    theWriter = new IndexWriter(directory, new IndexWriterConfig(Version.LUCENE_43, new StandardAnalyzer(org.apache.lucene.util.Version.LUCENE_43)));
	    theWriter.addDocument(theDocument);
	    theWriter.close();
	} catch (CorruptIndexException e) {
		logger.error("Corruption Exception", e);
	} catch (IOException e) {
		logger.error("IO Exception", e);
	}
	logger.debug("Done Write Index");
    }

}
