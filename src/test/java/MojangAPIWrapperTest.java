import net.brxen.mojangapi.MojangAPIWrapper;
import net.brxen.mojangapi.entry.NameHistoryEntry;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

public class MojangAPIWrapperTest {

    public static void main(String[] args) throws IOException {
        MojangAPIWrapper wrapper = new MojangAPIWrapper("Poggers 1.0 / Alpha");

        for (NameHistoryEntry entry : wrapper.fromUUID(UUID.fromString("547b8192-7905-44dd-90ae-58608787c141"))) {
            Long date = entry.getDate();
            System.out.println(entry.getName() + " " + (date != null ? date : ""));
        }


    }

}
