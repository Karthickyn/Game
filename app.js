/**
 * Math Tug of War - Game Engine and UI Logic
 */

// Game Constants
const WINNING_STEPS = 5; // Distance to win (score goes from -5 to +5)
const OPERATION_TYPES = ['+', '-', '*', '/'];

// App State
let currentAppMode = 'LAUNCH'; // LAUNCH, MODE_SELECTION, COUNTDOWN, PLAYING, GAME_OVER
let ropePosition = 0; // 0 = Center. Negative = P1 Advantage. Positive = P2 Advantage.
let p1Input = '';
let p2Input = '';
let currentP1Answer = null;
let currentP2Answer = null;

// DOM Elements
const screens = {
  LAUNCH: document.getElementById('launch-screen'),
  MODE_SELECTION: document.getElementById('mode-selection'),
  COUNTDOWN: document.getElementById('countdown-screen'),
  PLAYING: document.getElementById('game-screen'),
  GAME_OVER: document.getElementById('game-over-screen')
};

// Interactive Elements
const startBtn = document.getElementById('start-btn');
const mode1v1Btn = document.getElementById('mode-1v1-btn');
const playAgainBtn = document.getElementById('play-again-btn');
const countdownText = document.getElementById('countdown-text');
const winnerText = document.getElementById('winner-text');

// Game UI Elements
const p1QuestionBox = document.getElementById('p1-question');
const p1Preview = document.getElementById('p1-preview');
const p1Keypad = document.getElementById('p1-keypad');

const p2QuestionBox = document.getElementById('p2-question');
const p2Preview = document.getElementById('p2-preview');
const p2Keypad = document.getElementById('p2-keypad');

const knot = document.getElementById('knot');
const teamP1 = document.getElementById('team-p1');
const teamP2 = document.getElementById('team-p2');

// Audio Context (Optional - good for future upgrades)
const audioCtx = new (window.AudioContext || window.webkitAudioContext)();
function playBeep(freq = 440, type = 'sine', duration = 0.1) {
  try {
    const osc = audioCtx.createOscillator();
    const gain = audioCtx.createGain();
    osc.type = type;
    osc.frequency.setValueAtTime(freq, audioCtx.currentTime);
    gain.gain.setValueAtTime(0.1, audioCtx.currentTime);
    gain.gain.exponentialRampToValueAtTime(0.001, audioCtx.currentTime + duration);
    osc.connect(gain);
    gain.connect(audioCtx.destination);
    osc.start();
    osc.stop(audioCtx.currentTime + duration);
  } catch (e) {
    console.warn("Audio not initialized properly", e);
  }
}

// Generate Keypads
function generateKeypad(containerId, bgPlayerClass) {
  const container = document.getElementById(containerId);
  container.innerHTML = '';
  const keys = ['1','2','3','4','5','6','7','8','9','⌫','0','OK'];
  keys.forEach(key => {
    const btn = document.createElement('button');
    btn.className = `keypad-btn ${bgPlayerClass}`;
    if (key === '⌫') btn.classList.add('btn-del');
    if (key === 'OK') btn.classList.add('btn-ok');
    btn.textContent = key;
    
    // Add event listeners (both mouse and touch for mobile support)
    btn.addEventListener('touchstart', (e) => { e.preventDefault(); handleKeyPress(containerId, key); });
    btn.addEventListener('mousedown', (e) => { handleKeyPress(containerId, key); });

    container.appendChild(btn);
  });
}

generateKeypad('p1-keypad', 'p1-btn');
generateKeypad('p2-keypad', 'p2-btn');

// State Management
function switchScreen(newState) {
  Object.values(screens).forEach(screen => {
    if (screen) screen.classList.remove('active');
  });
  if (screens[newState]) {
    screens[newState].classList.add('active');
    currentAppMode = newState;
  }
}

// Button Listeners
startBtn.addEventListener('click', () => {
  // Resume audio context on first user interaction
  if(audioCtx.state === 'suspended') audioCtx.resume();
  playBeep(600, 'square');
  switchScreen('MODE_SELECTION');
});

mode1v1Btn.addEventListener('click', () => {
  playBeep(600, 'square');
  startCountdown();
});

playAgainBtn.addEventListener('click', () => {
  playBeep(800, 'triangle');
  startCountdown();
});

function startCountdown() {
  switchScreen('COUNTDOWN');
  let count = 3;
  countdownText.textContent = count;
  playBeep(440, 'square', 0.2);
  
  const ctxInterval = setInterval(() => {
    count--;
    if (count > 0) {
      countdownText.textContent = count;
      playBeep(440, 'square', 0.2);
    } else if (count === 0) {
      countdownText.textContent = 'GO!';
      playBeep(880, 'square', 0.4);
    } else {
      clearInterval(ctxInterval);
      startGamepPlay();
    }
  }, 1000);
}

function startGamepPlay() {
  switchScreen('PLAYING');
  ropePosition = 0;
  updateRopeVisuals();
  p1Input = '';
  p2Input = '';
  p1Preview.textContent = '?';
  p1Preview.className = 'answer-preview';
  p2Preview.textContent = '?';
  p2Preview.className = 'answer-preview';
  generateQuestions();
}

