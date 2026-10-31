package com.anchora.aem.core.models;

import com.adobe.cq.wcm.core.components.models.Accordion;

/**
 * Core Components {@link Accordion} with an authorable section heading, introduction and "Expand all" button.
 * All items always start collapsed, so {@link #getExpandedItems()} is empty.
 */
public interface SectionAccordion extends Accordion {

    String getSectionTitle();

    String getSectionIntro();

    /** Label of the button that opens every item. Defaults to "Expand all items". */
    String getExpandAllLabel();

    /** Label the same button shows once every item is open. Defaults to "Collapse all items". */
    String getCollapseAllLabel();
}
