package mobi.square.slots.ui;

import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class IntClickListener extends ClickListener {

    protected final int value;

    public IntClickListener(int value) {
        this.value = value;
    }

}
