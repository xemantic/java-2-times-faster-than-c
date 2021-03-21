/*
 * Copyright 2021  Kazimierz Pogoda
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

package com.xemantic.test.howfast.kotlin

import kotlin.math.sin

const val ITERATION_COUNT = 1000000000L

@ExperimentalUnsignedTypes
class XorShift64SRng(var state: ULong) {
  init {
    assert(state != 0UL)
  }

  fun getRand(): Double {
    var x: ULong = state /* The state must be seeded with a nonzero value. */
    x = x xor (x shr 12) // a
    x = x xor (x shl 25) // b
    x = x xor (x shr 27) // c

    state = x
    val rand_val: ULong = x * 0x2545F4914F6CDD1DUL;

    // mix to a double
    val a = (rand_val shr 32).toUInt()
    val b = (rand_val and 0xFFFFFFFFU).toUInt()

    return ((a shr 5).toDouble() * 67108864.0 + (b shr 6).toDouble()) * (1.0 / 9007199254740991.0)
  }
}

fun main() {
  var rng = XorShift64SRng(42UL);
  var checksum = 0.0
  for (i in 0 until ITERATION_COUNT) {
    checksum += rng.getRand()
  }
  println("checksum: $checksum")
}
