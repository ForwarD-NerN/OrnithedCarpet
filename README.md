# Ornithed Carpet

Carpet mod for 1.13.2-1.7.10

## Supported Versions

| version |  supported  |
|:-------:|:-----------:|
| 1.13.2  |      ✔      |
| 1.12.2  |      ✔      |
| 1.11.2  |      ✔      |
| 1.10.2  |      ✔      |
|  1.9.4  |      ✔      |
|  1.8.9  |      ✔      |
| 1.7.10  |      ✔      |

## Features

**This mod is compatible with fabric-carpet protocol**

## Rules

### language

Sets the language for Carpet

- Type: `String`
- Default: `en_us`
- Suggestions: `en_us`
- Categories: `FEATURE`

### carpetCommandPermissionLevel

Carpet command permission level

- Type: `String`
- Default: `ops`
- Suggestions: `ops`, `2`, `4`
- Categories: `CREATIVE`

### portalCreativeDelay

Amount of delay ticks to use a nether portal in creative

- Type: `Integer`
- Default: `1`
- Suggestions: `1`, `40`, `80`, `72000`
- Categories: `SURVIVAL`

### portalSurvivalDelay

Amount of delay ticks to use a nether portal in survival

- Type: `Integer`
- Default: `80`
- Suggestions: `1`, `40`, `80`, `72000`
- Categories: `SURVIVAL`

### persistentParrots

Parrots don't get of your shoulders until you receive proper damage

- Type: `Boolean`
- Default: `false`
- Suggestions: `true`, `false`
- Categories: `SURVIVAL`, `FEATURE`
- Versions: `1.12+`

### xpNoCooldown

Players absorb XP instantly, without delay

- Type: `Boolean`
- Default: `false`
- Suggestions: `true`, `false`
- Categories: `CREATIVE`

### pushLimit

Customizable piston push limit

- Type: `Integer`
- Default: `12`
- Suggestions: `10`, `12`, `14`, `100`
- Categories: `CREATIVE`

### railPowerLimit

Customizable powered rail power range

- Type: `Integer`
- Default: `9`
- Suggestions: `9`, `15`, `30`
- Categories: `CREATIVE`

### creativeNoClip

Creative No Clip

- Type: `Boolean`
- Default: `false`
- Suggestions: `true`, `false`
- Categories: `CREATIVE`, `CLIENT`

### explosionNoBlockDamage

Explosions won't destroy blocks

- Type: `Boolean`
- Default: `false`
- Suggestions: `true`, `false`
- Categories: `CREATIVE`, `TNT`

### tntDoNotUpdate

TNT doesn't update when placed against a power source

- Type: `Boolean`
- Default: `false`
- Suggestions: `true`, `false`
- Categories: `CREATIVE`, `TNT`

### quasiConnectivity

Pistons, droppers, and dispensers check for power to the block(s) above them

- Type: `Integer`
- Default: `1`
- Suggestions: `0`, `1`, `2`, `3`
- Categories: `CREATIVE`

### hopperCounter

Hoppers pointing to wool will count items passing through them, enables /counter command

- Type: `Boolean`
- Default: `false`
- Suggestions: `true`, `false`
- Categories: `COMMAND`, `CREATIVE`, `FEATURE`

### commandTick

Enables /tick command to control game clocks

- Type: `String`
- Default: `ops`
- Suggestions: `ops`, `2`, `4`
- Categories: `COMMAND`

### commandFill

Enables /fill command for minecraft 1.7.10-

- Type: `String`
- Default: `ops`
- Suggestions: `ops`, `2`, `4`
- Categories: `COMMAND`
- Versions: `1.7.10-`

### fillLimit

Customizable fill volume limit

- Type: `Integer`
- Default: `32768`
- Suggestions: `32768`, `250000`, `1000000`
- Categories: `CREATIVE`

### emeraldOreUpdateSuppressor

Makes all emerald ore blocks act as an update suppressor

- Type: `Boolean`
- Default: `false`
- Suggestions: `true`, `false`
- Categories: `CREATIVE`

### smoothClientAnimations

smooth client animations with low tps settings

- Type: `Boolean`
- Default: `false`
- Suggestions: `true`, `false`
- Categories: `CREATIVE`, `SURVIVAL`, `CLIENT`
