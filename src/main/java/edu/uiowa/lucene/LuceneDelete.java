package edu.uiowa.lucene;

import java.io.File;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockObtainFailedException;

@SuppressWarnings("serial")

public class LuceneDelete extends BodyTagSupport {
	String lucenePath = null;
	String field = null;
	String value = null;
	IndexWriter theWriter = null;
    Document theDocument = null;
	
	public int doStartTag() throws JspException {
        try {
            Directory directory = FSDirectory.open(new File(lucenePath));
            IndexReader reader = IndexReader.open(directory, false); // we don't want read-only because we are about to delete
            reader.deleteDocuments(new Term(field.trim(),value.trim()));
            reader.flush();
            reader.close();
            directory.close();
		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (LockObtainFailedException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
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
