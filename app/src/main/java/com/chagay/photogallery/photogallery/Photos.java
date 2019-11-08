package android.bignerdranch.com.photogallery;

import java.util.List;

public class Photos {

    private String page;
    private List<GalleryItem> photo;

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public List<GalleryItem> getPhoto() {
        return photo;
    }

    public void setPhoto(List<GalleryItem> photo) {
        this.photo = photo;
    }
}
