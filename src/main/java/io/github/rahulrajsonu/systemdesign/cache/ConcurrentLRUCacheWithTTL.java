package io.github.rahulrajsonu.systemdesign.cache;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class ConcurrentLRUCacheWithTTL<K, V> {

    private final int capacity;
    private final long ttlMillis;
    private final ConcurrentHashMap<K, Node<K, V>> map;
    private final PriorityQueue<Node<K, V>> expiryQueue;
    private final ReentrantLock lock;

    // Dummy head and tail for the doubly-linked list (LRU tracking)
    private final Node<K, V> head;
    private final Node<K, V> tail;

    private static class Node<K, V> {
        K key;
        V value;
        long expiryTime;
        Node<K, V> prev;
        Node<K, V> next;

        Node(K key, V value, long expiryTime) {
            this.key = key;
            this.value = value;
            this.expiryTime = expiryTime;
        }
    }

    public ConcurrentLRUCacheWithTTL(int capacity, long ttl, TimeUnit timeUnit) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be greater than 0");
        }
        this.capacity = capacity;
        this.ttlMillis = timeUnit.toMillis(ttl);
        this.map = new ConcurrentHashMap<>();
        
        // Min-heap ordered by absolute expiration time (earliest expiry at the root)
        this.expiryQueue = new PriorityQueue<>(Comparator.comparingLong(n -> n.expiryTime));
        this.lock = new ReentrantLock();

        this.head = new Node<>(null, null, 0);
        this.tail = new Node<>(null, null, 0);
        head.next = tail;
        tail.prev = head;
    }

    public V get(K key) {
        Node<K, V> node = map.get(key);
        if (node == null) {
            return null;
        }

        // Lazy Expiration Check
        if (System.currentTimeMillis() > node.expiryTime) {
            removeAndInvalidate(key, node);
            return null;
        }

        // Valid item: acquire lock to update LRU recency order
        lock.lock();
        try {
            if (map.containsKey(key) && System.currentTimeMillis() <= node.expiryTime) {
                moveToTail(node);
                return node.value;
            }
        } finally {
            lock.unlock();
        }

        return null;
    }

    public void put(K key, V value) {
        long newExpiryTime = System.currentTimeMillis() + ttlMillis;

        lock.lock();
        try {
            // 1. Purge batch-expired elements from the PriorityQueue
            purgeExpiredEntries();

            Node<K, V> node = map.get(key);

            if (node != null) {
                // Update existing node value and refresh TTL
                node.value = value;
                node.expiryTime = newExpiryTime;
                expiryQueue.add(node); // Add updated node to heap (old entry handled lazily)
                moveToTail(node);
            } else {
                // 2. Check capacity and evict LRU item if necessary
                if (map.size() >= capacity) {
                    Node<K, V> lru = head.next;
                    if (lru != tail) {
                        removeNode(lru);
                        map.remove(lru.key);
                    }
                }

                // Create and insert new node
                Node<K, V> newNode = new Node<>(key, value, newExpiryTime);
                addToTail(newNode);
                map.put(key, newNode);
                expiryQueue.add(newNode);
            }
        } finally {
            lock.unlock();
        }
    }

    // --- Expiry Management via PriorityQueue ---

    private void purgeExpiredEntries() {
        long now = System.currentTimeMillis();
        // Peek at the minimum expiry time; if expired, poll and remove from map/DLL
        while (!expiryQueue.isEmpty() && expiryQueue.peek().expiryTime <= now) {
            Node<K, V> expired = expiryQueue.poll();
            // Verify if the node is still active in the map (prevent stale heap updates)
            if (map.get(expired.key) == expired) {
                removeNode(expired);
                map.remove(expired.key);
            }
        }
    }

    private void removeAndInvalidate(K key, Node<K, V> node) {
        lock.lock();
        try {
            if (map.get(key) == node) {
                removeNode(node);
                map.remove(key);
            }
        } finally {
            lock.unlock();
        }
    }

    // --- Doubly-Linked List Helper Methods ---

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