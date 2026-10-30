package com.anchora.aem.core.models;

/**
 * Teaser: heading, rich text description, optional image and a single call-to-action link.
 */
public interface Teaser {

    /** Optional HTML id, used as an "On this page" anchor target. */
    String getId();

    String getTitle();

    /** {@code h2}-{@code h6} from the template policy ({@code titleType}), defaulting to {@code h2}. */
    String getHeadingElement();

    /** Rich text (HTML) from the RTE. */
    String getDescription();

    ResponsiveImage getImage();

    /** The call to action, or null when the label or target is missing. */
    LinkItem getLink();

    boolean isEmpty();
}
