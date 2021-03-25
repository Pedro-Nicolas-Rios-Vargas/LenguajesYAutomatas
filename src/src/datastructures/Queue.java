package src.datastructures;

import java.lang.Iterable;
import java.util.Iterator;

public class Queue<T> implements IQueue<T>, Iterable<T>{
    private DataNode<T> head;
    private DataNode<T> tail;
    private DataNode<T> ptr;
    private int size;

    public Queue() {
        this.ptr = this.head = this.tail = null;
        this.size = 0;
    }

    @Override
    public void enqueue(Object data) {
        DataNode<T> node = new DataNode(data);
        if(this.head == null) {
            this.head = this.tail = node;
        } else {
            this.tail.next = node;
            this.tail = this.tail.next;
        }
        ++this.size;
    }

    @Override
    public T dequeue() {
        if(this.size == 0)
            return null;

        T data = this.head.data;
        this.head = this.head.next;
        --this.size;

        return data;
    }

    @Override
    public T peek() {
        if(this.size == 0)
            return null;

        T data = this.head.data;
        this.head = this.head.next;

        return data;
    }

    @Override
    public int size() {
        return this.size;
    }

    public void arrayToQueue(T array) {
        
    }

    @Override
    public Iterator<T> iterator() {
        return new QueueIterator();
    }

    private class QueueIterator implements Iterator<T> {
        private DataNode<T> ptr;

        public QueueIterator() {
            this.ptr = head;
        }

        @Override
        public boolean hasNext() {
            if(ptr.next != null)
                return true;
            return false;
        }

        @Override
        public T next() {
            T data = ptr.data;
            ptr = ptr.next;
            return data;
        }

        @Override
        public void remove() {

        }
    }
}
