import java.util.Comparator;

/**
 * Created by arman on 5/24/2018 AD.
 */
public class Node {
    public int value;
    public char character;
    public Node right;
    public Node left;
    public boolean leaf;

    public Node(int value){
        this.value=value;
    }

    public void printTree(Node node){
        if (node != null) {
            printTree(node.left);
            System.out.println("\'" + node.character + "\'" + ":" + node.value);
            printTree(node.right);
        }
    }
}
