package com.anchora.aem.core.models.impl;

import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;

import com.anchora.aem.core.models.ResponsiveImage;
import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.Rendition;

/**
 * Builds a {@link ResponsiveImage} from the image fields that the hero and teaser dialogs share.
 * <p>
 * The srcset is made of the asset's web renditions ({@code cq5dam.web.W.H.ext}) plus the original. A web
 * rendition fits inside a W x H box without upscaling, so its real width is calculated from the original
 * size stored in the asset metadata.
 */
final class ResponsiveImages {

    static final String PN_FILE_REFERENCE = "fileReference";
    static final String PN_ALT = "alt";
    static final String PN_DECORATIVE = "isDecorative";
    static final String DAM_DESCRIPTION = "dc:description";
    static final String DAM_WIDTH = "tiff:ImageWidth";
    static final String DAM_HEIGHT = "tiff:ImageLength";

    private static final Pattern WEB_RENDITION = Pattern.compile("cq5dam\\.web\\.(\\d+)\\.(\\d+)\\.\\w+");

    private ResponsiveImages() {
    }

    /**
     * @param props the component properties ({@code fileReference}, {@code alt}, {@code isDecorative})
     * @return the image, or null when no asset is referenced or the asset does not exist
     */
    static ResponsiveImage from(ResourceResolver resolver, ValueMap props) {
        String reference = props.get(PN_FILE_REFERENCE, String.class);
        Resource assetResource = StringUtils.isBlank(reference) ? null : resolver.getResource(reference);
        Asset asset = assetResource == null ? null : assetResource.adaptTo(Asset.class);
        if (asset == null) {
            return null;
        }

        int width = NumberUtils.toInt(asset.getMetadataValue(DAM_WIDTH));
        int height = NumberUtils.toInt(asset.getMetadataValue(DAM_HEIGHT));

        // width descriptor -> URL, sorted from smallest to largest
        TreeMap<Integer, String> sources = new TreeMap<>();
        // src falls back to the largest web rendition, because the original can be very large
        String src = null;
        int srcWidth = 0;
        for (Rendition rendition : asset.getRenditions()) {
            Matcher matcher = WEB_RENDITION.matcher(rendition.getName());
            if (matcher.matches()) {
                int renditionWidth = fittedWidth(width, height,
                        Integer.parseInt(matcher.group(1)), Integer.parseInt(matcher.group(2)));
                String url = toUrl(rendition.getPath());
                sources.putIfAbsent(renditionWidth, url);
                if (renditionWidth > srcWidth) {
                    srcWidth = renditionWidth;
                    src = url;
                }
            }
        }
        String original = toUrl(asset.getPath());
        if (width > 0) {
            sources.putIfAbsent(width, original);
        }
        if (src == null) {
            src = original;
        }
        String srcset = sources.isEmpty() ? null : sources.entrySet().stream()
                .map(source -> source.getValue() + " " + source.getKey() + "w")
                .collect(Collectors.joining(", "));

        boolean decorative = props.get(PN_DECORATIVE, false);
        String alt = "";
        if (!decorative) {
            String authored = StringUtils.trimToNull(props.get(PN_ALT, String.class));
            alt = authored != null ? authored : StringUtils.trimToEmpty(asset.getMetadataValue(DAM_DESCRIPTION));
        }

        return new ResponsiveImage(src, srcset, alt, decorative,
                width > 0 ? width : null, height > 0 ? height : null);
    }

    /** Width of an image scaled down (never up) to fit a box. Falls back to the box width if the size is unknown. */
    static int fittedWidth(int width, int height, int boxWidth, int boxHeight) {
        if (width <= 0 || height <= 0) {
            return boxWidth;
        }
        double scale = Math.min(1d, Math.min((double) boxWidth / width, (double) boxHeight / height));
        return (int) Math.round(width * scale);
    }

    private static String toUrl(String path) {
        return path.replace("/jcr:content/", "/_jcr_content/").replace(" ", "%20");
    }
}
