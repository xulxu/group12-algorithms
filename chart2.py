import pandas as pd
import matplotlib.pyplot as plt
import numpy as np
from scipy.optimize import curve_fit

df = pd.read_csv(r'py\bubble.csv')
algorithms = df['Algorithm'].unique()
cases = df['Case'].unique()
colors = ['#c78c40', '#406fc7', '#c74040', '#40c742', '#8c40c7']
plt.figure(figsize=(12, 7))
color_idx = 0

for algo in algorithms:
    for case in cases:
        sub = df[(df['Algorithm'] == algo) & (df['Case'] == case)]
        if not sub.empty:
            sizes = sub['Size'].astype(int)
            times = sub['Time (s)'].astype(float)
            label = f"{algo} - {case}"
            plt.plot(sizes, times, label=label, marker='o', color=colors[color_idx % len(colors)], linewidth=2)
            color_idx += 1

def nlogn_func(n, a, b):
    return a * n * np.log2(n) + b
# a * n + b                 # Linear
# a * np.log2(n) + b        # Logarithmic
# a * n * np.log2(n) + b    # Superlinear
# a * n**2 + b * n + c      # Quadratic

df_best = df[df['Case'] == 'Best']
if not df_best.empty:
    sizes_best = df_best['Size'].astype(float)
    times_best = df_best['Time (s)'].astype(float)
    popt, _ = curve_fit(nlogn_func, sizes_best, times_best, maxfev=5000)
    x_fit = np.linspace(max(1, sizes_best.min()), 10000, 200)
    y_fit = nlogn_func(x_fit, *popt)
    # plt.plot(x_fit, y_fit, color='black', linestyle='--', linewidth=2, label='Best Case Superlinear Fit')

plt.xlabel("Input Size (n)")
plt.ylabel("Time (s)")
plt.grid(True)
plt.legend(fontsize=10)
plt.title(f"Time (s) vs Input Size for {algo} Sort")
plt.tight_layout()
plt.show()