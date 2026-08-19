#!/usr/bin/env node
const fs = require("node:fs");
const path = require("node:path");
const png = require("./png.js");

const NS = "enchanted_forest";
const ROOT = path.join(__dirname, "..", "src", "main", "resources");
const A = (...parts) => path.join(ROOT, "assets", NS, ...parts);
const D = (...parts) => path.join(ROOT, "data", NS, ...parts);
const write = (file, value) => {
	fs.mkdirSync(path.dirname(file), { recursive: true });
	fs.writeFileSync(file, JSON.stringify(value, null, 2) + "\n");
};

function randomFor(name) {
	let seed = 2166136261;
	for (const char of name) seed = Math.imul(seed ^ char.charCodeAt(0), 16777619);
	return () => {
		seed |= 0; seed = (seed + 0x6d2b79f5) | 0;
		let t = Math.imul(seed ^ (seed >>> 15), 1 | seed);
		t = (t + Math.imul(t ^ (t >>> 7), 61 | t)) ^ t;
		return ((t ^ (t >>> 14)) >>> 0) / 4294967296;
	};
}

class Canvas {
	constructor(name) {
		this.name = name;
		this.pixels = Buffer.alloc(16 * 16 * 4);
		this.random = randomFor(name);
	}
	set(x, y, color, alpha = 255) {
		if (x < 0 || y < 0 || x >= 16 || y >= 16) return;
		const offset = (y * 16 + x) * 4;
		this.pixels[offset] = color[0];
		this.pixels[offset + 1] = color[1];
		this.pixels[offset + 2] = color[2];
		this.pixels[offset + 3] = alpha;
	}
	fill(color, alpha = 255) {
		for (let y = 0; y < 16; y++) for (let x = 0; x < 16; x++) this.set(x, y, color, alpha);
	}
	write() {
		const file = A("textures", "block", `${this.name}.png`);
		fs.mkdirSync(path.dirname(file), { recursive: true });
		fs.writeFileSync(file, png.encode(16, 16, this.pixels));
	}
}

const COLORS = {
	indigo: [31, 28, 75], blue: [38, 91, 128], cyan: [72, 231, 219],
	mint: [83, 207, 164], violet: [175, 104, 255], pink: [255, 119, 232],
	gold: [255, 223, 112], pale: [221, 255, 248], moss: [42, 145, 132],
};

function bark(name, base, bright) {
	const c = new Canvas(name); c.fill(base);
	for (let x = 0; x < 16; x++) for (let y = 0; y < 16; y++) {
		const r = c.random();
		if (r < 0.22) c.set(x, y, base.map(v => Math.max(0, v - 18)));
		else if (r > 0.91 || (x + y * 3) % 23 === 0) c.set(x, y, bright);
	}
	for (let n = 0; n < 8; n++) {
		let x = Math.floor(c.random() * 16);
		for (let y = 0; y < 16; y++) {
			if (c.random() < 0.28) x += c.random() < 0.5 ? -1 : 1;
			c.set(x, y, c.random() < 0.2 ? COLORS.pink : bright);
		}
	}
	c.write();
}

function rings(name, base) {
	const c = new Canvas(name);
	for (let y = 0; y < 16; y++) for (let x = 0; x < 16; x++) {
		const distance = Math.hypot(x - 7.5, y - 7.5);
		const ring = Math.sin(distance * 2.6) > 0;
		c.set(x, y, distance > 6.8 ? COLORS.indigo : ring ? base : COLORS.cyan);
	}
	for (const [x, y] of [[3, 4], [11, 5], [6, 12], [12, 11]]) c.set(x, y, COLORS.gold);
	c.write();
}

function leaves() {
	const c = new Canvas("enchanted_leaves");
	for (let y = 0; y < 16; y++) for (let x = 0; x < 16; x++) {
		const r = c.random();
		if (r < 0.15) c.set(x, y, [0, 0, 0], 0);
		else if (r < 0.52) c.set(x, y, COLORS.moss);
		else if (r < 0.87) c.set(x, y, COLORS.mint);
		else c.set(x, y, c.random() < 0.5 ? COLORS.cyan : COLORS.violet);
	}
	c.write();
}

function planks() {
	const c = new Canvas("enchanted_planks"); c.fill(COLORS.blue);
	for (const y of [0, 5, 10, 15]) for (let x = 0; x < 16; x++) c.set(x, y, COLORS.indigo);
	for (let y = 0; y < 16; y++) for (let x = 0; x < 16; x++) {
		if ((x * 5 + y * 7) % 37 === 0) c.set(x, y, COLORS.cyan);
	}
	c.write();
}

