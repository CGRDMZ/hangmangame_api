package com.hangman.HangmanGame.game;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;

public class SingleLinkedList {
    private Node head;

    public SingleLinkedList() {
    }

    public void add(String data) {
        if (head == null) {
            head = new Node(data);
        } else {
            Node previous = null;
            Node temp = head;
            while (temp != null && Integer.parseInt(((String) temp.getData()).split(";")[1]) >= Integer.parseInt((data.split(";")[1]))) {
                previous = temp;
                temp = temp.getRight();
            }
            if (temp == null) {
                previous.setRight(new Node(data));
            } else if (temp == head) {
                Node newNode = new Node(data);
                newNode.setRight(temp);
                head = newNode;
            } else {
                Node newNode = new Node(data);
                previous.setRight(newNode);
                newNode.setRight(temp);
            }
        }
    }

    public void printToFile(int maxNumberOfNodes, String filename) {

        try (FileWriter writer = new FileWriter("app.log");
             BufferedWriter bw = new BufferedWriter(writer)) {
            FileWriter fw = new FileWriter(filename);
            BufferedWriter br = new BufferedWriter(fw);

            Node temp = head;
            int counter = 0;
            while (temp != null && counter < maxNumberOfNodes) {
                String row = (String) temp.getData();
                br.write(row);
                if (temp.getRight() != null && counter != maxNumberOfNodes-1)
                    br.newLine();
                temp = temp.getRight();
                counter++;
            }
            br.flush();
        } catch (java.io.IOException e) {
            System.err.format("IOException: %s%n", e);
        }

    }

    public void display() {
        Node temp = head;
        while (temp != null) {
            String[] score = ((String) temp.getData()).split(";");
            System.out.println(score[0] + " " + score[1]);
            temp = temp.getRight();
        }
    }
}
