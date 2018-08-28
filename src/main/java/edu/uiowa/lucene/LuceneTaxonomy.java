package edu.uiowa.lucene;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.facet.params.FacetSearchParams;
import org.apache.lucene.facet.search.FacetRequest;
import org.apache.lucene.facet.taxonomy.TaxonomyReader;
import org.apache.lucene.facet.taxonomy.directory.DirectoryTaxonomyReader;
import org.apache.lucene.store.FSDirectory;

@SuppressWarnings("serial")

public class LuceneTaxonomy extends BodyTagSupport {
    private static final Log log = LogFactory.getLog(LuceneTaxonomy.class);

    String taxonomyPath = null;
    TaxonomyReader taxoReader = null;
    FacetSearchParams fsp = null;
    List<FacetRequest> facetRequests = new ArrayList<FacetRequest>();
    List<String> drillDownFacets = new ArrayList<String>();
    
    public int doStartTag() throws JspException {
	log.info("taxonomy: " + taxonomyPath);
	try {
	    taxoReader = new DirectoryTaxonomyReader(FSDirectory.open(new File(taxonomyPath)));
//	    fsp = new FacetSearchParams();
	} catch (IOException e) {
	    log.error("IO Exception", e);;
	}
	return EVAL_BODY_INCLUDE;
    }

    public int doEndTag() throws JspException {
	clearServiceState();
	return super.doEndTag();
    }
    
    private void clearServiceState() {
	taxonomyPath = null;
	taxoReader = null;
	fsp = null;
	facetRequests = new ArrayList<FacetRequest>();
	drillDownFacets = new ArrayList<String>();
    }

    public String getTaxonomyPath() {
	return taxonomyPath;
    }

    public void setTaxonomyPath(String taxonomyPath) {
	this.taxonomyPath = taxonomyPath;
    }
    
    public void addFacetSearchParam(FacetRequest facetRequest) {
	facetRequests.add(facetRequest);
    }
    
    public void addDrillDownFacet(String facetRequest) {
	drillDownFacets.add(facetRequest);
    }

}
