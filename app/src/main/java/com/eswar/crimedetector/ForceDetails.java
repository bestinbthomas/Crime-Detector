package com.eswar.crimedetector;

import java.util.ArrayList;

public class ForceDetails {
    private String description;
    private String url;
    ArrayList< EngagementMethod > engagement_methods = new ArrayList<>();
    private String telephone;
    private String id;
    private String name;


    // Getter Methods

    public String getDescription() {
        return description;
    }

    public String getUrl() {
        return url;
    }

    public String getTelephone() {
        return telephone;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public ArrayList<EngagementMethod> getEngagementMethods() { return engagement_methods; }

    // Setter Methods

    public void setDescription(String description) {
        this.description = description;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEngagement_methods(ArrayList<EngagementMethod> engagement_methods) {
        this.engagement_methods = engagement_methods;
    }

    public class EngagementMethod {
        private String url;
        private String description;
        private String title;


        // Getter Methods

        public String getUrl() {
            return url;
        }

        public String getDescription() {
            return description;
        }

        public String getTitle() {
            return title;
        }

        // Setter Methods

        public void setUrl(String url) {
            this.url = url;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }
}
