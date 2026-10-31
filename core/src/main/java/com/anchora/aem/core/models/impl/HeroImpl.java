package com.anchora.aem.core.models.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import com.anchora.aem.core.models.Hero;
import com.anchora.aem.core.models.LinkItem;
import com.anchora.aem.core.models.ResponsiveImage;
import com.day.cq.commons.jcr.JcrConstants;

@Model(adaptables = SlingHttpServletRequest.class,
        adapters = Hero.class,
        resourceType = HeroImpl.RESOURCE_TYPE,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class HeroImpl implements Hero {

    static final String RESOURCE_TYPE = "anchora/components/hero";
    static final String NN_JUMP_LINKS = "jumpLinks";
    static final String PN_ANCHOR = "anchor";
    static final String DEFAULT_JUMP_LINKS_TITLE = "On this page";

    @SlingObject
    private Resource resource;

    @SlingObject
    private ResourceResolver resolver;

    @ValueMapValue(name = JcrConstants.JCR_TITLE)
    private String title;

    @ValueMapValue
    private String intro;

    @ValueMapValue
    private String jumpLinksTitle;

    private ResponsiveImage image;
    private List<LinkItem> jumpLinks;

    @PostConstruct
    protected void init() {
        image = ResponsiveImages.from(resolver, resource.getValueMap());
        jumpLinks = readJumpLinks(resource.getChild(NN_JUMP_LINKS));
    }

    /** Anchors may be authored with or without the leading "#". Incomplete entries are skipped. */
    private static List<LinkItem> readJumpLinks(Resource multifield) {
        if (multifield == null) {
            return Collections.emptyList();
        }
        List<LinkItem> links = new ArrayList<>();
        for (Resource item : multifield.getChildren()) {
            ValueMap props = item.getValueMap();
            String label = StringUtils.trimToNull(props.get(Links.PN_LABEL, String.class));
            String anchor = Links.anchorId(StringUtils.removeStart(
                    StringUtils.trim(props.get(PN_ANCHOR, String.class)), "#"));
            if (label != null && anchor != null) {
                links.add(new LinkItem(label, "#" + anchor, false));
            }
        }
        return Collections.unmodifiableList(links);
    }

    @Override
    public String getTitle() {
        return StringUtils.trimToNull(title);
    }

    @Override
    public String getIntro() {
        return StringUtils.trimToNull(intro);
    }

    @Override
    public ResponsiveImage getImage() {
        return image;
    }

    @Override
    public String getJumpLinksTitle() {
        return StringUtils.defaultIfBlank(StringUtils.trim(jumpLinksTitle), DEFAULT_JUMP_LINKS_TITLE);
    }

    @Override
    public List<LinkItem> getJumpLinks() {
        return jumpLinks;
    }

    @Override
    public boolean isEmpty() {
        return getTitle() == null;
    }
}
