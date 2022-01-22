package edu.uiowa.lucene;

import java.util.Hashtable;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SuppressWarnings("serial")

public class LuceneSearchContext extends BodyTagSupport {
	static Logger logger = LogManager.getLogger(LuceneSearchContext.class);

	Hashtable<String, String> uniquenessHash = null;
	int limitCriteria = Integer.MAX_VALUE;

	public int doStartTag() throws JspException {
		logger.info("context initialized: " + limitCriteria);
		uniquenessHash = new Hashtable<String,String>();
		return EVAL_BODY_INCLUDE;
	}

	public int doEndTag() throws JspException {
		clearServiceState();
		return super.doEndTag();
	}

	private void clearServiceState() {
		uniquenessHash = null;
	}

	public int getLimitCriteria() {
		return limitCriteria;
	}

	public void setLimitCriteria(int limitCriteria) {
		this.limitCriteria = limitCriteria;
	}

	public boolean uniquenessHashExists(String key) {
		if (key == null)
			return false;
		return uniquenessHash.containsKey(key);
	}
	
	public void adduniquenessKey(String key) {
		uniquenessHash.put(key, key);
	}

}
