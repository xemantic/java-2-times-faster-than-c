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

#include <stdio.h>
#include <stdint.h>

const long ITERATION_COUNT = 1000000000L;

struct xorshift64s_state {
  uint64_t a;
};

double xorshift64s(struct xorshift64s_state *state) {
	uint64_t x = state->a;	/* The state must be seeded with a nonzero value. */
	x ^= x >> 12; // a
	x ^= x << 25; // b
	x ^= x >> 27; // c
	state->a = x;
	uint64_t rand_val = x * UINT64_C(0x2545F4914F6CDD1D);

	// mix to a double
	uint32_t a = rand_val >> 32;
	uint32_t b = rand_val & 0xFFFFFFFF;

  return ((a >> 5) * 67108864.0 + (b >> 6)) * (1.0 / 9007199254740991.0);
}

int main(void) {
	struct xorshift64s_state rng_state = {
		.a = 42
	};

	double checksum = 0;
	for (long i = 0; i < ITERATION_COUNT; i++) {
		checksum += xorshift64s(&rng_state);
	}
	printf("checksum: %f\n", checksum);
}
