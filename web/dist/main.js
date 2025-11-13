"use strict";
// Get DOM elements [cite: 40]
const canvas = document.getElementById('frame-canvas');
const ctx = canvas.getContext('2d');
const statsEl = document.getElementById('stats');
if (ctx && statsEl) {
    const img = new Image();
    img.src = 'sample_edge.png';
    img.onload = () => {
        // Display the static image
        ctx.drawImage(img, 0, 0, canvas.width, canvas.height);
    };
    img.onerror = () => {
        ctx.fillStyle = 'red';
        ctx.font = '16px sans-serif';
        ctx.fillText('Error: Could not load sample_edge.png', 10, 30);
    };
    // Display basic text overlay for stats [cite: 39]
    const resolution = `${canvas.width}x${canvas.height}`;
    const mockFps = (Math.random() * 5 + 10).toFixed(1); // 10-15 FPS [cite: 32]
    statsEl.innerText = `Frame Stats: ${resolution} @ ${mockFps} FPS (static)`;
}
else {
    console.error("Could not find canvas or stats element.");
}
console.log("TypeScript Web Viewer Initialized.");
