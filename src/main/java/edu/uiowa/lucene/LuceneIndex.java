package edu.uiowa.lucene;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;

@SuppressWarnings("serial")

public class LuceneIndex extends BodyTagSupport {
    String lucenePath = null;
    IndexWriter theWriter = null;
    Document theDocument = null;
    boolean truncate = false;

    public int doStartTag() throws JspException {

	LuceneHelper.updateIndex(lucenePath, truncate);

	return EVAL_PAGE;
    }

    public int doEndTag() throws JspException {
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
