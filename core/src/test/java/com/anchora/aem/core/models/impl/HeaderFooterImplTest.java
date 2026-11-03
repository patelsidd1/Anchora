package com.anchora.aem.core.models.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Year;
import java.util.List;
import java.util.Map;

import org.apache.sling.api.resource.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.anchora.aem.core.models.Footer;
import com.anchora.aem.core.models.Header;
import com.anchora.aem.core.models.LinkGroup;
import com.anchora.aem.core.models.ResponsiveImage;
import com.anchora.aem.core.models.SocialLink;
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
    void headerHasDefaults() {
        context.currentResource(context.create().resource(ROOT + "/header",
                "sling:resourceType", HeaderImpl.RESOURCE_TYPE));

        Header model = context.request().adaptTo(Header.class);

        assertNull(model.getLogo());
        assertEquals("Anchora", model.getBrandLabel());
        assertNull(model.getBrandHref());
        assertNull(model.getPartnerLogo());
        assertNull(model.getFlag());
        assertNull(model.getRegion());
        assertNull(model.getLanguage());
        assertNull(model.getRegionLink(), "links without a target are not rendered");
        assertNull(model.getCartLink());
        assertNull(model.getLoginLink());
        assertEquals("Main", model.getNavLabel());
        assertEquals("Menu", model.getMenuLabel());
        assertTrue(model.getMenus().isEmpty());
        assertNull(model.getHelpLink());
    }

    @Test
    void headerReadsTopRow() {
        String logo = "/content/dam/anchora/brand/logo.png";
        String badge = "/content/dam/anchora/brand/badge.png";
        String flag = "/content/dam/anchora/brand/flag.png";
        context.create().asset(logo, 400, 80, "image/png");
        context.create().asset(badge, 68, 68, "image/png");
        context.create().asset(flag, 48, 48, "image/png");
        Resource header = context.create().resource(ROOT + "/header",
                "sling:resourceType", HeaderImpl.RESOURCE_TYPE,
                "fileReference", logo, "brandLabel", " Anchora Air ", "brandLink", "/content/anchora/us/en",
                "region", " AU ", "language", "EN", "regionLink", "/content/anchora/us/en#region",
                "cartLabel", " ", "cartLink", "https://www.example.com/cart",
                "loginLabel", " Sign in ", "loginLink", "/content/anchora/us/en/login");
        context.create().resource(header, "partner", "fileReference", badge, "alt", "Alliance member");
        context.create().resource(header, "flag", "fileReference", flag);
        context.currentResource(header);

        Header model = context.request().adaptTo(Header.class);

        assertEquals(logo, model.getLogo().getSrc());
        assertEquals("Anchora Air", model.getBrandLabel());
        assertEquals("/content/anchora/us/en.html", model.getBrandHref());
        assertEquals(badge, model.getPartnerLogo().getSrc());
        assertEquals("Alliance member", model.getPartnerLogo().getAlt());
        assertEquals(flag, model.getFlag().getSrc());
        assertEquals("AU", model.getRegion());
        assertEquals("EN", model.getLanguage());
        assertEquals("Change region and language", model.getRegionLink().getLabel());
        assertEquals("/content/anchora/us/en.html#region", model.getRegionLink().getHref());
        assertEquals("Cart", model.getCartLink().getLabel(), "a blank label falls back to the default");
        assertEquals("https://www.example.com/cart", model.getCartLink().getHref());
        assertEquals("Sign in", model.getLoginLink().getLabel());
        assertEquals("/content/anchora/us/en/login.html", model.getLoginLink().getHref());
    }

    @Test
    void headerReadsMenusAndHelp() {
        Resource header = context.create().resource(ROOT + "/header",
                "sling:resourceType", HeaderImpl.RESOURCE_TYPE,
                "navLabel", "Primary", "menuLabel", "Browse", "helpLink", "https://www.example.com/help");
        addLinks(context.create().resource(header, "menus/item0", "title", " Flights "));
        context.create().resource(header, "menus/item1", "title", "Travel");
        addLinks(context.create().resource(header, "menus/item2"));
        context.currentResource(header);

        Header model = context.request().adaptTo(Header.class);

        assertEquals("Primary", model.getNavLabel());
        assertEquals("Browse", model.getMenuLabel());
        List<LinkGroup> menus = model.getMenus();
        assertEquals(1, menus.size(), "menus without a label or without links are skipped");
        assertEquals("Flights", menus.get(0).getTitle());
        assertEquals(2, menus.get(0).getLinks().size());
        assertEquals("/content/anchora/us/en.html", menus.get(0).getLinks().get(0).getHref());
        assertEquals("noopener noreferrer", menus.get(0).getLinks().get(1).getRel());
        assertEquals("Help", model.getHelpLink().getLabel());
        assertEquals("https://www.example.com/help", model.getHelpLink().getHref());
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

    @Test
    void footerHasDefaults() {
        context.currentResource(context.create().resource(ROOT + "/footer",
                "sling:resourceType", FooterImpl.RESOURCE_TYPE));

        Footer model = context.request().adaptTo(Footer.class);

        assertFalse(model.isBackToTop());
        assertEquals("Back to top", model.getBackToTopLabel());
        assertNull(model.getLogo());
        assertEquals("Anchora", model.getBrandLabel());
        assertNull(model.getBrandHref());
        assertEquals("Connect with us", model.getSocialTitle());
        assertTrue(model.getColumns().isEmpty());
        assertTrue(model.getSocialLinks().isEmpty());
        assertTrue(model.getLinks().isEmpty());
        assertNull(model.getAcknowledgement());
    }

    @Test
    void footerReadsBrandBackToTopAndAcknowledgement() {
        String logo = "/content/dam/anchora/brand/logo.png";
        context.create().asset(logo, 400, 80, "image/png",
                Map.of("tiff:ImageWidth", 400L, "tiff:ImageLength", 80L));
        context.currentResource(context.create().resource(ROOT + "/footer",
                "sling:resourceType", FooterImpl.RESOURCE_TYPE,
                "backToTop", true, "backToTopLabel", " Top ",
                "fileReference", logo, "brandLabel", " Anchora Air ", "brandLink", "/content/anchora/us/en",
                "socialTitle", "Follow us", "acknowledgement", "  We acknowledge the Traditional Owners.  "));

        Footer model = context.request().adaptTo(Footer.class);

        assertTrue(model.isBackToTop());
        assertEquals("Top", model.getBackToTopLabel());
        ResponsiveImage image = model.getLogo();
        assertEquals(logo, image.getSrc());
        assertEquals(400, image.getWidth());
        assertEquals("Anchora Air", model.getBrandLabel());
        assertEquals("/content/anchora/us/en.html", model.getBrandHref());
        assertEquals("Follow us", model.getSocialTitle());
        assertEquals("We acknowledge the Traditional Owners.", model.getAcknowledgement());
    }

    @Test
    void footerReadsNestedLinkColumns() {
        Resource footer = context.create().resource(ROOT + "/footer",
                "sling:resourceType", FooterImpl.RESOURCE_TYPE);
        addLinks(context.create().resource(footer, "columns/item0", "title", " About us "));
        context.create().resource(footer, "columns/item1", "title", " ");
        addLinks(context.create().resource(footer, "columns/item2"));
        context.currentResource(footer);

        List<LinkGroup> columns = context.request().adaptTo(Footer.class).getColumns();

        assertEquals(2, columns.size(), "the column with no heading and no links is skipped");
        assertEquals("About us", columns.get(0).getTitle());
        assertEquals(2, columns.get(0).getLinks().size());
        assertEquals("/content/anchora/us/en.html", columns.get(0).getLinks().get(0).getHref());
        assertNull(columns.get(1).getTitle());
        assertEquals(2, columns.get(1).getLinks().size());
    }

    @Test
    void footerReadsSocialLinks() {
        Resource footer = context.create().resource(ROOT + "/footer",
                "sling:resourceType", FooterImpl.RESOURCE_TYPE);
        context.create().resource(footer, "socialLinks/item0", "platform", "facebook",
                "link", "https://www.facebook.com/", "newTab", true);
        context.create().resource(footer, "socialLinks/item1", "platform", "myspace",
                "link", "https://myspace.com/");
        context.create().resource(footer, "socialLinks/item2", "platform", "youtube");
        context.create().resource(footer, "socialLinks/item3", "platform", "x", "link", "https://x.com/");
        context.currentResource(footer);

        List<SocialLink> social = context.request().adaptTo(Footer.class).getSocialLinks();

        assertEquals(2, social.size(), "unknown platforms and items without a link are skipped");
        assertEquals("facebook", social.get(0).getPlatform());
        assertEquals("Facebook", social.get(0).getLink().getLabel());
        assertEquals("_blank", social.get(0).getLink().getTarget());
        assertEquals("x", social.get(1).getPlatform());
        assertEquals("X", social.get(1).getLink().getLabel());
        assertNull(social.get(1).getLink().getTarget());
    }
}
