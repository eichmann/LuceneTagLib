package edu.uiowa.lucene;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.index.CorruptIndexException;

@SuppressWarnings("serial")

public class LuceneHitRank extends BodyTagSupport {
    LuceneSearch theSearch = null;
    LuceneIterator theIterator = null;
    private static final Log log = LogFactory.getLog(LuceneHitRank.class);

    public int doStartTag() throws JspTagException {
	theSearch = (LuceneSearch) findAncestorWithClass(this, LuceneSearch.class);
	theIterator = (LuceneIterator) findAncestorWithClass(this, LuceneIterator.class);

	if (theSearch == null) {
	    throw new JspTagException("Lucene Hit Count tag not nesting in Search instance");
	}

	try {
	    pageContext.getOut().print(theIterator.hitFence);
	} catch (CorruptIndexException e) {
	    log.error("Corruption Exception", e);
	} catch (IOException e) {
	    log.error("IO Exception", e);
	}

	return SKIP_BODY;
    }

    public int doEndTag() throws JspException {
	return super.doEndTag();
    }

}
