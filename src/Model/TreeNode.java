package Model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class TreeNode<T> implements Serializable {
    private T val;
    private TreeNode<T> firstChild;
    private TreeNode<T> nextSibling;
    private TreeNode<T> parent;

    TreeNode(T val) {
        this.val = val;
    }

    public TreeNode<T> getFirstChild() {
        return firstChild;
    }

    public void setFirstChild(TreeNode<T> firstChild) {
        this.firstChild = firstChild;
    }

    public TreeNode<T> getNextSibling() {
        return nextSibling;
    }

    public void setNextSibling(TreeNode<T> nextSibling) {
        this.nextSibling = nextSibling;
    }

    public T getVal() {
        return val;
    }

    public void setVal(T val) {
        this.val = val;
    }

    public void insert(TreeNode<T> child) { // 插入为子结点
        TreeNode cur = firstChild;
        if (cur == null) { // 如果当前结点没有子结点
            firstChild = child;
        } else {
            while (cur.nextSibling != null)
                cur = cur.nextSibling;
            cur.nextSibling = child;
        }
        child.parent = this;
    }

    public void delete() { // 在树中删除当前结点
        if (firstChild != null) // 递归删除
            firstChild.delete();
        TreeNode cur = parent;
        if (cur != null)
            cur = cur.firstChild;
        TreeNode pre = cur;
        if (pre == this)
            this.parent.firstChild = this.nextSibling;
        else {
            cur = cur.nextSibling;
            assert cur != null;
            while (cur.nextSibling != null) {
                if (cur == this) {
                    pre.nextSibling = cur.nextSibling;
                    break;
                }
                pre = pre.nextSibling;
                cur = cur.nextSibling;
            }
        }
    }

    public List<T> preorderTraversal() { // 遍历以this为根结点的树
        List<T> res = new ArrayList<>();
        Stack<TreeNode<T>> stack = new Stack<>();
        TreeNode<T> cur = this;
        while (!stack.empty() || cur != null) {
            if (cur != null) {
                res.add(cur.getVal());
                if (cur.nextSibling != null) {
                    stack.push(cur.nextSibling);
                }
                cur = cur.firstChild;
            } else
                cur = stack.pop();
        }
        return res;
    }
}
