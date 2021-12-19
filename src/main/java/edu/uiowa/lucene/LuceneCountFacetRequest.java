package edu.uiowa.lucene;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.facet.search.CountFacetRequest;
import org.apache.lucene.facet.taxonomy.CategoryPath;

@SuppressWarnings("serial")

public class LuceneCountFacetRequest extends BodyTagSupport {
	static Logger logger = LogManager.getLogger(LuceneCountFacetRequest.class);

    LuceneTaxonomy theTaxonomy = null;
    String categoryPath = null;
    int resultCount = 10;
    int depth = 0;

    public int doStartTag() throws JspTagException {
	theTaxonomy = (LuceneTaxonomy) findAncestorWithClass(this, LuceneTaxonomy.class);

	if (theTaxonomy == null) {
	    throw new JspTagException("Lucene Concept Facet Request tag not nesting in Taxonomy instance");
	}
	
	logger.trace("adding category path " + categoryPath + ", result count: " + resultCount);
	CountFacetRequest theRequest = new CountFacetRequest(new CategoryPath(categoryPath), resultCount);
	if (depth > 0)
	    theRequest.setDepth(depth);
	theTaxonomy.addFacetSearchParam(theRequest);

	return EVAL_PAGE;
    }

    public int doEndTag() throws JspException {
	return super.doEndTag();
    }

    public String getCategoryPath() {
        return categoryPath;
    }

    public void setCategoryPath(String categoryPath) {
        this.categoryPath = categoryPath;
    }

    public int getResultCount() {
        return resultCount;
    }

    public void setResultCount(int resultCount) {
        this.resultCount = resultCount;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

}
