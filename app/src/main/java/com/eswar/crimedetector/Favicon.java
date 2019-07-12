package com.eswar.crimedetector;

import java.util.ArrayList;

public class Favicon {
    private String url;
    ArrayList< FaviconDetails > icons = new ArrayList <> ();


    // Getter Methods

    public String getUrl() {
        return url;
    }

    public ArrayList<FaviconDetails> getIcons() {
        return icons;
    }

    // Setter Methods

    public void setUrl(String url) {
        this.url = url;
    }

    public void setIcons(ArrayList<FaviconDetails> icons) {
        this.icons = icons;
    }

    public class FaviconDetails {
        private String url;
        private float width;
        private float height;
        private String format;
        private float bytes;
        private String error = null;
        private String sha1sum;


        // Getter Methods

        public String getUrl() {
            return url;
        }

        public float getWidth() {
            return width;
        }

        public float getHeight() {
            return height;
        }

        public String getFormat() {
            return format;
        }

        public float getBytes() {
            return bytes;
        }

        public String getError() {
            return error;
        }

        public String getSha1sum() {
            return sha1sum;
        }

        // Setter Methods

        public void setUrl(String url) {
            this.url = url;
        }

        public void setWidth(float width) {
            this.width = width;
        }

        public void setHeight(float height) {
            this.height = height;
        }

        public void setFormat(String format) {
            this.format = format;
        }

        public void setBytes(float bytes) {
            this.bytes = bytes;
        }

        public void setError(String error) {
            this.error = error;
        }

        public void setSha1sum(String sha1sum) {
            this.sha1sum = sha1sum;
        }
    }
}