function sapling() {
	const c = new Canvas("enchanted_sapling");
	for (let y = 5; y < 16; y++) c.set(7, y, COLORS.blue);
	for (const [x, y] of [[6, 6], [8, 5], [5, 8], [9, 8], [4, 11], [10, 11], [7, 3]]) {
		c.set(x, y, COLORS.mint); c.set(x + 1, y, COLORS.cyan); c.set(x, y + 1, COLORS.violet);
	}
	c.write();
}

function flower(name, petals, center) {
	const c = new Canvas(name);
	for (let y = 8; y < 16; y++) c.set(7, y, COLORS.moss);
	for (const [x, y] of [[7, 5], [5, 7], [9, 7], [7, 9], [6, 6], [8, 6], [6, 8], [8, 8]]) c.set(x, y, petals);
	c.set(7, 7, center); c.set(7, 6, COLORS.pale); c.write();
}

function moss() {
	const c = new Canvas("crystal_moss");
	for (let y = 9; y < 16; y++) for (let x = 0; x < 16; x++) {
		if (c.random() < 0.72 || y > 13) c.set(x, y, c.random() < 0.75 ? COLORS.moss : COLORS.cyan);
	}
	for (const [x, y] of [[2, 9], [6, 7], [10, 8], [13, 6]]) {
		c.set(x, y, COLORS.pale); c.set(x, y + 1, COLORS.violet);
	}
	c.write();
}

function textures() {
	bark("enchanted_log", COLORS.blue, COLORS.cyan);
	bark("enchanted_heartwood", COLORS.indigo, COLORS.violet);
	rings("enchanted_log_top", COLORS.blue);
	rings("enchanted_heartwood_top", COLORS.violet);
	leaves(); planks(); sapling(); moss();
	flower("starflower", COLORS.cyan, COLORS.gold);
	flower("fairy_bloom", COLORS.violet, COLORS.pale);
}

const BLOCKS = {
	enchanted_heartwood: { name: "Enchanted Heartwood", kind: "pillar", side: "enchanted_heartwood", top: "enchanted_heartwood_top" },
	enchanted_log: { name: "Enchanted Log", kind: "pillar", side: "enchanted_log", top: "enchanted_log_top" },
	enchanted_planks: { name: "Enchanted Planks", kind: "cube" },
	enchanted_leaves: { name: "Enchanted Leaves", kind: "leaves" },
	enchanted_sapling: { name: "Enchanted Sapling", kind: "cross" },
	starflower: { name: "Starflower", kind: "cross" },
	fairy_bloom: { name: "Fairy Bloom", kind: "cross" },
	crystal_moss: { name: "Crystal Moss", kind: "cross" },
};

function assets() {
	const lang = { "itemGroup.enchanted_forest.main": "Enchanted Forest", "biome.enchanted_forest.enchanted_forest": "Enchanted Forest" };
	for (const [id, block] of Object.entries(BLOCKS)) {
		if (block.kind === "pillar") {
			write(A("blockstates", `${id}.json`), { variants: {
				"axis=y": { model: `${NS}:block/${id}` },
				"axis=x": { model: `${NS}:block/${id}`, x: 90, y: 90 },
				"axis=z": { model: `${NS}:block/${id}`, x: 90 },
			} });
			write(A("models", "block", `${id}.json`), { parent: "minecraft:block/cube_column", textures: {
				end: `${NS}:block/${block.top}`, side: `${NS}:block/${block.side}`,
			} });
			write(A("items", `${id}.json`), { model: { type: "minecraft:model", model: `${NS}:block/${id}` } });
		} else if (block.kind === "cube" || block.kind === "leaves") {
			write(A("blockstates", `${id}.json`), { variants: { "": { model: `${NS}:block/${id}` } } });
			write(A("models", "block", `${id}.json`), { parent: block.kind === "leaves" ? "minecraft:block/leaves" : "minecraft:block/cube_all", textures: { all: `${NS}:block/${id}` } });
			write(A("items", `${id}.json`), { model: { type: "minecraft:model", model: `${NS}:block/${id}` } });
		} else {
			write(A("blockstates", `${id}.json`), { variants: { "": { model: `${NS}:block/${id}` } } });
			write(A("models", "block", `${id}.json`), { parent: "minecraft:block/cross", textures: { cross: `${NS}:block/${id}` } });
			write(A("models", "item", `${id}.json`), { parent: "minecraft:item/generated", textures: { layer0: `${NS}:block/${id}` } });
			write(A("items", `${id}.json`), { model: { type: "minecraft:model", model: `${NS}:item/${id}` } });
		}
		write(D("loot_table", "blocks", `${id}.json`), { type: "minecraft:block", random_sequence: `${NS}:blocks/${id}`, pools: [{
			rolls: 1, bonus_rolls: 0, conditions: [{ condition: "minecraft:survives_explosion" }],
			entries: [{ type: "minecraft:item", name: `${NS}:${id}` }],
		}] });
		lang[`block.${NS}.${id}`] = block.name;
	}
	write(A("lang", "en_us.json"), Object.fromEntries(Object.entries(lang).sort()));
	write(D("recipe", "enchanted_planks.json"), {
		type: "minecraft:crafting_shapeless", category: "building", group: "planks",
		ingredients: [`#${NS}:enchanted_logs`], result: { count: 4, id: `${NS}:enchanted_planks` },
	});
}

