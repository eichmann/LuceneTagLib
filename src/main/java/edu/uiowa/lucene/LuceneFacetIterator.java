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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.facet.search.FacetResult;
import org.apache.lucene.facet.search.FacetResultNode;

@SuppressWarnings("serial")

public class LuceneFacetIterator extends BodyTagSupport {
    private static final Log log = LogFactory.getLog(LuceneFacetIterator.class);

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
	log.info("doStartTag LuceneFacetIterator: " + theSearch + "\t" + parentFacet);
	if (theSearch == null) {
	    throw new JspTagException("Lucene facet iterator tag not nesting in Search instance");
	}

	if (parentFacet == null) {
	    facetResults = theSearch.getFacetResults();
	    log.info("FacetResult list: " + facetResults);
	    hitFence = 0;
	    if (hitFence < facetResults.size()) {
		theResult = facetResults.get(hitFence++);
		log.trace("facet: " + theResult);
		return EVAL_BODY_INCLUDE;
	    }
	} else {
	    try {
		facetResultNodes = parentFacet.theResult.getFacetResultNode().subResults;
	    } catch (Exception e) {
		facetResultNodes = new ArrayList<FacetResultNode>();
	    }
	    log.info("FacetResultNode list: " + facetResultNodes);
	    hitFence = 0;
	    if (hitFence < facetResultNodes.size()) {
		theResultNode = facetResultNodes.get(hitFence++);
		log.trace("facet node: " + theResultNode);
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
		log.trace("facet: " + theResult);
		return EVAL_BODY_AGAIN;
	    }
	} else {
	    if (hitFence >= facetResultNodes.size()) {
		clearServiceState();
		return SKIP_BODY;
	    }

	    if (hitFence < facetResultNodes.size()) {
		theResultNode = facetResultNodes.get(hitFence++);
		log.trace("facet node: " + theResultNode);
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
