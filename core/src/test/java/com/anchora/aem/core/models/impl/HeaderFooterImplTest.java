package com.anchora.aem.core.models.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Year;
import java.util.List;

import org.apache.sling.api.resource.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.anchora.aem.core.models.Footer;
import com.anchora.aem.core.models.Header;
import com.anchora.aem.core.models.LinkItem;
import com.anchora.aem.core.testcontext.AppAemContext;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

@ExtendWith(AemContextExtension.class)
class HeaderFooterImplTest {

    private static final String ROOT = "/content/experience-fragments/anchora/test/jcr:content/root";

    private final AemContext context = AppAemContext.newAemContext();

    @BeforeEach
    void setUp() {
        context.addModelsForClasses(HeaderImpl.class, FooterImpl.class);
    }

    private void addLinks(Resource parent) {
        context.create().resource(parent, "links/item0", "label", "Home", "link", "/content/anchora/us/en");
        context.create().resource(parent, "links/item1", "label", "Example", "link", "https://example.com",
                "newTab", true);
    }

    @Test
    void headerReadsBrandAndNavigation() {
        Resource header = context.create().resource(ROOT + "/header",
                "sling:resourceType", HeaderImpl.RESOURCE_TYPE,
                "brandLabel", "Anchora Air", "brandLink", "/content/anchora/us/en", "navLabel", "Primary");
        addLinks(header);
        context.currentResource(header);

        Header model = context.request().adaptTo(Header.class);

        assertEquals("Anchora Air", model.getBrandLabel());
        assertEquals("/content/anchora/us/en.html", model.getBrandHref());
        assertEquals("Primary", model.getNavLabel());
        List<LinkItem> links = model.getLinks();
        assertEquals(2, links.size());
        assertEquals("/content/anchora/us/en.html", links.get(0).getHref());
        assertEquals("noopener noreferrer", links.get(1).getRel());
    }

    @Test
    void headerHasDefaults() {
        context.currentResource(context.create().resource(ROOT + "/header",
                "sling:resourceType", HeaderImpl.RESOURCE_TYPE));

        Header model = context.request().adaptTo(Header.class);

        assertEquals("Anchora", model.getBrandLabel());
        assertNull(model.getBrandHref());
        assertEquals("Main", model.getNavLabel());
        assertTrue(model.getLinks().isEmpty());
    }

    @Test
    void footerReplacesYearToken() {
        Resource footer = context.create().resource(ROOT + "/footer",
                "sling:resourceType", FooterImpl.RESOURCE_TYPE, "copyright", "© {year} Anchora");
        addLinks(footer);
        context.currentResource(footer);

        Footer model = context.request().adaptTo(Footer.class);

        assertEquals("© " + Year.now().getValue() + " Anchora", model.getCopyright());
        assertEquals("Footer", model.getNavLabel());
        assertEquals(2, model.getLinks().size());
    }

    @Test
    void footerWithoutCopyright() {
        context.currentResource(context.create().resource(ROOT + "/footer",
                "sling:resourceType", FooterImpl.RESOURCE_TYPE, "copyright", " "));

        assertNull(context.request().adaptTo(Footer.class).getCopyright());
    }
}
