package com.notifyhub.notifyhub.notification.delivery;

import com.notifyhub.notifyhub.notification.domain.Branding;
import com.notifyhub.notifyhub.notification.messaging.DeliveryMessage;

/**
 * Renders a branded, email-client-safe HTML body from a delivery message.
 * Table-based layout with inline styles; images referenced by URL. All
 * caller-supplied text is HTML-escaped to prevent markup injection.
 */
final class EmailTemplate {

    private EmailTemplate() {
    }

    static String render(DeliveryMessage message) {
        Branding b = message.branding() == null ? Branding.NONE : message.branding();
        StringBuilder sb = new StringBuilder(1024);

        sb.append("<!doctype html><html><body style=\"margin:0;padding:0;background:#f4f4f7;")
                .append("font-family:Arial,Helvetica,sans-serif;color:#16181d;\">")
                .append("<table role=\"presentation\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" ")
                .append("style=\"background:#f4f4f7;\"><tr><td align=\"center\" style=\"padding:24px;\">")
                .append("<table role=\"presentation\" width=\"600\" cellpadding=\"0\" cellspacing=\"0\" ")
                .append("style=\"max-width:600px;width:100%;background:#ffffff;border:1px solid #ececf1;")
                .append("border-radius:12px;overflow:hidden;\">");

        if (b.logoUrl() != null) {
            sb.append("<tr><td align=\"center\" style=\"padding:24px 32px 8px;\">")
                    .append("<img src=\"").append(attr(b.logoUrl()))
                    .append("\" alt=\"\" style=\"max-height:40px;border:0;\"></td></tr>");
        }
        if (b.bannerUrl() != null) {
            sb.append("<tr><td style=\"padding:0;\"><img src=\"").append(attr(b.bannerUrl()))
                    .append("\" alt=\"\" width=\"600\" style=\"display:block;width:100%;height:auto;border:0;\">")
                    .append("</td></tr>");
        }

        sb.append("<tr><td style=\"padding:28px 32px 24px;\">");
        if (b.heading() != null) {
            sb.append("<h1 style=\"margin:0 0 12px;font-size:22px;line-height:1.25;color:#16181d;\">")
                    .append(text(b.heading())).append("</h1>");
        }
        sb.append("<div style=\"font-size:15px;line-height:1.6;color:#3a3d47;\">")
                .append(body(message.body())).append("</div>");

        if (b.hasCta()) {
            sb.append("<div style=\"margin-top:24px;\"><a href=\"").append(attr(b.ctaUrl()))
                    .append("\" style=\"display:inline-block;background:#5b5bd6;color:#ffffff;")
                    .append("text-decoration:none;font-weight:bold;font-size:14px;padding:12px 22px;")
                    .append("border-radius:8px;\">").append(text(b.ctaText())).append("</a></div>");
        }
        sb.append("</td></tr>");

        sb.append("<tr><td style=\"padding:16px 32px;background:#fafafa;border-top:1px solid #ececf1;")
                .append("color:#9a9ea9;font-size:12px;\">Sent via NotifyHub</td></tr>")
                .append("</table></td></tr></table></body></html>");
        return sb.toString();
    }

    /** Escape text content and turn newlines into line breaks. */
    private static String body(String raw) {
        return text(raw == null ? "" : raw).replace("\n", "<br>");
    }

    /** HTML-escape for element text. Callers guarantee a non-null value. */
    private static String text(String raw) {
        return raw.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    /** HTML-escape for use inside a double-quoted attribute (URLs). */
    private static String attr(String raw) {
        return text(raw);
    }
}
