package src.datastructures;

public class DataNode<T> {
    public T data;
    public DataNode<T> next;

    public DataNode(T data) {
        this.data = data;
        this.next = null;
    }

}