const patchFeature = block => ({ type: "minecraft:random_patch", config: {
	feature: { feature: { type: "minecraft:simple_block", config: {
		to_place: { type: "minecraft:simple_state_provider", state: { Name: `${NS}:${block}` } },
	} }, placement: [
		{ type: "minecraft:block_predicate_filter", predicate: { type: "minecraft:all_of", predicates: [
			{ type: "minecraft:replaceable" }, { type: "minecraft:would_survive", state: { Name: `${NS}:${block}` } },
		] } },
	] }, tries: 64, xz_spread: 5, y_spread: 2,
} });

function worldgen() {
	write(D("worldgen", "configured_feature", "enchanted_tree.json"), { type: `${NS}:enchanted_tree`, config: {} });
	write(D("worldgen", "placed_feature", "enchanted_trees.json"), { feature: `${NS}:enchanted_tree`, placement: [
		{ type: "minecraft:count", count: 12 }, { type: "minecraft:in_square" },
		{ type: "minecraft:surface_water_depth_filter", max_water_depth: 0 },
		{ type: "minecraft:heightmap", heightmap: "OCEAN_FLOOR" }, { type: "minecraft:biome" },
	] });
	for (const [id, count] of [["starflower", 4], ["fairy_bloom", 2], ["crystal_moss", 5]]) {
		write(D("worldgen", "configured_feature", `patch_${id}.json`), patchFeature(id));
		write(D("worldgen", "placed_feature", `patch_${id}.json`), { feature: `${NS}:patch_${id}`, placement: [
			{ type: "minecraft:count", count }, { type: "minecraft:in_square" },
			{ type: "minecraft:heightmap", heightmap: "WORLD_SURFACE_WG" }, { type: "minecraft:biome" },
		] });
	}

	write(D("worldgen", "biome", "enchanted_forest.json"), {
		attributes: {
			"minecraft:audio/ambient_sounds": { mood: { sound: "minecraft:ambient.cave", tick_delay: 6000, block_search_extent: 8, offset: 2 } },
			"minecraft:audio/background_music": { default: { sound: "minecraft:music.overworld.lush_caves", min_delay: 12000, max_delay: 24000 } },
			"minecraft:visual/ambient_particles": [
				{ particle: { type: "minecraft:firefly" }, probability: 0.018 },
				{ particle: { type: "minecraft:glow" }, probability: 0.004 },
			],
			"minecraft:visual/fog_color": "#8fcfff", "minecraft:visual/sky_color": "#8b7dff",
			"minecraft:visual/water_fog_color": "#291a63", "minecraft:visual/block_light_tint": "#d4b8ff",
		},
		carvers: ["minecraft:cave", "minecraft:cave_extra_underground", "minecraft:canyon"],
		downfall: 0.8,
		effects: { water_color: "#6557e8", foliage_color: "#51d9b4", grass_color: "#65d79f", dry_foliage_color: "#a26ee8" },
		features: [[], ["minecraft:lake_lava_underground", "minecraft:lake_lava_surface"], ["minecraft:amethyst_geode"],
			["minecraft:monster_room", "minecraft:monster_room_deep"], [], [],
			["minecraft:ore_dirt", "minecraft:ore_gravel", "minecraft:ore_granite_upper", "minecraft:ore_granite_lower", "minecraft:ore_diorite_upper", "minecraft:ore_diorite_lower", "minecraft:ore_andesite_upper", "minecraft:ore_andesite_lower", "minecraft:ore_tuff", "minecraft:ore_coal_upper", "minecraft:ore_coal_lower", "minecraft:ore_iron_upper", "minecraft:ore_iron_middle", "minecraft:ore_iron_small", "minecraft:ore_gold", "minecraft:ore_gold_lower", "minecraft:ore_redstone", "minecraft:ore_redstone_lower", "minecraft:ore_diamond", "minecraft:ore_diamond_medium", "minecraft:ore_diamond_large", "minecraft:ore_diamond_buried", "minecraft:ore_lapis", "minecraft:ore_lapis_buried", "minecraft:ore_copper", "minecraft:underwater_magma", "minecraft:disk_sand", "minecraft:disk_clay", "minecraft:disk_gravel"],
			[], ["minecraft:spring_water", "minecraft:spring_lava"],
			["minecraft:glow_lichen", `${NS}:enchanted_trees`, `${NS}:patch_starflower`, `${NS}:patch_fairy_bloom`, `${NS}:patch_crystal_moss`, "minecraft:patch_firefly_bush_swamp", "minecraft:patch_firefly_bush_near_water", "minecraft:forest_flowers", "minecraft:patch_grass_forest", "minecraft:brown_mushroom_normal", "minecraft:red_mushroom_normal"],
			["minecraft:freeze_top_layer"]],
		has_precipitation: true, spawn_costs: {}, temperature: 0.7,
		spawners: {
			ambient: [{ type: "minecraft:bat", weight: 10, minCount: 8, maxCount: 8 }],
			axolotls: [{ type: "minecraft:axolotl", weight: 10, minCount: 4, maxCount: 6 }],
			creature: [
				{ type: "minecraft:rabbit", weight: 12, minCount: 2, maxCount: 4 },
				{ type: "minecraft:fox", weight: 8, minCount: 2, maxCount: 3 },
				{ type: "minecraft:sheep", weight: 8, minCount: 3, maxCount: 4 },
			],
			misc: [],
			monster: [
				{ type: "minecraft:spider", weight: 80, minCount: 4, maxCount: 4 },
				{ type: "minecraft:zombie", weight: 75, minCount: 4, maxCount: 4 },
				{ type: "minecraft:skeleton", weight: 80, minCount: 4, maxCount: 4 },
				{ type: "minecraft:creeper", weight: 65, minCount: 4, maxCount: 4 },
				{ type: "minecraft:enderman", weight: 10, minCount: 1, maxCount: 2 },
			],
			underground_water_creature: [{ type: "minecraft:glow_squid", weight: 10, minCount: 4, maxCount: 6 }],
			water_ambient: [{ type: "minecraft:tropical_fish", weight: 25, minCount: 8, maxCount: 8 }], water_creature: [],
		},
	});
}

