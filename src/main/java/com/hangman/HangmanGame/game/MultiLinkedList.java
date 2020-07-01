package com.hangman.HangmanGame.game;

public class MultiLinkedList {
    private static int counter = 0;
    private Node head;

    public MultiLinkedList() {
    }

//    public void addWord(int letterCount, String word) {
//        if (head == null) {
//            head = new Node(1);
//        }
//        Node temp = head;
//        for (int i = 2; i <= letterCount; i++) {
//            if (temp.getDown() == null) {
//                temp.setDown(new Node(i));
//            }
//            temp = temp.getDown();
//        }
//        if ((int) temp.getData() == letterCount) {
//            if (temp.getRight() == null) {
//                temp.setRight(new Node(word));
//            } else {
//                Node previous = temp;
//                temp = temp.getRight();
//                while (temp != null && word.charAt(0) > ((String) temp.getData()).charAt(0)) {
//                    previous = temp;
//                    temp = temp.getRight();
//                }
//                if (temp != null) {
//                    Node newNode = new Node(word);
//                    newNode.setRight(temp.getRight());
//                    previous.setRight(newNode);
//                } else {
//                    previous.setRight(new Node(word));
//                }
//            }
//
//
//        }
//    }
    public void addWord(int letterCount, String word) {
        if (head == null) {
            head = new Node(1);
        }
        Node temp = head;
        for (int i = 2; i <= letterCount; i++) {
            if (temp.getDown() == null) {
                temp.setDown(new Node(i));
            }
            temp = temp.getDown();
        }
        if ((int) temp.getData() == letterCount) {
            if (temp.getRight() == null) {
                temp.setRight(new Node(word));
            } else {
                Node headTemp = temp;
                temp = temp.getRight();
                Node previous = null;
                while (temp != null && word.charAt(0) > ((String) temp.getData()).charAt(0)) {
                    previous = temp;
                    temp = temp.getRight();
                }
                if (temp == null) {
                    previous.setRight(new Node(word));
                } else if (temp == headTemp.getRight()) {
                    Node newNode = new Node(word);
                    newNode.setRight(temp);
                    headTemp.setRight(newNode);
                } else {
                    Node newNode = new Node(word);
                    previous.setRight(newNode);
                    newNode.setRight(temp);
                }
            }


        }
    }

    public Object getWord(int index) {
        Node temp = head;
        Node tempWord;
        int counter = 0;
        while(temp != null) {
            if (temp.getRight() != null) {
                tempWord = temp.getRight();
                while (tempWord != null) {
                    counter++;
                    if (counter == index) {
                        return tempWord.getData();
                    }
                    tempWord = tempWord.getRight();
                }
            }
            temp = temp.getDown();
        }
        System.out.println(counter + " " + index);
        return null;
    }
}
