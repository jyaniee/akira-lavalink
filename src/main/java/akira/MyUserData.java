package akira;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MyUserData {
    @JsonProperty("requesterId")
    private long requesterId;

    @JsonProperty("sourceType")
    private String sourceType;

    public MyUserData() {}

    public MyUserData(long requesterId, String sourceType) {
        this.requesterId = requesterId;
        this.sourceType = sourceType;
    }

    public long getRequesterId() {
        return requesterId;
    }

    public String getSourceType() {
        return sourceType;
    }
}
