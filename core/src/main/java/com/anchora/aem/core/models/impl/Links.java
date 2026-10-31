package com.anchora.aem.core.models.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;

import com.anchora.aem.core.models.LinkGroup;
import com.anchora.aem.core.models.LinkItem;

/**
 * Turns authored link values into hrefs and reads link multifields.
 */
final class Links {

    static final String PN_LABEL = "label";
    static final String PN_LINK = "link";
    static final String PN_NEW_TAB = "newTab";
    static final String PN_TITLE = "title";
    static final String NN_LINKS = "links";

    private static final String CONTENT_ROOT = "/content/";
    private static final String DAM_ROOT = "/content/dam/";

    private Links() {
    }

    /**
     * Internal page paths (under /content, outside the DAM, without an extension) are mapped and get
     * ".html"; any "#fragment" or "?query" is kept. Everything else (external URLs, "#anchors",
     * "mailto:", "tel:", assets) is returned unchanged.
     *
     * @return the href, or null when the link is blank
     */
    static String resolve(SlingHttpServletRequest request, String link) {
        String value = StringUtils.trimToNull(link);
        if (value == null) {
            return null;
        }
        int suffixStart = StringUtils.indexOfAny(value, '#', '?');
        String path = suffixStart < 0 ? value : value.substring(0, suffixStart);
        String suffix = suffixStart < 0 ? "" : value.substring(suffixStart);
        if (!isInternalPagePath(path)) {
            return value;
        }
        return request.getResourceResolver().map(request, path) + ".html" + suffix;
    }

    /**
     * @return the link, or null when the label or target is blank
     */
    static LinkItem create(SlingHttpServletRequest request, String label, String link, boolean newTab) {
        String text = StringUtils.trimToNull(label);
        String href = resolve(request, link);
        return text == null || href == null ? null : new LinkItem(text, href, newTab);
    }

    /**
     * Makes an authored ID safe to use as an HTML id and a "#" link target. An id cannot contain
     * spaces, so they become hyphens ("Joey club" becomes "Joey-club"), as in the Core Components.
     *
     * @return the ID, or null when it is blank
     */
    static String anchorId(String id) {
        return StringUtils.replace(StringUtils.normalizeSpace(StringUtils.trimToNull(id)), " ", "-");
    }

    /**
     * Reads a composite multifield ({@code item0}, {@code item1}...) with label, link and newTab
     * properties. Incomplete items are skipped.
     */
    static List<LinkItem> fromMultifield(SlingHttpServletRequest request, Resource multifield) {
        if (multifield == null) {
            return Collections.emptyList();
        }
        List<LinkItem> links = new ArrayList<>();
        for (Resource item : multifield.getChildren()) {
            ValueMap props = item.getValueMap();
            LinkItem link = create(request, props.get(PN_LABEL, String.class), props.get(PN_LINK, String.class),
                    props.get(PN_NEW_TAB, false));
            if (link != null) {
                links.add(link);
            }
        }
        return Collections.unmodifiableList(links);
    }

    /**
     * Reads a composite multifield whose items have a title and their own nested links multifield, such as the
     * footer columns and the header menus. Items with no title and no links are skipped.
     */
    static List<LinkGroup> groupsFromMultifield(SlingHttpServletRequest request, Resource multifield) {
        if (multifield == null) {
            return Collections.emptyList();
        }
        List<LinkGroup> groups = new ArrayList<>();
        for (Resource item : multifield.getChildren()) {
            String title = StringUtils.trimToNull(item.getValueMap().get(PN_TITLE, String.class));
            List<LinkItem> links = fromMultifield(request, item.getChild(NN_LINKS));
            if (title != null || !links.isEmpty()) {
                groups.add(new LinkGroup(title, links));
            }
        }
        return Collections.unmodifiableList(groups);
    }

    private static boolean isInternalPagePath(String path) {
        if (!path.startsWith(CONTENT_ROOT) || path.startsWith(DAM_ROOT)) {
            return false;
        }
        String lastSegment = path.substring(path.lastIndexOf('/') + 1);
        return !lastSegment.contains(".");
    }
}
