package com.anchora.aem.core.models.impl;

import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import com.anchora.aem.core.models.LinkItem;
import com.anchora.aem.core.models.ResponsiveImage;
import com.anchora.aem.core.models.Teaser;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.wcm.api.designer.Style;

@Model(adaptables = SlingHttpServletRequest.class,
        adapters = Teaser.class,
        resourceType = TeaserImpl.RESOURCE_TYPE,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class TeaserImpl implements Teaser {

    static final String RESOURCE_TYPE = "anchora/components/teaser";
    /** Policy property, set in the teaser's design dialog. Same name as the Core Teaser uses. */
    static final String PN_TITLE_TYPE = "titleType";
    static final String DEFAULT_HEADING = "h2";
    private static final Set<String> ALLOWED_HEADINGS = Set.of("h2", "h3", "h4", "h5", "h6");
    static final String IMAGE_LEFT = "left";
    static final String IMAGE_RIGHT = "right";

    @Self
    private SlingHttpServletRequest request;

    @SlingObject
    private Resource resource;

    @SlingObject
    private ResourceResolver resolver;

    @ScriptVariable
    private Style currentStyle;

    @ValueMapValue
    private String id;

    @ValueMapValue(name = JcrConstants.JCR_TITLE)
    private String title;

    @ValueMapValue
    private String subtitle;

    @ValueMapValue
    private String description;

    @ValueMapValue
    private String imagePosition;

    @ValueMapValue
    private String linkLabel;

    @ValueMapValue
    private String linkURL;

    @ValueMapValue
    private boolean linkNewTab;

    private String headingElement;
    private ResponsiveImage image;
    private LinkItem link;

    @PostConstruct
    protected void init() {
        String configured = currentStyle == null ? null : currentStyle.get(PN_TITLE_TYPE, String.class);
        headingElement = configured != null && ALLOWED_HEADINGS.contains(configured) ? configured : DEFAULT_HEADING;
        image = ResponsiveImages.from(resolver, resource.getValueMap());
        link = Links.create(request, linkLabel, linkURL, linkNewTab);
    }

    @Override
    public String getId() {
        return StringUtils.trimToNull(id);
    }

    @Override
    public String getTitle() {
        return StringUtils.trimToNull(title);
    }

    @Override
    public String getHeadingElement() {
        return headingElement;
    }

    @Override
    public String getSubtitle() {
        return StringUtils.trimToNull(subtitle);
    }

    @Override
    public String getDescription() {
        return StringUtils.trimToNull(description);
    }

    @Override
    public ResponsiveImage getImage() {
        return image;
    }

    @Override
    public String getImagePosition() {
        return IMAGE_RIGHT.equals(imagePosition) ? IMAGE_RIGHT : IMAGE_LEFT;
    }

    @Override
    public LinkItem getLink() {
        return link;
    }

    @Override
    public boolean isEmpty() {
        return getTitle() == null && getSubtitle() == null && getDescription() == null && image == null;
    }
}
