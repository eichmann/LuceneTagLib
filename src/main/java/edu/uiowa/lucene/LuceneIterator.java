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

import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;

@SuppressWarnings("serial")

public class LuceneIterator extends BodyTagSupport {
    LuceneSearch theSearch = null;
    TopDocs theHits = null;
    ScoreDoc theHit = null;
    int hitFence = 0;
    Document theDocument = null;
    String label = null;

    int limitCriteria = 0;

    public int doStartTag() throws JspException {
	    theSearch = (LuceneSearch)findAncestorWithClass(this, LuceneSearch.class);
		
		if (theSearch == null) {
			throw new JspTagException("Lucene Hit tag not nesting in Search instance");
		}
		
        try {
            theHits = theSearch.theHits;
            if (hitFence < theHits.scoreDocs.length) {
                theHit = theHits.scoreDocs[hitFence++];
                theDocument = theSearch.theSearcher.doc(theHit.doc);
                return EVAL_BODY_INCLUDE;
            }
        } catch (CorruptIndexException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return SKIP_BODY;
    }

    public int doAfterBody() throws JspTagException {
        if (limitCriteria > 0 && hitFence >= limitCriteria) {
            clearServiceState();
            return SKIP_BODY;
        }
        
        if (hitFence < theHits.scoreDocs.length) {
            theHit = theHits.scoreDocs[hitFence++];
            try {
				theDocument = theSearch.theSearcher.doc(theHit.doc);
			} catch (CorruptIndexException e) {
				e.printStackTrace();
				throw(new JspTagException(e.toString()));
			} catch (IOException e) {
				e.printStackTrace();
				throw(new JspTagException(e.toString()));
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

}
