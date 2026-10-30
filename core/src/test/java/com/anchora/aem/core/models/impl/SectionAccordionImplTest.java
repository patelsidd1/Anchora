package com.anchora.aem.core.models.impl;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.apache.sling.api.resource.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.adobe.cq.export.json.ComponentExporter;
import com.anchora.aem.core.models.SectionAccordion;
import com.anchora.aem.core.testcontext.AppAemContext;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

/**
 * Uses the real Core Components Accordion model (from core.wcm.components.core) as the delegate.
 */
@ExtendWith(AemContextExtension.class)
class SectionAccordionImplTest {

    private static final String ACCORDION = "/content/anchora/us/en/test/jcr:content/accordion";
    private static final String TEXT = "anchora/components/text";

    private final AemContext context = AppAemContext.newAemContext();

    @BeforeEach
    void setUp() {
        context.addModelsForClasses(SectionAccordionImpl.class);
        // the proxy components, as deployed by ui.apps
        context.create().resource("/apps/" + SectionAccordionImpl.RESOURCE_TYPE,
                "sling:resourceSuperType", "core/wcm/components/accordion/v1/accordion");
        context.create().resource("/apps/" + TEXT,
                "sling:resourceSuperType", "core/wcm/components/text/v2/text");
        context.create().page("/content/anchora/us/en/test");
    }

    private SectionAccordion adapt(Object... props) {
        Resource accordion = context.create().resource(ACCORDION, props);
        context.create().resource(accordion, "item_1", "sling:resourceType", TEXT,
                "cq:panelTitle", "Infants on domestic flights", "text", "<p>One</p>");
        context.create().resource(accordion, "item_2", "sling:resourceType", TEXT,
                "cq:panelTitle", "Infants on international flights", "text", "<p>Two</p>");
        context.currentResource(accordion);
        return context.request().adaptTo(SectionAccordion.class);
    }

    @Test
    void addsSectionHeadingAndIntro() {
        SectionAccordion accordion = adapt("sling:resourceType", SectionAccordionImpl.RESOURCE_TYPE,
                "sectionTitle", " Booking for infants (under 2 years) ", "sectionIntro", "Read before you book.");

        assertEquals("Booking for infants (under 2 years)", accordion.getSectionTitle());
        assertEquals("Read before you book.", accordion.getSectionIntro());
    }

    @Test
    void delegatesItemsAndSettingsToCore() {
        SectionAccordion accordion = adapt("sling:resourceType", SectionAccordionImpl.RESOURCE_TYPE,
                "id", "booking-for-infants", "singleExpansion", true);

        assertEquals("booking-for-infants", accordion.getId());
        assertTrue(accordion.isSingleExpansion());
        assertEquals(2, accordion.getChildren().size());
        assertEquals("Infants on domestic flights", accordion.getChildren().get(0).getTitle());
        assertEquals("Infants on international flights", accordion.getChildren().get(1).getTitle());
        assertArrayEquals(new String[] {"item_1", "item_2"}, accordion.getExportedItemsOrder());
    }

    @Test
    void allItemsStartCollapsedEvenIfExpandedItemsWereSaved() {
        SectionAccordion accordion = adapt("sling:resourceType", SectionAccordionImpl.RESOURCE_TYPE,
                "expandedItems", new String[] {"item_1"});

        assertEquals(0, accordion.getExpandedItems().length);
    }

    @Test
    void exportsOwnResourceType() {
        adapt("sling:resourceType", SectionAccordionImpl.RESOURCE_TYPE);
        ComponentExporter exporter = context.request().adaptTo(ComponentExporter.class);

        assertEquals(SectionAccordionImpl.RESOURCE_TYPE, exporter.getExportedType());
    }

    @Test
    void sectionFieldsAreOptional() {
        SectionAccordion accordion = adapt("sling:resourceType", SectionAccordionImpl.RESOURCE_TYPE);

        assertNull(accordion.getSectionTitle());
        assertNull(accordion.getSectionIntro());
    }
}
