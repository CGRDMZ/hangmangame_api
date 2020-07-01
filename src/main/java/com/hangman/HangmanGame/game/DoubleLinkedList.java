package com.hangman.HangmanGame.game;

public class DoubleLinkedList {
    private Node head;
    private Node tail;

    public DoubleLinkedList() {
    }

    public void add(Object data) {
        if (head == null && tail == null) {
            Node newNode = new Node(data);
            head = newNode;
            tail = newNode;
        } else {
            Node newNode = new Node(data);
            newNode.setLeft(tail);
            tail.setRight(newNode);
            tail = newNode;
        }
    }


    public int removeLetter(char c) {
        // i have used the below reference to implement this function.
        // [1]"Delete a node in a Doubly Linked List - GeeksforGeeks", GeeksforGeeks, 2020.
        // [Online]. Available: https://www.geeksforgeeks.org/delete-a-node-in-a-doubly-linked-list/.
        // [Accessed: 18- Jun- 2020].

        if (tail == null && head == null) {
            return -1;
        }

        if (c > 'z' || c < 'a') {
            return -1;
        }

        Node temp;
        if (c <= 'm') {
            temp = head;
            while (temp != null && (char) temp.getData() != c)
                temp = temp.getRight();
        } else {
            temp = tail;
            while (temp != null && (char) temp.getData() != c)
                temp = temp.getLeft();
        }

        if (temp == null) {
            return -1;
        }

        if (head == temp) {
            head = temp.getRight();
        }

        if (tail == temp) {
            tail = temp.getLeft();
        }

        if (temp.getRight() != null) {
            temp.getRight().setLeft(temp.getLeft());
        }

        if (temp.getLeft() != null) {
            temp.getLeft().setRight(temp.getRight());
        }

        return 0;
    }


    public String getTheLetters() {
        String s = "";

        Node temp = head;
        while (temp != null) {
            s += temp.getData();
            temp = temp.getRight();
        }

        return s;

    }

}
