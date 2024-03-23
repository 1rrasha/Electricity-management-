package application;
//1210773-Rasha Mansour

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

import javafx.scene.control.TextArea;

//AVL tree implementation
class AVLTree<T extends Comparable<T>> implements Iterable<T> {

	private AVLNode<T> root;
	private int size;

	// AVL tree operations

	// Insertion
	public void insert(T data) {
		root = insert(root, data);
		size++;
	}

	private AVLNode<T> insert(AVLNode<T> node, T data) {
		if (node == null) {
			return new AVLNode<>(data);
		}

		if (data.compareTo(node.data) < 0) {
			node.left = insert(node.left, data);
		} else if (data.compareTo(node.data) > 0) {
			node.right = insert(node.right, data);
		} else {
			// Duplicate values are not allowed
			return node;
		}

		node.height = 1 + Math.max(height(node.left), height(node.right));

		int balance = getBalance(node);

		// Left Left Case
		if (balance > 1 && data.compareTo(node.left.data) < 0) {
			return rightRotate(node);
		}

		// Right Right Case
		if (balance < -1 && data.compareTo(node.right.data) > 0) {
			return leftRotate(node);
		}

		// Left Right Case
		if (balance > 1 && data.compareTo(node.left.data) > 0) {
			node.left = leftRotate(node.left);
			return rightRotate(node);
		}

		// Right Left Case
		if (balance < -1 && data.compareTo(node.right.data) < 0) {
			node.right = rightRotate(node.right);
			return leftRotate(node);
		}

		return node;
	}

	// Deletion
	public void delete(T data) {
		root = delete(root, data);
		size--;
	}

	private AVLNode<T> delete(AVLNode<T> root, T data) {
		if (root == null) {
			return root;
		}

		if (data.compareTo(root.data) < 0) {
			root.left = delete(root.left, data);
		} else if (data.compareTo(root.data) > 0) {
			root.right = delete(root.right, data);
		} else {
			if (root.left == null || root.right == null) {
				AVLNode<T> temp = (root.left != null) ? root.left : root.right;

				if (temp == null) {
					temp = root;
					root = null;
				} else {
					root = temp; // Copy the contents of the non empty child
				}
			} else {
				AVLNode<T> temp = findMin(root.right);
				root.data = temp.data;
				root.right = delete(root.right, temp.data);
			}
		}

		if (root == null) {
			return root;
		}

		root.height = 1 + Math.max(height(root.left), height(root.right));

		int balance = getBalance(root);

		// Left Left Case
		if (balance > 1 && getBalance(root.left) >= 0) {
			return rightRotate(root);
		}

		// Left Right Case
		if (balance > 1 && getBalance(root.left) < 0) {
			root.left = leftRotate(root.left);
			return rightRotate(root);
		}

		// Right Right Case
		if (balance < -1 && getBalance(root.right) <= 0) {
			return leftRotate(root);
		}

		// Right Left Case
		if (balance < -1 && getBalance(root.right) > 0) {
			root.right = rightRotate(root.right);
			return leftRotate(root);
		}

		return root;
	}

	private AVLNode<T> findMin(AVLNode<T> node) {
		while (node.left != null) {
			node = node.left;
		}
		return node;
	}

	// Search
	public T search(T key) {
		return search(root, key);
	}

	private T search(AVLNode<T> root, T key) {
		if (root == null) {
			return null;
		}

		int compareResult = key.compareTo(root.data);

		if (compareResult < 0) {
			return search(root.left, key);
		} else if (compareResult > 0) {
			return search(root.right, key);
		} else {
			return root.data;
		}
	}

	// In order Traversal
	public void inOrderTraversal() {
		inOrderTraversal(root);
		System.out.println();
	}

	void inOrderTraversal(AVLNode<T> root) {
		if (root != null) {
			inOrderTraversal(root.left);
			System.out.print(root.data + " ");
			inOrderTraversal(root.right);
		}
	}

	public AVLNode<T> getRoot() {
		return root;
	}

	// Helper methods
	private int height(AVLNode<T> node) {
		if (node == null) {
			return 0;
		}
		return node.height;
	}

	private int getBalance(AVLNode<T> node) {
		if (node == null) {
			return 0;
		}
		return height(node.left) - height(node.right);
	}

	private AVLNode<T> rightRotate(AVLNode<T> y) {
		AVLNode<T> x = y.left;
		AVLNode<T> T2 = x.right;

		x.right = y;
		y.left = T2;

		y.height = 1 + Math.max(height(y.left), height(y.right));
		x.height = 1 + Math.max(height(x.left), height(x.right));

		return x;
	}

	private AVLNode<T> leftRotate(AVLNode<T> x) {
		AVLNode<T> y = x.right;
		AVLNode<T> T2 = y.left;

		y.left = x;
		x.right = T2;

		x.height = 1 + Math.max(height(x.left), height(x.right));
		y.height = 1 + Math.max(height(y.left), height(y.right));

		return y;
	}

	@Override
	public Iterator<T> iterator() {
		return new AVLTreeIterator(root);
	}

	private class AVLTreeIterator implements Iterator<T> {
		private Stack<AVLNode<T>> stack;

		AVLTreeIterator(AVLNode<T> root) {
			stack = new Stack<>();
			pushAll(root);
		}

		private void pushAll(AVLNode<T> node) {
			while (node != null) {
				stack.push(node);
				node = node.left;
			}
		}

		@Override
		public boolean hasNext() {
			return !stack.isEmpty();
		}

		@Override
		public T next() {
			AVLNode<T> node = stack.pop();
			pushAll(node.right);
			return node.data;
		}
	}

	public int size() {
		return size;
	}

	// Helper method to calculate the height of AVL tree
	private int avlTreeHeight(AVLNode<T> root) {
		if (root == null) {
			return 0;
		}
		return Math.max(avlTreeHeight(root.left), avlTreeHeight(root.right)) + 1;
	}

	// method to calculate the height of AVL tree
	public int avlTreeHeight() {
		return avlTreeHeight(root);
	}

	public boolean contains(T data) {
		return contains(root, data);
	}

	private boolean contains(AVLNode<T> node, T data) {
		if (node == null) {
			return false;
		}

		int compareResult = data.compareTo(node.data);

		if (compareResult == 0) {
			return true; // Element found in the current node
		} else if (compareResult < 0) {
			return contains(node.left, data); // Search in the left subtree
		} else {
			return contains(node.right, data); // Search in the right subtree
		}
	}
	/////////////////////////////////////////////////////////////////////////////////////

}
