package pms;
//package vn.edu.tdt.it.dsa;

import java.io.*;
import java.util.ArrayList;
import java.util.List;



public class WarehouseBook {

    protected static class WarehouseNode {
        private ProductRecord record;
        private WarehouseNode left, right;
        private int balance;


        public ProductRecord getRecord() {
            return record;
        }

        public void setRecord(ProductRecord record) {
            this.record = record;
        }

        public WarehouseNode getLeft() {
            return left;
        }

        public void setLeft(WarehouseNode left) {
            this.left = left;
        }

        public WarehouseNode getRight() {
            return right;
        }

        public void setRight(WarehouseNode right) {
            this.right = right;
        }

        public int getBalance() {
            return balance;
        }

        public void setBalance(int balance) {
            this.balance = balance;
        }
    }

    private WarehouseNode root;
    private int size;

    public int getSize() {
        return size;
    }

    public WarehouseBook() {
        root = null;
        size = 0;
    }

    public WarehouseBook(File file) throws IOException {
        int productID, quantity;
        BufferedReader br = new BufferedReader(new FileReader(file));
        String inLine = br.readLine();

        if (inLine == null)
            return;

        for (int i = 0; i < inLine.length(); i++) {
            Character ch = inLine.charAt(i);

            if (ch < 58 && ch > 47) {
                String str = inLine.substring(i, i + 5);
                productID = Integer.parseInt(str.substring(0,3));
                quantity = Integer.parseInt(str.substring(3));
                buildBST(productID, quantity);
                i = i + 5;
            }
        }
    }


    public void buildBST(int productID, int quantity) {
        WarehouseNode warehouseNode = new WarehouseNode();
        warehouseNode.record = new ProductRecord(productID, quantity);
        warehouseNode.left = warehouseNode.right = null;
        warehouseNode.balance = 1;

        if(root == null){
            root = warehouseNode;
            return;
        }

        WarehouseNode currentNode = root;
        WarehouseNode parentNode = null;

        while (true){
            parentNode = currentNode;

            if(currentNode.record.getProductID() > productID){
                currentNode = currentNode.getLeft();

                if(currentNode == null){
                    parentNode.setLeft(warehouseNode);
                    return;
                }
            }else {
                currentNode = currentNode.getRight();

                if(currentNode == null){
                    parentNode.setRight(warehouseNode);
                    return;
                }
            }
        }
    }

