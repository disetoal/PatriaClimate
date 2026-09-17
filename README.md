# PatriaClimate

PatriaClimate is the survival climate system I made for PatriaCraft. It started as a thirst plugin, then I turned it into a full body/environment system because I wanted biomes, rain, altitude, armor and water to actually matter while playing.

The version documented here is `1.1.4` for Paper `1.21.11` / Java 21.

## Main systems

- thirst and hydration
- body temperature in Celsius
- separate environment temperature
- wetness from rain/water and drying over time
- hot/cold acclimatization
- altitude cooling and underground geothermal heat
- day/night temperature changes
- rain, water and wind cooling
- shelter/shade checks
- nearby heat and cold sources
- armor insulation and heat resistance by armor material
- dirty water and purified water bottles
- direct drinking from water blocks
- thermal effects and damage at dangerous body temperatures
- WorldGuard region climate overrides
- PlaceholderAPI placeholders
- separate Java and Bedrock HUD rendering
- resource-pack glyph support

## How the code is organized

The plugin is currently compact: most of the survival logic is inside `PatriaClimate`, with a small `PatriaClimateExpansion` class for PlaceholderAPI.

```text
bo.patriacraft.climate
├── PatriaClimate
│   ├── ArmorProfile
│   ├── ClimateSnapshot
│   └── RegionEffect
└── PatriaClimateExpansion
```

I kept the systems in one main class while I was iterating quickly on the mechanics, but the logic itself is split into methods for thirst, temperature, wetness, climate calculation, HUD and integrations.

See [docs/CODE_STRUCTURE.md](docs/CODE_STRUCTURE.md) for the method-level breakdown.

## Main update flow

The plugin keeps persistent player values using Bukkit `NamespacedKey` + `PersistentDataContainer`, so things such as thirst and body temperature do not depend only on in-memory maps.

A simplified player tick looks like this:

```text
player tick
   ├── update thirst/exhaustion
   ├── update wetness
   ├── calculate environment snapshot
   ├── update acclimatization
   ├── move body temperature toward target
   ├── apply thermal state/effects
   └── render HUD
```

The environment snapshot combines a lot of smaller values instead of using one hardcoded temperature.

```text
biome
+ dimension
+ altitude
+ geothermal
+ time of day
+ sun/shade
+ wind
+ rain/water
+ nearby heat/cold blocks
+ held item modifier
+ WorldGuard region modifier
= environment temperature
```

Then body temperature uses the environment, wetness, armor and acclimatization to decide where the player's body temperature should move.

## Player data tracked

The current build tracks values such as:

```text
thirst
hydration
thirst exhaustion
body temperature
environment temperature
wetness
hot adaptation
cold adaptation
HUD enabled state
temperature state/delays
water bottle state (dirty/purified)
```

## Water system

Normal water bottles can be marked as dirty. Dirty water restores less thirst/hydration and can apply negative effects depending on the configured contamination chances.

Players can also drink directly from a water block. The code searches for a valid water block in range, has a cooldown and plays the normal drinking sound.

Purification is handled through an interaction with a lit campfire while holding a water bottle.

## HUD

I made the HUD work differently for Java and Bedrock because the same resource-pack/action-bar layout does not always line up correctly on both clients.

The plugin can use resource-pack glyphs for thirst drops and temperature states, and it checks if the oxygen bar is visible so the custom HUD does not overlap it as badly.

There is also a plain fallback renderer when the Adventure/resource-pack path is not available.

## Integrations

Soft dependencies used by this build:

```text
PlaceholderAPI
WorldGuard
Floodgate
Geyser-Spigot
```

WorldGuard regions can replace or modify environment temperature and also change wind/drying multipliers.

## Commands

```text
/patriaclimate
/pclimate
/pcclimate
/temperature
/thirst
```

Permissions:

```text
patriaclimate.use
patriaclimate.admin
```

## PlaceholderAPI

Identifier: `patriaclimate`

Examples:

```text
%patriaclimate_state%
%patriaclimate_temperature_celsius%
%patriaclimate_environment_celsius%
%patriaclimate_thirst%
%patriaclimate_thirst_percent%
%patriaclimate_hydration%
%patriaclimate_wetness%
%patriaclimate_hot_adaptation%
%patriaclimate_cold_adaptation%
%patriaclimate_bedrock%
```

More details are in [docs/API_AND_PLACEHOLDERS.md](docs/API_AND_PLACEHOLDERS.md).

## About the source

The actual source and jar are private because this plugin is still part of my server. I did not want a public repo where somebody can just download the project and reuse it.

Instead I documented the class structure, important methods, data flow, configuration and small safe code examples. That lets me show how the plugin works without publishing the full implementation.
