import pandas as pd
import numpy as np
from sklearn.metrics import r2_score

files = {
    "Bubble": r"py\bubble sort.csv",
    "Merge": r"py\merge all case.csv",
    "Quick": r"py\quick sort.csv",
    "Counting": r"py\counting sort.csv"
}

def n1(n): return n
def nlogn(n): return n * np.log2(n)
def n2(n): return n ** 2

case_models = {
    "Bubble": {
        "Best": n1,             # O(n)
        "Worst": n2,            # O(n^2)
        "Random": n2            # O(n^2)
    },
    "Merge": {
        "Best": nlogn,          # O(n log n)
        "Worst": nlogn,         # O(n log n)
        "Random": nlogn         # O(n log n)
    },
    "Quick": {
        "Best": nlogn,          # O(n log n)
        "Worst": n2,            # O(n^2)
        "Random": nlogn         # O(n log n)
    },
    "Counting": {
        "Best": n1,             # O(n)
        "Worst": n1,            # O(n)
        "Random": n1            # O(n)
    }
}

def compute_r2_cases_case_specific(filename, case_to_model):
    df = pd.read_csv(filename)
    size_col = [col for col in df.columns if 'Size' in col][0]
    time_col = [col for col in df.columns if 'Time' in col][0]
    case_col = [col for col in df.columns if 'Case' in col][0]
    results = []
    for case in df[case_col].unique():
        dfx = df[df[case_col] == case]
        n = dfx[size_col].astype(float).values
        times = dfx[time_col].astype(float).values
        x = case_to_model.get(case, n1)(n)
        if len(times) < 2:
            continue
        a, b = np.polyfit(x, times, 1)
        pred = a * x + b
        r2 = r2_score(times, pred)
        results.append((case, r2))
    return results

print(f"{'Algorithm':<10} {'Case':<8} {'Complexity':<15} {'R^2':<7}")
print("-" * 45)
for alg in files:
    for case, r2 in compute_r2_cases_case_specific(files[alg], case_models[alg]):
        complexity_name = {
            n1: 'O(n)',
            n2: 'O(n^2)',
            nlogn: 'O(n log n)'
        }[case_models[alg][case]]
        print(f"{alg:<10} {case:<8} {complexity_name:<15} {r2:.4f}")
