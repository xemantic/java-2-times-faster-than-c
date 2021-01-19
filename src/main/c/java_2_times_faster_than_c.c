#include <stdio.h>
#include <stdlib.h>

const int NODE_COUNT = 1000;
const long TRAVERSAL_COUNT = 5000000000L;

typedef struct Node Node;

struct Node {
  Node *previous;
  Node *next;
  long id;
};

struct Node *newNode(long id) {
  Node* node = malloc(sizeof(Node));
  node->id = id;
  return node;
}

void join(Node *root, Node *node) {
  root->previous = node;
  root->next = node;
  node->previous = root;
  node->next = root;
}

void dispose(Node *node) { // delete is reserved in C
  node->next->previous = node->previous;
  node->previous->next = node->next;
  free(node);
}

void insert(Node *previous, Node *node) {
  node->next = previous->next;
  node->previous = previous;
  previous->next->previous = node;
  previous->next = node;
}

int main() {
  long nodeId = 0;
  Node *head = newNode(nodeId++);
  join(head, newNode(nodeId++));
  for (int i = 2; i < NODE_COUNT; i++) {
    insert(head, newNode(nodeId++));
  }
  Node *toDelete = head;
  Node *toInsert = head;
  for (long i = 0; i < TRAVERSAL_COUNT; i++) {
    toInsert = toInsert->next;
    Node *prevToDelete = toDelete->previous;
    if (toInsert == toDelete) {
      toInsert = toInsert->next;
    }
    dispose(toDelete);
    toDelete = prevToDelete;
    insert(toInsert, newNode(nodeId++));
  }
  long checksum = 0;
  Node *traveler = head;
  do {
    checksum += traveler->id;
  } while (
      (traveler = traveler->next) != head
  );
  printf("checksum: %lu\n", checksum);
}
