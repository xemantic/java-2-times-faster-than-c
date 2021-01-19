/*
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
