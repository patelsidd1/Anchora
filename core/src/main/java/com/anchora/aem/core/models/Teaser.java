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

    /** Optional plain-text line shown under the title. */
    String getSubtitle();

    /** Rich text (HTML) from the RTE. */
    String getDescription();

    ResponsiveImage getImage();

    /** Side the image sits on from tablet width up: {@code left} (default) or {@code right}. */
    String getImagePosition();

    /** The call to action, or null when the label or target is missing. */
    LinkItem getLink();

    /** True when the author ticked "Show a Back to top link" under the teaser. */
    boolean isBackToTop();

    /** Text of the Back to top link, defaulting to "Back to top". */
    String getBackToTopLabel();

    boolean isEmpty();
}
