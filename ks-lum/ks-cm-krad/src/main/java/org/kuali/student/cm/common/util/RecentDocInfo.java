package org.kuali.student.cm.common.util;

/**
 * Data object for an item in doc history.
 * TODO: Find a better package.
 */
public class RecentDocInfo {
    private String name;
    private String location;

    public RecentDocInfo(String name, String location) {
        super();
        this.name = name;
        this.location = location;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getLocation() {
        return location;
    }
    public void setLocation(String location) {
        this.location = location;
    }
}