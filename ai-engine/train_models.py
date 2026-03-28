"""
train_models.py
---------------
Trains three ML models and saves them as .pkl files in the models/ directory.
Run this ONCE before starting the Flask server:
    python train_models.py

Models produced:
    models/literacy_model.pkl   – RandomForestClassifier
    models/risk_model.pkl       – DecisionTreeClassifier
    models/recommend_model.pkl  – RandomForestClassifier
"""

import os
import pickle
import pandas as pd
from sklearn.ensemble import RandomForestClassifier
from sklearn.tree import DecisionTreeClassifier

os.makedirs("models", exist_ok=True)


# ── MODEL 1: Financial Literacy Classification ────────────────────────────────
# Input  : quiz_score (0–100), simulation_score (0–100)
# Output : Beginner / Intermediate / Advanced
#
# Training data covers the full score range so the model generalises well.
# Rule of thumb encoded in the labels:
#   Both scores < 40          → Beginner
#   Both scores 40–70         → Intermediate
#   Both scores > 70          → Advanced
# ─────────────────────────────────────────────────────────────────────────────
literacy_data = pd.DataFrame({
    "quiz_score":       [10, 20, 30, 15, 25, 35, 45, 55, 60, 50, 65, 70, 75, 80, 85, 90, 95, 100],
    "simulation_score": [10, 15, 25, 20, 30, 40, 50, 55, 45, 60, 65, 70, 80, 85, 90, 88, 95, 100],
    "label":            [
        "Beginner", "Beginner", "Beginner", "Beginner", "Beginner", "Beginner",
        "Intermediate", "Intermediate", "Intermediate", "Intermediate", "Intermediate", "Intermediate",
        "Advanced", "Advanced", "Advanced", "Advanced", "Advanced", "Advanced"
    ]
})

# n_estimators=50: 50 decision trees vote on the final label (majority wins).
# random_state=42: fixes the random seed so training is reproducible.
literacy_model = RandomForestClassifier(n_estimators=50, random_state=42)
literacy_model.fit(
    literacy_data[["quiz_score", "simulation_score"]],
    literacy_data["label"]
)

with open("models/literacy_model.pkl", "wb") as f:
    pickle.dump(literacy_model, f)
print("[OK] literacy_model.pkl saved")


# ── MODEL 2: Risk Profiling ───────────────────────────────────────────────────
# Input  : investment_choice (0=safe, 1=mixed, 2=aggressive)
#          loss_tolerance    (0=low,  1=medium, 2=high)
# Output : Conservative / Moderate / Aggressive
#
# A DecisionTree is ideal here because the decision boundaries are crisp
# categorical rules, not fuzzy numeric gradients.
# ─────────────────────────────────────────────────────────────────────────────
risk_data = pd.DataFrame({
    "investment_choice": [0, 0, 0, 0, 1, 1, 1, 1, 2, 2, 2, 2],
    "loss_tolerance":    [0, 0, 1, 0, 1, 1, 2, 1, 2, 2, 2, 1],
    "label":             [
        "Conservative", "Conservative", "Conservative", "Conservative",
        "Moderate",     "Moderate",     "Moderate",     "Moderate",
        "Aggressive",   "Aggressive",   "Aggressive",   "Moderate"
    ]
})

# max_depth=3: shallow tree prevents overfitting on this small dataset.
risk_model = DecisionTreeClassifier(max_depth=3, random_state=42)
risk_model.fit(
    risk_data[["investment_choice", "loss_tolerance"]],
    risk_data["label"]
)

with open("models/risk_model.pkl", "wb") as f:
    pickle.dump(risk_model, f)
print("[OK] risk_model.pkl saved")


# ── MODEL 3: Recommendation System ───────────────────────────────────────────
# Input  : quiz_score (0–100), simulation_score (0–100)
# Output : next recommended topic/level as a string
#
# Logic: identify the weaker of the two scores and recommend the matching topic.
# A RandomForest learns this mapping from labelled examples.
# ─────────────────────────────────────────────────────────────────────────────
recommend_data = pd.DataFrame({
    "quiz_score":       [10, 20, 30, 40, 50, 60, 70, 80, 90, 100, 15, 55, 75, 95],
    "simulation_score": [80, 70, 60, 50, 10, 20, 30, 40, 95, 100, 90, 15, 35, 85],
    "label":            [
        # Low quiz, high simulation → focus on quiz/theory
        "Revise Quiz: Basics of Saving",
        "Revise Quiz: Budgeting 101",
        "Revise Quiz: Investment Fundamentals",
        "Revise Quiz: Tax Awareness",
        # Low simulation, high quiz → focus on practical simulation
        "Try Simulation: Budget Planner",
        "Try Simulation: Stock Market Basics",
        "Try Simulation: Tax Calculator",
        "Try Simulation: Advanced Portfolio",
        # Both high → advance to next level
        "Advance to Next Level",
        "Advance to Next Level",
        # Mixed cases
        "Revise Quiz: Basics of Saving",
        "Try Simulation: Budget Planner",
        "Try Simulation: Stock Market Basics",
        "Advance to Next Level"
    ]
})

recommend_model = RandomForestClassifier(n_estimators=50, random_state=42)
recommend_model.fit(
    recommend_data[["quiz_score", "simulation_score"]],
    recommend_data["label"]
)

with open("models/recommend_model.pkl", "wb") as f:
    pickle.dump(recommend_model, f)
print("[OK] recommend_model.pkl saved")

print("All models trained and saved to models/")
