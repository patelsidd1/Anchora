package com.anchora.aem.core.models.impl;

import java.time.Year;
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

import com.anchora.aem.core.models.Footer;
import com.anchora.aem.core.models.LinkItem;

@Model(adaptables = SlingHttpServletRequest.class,
        adapters = Footer.class,
        resourceType = FooterImpl.RESOURCE_TYPE,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class FooterImpl implements Footer {

    static final String RESOURCE_TYPE = "anchora/components/footer";
    static final String NN_LINKS = "links";
    static final String DEFAULT_NAV_LABEL = "Footer";
    static final String YEAR_TOKEN = "{year}";

    @Self
    private SlingHttpServletRequest request;

    @SlingObject
    private Resource resource;

    @ValueMapValue
    private String navLabel;

    @ValueMapValue
    private String copyright;

    private List<LinkItem> links;

    @PostConstruct
    protected void init() {
        links = Links.fromMultifield(request, resource.getChild(NN_LINKS));
    }

    @Override
    public String getNavLabel() {
        return StringUtils.defaultIfBlank(navLabel, DEFAULT_NAV_LABEL).trim();
    }

    @Override
    public List<LinkItem> getLinks() {
        return links;
    }

    @Override
    public String getCopyright() {
        String text = StringUtils.trimToNull(copyright);
        return text == null ? null : text.replace(YEAR_TOKEN, String.valueOf(Year.now().getValue()));
    }
}