function generateMathQuestion(operation, difficultyLevel = 1) {
  let num1, num2, answer;
  switch (operation) {
    case '+':
      num1 = Math.floor(Math.random() * 20) + 1;
      num2 = Math.floor(Math.random() * 20) + 1;
      answer = num1 + num2;
      return { str: `${num1} + ${num2}`, answer };
    case '-':
      // Ensure num1 >= num2 to avoid negative answers for simplicity
      num1 = Math.floor(Math.random() * 20) + 5;
      num2 = Math.floor(Math.random() * num1);
      answer = num1 - num2;
      return { str: `${num1} - ${num2}`, answer };
    case '*':
      num1 = Math.floor(Math.random() * 10) + 2;
      num2 = Math.floor(Math.random() * 10) + 2;
      answer = num1 * num2;
      return { str: `${num1} × ${num2}`, answer };
    case '/':
      // Ensure division yields whole number
      num2 = Math.floor(Math.random() * 10) + 2;
      answer = Math.floor(Math.random() * 10) + 2;
      num1 = num2 * answer;
      return { str: `${num1} ÷ ${num2}`, answer };
  }
}

function generateQuestions() {
  const opIndex = Math.floor(Math.random() * OPERATION_TYPES.length);
  const operation = OPERATION_TYPES[opIndex];

  // Generate slightly different but balanced questions for both
  const q1 = generateMathQuestion(operation);
  let q2;
  // Ensure we don't accidentally give them the exact same question
  do {
    q2 = generateMathQuestion(operation);
  } while (q1.str === q2.str && operation !== '/'); // Divs might be hard to ensure totally unique in this naive impl, but fine

  currentP1Answer = q1.answer;
  p1QuestionBox.textContent = `${q1.str} = ?`;
  p1Input = '';
  p1Preview.textContent = '?';
  p1Preview.className = 'answer-preview';

  currentP2Answer = q2.answer;
  p2QuestionBox.textContent = `${q2.str} = ?`;
  p2Input = '';
  p2Preview.textContent = '?';
  p2Preview.className = 'answer-preview';
}

function handleKeyPress(containerId, key) {
  if (currentAppMode !== 'PLAYING') return;

  const isP1 = containerId === 'p1-keypad';
  let currentInput = isP1 ? p1Input : p2Input;
  const previewBox = isP1 ? p1Preview : p2Preview;
  const correctAns = isP1 ? currentP1Answer : currentP2Answer;

  if (key === '⌫') {
    currentInput = currentInput.slice(0, -1);
    playBeep(200, 'sine', 0.05);
  } else if (key === 'OK') {
    if (currentInput === '') return;
    checkAnswer(isP1, parseInt(currentInput, 10), correctAns);
    currentInput = '';
  } else {
    // Number key
    if (currentInput.length < 3) {
      currentInput += key;
      playBeep(400, 'sine', 0.05);
    }
  }

  // Update State & UI
  if (isP1) {
    p1Input = currentInput;
  } else {
    p2Input = currentInput;
  }
  
  previewBox.textContent = currentInput || '?';
  previewBox.className = 'answer-preview'; // Reset color
}

function checkAnswer(isP1, submittedVal, correctVal) {
  const previewBox = isP1 ? p1Preview : p2Preview;

  if (submittedVal === correctVal) {
    // CORRECT!
    playBeep(800, 'square', 0.1);
    previewBox.classList.add('correct');
    
    // Move Rope
    if (isP1) {
      ropePosition -= 1; // P1 pulls Negative (Up)
    } else {
      ropePosition += 1; // P2 pulls Positive (Down)
    }
    
    updateRopeVisuals();
    checkWinCondition();
    
    // Generate new questions ONLY when answered correctly to keep flow
    // A small delay to show correct state before popping new question
    setTimeout(generateQuestions, 300); 

  } else {
    // INCORRECT!
    playBeep(150, 'sawtooth', 0.2);
    previewBox.classList.add('wrong');
    // Clear input after short delay
    setTimeout(() => {
      if (isP1) { p1Input = ''; p1Preview.textContent = '?'; p1Preview.className = 'answer-preview'; }
      else { p2Input = ''; p2Preview.textContent = '?'; p2Preview.className = 'answer-preview'; }
    }, 400);
  }
}

function updateRopeVisuals() {
  // ropePosition varies from -WINNING_STEPS to +WINNING_STEPS
  // 50% is center.
  // Let's say +/- 40% is the max bound before win (from 10% to 90%)
  // So each step = 40% / WINNING_STEPS
  
  const stepSizePct = 40 / WINNING_STEPS;
  // knot.top ranges from 50% down to 10% (P1 Win) or up to 90% (P2 Win)
  const targetTop = 50 + (ropePosition * stepSizePct);
  
  knot.style.top = `${targetTop}%`;
  
  // Also adjust team avatars base offset using same formula
  teamP1.style.top = `calc(${targetTop}% - 60px)`;
  teamP2.style.top = `calc(${targetTop}% + 60px)`;
}

function checkWinCondition() {
  if (Math.abs(ropePosition) >= WINNING_STEPS) {
    // We have a winner!
    currentAppMode = 'GAME_OVER';
    setTimeout(() => {
      switchScreen('GAME_OVER');
      if (ropePosition <= -WINNING_STEPS) {
        winnerText.textContent = 'PLAYER 1 WINS!';
        winnerText.className = 'title p1-win-text bounce-anim';
        playBeep(1000, 'triangle', 0.5); // Win sound basic
      } else {
        winnerText.textContent = 'PLAYER 2 WINS!';
        winnerText.className = 'title p2-win-text bounce-anim';
        playBeep(1000, 'triangle', 0.5);
      }
    }, 500); // Short delay to see the rope reach the edge
  }
}
