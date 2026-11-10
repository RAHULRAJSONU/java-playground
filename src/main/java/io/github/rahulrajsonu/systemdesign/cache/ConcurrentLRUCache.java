package io.github.rahulrajsonu.systemdesign.cache;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class ConcurrentLRUCache<K, V> {

    private final int capacity;
    private final ConcurrentHashMap<K, Node<K, V>> map;
    private final ReentrantLock lock;

    // Dummy head and tail nodes to eliminate null checks for boundary conditions
    private final Node<K, V> head;
    private final Node<K, V> tail;

    private static class Node<K, V> {
        K key;
        V value;
        Node<K, V> prev;
        Node<K, V> next;

        Node(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }

    public ConcurrentLRUCache(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be greater than 0");
        }
        this.capacity = capacity;
        this.map = new ConcurrentHashMap<>();
        this.lock = new ReentrantLock();

        this.head = new Node<>(null, null);
        this.tail = new Node<>(null, null);
        head.next = tail;
        tail.prev = head;
    }

    public V get(K key) {
        Node<K, V> node = map.get(key);
        if (node == null) {
            return null;
        }

        // To maintain strict LRU order, a read (get) updates recency.
        // We acquire the lock only for updating the doubly-linked list position.
        lock.lock();
        try {
            // Double-check that the node is still present in the map after acquiring the lock
            if (map.containsKey(key)) {
                moveToTail(node);
                return node.value;
            }
        } finally {
            lock.unlock();
        }

        return null;
    }

    public void put(K key, V value) {
        lock.lock();
        try {
            Node<K, V> node = map.get(key);

            if (node != null) {
                // Update existing value and mark as most recently used
                node.value = value;
                moveToTail(node);
            } else {
                // Check capacity and evict the least recently used item (the one right after head)
                if (map.size() >= capacity) {
                    Node<K, V> lru = head.next;
                    if (lru != tail) {
                        removeNode(lru);
                        map.remove(lru.key);
                    }
                }

                // Create and insert new node at the tail
                Node<K, V> newNode = new Node<>(key, value);
                addToTail(newNode);
                map.put(key, newNode);
            }
        } finally {
            lock.unlock();
        }
    }

    public int size() {
        return map.size();
    }

    // --- Helper methods for Doubly-Linked List (Must be called within a locked block) ---

    private void removeNode(Node<K, V> node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
    }

    private void addToTail(Node<K, V> node) {
        node.prev = tail.prev;
        node.next = tail;
        tail.prev.next = node;
        tail.prev = node;
    }

    private void moveToTail(Node<K, V> node) {
        removeNode(node);
        addToTail(node);
    }
}