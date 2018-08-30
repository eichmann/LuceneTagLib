package edu.uiowa.lucene;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@SuppressWarnings("serial")

public class LuceneDrillDownProcessor extends BodyTagSupport {
    private static final Log log = LogFactory.getLog(LuceneDrillDownProcessor.class);

    LuceneTaxonomy theTaxonomy = null;
    String categoryPaths = null;
    ArrayList<String> categoryPathList = new ArrayList<String>();
    String drillUpCategory = null;
    String drillOutCategory = null;

    public int doStartTag() throws JspTagException {
	theTaxonomy = (LuceneTaxonomy) findAncestorWithClass(this, LuceneTaxonomy.class);
	log.trace("doStartTag - categoryPaths: " + categoryPaths + "\tdrillUpCategory: " + drillUpCategory + "\tdrillOutCategory: " + drillOutCategory);

	if (theTaxonomy == null) {
	    throw new JspTagException("Lucene drill down facet tag not nesting in Taxonomy instance");
	}
	
	if (categoryPaths == null || categoryPaths.length() == 0)
	    return EVAL_PAGE;
	
	for (String categoryPath : categoryPaths.split("\\|")) {
	    for (int i = 0; i < categoryPathList.size(); i++) {
		if (categoryPath.startsWith(categoryPathList.get(i)))
		    categoryPathList.remove(i);
	    }
	    categoryPathList.add(categoryPath);
	}
	log.info("previous category path list: " + categoryPathList);
	
	if (drillUpCategory != null && drillUpCategory.length() > 0) {
	    log.info("removing classpath: " + drillUpCategory);
	    ArrayList<String> temp = new ArrayList<String>();
	    for (String current : categoryPathList) {
		if (!current.equals(drillUpCategory))
		    temp.add(current);
	    }
	    categoryPathList = temp;
	}
	
	if (drillOutCategory != null && drillOutCategory.length() > 0) {
	    String newClassPath = drillOutCategory.substring(0, drillOutCategory.lastIndexOf("/"));
	    log.info("shifting from classpath: " + drillOutCategory + " to " + newClassPath);
	    categoryPathList.remove(drillOutCategory);
	    categoryPathList.add(newClassPath);
	}
	
	for (String categoryPath : categoryPathList) {
	    log.info("adding drill down category path " + categoryPath);
	    theTaxonomy.addDrillDownFacet(categoryPath);
	    try {
		pageContext.getOut().print(categoryPath + "|");
	    } catch (IOException e) {
		log.error("exception raise: ", e);
	    }
	}

	return EVAL_PAGE;
    }

    public int doEndTag() throws JspException {
	clearServiceState();
	return super.doEndTag();
    }

    private void clearServiceState() {
	theTaxonomy = null;
	categoryPaths = null;
	categoryPathList = new ArrayList<String>();
	drillUpCategory = null;
	drillOutCategory = null;
    }

     public String getCategoryPaths() {
        return categoryPaths;
    }

    public void setCategoryPaths(String categoryPaths) {
        this.categoryPaths = categoryPaths;
    }

    public String getDrillUpCategory() {
        return drillUpCategory;
    }

    public void setDrillUpCategory(String drillUpCategory) {
        this.drillUpCategory = drillUpCategory;
    }

    public String getDrillOutCategory() {
        return drillOutCategory;
    }

    public void setDrillOutCategory(String drillOutCategory) {
        this.drillOutCategory = drillOutCategory;
    }

}
