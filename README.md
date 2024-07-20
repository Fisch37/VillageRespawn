# VillageRespawn
VillageRespawn is a Fabric mod intended for exploration-heavy gameplay.
Every time you enter a new village, your respawn point is automatically set
to that village's centre (i.e. the bell).

This makes visiting villages actually relevant besides stealing all the hay.
It also means that players will not have to run for 5000 blocks because they
died in that one structure they visited. Players will no longer have to worry
about setting their spawn when adventuring.

## Other features
### Village Names
Villages also get randomly assigned names! When you enter a village you will get
a neat pop-up telling you its name. If the text is green, that means you haven't
visited this village before, otherwise it will be grey.

The names are from [fantasynamegenerators.com](https://www.fantasynamegenerators.com/town-names.php).
There are a total of 666 (I promise that's a coincidence) town names available, but
you may encounter duplicates since I'm not tracking used names right now.

### Old Villages
When entering a village you have already been to, your spawn won't be set by default,
but you can set it manually by shift-clicking the bell.
Note that this only works for bells within villages!

### Minimap Support
This mod has built-in support for [JourneyMap](https://modrinth.com/mod/journeymap)
and [Xaero's Minimap](https://modrinth.com/mod/xaeros-minimap) and shows visited
villages as waypoints. Voxelmap support is something I intend to add just for the fun
of it, but the release status on that is undetermined.
(See the [voxelmap](https://github.com/Fisch37/VillageRespawn/tree/voxelmap) branch for status)

### Modding Support
Whether a structure qualifies as a "village" is decided using the new
`#village_respawn:village` structure tag. This means datapacks and mods
can easily extend the tag as needed. The mod also has built-in compatibility
with [Choice Theorem's Overhauled Villages](https://modrinth.com/mod/ct-overhaul-village).

## Beta notice
This mod is still a work-in-progress and as such you may encounter small behavioural quirks
if not downright bugs (even though I've tested this mod extensively).

Some behaviours may change though I don't expect any "breaking changes" to occur.
Essentially, you should have no problems updating to newer versions unless otherwise specified.
