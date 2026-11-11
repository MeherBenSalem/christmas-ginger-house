# Christmas Ginger House 2.0.1

Bug fix for Minecraft 26.1.2 and 26.2 (Fabric and NeoForge).

## Fixed
- The game crashed ("Ticking entity: Can't find attribute minecraft:tempt_range") as soon as a Gingerbread Man or a Reindeer was loaded. Minecraft 26.x reads a tempt range for any mob that follows food, and these two mobs did not have one. They now use the vanilla range of 10 blocks, so reindeer follow carrots, apples and candy canes and gingerbread men follow icing and sugar again.

Minecraft 1.20.1 and 1.21.1 were not affected and stay on 2.0.0.
