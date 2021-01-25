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
#include <math.h>

const int  MAX_PAYLOAD_SIZE   = 10000;
const int  INITIAL_NODE_COUNT = 1000;
const long MUTATION_COUNT     = 10000000L;
const int  MAX_MUTATION_SIZE  = 10;

typedef struct Node Node;

struct Node {
  long   id;
  int    size;
  char   *payload;
  Node   *previous;
  Node   *next;
} NodeDef;

double almost_pseudo_random(long ordinal) {
  return fmod((sin(((double) ordinal) * 100000.0) + 1.0), 1.0);
}

Node *new_node(long id) {
  int size = (int) (almost_pseudo_random(id) * MAX_PAYLOAD_SIZE);
  int charId = (char) id;
  Node *node = malloc(sizeof(NodeDef));
  node->id = id;
  node->size = size;
  node->payload = malloc(sizeof(char) * size);
  for (int i = 0; i < size; i++) {
    node->payload[i] = charId;
  }
  return node;
}

void join(Node *alfa, Node *beta) {
  alfa->previous = beta;
  alfa->next = beta;
  beta->previous = alfa;
  beta->next = alfa;
}

void delete(Node *node) {
  node->next->previous = node->previous;
  node->previous->next = node->next;
  free(node->payload);
  free(node);
}

void insert(Node *previous, Node *node) {
  node->next = previous->next;
  node->previous = previous;
  previous->next->previous = node;
  previous->next = node;
}

int main() {
  long node_id = 0;
  long mutation_seq = 0;
  Node *head = new_node(node_id++);
  join(head, new_node(node_id++));
  for (int i = 2; i < INITIAL_NODE_COUNT; i++) {
    insert(head, new_node(node_id++));
  }
  long node_count = INITIAL_NODE_COUNT;
  for (long i = 0; i < MUTATION_COUNT; i++) {
    int delete_count = (int) (almost_pseudo_random(mutation_seq++) * (double) MAX_MUTATION_SIZE);
    if (delete_count > (node_count - 2)) {
      delete_count = (int) node_count - 2;
    }
    for (int j = 0; j < delete_count; j++) {
      Node *to_delete = head;
      head = head->previous;
      delete(to_delete);
    }
    node_count -= delete_count;
    int insert_count = (int) (almost_pseudo_random(mutation_seq++) * (double) MAX_MUTATION_SIZE);
    for (int j = 0; j < delete_count; j++) {
      insert(head, new_node(node_id++));
      head = head->next;
    }
    node_count += insert_count;
  }
  long checksum = 0;
  Node *traveler = head;
  do {
    checksum += traveler->id + traveler->size;
    if (traveler->size > 0) {
      checksum += traveler->payload[0];
    }
  } while (
      (traveler = traveler->next) != head
  );
  printf("node count: %lu\n", node_count);
  printf("checksum: %lu\n", checksum);
}
