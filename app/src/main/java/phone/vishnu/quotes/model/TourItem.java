package phone.vishnu.quotes.model;

import java.util.Objects;

public class TourItem {

    private int imgId;
    private String title;
    private String desc;

    public TourItem(int imgId, String title, String desc) {
        this.imgId = imgId;
        this.title = title;
        this.desc = desc;
    }

    public int getImgId() {
        return imgId;
    }

    public void setImgId(int imgId) {
        this.imgId = imgId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        TourItem tourItem = (TourItem) o;
        return imgId == tourItem.imgId &&
                Objects.equals(title, tourItem.title) &&
                Objects.equals(desc, tourItem.desc);
    }
}
