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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;

@SuppressWarnings("serial")

public class LuceneUniqueIterator extends BodyTagSupport {
	LuceneSearch theSearch = null;
	LuceneSearchContext theContext = null;
	TopDocs theHits = null;
	ScoreDoc theHit = null;
	private int hitFence = 0;
	private int hitOffset = 0;
	double thresholdFence = 0.0;
	Document theDocument = null;
	String label = null;
	int uniqueCount = 0;

	int limitCriteria = Integer.MAX_VALUE;
	int startCriteria = 1;
	double thresholdCriteria = 0.0;
	static Logger logger = LogManager.getLogger(LuceneUniqueIterator.class);

	public int doStartTag() throws JspException {
		logger.info("limit: " + limitCriteria);
		logger.info("start: " + startCriteria);
		logger.info("threshold: " + thresholdCriteria);
		theSearch = (LuceneSearch) findAncestorWithClass(this, LuceneSearch.class);
		theContext = (LuceneSearchContext) findAncestorWithClass(this, LuceneSearchContext.class);

		if (theSearch == null) {
			throw new JspTagException("Lucene Hit tag not nesting in Search instance");
		}

		if (theContext == null) {
			throw new JspTagException("Lucene unique iterator tag not nested in a SearchContext instance");
		}

		try {
			if (startCriteria < 1) // a missing parameter in the requesting URL
				// results in this getting set to 0.
				startCriteria = 1;
			hitFence = startCriteria - 1;
			theHits = theSearch.theHits;
			logger.info("search result count: " + theHits.totalHits);
			boolean unique = false;
			while (!unique && hitFence < theHits.scoreDocs.length) {
				theHit = theHits.scoreDocs[hitFence++];
				theDocument = theSearch.theSearcher.doc(theHit.doc);
				if (theContext.uniquenessHashExists(theDocument.get(label))) {
					logger.debug("duplicate skipped: " + theDocument.get(label));
					continue;
				}
				logger.debug("unique: " + theDocument.get(label));
				theContext.adduniquenessKey(theDocument.get(label));
				thresholdFence = theHit.score;
				unique = true;
				uniqueCount++;
				if (thresholdFence < thresholdCriteria) {
					clearServiceState();
					return SKIP_BODY;
				}
			}

			if (!unique) {
				clearServiceState();
				return SKIP_BODY;
			} else
				return EVAL_BODY_INCLUDE;
		} catch (CorruptIndexException e) {
			logger.error("Corruption Exception", e);

		} catch (IOException e) {
			logger.error("IO Exception", e);

		}

		return SKIP_BODY;
	}

	public int doAfterBody() throws JspTagException {
		if (limitCriteria <= 0 || limitCriteria <= uniqueCount) {
			clearServiceState();
			return SKIP_BODY;
		}

		try {
			boolean unique = false;
			while (!unique && hitFence < theHits.scoreDocs.length) {
				theHit = theHits.scoreDocs[hitFence++];
				theDocument = theSearch.theSearcher.doc(theHit.doc);
				if (theContext.uniquenessHashExists(theDocument.get(label))) {
					logger.debug("duplicate skipped: " + theDocument.get(label));
					continue;
				}
				logger.debug("unique: " + theDocument.get(label));
				theContext.adduniquenessKey(theDocument.get(label));
				thresholdFence = theHit.score;
				unique = true;
				uniqueCount++;
				if (thresholdFence < thresholdCriteria) {
					clearServiceState();
					return SKIP_BODY;
				}
			}

			if (!unique) {
				clearServiceState();
				return SKIP_BODY;
			} else
				return EVAL_BODY_AGAIN;
		} catch (IOException e) {
			clearServiceState();
			logger.error("IO Exception", e);
			throw (new JspTagException(e.toString()));
		}
	}

	private void clearServiceState() {
		this.theHits = null;
		this.label = null;
		theSearch = null;
		theContext = null;
		theHit = null;
		hitFence = 0;
		uniqueCount = 0;
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
