
package listTools;

import java.util.Collection;
import java.util.Iterator;
import java.util.Queue;

/**
 *
 * @author Dov
 * @param <T>
 */
public class MyQueue<T> implements Queue<T>{


    private int fixedCapacity = Integer.MAX_VALUE;

    public int getFixedCapacity() {
        return fixedCapacity;
    }
    public void setFixedCapacity(int fixedCapacity) {
        this.fixedCapacity = fixedCapacity;
    }
    
    @Override
    public boolean offer(T e) {
        if(size() < getFixedCapacity()){
            add(e);
            return true;
        }
        return false;
    }

    @Override
    public T poll() {
        if(isEmpty()) return null;
        return next();
    }

    @Override
    public T element() {
        return peek();
    }

    @Override
    public int size() {
        reset();
        int size = 0;
        while(indexHasNext()){
            nextIndex();
            size++;
        }
        return size;
    }

    @Override
    public boolean contains(Object o) {
        reset();
        while(indexHasNext())
            if(nextIndex().equals(o)) return true;
        return false;
    }

    @Override
    public <T> T[] toArray(T[] ts) {
        reset();
        int size = size();
        
        if (ts == null || ts.length < size) {
            T[] toArray = (T[]) (new Object[size()]);
            for (int i = 0; i < toArray.length; i++)
                toArray[i] = (T) nextIndex();
            return toArray;
        }
        
        for(int i = 0; i < size; i++)
            ts[i] = (T)nextIndex();
        return ts;
    }
    
    @Override
    public boolean containsAll(Collection<?> clctn) {
        Iterator itr = clctn.iterator();
        while(itr.hasNext())
            if(!contains(itr.next())) return false;
        return true;
    }

    @Override
    public boolean addAll(Collection<? extends T> clctn) {
        Iterator<? extends T> itr = clctn.iterator();
        while(itr.hasNext()) add(itr.next());
        return true;
    }

    @Override
    public boolean removeAll(Collection<?> clctn) {
        int remCount = 0;
        reset();
        while(indexHasNext())
            if(clctn.contains(peekIndex())){
                removeNextIndex();
                remCount++;
            }
            else nextIndex();
        return remCount == clctn.size();
    }

    @Override
    public void clear() {
        first = null;
        reset();
    }

    @Override
    public Iterator<T> iterator() {
        final MyQueue<T> mq = this;
        reset();
        return new Iterator<T>() {            

            @Override
            public boolean hasNext() {
                return mq.indexHasNext();
            }

            @Override
            public T next() {
                return nextIndex();
            }

            @Override
            public void remove() {
                nextIndex();
            }
        };
    }

    @Override
    public boolean remove(Object o) {
        reset();
        while(indexHasNext())
            if(peekIndex().equals(o)) {
                removeNextIndex();
                return true;
            }
            else nextIndex();
        return false;
    }

    @Override
    public boolean retainAll(Collection<?> clctn) {
        reset();
        int retainCount = 0;
        while(indexHasNext())
            if(clctn.contains(peekIndex())) {
                retainCount++;
                nextIndex();
            } else removeNextIndex();
        return retainCount == clctn.size();
    }

    @Override
    public Object[] toArray() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    protected class Node{
        protected Node next;
        protected T content;

        public Node() {
        }
        public Node(Node next, T content) {
            this.next = next;
            this.content = content;
        }

        public void setContent(T content) {
            this.content = content;
        }

        public void setNext(Node next) {
            this.next = next;
        }

        public T getContent() {
            return content;
        }

        public Node getNext() {
            return next;
        }
        
    }
    
    protected Node first, last, index;    

    /**
     * constructor
     */
    public MyQueue() {
    }
    
    public boolean hasNext() {
        return first != null;
    }

    public T next() {
//        if(isLocked) throw new ArithmeticException("This Queue is locked");
        return nextNode().content;
    }
    
    @Override
    public T remove() {
//        if(isLocked)throw new ArithmeticException("can't remove, locked");
        if(isEmpty()) return null;
        T t = first.content;
        first = first.next;
        return t;
    }
    
    /**
     * adds the the appendage to this linked list in O(k) time.
     * @param appendage
     * @return 
     */
    public MyQueue<T> add(MyQueue<T> appendage){
        
        if(appendage.isEmpty()) return this;
        if(isEmpty()){
            first = appendage.first;
            last = appendage.last;
        }else {
            last.next = appendage.first;
            last = appendage.last;
        }
        return this;
    }

   
    @Override
    public boolean add(T t){
        if (isEmpty()) {
            first = new Node(null, t);
            last = first;
        }else{
            last.next = new Node(null, t);
            last = last.next;
        }
        return true;
    }
    
/*    
    private boolean isLocked = false;
    public boolean lock(){
        boolean alreadyLocked = isLocked;
        isLocked = true;
        return alreadyLocked;
    }
    public boolean unlock(){
        boolean alreadyUnlocked = !isLocked;
        isLocked = false;
        return alreadyUnlocked;
    }

    public boolean isLocked() {
        return isLocked;
    }
  */  
    public void reset(){
        //if(!sLocked) throw new ArithmeticException("this list has been locked against resetting the index");
        index = new Node(first, null);
        
    }
    
    public T nextIndex(){
        return nextIndexNode().content;
    }
    
    protected Node nextIndexNode(){
        index = index.next;
        return index;
    }
    
    protected Node nextNode(){
        Node temp = first;
        remove();
        return temp;
    }
    
        
    public boolean indexHasNext(){
        return index.next != null;
    }
    
    public void removeNextIndex(){
        if(first == index.next) first = first.next;
        if(last == index.next) last = index;
        index.next = index.next.next;
    }
    
    @Override
    public boolean isEmpty(){
        return first == null;
    }
    
    @Override
    public T peek(){
        return first.content;
    }
    
    public T peekIndex(){
        return index.next.content;
    }
    
    @Override
    public String toString() {
        reset();
        
        String toString = "[";
        if(indexHasNext())toString += nextIndex();
        while(indexHasNext()){
            System.out.println(peekIndex());
            toString += ", " + nextIndex();
        }
        toString += "]";
        
        return toString;
    }
}