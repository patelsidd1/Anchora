package com.anchora.aem.core.models;

/**
 * A rendered link: label, resolved {@code href} and the attributes needed when it opens in a new tab.
 */
public final class LinkItem {

    static final String TARGET_BLANK = "_blank";
    static final String REL_NEW_TAB = "noopener noreferrer";

    private final String label;
    private final String href;
    private final boolean newTab;

    public LinkItem(String label, String href, boolean newTab) {
        this.label = label;
        this.href = href;
        this.newTab = newTab;
    }

    public String getLabel() {
        return label;
    }

    public String getHref() {
        return href;
    }

    public boolean isNewTab() {
        return newTab;
    }

    /** {@code _blank} for new-tab links, otherwise null so HTL omits the attribute. */
    public String getTarget() {
        return newTab ? TARGET_BLANK : null;
    }

    /** {@code noopener noreferrer} for new-tab links, otherwise null so HTL omits the attribute. */
    public String getRel() {
        return newTab ? REL_NEW_TAB : null;
    }
}
