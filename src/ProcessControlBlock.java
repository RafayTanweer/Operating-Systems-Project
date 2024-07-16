
import java.util.ArrayList;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Abdur-Rafay
 */
public class ProcessControlBlock {
    
    short ProcessID;
    byte priority;
    short DataSize;
    short CodeSize;
    short ProcessSize;
    String FileName;
    short[] GPRs = new short[16];  
    short[] SPRs = new short[16];
    ArrayList<int[][]> DataPageTable;
    ArrayList<int[][]> CodePageTable;
    boolean flag = false;
    
    
    
}
