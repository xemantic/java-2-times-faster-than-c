package main

import "math"

func almostPseudoRandom(ordinal int64) float64  {
	return math.Mod(math.Sin(float64(ordinal) * 100000) + 1, 1)
}
