package edu.uiowa.lucene;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.facet.search.FacetResult;
import org.apache.lucene.facet.search.FacetResultNode;
import org.apache.lucene.index.CorruptIndexException;

@SuppressWarnings("serial")

public class LuceneFacet extends BodyTagSupport {
    LuceneSearch theSearch = null;
    LuceneFacetIterator theIterator = null;
    FacetResult theResult = null;
    FacetResultNode theResultNode = null;
    String label = null;
	static Logger logger = LogManager.getLogger(LuceneFacet.class);

    public int doStartTag() throws JspTagException {
	theSearch = (LuceneSearch) findAncestorWithClass(this, LuceneSearch.class);
	theIterator = (LuceneFacetIterator) findAncestorWithClass(this, LuceneFacetIterator.class);

	if (theSearch == null) {
	    throw new JspTagException("Lucene Hit tag not nesting in Search instance");
	}

	if (theIterator == null || (theIterator.theResult == null && theIterator.theResultNode == null)) {
	    throw new JspTagException("Lucene Facet not nested in valid Facet Iterator instance");
	}

	theResult = theIterator.theResult;
	theResultNode = theIterator.theResultNode;
	logger.trace("facet tag: " + theResult + "\t" + theResultNode);

	if (theResult != null) {
		logger.trace("facet subtags: " + theResult.getFacetResultNode().subResults);

	    try {
		if (label.equals("none")) {
		    // do nothing except provide context
		} else if (label.equals("count"))
		    pageContext.getOut().print(theResult.getNumValidDescendants());
		else {
		    pageContext.getOut().print(theResult.getFacetResultNode().label);
		}
	    } catch (CorruptIndexException e) {
	    	logger.error("Corruption Exception", e);
	    } catch (IOException e) {
	    	logger.error("IO Exception", e);
	    }
	} else {
		logger.trace("facet subtags: " + theResultNode.subResults);

	    try {
		if (label.equals("none")) {
		    // do nothing except provide context
		} else if (label.equals("count"))
		    pageContext.getOut().print((int)theResultNode.value);
		else {
		    pageContext.getOut().print(theResultNode.label.components[theResultNode.label.components.length-1]);
		}
	    } catch (CorruptIndexException e) {
	    	logger.error("Corruption Exception", e);
	    } catch (IOException e) {
	    	logger.error("IO Exception", e);
	    }
	}

	return EVAL_BODY_INCLUDE;
    }

    public int doEndTag() throws JspException {
	return super.doEndTag();
    }

    public String getLabel() {
	return label;
    }

    public void setLabel(String label) {
	this.label = label;
    }

}
