package com.anchora.aem.core.models;

/**
 * Everything HTL needs to render an {@code <img>}: a fallback {@code src}, an optional {@code srcset}
 * with width descriptors, alt text and the intrinsic size (used only to reserve the aspect ratio).
 */
public final class ResponsiveImage {

    private final String src;
    private final String srcset;
    private final String alt;
    private final boolean decorative;
    private final Integer width;
    private final Integer height;

    public ResponsiveImage(String src, String srcset, String alt, boolean decorative, Integer width, Integer height) {
        this.src = src;
        this.srcset = srcset;
        this.alt = alt;
        this.decorative = decorative;
        this.width = width;
        this.height = height;
    }

    public String getSrc() {
        return src;
    }

    /** Comma-separated candidates such as {@code "/a.jpeg 640w, /b.jpeg 1280w"}, or null. */
    public String getSrcset() {
        return srcset;
    }

    /** Never null. Empty when the image is decorative or no alt text is available. */
    public String getAlt() {
        return alt;
    }

    public boolean isDecorative() {
        return decorative;
    }

    public Integer getWidth() {
        return width;
    }

    public Integer getHeight() {
        return height;
    }
}
