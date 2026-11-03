package com.anchora.aem.core.models.impl;

import java.time.Year;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import com.anchora.aem.core.models.Footer;
import com.anchora.aem.core.models.LinkGroup;
import com.anchora.aem.core.models.LinkItem;
import com.anchora.aem.core.models.ResponsiveImage;
import com.anchora.aem.core.models.SocialLink;

@Model(adaptables = SlingHttpServletRequest.class,
        adapters = Footer.class,
        resourceType = FooterImpl.RESOURCE_TYPE,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class FooterImpl implements Footer {

    static final String RESOURCE_TYPE = "anchora/components/footer";
    static final String NN_COLUMNS = "columns";
    static final String NN_SOCIAL_LINKS = "socialLinks";
    static final String PN_PLATFORM = "platform";
    static final String DEFAULT_BACK_TO_TOP_LABEL = "Back to top";
    static final String DEFAULT_BRAND = "Anchora";
    static final String DEFAULT_NAV_LABEL = "Footer";
    static final String DEFAULT_SOCIAL_TITLE = "Connect with us";
    static final String YEAR_TOKEN = "{year}";

    /** The platforms the dialog offers and the stylesheet has icons for, with the name screen readers announce. */
    static final Map<String, String> PLATFORMS = Map.of(
            "facebook", "Facebook",
            "x", "X",
            "linkedin", "LinkedIn",
            "tiktok", "TikTok",
            "instagram", "Instagram",
            "youtube", "YouTube");

    @Self
    private SlingHttpServletRequest request;

    @SlingObject
    private Resource resource;

    @ValueMapValue
    private boolean backToTop;

    @ValueMapValue
    private String backToTopLabel;

    @ValueMapValue
    private String brandLabel;

    @ValueMapValue
    private String brandLink;

    @ValueMapValue
    private String navLabel;

    @ValueMapValue
    private String socialTitle;

    @ValueMapValue
    private String copyright;

    @ValueMapValue
    private String acknowledgement;

    private ResponsiveImage logo;
    private List<LinkGroup> columns;
    private List<SocialLink> socialLinks;
    private List<LinkItem> links;

    @PostConstruct
    protected void init() {
        logo = ResponsiveImages.from(request.getResourceResolver(), resource.getValueMap());
        columns = Links.groupsFromMultifield(request, resource.getChild(NN_COLUMNS));
        socialLinks = readSocialLinks(resource.getChild(NN_SOCIAL_LINKS));
        links = Links.fromMultifield(request, resource.getChild(Links.NN_LINKS));
    }

    /** The link label is the platform name, so unknown platforms have no label and are skipped. */
    private List<SocialLink> readSocialLinks(Resource multifield) {
        if (multifield == null) {
            return Collections.emptyList();
        }
        List<SocialLink> result = new ArrayList<>();
        for (Resource item : multifield.getChildren()) {
            ValueMap props = item.getValueMap();
            String platform = props.get(PN_PLATFORM, "");
            LinkItem link = Links.create(request, PLATFORMS.get(platform), props.get(Links.PN_LINK, String.class),
                    props.get(Links.PN_NEW_TAB, false));
            if (link != null) {
                result.add(new SocialLink(platform, link));
            }
        }
        return Collections.unmodifiableList(result);
    }

    @Override
    public boolean isBackToTop() {
        return backToTop;
    }

    @Override
    public String getBackToTopLabel() {
        return StringUtils.defaultIfBlank(backToTopLabel, DEFAULT_BACK_TO_TOP_LABEL).trim();
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
    public String getNavLabel() {
        return StringUtils.defaultIfBlank(navLabel, DEFAULT_NAV_LABEL).trim();
    }

    @Override
    public List<LinkGroup> getColumns() {
        return columns;
    }

    @Override
    public String getSocialTitle() {
        return StringUtils.defaultIfBlank(socialTitle, DEFAULT_SOCIAL_TITLE).trim();
    }

    @Override
    public List<SocialLink> getSocialLinks() {
        return socialLinks;
    }

    @Override
    public List<LinkItem> getLinks() {
        return links;
    }

    @Override
    public String getCopyright() {
        String text = StringUtils.trimToNull(copyright);
        return text == null ? null : text.replace(YEAR_TOKEN, String.valueOf(Year.now().getValue()));
    }

    @Override
    public String getAcknowledgement() {
        return StringUtils.trimToNull(acknowledgement);
    }
}
