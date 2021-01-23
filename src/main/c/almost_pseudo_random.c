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

#include <stdio.h>
#include <math.h>

const long ITERATION_COUNT = 1000000000L;

double almost_pseudo_random(long ordinal) {
  return fmod(sin(((double) ordinal) * 100000.0) + 1.0, 1.0);
}

void main() {
  double checksum = 0;
  for (long i = 0; i < ITERATION_COUNT; i++) {
    checksum += almost_pseudo_random(i);
  }
  printf("checksum: %f\n", checksum);
}
