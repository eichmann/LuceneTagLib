package edu.uiowa.lucene;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.document.Document;
import org.apache.lucene.store.SimpleFSLockFactory;

@SuppressWarnings("serial")

public class LuceneDocument extends BodyTagSupport {
    String lucenePath = null;
    // IndexWriter theWriter = null;
    Document theDocument = null;

    public static SimpleFSLockFactory _LockFactory;
    @SuppressWarnings("unused")
    private static final Log log = LogFactory.getLog(LuceneDocument.class);

    public int doStartTag() throws JspException {
	theDocument = new Document();
	return EVAL_PAGE;
    }

    public int doEndTag() throws JspException {
	LuceneHelper.updateDocument(getLucenePath(), theDocument);

	return super.doEndTag();
    }

    public String getLucenePath() {
	return lucenePath;
    }

    public void setLucenePath(String lucenePath) {
	this.lucenePath = lucenePath;
    }

}
