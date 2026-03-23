# 🎮 1v1 Math Tug of War Game - App Specification

## 📱 App Overview
A simple 1v1 competitive mobile game where two players solve math problems to pull a rope in a tug-of-war. The faster player gains advantage and wins.

---

## 🚀 App Flow

### 1. Launch Screen
- Display:
  - Title: **Math Tug of War**
  - Button: **Start 1v1**

---

### 2. Mode Selection
- Only one option:
  - **1 vs 1**
- On click → move to countdown

---

### 3. Countdown Screen
- Show:
  - `3... 2... 1... GO!`
- Duration: 3 seconds
- Then navigate to game screen

---

## 🎯 Game Screen Layout

### 🔲 Screen Split (Vertical)
| Player 1 Area |
| (Top Section) |
| Tug of War Area |
| (Middle Section) |
| Player 2 Area |
| (Bottom Section) |

---

## 👥 Player Sections (Top & Bottom)

### Each Player Gets:
- A math question
- Number input keypad
- Submit button

### Example:

12 + 8 = ?

[1][2][3]
[4][5][6]
[7][8][9]
[0][←][OK]


---

## 🧠 Question Generation Logic

### Rules:
- Both players receive **same type of operation**
  - Example:
    - Player 1: `12 + 8`
    - Player 2: `7 + 9`
- Operations:
  - Addition (+)
  - Subtraction (-)
  - Multiplication (×)
  - Division (÷)

### Conditions:
- Same operation type for both players
- Different numbers
- Division should always result in whole numbers

---

## ⚔️ Tug of War Mechanics (Middle Section)

### Visual Elements:
- Rope at center
- 4 characters:
  - 2 for Player 1 (Top team)
  - 2 for Player 2 (Bottom team)
- Different colors for each team

---

### 🎮 Game Logic

- Each correct answer:
  → Pull rope **1 step** toward that player

- Wrong answer:
  → No movement

- Faster correct answer wins the round movement

---

### 🏆 Winning Condition

- Define a limit (e.g., 5 steps)
- If rope reaches one side → Player wins

---

## 🔁 Game End Screen

### Show:
- Winner (Player 1 / Player 2)
- Button: **Play Again**

### On Click:
- Restart game from countdown

---

## ⚙️ Core Logic Pseudocode

```pseudo
START GAME

WHILE no winner:
    operation = random(+, -, ×, ÷)

    player1_question = generate(operation)
    player2_question = generate(operation)

    WAIT for answers

    IF player1 correct AND faster:
        move rope up

    ELSE IF player2 correct AND faster:
        move rope down

    CHECK win condition

END WHILE

SHOW winner screen
🧩 Tech Suggestions (Optional)
For No-Code / Low-Code:
FlutterFlow
Thunkable
For Coding:
Flutter (Best for performance)
React Native (JS-based)
Unity (if adding animations later)
🎨 UI Ideas
Bright arcade style colors
Smooth rope animation
Sound effects:
Countdown beep
Correct answer ding
Rope pull sound
💡 Future Improvements
Add timer per question
Add difficulty levels
Add online multiplayer
Add score tracking