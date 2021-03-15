package src.datastructures;

public class Queue<T> implements IQueue<T>{
    private DataNode<T> head;
    private DataNode<T> tail;
    private int size;

    public Queue() {
        this.head = this.tail = null;
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
}
