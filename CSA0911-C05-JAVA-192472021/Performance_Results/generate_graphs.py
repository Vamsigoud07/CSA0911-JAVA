import os
import csv
import matplotlib.pyplot as plt

plt.style.use('seaborn-v0_8-whitegrid' if 'seaborn-v0_8-whitegrid' in plt.style.available else 'default')
fig_dir = r"c:\Users\vamsi\OneDrive\Desktop\C05-JAVA\Assignment-5\Performance_Results"
screenshots_dir = r"c:\Users\vamsi\OneDrive\Desktop\C05-JAVA\Assignment-5\Screenshots\Part-B"

os.makedirs(fig_dir, exist_ok=True)
os.makedirs(screenshots_dir, exist_ok=True)

# -------------------------------------------------------------
# GRAPH 1: Buffering Performance (Buffered vs Unbuffered)
# -------------------------------------------------------------
buf_file = os.path.join(fig_dir, "buffering_results.csv")
x_mb = []
unbuf_ms = []
buf_ms = []
speedups = []
with open(buf_file, "r") as f:
    reader = csv.DictReader(f)
    for row in reader:
        x_mb.append(int(row['Target_Size_MB']))
        unbuf_ms.append(float(row['Unbuffered_Time_ms']))
        buf_ms.append(float(row['Buffered_Time_ms']))
        speedups.append(float(row['Speedup_Factor']))

fig, ax1 = plt.subplots(figsize=(8, 5))
ax1.plot(x_mb, unbuf_ms, marker='o', color='#d9534f', linewidth=2.5, label='Unbuffered (byte-by-byte)')
ax1.plot(x_mb, buf_ms, marker='s', color='#0275d8', linewidth=2.5, label='Buffered (8KB buffer)')
ax1.set_xlabel('File Size (MB)', fontsize=12, fontweight='bold')
ax1.set_ylabel('Execution Time (ms - Log Scale)', fontsize=12, fontweight='bold')
ax1.set_yscale('log')
ax1.set_title('Task B1.3: Unbuffered vs Buffered I/O Read Latency\nStudent: K VENKATA VAMSI GOUD (192472021)', fontsize=13, fontweight='bold')
ax1.grid(True, which="both", ls="--", alpha=0.5)
ax1.legend(loc='upper left', frameon=True, fontsize=10)

for i, txt in enumerate(speedups):
    ax1.annotate(f"{txt:.0f}x faster", (x_mb[i], buf_ms[i]), 
                 textcoords="offset points", xytext=(0,10), ha='center', fontsize=9, fontweight='bold', color='#0275d8')

plt.tight_layout()
g1_path = os.path.join(fig_dir, "graph_buffering_performance.png")
fig.savefig(g1_path, dpi=300)
fig.savefig(os.path.join(screenshots_dir, "graph_buffering_performance.png"), dpi=300)
plt.close()
print("Graph 1 generated:", g1_path)

# -------------------------------------------------------------
# GRAPH 2: File Search Growth (O(N) Sequential Search)
# -------------------------------------------------------------
search_file = os.path.join(fig_dir, "file_search_results.csv")
records = []
early_ms = []
mid_ms = []
late_ms = []
missing_ms = []
with open(search_file, "r") as f:
    reader = csv.DictReader(f)
    for row in reader:
        records.append(int(row['Record_Count']))
        early_ms.append(float(row['Early_Search_ms']))
        mid_ms.append(float(row['Middle_Search_ms']))
        late_ms.append(float(row['Late_Search_ms']))
        missing_ms.append(float(row['Missing_Search_ms']))

fig, ax = plt.subplots(figsize=(8, 5))
ax.plot(records, early_ms, marker='^', color='#5cb85c', linewidth=2, label='Early Record (Best Case: O(1))')
ax.plot(records, mid_ms, marker='o', color='#f0ad4e', linewidth=2, label='Middle Record (Average Case: O(N/2))')
ax.plot(records, late_ms, marker='s', color='#d9534f', linewidth=2, label='Late Record (Worst Case: O(N))')
ax.plot(records, missing_ms, marker='x', color='#6f42c1', linewidth=2, linestyle='--', label='Missing Record (Exhaustive Scan: O(N))')

ax.set_xlabel('Number of Records in File', fontsize=12, fontweight='bold')
ax.set_ylabel('Lookup Latency (ms)', fontsize=12, fontweight='bold')
ax.set_title('Task B1.4: Sequential File Search Scalability\nStudent: K VENKATA VAMSI GOUD (192472021)', fontsize=13, fontweight='bold')
ax.grid(True, ls="--", alpha=0.6)
ax.legend(loc='upper left', frameon=True, fontsize=10)

plt.tight_layout()
g2_path = os.path.join(fig_dir, "graph_file_search_growth.png")
fig.savefig(g2_path, dpi=300)
fig.savefig(os.path.join(screenshots_dir, "graph_file_search_growth.png"), dpi=300)
plt.close()
print("Graph 2 generated:", g2_path)

# -------------------------------------------------------------
# GRAPH 3: JDBC Ingestion (Statement vs PreparedStatement vs Batch)
# -------------------------------------------------------------
jdbc_file = os.path.join(fig_dir, "jdbc_insert_results.csv")
j_records = []
stmt_ms = []
pstmt_ms = []
batch_ms = []
with open(jdbc_file, "r") as f:
    reader = csv.DictReader(f)
    for row in reader:
        j_records.append(int(row['Records']))
        stmt_ms.append(float(row['Statement_ms']))
        pstmt_ms.append(float(row['PreparedStatement_ms']))
        batch_ms.append(float(row['Batch_ms']))

fig, ax = plt.subplots(figsize=(8, 5))
x_indices = list(range(len(j_records)))
bar_width = 0.25

r1 = [i - bar_width for i in x_indices]
r2 = [i for i in x_indices]
r3 = [i + bar_width for i in x_indices]

ax.bar(r1, stmt_ms, width=bar_width, color='#d9534f', label='Statement (Unparameterized)')
ax.bar(r2, pstmt_ms, width=bar_width, color='#f0ad4e', label='PreparedStatement (Single Insert)')
ax.bar(r3, batch_ms, width=bar_width, color='#5cb85c', label='PreparedStatement Batch (1,000 / batch)')

ax.set_xlabel('Records Inserted', fontsize=12, fontweight='bold')
ax.set_ylabel('Insertion Latency (ms)', fontsize=12, fontweight='bold')
ax.set_title('Task B3.2: Statement vs PreparedStatement vs Batch Insertion\nStudent: K VENKATA VAMSI GOUD (192472021)', fontsize=13, fontweight='bold')
ax.set_xticks(x_indices)
ax.set_xticklabels([f"{c:,}" for c in j_records], fontsize=11)
ax.legend(loc='upper left', frameon=True, fontsize=10)
ax.grid(True, axis='y', ls="--", alpha=0.6)

plt.tight_layout()
g3_path = os.path.join(fig_dir, "graph_jdbc_insertion.png")
fig.savefig(g3_path, dpi=300)
fig.savefig(os.path.join(screenshots_dir, "graph_jdbc_insertion.png"), dpi=300)
plt.close()
print("Graph 3 generated:", g3_path)
