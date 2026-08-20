# Enchanted Forest verification log

## v0.1.9 — post-ship audit and the render gap it exposed (2026-08-20)

0.1.9 shipped at 03:05Z on 2026-08-20. This entry is written afterwards, during a
folder-wide audit that found this release had no verification log and — more to the
point — **had shipped a new textured block without a render battery run**.

### The gap

0.1.9 is "Craft purple heartwood into purple planks". It adds a block, a blockstate, a
block model, an item model, a 270-byte texture, a recipe, and three tag entries. The
newest frames in `build/run-gametest/screenshots/` were timestamped 2026-08-19 21:57;
the commit is 23:01. So the release went out on screenshots taken **before the block
existed**, which is exactly the shape of the two cosmos releases rule 9 was written
after: server-side checks all green, nothing to look at.

Worse, running the battery as it stood would not have closed the gap. It took two frames,
both of the grove, and touched no plank at all. A battery that does not cover the change
is not evidence about the change.

### Fixed, and the evidence

The battery now places the new planks next to the heartwood they are crafted from and the
older enchanted planks, and asserts them from the server before the shutter:

- `runGametest` **BUILD SUCCESSFUL** (2026-08-20 16:33). ✅
- `ENCHANTED_FOREST_PLANKS_AUDIT heartwood_planks=8 planks=8 problems=none` — both blocks
  place as real blocks, and `enchanted_forest:enchanted_heartwood_planks` resolves as a
  recipe in the server's recipe manager. ✅
- `ENCHANTED_FOREST_AUDIT tree=BlockPos{x=-8, y=-60, z=4} grove=15 trunk=5 total=7 bird=1
  fox=1 bear=1 problems=none` — the pre-existing grove and wildlife checks still pass. ✅

**Frames read, not merely produced** (rule 9):

| Frame | What it shows |
|---|---|
| `0002_enchanted_forest_heartwood_planks_and_planks` | All three woods in one shot. The new heartwood planks draw as a plank texture in a distinctly brighter magenta, clearly separable from both the heartwood they come from and the older enchanted planks beside them. Nothing untextured, nothing missing, no truncated sheet. ✅ |
| `0000/0001_..._purple_grove_and_wildlife_glint_a/b` | Fifteen-tree grove at full canopy, foxes and bird in frame, palette consistent. ✅ |

The brighter magenta is deliberate, not a slip: the generator recolors `oak_planks` at
hue 0.82 for heartwood planks against 0.76 for enchanted planks, so the two woods are
meant to read apart. The frame confirms they do.

### Generator fidelity

`node tools/generate.js` regenerates every asset in place and leaves the working tree
clean. The committed textures, models, recipes and tags are exactly what the generator
produces — no hand-edit has drifted in. ✅

### What is actually deployed

- The jar in the server's `/mods` hashes to `3f5cd46bf97a467a5075d044…`, exactly what
  `mods.json` declares — verified by pulling the file off the server and hashing it. ✅
- `enchanted_forest 0.1.9` initialised in the 13:43 boot, in a 104-mod set with zero mixin
  failures and zero errors. ✅
- Rebuilt from committed source at `v0.1.9`, `tools/jar-compare.js` reports **SAME CONTENT**
  against the shipped jar: every one of the 167 entries matches by CRC, and the jars differ
  only in the order Loom wrote `Fabric-Loom-Client-Only-Entries` into the manifest. The
  shipped artifact is the committed source. ✅

### Not covered by this entry

- **No confirmation that enchanted forest biome or trees have generated on the live
  server.** Biome placement only affects chunks generated after the mod landed, and the
  world's directories predate 0.1.9. The frames above are from a throwaway gametest world.
- Nobody has crafted the planks on the live server. The recipe is proven to load, not to
  have been used.
