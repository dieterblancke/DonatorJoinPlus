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
    private Integer joinSoundVolume;
    private Integer joinSoundPitch;
    private String leaveSound;
    private Integer leaveSoundVolume;
    private Integer leaveSoundPitch;
    private boolean soundToggled;
    private boolean fireworkToggled;
    private boolean messagesMuted;

    public void setJoinSound(String joinSound, Integer joinSoundVolume, Integer joinSoundPitch) {
        this.joinSound = joinSound;
        this.joinSoundVolume = joinSoundVolume;
        this.joinSoundPitch = joinSoundPitch;
    }

    public void setLeaveSound(String leaveSound, Integer leaveSoundVolume, Integer leaveSoundPitch) {
        this.leaveSound = leaveSound;
        this.leaveSoundVolume = leaveSoundVolume;
        this.leaveSoundPitch = leaveSoundPitch;
    }
}
