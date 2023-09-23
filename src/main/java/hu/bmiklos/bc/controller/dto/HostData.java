package hu.bmiklos.bc.controller.dto;

import org.springframework.lang.Nullable;

public class HostData {
    @Nullable
    private final String id;
    private final String name;
    private final boolean userHosted;

    public HostData(String id, String name, boolean userHosted) {
        this.id = id;
        this.name = name;
        this.userHosted = userHosted;
    }

    public HostData(String string, String name) {
        this(string, name, false);
    }

    @Nullable
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isUserHosted() {
        return userHosted;
    }
}
