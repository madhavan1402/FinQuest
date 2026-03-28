"""
app.py
------
FinQuest AI Engine — Flask microservice running on port 5000.
Exposes three prediction endpoints consumed by the Spring Boot backend.

Start with:
    python app.py

Endpoints:
    POST /predict-literacy   → Beginner / Intermediate / Advanced
    POST /risk-profile       → Conservative / Moderate / Aggressive
    POST /recommend          → next recommended topic/level
"""

import pickle
import os
from flask import Flask, request, jsonify

app = Flask(__name__)


# ── Load pre-trained models at startup ───────────────────────────────────────
# Models are loaded once into memory so every request is fast (no re-loading).
def load_model(filename):
    path = os.path.join("models", filename)
    if not os.path.exists(path):
        raise FileNotFoundError(
            f"{path} not found. Run 'python train_models.py' first."
        )
    with open(path, "rb") as f:
        return pickle.load(f)

literacy_model  = load_model("literacy_model.pkl")
risk_model      = load_model("risk_model.pkl")
recommend_model = load_model("recommend_model.pkl")


# ── Helper ────────────────────────────────────────────────────────────────────
def require_fields(data, *fields):
    """Returns an error response tuple if any required field is missing."""
    missing = [f for f in fields if f not in data]
    if missing:
        return jsonify({"error": f"Missing fields: {missing}"}), 400
    return None


# ── POST /predict-literacy ────────────────────────────────────────────────────
# Input : { "quiz_score": 75, "simulation_score": 60 }
# Output: { "literacy_level": "Intermediate" }
#
# The RandomForest takes both scores, runs them through 50 decision trees,
# and returns the majority-voted label.
@app.route("/predict-literacy", methods=["POST"])
def predict_literacy():
    data = request.get_json()
    err  = require_fields(data, "quiz_score", "simulation_score")
    if err:
        return err

    quiz_score       = float(data["quiz_score"])
    simulation_score = float(data["simulation_score"])

    # model.predict() expects a 2D array: [[feature1, feature2]]
    prediction = literacy_model.predict([[quiz_score, simulation_score]])[0]

    return jsonify({"literacy_level": prediction})


# ── POST /risk-profile ────────────────────────────────────────────────────────
# Input : { "investment_choice": 1, "loss_tolerance": 2 }
#           investment_choice: 0=safe, 1=mixed, 2=aggressive
#           loss_tolerance:    0=low,  1=medium, 2=high
# Output: { "risk_profile": "Moderate" }
#
# DecisionTree follows learned if/else rules on the two categorical inputs.
@app.route("/risk-profile", methods=["POST"])
def risk_profile():
    data = request.get_json()
    err  = require_fields(data, "investment_choice", "loss_tolerance")
    if err:
        return err

    investment_choice = int(data["investment_choice"])
    loss_tolerance    = int(data["loss_tolerance"])

    prediction = risk_model.predict([[investment_choice, loss_tolerance]])[0]

    return jsonify({"risk_profile": prediction})


# ── POST /recommend ───────────────────────────────────────────────────────────
# Input : { "quiz_score": 40, "simulation_score": 80 }
# Output: { "recommendation": "Revise Quiz: Budgeting 101" }
#
# The model identifies which area is weaker and returns the matching topic.
# If both scores are high it recommends advancing to the next level.
@app.route("/recommend", methods=["POST"])
def recommend():
    data = request.get_json()
    err  = require_fields(data, "quiz_score", "simulation_score")
    if err:
        return err

    quiz_score       = float(data["quiz_score"])
    simulation_score = float(data["simulation_score"])

    prediction = recommend_model.predict([[quiz_score, simulation_score]])[0]

    return jsonify({"recommendation": prediction})


# ── Health check ──────────────────────────────────────────────────────────────
@app.route("/health", methods=["GET"])
def health():
    return jsonify({"status": "AI Engine running"})


if __name__ == "__main__":
    # debug=False in production; port 5000 is the Flask default
    app.run(host="0.0.0.0", port=5000, debug=True)
