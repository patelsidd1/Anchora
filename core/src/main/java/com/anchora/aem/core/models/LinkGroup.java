package com.anchora.aem.core.models;

import java.util.List;

/**
 * A heading with a list of links, such as one column of the footer.
 */
public final class LinkGroup {

    private final String title;
    private final List<LinkItem> links;

    public LinkGroup(String title, List<LinkItem> links) {
        this.title = title;
        this.links = links;
    }

    /** Null when the author left the heading blank. */
    public String getTitle() {
        return title;
    }

    public List<LinkItem> getLinks() {
        return links;
    }
}
