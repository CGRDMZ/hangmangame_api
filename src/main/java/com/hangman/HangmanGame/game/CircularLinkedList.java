package com.hangman.HangmanGame.game;

public class CircularLinkedList {
    private Node head;

    public CircularLinkedList() {
    }

    public void add(Object data) {
        if (head == null) {
            head = new Node(data);
            head.setRight(head);
        } else {
            // adds to the begining of the list
            Node temp = head;
            //find the last element on the list
            do {
                temp = temp.getRight();
            } while (temp.getRight() != head);
            Node newNode = new Node(data);
            newNode.setRight(head);
            temp.setRight(newNode);
            head = newNode;
        }
    }

    public String getTheWord() {
        Node temp = head;
        String word = "";
        do {
            word += (char) temp.getData();
            temp = temp.getRight();
        } while (temp != head);
        return word;
    }

    public boolean revealTheletters(CircularLinkedList reference, char c) {
        Node temp = head;
        Node refTemp = reference.head;

        boolean isFound = false;
        do {
            if (c == (char) refTemp.getData() && (char) temp.getData() == '-') {
                temp.setData(refTemp.getData());
                isFound = true;
            }
            temp = temp.getRight();
            refTemp = refTemp.getRight();
        } while (temp != head && refTemp != head);

        return isFound;
    }
}
