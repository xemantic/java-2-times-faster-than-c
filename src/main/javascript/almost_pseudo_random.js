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
 * along with shader-web-background.  If not, see <https://www.gnu.org/licenses/>.
 */

const ITERATION_COUNT = 1000000000;

function almostPseudoRandom(ordinal) {
    return (Math.sin(ordinal * 100000.0) + 1.0) % 1.0;
}

let checksum = 0;
for (let i = 0; i < ITERATION_COUNT; i++) {
    checksum += almostPseudoRandom(i);
}

console.log("checksum: " + checksum);
