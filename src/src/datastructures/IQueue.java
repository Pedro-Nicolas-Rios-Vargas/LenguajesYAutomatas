package src.datastructures;

public interface IQueue<T> {
    public void enqueue(T data);
    public T dequeue();
    public T peek();
    public int size();
}
