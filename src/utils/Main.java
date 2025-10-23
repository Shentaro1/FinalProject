package utils;

import managers.HistoryManager;
import managers.InMemoryTaskManager;
import tasks.AbstractTask;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        InMemoryTaskManager tm = new InMemoryTaskManager(new HistoryManager() {
            @Override
            public void add(AbstractTask task) {

            }

            @Override
            public ArrayList<AbstractTask> getHistory() {
                return null;
            }

            @Override
            public void remove(int id) {

            }
        });
    }
}
