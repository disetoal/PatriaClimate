# API and placeholders

PatriaClimate is mainly an internal server plugin, but the current main class exposes some useful read methods.

## Java read API

```java
public double getEnvironmentCelsius(Player player)
public double getBodyCelsius(Player player)
public double getWetness(Player player)
public double getHotAdaptation(Player player)
public double getColdAdaptation(Player player)
public String resolvePlaceholder(Player player, String key)
```

These are read-oriented methods. The internal setters remain private so other plugins cannot freely put a player into an invalid climate state.

### Example

This is only an API usage example, not the plugin source:

```java
PatriaClimate climate = (PatriaClimate) Bukkit.getPluginManager()
        .getPlugin("PatriaClimate");

if (climate != null) {
    double body = climate.getBodyCelsius(player);
    double ambient = climate.getEnvironmentCelsius(player);

    player.sendMessage("Body: " + body + "C / Outside: " + ambient + "C");
}
```

## PlaceholderAPI

Expansion identifier:

```text
patriaclimate
```

Supported keys in the current build:

| Placeholder | Value |
| --- | --- |
| `%patriaclimate_temperature%` | thermal state name |
| `%patriaclimate_state%` | thermal state name |
| `%patriaclimate_temperature_celsius%` | body temperature, 1 decimal |
| `%patriaclimate_body_celsius%` | body temperature, 1 decimal |
| `%patriaclimate_environment_celsius%` | environment temperature, 1 decimal |
| `%patriaclimate_thirst%` | current thirst, 1 decimal |
| `%patriaclimate_thirst_percent%` | thirst percentage |
| `%patriaclimate_hydration%` | hydration value |
| `%patriaclimate_wetness%` | wetness percentage/value |
| `%patriaclimate_hot_adaptation%` | hot adaptation percentage |
| `%patriaclimate_cold_adaptation%` | cold adaptation percentage |
| `%patriaclimate_bedrock%` | `true` / `false` |

The PlaceholderAPI expansion is kept in a separate `PatriaClimateExpansion` class and delegates values back to `PatriaClimate.resolvePlaceholder(...)`.
