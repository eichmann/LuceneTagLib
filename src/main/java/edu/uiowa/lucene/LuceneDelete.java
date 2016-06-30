package edu.uiowa.lucene;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.SimpleFSLockFactory;

@SuppressWarnings("serial")

public class LuceneDelete extends BodyTagSupport {
	String lucenePath = null;
	String field = null;
	String value = null;
	IndexWriter theWriter = null;
    Document theDocument = null;
    
	public static SimpleFSLockFactory _LockFactory;
    @SuppressWarnings("unused")
    private static final Log log =LogFactory.getLog(LuceneDelete.class);

	
	public int doStartTag() throws JspException {
		
		LuceneHelper.deleteIndex(lucenePath, field, value);
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
