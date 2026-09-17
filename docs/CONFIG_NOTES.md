# Configuration notes

I kept most mechanics configurable so I could tune them on the live survival server without rebuilding the plugin.

## World control

```yaml
enabled-worlds:
  - survival
  - survival_nether

disabled-worlds:
  - lobby
```

## Thirst

Controls max thirst, sprint threshold, regeneration threshold, zero-thirst damage and heat-based exhaustion.

```yaml
thirst:
  max: 20.0
  prevent-sprint: true
  sprint-min-thirst: 6.0
  prevent-natural-regen: true
```

## Dirty water

The dirty water section controls direct drinking, cooldown, restored thirst/hydration and contamination effects.

```yaml
water:
  dirty:
    allow-direct-drink: true
    direct-drink-cooldown-ms: 1500
    direct-drink-distance: 5
    contamination-chance: 0.25
```

## Body temperature

The plugin uses a normal body target around 37 C and separate limits for thermal states.

```yaml
temperature:
  body:
    normal-celsius: 37.0
    comfort-environment-celsius: 20.0
  states:
    hypothermia-below: 34.5
    overheating-above: 39.5
```

## Region overrides

WorldGuard region ids are matched case-insensitively.

```yaml
integrations:
  worldguard:
    region-overrides:
      - region: example_hot_zone
        environment-celsius: 46.0
        drying-multiplier: 1.60
      - region: example_mountain
        environment-celsius: -12.0
        wind-multiplier: 1.50
```

A region can either replace the environment temperature or add/subtract from the normal calculation.
