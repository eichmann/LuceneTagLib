/*
 * Created on Nov 2, 2009
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package edu.uiowa.lucene;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.facet.search.FacetResult;
import org.apache.lucene.facet.search.FacetResultNode;

@SuppressWarnings("serial")

public class LuceneFacetIterator extends BodyTagSupport {
	static Logger logger = LogManager.getLogger(LuceneFacetIterator.class);

    LuceneSearch theSearch = null;
    LuceneFacet parentFacet = null;
    List<FacetResult> facetResults = null;
    List<FacetResultNode> facetResultNodes = null;
    FacetResult theResult = null;
    FacetResultNode theResultNode = null;
    int hitFence = 0;

    public int doStartTag() throws JspException {
	theSearch = (LuceneSearch) findAncestorWithClass(this, LuceneSearch.class);
	parentFacet = (LuceneFacet) findAncestorWithClass(this, LuceneFacet.class);
	logger.trace("doStartTag LuceneFacetIterator: " + theSearch + "\t" + parentFacet);
	if (theSearch == null) {
	    throw new JspTagException("Lucene facet iterator tag not nesting in Search instance");
	}

	if (parentFacet == null) {
	    facetResults = theSearch.getFacetResults();
	    logger.trace("FacetResult list: " + facetResults);
	    hitFence = 0;
	    if (hitFence < facetResults.size()) {
		theResult = facetResults.get(hitFence++);
		logger.trace("facet: " + theResult);
		return EVAL_BODY_INCLUDE;
	    }
	} else {
	    try {
		if (parentFacet.theResult != null)
		    facetResultNodes = parentFacet.theResult.getFacetResultNode().subResults;
		else
		    facetResultNodes = parentFacet.theResultNode.subResults;
	    } catch (Exception e) {
		facetResultNodes = new ArrayList<FacetResultNode>();
	    }
	    logger.trace("FacetResultNode list: " + facetResultNodes);
	    hitFence = 0;
	    if (hitFence < facetResultNodes.size()) {
		theResultNode = facetResultNodes.get(hitFence++);
		logger.trace("facet node: " + theResultNode);
		return EVAL_BODY_INCLUDE;
	    }
	}
	if (facetResults == null && facetResultNodes == null) {
	    throw new JspTagException("Lucene facet result vectors are null");
	}

	return SKIP_BODY;
    }

    public int doAfterBody() throws JspTagException {
	if (parentFacet == null) {
	    if (hitFence >= facetResults.size()) {
		clearServiceState();
		return SKIP_BODY;
	    }

	    if (hitFence < facetResults.size()) {
		theResult = facetResults.get(hitFence++);
		logger.trace("facet: " + theResult);
		return EVAL_BODY_AGAIN;
	    }
	} else {
	    if (hitFence >= facetResultNodes.size()) {
		clearServiceState();
		return SKIP_BODY;
	    }

	    if (hitFence < facetResultNodes.size()) {
		theResultNode = facetResultNodes.get(hitFence++);
		logger.trace("facet node: " + theResultNode);
		return EVAL_BODY_AGAIN;
	    }
	}
	clearServiceState();
	return SKIP_BODY;
    }

    private void clearServiceState() {
	theSearch = null;
	facetResults = null;
	theResult = null;
	hitFence = 0;
    }

    public FacetResult getFacetResult() {
	return theResult;
    }

}
