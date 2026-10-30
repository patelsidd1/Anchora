package com.anchora.aem.core.models.impl;

import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import com.anchora.aem.core.models.Header;
import com.anchora.aem.core.models.LinkItem;

@Model(adaptables = SlingHttpServletRequest.class,
        adapters = Header.class,
        resourceType = HeaderImpl.RESOURCE_TYPE,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class HeaderImpl implements Header {

    static final String RESOURCE_TYPE = "anchora/components/header";
    static final String NN_LINKS = "links";
    static final String DEFAULT_BRAND = "Anchora";
    static final String DEFAULT_NAV_LABEL = "Main";

    @Self
    private SlingHttpServletRequest request;

    @SlingObject
    private Resource resource;

    @ValueMapValue
    private String brandLabel;

    @ValueMapValue
    private String brandLink;

    @ValueMapValue
    private String navLabel;

    private List<LinkItem> links;

    @PostConstruct
    protected void init() {
        links = Links.fromMultifield(request, resource.getChild(NN_LINKS));
    }

    @Override
    public String getBrandLabel() {
        return StringUtils.defaultIfBlank(brandLabel, DEFAULT_BRAND).trim();
    }

    @Override
    public String getBrandHref() {
        return Links.resolve(request, brandLink);
    }

    @Override
    public String getNavLabel() {
        return StringUtils.defaultIfBlank(navLabel, DEFAULT_NAV_LABEL).trim();
    }

    @Override
    public List<LinkItem> getLinks() {
        return links;
    }
}
