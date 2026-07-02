package com.notifyhub.notifyhub.notification.delivery;

import static org.assertj.core.api.Assertions.assertThat;

import com.notifyhub.notifyhub.notification.domain.Branding;
import com.notifyhub.notifyhub.notification.messaging.DeliveryMessage;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class EmailTemplateTest {

    private DeliveryMessage message(String body, Branding branding) {
        return new DeliveryMessage(UUID.randomUUID(), "user@x.com", "Subject", body, branding);
    }

    @Test
    void rendersAllBrandingElements() {
        Branding b = Branding.of("https://x/logo.png", "https://x/banner.png", "Welcome", "Go", "https://x/app");
        String html = EmailTemplate.render(message("Hello there", b));

        assertThat(html).contains("<!doctype html>");
        assertThat(html).contains("https://x/logo.png");
        assertThat(html).contains("https://x/banner.png");
        assertThat(html).contains("Welcome");
        assertThat(html).contains("Hello there");
        assertThat(html).contains("href=\"https://x/app\"").contains(">Go</a>");
        assertThat(html).contains("Sent via NotifyHub");
    }

    @Test
    void omitsAbsentElements() {
        String html = EmailTemplate.render(message("Body only", Branding.of(null, null, "Heading", null, null)));

        assertThat(html).contains("Heading").contains("Body only");
        assertThat(html).doesNotContain("<img");   // no logo / banner
        assertThat(html).doesNotContain("</a>");    // no CTA button
    }

    @Test
    void escapesHtmlAndConvertsNewlines() {
        Branding b = Branding.of(null, null, "<b>x</b>", null, null);
        String html = EmailTemplate.render(message("line1\nline2 <script>", b));

        assertThat(html).contains("&lt;b&gt;x&lt;/b&gt;");
        assertThat(html).contains("line1<br>line2 &lt;script&gt;");
        assertThat(html).doesNotContain("<script>");
    }

    @Test
    void toleratesNullBrandingAndBody() {
        String html = EmailTemplate.render(new DeliveryMessage(UUID.randomUUID(), "u@x.com", "S", null, null));
        assertThat(html).contains("Sent via NotifyHub");
    }
}
