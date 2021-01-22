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

package com.xemantic.test.howfast;

public class AlmostPseudoRandom {

  private static final long ITERATION_COUNT = 1000000000L;

  private static double almostPseudoRandom(long ordinal) {
    return (Math.sin(((double) ordinal) * 100000.0) + 1.0) % 1.0;
  }

  public static void main(String[] args) {
    double checksum = 0;
    for (long i = 0; i < ITERATION_COUNT; i++) {
      checksum += almostPseudoRandom(i);
    }
    System.out.println("checksum: " + checksum);
  }

}
