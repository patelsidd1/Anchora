package com.anchora.aem.core.models.impl;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import com.anchora.aem.core.models.Header;
import com.anchora.aem.core.models.LinkGroup;
import com.anchora.aem.core.models.LinkItem;
import com.anchora.aem.core.models.ResponsiveImage;

@Model(adaptables = SlingHttpServletRequest.class,
        adapters = Header.class,
        resourceType = HeaderImpl.RESOURCE_TYPE,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class HeaderImpl implements Header {

    static final String RESOURCE_TYPE = "anchora/components/header";
    static final String NN_PARTNER = "partner";
    static final String NN_FLAG = "flag";
    static final String NN_MENUS = "menus";
    static final String DEFAULT_BRAND = "Anchora";
    static final String DEFAULT_REGION_LABEL = "Change region and language";
    static final String DEFAULT_CART_LABEL = "Cart";
    static final String DEFAULT_LOGIN_LABEL = "Log in";
    static final String DEFAULT_NAV_LABEL = "Main";
    static final String DEFAULT_MENU_LABEL = "Menu";
    static final String DEFAULT_HELP_LABEL = "Help";

    @Self
    private SlingHttpServletRequest request;

    @SlingObject
    private Resource resource;

    @ValueMapValue
    private String brandLabel;

    @ValueMapValue
    private String brandLink;

    @ValueMapValue
    private String region;

    @ValueMapValue
    private String language;

    @ValueMapValue
    private String regionLabel;

    @ValueMapValue
    private String regionLink;

    @ValueMapValue
    private String cartLabel;

    @ValueMapValue
    private String cartLink;

    @ValueMapValue
    private String loginLabel;

    @ValueMapValue
    private String loginLink;

    @ValueMapValue
    private String navLabel;

    @ValueMapValue
    private String menuLabel;

    @ValueMapValue
    private String helpLabel;

    @ValueMapValue
    private String helpLink;

    private ResponsiveImage logo;
    private ResponsiveImage partnerLogo;
    private ResponsiveImage flag;
    private List<LinkGroup> menus;

    @PostConstruct
    protected void init() {
        ResourceResolver resolver = request.getResourceResolver();
        logo = ResponsiveImages.from(resolver, resource.getValueMap());
        partnerLogo = ResponsiveImages.from(resolver, childProperties(NN_PARTNER));
        flag = ResponsiveImages.from(resolver, childProperties(NN_FLAG));
        // A dropdown needs both a label for its button and links to show.
        menus = Links.groupsFromMultifield(request, resource.getChild(NN_MENUS)).stream()
                .filter(menu -> menu.getTitle() != null && !menu.getLinks().isEmpty())
                .collect(Collectors.toUnmodifiableList());
    }

    /** The partner badge and flag images are stored in child nodes, so they can reuse the image field names. */
    private ValueMap childProperties(String name) {
        Resource child = resource.getChild(name);
        return child == null ? ValueMap.EMPTY : child.getValueMap();
    }

    private LinkItem link(String label, String defaultLabel, String target) {
        return Links.create(request, StringUtils.defaultIfBlank(label, defaultLabel), target, false);
    }

    @Override
    public ResponsiveImage getLogo() {
        return logo;
    }

    @Override
    public String getBrandLabel() {
        return StringUtils.defaultIfBlank(brandLabel, DEFAULT_BRAND).trim();
    }

    @Override
    public String getBrandHref() {
        return Links.resolve(request, brandLink);
    }

    @Override
    public ResponsiveImage getPartnerLogo() {
        return partnerLogo;
    }

    @Override
    public ResponsiveImage getFlag() {
        return flag;
    }

    @Override
    public String getRegion() {
        return StringUtils.trimToNull(region);
    }

    @Override
    public String getLanguage() {
        return StringUtils.trimToNull(language);
    }

    @Override
    public LinkItem getRegionLink() {
        return link(regionLabel, DEFAULT_REGION_LABEL, regionLink);
    }

    @Override
    public LinkItem getCartLink() {
        return link(cartLabel, DEFAULT_CART_LABEL, cartLink);
    }

    @Override
    public LinkItem getLoginLink() {
        return link(loginLabel, DEFAULT_LOGIN_LABEL, loginLink);
    }

    @Override
    public String getNavLabel() {
        return StringUtils.defaultIfBlank(navLabel, DEFAULT_NAV_LABEL).trim();
    }

    @Override
    public String getMenuLabel() {
        return StringUtils.defaultIfBlank(menuLabel, DEFAULT_MENU_LABEL).trim();
    }

    @Override
    public List<LinkGroup> getMenus() {
        return menus;
    }

    @Override
    public LinkItem getHelpLink() {
        return link(helpLabel, DEFAULT_HELP_LABEL, helpLink);
    }
}
