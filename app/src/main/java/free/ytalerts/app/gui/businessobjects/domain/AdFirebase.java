package free.ytalerts.app.gui.businessobjects.domain;

public class AdFirebase {

    private String child;
    private String banner;
    private String channel;
    private long clicks;
    private String id_channel;
    private long impressions;
    private String description;

    public String getChild() {
        return child;
    }

    public void setChild(String child) {
        this.child = child;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBanner() {
        return banner;
    }

    public void setBanner(String banner) {
        this.banner = banner;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public long getClicks() {
        return clicks;
    }

    public void setClicks(long clicks) {
        this.clicks = clicks;
    }

    public String getId_channel() {
        return id_channel;
    }

    public void setId_channel(String id_channel) {
        this.id_channel = id_channel;
    }

    public long getImpressions() {
        return impressions;
    }

    public void setImpressions(long impressions) {
        this.impressions = impressions;
    }
}
