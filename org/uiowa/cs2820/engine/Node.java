package org.uiowa.cs2820.engine;

import java.util.*;
import java.io.*;

// local Traverser to find a Node with given Field
class SearchField extends Traverser {
  Node h = null;

  public SearchField( Field x ) {
	  super(x);
  } 

  public boolean process(Item I) { 
    Node F = (Node) I;  // we are traversing Nodes 
    if (F.Key.equals(field)) {
      h = F; // remember this node
      if(hitList==null) hitList = new ArrayList<Node>();
      hitList.add(F);
      return false;  // stop traversing; 
      }
    return true;
    }
}

class SearchGreater extends Traverser {

	  public SearchGreater( Field x ) { 
		  super(x);
	  } 
	  public boolean process(Item I) {
		Node F = (Node) I; 
		String itemValue = (String) F.Key.getFieldValue();
		String fieldValue = (String) field.getFieldValue();
		   if (F.Key.getFieldName().equals(field.getFieldName())
		    	&& itemValue.compareTo(fieldValue)>0) {
		    hitList.add(F);
		    }
			return true;
		}
}

class SearchLess extends Traverser {

	  public SearchLess( Field x ) { 
		  super(x);
	  } 
	  public boolean process(Item I) {
		Node F = (Node) I; 
		String itemValue = (String) F.Key.getFieldValue();
		String fieldValue = (String) field.getFieldValue();
		   if (F.Key.getFieldName().equals(field.getFieldName())
		    	&& itemValue.compareTo(fieldValue)<0) {
		    hitList.add(F);
		    }
			return true;
		}
}


class SearchSuffix extends Traverser {

	public SearchSuffix( Field x ) {
		super(x);
	}
	@Override
	public boolean process(Item I) {
		Node F = (Node) I;
		String itemValue = (String) F.Key.getFieldValue();
		String fieldValue = (String) field.getFieldValue();
			if (F.Key.getFieldName().equals(field.getFieldName())
				&& itemValue.endsWith(fieldValue)){
		    hitList.add(F);
		    }
			return true;
		}
}

class SearchPrefix extends Traverser {

	public SearchPrefix( Field x ) {
		super(x);
	}
	@Override
	public boolean process(Item I) {
		Node F = (Node) I;
		String itemValue = (String) F.Key.getFieldValue();
		String fieldValue = (String) field.getFieldValue();
			if (F.Key.getFieldName().equals(field.getFieldName())
				&& itemValue.startsWith(fieldValue)){
		    hitList.add(F);
		    }
			return true;
		}
}

class SearchContains extends Traverser {

	public SearchContains( Field x ) {
		super(x);
	}
	
	@Override
	public boolean process(Item I) {
		Node F = (Node) I;
		String itemValue = (String) F.Key.getFieldValue();
		String fieldValue = (String) field.getFieldValue();
			if (F.Key.getFieldName().equals(field.getFieldName())
				&& itemValue.contains(fieldValue)){
		    hitList.add(F);
		    }
			return true;
		}
}
	

// local Traverser to find remove Id from all nodes
class IdRemover extends IDTraverser {
  ArrayList<Node> empties;  // an array of nodes with no Ids
  String identifier;        // identifier to remove
 
  public IdRemover( String id ) {
    identifier = id;
    empties = new ArrayList<Node>();
    }
  
  public boolean process(Item I) {
    Node F = (Node) I;
    int oldhead = F.identlist;
    F.identlist = Identifier.remove(F.identlist,identifier);
    F.save();
    if (F.identlist == -1) empties.add(F);
    return true;
    }
  }

public class Node implements Item, Serializable {

  static DiskLinkedList D = null;
  static Node firstNode = null;

  // Node is a basic unit in the database
  Field Key;  
  int location;  // location of area in DiskSpace
  int next;      // location of next Node
  int identlist; // location of DiskLinkedList of Identifiers

  // test for first node ever in database 
  public static void setup() {
    try {
      byte [] node0 = DiskSpace.read(0); 
      firstNode = (Node) Utility.revert(node0);
      }
    catch (Exception e) { }
    if (firstNode != null) {
      D = new DiskLinkedList(firstNode.next);
      }
    else { // firstNode is null
      Allocate.resetall();
      firstNode = new Node();  
      firstNode.location = Allocate.allocate();
      assert (firstNode.location == 0);
      firstNode.save();
      D = new DiskLinkedList(firstNode.next);
      }
    }
  
  // construct empty Node
  public Node() { 
    Key = null;
    location = 0;
    next = identlist = -1;
    }

  public int getLocation() { return location; }
  public void setLocation(int loc) { location = loc; }
  public int getNext() { return next; }
  public void setNext(int loc) { next = loc; }

  // construct Node for Field f and Identifier id
  public static Node newNode(Field f) {
    setup();  // establish firstNode and DiskLinkedList
    Node R = new Node();
    R.Key = f;
    D.insert(R);  // assigns location and next
    firstNode.next = R.location;
    firstNode.save();
    return R; 
    }

  // find existing node that has field equal to given value
  public static Node findNode(Field f) {
    setup();
    SearchField S = new SearchField(f);  // make custom traverser 
    D.traverse(S);  // go through the linked list
    return S.h;     // either null or the found Node
    }

  // save Node to DiskSpace - invoke after changes to Node
  public void save() {
    DiskSpace.write(this.location,Utility.convert(this));
    }

  // remove an identifier from all Nodes 
  public static void remove( String id ) {
    setup();
    IdRemover G = new IdRemover(id);
    D.traverse(G);  // remove identifiers from all nodes
    // Now remove all nodes that have no identifiers
    for (Node n: G.empties) {
      D.remove(n);
      firstNode.next = D.getFirst();
      firstNode.save();
      }
    }

  public String toString() {
    return "Node at " + location + " next = " + next + " field " + Key;
    }
  
  public static ArrayList<Node> findNodeLst(Traverser S) {
	    setup();
	    D.traverse(S);  // go through the linked list
	    return S.getLst();     
  }
  
}

