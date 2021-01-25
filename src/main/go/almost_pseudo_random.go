package main

const IterationCount int64 = 1000000000

func main() {
	var checksum float64 = 0
	for i := int64(0); i < IterationCount; i++ {
		checksum += math.Mod(math.Sin(float64(i) * 100000) + 1, 1)
	}
	println("checksum: " ,checksum)
}
