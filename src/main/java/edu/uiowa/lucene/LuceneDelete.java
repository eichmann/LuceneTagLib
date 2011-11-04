package edu.uiowa.lucene;

import java.io.File;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.store.SimpleFSLockFactory;

@SuppressWarnings("serial")

public class LuceneDelete extends BodyTagSupport {
	String lucenePath = null;
	String field = null;
	String value = null;
	IndexWriter theWriter = null;
    Document theDocument = null;
    
	public static SimpleFSLockFactory _LockFactory;
    private static final Log log =LogFactory.getLog(LuceneDelete.class);

	
	public int doStartTag() throws JspException {
		LuceneIndex.writeLock.lock();
		log.debug("Lucene lock acquired...");
        try {

            Directory directory = FSDirectory.open(new File(lucenePath), _LockFactory);
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
		} finally {
			LuceneIndex.writeLock.unlock();
			log.debug("Lucene lock released.");
		}

		return EVAL_PAGE;
	}
	
	public String getLucenePath() {
		return lucenePath;
	}

	public void setLucenePath(String lucenePath) {
		this.lucenePath = lucenePath;
	}

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