    public void save(File file) {
        try {
            if(!file.exists()){
                file.createNewFile();
            }

            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            bw.write(toString());
            System.out.println(toString());

            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<String> getAllNodes(WarehouseNode node, List<String> list){
        if(node != null){
            list.add(node.getRecord().toString());
            list.add("(");
            getAllNodes(node.left, list);
            getAllNodes(node.right, list);
            list.add(")");
        }else {
            list.add("N");
        }

        return list;
    }

    public void display(WarehouseNode root){
        if(root!=null){
            System.out.print(" " + root.getRecord().toString());
            display(root.left);
            display(root.right);
        }
    }

    public void process(File file) throws IOException {
        List<String> eventList = new ArrayList<String>();

        BufferedReader br = new BufferedReader(new FileReader(file));
        String inLine = br.readLine();
        br.close();

        if (inLine == null)
            return;

        String [] events = inLine.split(" ");

        for(int i = 0; i < events.length; i++){
            eventList.add(events[i]);
        }

        process(eventList);
    }

    public void process(List<String> events) {

        foo:
        for(int i = 0; i < events.size(); i++){
            if(events.get(i).equals("0")){
                break;
            }

            for(int j = 0; j < events.get(i).length(); j++){
                char key = events.get(i).charAt(j);

                switch (key){
                    case '1':
                        addProduct(events.get(i).substring(j+1));
                        continue foo;

                    case '2':
                        descProduct(events.get(i).substring(j+1));
                        continue foo;

                    case '3':
                        List<String> resultInOrder = new ArrayList<String>();
                        inOrderTraversal(root, resultInOrder);
                        int mid = (int)(resultInOrder.size()/2);
                        root = new WarehouseNode();
                        buildSimpleAVL(resultInOrder, mid);
                        continue foo;

                    case '4':
                        List<String> resultPreOder = new ArrayList<String>();
                        resultPreOder = preOrderTraversal(root, resultPreOder);
                        root = null;

                        for(int h = 0; h < resultPreOder.size(); h ++){
                            root = insertAVL(resultPreOder.get(h), root);
                        }
                        continue foo;

                    case '5':
                        List<String> resultPostOder = new ArrayList<String>();
                        resultPostOder = postOder(root, resultPostOder);
                        reBulidBst(events.get(i), resultPostOder);
                        continue foo;

                    case '6':
                        int hight = Integer.parseInt(events.get(i).substring(1));
                        int count = 1;
                        WarehouseNode newNode = new WarehouseNode();
                        newNode.setRecord(root.getRecord());
                        newNode = deleteNodeByHigh(root, 1, hight);
                        continue foo;
                }
            }
        }

    }

    public void reBulidBst(String event, List<String> resultPostOder){
        int productID = Integer.parseInt(event.substring(1, 4));
        int quantity = Integer.parseInt(event.substring(4));

        for(int i = 0; i < resultPostOder.size(); i++){
            String tmp = resultPostOder.get(i).substring(0, 3);
            if(tmp.equals(String.valueOf(productID))){
                quantity += Integer.parseInt(resultPostOder.get(i).substring(3));
                resultPostOder.remove(i);
            }
        }

        root = new WarehouseNode();
        root.setRecord(new ProductRecord(productID, quantity));
        root.left = root.right = null;

        for(int i = 0; i < resultPostOder.size(); i++){
            productID = Integer.parseInt(resultPostOder.get(i).substring(0, 3));
            quantity = Integer.parseInt(resultPostOder.get(i).substring(3));
            buildBST(productID, quantity);
        }
    }

    public void buildSimpleAVL(List<String> result, int mid){
        int productID = Integer.parseInt(result.get(mid).substring(0, 3));
        int quantity = Integer.parseInt(result.get(mid).substring(3));
        root.setRecord(new ProductRecord(productID, quantity));

        WarehouseNode leftRoot = null;
        WarehouseNode rightRoot = null;

        for(int k = 0; k < result.size(); k ++){
            if(k < mid ){
                leftRoot = insertAVL(result.get(k), leftRoot);
            }
            if(k > mid){
                rightRoot = insertAVL(result.get(k), rightRoot);
            }
        }

        root.left = leftRoot;
        root.right = rightRoot;
    }


    public WarehouseNode deleteNodeByHigh(WarehouseNode root, int count, int hight){
        if(root != null && count < hight - 1){
            deleteNodeByHigh(root.getRight(), count + 1, hight);

            deleteNodeByHigh(root.getLeft(), count + 1, hight);
            return root;
        }
        if(root == null){
            return root;
        }

        if(root.left != null){
            root.left = null;
        }
        if(root.right != null){
            root.right = null;
        }

        return root;
    }

    public List<String> postOder(WarehouseNode root, List<String> result){
        if(root != null){
            postOder(root.getRight(), result);
            postOder(root.getLeft(), result);

            String tmp = String.valueOf(root.getRecord().getQuantity());
            if (tmp.length() < 2)
                tmp = "0" + tmp;

            result.add(String.valueOf(root.getRecord().getProductID()) + tmp);
        }

        return result;
    }

    public List<String> preOrderTraversal(WarehouseNode root, List<String> result){
        if(root != null){
            String tmp = String.valueOf(root.getRecord().getQuantity());
            if(tmp.length() < 2)
                tmp = "0" + tmp;

            result.add(String.valueOf(root.getRecord().getProductID()) + tmp);
            preOrderTraversal(root.getLeft(), result);
            preOrderTraversal(root.getRight(), result);
        }

        return result;
    }
    private int height(WarehouseNode node) {
        return node == null ? -1 : node.balance;
    }

    public WarehouseNode insertAVL(String data, WarehouseNode root) {
        int productID = Integer.parseInt(data.substring(0, 3));
        int quantity = Integer.parseInt(data.substring(3));

        WarehouseNode newNode = new WarehouseNode();
        newNode.balance = 0;
        newNode.setRecord(new ProductRecord(productID, quantity));
        newNode.left = null;
        newNode.right = null;

        if (root == null){
            root = newNode;
        }
        else if (productID < root.getRecord().getProductID()) {
            root.left = insertAVL(data, root.left);
            if (height(root.left) - height(root.right) == 2)
                if (productID < root.left.getRecord().getProductID())
                    root = rotateWithLeftChild(root);
                else
                    root = doubleWithLeftChild(root);
        } else if (productID > root.getRecord().getProductID()) {
            root.right = insertAVL(data, root.right);
            if (height(root.right) - height(root.left) == 2)
                if (productID > root.right.getRecord().getProductID())
                    root = rotateWithRightChild(root);
                else
                    root = doubleWithRightChild(root);
        } else ;
        root.balance = max(height(root.left), height(root.right)) + 1;
        return root;
    }

    private WarehouseNode doubleWithRightChild(WarehouseNode root) {
        root.right = rotateWithLeftChild(root.right);
        return rotateWithRightChild(root);
    }

    private WarehouseNode doubleWithLeftChild(WarehouseNode root) {
        root.left = rotateWithRightChild(root.left);
        return rotateWithLeftChild(root);
    }

    private WarehouseNode rotateWithRightChild(WarehouseNode root) {
        WarehouseNode newNode = root.right;
        root.right = newNode.left;
        newNode.left = root;
        root.balance = max(height(root.left), height(root.right)) + 1;
        newNode.balance = max(height(newNode.right), root.balance) + 1;
        return newNode;
    }

    private WarehouseNode rotateWithLeftChild(WarehouseNode root) {
        WarehouseNode newNode = root.left;
        root.left = newNode.right;
        newNode.right = root;
        root.balance = max(height(root.left), height(root.right)) + 1;
        newNode.balance = max(height(newNode.left), root.balance) + 1;
        return newNode;
    }

    private int max(int lhs, int rhs) {
        return lhs > rhs ? lhs : rhs;
    }

    public void printList(List<String> list){
        for(int i = 0; i < list.size(); i++){
            System.out.print(" " + list.get(i));
        }
    }

    public List<String> inOrderTraversal(WarehouseNode root, List<String> result) {
        if(root != null){
            //helper(root, result);
            if(root.left != null)
                inOrderTraversal(root.left, result);

            String tmp = String.valueOf(root.getRecord().getQuantity());
            if(tmp.length() < 2)
                tmp = "0" + tmp;

            result.add(String.valueOf(root.getRecord().getProductID()) + tmp);

            if(root.right != null)
                inOrderTraversal(root.right, result);
        }

        return result;
    }

    public void descProduct(String node){
        int productID = Integer.parseInt(node.substring(0, 3));
        int quantity = Integer.parseInt(node.substring(3));
        List<Integer> productList = new ArrayList<Integer>();

        productList = getAllProductID(root, productList);
        int [] subArray = new int[productList.size()];

        for(int i = 0; i < subArray.length; i++){
            subArray[i] = Math.abs(productList.get(i) - productID);
        }

        int min = subArray[0];
        int index = 0;

        for(int i = 0; i < subArray.length; i++){
            if(subArray[i] < min){
                min = subArray[i];
                index = i;
            }
        }

        for(int i = index + 1; i < subArray.length; i++){
            if(subArray[i] == min){
                if(productList.get(index) > productList.get(i))
                    index = i;
            }
        }

        productID = productList.get(index);
        WarehouseNode currentNode = root;

        while (true){
            if(currentNode.getRecord().getProductID() == productID){
                int tmp = currentNode.getRecord().getQuantity() - quantity;
                if(tmp <= 0){
                   root = deleteNode(root, productID);
                }else {
                    currentNode.getRecord().setQuantity(tmp);
                }

                break;
            }

            if(currentNode.getRecord().getProductID() > productID){
                currentNode = currentNode.left;
                continue;
            }

            if(currentNode.getRecord().getProductID() < productID){
                currentNode = currentNode.right;
                continue;
            }
        }

    }

    public WarehouseNode deleteNode(WarehouseNode root, int productID)
    {
        if (root == null)
            return root;

        if (productID < root.getRecord().getProductID())
            root.left = deleteNode(root.left, productID);
        else if (productID > root.getRecord().getProductID())
            root.right = deleteNode(root.right, productID);
        else
        {
            if (root.left == null)
                return root.right;
            else if (root.right == null)
                return root.left;
            root.setRecord(new ProductRecord(minValue(root.right), root.right.getRecord().getQuantity()));

            root.right = deleteNode(root.right, root.getRecord().getProductID());
        }

        return root;
    }

    int minValue(WarehouseNode root)
    {
        int minv = root.getRecord().getProductID();
        while (root.left != null)
        {
            minv = root.left.getRecord().getProductID();
            root = root.left;
        }
        return minv;
    }

    public List<Integer> getAllProductID(WarehouseNode root, List<Integer> productList){
        if(root != null){
            productList.add(root.getRecord().getProductID());
            getAllProductID(root.getLeft(), productList);
            getAllProductID(root.getRight(), productList);
        }

        return productList;
    }

    public void addProduct(String node){
        int productID = Integer.parseInt(node.substring(0, 3));
        int quantity = Integer.parseInt(node.substring(3));

        WarehouseNode currentNode = root;
        WarehouseNode parentNode = null;

        WarehouseNode newNode = new WarehouseNode();
        newNode.record = new ProductRecord(productID, quantity);
        newNode.left = newNode.right = null;
        newNode.balance = 1;

        if (root == null){
            root = newNode;
            return;
        }

        while (true){
            parentNode = currentNode;

            if(currentNode.getRecord().getProductID() == productID){
                int tmp = currentNode.getRecord().getQuantity() + quantity;
                currentNode.getRecord().setQuantity(tmp);
                return;
            }

            if(currentNode.getRecord().getProductID() > productID){
                currentNode = currentNode.getLeft();
                if(currentNode == null){
                    parentNode.left = newNode;
                    return;
                }
            }else {
                currentNode = currentNode.getRight();
                if(currentNode == null){
                    parentNode.right = newNode;
                    return;
                }
            }
        }
    }



    @Override
    public String toString() {
        String res = "";

        if(root == null)
            return "";

        List<String> list = new ArrayList<String>();
        list = getAllNodes(root, list);

        res += "";
        for(int i = 0; i < list.size(); i++){
            if(list.get(i).equals("(") && list.get(i+1).equals("N") && list.get(i+2).equals("N")){
                for(int j = 0; j < 4; j ++){
                    list.remove(i);
                }
            }
        }

        for(int i = 0; i < list.size(); i++){
            res += list.get(i);
            if((i+1) < list.size() && (list.get(i).equals("(") || list.get(i+1).equals(")")))
                continue;

            if(i + 1 == list.size())
                break;

            res += " ";
        }
        res += "";

        return res;
    }

    public static void main(String[] args) {
        try {
            WarehouseBook wb = new WarehouseBook(new File("warehouse.txt"));
            System.out.print("List Product Before Event: ");
            wb.display(wb.root);

            BufferedReader br = new BufferedReader(new FileReader(new File("events.txt")));
            System.out.print("\nEvents: " + br.readLine());
            br.close();

            wb.process(new File("events.txt"));
            System.out.print("\nList Product After Event: ");
            wb.display(wb.root);

            System.out.print("\nList Final:");
            wb.save(new File("warehouse_new.txt"));
            System.out.println("\nDone!!!");
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }
}
