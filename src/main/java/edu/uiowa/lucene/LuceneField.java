package edu.uiowa.lucene;

import java.util.StringTokenizer;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.document.Field;

@SuppressWarnings("serial")

public class LuceneField extends BodyTagSupport {
    LuceneDocument theDocument = null;
    boolean keyField = false;
	String label = null;
	String value = null;
    private static final Log log =LogFactory.getLog(LuceneField.class);

    public static String normalizeContent(String content) {
        // we jump through this tokenization hoop due to the significant whitespace present in JSP/tag body text
    	StringBuffer buffer = new StringBuffer();
        StringTokenizer tokenizer = new StringTokenizer(content);

        while (tokenizer.hasMoreTokens())
        	buffer.append(tokenizer.nextToken() + " ");                	
    	
        return buffer.toString().trim();
    }
	
	public int doStartTag() throws JspTagException {
		theDocument = (LuceneDocument)findAncestorWithClass(this, LuceneDocument.class);
		
		if (theDocument == null) {
			throw new JspTagException("Lucene Field tag not nesting in Document instance");
		}
		
		addField(value);

		return EVAL_PAGE;
	}
	
	public int doEndTag() throws JspException {
	    if (this.getBodyContent() != null) {
	        String tagBody = this.getBodyContent().getString();
	        addField(tagBody);
	    }
	    
	    return super.doEndTag();        
	}
	
    @SuppressWarnings("deprecation")
    private void addField(String content) {
        if (content != null && content.length() > 0) {
            if (keyField) {
            	String normalizedContent = normalizeContent(content);
                theDocument.theDocument.add(new Field(label, normalizedContent, Field.Store.YES, Field.Index.NOT_ANALYZED));

                if (log.isDebugEnabled()) {
                    log.debug("\tlabel: " + label + "\tcontent: '" + normalizedContent + "'");
                }
            } else {
                theDocument.theDocument.add(new Field(label, content, Field.Store.NO, Field.Index.ANALYZED));
                
                if (log.isDebugEnabled()) {
                	StringBuffer buffer = new StringBuffer();
                    StringTokenizer tokenizer = new StringTokenizer(content);
                    while (buffer.length() < 50 && tokenizer.hasMoreTokens())
                    	buffer.append(tokenizer.nextToken() + " ");                	
                    log.debug("\tlabel: " + label + "\tcontent: " + buffer + "...");
                }
            }
        }

    }

	public boolean isKeyField() {
		return keyField;
	}

	public void setKeyField(boolean keyField) {
		this.keyField = keyField;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label.trim();
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value.trim();
	}

}
