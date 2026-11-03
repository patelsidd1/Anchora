package com.anchora.aem.core.models;

/**
 * A link to a social media profile, shown as an icon.
 */
public final class SocialLink {

    private final String platform;
    private final LinkItem link;

    public SocialLink(String platform, LinkItem link) {
        this.platform = platform;
        this.link = link;
    }

    /** Key such as {@code facebook}. The stylesheet draws the matching icon. */
    public String getPlatform() {
        return platform;
    }

    /** The label is the platform name, which screen readers announce instead of the icon. */
    public LinkItem getLink() {
        return link;
    }
}
