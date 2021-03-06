package org.societies.teleport;

import org.societies.api.math.Location;
import org.societies.groups.member.Member;


/**
 * Represents a DisabledTeleporter
 */
class DisabledTeleporter implements Teleporter {
    @Override
    public void teleport(Member member, Location target) {
        member.send("teleport.disabled");
    }
}
