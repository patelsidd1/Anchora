package com.anchora.aem.core.models;

import com.adobe.cq.wcm.core.components.models.Accordion;

/**
 * Core Components {@link Accordion} with an authorable section heading and introduction.
 * All items always start collapsed, so {@link #getExpandedItems()} is empty.
 */
public interface SectionAccordion extends Accordion {

    String getSectionTitle();

    String getSectionIntro();
}
