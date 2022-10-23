package component.room;

import component.GameComponent;

public abstract class AbstractRoom extends GameComponent {

    private String imgPath;

    public AbstractRoom(int id, String name, String description) {
        super(id, name, description);
    }

    public AbstractRoom(int id, String name, String description, String imgPath) {
        super(id, name, description);
        this.imgPath = imgPath;
    }

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }
}
