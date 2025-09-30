import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
from sklearn.metrics import r2_score

algorithm = 'counting'  # Cases: 'bubble', 'merge', 'quick', 'counting'

energy_log_paths = {
    "bubble":   r"py\bubble all cases.csv",
    "merge":    r"py\merge sort all cases.CSV",
    "quick":    r"py\quick sort all case.CSV",
    "counting": r"py\counting sort all case.CSV"
}
benchmark_paths = {
    "bubble":   r"py\bubble sort.csv",
    "merge":    r"py\merge all case.csv",
    "quick":    r"py\quick sort.csv",
    "counting": r"py\counting sort.csv"
}

intervals = {
    'bubble': {
        'Best':   ('20:14:07', '20:14:41'),
        'Worst':  ('19:58:05', '19:59:13'),
        'Random': ('20:11:52', '20:12:59'),
    },
    'merge': {
        'Best':   ('20:39:38', '20:40:34'),
        'Worst':  ('20:40:37', '20:41:32'),
        'Random': ('20:41:35', '20:42:28'),
    },
    'quick': {
        'Best':   ('21:42:24', '21:43:19'),
        'Worst':  ('21:43:22', '21:44:45'),
        'Random': ('21:44:48', '21:46:02'),
    },
    'counting': {
        'Best':   ('22:01:39', '22:02:20'),
        'Worst':  ('22:02:23', '22:03:04'),
        'Random': ('22:03:07', '22:04:32'),
    }
}

complexity_map = {
    'bubble': {
        'Best':   lambda n: n,
        'Worst':  lambda n: n**2,
        'Random': lambda n: n**2,
    },
    'merge': {
        'Best':   lambda n: n * np.log2(n),
        'Worst':  lambda n: n * np.log2(n),
        'Random': lambda n: n * np.log2(n),
    },
    'quick': {
        'Best':   lambda n: n * np.log2(n),
        'Worst':  lambda n: n**2,
        'Random': lambda n: n * np.log2(n),
    },
    'counting': {
        'Best':   lambda n: n,
        'Worst':  lambda n: n,
        'Random': lambda n: n,
    }
}

complexity_label = {
    'bubble': {
        'Best':   'n (Input Size)',
        'Worst':  'n² (Input Size Squared)',
        'Random': 'n² (Input Size Squared)',
    },
    'merge': {
        'Best':   'n log n (Input Size × log n)',
        'Worst':  'n log n (Input Size × log n)',
        'Random': 'n log n (Input Size × log n)',
    },
    'quick': {
        'Best':   'n log n (Input Size × log n)',
        'Worst':  'n² (Input Size Squared)',
        'Random': 'n log n (Input Size × log n)',
    },
    'counting': {
        'Best':   'n (Input Size)',
        'Worst':  'n (Input Size)',
        'Random': 'n (Input Size)',
    }
}

colors = {'Best': '#c78c40', 'Worst': '#406fc7', 'Random': '#c74040'}

benchmark = pd.read_csv(benchmark_paths[algorithm])
energy_log = pd.read_csv(energy_log_paths[algorithm], encoding='latin1')

fig, axs = plt.subplots(1, 3, figsize=(18, 5))
case_order = ['Best', 'Worst', 'Random']

n_max = {case: benchmark[benchmark['Case'].str.lower() == case.lower()]['Size'].max() for case in case_order}

for ax_i, case in enumerate(case_order):
    start, end = intervals[algorithm][case]
    energy_log['Time_dt'] = pd.to_datetime(energy_log['Time'], format='%H:%M:%S.%f', errors='coerce').dt.time
    start_time = pd.to_datetime(start, format='%H:%M:%S').time()
    end_time = pd.to_datetime(end, format='%H:%M:%S').time()
    interval_energy = energy_log[(energy_log['Time_dt'] >= start_time) & (energy_log['Time_dt'] <= end_time)].copy()
    interval_energy['Power_W'] = pd.to_numeric(interval_energy['CPU Core Power (SVI3 TFN) [W]'], errors='coerce')
    dt = 0.1
    interval_energy['Energy_J'] = interval_energy['Power_W'] * dt
    interval_energy['Cumulative_Energy_J'] = interval_energy['Energy_J'].cumsum()
    N = len(interval_energy)
    if np.isnan(n_max[case]) or n_max[case] is None:
        continue
    x_complex = np.linspace(0, complexity_map[algorithm][case](n_max[case]), N)
    y_energy = interval_energy['Cumulative_Energy_J'].values
    axs[ax_i].plot(x_complex, y_energy, color=colors[case])
    fit = np.polyfit(x_complex, y_energy, deg=1)
    y_pred = np.polyval(fit, x_complex)
    r2 = r2_score(y_energy, y_pred)
    axs[ax_i].plot(x_complex, y_pred, linestyle='--', color='gray', alpha=0.5)
    axs[ax_i].text(0.05, 0.95, f'$R^2 = {r2:.4f}$', transform=axs[ax_i].transAxes,
                   fontsize=12, verticalalignment='top', bbox=dict(boxstyle="round", alpha=0.2))
    axs[ax_i].set_xlabel(complexity_label[algorithm][case])
    axs[ax_i].set_ylabel('Cumulative Energy (J)')
    axs[ax_i].set_title(f'{algorithm.capitalize()} Sort - {case} Case')
    axs[ax_i].grid(True, which='both', linestyle='--', linewidth=0.5, alpha=0.7)

plt.suptitle(f'{algorithm.capitalize()} Sort: Cumulative Energy vs Theoretical Complexity')
plt.tight_layout(rect=[0, 0, 1, 0.96])
plt.show()
