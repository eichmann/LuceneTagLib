package edu.uiowa.lucene;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.lucene.document.Field;

@SuppressWarnings("serial")

public class LuceneField extends BodyTagSupport {
    LuceneDocument theDocument = null;
    boolean keyField = false;
	String label = null;
	String value = null;
	
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
	
    private void addField(String content) {
        if (content != null && content.length() > 0) {
//            System.out.println("label: " + label + "\tcontent: " + content);
            if (keyField)
                theDocument.theDocument.add(new Field(label.trim(), content.trim(), Field.Store.YES, Field.Index.NOT_ANALYZED));
            else
                theDocument.theDocument.add(new Field(label.trim(), content.trim(), Field.Store.NO, Field.Index.ANALYZED));
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
		this.label = label;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
