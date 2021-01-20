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

package com.xemantic.test.howfast;

public class Java2TimesFasterThanC {

  public static final int NODE_COUNT = 1000;
  public static final long TRAVERSAL_COUNT = 5000000000L;

  private static class Node {

    private Node previous;
    private Node next;
    private long id;

    private Node(long id) {
      this.id = id;
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
    Node head = new Node(nodeId++);
    head.join(new Node(nodeId++));
    for (int i = 2; i < NODE_COUNT; i++) {
      head.insert(new Node(nodeId++));
    }
    Node toDelete = head;
    Node toInsert = head;
    for (long i = 0; i < TRAVERSAL_COUNT; i++) {
      toInsert = toInsert.next;
      Node prevToDelete = toDelete.previous;
      if (toInsert == toDelete) {
        toInsert = toInsert.next;
      }
      toDelete.delete();
      toDelete = prevToDelete;
      toInsert.insert(new Node(nodeId++));
    }
    long checksum = 0;
    head = toInsert;
    Node traveler = head;
    do {
      checksum += traveler.id;
    } while (
        (traveler = traveler.next) != head
    );
    System.out.println("checksum: " + checksum);
  }

}
