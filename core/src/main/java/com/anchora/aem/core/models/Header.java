package com.anchora.aem.core.models;

import java.util.List;

/**
 * Site header. The top row has the logo, a partner badge, the region switch, the cart and the log in button;
 * the second row has the main navigation: dropdown menus and a help link. Every part is optional and set in
 * the header experience fragment.
 */
public interface Header {

    /** The logo image, or null to show {@link #getBrandLabel()} as text. */
    ResponsiveImage getLogo();

    /** The brand name: the logo's alt text, or the text shown when there is no logo. Defaults to "Anchora". */
    String getBrandLabel();

    /** Where the logo links to, or null for no link. */
    String getBrandHref();

    /** A badge shown next to the logo, such as an alliance membership, or null. */
    ResponsiveImage getPartnerLogo();

    /** The flag shown in the region switch, or null. It is decorative: the region text says the same. */
    ResponsiveImage getFlag();

    /** The region code shown in the region switch, such as "AU", or null. */
    String getRegion();

    /** The language code shown in the region switch, such as "EN", or null. */
    String getLanguage();

    /**
     * The region switch link, or null when no link is set. The label is hidden text that tells screen reader
     * users what the link does. Defaults to "Change region and language".
     */
    LinkItem getRegionLink();

    /** The cart icon link, or null. The label is hidden text and defaults to "Cart". */
    LinkItem getCartLink();

    /** The log in button, or null. The label defaults to "Log in". */
    LinkItem getLoginLink();

    /** Accessible name of the {@code <nav>} landmark. Defaults to "Main". */
    String getNavLabel();

    /** Text of the button that opens the navigation on small screens. Defaults to "Menu". */
    String getMenuLabel();

    /** Dropdown menus. Menus without a label or without links are skipped. */
    List<LinkGroup> getMenus();

    /** The help link at the end of the navigation, or null. The label defaults to "Help". */
    LinkItem getHelpLink();
}
