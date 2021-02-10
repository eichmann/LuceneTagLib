/*
 * Created on Nov 2, 2009
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package edu.uiowa.lucene;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;

@SuppressWarnings("serial")

public class LuceneIterator extends BodyTagSupport {
    LuceneSearch theSearch = null;
    TopDocs theHits = null;
    ScoreDoc theHit = null;
    private int hitFence = 0;
    private int hitOffset = 0;
    double thresholdFence = 0.0;
    Document theDocument = null;
    String label = null;

    int limitCriteria = 0;
    int startCriteria = 1;
    double thresholdCriteria = 0.0;
    private static final Log log = LogFactory.getLog(LuceneIterator.class);

    public int doStartTag() throws JspException {
	log.trace("limit: " + limitCriteria);
	log.trace("start: " + startCriteria);
	log.trace("threshold: " + thresholdCriteria);
	theSearch = (LuceneSearch) findAncestorWithClass(this, LuceneSearch.class);

	if (theSearch == null) {
	    throw new JspTagException("Lucene Hit tag not nesting in Search instance");
	}

	try {
	    if (startCriteria < 1) // a missing parameter in the requesting URL
				   // results in this getting set to 0.
		startCriteria = 1;
	    hitFence = startCriteria - 1;
	    theHits = theSearch.theHits;
	    if (hitFence < theHits.scoreDocs.length) {
		theHit = theHits.scoreDocs[hitFence++];
		thresholdFence = theHit.score;
		theDocument = theSearch.theSearcher.doc(theHit.doc);

		if (thresholdFence < thresholdCriteria) {
		    clearServiceState();
		    return SKIP_BODY;
		}

		return EVAL_BODY_INCLUDE;
	    }
	} catch (CorruptIndexException e) {
	    log.error("Corruption Exception", e);

	} catch (IOException e) {
	    log.error("IO Exception", e);

	}

	return SKIP_BODY;
    }

    public int doAfterBody() throws JspTagException {
	if (limitCriteria <= 0 || (limitCriteria > 0 && hitFence >= limitCriteria + startCriteria - 1)) {
	    clearServiceState();
	    return SKIP_BODY;
	}

	if (hitFence < theHits.scoreDocs.length) {
	    theHit = theHits.scoreDocs[hitFence++];
	    thresholdFence = theHit.score;

	    if (thresholdFence < thresholdCriteria) {
		clearServiceState();
		return SKIP_BODY;
	    }

	    try {
		theDocument = theSearch.theSearcher.doc(theHit.doc);
	    } catch (CorruptIndexException e) {
		log.error("Corruption Exception", e);

		throw (new JspTagException(e.toString()));
	    } catch (IOException e) {
		log.error("IO Exception", e);
		throw (new JspTagException(e.toString()));
	    }
	    return EVAL_BODY_AGAIN;
	}
	clearServiceState();
	return SKIP_BODY;
    }

    private void clearServiceState() {
	this.theHits = null;
	this.label = null;
	theHit = null;
	hitFence = 0;
    }

    public int getHitRank() {
        return hitOffset + hitFence;
    }

    public int getRankOffset() {
        return hitOffset;
    }

    public void setRankOffset(int hitOffset) {
        this.hitOffset = hitOffset;
    }

    public String getLabel() {
	return label;
    }

    public void setLabel(String label) {
	this.label = label;
    }

    public int getLimitCriteria() {
	return limitCriteria;
    }

    public void setLimitCriteria(int limitCriteria) {
	this.limitCriteria = limitCriteria;
    }

    public double getThresholdCriteria() {
	return thresholdCriteria;
    }

    public void setThresholdCriteria(double thresholdCriteria) {
	this.thresholdCriteria = thresholdCriteria;
    }

    public int getStartCriteria() {
	return startCriteria;
    }

    public void setStartCriteria(int startCriteria) {
	this.startCriteria = startCriteria;
    }

}
