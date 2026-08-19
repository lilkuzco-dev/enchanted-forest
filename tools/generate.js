#!/usr/bin/env node
const fs = require("node:fs");
const path = require("node:path");
const png = require("./png.js");

const NS = "enchanted_forest";
const ROOT = path.join(__dirname, "..", "src", "main", "resources");
const VANILLA = path.join(__dirname, "vanilla");
const A = (...parts) => path.join(ROOT, "assets", NS, ...parts);
const D = (...parts) => path.join(ROOT, "data", NS, ...parts);
const write = (file, value) => {
	fs.mkdirSync(path.dirname(file), { recursive: true });
	fs.writeFileSync(file, JSON.stringify(value, null, 2) + "\n");
};

function rgbToHsl(r, g, b) {
	r /= 255; g /= 255; b /= 255;
	const max = Math.max(r, g, b), min = Math.min(r, g, b);
	let h = 0, s = 0;
	const l = (max + min) / 2;
	if (max !== min) {
		const d = max - min;
		s = l > 0.5 ? d / (2 - max - min) : d / (max + min);
		if (max === r) h = (g - b) / d + (g < b ? 6 : 0);
		else if (max === g) h = (b - r) / d + 2;
		else h = (r - g) / d + 4;
		h /= 6;
	}
	return [h, s, l];
}

function hslToRgb(h, s, l) {
	const hue = (p, q, t) => {
		if (t < 0) t += 1;
		if (t > 1) t -= 1;
		if (t < 1 / 6) return p + (q - p) * 6 * t;
		if (t < 1 / 2) return q;
		if (t < 2 / 3) return p + (q - p) * (2 / 3 - t) * 6;
		return p;
	};
	if (s === 0) return [l, l, l].map(v => Math.round(v * 255));
	const q = l < 0.5 ? l * (1 + s) : l + s - l * s;
	const p = 2 * l - q;
	return [hue(p, q, h + 1 / 3), hue(p, q, h), hue(p, q, h - 1 / 3)]
		.map(v => Math.round(v * 255));
}

function clamp(value, low = 0, high = 1) {
	return Math.max(low, Math.min(high, value));
}

/** Recolors vanilla pixels without changing their shape, transparency, or UV layout. */
function recolorTexture(source, output, transform) {
	const image = png.decode(fs.readFileSync(path.join(VANILLA, `${source}.png`)));
	for (let i = 0; i < image.px.length; i += 4) {
		if (image.px[i + 3] === 0) continue;
		const [h, s, l] = rgbToHsl(image.px[i], image.px[i + 1], image.px[i + 2]);
		const [nextH, nextS, nextL] = transform(h, s, l);
		const [r, g, b] = hslToRgb(nextH, clamp(nextS), clamp(nextL));
		image.px[i] = r; image.px[i + 1] = g; image.px[i + 2] = b;
	}
	const file = A("textures", `${output}.png`);
	fs.mkdirSync(path.dirname(file), { recursive: true });
	fs.writeFileSync(file, png.encode(image.w, image.h, image.px));
}

function recolorVanilla(source, output, transform) {
	recolorTexture(source, `block/${output}`, transform);
}

const fixedEnchantedHue = (hue, saturation, lightScale = 1, lightOffset = 0) =>
	(_h, _s, l) => [hue, saturation, clamp(l * lightScale + lightOffset, 0.05, 0.92)];

function recolorPlant(petalHue) {
	return (h, s, l) => {
		// Vanilla stems and leaves become bright turquoise while their exact pixels remain intact.
		if (h >= 0.18 && h <= 0.48 && s > 0.12) {
			return [0.46, Math.max(0.58, s), clamp(l * 1.08 + 0.015, 0.08, 0.88)];
		}
		// Preserve the recognizable warm flower centers as enchanted gold.
		if (h >= 0.08 && h < 0.18 && s > 0.3) {
			return [0.12, Math.max(0.72, s), clamp(l * 1.08, 0.12, 0.9)];
		}
		return [petalHue, Math.max(0.62, s), clamp(l * 1.04 + 0.02, 0.1, 0.92)];
	};
}

function textures() {
	// Keep Mojang's oak grain and rings exactly; only the palette changes.
	recolorVanilla("oak_log", "enchanted_log", fixedEnchantedHue(0.53, 0.68, 0.98, 0.02));
	recolorVanilla("oak_log_top", "enchanted_log_top", fixedEnchantedHue(0.51, 0.72, 1.02, 0.015));
	recolorVanilla("oak_log", "enchanted_heartwood", fixedEnchantedHue(0.76, 0.66, 0.9, -0.015));
	recolorVanilla("oak_log_top", "enchanted_heartwood_top", fixedEnchantedHue(0.76, 0.72, 0.96));
	recolorVanilla("oak_planks", "enchanted_planks", fixedEnchantedHue(0.55, 0.63, 0.98, 0.015));
	recolorVanilla("oak_leaves", "enchanted_leaves", fixedEnchantedHue(0.45, 0.7, 1.08, 0.02));
	recolorVanilla("oak_sapling", "enchanted_sapling", recolorPlant(0.76));

	// Custom luminous plants now retain familiar vanilla silhouettes.
	recolorVanilla("allium", "starflower", recolorPlant(0.51));
	recolorVanilla("oxeye_daisy", "fairy_bloom", recolorPlant(0.78));
	recolorVanilla("short_grass", "crystal_moss", fixedEnchantedHue(0.47, 0.72, 1.12, 0.015));
}

