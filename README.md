# Enchanted Forest

## v0.1.8 — forest-floor placement and true armor glint

Enchanted trees now accept grass blocks as valid soil under Minecraft 26.2,
so the configured 18 attempts per chunk can actually produce a dense forest.
Tree trunks also use the real armor-glint texture transform and layering mode,
with two timed render-test frames covering the animated effect.

## v0.1.7 — full-size biome regions

Enchanted forests now replace complete birch-forest climate entries instead of
splitting off narrow erosion slices. This prevents tiny enchanted biome islands and
gives the biome the same practical region scale as vanilla birch forests.

## v0.1.6 — a forest, not a plains

Tree placement now makes roughly 9–13 successful vanilla-height trees per chunk
instead of a sparse handful. Ordinary forest flowers and vanilla grass patches have
been removed in favor of much denser starflowers, fairy blooms, and crystal moss.
Every part of every enchanted tree now uses one coherent purple palette—from leaves
and sapling through heartwood and trunk—beneath the matching enchanted glint.

A Minecraft 26.2 Fabric biome built around luminous vegetation and trees whose trunks
carry the moving purple glint used by enchanted equipment.

The biome occupies birch-forest climate entries, keeping it separate from Waldschatten's
ordinary/dark-forest claim. Its custom tree feature places one render anchor per tree,
rather than a block entity in every log or leaf, so the animated glint remains practical
in a dense forest.

The biome also has its own enchanted wildlife: five color variants of flying,
tameable birds; foxes with their normal hunting, sleeping, and trust behavior; and
rare family-defending bears. Their textures preserve the vanilla animal UV layouts
and animation-specific sheets while shifting the palette toward cyan and violet.

Enchanted trees stay at vanilla-oak scale: five trunk blocks and no more than seven
blocks from their base to the top of the canopy.
