/*
 * Copyright 2021  Kazimierz Pogoda
 * Copyright 2021  Sam Leonard
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

package com.xemantic.test.howfast;

public class Java2TimesFasterThanC {

  private static final int  MAX_PAYLOAD_SIZE   = 50;
  private static final int  INITIAL_NODE_COUNT = 10000;
  private static final long MUTATION_COUNT     = 1000000L;
  private static final int  MAX_MUTATION_SIZE  = 200;

  private static class Node {

    private long   id;
    private byte[] payload;
    private Node   previous;
    private Node   next;

    private Node(long id, xorshift64s_rng rng) {
      this.id = id;
      int size = (int) (rng.get_rand() * (double) MAX_PAYLOAD_SIZE);
      byte[] data = new byte[size];
      for (int i = 0; i < size; i++) {
        data[i] = (byte) i;
      }
      payload = data;
    }

    void join(Node node) {
      previous = node;
      next = node;
      node.previous = this;
      node.next = this;
    }

    void delete() {
      next.previous = previous;
      previous.next = next;
      // free memory (which is explicit in C and implicit in Java)
    }

    void insert(Node node) {
      node.next = next;
      node.previous = this;
      next.previous = node;
      next = node;
    }

  }

  public static void main(String[] args) {
    long nodeId = 0;
    xorshift64s_rng rng = new xorshift64s_rng(42);
    Node head = new Node(nodeId++, rng);
    head.join(new Node(nodeId++, rng));
    for (int i = 2; i < INITIAL_NODE_COUNT; i++) {
      head.insert(new Node(nodeId++, rng));
    }
    long nodeCount = INITIAL_NODE_COUNT;
    for (long i = 0; i < MUTATION_COUNT; i++) {
      int deleteCount = (int) (rng.get_rand() * (double) MAX_MUTATION_SIZE);
      if (deleteCount > (nodeCount - 2)) {
        deleteCount = (int) nodeCount - 2;
      }
      for (int j = 0; j < deleteCount; j++) {
        Node toDelete = head;
        head = head.previous;
        toDelete.delete();
      }
      nodeCount -= deleteCount;
      int insertCount = (int) (rng.get_rand() * (double) MAX_MUTATION_SIZE);
      for (int j = 0; j < insertCount; j++) {
        head.insert(new Node(nodeId++, rng));
        head = head.next;
      }
      nodeCount += insertCount;
    }
    long checksum = 0;
    Node traveler = head;
    do {
      checksum += traveler.id + traveler.payload.length;
      if (traveler.payload.length > 0) {
        checksum += traveler.payload[0];
        checksum += traveler.payload[traveler.payload.length - 1];
      }
    } while (
        (traveler = traveler.next) != head
    );
    System.out.println("node count: " + nodeCount);
    System.out.println("checksum: " + checksum);
  }

}
