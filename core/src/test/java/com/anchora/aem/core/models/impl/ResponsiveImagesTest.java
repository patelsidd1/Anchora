package com.anchora.aem.core.models.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.wrappers.ValueMapDecorator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.anchora.aem.core.models.ResponsiveImage;
import com.anchora.aem.core.testcontext.AppAemContext;
import com.day.cq.dam.api.Asset;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

@ExtendWith(AemContextExtension.class)
class ResponsiveImagesTest {

    private static final String ASSET = "/content/dam/anchora/hero.jpg";
    private static final String RENDITIONS = ASSET + "/_jcr_content/renditions/";

    private final AemContext context = AppAemContext.newAemContext();

    private Asset createAsset(String path, int width, int height, String description) {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put(ResponsiveImages.DAM_WIDTH, (long) width);
        metadata.put(ResponsiveImages.DAM_HEIGHT, (long) height);
        if (description != null) {
            metadata.put(ResponsiveImages.DAM_DESCRIPTION, description);
        }
        return context.create().asset(path, width, height, "image/jpeg", metadata);
    }

    private ResponsiveImage image(Object... props) {
        Map<String, Object> map = new HashMap<>();
        for (int i = 0; i < props.length; i += 2) {
            map.put((String) props[i], props[i + 1]);
        }
        ValueMap valueMap = new ValueMapDecorator(map);
        return ResponsiveImages.from(context.resourceResolver(), valueMap);
    }

    @Test
    void noReferenceOrMissingAssetGivesNoImage() {
        assertNull(image());
        assertNull(image("fileReference", "/content/dam/anchora/missing.jpg"));
    }

    @Test
    void srcsetListsWebRenditionsAndOriginalByWidth() {
        Asset asset = createAsset(ASSET, 1920, 800, "Paper plane");
        context.create().assetRendition(asset, "cq5dam.web.1280.1280.jpeg", 1280, 533, "image/jpeg");
        context.create().assetRendition(asset, "cq5dam.web.640.640.jpeg", 640, 267, "image/jpeg");
        context.create().assetRendition(asset, "cq5dam.thumbnail.319.319.png", 319, 133, "image/png");

        ResponsiveImage image = image("fileReference", ASSET);

        assertEquals(RENDITIONS + "cq5dam.web.640.640.jpeg 640w, "
                + RENDITIONS + "cq5dam.web.1280.1280.jpeg 1280w, "
                + ASSET + " 1920w", image.getSrcset());
        assertEquals(RENDITIONS + "cq5dam.web.1280.1280.jpeg", image.getSrc());
        assertEquals(1920, image.getWidth());
        assertEquals(800, image.getHeight());
    }

    @Test
    void renditionIsNeverWiderThanTheOriginal() {
        Asset asset = createAsset(ASSET, 800, 600, null);
        context.create().assetRendition(asset, "cq5dam.web.1280.1280.jpeg", 800, 600, "image/jpeg");

        ResponsiveImage image = image("fileReference", ASSET);

        assertEquals(RENDITIONS + "cq5dam.web.1280.1280.jpeg 800w", image.getSrcset());
    }

    @Test
    void originalIsUsedWhenThereAreNoWebRenditions() {
        createAsset(ASSET, 1200, 800, null);

        ResponsiveImage image = image("fileReference", ASSET);

        assertEquals(ASSET, image.getSrc());
        assertEquals(ASSET + " 1200w", image.getSrcset());
    }

    @Test
    void authoredAltTextWins() {
        createAsset(ASSET, 1200, 800, "From DAM");
        ResponsiveImage image = image("fileReference", ASSET, "alt", " Authored ");
        assertEquals("Authored", image.getAlt());
        assertFalse(image.isDecorative());
    }

    @Test
    void blankAltTextFallsBackToDamDescription() {
        createAsset(ASSET, 1200, 800, "From DAM");
        assertEquals("From DAM", image("fileReference", ASSET, "alt", " ").getAlt());
    }

    @Test
    void decorativeImageHasEmptyAlt() {
        createAsset(ASSET, 1200, 800, "From DAM");
        ResponsiveImage image = image("fileReference", ASSET, "alt", "Authored", "isDecorative", true);
        assertEquals("", image.getAlt());
        assertTrue(image.isDecorative());
    }

    @Test
    void altIsEmptyWhenNothingIsAvailable() {
        createAsset(ASSET, 1200, 800, null);
        assertEquals("", image("fileReference", ASSET).getAlt());
    }

    @Test
    void fittedWidthScalesDownToTheBox() {
        assertEquals(1280, ResponsiveImages.fittedWidth(1920, 800, 1280, 1280));
        assertEquals(640, ResponsiveImages.fittedWidth(800, 1600, 1280, 1280));
        assertEquals(500, ResponsiveImages.fittedWidth(500, 400, 1280, 1280));
        assertEquals(1280, ResponsiveImages.fittedWidth(0, 0, 1280, 1280));
    }
}
