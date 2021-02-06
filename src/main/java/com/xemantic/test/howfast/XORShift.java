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

package com.xemantic.test.howfast;

class xorshift64s_rng {
	private long state;

	xorshift64s_rng(long state) {
		this.state = state;
	}

	double get_rand() {
		long x = this.state;	/* The state must be seeded with a nonzero value. */
		x ^= x >>> 12; // a
		x ^= x  << 25; // b
		x ^= x >>> 27; // c
		this.state = x;

		long rand_val = x * 0x2545F4914F6CDD1DL;

		// mix to a double
		long a = (rand_val >>> 32) & 0xFFFFFFFFL;
		long b = rand_val & 0xFFFFFFFFL;

		return ((a >> 5) * 67108864.0 + (b >> 6)) * (1.0 / 9007199254740991.0);
	}
}

public class XORShift {

  private static final long ITERATION_COUNT = 1000000000L;

  public static void main(String[] args) {
		xorshift64s_rng rng = new xorshift64s_rng(42);

    double checksum = 0;
    for (long i = 0; i < ITERATION_COUNT; i++) {
      checksum += rng.get_rand();
    }
    System.out.println("checksum: " + checksum);
  }

}
