/*
 * Copyright 2021  Elad Hirsch
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

package main

import (
	"flag"
	_ "fmt"
	"log"
	"os"
	"runtime/pprof"
)

const MaxPayloadSize int   = 50
const InitialNodeCount int = 10000
const MutationCount int64  = 1000000
const MaxMutationSize int  = 200

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

type Node struct {
	id      int64
	prev    *Node
	next    *Node
	payload []byte
}

func (node *Node) insert(newNode *Node) {
	newNode.next = node.next
	newNode.prev = node
	node.next.prev = newNode
	node.next = newNode
}

func (node *Node) delete() {
	node.next.prev = node.prev
	node.prev.next = node.next
}

func (node *Node) join(newNode *Node) {
	node.prev = newNode
	node.next = newNode
	newNode.prev = node
	newNode.next = node
}

func createNode(id int, rng *xorshift64sRng) *Node  {
	size := int(rng.getRand() * float64(MaxPayloadSize))
  data := make([]byte, size);
  for i := 0; i < size; i++ {
    data[i] = byte(i);
	}
	return &Node{
    id:      int64(id),
    payload: data,
  }
}

var cpuProfile = flag.String("cpuprofile", "", "write cpu profile to `file`")

func main() {
	flag.Parse()

	if *cpuProfile != "" {
		f, err := os.Create(*cpuProfile)
		if err != nil {
			log.Fatal("could not create CPU profile: ", err)
		}
		defer f.Close()
		if err := pprof.StartCPUProfile(f); err != nil {
			log.Fatal("could not start CPU profile: ", err)
		}
		defer pprof.StopCPUProfile()
	}

	nodeId := 0
  rng := newRng(42);
	head := createNode(nodeId, rng)
	nodeId++
	head.join(createNode(nodeId, rng))
	nodeId++

	for i := 2; i < InitialNodeCount; i++ {
		head.insert(createNode(nodeId, rng))
		nodeId++
	}
	nodeCount := InitialNodeCount

	for i := int64(0); i < MutationCount; i++ {
		deleteCount := int(rng.getRand() * float64(MaxMutationSize))
		if deleteCount > (nodeCount -2) {
			deleteCount = nodeCount - 2
		}

		for j := 0; j < deleteCount; j++ {
			toDelete := head
			head = head.prev
			toDelete.delete()
		}

		nodeCount -= deleteCount
		insertCount := int(rng.getRand() * float64(MaxMutationSize))

		for j := 0; j < insertCount; j++ {
			head.insert(createNode(nodeId, rng))
			nodeId++
			head = head.next
		}
		nodeCount += insertCount
	}

	var checksum int64 = 0
	traveler := head

	for {
		checksum += traveler.id + int64(len(traveler.payload))
		if len(traveler.payload) > 0 {
			checksum += int64(int8(traveler.payload[0]))
		}
		traveler = traveler.next
		if traveler == head {
			break
		}
	}

	println("node count:" ,nodeCount)
	println("checksum: ", checksum)
}
