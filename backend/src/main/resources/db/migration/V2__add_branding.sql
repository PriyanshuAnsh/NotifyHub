-- Optional branding for rich (HTML) email bodies. All nullable; absent = plain text.
ALTER TABLE notifications
    ADD COLUMN logo_url   VARCHAR(1024),
    ADD COLUMN banner_url VARCHAR(1024),
    ADD COLUMN heading    VARCHAR(255),
    ADD COLUMN cta_text   VARCHAR(150),
    ADD COLUMN cta_url    VARCHAR(1024);
