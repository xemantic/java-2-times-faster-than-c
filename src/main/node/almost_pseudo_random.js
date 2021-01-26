const ITERATION_COUNT = 1000000000;

function almostPseudoRandom(ordinal) {
    return (Math.sin(ordinal * 100000.0) + 1.0) % 1.0;
}

let checksum = 0;
for (let i = 0; i < ITERATION_COUNT; i++) {
    checksum += almostPseudoRandom(i);
}

console.log("checksum: " + checksum);
