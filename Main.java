/*
* Design of Algorithms
* Huffman Coding
* Arman Ghoreshi
* Ali Goldani
* **/


import java.io.*;
import java.util.*;

public class Main {

    public static String[] huffmanCodes= new String [256];
    static int extraEncode =0;

    public static void main(String[] args) {
        String Directory = "test_files/tolstoy.txt";
        String OutPutDirectory="output/tolstoy.dat";
        String result="File Input";
        String encodedString;
        int [] count = new int[256];

        try {
            result = TextFileToString(Directory);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        System.out.println("================INPUT  STRING================");
        System.out.println(result);
        count = charCounts(result);
        System.out.println("=============================================");
        System.out.println("\n");
//        for (int val :count) {
//            System.out.print(val+" ");
//        }

        ArrayList<Node> tempArrayList = intToNode(count);
//        for (Node val :tempArrayList) {
//            System.out.print(val.value + " ");
//        }

        Node root = MakeTree(tempArrayList);

        System.out.println("================IN-ORDER TREE================");
        root.printTree(root);
        System.out.println("=============================================");
        System.out.println("\n");




        String temp = "";
        System.out.println("====================KEYS=====================");
        generateKeys(root,temp);
        System.out.println("=============================================");
        System.out.println("\n");


        try {
            writeToFile(OutPutDirectory,result);
        } catch (IOException e) {
            e.printStackTrace();
        }


        encodedString = encodeHuffman(OutPutDirectory);
        decodeHuffman(encodedString,root);
    }

    public static int[] charCounts(String s) {
        int[] counts = new int[256]; // maximum value of an ASCII character
        char[] c = s.toCharArray();
        for (int i = 0; i < c.length; ++i) {
            counts[c[i]]++;
        }
        return counts;
    }

    public static String TextFileToString(String directory) throws FileNotFoundException {
        String temp = "";
        Scanner fileReader = null;
        BufferedReader bufferedReader = null;
        fileReader = new Scanner(new File(directory));

        while (fileReader.hasNext()) {
            temp = temp.concat(fileReader.nextLine());
        }

        return temp;
    }

    public static Node MakeTree(ArrayList<Node> input){
        if(input.size()==1)
            return input.get(0);
        else {
            Node parent = new Node(input.get(input.size() - 1).value + input.get(input.size() - 2).value);
            parent.leaf = false;
            parent.left = input.get(input.size() - 1);
            parent.right = input.get(input.size() - 2);
            input.remove(input.size() - 2);
            input.remove(input.size() - 1);
            input.add(parent);
            Collections.sort(input, new Comparator<Node>() {
                @Override
                public int compare(Node n1, Node n2) {
                    if (n1.value == n2.value)
                        return 0;
                    else if (n1.value < n2.value)
                        return 1;
                    else
                        return -1;
                }
            });
            MakeTree(input);
        }
        return input.get(0);
    }

    public static ArrayList<Node> intToNode(int[] input){
        ArrayList<Node> tempList = new ArrayList<Node>();
        for (int i = 0; i < input.length; i++) {
            if(input[i]!=0) {
                Node temp = new Node(input[i]);
                temp.character = (char) i;
                temp.leaf = true;
                tempList.add(temp);

            }
        }
        Collections.sort(tempList, new Comparator<Node>() {
            @Override
            public int compare(Node n1, Node n2) {
                if(n1.value == n2.value)
                    return 0;
                else if(n1.value < n2.value)
                    return 1;
                else
                    return -1;
            }
        });

        return tempList;
    }

    public static void generateKeys(Node node,String temp) {
        if(node!=null){
            if(node.left!=null)
                generateKeys(node.left,temp+"0");

            if(node.left!=null)
                generateKeys(node.right,temp+"1");

            if(node.leaf){
                huffmanCodes[node.character]=temp;
                System.out.println("\'"+ node.character + "\'" + ":" + temp);
            }

        }
    }

    public static void writeToFile(String outputdirectory , String s) throws IOException {
        OutputStream fos = null;
        try {
            fos = new FileOutputStream(outputdirectory);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        int i = 0;
        byte tempByte;
        byte[] arrayOfByts = new byte[1];
        String str_temp="";
        int remainingBits=8;
        try {


            while (true) {

                if (remainingBits - huffmanCodes[s.charAt(i)].length() > 0) {
                    str_temp += huffmanCodes[s.charAt(i)];
                    remainingBits = remainingBits - huffmanCodes[s.charAt(i)].length();

                } else if (remainingBits - huffmanCodes[s.charAt(i)].length() < 0) {
                    int temp = Math.abs(remainingBits - huffmanCodes[s.charAt(i)].length());
                    int j = 0;
                    for (int a = 0; a < ((int) temp / 8) + 1; a++) {
                        for (; j < remainingBits; j++) {
                            str_temp += huffmanCodes[s.charAt(i)].charAt(j);
                        }
                        arrayOfByts[0] = (byte) Integer.parseInt(str_temp, 2);
                        fos.write(arrayOfByts);
                        str_temp = "";
                        remainingBits = 8;
                    }
                    for (int k = j; k < huffmanCodes[s.charAt(i)].length(); k++) {
                        str_temp += huffmanCodes[s.charAt(i)].charAt(k);
                        remainingBits--;
                    }
                } else {
                    for (int j = 0; j < huffmanCodes[s.charAt(i)].length(); j++) {
                        str_temp += huffmanCodes[s.charAt(i)].charAt(j);
                    }
                    arrayOfByts[0] = (byte) Integer.parseInt(str_temp, 2);
                    fos.write(arrayOfByts);
                    str_temp = "";
                    remainingBits = 8;
                }
                i++;
            }
        }catch (IndexOutOfBoundsException e){
            extraEncode =8-str_temp.length();
            arrayOfByts[0] = (byte) (Integer.parseInt(str_temp, 2)<< extraEncode);
            fos.write(arrayOfByts);
//            System.out.println(e.getStackTrace());
            fos.close();
        }
        fos.close();
    }


    public static void decodeHuffman(String encodedString, Node root){
        System.out.println("===============DECODED  STRING===============");
        String decodedString="";
        Node tempNode = null;
        int counter = 0;
        while (counter < encodedString.length()) {
            tempNode = root;
            while (counter < encodedString.length() && !tempNode.leaf) {
                if (encodedString.charAt(counter) == '0'){
                    tempNode = tempNode.left;
                    counter++;
                }
                else if (counter < encodedString.length() && encodedString.charAt(counter) == '1'){
                    tempNode = tempNode.right;
                    counter++;
                }
            }
            if (tempNode!=null)
                System.out.print(tempNode.character);
        }
        System.out.println("");
        System.out.println("=============================================");

    }

    public static String encodeHuffman(String directory){
        InputStream fis=null;
        int temp=0;
        String encoded="";
        String tempS;
        try {
            fis = new FileInputStream(directory);

        }catch (Exception e){

        }

        while (true){
            //System.out.println("while");
            try {
                temp = fis.read();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (temp != -1) {
                tempS = Integer.toBinaryString(temp);
                while (tempS.length()<8){
                    tempS = ("0"+tempS);
                }
                encoded += tempS;
            }
            else break;
        }
        encoded = encoded.substring(0,encoded.length()- extraEncode);
        System.out.println("===============ENCODED  STRING===============");
        System.out.println(encoded);
        System.out.println("=============================================");
        System.out.println("\n");
        return encoded;
    }

}