function enchantedBirdHue(h, s, l) {
	if (l < 0.15) return [0.71, 0.42, clamp(l * 1.12, 0.035, 0.22)];
	if (s < 0.16) return [0.74, 0.58, clamp(l * 1.02, 0.08, 0.9)];
	return [(h + 0.31) % 1, Math.max(0.68, s), clamp(l * 1.05 + 0.01, 0.08, 0.9)];
}

function enchantedFoxHue(_h, s, l) {
	if (l < 0.16) return [0.71, 0.5, clamp(l * 1.15, 0.035, 0.22)];
	if (s < 0.2 && l > 0.48) return [0.51, 0.55, clamp(l * 1.02, 0.48, 0.92)];
	return [0.76, Math.max(0.62, s), clamp(l * 1.02 + 0.015, 0.08, 0.88)];
}

function enchantedBearHue(_h, _s, l) {
	if (l < 0.3) return [0.71, 0.55, clamp(l * 1.05, 0.035, 0.3)];
	return [0.54, 0.52, clamp(l * 0.93 + 0.035, 0.3, 0.9)];
}

function entityTextures() {
	for (const variant of ["red_blue", "blue", "green", "yellow_blue", "grey"]) {
		recolorTexture(`entity/parrot_${variant}`, `entity/enchanted_bird_${variant}`, enchantedBirdHue);
	}
	for (const state of ["fox", "fox_baby", "fox_sleep", "fox_sleep_baby"]) {
		recolorTexture(`entity/${state}`, `entity/enchanted_${state}`, enchantedFoxHue);
	}
	recolorTexture("entity/polarbear", "entity/enchanted_bear", enchantedBearHue);
	recolorTexture("entity/polarbear_baby", "entity/enchanted_bear_baby", enchantedBearHue);
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
	const lang = {
		"itemGroup.enchanted_forest.main": "Enchanted Forest",
		"biome.enchanted_forest.enchanted_forest": "Enchanted Forest",
		"entity.enchanted_forest.enchanted_bird": "Enchanted Bird",
		"entity.enchanted_forest.enchanted_fox": "Enchanted Fox",
		"entity.enchanted_forest.enchanted_bear": "Enchanted Bear",
	};
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

function entityLoot() {
	write(D("loot_table", "entities", "enchanted_bird.json"), {
		type: "minecraft:entity", random_sequence: `${NS}:entities/enchanted_bird`, pools: [{
			rolls: 1, entries: [{ type: "minecraft:item", name: "minecraft:feather", functions: [{
				function: "minecraft:set_count", count: { type: "minecraft:uniform", min: 1, max: 2 },
			}] }],
		}],
	});
	write(D("loot_table", "entities", "enchanted_fox.json"), {
		type: "minecraft:entity", random_sequence: `${NS}:entities/enchanted_fox`,
	});
	write(D("loot_table", "entities", "enchanted_bear.json"), {
		type: "minecraft:entity", random_sequence: `${NS}:entities/enchanted_bear`, pools: [{
			rolls: 1, entries: [
				{ type: "minecraft:item", name: "minecraft:cod", weight: 3, functions: [{
					function: "minecraft:set_count", count: { type: "minecraft:uniform", min: 0, max: 2 },
				}] },
				{ type: "minecraft:item", name: "minecraft:salmon", functions: [{
					function: "minecraft:set_count", count: { type: "minecraft:uniform", min: 0, max: 2 },
				}] },
			],
		}],
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
		{ type: "minecraft:count", count: 8 }, { type: "minecraft:in_square" },
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
				{ type: `${NS}:enchanted_bird`, weight: 24, minCount: 3, maxCount: 6 },
				{ type: `${NS}:enchanted_fox`, weight: 10, minCount: 2, maxCount: 4 },
				{ type: `${NS}:enchanted_bear`, weight: 3, minCount: 1, maxCount: 2 },
				{ type: "minecraft:rabbit", weight: 12, minCount: 2, maxCount: 4 },
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

textures(); entityTextures(); assets(); entityLoot(); worldgen(); tags();
console.log(`generated ${Object.keys(BLOCKS).length} Enchanted Forest blocks and biome resources`);
