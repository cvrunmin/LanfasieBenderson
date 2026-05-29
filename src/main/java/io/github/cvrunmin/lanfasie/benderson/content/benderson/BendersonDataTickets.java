package io.github.cvrunmin.lanfasie.benderson.content.benderson;

import com.geckolib.constant.DataTickets;
import com.geckolib.constant.dataticket.DataTicket;
import com.google.common.reflect.TypeToken;
import net.minecraft.world.phys.Vec3;

public class BendersonDataTickets {
    public static final DataTicket<String> ANIMATE_STATE = DataTickets.create("animate_state", new TypeToken<>() {});
    public static final DataTicket<Vec3> ARENA_CENTER = DataTickets.create("arena_center", new TypeToken<>() {});
    public static final DataTicket<Integer> ARENA_RANGE = DataTickets.create("arena_range", new TypeToken<>() {});
}
