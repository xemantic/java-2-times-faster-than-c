package main

import (
	"flag"
	_ "fmt"
	"log"
	"os"
	"runtime/pprof"
)

const NodeCount int = 1000
const TraversalCount int64 = 5000000000

type Node struct {
	value int
	prev  *Node
	next  *Node
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
	head := &Node{value: nodeId}
	nodeId++
	head.join(&Node{value: nodeId})

	for i := 2; i < NodeCount; i++ {
		nodeId++
		head.insert(&Node{value: nodeId})
	}

	toDelete := head
	toInsert := head

	for i := int64(0); i < TraversalCount; i++ {
		toInsert = toInsert.next
		prevToDelete := toDelete.prev
		if toInsert == toDelete {
			toInsert = toInsert.next
		}
		toDelete.delete()
		toDelete = prevToDelete
		nodeId++
		toInsert.insert(&Node{value: nodeId})
	}

	checksum := 0
	head = toInsert
	traveler := head

	for {
		checksum += traveler.value
		traveler = traveler.next
		if traveler == head {
			break
		}
	}

	println("checksum: ", checksum)
}
