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
