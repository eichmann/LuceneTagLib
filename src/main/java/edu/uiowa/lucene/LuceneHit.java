package edu.uiowa.lucene;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.index.CorruptIndexException;

@SuppressWarnings("serial")

public class LuceneHit extends BodyTagSupport {
    LuceneSearch theSearch = null;
    LuceneIterator theIterator = null;
    LuceneUniqueIterator theUniqueIterator = null;
    boolean keyField = false;
    String label = null;
    String value = null;
	static Logger logger = LogManager.getLogger(LuceneHit.class);

    public int doStartTag() throws JspTagException {
	theSearch = (LuceneSearch) findAncestorWithClass(this, LuceneSearch.class);
	theIterator = (LuceneIterator) findAncestorWithClass(this, LuceneIterator.class);
	theUniqueIterator = (LuceneUniqueIterator) findAncestorWithClass(this, LuceneUniqueIterator.class);

	if (theSearch == null) {
	    throw new JspTagException("Lucene Hit tag not nesting in Search instance");
	}

	try {
	    if (label.equals("score"))
		pageContext.getOut().print(theIterator.theHit.score);
	    else if (theIterator == null) {
	    	logger.trace("lucene hit: " + theUniqueIterator.theDocument.get(label));
	    	pageContext.getOut().print(theUniqueIterator.theDocument.get(label));	    	
	    } else {
	    	logger.trace("lucene hit: " + theIterator.theDocument.get(label));
	    	pageContext.getOut().print(theIterator.theDocument.get(label));
	    }
	} catch (CorruptIndexException e) {
		logger.error("Corruption Exception", e);
	} catch (IOException e) {
		logger.error("IO Exception", e);
	}

	return SKIP_BODY;
    }

    public int doEndTag() throws JspException {
	return super.doEndTag();
    }

    public boolean isKeyField() {
	return keyField;
    }

    public void setKeyField(boolean keyField) {
	this.keyField = keyField;
    }

    public String getLabel() {
	return label;
    }

    public void setLabel(String label) {
	this.label = label;
    }

    public String getValue() {
	return value;
    }

    public void setValue(String value) {
	this.value = value;
    }

}
