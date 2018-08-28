package edu.uiowa.lucene;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@SuppressWarnings("serial")

public class LuceneDrillDownFacet extends BodyTagSupport {
    private static final Log log = LogFactory.getLog(LuceneDrillDownFacet.class);

    LuceneTaxonomy theTaxonomy = null;
    String categoryPath = null;

    public int doStartTag() throws JspTagException {
	theTaxonomy = (LuceneTaxonomy) findAncestorWithClass(this, LuceneTaxonomy.class);

	if (theTaxonomy == null) {
	    throw new JspTagException("Lucene drill down facet tag not nesting in Taxonomy instance");
	}
	
	log.info("adding drill down category path " + categoryPath);
	theTaxonomy.addDrillDownFacet(categoryPath);

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

}
