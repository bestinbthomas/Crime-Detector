package com.eswar.crimedetector;

import android.support.annotation.NonNull;

public class Crime{
    private String category, location_type, context, persistent_id, month;
    private long id;
    private OutcomeStatus outcome_status;
    private Location location;

    //Getter Methods

    public String getCategory() {
        return category;
    }

    public long getId() {
        return id;
    }

    public Location getLocation() {
        return location;
    }

    public OutcomeStatus getOutcome_status() {
        return outcome_status;
    }

    public String getContext() {
        return context;
    }

    public String getLocation_type() {
        return location_type;
    }

    public String getMonth() {
        return month;
    }

    public String getPersistent_id() {
        return persistent_id;
    }

    //Setter Methods

    public void setId(long id) {
        this.id = id;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public void setLocation_type(String location_type) {
        this.location_type = location_type;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public void setOutcome_status(OutcomeStatus outcome_status) {
        this.outcome_status = outcome_status;
    }

    public void setPersistent_id(String persistent_id) {
        this.persistent_id = persistent_id;
    }

    @NonNull
    @Override
    public String toString() {
        return "Category: " + category + "\nID: " + id + "\nContext: " + context + "\nLocation Type: " + location_type + "\n" + location + "\nMonth: " + month + "\n" + outcome_status;
    }

    public class OutcomeStatus{
        private String category, date;

        //Getter Methods

        public String getCategory() {
            return category;
        }
        public String getDate() {
            return date;
        }

        //Setter Methods

        public void setCategory(String category) {
            this.category = category;
        }
        public void setDate(String date) {
            this.date = date;
        }

        @NonNull
        @Override
        public String toString() {
            return "Outcome status: \nCategory: " + category + "\nDate: " + date;
        }
    }

    public class Location{
        private String latitude, longitude;
        private Street street;

        //Getter Methods

        public Street getStreet() {
            return street;
        }
        public String getLatitude() {
            return latitude;
        }
        public String getLongitude() {
            return longitude;
        }

        //Setter Methods

        public void setLatitude(String latitude) {
            this.latitude = latitude;
        }
        public void setLongitude(String longitude) {
            this.longitude = longitude;
        }
        public void setStreet(Street street) {
            this.street = street;
        }

        @NonNull
        @Override
        public String toString() {
            return "Location:\n" + "Latitude:" + latitude + ", Longitude: " + longitude + "\n" + street;
        }

        public class Street{
            private String id, name;

            //Getter Methods

            public String getId() {
                return id;
            }
            public String getName() {
                return name;
            }

            //Setter Methods

            public void setId(String id) {
                this.id = id;
            }
            public void setName(String name) {
                this.name = name;
            }

            @NonNull
            @Override
            public String toString() {
                return "Street\n ID: " + id + "\nName: " + name;
            }
        }
    }
}