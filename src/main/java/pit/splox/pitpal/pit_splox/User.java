package pit.splox.pitpal.pit_splox;

import pit.splox.pitpal.Listenable;
import pit.splox.pitpal.PitPal;
import static pit.splox.pitpal.PitPal.*;

import net.minecraft.client.Minecraft;

public class User extends Listenable<User> {
    public boolean isAuthenticated = false;

    public static User getInstance() {
        return PitPal.getInstance().user;
    }

    public void authenticate() {
        if (isAuthenticated) {
            logger.warn("User already authenticated");
            return;
        }
    }
}
