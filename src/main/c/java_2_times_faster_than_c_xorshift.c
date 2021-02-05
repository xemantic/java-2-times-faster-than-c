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
#include <stdint.h>
#include <stdlib.h>
#include <math.h>

const int  MAX_PAYLOAD_SIZE   = 50;
const int  INITIAL_NODE_COUNT = 10000;
const long MUTATION_COUNT     = 1000000L;
const int  MAX_MUTATION_SIZE  = 200;

struct xorshift64s_state {
  uint64_t a;
};

double xorshift64s(struct xorshift64s_state *state) {
	uint64_t x = state->a;	/* The state must be seeded with a nonzero value. */
	x ^= x >> 12; // a
	x ^= x << 25; // b
	x ^= x >> 27; // c
	state->a = x;
	uint64_t rand_val = x * UINT64_C(0x2545F4914F6CDD1D);

	// mix to a double
	uint32_t a = rand_val >> 32;
	uint32_t b = rand_val & 0xFFFFFFFF;
  return ((a >> 5) * 67108864.0 + (b >> 6)) * (1.0 / 9007199254740991.0);
}

typedef struct Node Node;

struct Node {
  long   id;
  int    size;
  char   *payload;
  Node   *previous;
  Node   *next;
} NodeDef;

Node *new_node(long id, struct xorshift64s_state * rng_state) {
  int size = (int) (xorshift64s(rng_state) * MAX_PAYLOAD_SIZE);
  int charId = (char) id;
  Node *node = malloc(sizeof(NodeDef));
  node->id = id;
  node->size = size;
  char *payload = malloc(sizeof(char) * size);
  for (int i = 0; i < size; i++) {
    payload[i] = (char) i;
  }
  node->payload = payload;
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
	struct xorshift64s_state rng_state = {
		.a = 42,
	};
  Node *head = new_node(node_id++, &rng_state);
  join(head, new_node(node_id++, &rng_state));
  for (int i = 2; i < INITIAL_NODE_COUNT; i++) {
    insert(head, new_node(node_id++, &rng_state));
  }
  long node_count = INITIAL_NODE_COUNT;
  for (long i = 0; i < MUTATION_COUNT; i++) {
    int delete_count = (int) (xorshift64s(&rng_state) * (double) MAX_MUTATION_SIZE);
    if (delete_count > (node_count - 2)) {
      delete_count = (int) node_count - 2;
    }
    for (int j = 0; j < delete_count; j++) {
      Node *to_delete = head;
      head = head->previous;
      delete(to_delete);
    }
    node_count -= delete_count;
    int insert_count = (int) (xorshift64s(&rng_state) * (double) MAX_MUTATION_SIZE);
    for (int j = 0; j < insert_count; j++) {
      insert(head, new_node(node_id++, &rng_state));
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
      checksum += traveler->payload[traveler->size - 1];
    }
  } while (
      (traveler = traveler->next) != head
  );
  printf("node count: %lu\n", node_count);
  printf("checksum: %lu\n", checksum);
}
