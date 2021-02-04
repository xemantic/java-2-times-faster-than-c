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

package com.xemantic.test.howfast.kotlin

import kotlin.math.sin

private const val MAX_PAYLOAD_SIZE   = 50
private const val INITIAL_NODE_COUNT = 10000
private const val MUTATION_COUNT     = 1000000L
private const val MAX_MUTATION_SIZE  = 200

private fun almostPseudoRandom(ordinal: Long): Double {
  return (sin(ordinal.toDouble() * 100000.0) + 1.0) % 1.0
}

private class Node(val id: Long) {
  var previous: Node? = null
  var next: Node? = null
  val payload: ByteArray = ByteArray((almostPseudoRandom(id) * MAX_PAYLOAD_SIZE).toInt())

  init {
    for (i in payload.indices) {
      payload[i] = i.toByte()
    }
  }

  fun join(node: Node) {
    previous = node
    next = node
    node.previous = this
    node.next = this
  }

  fun delete() {
    next?.previous = previous
    previous?.next = next
    // free memory (which is explicit in C and implicit in Kotlin)
  }

  fun insert(node: Node) {
    node.next = next
    node.previous = this
    next?.previous = node
    next = node
  }

}

fun main() {
  var nodeId: Long = 0
  var mutationSeq: Long = 0
  var head = Node(nodeId++)
  head.join(Node(nodeId++))
  for (i in 2 until INITIAL_NODE_COUNT) {
    head.insert(Node(nodeId++))
  }
  var nodeCount = INITIAL_NODE_COUNT.toLong()
  for (i in 0 until MUTATION_COUNT) {
    var deleteCount =
      (almostPseudoRandom(mutationSeq++) * MAX_MUTATION_SIZE.toDouble()).toInt()
    if (deleteCount > nodeCount - 2) {
      deleteCount = nodeCount.toInt() - 2
    }
    for (j in 0 until deleteCount) {
      val toDelete = head
      head = head.previous!!
      toDelete.delete()
    }
    nodeCount -= deleteCount.toLong()
    val insertCount =
      (almostPseudoRandom(mutationSeq++) * MAX_MUTATION_SIZE.toDouble()).toInt()
    for (j in 0 until insertCount) {
      head.insert(Node(nodeId++))
      head = head.next!!
    }
    nodeCount += insertCount.toLong()
  }
  var checksum: Long = 0
  var traveler = head
  do {
    checksum += traveler.id + traveler.payload.size
    if (traveler.payload.isNotEmpty()) {
      checksum += traveler.payload[0]
      checksum += traveler.payload[traveler.payload.size - 1]
    }
  } while (traveler.next.also { traveler = it!! } !== head)
  println("node count: $nodeCount")
  println("checksum: $checksum")
}
