/*
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

use std::ptr;

const MAX_PAYLOAD_SIZE: i32   = 50;
const INITIAL_NODE_COUNT: i32 = 10000;
const MUTATION_COUNT: i64     = 1000000;
const MAX_MUTATION_SIZE: i32  = 200;

struct Node {
  id: i64,
  payload: Vec<i8>,
  next: NodePointer,
  previous: NodePointer,
}

type NodePointer = *mut Node;

fn almost_pseudo_random(ordinal: i64) -> f64 {
  ((ordinal as f64 * 100000.0).sin() + 1.0) % 1.0
}

fn main() {
    let mut node_id: i64 = 0;
    let mut mutation_seq: i64 = 0;

    let mut head = Node::new(node_id); node_id += 1;

    // hell yeah who needs safety when we have pointers, YEE HAWWW
    unsafe {
        Node::join(head, Node::new(node_id)); node_id += 1;
        for _ in 2..INITIAL_NODE_COUNT {
            Node::insert(head, Node::new(node_id)); node_id += 1;
        }
        let mut node_count: i64 = INITIAL_NODE_COUNT as i64;
        for _ in 0..MUTATION_COUNT {
            let mut delete_count = (almost_pseudo_random(mutation_seq) * MAX_MUTATION_SIZE as f64) as i32; mutation_seq += 1;
            if delete_count > (node_count - 2) as i32 {
                delete_count = (node_count - 2) as i32;
            }
            for _ in 0..delete_count {
                let to_delete = head;
                head = (*head).previous;
                Node::delete(to_delete);
            }
            node_count -= delete_count as i64;
            let insert_count = (almost_pseudo_random(mutation_seq) * MAX_MUTATION_SIZE as f64) as i32; mutation_seq += 1;
            for _ in 0..insert_count {
                Node::insert(head, Node::new(node_id)); node_id += 1;
                head = (*head).next;
            }
            node_count += insert_count as i64;
        }
        let mut checksum: i64 = 0;
        let mut traveler = head;
        loop {
            checksum += (*traveler).id as i64 + (*traveler).payload.len() as i64;
            if let Some(val) = (*traveler).payload.first() {
                checksum += *val as i64;
            }
            if let Some(val) = (*traveler).payload.last() {
                checksum += *val as i64;
            }
            traveler = (*traveler).next;
            if traveler == head { break };
        }
        println!("node count: {}", node_count);
        println!("checksum: {}", checksum);
    }
}

impl Node {
    fn new(id: i64) -> NodePointer {
        Box::into_raw(Box::new(Self{
            id,
            payload: vec![id as i8; (almost_pseudo_random(id) * MAX_PAYLOAD_SIZE as f64) as usize],
            next: ptr::null_mut(),
            previous: ptr::null_mut(),
        }))
    }

    unsafe fn join(alfa: NodePointer, beta: NodePointer) {
        (*alfa).previous = beta;
        (*alfa).next     = beta;
        (*beta).previous = alfa;
        (*beta).next     = alfa;
    }

    unsafe fn delete(node: NodePointer) {
        (*(*node).next).previous = (*node).previous;
        (*(*node).previous).next = (*node).next;

        // let the created box do the deallocation when it goes out of scope
        Box::from_raw(node);
    }

    unsafe fn insert(previous: NodePointer, node: NodePointer) {
        (*node).next = (*previous).next;
        (*node).previous = previous;
        (*(*previous).next).previous = node;
        (*previous).next = node;
    }
}
