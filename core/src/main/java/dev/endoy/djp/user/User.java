package dev.endoy.djp.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class User
{

    private final UUID uuid;
    private boolean toggled;
    private String slotGroup;
    private String joinSound;
    private float joinSoundVolume;
    private float joinSoundPitch;
    private String leaveSound;
    private float leaveSoundVolume;
    private float leaveSoundPitch;
    private boolean soundToggled;
    private boolean fireworkToggled;
    private boolean messagesMuted;

    public void setJoinSound(String joinSound, float joinSoundVolume, float joinSoundPitch) {
        this.joinSound = joinSound;
        this.joinSoundVolume = joinSoundVolume;
        this.joinSoundPitch = joinSoundPitch;
    }

    public void setLeaveSound(String leaveSound, float leaveSoundVolume, float leaveSoundPitch) {
        this.leaveSound = leaveSound;
        this.leaveSoundVolume = leaveSoundVolume;
        this.leaveSoundPitch = leaveSoundPitch;
    }
}
