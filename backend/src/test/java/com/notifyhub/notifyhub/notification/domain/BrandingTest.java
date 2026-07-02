package com.notifyhub.notifyhub.notification.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class BrandingTest {

    @Test
    void ofNormalizesBlanksToNullAndTrims() {
        Branding b = Branding.of("  https://x/logo.png ", "", "  ", null, "https://x");

        assertThat(b.logoUrl()).isEqualTo("https://x/logo.png");
        assertThat(b.bannerUrl()).isNull();
        assertThat(b.heading()).isNull();
        assertThat(b.ctaText()).isNull();
        assertThat(b.ctaUrl()).isEqualTo("https://x");
    }

    @Test
    void noneHasNoContent() {
        assertThat(Branding.NONE.hasContent()).isFalse();
        assertThat(Branding.of("", " ", null, "", "").hasContent()).isFalse();
    }

    @Test
    void anyFieldGivesContent() {
        assertThat(Branding.of("logo", null, null, null, null).hasContent()).isTrue();
        assertThat(Branding.of(null, "banner", null, null, null).hasContent()).isTrue();
        assertThat(Branding.of(null, null, "Hi", null, null).hasContent()).isTrue();
        assertThat(Branding.of(null, null, null, "Go", null).hasContent()).isTrue();
        assertThat(Branding.of(null, null, null, null, "https://x").hasContent()).isTrue();
    }

    @Test
    void ctaRequiresBothTextAndUrl() {
        assertThat(Branding.of(null, null, null, "Go", "https://x").hasCta()).isTrue();
        assertThat(Branding.of(null, null, null, "Go", null).hasCta()).isFalse();
        assertThat(Branding.of(null, null, null, null, "https://x").hasCta()).isFalse();
    }
}
