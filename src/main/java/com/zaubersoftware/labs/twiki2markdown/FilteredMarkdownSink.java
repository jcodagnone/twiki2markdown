package com.zaubersoftware.labs.twiki2markdown;
import java.io.Writer;

import org.apache.maven.doxia.module.markdown.MarkdownSink;


/*
 * Copyright (c) 2012 Zauber S.A.  -- All rights reserved
 */

/**
 * TODO: Description of the class, Comments in english by default  
 * 
 * 
 * @author Cristian Pereyra
 * @since Jan 2, 2012
 */
public class FilteredMarkdownSink extends MarkdownSink {

    String filename;
    String filenameNoExtension;
    
    /**
     * Creates the MarkdownSinkWrapper.
     *
     * @param writer
     */
    public FilteredMarkdownSink(Writer writer, String filename) {
        super(writer);
        String[] splits = filename.split("/");
        this.filename = splits[splits.length - 1];
        this.filenameNoExtension = this.filename.replace(".txt", "");
    }
    
    
    private String filteredText(String text) {
        if (text.matches("^%META.*$") || text.matches("%TOC%")) {
            return "";
        } else if (text.matches("%ATTACHURLPATH%.*")) {
            text = text.replace("%ATTACHURLPATH%", "/images/" + filenameNoExtension);
        }
        return text;
    }
    
    /** @see org.apache.maven.doxia.sink.SinkAdapter#text(java.lang.String, org.apache.maven.doxia.sink.SinkEventAttributes) */
    @Override
    public void text(String text) {
        text = filteredText(text);
        super.text(text);
    }
    
    
    /** @see org.apache.maven.doxia.module.markdown.MarkdownSink#rawText(java.lang.String) */
    @Override
    public void rawText(String text) {
        text = filteredText(text);
        super.rawText(text);
    }

    
    /** @see org.apache.maven.doxia.module.markdown.MarkdownSink#figureGraphics(java.lang.String) */
    @Override
    public void figureGraphics(String name) {
        name = filteredText(name);
        super.figureGraphics(name);
    }
    
}
