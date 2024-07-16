/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Abdur-Rafay
 */
public class PCB_RoundRobin_Queue {
    
    ProcessControlBlock[] list;
    int maxQueueSize, queueFront, queueRear, count;
    
    public PCB_RoundRobin_Queue(){
        maxQueueSize = 1000;
        queueFront = 0;
        queueRear = 0;
        count = 0;
        list = new ProcessControlBlock[maxQueueSize];
        for(int i = queueFront; i < maxQueueSize - 1; i = (i+1) % maxQueueSize)
            list[i] = new ProcessControlBlock();
        list[maxQueueSize - 1] = new ProcessControlBlock();
        
    }
    
    public boolean isFull(){
        return (count == maxQueueSize);
    }
    
    public boolean isEmpty(){
        return (count == 0);
    }
    
    public void AddAtEnd(ProcessControlBlock j){
        
        if(isFull())
            System.out.println();
        else{
            
            list[queueRear] = j;
            queueRear = (queueRear + 1) % maxQueueSize;
            count++;
            
        }
        
        
    }
    
    public void enqueue(ProcessControlBlock j){
        
        if(isFull())
            System.out.println();
        else{
            
            list[queueRear] = j;
            queueRear = (queueRear + 1) % maxQueueSize;
            count++;
            
            bubbleSort(list);
            
            
        }
            
    }
    
    public void bubbleSort(ProcessControlBlock[] arr) {  
        int n = queueRear;  
        Object temp = 0;  
        for(int i=0; i < n; i++){  
            
            for(int j=1; j < (n-i); j++){  
                if(arr[j-1].priority > arr[j].priority){  
                   //swap elements  
                   temp = arr[j-1];  
                   arr[j-1] = arr[j];  
                   arr[j] = (ProcessControlBlock) temp;  
                }  
                          
            } 
            
        }  
  
    }  
    
    
    public ProcessControlBlock dequeue(){
        
        ProcessControlBlock DeletedValue = new ProcessControlBlock();
        
        if(isEmpty()){
            return null;
        }
            
        else{
            DeletedValue =  list[queueFront];
            list[queueFront] = new ProcessControlBlock();
            queueFront = (queueFront + 1) % maxQueueSize;
            count--;
            return DeletedValue;
        }
    }
    
    public ProcessControlBlock peek_front(){
        
        if(isEmpty()){
            return null;
        }
            
        else{
            return list[queueFront];
        }
           
            
        
    }
    
    public static void main(String[] args) {
        
        ProcessControlBlock PCB = new ProcessControlBlock();
        PCB.priority = 2;
        
        ProcessControlBlock PCB1 = new ProcessControlBlock();
        PCB1.priority = 4;
        
        ProcessControlBlock PCB2 = new ProcessControlBlock();
        PCB2.priority = 6;
        
        
        PCB_RoundRobin_Queue q = new PCB_RoundRobin_Queue();
        q.enqueue(PCB2);
        q.enqueue(PCB1);
        q.enqueue(PCB);
        
        q.dequeue();
        q.AddAtEnd(PCB);
        q.dequeue();
        q.AddAtEnd(PCB1);
        q.dequeue();
        q.AddAtEnd(PCB2);
        
        ProcessControlBlock temp;
        
        while((temp = q.dequeue()) != null){
            System.out.println(temp.priority);
        }
        
        
        
        
    }
    
}
