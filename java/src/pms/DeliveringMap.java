package pms;
//package vn.edu.tdt.it.dsa;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class DeliveringMap {
    class Node {
        int distance;
        int checkPlace;

        public Node(int distance, int checkPlace) {
            this.distance = distance;
            this.checkPlace = checkPlace;
        }
    }

    class Street {
        int source;
        int destination;
        List<Node> distanceList;

        public Street() {
            source = destination = 0;
            distanceList = new ArrayList<Node>();
        }
    }

    private Street street;
    private boolean[] check;
    private int[][] graph;
    private int size;
    private int src, des;
    private int len;
    private int totalDistance;
    private ArrayList<Integer> list;
    private List<Integer> minimumSpanningTree;
    List<String> containNameNode;

    public DeliveringMap(File file) throws IOException {
        list = new ArrayList<Integer>();
        street = new Street();
        size = 0;
        totalDistance = 0;
        minimumSpanningTree = new ArrayList<Integer>();
        containNameNode = new ArrayList<String>();

        String map = readFile(file);
        if (map == null)
            return;

        graph = buildNode(map.split(" "));
        len = graph.length;
        check = new boolean[graph.length];
        Arrays.fill(check, false);
    }

    private int[][] buildNode(String[] metric) {

        String src, des;

        //add place_id to list
        for (int i = 0; i < metric.length; i++) {
            src = metric[i].substring(0, 2);
            des = metric[i].substring(5);
            this.totalDistance += Integer.parseInt(metric[i].substring(2, 5));
            containNameNode.add(src);
            containNameNode.add(des);
        }

        this.src = 0;
        this.des = Integer.parseInt(containNameNode.get(containNameNode.size() - 1));

        //remove duplicate values in list
        for (int i = 0; i < containNameNode.size(); i++) {
            for (int j = i + 1; j < containNameNode.size(); j++) {
                if (containNameNode.get(j).equals(containNameNode.get(i))) {
                    containNameNode.remove(j);
                    j--;
                }
            }
        }

        int[][] graph = new int[containNameNode.size()][containNameNode.size()];

        for (int i = 0; i < graph.length; i++) {
            for (int j = 0; j < graph.length; j++) {
                graph[i][j] = -3; //-3
            }
        }
        //change place_id
        int distance;
        int S = 0, D = 0; //Source, Destination is saved in A Node

        foo:
        for (int i = 0; i < metric.length; i++) {
            src = metric[i].substring(0, 2);
            des = metric[i].substring(5);
            distance = Integer.parseInt(metric[i].substring(2, 5));

            for (int j = 0; j < containNameNode.size(); j++) {
                if (src.equals(containNameNode.get(j))) {
                    S = j;
                    break;
                }
            }

            for (int j = 0; j < containNameNode.size(); j++) {
                if (des.equals(containNameNode.get(j))) {
                    D = j;
                    break;
                }
            }

            graph[S][D] = distance;
            graph[D][S] = distance;
        }

        for (int j = 0; j < containNameNode.size(); j++) {
            if (this.des == Integer.parseInt(containNameNode.get(j))) {
                this.des = j;
                break;
            }
        }

        return graph;
    }

    public String readFile(File file) throws IOException {
        if (!file.exists())
            file.createNewFile();

        BufferedReader br = new BufferedReader(new FileReader(file));
        String inLine;
        String input;

        input = br.readLine();
        if (input == null)
            return null;

        while ((inLine = br.readLine()) != null) {
            input += " " + inLine;
        }

        br.close();
        return input;
    }

    public int calculate(int level, boolean rushHour) {
        int res = 0;
        String order = "MIN";
        street.source = this.src;
        street.destination = this.des;

        if (level < 9) {
            dfs(this.src, this.des);

            if (street.distanceList.size() == 0)
                return 0;
        }

        switch (level) {
            case 1:
            case 0:
                res = level + this.len + this.totalDistance;
                break;

            case 2:
            case 3:
            case 4:
                res = processFromTwoToFourLevel(level, rushHour, order);
                break;

            case 5:
            case 6:
                res = processFromFiveToSixLevel(level, rushHour);
                break;

            case 7:
                res = processSevenLevel(level, rushHour);
                break;

            case 9:
                res = processNineLevel(level, rushHour);
                break;
        }
        return res;
    }

    public int processNineLevel(int level, boolean rushHour) {
        if (rushHour)
            return -3;

        for (int i = 0; i < len; i++) {
            for (int j = 0; j < len; j++) {
                if (i == j)
                    continue;

                this.src = i;
                this.des = j;
                list = new ArrayList<Integer>();
                size = 0;
                Arrays.fill(check, false);

                dfs(this.src, this.des);
            }
        }

        int res = minimumSpanningTree.get(0);
        for (int i = 1; i < minimumSpanningTree.size(); i++) {
            if (minimumSpanningTree.get(i) < res) {
                res = minimumSpanningTree.get(i);
            }
        }

        return res;
    }

    public int processSevenLevel(int level, boolean rushHour) {
        int res = street.distanceList.get(getIndex(street.distanceList, "MAX", true, false)).distance;
        if (res == 99) {
            res = 99 - 70 * level;
            return 0;
        }

        res += street.distanceList.get(getIndex(street.distanceList, "MIN", true, false)).distance;
        res = res / 2;
        if (res - 99 == 99) {
            res = 99 - 70 * level;
            return 0;
        }

        if (res >= 30 * level)
            res = -21;

        return res;
    }

    public int processFromFiveToSixLevel(int level, boolean rushHour) {
        int res = street.distanceList.get(getIndex(street.distanceList, "MAX", true, true)).distance;
        if (res > 100 * level)
            res = -level;
        if (res == 99) {
            res = 99 - 70 * level;
        }
        return res;
    }

    public int processFromTwoToFourLevel(int level, boolean rushHour, String order) {
        int res = 0;
        order = "MIN";

        res = street.distanceList.get(getIndex(street.distanceList, order, true, true)).distance;
        if (res > 100 * level)
            res = (50 * level - res);
        if (res == 99) {
            res = 99 - 70 * level;
        }
        return res;
    }

    //order = min or max | check : don't enter block at 270 line when recall method getIndex |
    // haveCoca: is have a Freshwater shop?
    // check = true - if have a Freshwater shop, find all roads through Freshwater shop and add their distances to an arrayList
    // check = false - find a short street in them.(don't find all roads through Freshwater shop again)
    private int getIndex(List<Node> list, String order, boolean check, boolean haveCoca) {
        int result = list.get(0).distance;
        int index = 0;

        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).checkPlace == 1 && check && haveCoca) {
                List<Node> newList = new ArrayList<Node>();
                newList.add(new Node(list.get(i).distance, i));
                for (int j = i + 1; j < list.size(); j++) {
                    if (list.get(j).checkPlace == 1) {
                        newList.add(new Node(list.get(j).distance, j));
                    }
                }

                index = getIndex(newList, order, false, haveCoca);
                return newList.get(index).checkPlace;
            }

            if (order.equals("MIN")) {
                if (list.get(i).distance < result) {
                    result = list.get(i).distance;
                    index = i;
                }
            } else {
                if (list.get(i).distance > result) {
                    result = list.get(i).distance;
                    index = i;
                }
            }

        }

        return index;
    }

    private void dfs(int src, int dst) {

        this.list.add(src);
        size++;
        check[src] = true;

        if (src == dst) {
            int total = 0;
            int checkPoint = 0;

            //Level 9
            for (int i = 0; i < list.size(); i++) {
              //  System.out.print(list.get(i) + " ");
                if (i + 1 == list.size())
                    break;

                int tmp = graph[list.get(i)][list.get(i + 1)];

                if (tmp == 0) {
                    checkPoint = 1;
                }
                if (tmp == 99) {
                    checkPoint = -1;
                }

                total += graph[list.get(i)][list.get(i + 1)];
            }
          //  System.out.println("total = " + total);
            street.distanceList.add(new Node(total, checkPoint));

            if (list.size() == this.len) {
                minimumSpanningTree.add(total);
            }

            return;
        }

        for (int i = 0; i < len; i++) {
            if (graph[src][i] >= 0) {
                if (check[i] == false) {
                    dfs(i, dst);
                    check[i] = false;
                    size--;
                    list.remove(size);
                }
            }
        }
    }

    public static void main(String[] args) {
        try {
            DeliveringMap map = new DeliveringMap(new File("map.txt"));
            System.out.println(map.calculate(9, false));

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
