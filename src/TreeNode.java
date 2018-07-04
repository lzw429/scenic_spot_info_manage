import java.io.Serializable;
import java.util.List;

public class TreeNode<T> implements Serializable {
    TreeNode parentNode;
    List<TreeNode> childList;
    T value;

    public TreeNode getParentNode() {
        return parentNode;
    }

    public void setParentNode(TreeNode parentNode) {
        this.parentNode = parentNode;
    }

    public List<TreeNode> getChildList() {
        return childList;
    }

    public void setChildList(List<TreeNode> childList) {
        this.childList = childList;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    TreeNode(T value) {
        this.value = value;
    }
}
