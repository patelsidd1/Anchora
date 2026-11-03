package com.anchora.aem.core.models.impl;

import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;

import com.anchora.aem.core.models.AccordionItem;
import com.anchora.aem.core.models.LinkItem;

@Model(adaptables = SlingHttpServletRequest.class,
        adapters = AccordionItem.class,
        resourceType = AccordionItemImpl.RESOURCE_TYPE,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class AccordionItemImpl implements AccordionItem {

    static final String RESOURCE_TYPE = "anchora/components/accordion-item";

    @Self
    private SlingHttpServletRequest request;

    @SlingObject
    private Resource resource;

    private List<LinkItem> links;

    @PostConstruct
    private void init() {
        links = Links.fromMultifield(request, resource.getChild(Links.NN_LINKS));
    }

    @Override
    public List<LinkItem> getLinks() {
        return links;
    }
}
