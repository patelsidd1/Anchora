package com.anchora.aem.core.models;

import java.util.List;

/**
 * Placeholder site footer: a list of links and a copyright line.
 */
public interface Footer {

    /** Accessible name of the {@code <nav>} landmark. */
    String getNavLabel();

    List<LinkItem> getLinks();

    /** The authored copyright line with {@code {year}} replaced by the current year. */
    String getCopyright();
}
