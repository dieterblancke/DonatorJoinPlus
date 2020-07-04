package com.dbsoftwares.djp.user;

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
    private String leaveSound;
    private boolean soundToggled;
    private boolean fireworkToggled;
    private boolean messagesMuted;

}
