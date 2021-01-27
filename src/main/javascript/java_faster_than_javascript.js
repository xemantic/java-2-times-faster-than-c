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

const MAX_PAYLOAD_SIZE   = 10000;
const INITIAL_NODE_COUNT = 1000;
const MUTATION_COUNT     = 10000000;
const MAX_MUTATION_SIZE  = 10;

function almostPseudoRandom(ordinal) {
    return (Math.sin(ordinal * 100000.0) + 1.0) % 1.0;
}

class Node {
    constructor(id) {
        this.id = id;
        this.payload = new Int8Array(
          new ArrayBuffer(
            Math.floor(almostPseudoRandom(id) * MAX_PAYLOAD_SIZE)
          )
        );
        this.payload.fill(id);
    }

    insert(node) {
        node.next = this.next;
        node.previous = this;
        this.next.previous = node;
        this.next = node;
    }

    delete() {
        this.next.previous = this.previous;
        this.previous.next = this.next;
    }

    join(node) {
        this.previous = node;
        this.next = node;
        node.previous = this;
        node.next = this;
    }

}

let nodeId = 0;
let mutationSeq = 0;

let head = new Node(nodeId++);
head.join(new Node(nodeId++));

for (let i = 2; i < INITIAL_NODE_COUNT; i++) {
    head.insert(new Node(nodeId++));
}

let nodeCount = INITIAL_NODE_COUNT;

for (let i = 0; i < MUTATION_COUNT; i++) {
    let deleteCount = Math.floor(almostPseudoRandom(mutationSeq++) * MAX_MUTATION_SIZE);
    if (deleteCount > (nodeCount - 2)) {
        deleteCount = nodeCount - 2;
    }
    for (let j = 0; j < deleteCount; j++) {
        let toDelete = head;
        head = head.previous;
        toDelete.delete();
    }
    nodeCount -= deleteCount;
    let insertCount = Math.floor(almostPseudoRandom(mutationSeq++) * MAX_MUTATION_SIZE);
    for (let j = 0; j < insertCount; j++) {
        head.insert(new Node(nodeId++));
        head = head.next;
    }
    nodeCount += insertCount;
}


let checksum = 0;
let traveler = head;

do {
    checksum += traveler.id + traveler.payload.length;
    if (traveler.payload.length > 0) {
        checksum += (traveler.payload[0]);
    }
} while ((traveler = traveler.next) !== head) {}

console.log("node count: " + nodeCount);
console.log("checksum: " + checksum)