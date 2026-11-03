package com.anchora.aem.core.models;

import java.util.List;

/**
 * An accordion panel: the Core Text component's rich text, followed by optional call-to-action links
 * ("More about bassinets", for example). The text itself is rendered by Core Text.
 */
public interface AccordionItem {

    /** The links shown under the text. Items without a label or a target are skipped. */
    List<LinkItem> getLinks();
}
