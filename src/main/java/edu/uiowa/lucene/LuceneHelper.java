package edu.uiowa.lucene;

import java.io.File;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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

    private static final Log log = LogFactory.getLog(LuceneHelper.class);

    /**
     * @param lucenePath
     * @param field
     * @param value
     */
    public static synchronized void deleteIndex(String lucenePath, String field, String value) {
	log.debug("Starting Delete Operation");
	try {
	    String normalizedValue = LuceneField.normalizeContent(value);
	    Directory directory = FSDirectory.open(new File(lucenePath));
	    IndexWriter indexWriter = new IndexWriter(directory,
		    new IndexWriterConfig(Version.LUCENE_43, new StandardAnalyzer(org.apache.lucene.util.Version.LUCENE_43)));
	    indexWriter.deleteDocuments(new Term(field.trim(), normalizedValue.trim()));
	    log.debug("Deleting Term (Field,value):" + field + "," + value);
	    indexWriter.close();
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
	IndexWriter theWriter = null;

	try {
	    Directory directory = FSDirectory.open(new File(lucenePath));
	    theWriter = new IndexWriter(directory, new IndexWriterConfig(Version.LUCENE_43, new StandardAnalyzer(org.apache.lucene.util.Version.LUCENE_43)));
	    theWriter.addDocument(theDocument);
	    theWriter.close();
	} catch (CorruptIndexException e) {
	    log.error("Corruption Exception", e);
	} catch (IOException e) {
	    log.error("IO Exception", e);
	}
	log.debug("Done Write Index");
    }

}
