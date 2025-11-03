package managers;
import tasks.AbstractTask;
import java.util.ArrayList;
import java.util.HashMap;

public class InMemoryHistoryManager implements HistoryManager {
    private static class Node {
        AbstractTask<?> item;
        Node next;
        Node prev;

        Node(Node prev, AbstractTask<?> item, Node next) {
            this.item = item;
            this.next = next;
            this.prev = prev;
        }
    }

    private Node head;
    private Node tail;
    private final HashMap<Integer, Node> hashTable;

    public InMemoryHistoryManager() {
        this.hashTable = new HashMap<>();
    }

    public void add(AbstractTask<?> task) {
        linkLast(task);
    }

    public void remove(int id) {
        Node x;
        if ((x = hashTable.remove(id)) != null)
            removeNode(x);
    }

    private void linkLast(AbstractTask<?> e) {
        final Node l = tail;
        final Node newNode = new Node(l, e, null);
        tail = newNode;

        if (l == null)
            head = newNode;
        else
            l.next = newNode;

        Node prevNode;

        if ((prevNode = hashTable.put(e.getId(), newNode)) != null)
            removeNode(prevNode);
    }

    private void removeNode(Node x) {
        final AbstractTask<?> element = x.item;
        final Node next = x.next;
        final Node prev = x.prev;

        if (prev == null) {
            head = next;
        } else {
            prev.next = next;
            x.prev = null;
        }

        if (next == null) {
            tail = prev;
        } else {
            next.prev = prev;
            x.next = null;
        }

        x.item = null;
    }

    public ArrayList<AbstractTask<?>> getTasks() {
        ArrayList<AbstractTask<?>> resultList = new ArrayList<>();
        for (Node x = head; x != null; x = x.next)
            resultList.add(x.item.copy());
        return resultList;
    }
}
