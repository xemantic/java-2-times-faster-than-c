/*
 * Copyright 2021  Sam Leonard
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

package main

import (
	"fmt"
)

const iterationCount int64 = 1000000000

type xorshift64sRng struct {
	a uint64
}

func (state *xorshift64sRng) getRand() float64 {
	var x = state.a /* The state must be seeded with a nonzero value. */
	x ^= x >> 12    // a
	x ^= x << 25    // b
	x ^= x >> 27    // c
	state.a = x
	var randVal = x * uint64(0x2545F4914F6CDD1D)

	// mix to a double
	var a = randVal >> 32
	var b = randVal & 0xFFFFFFFF

	return (float64(a>>5)*67108864.0 + float64(b>>6)) * (1.0 / 9007199254740991.0)
}

func newRng(a uint64) *xorshift64sRng {
	return &xorshift64sRng{
		a: a,
	}
}

func main() {
	var rng = newRng(42)

	var checksum float64 = 0
	for i := int64(0); i < iterationCount; i++ {
		checksum += rng.getRand()
	}

	fmt.Println("checksum:", checksum)
}
