package component.room;

import component.GameComponent;

public abstract class AbstractRoom extends GameComponent {

    private String imgPath;

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    public void setSecondaryId(char value) {
        setId(getId() + Character.toString(value));
    }
}
