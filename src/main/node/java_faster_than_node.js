const MAX_PAYLOAD_SIZE   = 10000;
const INITIAL_NODE_COUNT = 1000;
const MUTATION_COUNT     = 10000000;
const MAX_MUTATION_SIZE  = 10;

function almostPseudoRandom(ordinal) {
    return (Math.sin(ordinal * 100000.0) + 1.0) % 1.0;
}

const Node = (function () {
    function Node(id) {
        this.id = id;
        this.payload = [];
        let payloadSize = Math.floor(almostPseudoRandom(id) * MAX_PAYLOAD_SIZE);
        this.payload = Buffer.alloc(payloadSize);
        this.payload.fill(id);
    }

    Node.prototype.insert = function (node) {
        node.next = this.next;
        node.previous = this;
        this.next.previous = node;
        this.next = node;
    }

    Node.prototype.delete = function () {
        this.next.previous = this.previous;
        this.previous.next = this.next;
    }

    Node.prototype.join = function (node) {
        this.previous = node;
        this.next = node;
        node.previous = this;
        node.next = this;
    }

    return Node;
})();

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
    for (let j = 0; j < deleteCount; j++) {
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
        checksum += (traveler.payload[0] <<24 >>24);
    }
} while ((traveler = traveler.next) !== head) {}

console.log("node count: " + nodeCount);
console.log("checksum: " + checksum)