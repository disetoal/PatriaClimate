// Small public API example only. This is not the PatriaClimate source code.

import bo.patriacraft.climate.PatriaClimate;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public final class ClimateApiExample {
    public static void showClimate(Player player) {
        PatriaClimate climate = (PatriaClimate) Bukkit.getPluginManager()
                .getPlugin("PatriaClimate");

        if (climate == null) return;

        double body = climate.getBodyCelsius(player);
        double environment = climate.getEnvironmentCelsius(player);
        double wetness = climate.getWetness(player);

        player.sendMessage("Body: " + body + " C");
        player.sendMessage("Environment: " + environment + " C");
        player.sendMessage("Wetness: " + wetness);
    }
}
