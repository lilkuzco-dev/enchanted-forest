#!/usr/bin/env node
// Nearest-neighbor preview of every generated wildlife UV sheet. The different source
// dimensions are fitted without resampling or smoothing so individual pixels remain visible.
const fs = require("node:fs");
const path = require("node:path");
const png = require("./png.js");

const DIR = path.join(__dirname, "..", "src/main/resources/assets/enchanted_forest/textures/entity");
const OUT = process.argv[2] || path.join(__dirname, "..", "build", "wildlife-contact-sheet.png");
const COLS = 3, CELL_W = 264, CELL_H = 136, PAD = 4;
const files = fs.readdirSync(DIR).filter(file => file.endsWith(".png")).sort();
const tiles = files.map(file => ({ name: file.replace(/\.png$/, ""), image: png.decode(fs.readFileSync(path.join(DIR, file))) }));
const rows = Math.ceil(tiles.length / COLS);
const width = COLS * CELL_W, height = rows * CELL_H;
const out = Buffer.alloc(width * height * 4);

function put(x, y, r, g, b, a = 255) {
	const i = (y * width + x) * 4;
	out[i] = r; out[i + 1] = g; out[i + 2] = b; out[i + 3] = a;
}

for (let y = 0; y < height; y++) for (let x = 0; x < width; x++) {
	const shade = ((x >> 3) + (y >> 3)) % 2 === 0 ? 150 : 110;
	put(x, y, shade, shade, shade);
}

tiles.forEach((tile, index) => {
	const { w, h, px } = tile.image;
	const scale = Math.max(1, Math.floor(Math.min((CELL_W - PAD * 2) / w, (CELL_H - PAD * 2) / h)));
	const left = (index % COLS) * CELL_W + Math.floor((CELL_W - w * scale) / 2);
	const top = Math.floor(index / COLS) * CELL_H + Math.floor((CELL_H - h * scale) / 2);
	for (let y = 0; y < h * scale; y++) for (let x = 0; x < w * scale; x++) {
		const source = (Math.floor(y / scale) * w + Math.floor(x / scale)) * 4;
		if (px[source + 3] !== 0) put(left + x, top + y, px[source], px[source + 1], px[source + 2]);
	}
});

fs.mkdirSync(path.dirname(OUT), { recursive: true });
fs.writeFileSync(OUT, png.encode(width, height, out));
console.log(`wrote ${OUT} (${width}x${height})`);
tiles.forEach((tile, index) => {
	if (index % COLS === 0) process.stdout.write(`\nrow ${Math.floor(index / COLS)}: `);
	process.stdout.write(`${tile.name}  `);
});
console.log();
