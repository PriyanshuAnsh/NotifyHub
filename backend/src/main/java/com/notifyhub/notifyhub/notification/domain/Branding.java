package com.notifyhub.notifyhub.notification.domain;

/**
 * Optional branding for a rich HTML email body. Any non-blank field opts the
 * notification into the branded template; all blank means plain text.
 * Images are referenced by URL — no attachments.
 */
public record Branding(
        String logoUrl,
        String bannerUrl,
        String heading,
        String ctaText,
        String ctaUrl) {

    public static final Branding NONE = new Branding(null, null, null, null, null);

    /** Normalizes blank strings to null so the template treats them as absent. */
    public static Branding of(String logoUrl, String bannerUrl, String heading,
                              String ctaText, String ctaUrl) {
        return new Branding(blankToNull(logoUrl), blankToNull(bannerUrl), blankToNull(heading),
                blankToNull(ctaText), blankToNull(ctaUrl));
    }

    public boolean hasContent() {
        return logoUrl != null || bannerUrl != null || heading != null
                || ctaText != null || ctaUrl != null;
    }

    /** A call-to-action button renders only when both its text and URL are present. */
    public boolean hasCta() {
        return ctaText != null && ctaUrl != null;
    }

    private static String blankToNull(String s) {
        return (s == null || s.isBlank()) ? null : s.trim();
    }
}
