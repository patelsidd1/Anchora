package com.anchora.aem.core.models;

import java.util.List;

/**
 * Placeholder site header: a text logo and the main navigation.
 */
public interface Header {

    String getBrandLabel();

    String getBrandHref();

    /** Accessible name of the {@code <nav>} landmark. */
    String getNavLabel();

    List<LinkItem> getLinks();
}
