package managers;

import tasks.AbstractTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class InMemoryHistoryManager<T> implements HistoryManager {
    private Node<T> head;
    private Node<T> tail;
    private int size;
    private Map<Integer, Node<T>> history = new HashMap<>();

    @Override
    public void add(AbstractTask abstractTask) {
        linkLast(abstractTask);
    }

    @Override
    public void remove(int id) {
        removeNode(id);
    }

    @Override
    public ArrayList<AbstractTask> getHistory() {
        ArrayList<AbstractTask> historyList = new ArrayList<>(history.size());
        Node<T> current = head;
        while (current != null) {
            historyList.add(current.data.copy());
            current = current.next;
        }
        return historyList;
    }

    private static class Node<T> {
        public AbstractTask data;
        Node<T> next;
        Node<T> prev;

        public Node(AbstractTask data) {
            this.data = data;
            this.prev = null;
            this.next = null;
        }
    }

    private void linkLast(AbstractTask abstractTask) {
        if (abstractTask == null) {
            return;
        }

        if (history.containsKey(abstractTask.getId())) {
            remove(abstractTask.getId());
        }

        Node<T> newNode = new Node<>(abstractTask);

        if (head == null) {
            head = newNode;
            tail = newNode;
        } else {
            tail.next = newNode;
            newNode.prev = tail;
            tail = newNode;
        }
        size++;
        history.put(abstractTask.getId(), newNode);
    }

    private void removeNode(int id) {
        Node<T> nodeToRemove = history.get(id);

        if (nodeToRemove == null || head == null) {
            return;
        }

        if (nodeToRemove == head && nodeToRemove == tail) {
            head = null;
            tail = null;
        } else if (nodeToRemove == head) {
            head = head.next;
            head.prev = null;
        } else if (nodeToRemove == tail) {
            tail = tail.prev;
            tail.next = null;
        } else {
            nodeToRemove.prev.next = nodeToRemove.next;
            nodeToRemove.next.prev = nodeToRemove.prev;
        }

        nodeToRemove.next = null;
        nodeToRemove.prev = null;

        history.remove(id);
        size--;
    }
}
