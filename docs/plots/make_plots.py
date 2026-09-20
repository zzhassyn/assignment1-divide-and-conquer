"""Regenerate the report plots from results/results.csv (requires Pillow)."""

import csv
import math
from pathlib import Path

from PIL import Image, ImageDraw, ImageFont


ROOT = Path(__file__).resolve().parents[2]
OUT = Path(__file__).resolve().parent
with (ROOT / "results" / "results.csv").open(newline="", encoding="utf-8") as stream:
    ROWS = list(csv.DictReader(stream))

COLORS = {
    "MergeSort": "#2563eb",
    "QuickSort": "#e11d48",
    "DeterministicSelect": "#16a34a",
    "ClosestPair": "#9333ea",
    "RANDOM": "#2563eb",
    "SORTED": "#16a34a",
    "REVERSE_SORTED": "#e11d48",
    "DUPLICATE_HEAVY": "#9333ea",
}


def font(size):
    for path in (Path("C:/Windows/Fonts/arial.ttf"), Path("/usr/share/fonts/truetype/dejavu/DejaVuSans.ttf")):
        if path.exists():
            return ImageFont.truetype(str(path), size)
    return ImageFont.load_default()


def plot(filename, title, y_label, groups, field, log_y=False):
    image = Image.new("RGB", (1200, 720), "white")
    draw = ImageDraw.Draw(image)
    left, top, right, bottom = 105, 110, 1130, 600
    all_rows = [r for group in groups.values() for r in group]
    min_n = min(int(r["n"]) for r in all_rows)
    max_n = max(int(r["n"]) for r in all_rows)
    max_y = max(float(r[field]) for r in all_rows) * 1.08
    min_y = min(float(r[field]) for r in all_rows)
    if log_y:
        min_y = 10 ** math.floor(math.log10(min_y))
        max_y = 10 ** math.ceil(math.log10(max_y))
    x_ticks = sorted({int(r["n"]) for r in all_rows})

    def x_pos(n):
        return left + (math.log(n) - math.log(min_n)) / (math.log(max_n) - math.log(min_n)) * (right - left)

    def y_pos(value):
        if log_y:
            return bottom - (math.log10(value) - math.log10(min_y)) / (math.log10(max_y) - math.log10(min_y)) * (bottom - top)
        return bottom - value / max_y * (bottom - top)

    draw.text((left, 35), title, fill="#111827", font=font(28))
    scales = "both axes on log scales" if log_y else "input size on log scale"
    draw.text((left, 75), f"{y_label}  |  {scales}", fill="#475569", font=font(17))
    ticks = ([10 ** power for power in range(int(math.log10(min_y)), int(math.log10(max_y)) + 1)]
             if log_y else [max_y * i / 5 for i in range(6)])
    for value in ticks:
        y = y_pos(value)
        draw.line((left, y, right, y), fill="#e2e8f0", width=1)
        draw.text((20, y - 10), f"{value:,.3g}" if log_y else f"{value:,.1f}", fill="#475569", font=font(14))
    for n in x_ticks:
        x = x_pos(n)
        draw.line((x, bottom, x, bottom + 7), fill="#64748b", width=2)
        draw.text((x - 22, bottom + 15), f"{n:,}", fill="#475569", font=font(13))
    draw.line((left, top, left, bottom, right, bottom), fill="#334155", width=2)

    for index, (name, rows) in enumerate(groups.items()):
        color = COLORS[name]
        points = [(x_pos(int(r["n"])), y_pos(float(r[field]))) for r in sorted(rows, key=lambda r: int(r["n"]))]
        if len(points) > 1:
            draw.line(points, fill=color, width=4)
        for x, y in points:
            draw.ellipse((x - 5, y - 5, x + 5, y + 5), fill=color)
        legend_x = left + (index % 2) * 390
        legend_y = 645 + (index // 2) * 26
        draw.line((legend_x, legend_y + 7, legend_x + 28, legend_y + 7), fill=color, width=4)
        draw.text((legend_x + 38, legend_y), name.replace("_", " "), fill="#334155", font=font(16))
    image.save(OUT / filename)


random_rows = [r for r in ROWS if r["input_type"] == "RANDOM"]
algorithms = {name: [r for r in random_rows if r["algorithm"] == name] for name in COLORS if name in {r["algorithm"] for r in random_rows}}
plot("time_vs_n.png", "Execution time vs input size", "Time (ms)", algorithms, "time_ms", log_y=True)
plot("depth_vs_n.png", "Maximum recursion depth vs input size", "Depth", algorithms, "max_depth")
plot("comparisons_vs_n.png", "Comparisons vs input size", "Comparisons", algorithms, "comparisons")
quick_rows = [r for r in ROWS if r["algorithm"] == "QuickSort"]
input_types = {name: [r for r in quick_rows if r["input_type"] == name] for name in ("RANDOM", "SORTED", "REVERSE_SORTED", "DUPLICATE_HEAVY")}
plot("quicksort_time_by_input_type.png", "QuickSort time by input type", "Time (ms)", input_types, "time_ms", log_y=True)
