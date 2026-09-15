# Christmas Ginger House

A festive gingerbread house structure that generates in snowy and plains biomes.

## Features

- Jigsaw worldgen structure (`house_ginger`)
- Spawns in snowy slopes, plains, snowy plains, and snowy taiga (plus `#minecraft:is_snowy` on 26.x)
- MultiLoader: Fabric, Forge, and NeoForge depending on Minecraft version

## Version matrix

| Root | Minecraft | Loaders |
| --- | --- | --- |
| `1.20.1/` | 1.20.1 | Fabric, Forge |
| `1.21.1/` | 1.21.1 | Fabric, NeoForge |
| `26.1.2/` | 26.1.2 | Fabric, NeoForge |
| `26.2/` | 26.2 | Fabric, NeoForge |

## Build

```bash
cd 26.2   # or 26.1.2 / 1.21.1 / 1.20.1
./gradlew build
```

JDK 25 is required for 26.x roots. JDK 21 for 1.21.1. JDK 17 for 1.20.1.

## Publish IDs

- Modrinth: `Ddgd2w2n` ([the-gingerbread-house](https://modrinth.com/mod/the-gingerbread-house))
- CurseForge: `1409805`

Local upload (26.1.2 and 26.2):

```bash
node scripts/upload_platforms.mjs --workspace 26.1.2 --version 1.1.0
node scripts/upload_platforms.mjs --workspace 26.2 --version 1.1.0
```

## Contributing

See [CONTRIBUTING.md](CONTRIBUTING.md).

## Security

See [.github/SECURITY.md](.github/SECURITY.md).

## License

Apache License 2.0. See [LICENSE](LICENSE) and [NOTICE](NOTICE).