function tags() {
	const writeTag = (namespace, pathName, values) => write(path.join(ROOT, "data", namespace, "tags", ...pathName.split("/")) + ".json", { replace: false, values });
	const logs = [`${NS}:enchanted_heartwood`, `${NS}:enchanted_log`];
	writeTag("minecraft", "block/logs", logs); writeTag("minecraft", "block/logs_that_burn", logs);
	writeTag("minecraft", "item/logs", logs); writeTag("minecraft", "item/logs_that_burn", logs);
	writeTag("minecraft", "block/planks", [`${NS}:enchanted_planks`]); writeTag("minecraft", "item/planks", [`${NS}:enchanted_planks`]);
	writeTag("minecraft", "block/leaves", [`${NS}:enchanted_leaves`]); writeTag("minecraft", "item/leaves", [`${NS}:enchanted_leaves`]);
	writeTag("minecraft", "block/saplings", [`${NS}:enchanted_sapling`]); writeTag("minecraft", "item/saplings", [`${NS}:enchanted_sapling`]);
	writeTag("minecraft", "block/mineable/axe", [...logs, `${NS}:enchanted_planks`]);
	writeTag("minecraft", "block/mineable/hoe", [`${NS}:enchanted_leaves`]);
	writeTag("minecraft", "block/replaceable_by_trees", [`${NS}:starflower`, `${NS}:fairy_bloom`, `${NS}:crystal_moss`, `${NS}:enchanted_sapling`]);
	writeTag("minecraft", "worldgen/biome/is_forest", [`${NS}:enchanted_forest`]);
	writeTag("minecraft", "worldgen/biome/is_overworld", [`${NS}:enchanted_forest`]);
	writeTag("c", "worldgen/biome/is_forest", [`${NS}:enchanted_forest`]);
	writeTag(NS, "block/enchanted_logs", logs); writeTag(NS, "item/enchanted_logs", logs);
}

textures(); assets(); worldgen(); tags();
console.log(`generated ${Object.keys(BLOCKS).length} Enchanted Forest blocks and biome resources`);
