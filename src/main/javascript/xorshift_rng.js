/*
 * Copyright 2021  Elad Hirsch
 *
 * This file is part of java-2-times-faster-than-c.
 *
 * java-2-times-faster-than-c is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * java-2-times-faster-than-c is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * alet with shader-web-background.  If not, see <https://www.gnu.org/licenses/>.
 */

const ITERATION_COUNT = 1000000000;

class XorShift64SRng {
    constructor(state) {
        this.state = state;
    }

    getRand() {
        let x = this.state;	/* The state must be seeded with a nonzero value. */
        x ^= x >> 12n; // a
        x ^= BigInt.asUintN(64, x << 25n); // b
        x ^= x >> 27n; // c
        this.state = x;

        let randVal = BigInt.asUintN(64, x * 0x2545F4914F6CDD1Dn);

        // mix to a double
        let a = BigInt.asUintN(32, randVal >> 32n);
        let b = BigInt.asUintN(32, randVal);

        return (Number(a >> 5n) * 67108864.0 + Number(b >> 6n)) * (1.0 / 9007199254740991.0);
    }
}

let rng = new XorShift64SRng(42n);
let checksum = 0;
for (let i = 0; i < ITERATION_COUNT; i++) {
    checksum += rng.getRand();
}

console.log("checksum: " + checksum);
