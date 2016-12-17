package mobi.square.slots.b.bookofra;

import com.badlogic.gdx.audio.Sound;

import mobi.square.slots.api.Connection;
import mobi.square.slots.classes.Machine;
import mobi.square.slots.enums.SlotsType;
import mobi.square.slots.stages.Basic;

public class MBookOfRa extends Machine {

    private Sound spin_time_sound;
    private Sound win_sound;

    private MBookOfRa(Basic parent) {
        super(parent, SlotsType.BOOK_OF_RA);
    }

    public static MBookOfRa newInstance(Basic parent) {
        MBookOfRa instance = new MBookOfRa(parent);
        return instance;
    }

    @Override
    public void initialize(SlotsType type) {
        super.initialize(type);
        this.spin_time_sound = Connection.getManager().get("BookOfRa/spin_time.ogg", Sound.class);
        this.win_sound = Connection.getManager().get("BookOfRa/win.ogg", Sound.class);
    }

    @Override
    public void start() {
        if (!super.started) {
            super.start();
            if (Connection.getInstance().isSoundOn() && this.spin_time_sound != null) {
                this.spin_time_sound.play();
                this.win_sound.stop();
            }
        }
    }

    @Override
    protected void machineStopped() {
        super.machineStopped();
        if (Connection.getInstance().isSoundOn() && this.spin_time_sound != null) {
            this.spin_time_sound.stop();
        }
        if ((super.lines != null && super.lines.getCount() > 0) ||
                (super.img_lines != null && super.img_lines.getCount() > 0)) {
            this.win_sound.play();
        }
        super.machineStopped();
    }

    @Override
    protected Sound getReelStopSound() {
        return Connection.getManager().get("BookOfRa/reel_stop.ogg", Sound.class);
    }

    @Override
    protected Sound getMachineStartSound() {
        return null;
    }

    @Override
    protected Sound getStartSpinSound() {
        return null;
    }

}
