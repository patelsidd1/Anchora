package com.anchora.aem.core.models.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.apache.sling.api.resource.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.anchora.aem.core.models.AccordionItem;
import com.anchora.aem.core.models.LinkItem;
import com.anchora.aem.core.testcontext.AppAemContext;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

@ExtendWith(AemContextExtension.class)
class AccordionItemImplTest {

    private static final String ITEM = "/content/anchora/us/en/test/jcr:content/accordion/item_1";

    private final AemContext context = AppAemContext.newAemContext();

    @BeforeEach
    void setUp() {
        context.addModelsForClasses(AccordionItemImpl.class);
    }

    private AccordionItem adapt(Resource item) {
        context.currentResource(item);
        return context.request().adaptTo(AccordionItem.class);
    }

    @Test
    void readsLinksInOrder() {
        Resource item = context.create().resource(ITEM, "sling:resourceType", AccordionItemImpl.RESOURCE_TYPE,
                "text", "<p>Bassinets are subject to availability.</p>");
        Resource links = context.create().resource(item, "links");
        context.create().resource(links, "item0", "label", "More about bassinets",
                "link", "/content/anchora/us/en/bassinets");
        context.create().resource(links, "item1", "label", "Incomplete");
        context.create().resource(links, "item2", "label", "Contact us", "link", "https://www.example.com/",
                "newTab", true);

        List<LinkItem> result = adapt(item).getLinks();

        assertEquals(2, result.size());
        assertEquals("More about bassinets", result.get(0).getLabel());
        assertEquals("/content/anchora/us/en/bassinets.html", result.get(0).getHref());
        assertEquals("Contact us", result.get(1).getLabel());
        assertEquals("_blank", result.get(1).getTarget());
    }

    @Test
    void itemWithoutLinksHasEmptyList() {
        Resource item = context.create().resource(ITEM, "sling:resourceType", AccordionItemImpl.RESOURCE_TYPE);

        assertTrue(adapt(item).getLinks().isEmpty());
    }
}
