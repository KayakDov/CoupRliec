
package listTools;

import java.util.ArrayList;
import java.util.Comparator;

/**
 *
 * @author Dov Neimand
 */
public class InsertionSort {
    
    ArrayList list;
    Comparator comp;

    public InsertionSort(ArrayList list, Comparator comp) {
        this.list = list;
        this.comp = comp;
        go();
    }

    
    
    
    
    private boolean lessThan(int i, int j){
        return comp.compare(list.get(i), list.get(j)) > 0 ;
    }
    
    private void move(int target, int from){
        list.add(target, list.remove(from));
    }
    
    public void go(){
        
        for(int sorted = 1; sorted < list.size(); sorted++)
            for(int i = 0; i < sorted; i++)
                if(lessThan(i, sorted)) move(i, sorted);
                
        
    }

}
