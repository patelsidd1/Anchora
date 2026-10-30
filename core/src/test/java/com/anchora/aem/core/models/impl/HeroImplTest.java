package com.anchora.aem.core.models.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;

import org.apache.sling.api.resource.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.anchora.aem.core.models.Hero;
import com.anchora.aem.core.models.LinkItem;
import com.anchora.aem.core.testcontext.AppAemContext;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

@ExtendWith(AemContextExtension.class)
class HeroImplTest {

    private static final String HERO = "/content/anchora/us/en/test/jcr:content/hero";
    private static final String ASSET = "/content/dam/anchora/hero.jpg";

    private final AemContext context = AppAemContext.newAemContext();

    @BeforeEach
    void setUp() {
        context.addModelsForClasses(HeroImpl.class);
        context.create().page("/content/anchora/us/en/test");
        context.create().asset(ASSET, 1920, 800, "image/jpeg",
                Map.of("tiff:ImageWidth", 1920L, "tiff:ImageLength", 800L, "dc:description", "A paper plane"));
    }

    private Hero adapt(Resource hero) {
        context.currentResource(hero);
        return context.request().adaptTo(Hero.class);
    }

    @Test
    void readsAuthoredContent() {
        Resource hero = context.create().resource(HERO,
                "sling:resourceType", HeroImpl.RESOURCE_TYPE,
                "jcr:title", "Flying with infants and children",
                "intro", "Plan ahead.",
                "fileReference", ASSET);
        context.create().resource(hero, "jumpLinks/item0", "label", "Joey Club", "anchor", "#joey-club");
        context.create().resource(hero, "jumpLinks/item1", "label", "Booking for infants",
                "anchor", "booking-for-infants");
        context.create().resource(hero, "jumpLinks/item2", "label", "No anchor");

        Hero model = adapt(hero);

        assertEquals("Flying with infants and children", model.getTitle());
        assertEquals("Plan ahead.", model.getIntro());
        assertFalse(model.isEmpty());
        assertNotNull(model.getImage());
        assertEquals("A paper plane", model.getImage().getAlt());
        List<LinkItem> links = model.getJumpLinks();
        assertEquals(2, links.size());
        assertEquals("Joey Club", links.get(0).getLabel());
        assertEquals("#joey-club", links.get(0).getHref());
        assertEquals("#booking-for-infants", links.get(1).getHref());
    }

    @Test
    void isEmptyWithoutTitle() {
        Hero model = adapt(context.create().resource(HERO,
                "sling:resourceType", HeroImpl.RESOURCE_TYPE, "jcr:title", "  ", "fileReference", ASSET));

        assertTrue(model.isEmpty());
        assertNull(model.getTitle());
        assertTrue(model.getJumpLinks().isEmpty());
    }

    @Test
    void usesAuthoredJumpLinksTitle() {
        Hero model = adapt(context.create().resource(HERO,
                "sling:resourceType", HeroImpl.RESOURCE_TYPE,
                "jcr:title", "Title", "jumpLinksTitle", "  In this guide "));

        assertEquals("In this guide", model.getJumpLinksTitle());
    }

    @Test
    void defaultsJumpLinksTitleWhenBlank() {
        Hero model = adapt(context.create().resource(HERO,
                "sling:resourceType", HeroImpl.RESOURCE_TYPE,
                "jcr:title", "Title", "jumpLinksTitle", "   "));

        assertEquals("On this page", model.getJumpLinksTitle());
    }

    @Test
    void rendersWithoutImageWhenAssetIsMissing() {
        Hero model = adapt(context.create().resource(HERO,
                "sling:resourceType", HeroImpl.RESOURCE_TYPE,
                "jcr:title", "Title", "fileReference", "/content/dam/anchora/deleted.jpg"));

        assertFalse(model.isEmpty());
        assertNull(model.getImage());
        assertNull(model.getIntro());
    }
}
