# Code structure

## Main class

```text
bo.patriacraft.climate.PatriaClimate
```

It extends `JavaPlugin` and implements Bukkit `Listener`, `CommandExecutor` and `TabCompleter`.

The code is split by responsibility even though it currently lives mostly in one class.

## Player lifecycle

```java
onJoin(PlayerJoinEvent event)
onQuit(PlayerQuitEvent event)
initialize(Player player)
tickAllPlayers()
```

`initialize` makes sure the persistent values exist for a player. Temporary caches are cleaned when the player leaves.

## Thirst

Important methods:

```java
updateThirst(Player player)
drink(Player player, double thirst, double hydration)
getThirst(Player player)
setThirst(Player player, double value)
getHydration(Player player)
setHydration(Player player, double value)
addThirstExhaustion(Player player, double value)
```

Bukkit exhaustion events feed into the custom thirst exhaustion value. Sprinting and natural health regeneration can be restricted when thirst is below configured limits.

When thirst reaches zero, damage is not applied every tick; the plugin uses a configurable interval.

## Temperature and wetness

```java
updateTemperature(Player player)
updateWetness(Player player)
calculateClimateSnapshot(Player player)
handleExtremeDamage(Player player, double bodyCelsius, int level)
thermalState(double bodyCelsius)
applyThermalStateEffects(Player player, double bodyCelsius)
```

The important part is that body temperature and environment temperature are separate values.

`calculateClimateSnapshot` builds one read-only snapshot with the inputs used during that update. The current snapshot includes:

```text
biomeRaw
biomeCelsius
dimensionCelsius
altitudeCelsius
geothermalCelsius
timeCelsius
sunShelterCelsius
windCelsius
rainWaterCelsius
heatSourcesCelsius
heldCelsius
regionCelsius
environmentCelsius
armorColdInsulation
armorHeatResistance
armorPieces
wetness
hotAdaptation
coldAdaptation
bodyTargetCelsius
sheltered
raining
inWater
inLava
regionNames
```

That snapshot is also useful for the debug command because it makes it possible to see *why* the calculated temperature is what it is instead of only seeing one final number.

## Climate calculation

This is the general shape of the calculation, simplified so the private implementation is not published:

```java
ambient = biomeTemperature(player);
ambient += dimensionModifier(player.getWorld());
ambient += altitudeModifier(player.getLocation().getY());
ambient += geothermalModifier(player.getLocation().getY());
ambient += timeModifier(player.getWorld());
ambient += shelterAndSunModifier(player);
ambient += windModifier(player);
ambient += rainAndWaterModifier(player);
ambient += nearbyBlocksModifier(player);
ambient += heldItemModifier(player);
ambient = applyWorldGuardRegionOverride(ambient, player);

bodyTarget = calculateBodyTarget(ambient, wetness, armor, adaptation);
```

The real code also clamps values and handles special cases such as lava, fire, Nether and End conditions.

## Armor

The class uses an `ArmorProfile` value object.

```text
coldInsulation
heatResistance
```

Each material can have different cold and heat behavior. This means four pieces of leather are not treated exactly the same as four pieces of netherite.

The config also limits the total reduction so armor cannot completely remove the survival mechanic.

## Nearby blocks

`proximityTemperatureCelsius` scans a configured radius and assigns calibrated values to heat/cold sources.

Examples include:

```text
lava
magma
fire / soul fire
campfires
furnaces
ice
packed ice
blue ice
snow
powder snow
```

The result is clamped before being added to the environment calculation.

## Wetness

Wetness increases in water and rain. Drying depends on base drying speed, heat and fire.

WorldGuard regions can also change drying speed through a multiplier.

Wet players cool faster in cold conditions, which is why rain on a mountain is more dangerous than the same rain in a warm low area.

## Acclimatization

The plugin stores hot and cold adaptation separately.

```java
getHotAdaptation(Player player)
getColdAdaptation(Player player)
updateAcclimatization(Player player, ClimateSnapshot snapshot)
```

Adaptation slowly rises in repeated hot/cold environments and decays outside them. It can shift the body target by a limited amount instead of making the player immune.

## Water

```java
findDrinkableWater(Player player, Block clicked)
isDrinkableWaterBlock(Block block)
isWaterPotion(ItemStack item)
isPurifiedWater(ItemStack item)
markPurifiedWater(ItemStack item)
markDirtyWater(ItemStack item)
drinkDirtyWater(Player player, String source)
```

The dirty/purified state is stored on the item instead of relying only on display text.

## Bedrock detection

`isBedrockPlayer` uses Floodgate when it is available and caches the result by UUID.

That result is used mainly for HUD rendering and exposed through PlaceholderAPI.

## HUD

```java
sendHud(Player player)
renderAdventureHud(Player player)
renderPlainHud(Player player)
renderBedrockHud(Player player)
clearActionBar(Player player)
```

The Adventure methods are accessed through a small compatibility bridge so the plugin can degrade gracefully if a method is unavailable in the runtime it is loaded on.

## WorldGuard

The plugin caches the set of region ids around each player for a short amount of time rather than doing a full region lookup every HUD/update operation.

A region effect can contain:

```text
environment temperature override
additive Celsius modifier
wind multiplier
drying multiplier
```

## Caches

Not everything is stored permanently. Short-lived data uses in-memory maps, for example:

```text
latest climate snapshot
dirty-water drink cooldown
Bedrock detection result
WorldGuard regions + cache tick
extreme temperature damage timers
```

Persistent gameplay values stay in Bukkit's `PersistentDataContainer`.
