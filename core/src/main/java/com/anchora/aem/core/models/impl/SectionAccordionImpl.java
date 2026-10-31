package com.anchora.aem.core.models.impl;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Via;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.apache.sling.models.annotations.via.ResourceSuperType;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ContainerExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.adobe.cq.wcm.core.components.models.Accordion;
import com.adobe.cq.wcm.core.components.models.ListItem;
import com.adobe.cq.wcm.core.components.models.PanelContainerItem;
import com.adobe.cq.wcm.core.components.models.datalayer.ComponentData;
import com.anchora.aem.core.models.SectionAccordion;

/**
 * Extends the Core Components Accordion with the delegation pattern: the Core model is adapted through the
 * proxy's resource super type and every Core method is passed through, except {@link #getExpandedItems()}.
 */
@Model(adaptables = SlingHttpServletRequest.class,
        adapters = {SectionAccordion.class, ComponentExporter.class, ContainerExporter.class},
        resourceType = SectionAccordionImpl.RESOURCE_TYPE,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME, extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class SectionAccordionImpl implements SectionAccordion {

    static final String RESOURCE_TYPE = "anchora/components/accordion";
    static final String DEFAULT_EXPAND_ALL_LABEL = "Expand all items";
    static final String DEFAULT_COLLAPSE_ALL_LABEL = "Collapse all items";

    @Self(injectionStrategy = InjectionStrategy.REQUIRED)
    @Via(type = ResourceSuperType.class)
    private Accordion delegate;

    @SlingObject
    private Resource resource;

    @ValueMapValue
    private String sectionTitle;

    @ValueMapValue
    private String sectionIntro;

    @ValueMapValue
    private String expandAllLabel;

    @ValueMapValue
    private String collapseAllLabel;

    @Override
    public String getSectionTitle() {
        return StringUtils.trimToNull(sectionTitle);
    }

    @Override
    public String getSectionIntro() {
        return StringUtils.trimToNull(sectionIntro);
    }

    @Override
    public String getExpandAllLabel() {
        return StringUtils.defaultIfBlank(StringUtils.trim(expandAllLabel), DEFAULT_EXPAND_ALL_LABEL);
    }

    @Override
    public String getCollapseAllLabel() {
        return StringUtils.defaultIfBlank(StringUtils.trim(collapseAllLabel), DEFAULT_COLLAPSE_ALL_LABEL);
    }

    /** AC-04: every item starts collapsed, whatever was saved in the Core "expanded items" setting. */
    @Override
    public String[] getExpandedItems() {
        return new String[0];
    }

    /** The delegate sees the Core resource type, so report this component's own type. */
    @Override
    public String getExportedType() {
        return resource.getResourceType();
    }

    @Override
    public boolean isSingleExpansion() {
        return delegate.isSingleExpansion();
    }

    @Override
    public String getHeadingElement() {
        return delegate.getHeadingElement();
    }

    @Override
    public List<? extends PanelContainerItem> getChildren() {
        return delegate.getChildren();
    }

    @Override
    @SuppressWarnings("deprecation")
    public List<ListItem> getItems() {
        return delegate.getItems();
    }

    @Override
    public String getBackgroundStyle() {
        return delegate.getBackgroundStyle();
    }

    @Override
    public Map<String, ? extends ComponentExporter> getExportedItems() {
        return delegate.getExportedItems();
    }

    @Override
    public String[] getExportedItemsOrder() {
        return delegate.getExportedItemsOrder();
    }

    @Override
    public String getId() {
        return delegate.getId();
    }

    @Override
    public ComponentData getData() {
        return delegate.getData();
    }

    @Override
    public String getAppliedCssClasses() {
        return delegate.getAppliedCssClasses();
    }
}
