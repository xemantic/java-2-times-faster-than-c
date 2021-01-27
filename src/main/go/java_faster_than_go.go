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
	"math"
	"os"
	"runtime/pprof"
)

const MaxPayloadSize int   = 10000
const InitialNodeCount int = 1000
const MutationCount int64  = 10000000
const MaxMutationSize int  = 10

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

func createNode(id int) *Node  {
	size := int(almostPseudoRandom(int64(id)) * float64(MaxPayloadSize))
	n := &Node{
		id:      int64(id),
		payload: make([]byte ,size),
	}

	if size == 0 { return n }

	return fill(id, n, size)
}

func fill(id int, n *Node, size int) * Node {
	// using native copy significantly improve performance
	n.payload[0] = byte(id)
	for j := 1; j < size; j *= 2 {
		copy(n.payload[j:], n.payload[:j])
	}

	return n
}

func almostPseudoRandom(ordinal int64) float64 {
	return math.Mod(math.Sin(float64(ordinal) * 100000.0) + 1.0, 1.0)
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
	mutationSeq := 0
	head := createNode(nodeId)
	nodeId++
	head.join(createNode(nodeId))
	nodeId++

	for i := 2; i < InitialNodeCount; i++ {
		head.insert(createNode(nodeId))
		nodeId++
	}
	nodeCount := InitialNodeCount

	for i := int64(0); i < MutationCount; i++ {
		deleteCount := int(almostPseudoRandom(int64(mutationSeq)) * float64(MaxMutationSize))
		mutationSeq++
		if deleteCount > (nodeCount -2) {
			deleteCount = nodeCount - 2
		}

		for j := 0; j < deleteCount; j++ {
			toDelete := head
			head = head.prev
			toDelete.delete()
		}

		nodeCount -= deleteCount
		insertCount := int(almostPseudoRandom(int64(mutationSeq)) * float64(MaxMutationSize))
		mutationSeq++

		for j := 0; j < insertCount; j++ {
			head.insert(createNode(nodeId))
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
