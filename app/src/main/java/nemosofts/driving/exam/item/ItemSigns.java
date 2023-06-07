package nemosofts.driving.exam.item;

public class ItemSigns {

    private final String id;
    private final String name;
    private final String image;
    private final String image_thumb;

    public ItemSigns(String id, String name, String image, String image_thumb) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.image_thumb = image_thumb;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }

    public String getImageThumb() {
        return image_thumb;
    }
}
