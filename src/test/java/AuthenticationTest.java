import net.brxen.mojangapi.AuthenticationWrapper;
import net.brxen.mojangapi.Player;

import java.util.Scanner;
import java.util.UUID;

public class AuthenticationTest {

    public static void main(String[] args) {
        AuthenticationWrapper wrapper = new AuthenticationWrapper(UUID.randomUUID().toString(), "BLAHBLAHJKFKJEFjk 1.0 / Test");
        Scanner in = new Scanner(System.in);
        Player p = wrapper.authenticate("", "");
        System.out.println(String.format("UUID: %s, Username: %s, Token: %s", p.getUUID(), p.getUsername(), p.getAccessToken()));
        System.out.println((p = wrapper.refresh(p.getAccessToken())) == null ? "Refresh failed" : "Refresh success!");
        System.out.println(String.format("UUID: %s, Username: %s, Token: %s", p.getUUID(), p.getUsername(), p.getAccessToken()));

    }

}
