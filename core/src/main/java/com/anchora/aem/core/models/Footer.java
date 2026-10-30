package com.anchora.aem.core.models;

import java.util.List;

/**
 * Site footer: back to top link, logo, link columns, social links, legal links, copyright and an
 * acknowledgement. Every part is optional and set in the footer experience fragment.
 */
public interface Footer {

    boolean isBackToTop();

    /** The authored label, or "Back to top". */
    String getBackToTopLabel();

    /** The logo image, or null to show {@link #getBrandLabel()} as text. */
    ResponsiveImage getLogo();

    /** The brand name: the logo's alt text, or the text shown when there is no logo. Defaults to "Anchora". */
    String getBrandLabel();

    /** Where the logo links to, or null for no link. */
    String getBrandHref();

    /** Accessible name of the {@code <nav>} landmark that holds the link columns. */
    String getNavLabel();

    /** Link columns. Columns without a heading and without links are skipped. */
    List<LinkGroup> getColumns();

    /** Heading above the social links. Defaults to "Connect with us". */
    String getSocialTitle();

    /** Social links. Items with an unknown platform or without a link are skipped. */
    List<SocialLink> getSocialLinks();

    /** Legal links, such as privacy and terms of use. */
    List<LinkItem> getLinks();

    /** The authored copyright line with {@code {year}} replaced by the current year. */
    String getCopyright();

    /** Text for the band at the bottom of the footer, for example an Acknowledgement of Country. */
    String getAcknowledgement();
}
