package edu.uiowa.lucene;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.index.CorruptIndexException;

@SuppressWarnings("serial")

public class LuceneCount extends BodyTagSupport {
    LuceneSearch theSearch = null;
	static Logger logger = LogManager.getLogger(LuceneCount.class);

    public int doStartTag() throws JspTagException {
	theSearch = (LuceneSearch) findAncestorWithClass(this, LuceneSearch.class);

	if (theSearch == null) {
	    throw new JspTagException("Lucene Hit tag not nesting in Search instance");
	}

	try {
	    pageContext.getOut().print(theSearch.theHits.scoreDocs.length);
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

}
