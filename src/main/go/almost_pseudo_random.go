package main

const IterationCount int64 = 1000000000

func main() {
	var checksum float64 = 0
	for j := int64(0); j < IterationCount; j++ {
		checksum += almostPseudoRandom(j)
	}
	println("checksum: " ,checksum)
}
