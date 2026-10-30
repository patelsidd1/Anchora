package com.anchora.aem.core.models;

import java.util.List;

/**
 * Hero banner: the page H1, an introduction, an optional image and "On this page" jump links.
 */
public interface Hero {

    String getTitle();

    String getIntro();

    /** The DAM image, or null when none is set or the asset no longer exists. */
    ResponsiveImage getImage();

    /** Jump links with {@code #anchor} hrefs. Never null. */
    List<LinkItem> getJumpLinks();

    /** True when there is no title. The editor shows a placeholder and publish renders nothing. */
    boolean isEmpty();
}
