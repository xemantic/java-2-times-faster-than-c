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

const MAX_PAYLOAD_SIZE: i32   = 10000;
const INITIAL_NODE_COUNT: i32 = 1000;
const MUTATION_COUNT: i64     = 10000000;
const MAX_MUTATION_SIZE: i32  = 10;

struct Node {
  id: i64,
  payload: Vec<i8>,
  next: Option<NodePointer>,
  previous: Option<NodePointer>,
}

type NodePointer = ptr::NonNull<Node>;

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
                head = (*head.as_ptr()).previous.expect("Head has empty previous node.");
                Node::delete(to_delete);
            }
            node_count -= delete_count as i64;
            let insert_count = (almost_pseudo_random(mutation_seq) * MAX_MUTATION_SIZE as f64) as i32; mutation_seq += 1;
            for _ in 0..insert_count {
                Node::insert(head, Node::new(node_id)); node_id += 1;
                head = (*head.as_ptr()).next.expect("Head has empty next node.");
            }
            node_count += insert_count as i64;
        }
        let mut checksum: i64 = 0;
        let mut traveler = head;
        loop {
            checksum += (*traveler.as_ptr()).id as i64 + (*traveler.as_ptr()).payload.len() as i64;
            if let Some(val) = (*traveler.as_ptr()).payload.get(0) {
                checksum += *val as i64;
            }
            traveler = (*traveler.as_ptr()).next.expect("Traveler has an empty next pointer.");
            if traveler == head { break };
        }
        println!("node count: {}", node_count);
        println!("checksum: {}", checksum);
    }
}

impl Node {
    fn new(id: i64) -> NodePointer {
        NodePointer::new(
            Box::into_raw(Box::new(Self{
                id,
                payload: vec![id as i8; (almost_pseudo_random(id) * MAX_PAYLOAD_SIZE as f64) as usize],
                next: None,
                previous: None,
            }))
        ).expect("Failed to Allocate node.")
    }

    unsafe fn join(alfa: NodePointer, beta: NodePointer) {
        (*alfa.as_ptr()).previous = Some(beta);
        (*alfa.as_ptr()).next     = Some(beta);
        (*beta.as_ptr()).previous = Some(alfa);
        (*beta.as_ptr()).next     = Some(alfa);
    }

    unsafe fn delete(node: NodePointer) {
        let node_next: NodePointer = (*node.as_ptr()).next.expect("Node to delete has empty next pointer.");
        let node_prev: NodePointer = (*node.as_ptr()).previous.expect("Node to delete has empty previous pointer.");

        (*node_next.as_ptr()).previous = Some(node_prev);
        (*node_prev.as_ptr()).next = Some(node_next);

        // let the created box do the deallocation when it goes out of scope
        Box::from_raw(node.as_ptr());
    }

    unsafe fn insert(previous: NodePointer, node: NodePointer) {
        (*node.as_ptr()).next = (*previous.as_ptr()).next;
        (*node.as_ptr()).previous = Some(previous);
        let prev_next = (*previous.as_ptr()).next.expect("Empty next pointer on previous node in insert.");
        (*prev_next.as_ptr()).previous = Some(node);
        (*previous.as_ptr()).next = Some(node);
    }
}
