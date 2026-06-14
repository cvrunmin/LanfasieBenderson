package io.github.cvrunmin.lanfasie.benderson.content.benderson;

import com.geckolib.constant.DataTickets;
import com.geckolib.constant.dataticket.DataTicket;
import com.google.common.reflect.TypeToken;
import io.github.cvrunmin.lanfasie.benderson.content.anticalabrum.Anticalabrum;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

public class BendersonDataTickets {
    public static final DataTicket<String> ANIMATE_STATE = DataTickets.create("animate_state", new TypeToken<>() {});
    public static final DataTicket<Benderson.BodyState> BODY_STATE = DataTickets.create("body_state", new TypeToken<>() {});
    public static final DataTicket<Optional<Anticalabrum.AnticalabrumType>> OPTIONAL_ANTI_TYPE = DataTickets.create("optional_anti_type", new TypeToken<>() {});
    public static final DataTicket<Vec3> MODEL_ROOT_POS = DataTickets.create("model_root_pos", new TypeToken<>() {});
}
