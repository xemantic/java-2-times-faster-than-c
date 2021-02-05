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

pub struct Xorshift64sRng {
    a: u64
}

impl Xorshift64sRng {
    pub fn new(a: u64) -> Self {
        Self { a }
    }

    pub fn get_rand(&mut self) -> f64 {
        let mut x: u64 = self.a;    /* The state must be seeded with a nonzero value. */
        x ^= x >> 12; // a
        x ^= x << 25; // b
        x ^= x >> 27; // c
        self.a = x;
        let rand_val: u64 = x * 0x2545F4914F6CDD1D_u64;

        // mix to a double
        let a: u32 = (rand_val >> 32) as u32;
        let b: u32 = (rand_val & 0xFFFFFFFF_u64) as u32;

        ((a >> 5) as f64 * 67108864.0 + (b >> 6) as f64) * (1.0 / 9007199254740991.0)
    }
}
