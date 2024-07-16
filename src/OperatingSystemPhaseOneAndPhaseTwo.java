
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Scanner;


/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

//GROUP MEMBERS:
//Abdur Rafay Tanweer 19734
//Ali Muhammad Aslam 18585
//Abdur Rafai Tariq 18583


public class OperatingSystemPhaseOneAndPhaseTwo {
    
    
    public byte[] Memory = new byte[256 * 256];   //Creating a byte array for Memory
        
    public short[] GPRs = new short[16];   //Creating an array of short data type for General Purpose Registers(R0 to R15)
        
    public short[] SPRs = new short[16];   //Creating an array of short data type for Special Purpose Registers
                                           // SPRs[0] is the Code Base
                                           // SPRs[2] is the Code Counter
                                           // SPRs[9] is the Program Counter
                                           // SPRs[10] is the Instruction Register
                                           // In this phase we have taken SPRs[13] as the Flag Register
                                           // The code line: SPRs[9] = (short) (SPRs[9] + 1); means we are incrementing the program counter by 1. (Stated here because this line of code is used all over the code)
    
    public int[] Frames = new int[512];
    
    public PriorityQueue<ProcessControlBlock> ReadyQueue1 = new PriorityQueue<ProcessControlBlock>(new Comparator<ProcessControlBlock>() {
            public int compare(ProcessControlBlock P1, ProcessControlBlock P2) {
                return P1.priority-P2.priority;
            }
        });
     
     /*public PriorityQueue<ProcessControlBlock> ReadyQueue2 = new PriorityQueue<ProcessControlBlock>(new Comparator<ProcessControlBlock>() {
            public int compare(ProcessControlBlock P1, ProcessControlBlock P2) {
                return P1.priority-P2.priority;
            }
        });*/
    
    
    public PCB_RoundRobin_Queue ReadyQueue2 = new PCB_RoundRobin_Queue();
     
    public PriorityQueue<ProcessControlBlock> RunningQueue = new PriorityQueue<>();
    
       
    public void FetchDecodeExecute() throws FileNotFoundException, IOException {
                
        for(int i = 0; i < Frames.length; i++)
           Frames[i] = i;
        
         ArrayList<int[][]> Free_PageTable = new ArrayList<>();
         
         int FreePageTable_PageCount = 0;
        
        //-----------------------------------------------------------------------------------------------------------------------//
        
        byte Priority_Process1;
    
        short PID_Process1;
        
        int startIndex;
        
        short DataSize_Process1 = 0;
        
        String inputFile = "noop";
       
        InputStream inputStream = new FileInputStream(inputFile);
 
        byte byteRead;
 
            //while ((byteRead = inputStream.read()) != -1) {
                //System.out.println(byteRead);
            //}
            
        byteRead =  (byte) inputStream.read();
        Priority_Process1 = byteRead;
            
        String HexOfPID1 = Integer.toHexString(unsignedToBytes((byte) inputStream.read())) + Integer.toHexString(unsignedToBytes((byte) inputStream.read()));
        PID_Process1 = (short) Integer.parseInt(HexOfPID1, 16);
            
        String HexOfDataSizeP1 = Integer.toHexString(unsignedToBytes((byte) inputStream.read())) + Integer.toHexString(unsignedToBytes((byte) inputStream.read()));
        DataSize_Process1 = (short) Integer.parseInt(HexOfDataSizeP1, 16);
            
        
        ArrayList<int[][]> DataPageTable_Process1 = new ArrayList<>();
        
        int NumberOfFrames_DataPageTable_Process1;
        
        if ((DataSize_Process1/(double)128) % 1 == 0)
            NumberOfFrames_DataPageTable_Process1 = (int) (DataSize_Process1/(double) 128);
        else
            NumberOfFrames_DataPageTable_Process1 = (int)((DataSize_Process1/(double) 128) + 1);
        
       
        int FrameCounter = 0;
        
        startIndex = FrameCounter * 128;
        
        for (int i = 0; i < NumberOfFrames_DataPageTable_Process1; i++){
            
            int[][] DataPageTableProcess1_Entry = new int[1][2];
            DataPageTableProcess1_Entry[0][0] = i;
            DataPageTableProcess1_Entry[0][1] = FrameCounter;
            DataPageTable_Process1.add(DataPageTableProcess1_Entry);
            FrameCounter++;
            
        }
        
        
        inputStream.read();
        inputStream.read();
        inputStream.read();
        
        
        for (int i = startIndex; i < DataSize_Process1; i++){
            Memory[i] = (byte) inputStream.read();
        } 
            
        ArrayList<Byte> Code_Bytes = new ArrayList<>();
        
        short CodeSize_Process1 = 0;
            
        while( (byteRead = (byte) inputStream.read()) != -1 ){
            
            Code_Bytes.add(byteRead);
            CodeSize_Process1++;
            
        }
        
        //System.out.println(byteRead);
        //System.out.println(CodeSize_Process1);
        
        ArrayList<int[][]> CodePageTable_Process1 = new ArrayList<>();
        
        int NumberOfFrames_CodePageTable_Process1;
        
        if ((CodeSize_Process1/(double)128) % 1 == 0)
            NumberOfFrames_CodePageTable_Process1 = (int) (CodeSize_Process1/(double) 128);
        else
            NumberOfFrames_CodePageTable_Process1 = (int)((CodeSize_Process1/(double) 128) + 1);
        
        
        startIndex = FrameCounter * 128;
        //System.out.println(startIndex);
        
        for (int i = 0; i < NumberOfFrames_CodePageTable_Process1; i++){
            
            int[][] CodePageTableProcess1_Entry = new int[1][2];
            CodePageTableProcess1_Entry[0][0] = i;
            CodePageTableProcess1_Entry[0][1] = FrameCounter;
            CodePageTable_Process1.add(CodePageTableProcess1_Entry);
            FrameCounter++;
            
        }
        
        //for(int i = 0; i < CodePageTable_Process1.size(); i++)
            //System.out.println(CodePageTable_Process1.get(i)[0][0] + "   " + CodePageTable_Process1.get(i)[0][1]);
        
        int a = 0;
        
        for (int i = startIndex; i < CodeSize_Process1; i++){
            Memory[i] = Code_Bytes.get(a);
            a++;
        } 
            
        ProcessControlBlock PCB_Process1 = new ProcessControlBlock();
        PCB_Process1.ProcessID = PID_Process1;
        PCB_Process1.priority = Priority_Process1;
        PCB_Process1.DataSize = DataSize_Process1;
        PCB_Process1.CodeSize = CodeSize_Process1;
        PCB_Process1.ProcessSize = (short) (DataSize_Process1 + CodeSize_Process1);
        PCB_Process1.FileName = "noop";
        PCB_Process1.DataPageTable = DataPageTable_Process1;
        PCB_Process1.CodePageTable = CodePageTable_Process1;
        
        //----------------------------------------------------------------------------------------------------------------------------------//
        
        byte Priority_Process2;
    
        short PID_Process2;
        
        short DataSize_Process2 = 0;
        
        inputFile = "p5";
       
        InputStream inputStream2 = new FileInputStream(inputFile);
 
 
            //while ((byteRead = inputStream.read()) != -1) {
                //System.out.println(byteRead);
            //}
            
        byteRead =  (byte) inputStream2.read();
        Priority_Process2 = byteRead;
            
        String HexOfPID2 = Integer.toHexString(unsignedToBytes((byte) inputStream2.read())) + Integer.toHexString(unsignedToBytes((byte) inputStream2.read()));
        PID_Process2 = (short) Integer.parseInt(HexOfPID2, 16);
            
        String HexOfDataSizeP2 = Integer.toHexString(unsignedToBytes((byte) inputStream2.read())) + Integer.toHexString(unsignedToBytes((byte) inputStream2.read()));
        DataSize_Process2 = (short) Integer.parseInt(HexOfDataSizeP2, 16);
            
        
        ArrayList<int[][]> DataPageTable_Process2 = new ArrayList<>();
        
        int NumberOfFrames_DataPageTable_Process2;
        
        if ((DataSize_Process2/(double)128) % 1 == 0)
            NumberOfFrames_DataPageTable_Process2 = (int) (DataSize_Process2/(double) 128);
        else
            NumberOfFrames_DataPageTable_Process2 = (int)((DataSize_Process2/(double) 128) + 1);
        
        
        startIndex = FrameCounter * 128;
        
        for (int i = 0; i < NumberOfFrames_DataPageTable_Process2; i++){
            
            int[][] DataPageTableProcess2_Entry = new int[1][2];
            DataPageTableProcess2_Entry[0][0] = i;
            DataPageTableProcess2_Entry[0][1] = FrameCounter;
            DataPageTable_Process2.add(DataPageTableProcess2_Entry);
            FrameCounter++;
            
        }
        
        
        inputStream2.read();
        inputStream2.read();
        inputStream2.read();
        
        
        for (int i = startIndex; i < DataSize_Process2; i++){
            Memory[i] = (byte) inputStream2.read();
        } 
            
        ArrayList<Byte> Code_Bytes2 = new ArrayList<>();
        
        short CodeSize_Process2 = 0;
            
        while( (byteRead = (byte) inputStream2.read()) != -1 ){
            
            Code_Bytes2.add(byteRead);
            CodeSize_Process2++;
            
        }
        
        //System.out.println(byteRead);
        //System.out.println(CodeSize_Process1);
        
        ArrayList<int[][]> CodePageTable_Process2 = new ArrayList<>();
        
        int NumberOfFrames_CodePageTable_Process2;
        
        if ((CodeSize_Process2/(double)128) % 1 == 0)
            NumberOfFrames_CodePageTable_Process2 = (int) (CodeSize_Process2/(double) 128);
        else
            NumberOfFrames_CodePageTable_Process2 = (int)((CodeSize_Process2/(double) 128) + 1);
        
        
        startIndex = FrameCounter * 128;
        //System.out.println(startIndex);
        
        for (int i = 0; i < NumberOfFrames_CodePageTable_Process2; i++){
            
            int[][] CodePageTableProcess2_Entry = new int[1][2];
            CodePageTableProcess2_Entry[0][0] = i;
            CodePageTableProcess2_Entry[0][1] = FrameCounter;
            CodePageTable_Process2.add(CodePageTableProcess2_Entry);
            FrameCounter++;
            
        }
        
        //for(int i = 0; i < CodePageTable_Process1.size(); i++)
            //System.out.println(CodePageTable_Process1.get(i)[0][0] + "   " + CodePageTable_Process1.get(i)[0][1]);
        
        a = 0;
        
        for (int i = startIndex; i < CodeSize_Process2; i++){
            Memory[i] = Code_Bytes2.get(a);
            a++;
        } 
            
        ProcessControlBlock PCB_Process2 = new ProcessControlBlock();
        PCB_Process2.ProcessID = PID_Process2;
        PCB_Process2.priority = Priority_Process2;
        PCB_Process2.DataSize = DataSize_Process2;
        PCB_Process2.CodeSize = CodeSize_Process2;
        PCB_Process2.ProcessSize = (short) (DataSize_Process2 + CodeSize_Process2);
        PCB_Process2.FileName = "p5";
        PCB_Process2.DataPageTable = DataPageTable_Process2;
        PCB_Process2.CodePageTable = CodePageTable_Process2;
        
        
        //----------------------------------------------------------------------------------------------------------------------------------//
        
        if(PCB_Process1.priority >= 0 && PCB_Process1.priority <= 15)
            ReadyQueue1.add(PCB_Process1);
        else if(PCB_Process1.priority >= 16 && PCB_Process1.priority <= 31)
            ReadyQueue2.enqueue(PCB_Process1);
            
        //----------------------------------------------------------------------------------------------------------------------------------//
        
        if(FrameCounter != 512){
            
            for(int i = FrameCounter; i < 512; i++){
            
                int[][] FreePageTable_Entry = new int[1][2];
                FreePageTable_Entry[0][0] = FreePageTable_PageCount;
                FreePageTable_Entry[0][1] = FrameCounter;
                Free_PageTable.add(FreePageTable_Entry);
                FreePageTable_PageCount++;
            
            }
            
        }
         
        //------------------------------------------------------------------------------------------------------------------------------------//
        
        
        ProcessControlBlock Process_to_Execute;
        
        Process_to_Execute = CPU_Scheduler();
        
        int Instruction_Count = 0;
        
        int Instructions_StartIndex;
        
        int Instructions_EndIndex;
        
        boolean Quantum;
        
        while(Process_to_Execute != null){
            
            RunningQueue.add(Process_to_Execute);
            
            if(Process_to_Execute.priority >= 0 && Process_to_Execute.priority <= 15){
                   
                SPRs = Process_to_Execute.SPRs;
                GPRs = Process_to_Execute.GPRs;

                System.out.println("Process " + Process_to_Execute.FileName + " has started execution.");
                
                Instructions_StartIndex = Process_to_Execute.CodePageTable.get(0)[0][1] * 128;

                Instructions_EndIndex = (Process_to_Execute.CodePageTable.get(Process_to_Execute.CodePageTable.size() - 1)[0][1] * 128) + (Process_to_Execute.CodeSize - 1);

                SPRs[0] = (short) Instructions_StartIndex;

                SPRs[2] = (short) Instructions_EndIndex;

                SPRs[9] = SPRs[0];

                print(); 

                SPRs[10] = unsignedToBytes(Memory[SPRs[9]]);  //Fetching the instruction from the memory and storing it in the Instruction Register

                SPRs[9] = (short) (SPRs[9] + 1); //Increment PC by 1

                String opcode = Integer.toHexString(SPRs[10]); //Decoding the opcode byte in IR i.e. converting it into hexadecimal.

                while(!opcode.equals("f3")){  //THe while loop will run untill the "f3" instruction that terminated the process

                    switch(opcode){   //Calling the appropiate function to execute the instruction on the basis of the opcode

                        case "16":
                            MOV();
                            break;
                        case "17":
                            ADD();
                            break;
                        case "18":
                            SUB();
                            break;
                        case "19":
                            MUL();
                            break;
                        case "1a":
                            DIV();
                            break;
                        case "1b":
                            AND();
                            break;
                        case "1c":
                            OR();
                            break;
                        case "30":
                            MOVI();
                            break;
                        case "31":
                            ADDI();
                            break;
                        case "32":
                            SUBI();
                            break;
                        case "33":
                            MULI();
                            break;
                        case "34":
                            DIVI();
                            break;
                        case "35":
                            ANDI();
                            break;
                        case "36":
                            ORI();
                            break;
                        case "37":
                            BZ();
                            break;
                        case "38":
                            BNZ();
                            break;
                        case "39":
                            BC();
                            break;
                        case "3a":
                            BS();
                            break;
                        case "3b":
                            JMP();
                            break;
                        case "51":
                            MOVL();
                            break;
                        case "52":
                            MOVS();
                            break;
                        case "71":
                            SHL();
                            break;
                        case "72":
                            SHR();
                            break;
                        case "73":
                            RTL();
                            break;
                        case "74":
                            RTR();
                            break;
                        case "75":
                            INC();
                            break;
                        case "76":
                            DEC();
                            break;
                        case "f2":
                            NOOP();
                            break;

                    }

                    print();

                    SPRs[10] = unsignedToBytes(Memory[SPRs[9]]); //Fetch instruction from Memory again and store it in the Instruction Register

                    SPRs[9] = (short) (SPRs[9] + 1); //Increment PC by 1

                    opcode = Integer.toHexString(SPRs[10]); //Decoding the opcode byte in IR i.e. converting it into hexadecimal.

                }

                Process_to_Execute.SPRs = SPRs;
                Process_to_Execute.GPRs = GPRs;

                System.out.println("The Execution of Process " + Process_to_Execute.FileName + " is complete.");

                printPCB(Process_to_Execute);
                
                createPCBFile(Process_to_Execute);

                for(int i = 0; i < Process_to_Execute.DataPageTable.size(); i++){

                    int[][] FreePageTable_Entry = new int[1][2];
                    FreePageTable_Entry[0][0] = FreePageTable_PageCount;
                    FreePageTable_Entry[0][1] = Process_to_Execute.DataPageTable.get(i)[0][1];
                    Free_PageTable.add(FreePageTable_Entry);
                    FreePageTable_PageCount++;

                }

                for(int i = 0; i < Process_to_Execute.CodePageTable.size(); i++){

                    int[][] FreePageTable_Entry = new int[1][2];
                    FreePageTable_Entry[0][0] = FreePageTable_PageCount;
                    FreePageTable_Entry[0][1] = Process_to_Execute.CodePageTable.get(i)[0][1];
                    Free_PageTable.add(FreePageTable_Entry);
                    FreePageTable_PageCount++;

                }

                RunningQueue.clear();

                Process_to_Execute = CPU_Scheduler();
              
            }
            else if(Process_to_Execute.priority >= 16 && Process_to_Execute.priority <= 31){
                
                SPRs = Process_to_Execute.SPRs;
                GPRs = Process_to_Execute.GPRs;
                
                Quantum = false;
                
                Instruction_Count = 0;

                System.out.println("Process " + Process_to_Execute.FileName + " has started execution.");
                
                Instructions_StartIndex = Process_to_Execute.CodePageTable.get(0)[0][1] * 128;

                Instructions_EndIndex = (Process_to_Execute.CodePageTable.get(Process_to_Execute.CodePageTable.size() - 1)[0][1] * 128) + (Process_to_Execute.CodeSize - 1);

                SPRs[0] = (short) Instructions_StartIndex;

                SPRs[2] = (short) Instructions_EndIndex;
                
                if(Process_to_Execute.flag == false)
                    SPRs[9] = SPRs[0];

                print(); 

                SPRs[10] = unsignedToBytes(Memory[SPRs[9]]);  //Fetching the instruction from the memory and storing it in the Instruction Register

                SPRs[9] = (short) (SPRs[9] + 1); //Increment PC by 1

                String opcode = Integer.toHexString(SPRs[10]); //Decoding the opcode byte in IR i.e. converting it into hexadecimal.

                while(!opcode.equals("f3")){  //THe while loop will run untill the "f3" instruction that terminated the process

                    switch(opcode){   //Calling the appropiate function to execute the instruction on the basis of the opcode

                        case "16":
                            MOV();
                            break;
                        case "17":
                            ADD();
                            break;
                        case "18":
                            SUB();
                            break;
                        case "19":
                            MUL();
                            break;
                        case "1a":
                            DIV();
                            break;
                        case "1b":
                            AND();
                            break;
                        case "1c":
                            OR();
                            break;
                        case "30":
                            MOVI();
                            break;
                        case "31":
                            ADDI();
                            break;
                        case "32":
                            SUBI();
                            break;
                        case "33":
                            MULI();
                            break;
                        case "34":
                            DIVI();
                            break;
                        case "35":
                            ANDI();
                            break;
                        case "36":
                            ORI();
                            break;
                        case "37":
                            BZ();
                            break;
                        case "38":
                            BNZ();
                            break;
                        case "39":
                            BC();
                            break;
                        case "3a":
                            BS();
                            break;
                        case "3b":
                            JMP();
                            break;
                        case "51":
                            MOVL();
                            break;
                        case "52":
                            MOVS();
                            break;
                        case "71":
                            SHL();
                            break;
                        case "72":
                            SHR();
                            break;
                        case "73":
                            RTL();
                            break;
                        case "74":
                            RTR();
                            break;
                        case "75":
                            INC();
                            break;
                        case "76":
                            DEC();
                            break;
                        case "f2":
                            NOOP();
                            break;

                    }

                    print();
                    
                    Instruction_Count++;
                    
                    if(Instruction_Count == 4){
                        System.out.println("Time Quantum for this process is completed");
                        System.out.println("Now this process will be executed in the next available Quantum");
                        System.out.println("----------------------------------------------------------");
                        Quantum = true;
                        Process_to_Execute.flag = true;
                        break;
                    }
                        

                    SPRs[10] = unsignedToBytes(Memory[SPRs[9]]); //Fetch instruction from Memory again and store it in the Instruction Register

                    SPRs[9] = (short) (SPRs[9] + 1); //Increment PC by 1

                    opcode = Integer.toHexString(SPRs[10]); //Decoding the opcode byte in IR i.e. converting it into hexadecimal.

                }

                Process_to_Execute.SPRs = SPRs;
                Process_to_Execute.GPRs = GPRs;
                
                if(Quantum == false){
                    
                    System.out.println("The Execution of Process " + Process_to_Execute.FileName + " is complete.");

                    printPCB(Process_to_Execute);
                    
                    createPCBFile(Process_to_Execute);

                    for(int i = 0; i < Process_to_Execute.DataPageTable.size(); i++){

                        int[][] FreePageTable_Entry = new int[1][2];
                        FreePageTable_Entry[0][0] = FreePageTable_PageCount;
                        FreePageTable_Entry[0][1] = Process_to_Execute.DataPageTable.get(i)[0][1];
                        Free_PageTable.add(FreePageTable_Entry);
                        FreePageTable_PageCount++;

                    }

                    for(int i = 0; i < Process_to_Execute.CodePageTable.size(); i++){

                        int[][] FreePageTable_Entry = new int[1][2];
                        FreePageTable_Entry[0][0] = FreePageTable_PageCount;
                        FreePageTable_Entry[0][1] = Process_to_Execute.CodePageTable.get(i)[0][1];
                        Free_PageTable.add(FreePageTable_Entry);
                        FreePageTable_PageCount++;

                    }
                    
                    RunningQueue.clear();

                    Process_to_Execute = CPU_Scheduler();
                    
                }
                else{
                    
                    ReadyQueue2.AddAtEnd(Process_to_Execute);
                    
                    RunningQueue.clear();

                    Process_to_Execute = CPU_Scheduler();
                    
                    
                }

                
                
            }
            
         
        
        }
        
        System.out.println("All Processes Executed");
        
        
        //Phase 1 Stuff
        /*ArrayList<String> hex_instr = new ArrayList(); //Creating an ArrayList in order to store the hexadecimal strings of the instructions
        
        File Phase1 = new File("p1.txt");  //Reading the hexadecimal instructions from the filr p1 
        Scanner sc = new Scanner(Phase1);
        while(sc.hasNext())
            hex_instr.add(sc.next());
        
             
        byte[] byte_instr = new byte[12]; 
        
        for(int i = 0; i < hex_instr.size(); i++)
            byte_instr[i] = (byte) Integer.parseInt(hex_instr.get(i), 16); //Converting the hexadecimal instructions i.e. two hexadecimal digits into a byte and storing them in a byte array 
            
        
        SPRs[0] = 0; //Code Base set to the index of the first instruction i.e. 0
        SPRs[2] = SPRs[0];  //Code Counter will initially be at the Code Base
        
        for(int i = 0; i < byte_instr.length; i++){
            
            Memory[i] = byte_instr[i];  //Loading instructions into the Memory
            SPRs[2] = (short) i; //Code Counter will be incremented and eventually point to the index of the last instruction
            
        }
        
        SPRs[9] = SPRs[0]; //PC is initially set to the Code Base
        
        System.out.println("Instructions loaded into Memory.");
        
        print(); //Printing the initial contents of all the Registers
        
        SPRs[10] = unsignedToBytes(Memory[SPRs[9]]);  //Fetching the instruction from the memory and storing it in the Instruction Register
        
        SPRs[9] = (short) (SPRs[9] + 1); //PC incremented by 1
        
        String opcode = Integer.toHexString(SPRs[10]); //Decoding the opcode byte in IR i.e. converting it into hexadecimal. 
        //System.out.println(opcode);
        
        while(!opcode.equals("f3")){  //THe while loop will run untill the "f3" instruction that terminated the process
            
            switch(opcode){   //Calling the appropiate function to execute the instruction on the basis of the opcode
                
                case "16":
                    MOV();
                    break;
                case "17":
                    ADD();
                    break;
                case "18":
                    SUB();
                    break;
                case "19":
                    MUL();
                    break;
                case "1a":
                    DIV();
                    break;
                case "1b":
                    AND();
                    break;
                case "1c":
                    OR();
                    break;
                case "30":
                    MOVI();
                    break;
                case "31":
                    ADDI();
                    break;
                case "32":
                    SUBI();
                    break;
                case "33":
                    MULI();
                    break;
                case "34":
                    DIVI();
                    break;
                case "35":
                    ANDI();
                    break;
                case "36":
                    ORI();
                    break;
                case "37":
                    BZ();
                    break;
                case "38":
                    BNZ();
                    break;
                case "39":
                    BC();
                    break;
                case "3a":
                    BS();
                    break;
                case "3b":
                    JMP();
                    break;
                case "51":
                    MOVL();
                    break;
                case "52":
                    MOVS();
                    break;
                case "71":
                    SHL();
                    break;
                case "72":
                    SHR();
                    break;
                case "73":
                    RTL();
                    break;
                case "74":
                    RTR();
                    break;
                case "75":
                    INC();
                    break;
                case "76":
                    DEC();
                    break;
                case "f2":
                    NOOP();
                    break;
    
            }

            print(); //Printing the contents of the registers after each executed instruction
            
            SPRs[10] = unsignedToBytes(Memory[SPRs[9]]); //Fetch instruction from Memory again and store it in the Instruction Register
        
            SPRs[9] = (short) (SPRs[9] + 1); //Increment PC by 1
            
            opcode = Integer.toHexString(SPRs[10]); //Decoding the opcode byte in IR i.e. converting it into hexadecimal.
   
        }
        
        System.out.println("Execution Terminated");
        System.out.println("Final Status of the registers:"); //Printing the Final status of all the Registers
        
        for(int i = 0; i < GPRs.length; i++)   
            System.out.println("R" + i + ": " + GPRs[i]);
        
        System.out.println();
        
        System.out.println("The Special Purpose Registers:");
            
        for(int i = 0; i < SPRs.length; i++)
            System.out.println("SPR " + i + ": " + SPRs[i]);
        
        System.out.println();*/
        
        
    }
    
    public void createPCBFile(ProcessControlBlock PCB) throws IOException{
        
        FileWriter fw = new FileWriter("PCB_" + PCB.FileName ,false);
        
        fw.write("Process File Name: " + PCB.FileName + "\n");
        fw.write("Process ID: " + PCB.ProcessID + "\n");
        fw.write("Process Priority: " + PCB.priority + "\n");
        fw.write("Process Size: " + PCB.ProcessSize + "\n");
        
        for(int i = 0; i < PCB.GPRs.length; i++)   
            fw.write("R" + i + ": " + GPRs[i] + "\n");
        
        fw.write("\n");
        
        fw.write("The Special Purpose Registers: " + "\n");
        
        for(int i = 0; i < PCB.SPRs.length; i++)   
            fw.write("R" + i + ": " + SPRs[i] + "\n");
        
        fw.close();
        
        
        
    }
    
    public void printPCB(ProcessControlBlock PCB){
        
        System.out.println("Printing the contents of PCB including the final status of the registers: "); //Printing the Final status of all the Registers
        
        System.out.println("Process File Name: " + PCB.FileName);
        
        System.out.println("Process ID: " + PCB.ProcessID);

        System.out.println("Process Priority: " + PCB.priority);
        
        System.out.println("Process Size: " + PCB.ProcessSize);
        
        System.out.println("The General Purpose Registers:");
        
        for(int i = 0; i < PCB.GPRs.length; i++)   
            System.out.println("R" + i + ": " + GPRs[i]);
        
        System.out.println();
        
        System.out.println("The Special Purpose Registers:");
            
        for(int i = 0; i < PCB.SPRs.length; i++)
            System.out.println("SPR " + i + ": " + SPRs[i]);
        
        System.out.println();
        
    }
    
    public short unsignedToBytes(byte a){ //Function used when retrieving data from memory and to overcome the limitations of byte
        
        short b = (short) (a & 0xFF);
        return b;
        
    }
    
    public ProcessControlBlock CPU_Scheduler(){
        
        if(ReadyQueue1.peek() == null && ReadyQueue2.peek_front() == null)
            return null;
        else{
            
            ProcessControlBlock Process;
        
            Process = ReadyQueue1.poll();
        
            if(Process != null)
                return Process;
            else{
            
                Process = ReadyQueue2.dequeue();
                return Process;
            
            }
            
        }
         
    }
    
    
    public void MOV(){
        
        short DestinationRegister = Memory[SPRs[9]]; //Reading from Memory, the register where the contents of the other register will be stored
        
        SPRs[9] = (short) (SPRs[9] + 1);
        
        short RegToCopyContentFrom = Memory[SPRs[9]]; //Reading from Memory, the register whose contents need to be copied
        
        GPRs[DestinationRegister] = GPRs[RegToCopyContentFrom]; //Moving the contents of the RegToCopyContentFRom into the Destination Register
        
        SPRs[9] = (short) (SPRs[9] + 1);
        
        System.out.println("MOV instruction executed.");
         
    }
    
    
    public void ADD(){
        
        short DestinationRegister = Memory[SPRs[9]]; //Reading from Memory, the register where the result will be stored
        
        SPRs[9] = (short) (SPRs[9] + 1);
        
        short RegToTakeContentFrom  = Memory[SPRs[9]]; //Reading from Memory, the register whose content will be needed to perform the operation.
        
        //The operation will be performed between the content of the Destination Register and the content of the other Register
        
        int Result = (GPRs[DestinationRegister] + GPRs[RegToTakeContentFrom]); //Performing the addition operation
        
        //Checking Conditions in order to set FlagRegister
        
        //if(Result == 0)
            //setFlagRegZeroBit();
        //else if(Result < 0)
            //setFlagRegSignBit();
        
        //if(Result < -32768 || Result > 32767)
            //setFlagRegOverflowBit();
        
        GPRs[DestinationRegister] = (short) Result;  //Storing the result in the destination register
        
        SPRs[9] = (short) (SPRs[9] + 1);
        
        System.out.println("ADD instruction executed.");
        
    }
    
    public void SUB(){
        
        short DestinationRegister = Memory[SPRs[9]]; //Reading from Memory, the register where the result will be stored
        
        SPRs[9] = (short) (SPRs[9] + 1);
        
        short RegToTakeContentFrom  = Memory[SPRs[9]]; //Reading from Memory, the register whose content will be needed to perform the operation.
        
        //The operation will be performed between the content of the Destination Register and the content of the other Register
        
        int Result = (GPRs[DestinationRegister] - GPRs[RegToTakeContentFrom]);  //Performing the subtraction operation
        
        //Checking Conditions in order to set FlagRegister
        
        //if(Result == 0)
            //setFlagRegZeroBit();
        //else if(Result < 0)
            //setFlagRegSignBit();
        
        //if(Result < -32768 || Result > 32767)
            //setFlagRegOverflowBit();
        
        GPRs[DestinationRegister] = (short) Result;  //Storing the result in the destination register
        
        SPRs[9] = (short) (SPRs[9] + 1);
        
        System.out.println("SUB instruction executed.");
        
    }
    
    public void MUL(){
        
        short DestinationRegister = Memory[SPRs[9]]; //Reading from Memory, the register where the result will be stored
        
        SPRs[9] = (short) (SPRs[9] + 1);
        
        short RegToTakeContentFrom  = Memory[SPRs[9]]; //Reading from Memory, the register whose content will be needed to perform the operation.
        
        //The operation will be performed between the content of the Destination Register and the content of the other Register 
        
        int Result = (GPRs[DestinationRegister] * GPRs[RegToTakeContentFrom]); //Performing the multiply operation
        
        //Checking Conditions in order to set FlagRegister
        
        //if(Result == 0)
            //setFlagRegZeroBit();
        //else if(Result < 0)
            //setFlagRegSignBit();
        
        //if(Result < -32768 || Result > 32767)
            //setFlagRegOverflowBit();
        
        GPRs[DestinationRegister] = (short) Result; //Storing the result in the destination register
        
        SPRs[9] = (short) (SPRs[9] + 1);
        
        System.out.println("MUL instruction executed.");
        
    }
    
    public void DIV(){
        
        short DestinationRegister = Memory[SPRs[9]];  //Reading from Memory, the register where the result will be stored
        
        SPRs[9] = (short) (SPRs[9] + 1);
        
        short RegToTakeContentFrom  = Memory[SPRs[9]]; //Reading from Memory, the register whose content will be needed to perform the operation.
        
        //The operation will be performed between the content of the Destination Register and the content of the other Register 
        
        int Result = (GPRs[DestinationRegister] / GPRs[RegToTakeContentFrom]); //Performing the division operation
        
        //Checking Conditions in order to set FlagRegister
        
        //if(Result == 0)
            //setFlagRegZeroBit();
        //else if(Result < 0)
            //setFlagRegSignBit();
        
        //if(Result < -32768 || Result > 32767)
            //setFlagRegOverflowBit();
        
        GPRs[DestinationRegister] = (short) Result;  //Storing the result in the destination register
        
        SPRs[9] = (short) (SPRs[9] + 1);
        
        System.out.println("DIV instruction executed.");
        
    }
    
    public void AND(){
        
        short DestinationRegister = Memory[SPRs[9]]; //Reading from Memory, the register where the result will be stored
        
        SPRs[9] = (short) (SPRs[9] + 1);
        
        short RegToTakeContentFrom  = Memory[SPRs[9]]; //Reading from Memory, the register whose content will be needed to perform the operation.
        
        //The operation will be performed between the content of the Destination Register and the content of the other Register
        
        int Result = (GPRs[DestinationRegister] & GPRs[RegToTakeContentFrom]); //Performing the AND operation
        
        //Checking Conditions in order to set FlagRegister
        
        //if(Result == 0)
            //setFlagRegZeroBit();
        //else if(Result < 0)
            //setFlagRegSignBit();
        
        //if(Result < -32768 || Result > 32767)
            //setFlagRegOverflowBit();
        
        GPRs[DestinationRegister] = (short) Result; //Storing the result in the destination register
        
        SPRs[9] = (short) (SPRs[9] + 1);
        
        System.out.println("AND instruction executed.");
        
    }
    
    public void OR(){
        
        short DestinationRegister = Memory[SPRs[9]]; //Reading from Memory, the register where the result will be stored
        
        SPRs[9] = (short) (SPRs[9] + 1);
        
        short RegToTakeContentFrom  = Memory[SPRs[9]]; //Reading from Memory, the register whose content will be needed to perform the operation.
        
        //The operation will be performed between the content of the Destination Register and the content of the other Register
        
        int Result = (GPRs[DestinationRegister] | GPRs[RegToTakeContentFrom]); //Performing the OR operation
        
        //Checking Conditions in order to set FlagRegister
        
        //if(Result == 0)
            //setFlagRegZeroBit();
        //else if(Result < 0)
            //setFlagRegSignBit();
        
        //if(Result < -32768 || Result > 32767)
            //setFlagRegOverflowBit();
        
        GPRs[DestinationRegister] = (short) Result; //Storing the result in the destination register
        
        SPRs[9] = (short) (SPRs[9] + 1);
        
        System.out.println("OR instruction executed.");
        
    }
     
    public void MOVI(){
        
        short DestinationRegister = Memory[SPRs[9]];
        //System.out.println(DestinationRegister);
        //int DestRegIndex = Integer.parseInt(Integer.toHexString(DestinationRegister),16);
        
        //Reading 2 bytes from Memory for num
        
        SPRs[9] = (short) (SPRs[9] + 1);
        
        short num1 = unsignedToBytes(Memory[SPRs[9]]);
        
        SPRs[9] = (short) (SPRs[9] + 1);
        
        short num2 = unsignedToBytes(Memory[SPRs[9]]);
        
        String HexOfNum = Integer.toHexString(num1) + Integer.toHexString(num2);
        short finalnum = (short) Integer.parseInt(HexOfNum, 16);
        
        GPRs[DestinationRegister] = finalnum;
        
        //System.out.println(num1);
        //System.out.println(num2);
        //System.out.println(finalnum);
        
        SPRs[9] = (short) (SPRs[9] + 1);
        
        System.out.println("MOVI instruction executed.");

    }
    
    
    public void ADDI(){
        
        short DestinationRegister = Memory[SPRs[9]];
        //System.out.println(DestinationRegister);
        //int DestRegIndex = Integer.parseInt(Integer.toHexString(DestinationRegister),16);
        
        //Reading 2 bytes from Memory for num
        
        SPRs[9] = (short) (SPRs[9] + 1);
        
        short num1 = unsignedToBytes(Memory[SPRs[9]]);
        
        SPRs[9] = (short) (SPRs[9] + 1);
        
        short num2 = unsignedToBytes(Memory[SPRs[9]]);
        
        String HexOfNum = Integer.toHexString(num1) + Integer.toHexString(num2);
        short finalnum = (short) Integer.parseInt(HexOfNum, 16);
        
        int Result = (GPRs[DestinationRegister] + finalnum);
        
        //Checking Conditions in order to set FlagRegister
        
        //if(Result == 0)
            //setFlagRegZeroBit();
        //else if(Result < 0)
            //setFlagRegSignBit();
        
        //if(Result < -32768 || Result > 32767)
            //setFlagRegOverflowBit();
        
        GPRs[DestinationRegister] = (short) Result;
        
        //System.out.println(num1);
        //System.out.println(num2);
        //System.out.println(finalnum);
        
        SPRs[9] = (short) (SPRs[9] + 1);
        
        System.out.println("ADDI instruction executed.");

    }
    
    
    public void SUBI(){
        
        short DestinationRegister = Memory[SPRs[9]];
        //System.out.println(DestinationRegister);
        //int DestRegIndex = Integer.parseInt(Integer.toHexString(DestinationRegister),16);
        
        //Reading 2 bytes from Memory for num
        
        SPRs[9] = (short) (SPRs[9] + 1);
        
        short num1 = unsignedToBytes(Memory[SPRs[9]]);
        
        SPRs[9] = (short) (SPRs[9] + 1);
        
        short num2 = unsignedToBytes(Memory[SPRs[9]]);
        
        String HexOfNum = Integer.toHexString(num1) + Integer.toHexString(num2);
        short finalnum = (short) Integer.parseInt(HexOfNum, 16);
        
        int Result = (GPRs[DestinationRegister] - finalnum);
        
        //Checking Conditions in order to set FlagRegister
        
        //if(Result == 0)
            //setFlagRegZeroBit();
        //else if(Result < 0)
            //setFlagRegSignBit();
        
        //if(Result < -32768 || Result > 32767)
            //setFlagRegOverflowBit();
        
        GPRs[DestinationRegister] = (short) Result;
        
        //System.out.println(num1);
        //System.out.println(num2);
        //System.out.println(finalnum);
        
        SPRs[9] = (short) (SPRs[9] + 1);
        
        System.out.println("SUBI instruction executed.");

    }
    
    public void MULI(){
        
        short DestinationRegister = Memory[SPRs[9]];
        //System.out.println(DestinationRegister);
        //int DestRegIndex = Integer.parseInt(Integer.toHexString(DestinationRegister),16);
        
        //Reading 2 bytes from Memory for num
        
        SPRs[9] = (short) (SPRs[9] + 1);
        
        short num1 = unsignedToBytes(Memory[SPRs[9]]);
        
        SPRs[9] = (short) (SPRs[9] + 1);
        
        short num2 = unsignedToBytes(Memory[SPRs[9]]);
        
        String HexOfNum = Integer.toHexString(num1) + Integer.toHexString(num2);
        short finalnum = (short) Integer.parseInt(HexOfNum, 16); 
        
        int Result = (GPRs[DestinationRegister] * finalnum);
        
        //Checking Conditions in order to set FlagRegister
        
        //if(Result == 0)
            //setFlagRegZeroBit();
        //else if(Result < 0)
            //setFlagRegSignBit();
        
        //if(Result < -32768 || Result > 32767)
            //setFlagRegOverflowBit();
        
        GPRs[DestinationRegister] = (short) Result;
        
        //System.out.println(num1);
        //System.out.println(num2);
        //System.out.println(finalnum);
        
        SPRs[9] = (short) (SPRs[9] + 1);
        
        System.out.println("MULI instruction executed.");

    }
    
    public void DIVI(){
        
        short DestinationRegister = Memory[SPRs[9]];
        //System.out.println(DestinationRegister);
        //int DestRegIndex = Integer.parseInt(Integer.toHexString(DestinationRegister),16);
        
        //Reading 2 bytes from Memory for num
        
        SPRs[9] = (short) (SPRs[9] + 1);
        
        short num1 = unsignedToBytes(Memory[SPRs[9]]);
        
        SPRs[9] = (short) (SPRs[9] + 1);
        
        short num2 = unsignedToBytes(Memory[SPRs[9]]);
        
        String HexOfNum = Integer.toHexString(num1) + Integer.toHexString(num2);
        short finalnum = (short) Integer.parseInt(HexOfNum, 16);
        
        int Result = (GPRs[DestinationRegister] / finalnum);
        
        //Checking Conditions in order to set FlagRegister
        
        //if(Result == 0)
            //setFlagRegZeroBit();
        //else if(Result < 0)
            //setFlagRegSignBit();
        
        //if(Result < -32768 || Result > 32767)
            //setFlagRegOverflowBit();
        
        GPRs[DestinationRegister] = (short) Result;
        
        //System.out.println(num1);
        //System.out.println(num2);
        //System.out.println(finalnum);
        
        SPRs[9] = (short) (SPRs[9] + 1);
        
        System.out.println("DIVI instruction executed.");

    }
    
    public void ANDI(){
        
        short DestinationRegister = Memory[SPRs[9]];
        //System.out.println(DestinationRegister);
        //int DestRegIndex = Integer.parseInt(Integer.toHexString(DestinationRegister),16);
        
        //Reading 2 bytes from Memory for num
        
        SPRs[9] = (short) (SPRs[9] + 1);
        
        short num1 = unsignedToBytes(Memory[SPRs[9]]);
        
        SPRs[9] = (short) (SPRs[9] + 1);
        
        short num2 = unsignedToBytes(Memory[SPRs[9]]);
        
        String HexOfNum = Integer.toHexString(num1) + Integer.toHexString(num2);
        short finalnum = (short) Integer.parseInt(HexOfNum, 16);
        
        int Result = (GPRs[DestinationRegister] & finalnum);
        
        //Checking Conditions in order to set FlagRegister
        
        //if(Result == 0)
            //setFlagRegZeroBit();
        //else if(Result < 0)
            //setFlagRegSignBit();
        
        //if(Result < -32768 || Result > 32767)
            //setFlagRegOverflowBit();
        
        GPRs[DestinationRegister] = (short) Result;
        
        //System.out.println(num1);
        //System.out.println(num2);
        //System.out.println(finalnum);
        
        SPRs[9] = (short) (SPRs[9] + 1);
        
        System.out.println("ANDI instruction executed.");

    }
    
    public void ORI(){
        
        short DestinationRegister = Memory[SPRs[9]];
        //System.out.println(DestinationRegister);
        //int DestRegIndex = Integer.parseInt(Integer.toHexString(DestinationRegister),16);
        
        //Reading 2 bytes from Memory for num
        
        SPRs[9] = (short) (SPRs[9] + 1);
        
        short num1 = unsignedToBytes(Memory[SPRs[9]]);
        
        SPRs[9] = (short) (SPRs[9] + 1);
        
        short num2 = unsignedToBytes(Memory[SPRs[9]]);
        
        String HexOfNum = Integer.toHexString(num1) + Integer.toHexString(num2);
        short finalnum = (short) Integer.parseInt(HexOfNum, 16);
        
        int Result = (GPRs[DestinationRegister] | finalnum);
        
        //Checking Conditions in order to set FlagRegister
        
        //if(Result == 0)
            //setFlagRegZeroBit();
        //else if(Result < 0)
            //setFlagRegSignBit();
        
        //if(Result < -32768 || Result > 32767)
            //setFlagRegOverflowBit();
        
        GPRs[DestinationRegister] = (short) Result;
        
        //System.out.println(num1);
        //System.out.println(num2);
        //System.out.println(finalnum);
        
        SPRs[9] = (short) (SPRs[9] + 1);
        
        System.out.println("ORI instruction executed.");

    }
    
    
    public void BZ(){
        
        short temp = SPRs[13];
        
        String s = Integer.toBinaryString(temp);
        
        if(s.length() < 16){
            
            while(s.length() != 16)
                s = "0" + s;
            
        }
      
        if (s.charAt(s.length() - 2) == 1){  //Checking if the Zero bit of the Flag Register is 1 
            short num1 = unsignedToBytes(Memory[SPRs[9]]);
            
            SPRs[9] = (short) (SPRs[9] + 1);
            
            short num2 = unsignedToBytes(Memory[SPRs[9]]);
            
            String NumHex = Integer.toHexString(num1) + Integer.toHexString(num2);
            
            short Offset = (short) (SPRs[0] + Integer.parseInt(NumHex, 16)); //Offset = Code Base + num
            
            if (Offset <= SPRs[2])
                SPRs[9] = Offset; //Jumping to Offset
            else
                SPRs[9] = (short) (SPRs[9] + 1);
            
        }
        else{
            System.out.println("Else mein jaraha BZ");
            SPRs[9] = (short) (SPRs[9] + 2);
        }    
            
        
        System.out.println("BZ instruction executed.");
            
    }
     
     
     
    public void BNZ(){
        
        short temp = SPRs[13];
        
        String s = Integer.toBinaryString(temp);
        
        if(s.length() < 16){
            
            while(s.length() != 16)
                s = "0" + s;
            
        }
      
        if (s.charAt(s.length() - 2) != 1){ //Checking if the Zero bit of the Flag Register is not 1 
            
            short num1 = unsignedToBytes(Memory[SPRs[9]]);
            
            SPRs[9] = (short) (SPRs[9] + 1);
            
            short num2 = unsignedToBytes(Memory[SPRs[9]]);
            
            String NumHex = Integer.toHexString(num1) + Integer.toHexString(num2);
            
            short Offset = (short) (SPRs[0] + Integer.parseInt(NumHex, 16)); //Offset = Code Base + num
            
            if (Offset <= SPRs[2])
                SPRs[9] = Offset; //Jumping to Offset
            else
                SPRs[9] = (short) (SPRs[9] + 1);
            
        }
        else
            SPRs[9] = (short) (SPRs[9] + 2);
        
        System.out.println("BNZ instruction executed.");
            
    }
     
     
    public void BC(){

        short temp = SPRs[13];
        
        String s = Integer.toBinaryString(temp);
        
        if(s.length() < 16){
            
            while(s.length() != 16)
                s = "0" + s;
            
        }
        
        if (s.charAt(s.length() - 1) == '1'){  //Checking if the Carry bit of the Flag Register is 1 
            
            short num1 = unsignedToBytes(Memory[SPRs[9]]);
            
            SPRs[9] = (short) (SPRs[9] + 1);
            
            short num2 = unsignedToBytes(Memory[SPRs[9]]);
            
            String NumHex = Integer.toHexString(num1) + Integer.toHexString(num2);
            
            short Offset = (short) (SPRs[0] + Integer.parseInt(NumHex, 16));  //Offset = Code Base + num
            
            if (Offset <= SPRs[2])
                SPRs[9] = Offset;  //Jumping to Offset
            else
                SPRs[9] = (short) (SPRs[9] + 1);
            
        }
        else
            SPRs[9] = (short) (SPRs[9] + 2);
        
        System.out.println("BC instruction executed.");
       
    }
    
    public void BS(){

        short temp = SPRs[13];
        
        String s = Integer.toBinaryString(temp);
        
        if(s.length() < 16){
            
            while(s.length() != 16)
                s = "0" + s;
            
        }
        
        if (s.charAt(s.length() - 3) == '1'){   //Checking if the sign bit of the Flag Register is 1
            
            short num1 = unsignedToBytes(Memory[SPRs[9]]);
            
            SPRs[9] = (short) (SPRs[9] + 1);
            
            short num2 = unsignedToBytes(Memory[SPRs[9]]);
            
            String NumHex = Integer.toHexString(num1) + Integer.toHexString(num2);
            
            short Offset = (short) (SPRs[0] + Integer.parseInt(NumHex, 16));  //Offset = Code Base + num
            
            if (Offset <= SPRs[2])
                SPRs[9] = Offset;  //Jumping to Offset
            else
                SPRs[9] = (short) (SPRs[9] + 1);
            
        }
        else
            SPRs[9] = (short) (SPRs[9] + 2);
        
        System.out.println("BS instruction executed.");
        
       
    }
     
    public void JMP(){
        
        short num1 = unsignedToBytes(Memory[SPRs[9]]);
            
        SPRs[9] = (short) (SPRs[9] + 1);
            
        short num2 = unsignedToBytes(Memory[SPRs[9]]);
            
        String NumHex = Integer.toHexString(num1) + Integer.toHexString(num2);
            
        short Offset = (short) (SPRs[0] + Integer.parseInt(NumHex, 16));  //Offset = Code Base + num
            
        if (Offset <= SPRs[2])
            SPRs[9] = Offset;  //Jumping to Offset
        else
            SPRs[9] = (short) (SPRs[9] + 1);
        
        System.out.println("JMP instruction executed.");
        
    }
    
    
    public void MOVL(){
        
        //Load Word
        
        short DestinationRegister = Memory[SPRs[9]];
         
        SPRs[9] = (short) (SPRs[9] + 1);
        short sourceRegister = Memory[SPRs[9]];
        
        SPRs[9] = (short) (SPRs[9] + 1);
        short offset = unsignedToBytes(Memory[SPRs[9]]);
        
        short finalSourceRegister = (short) (sourceRegister + offset);
        GPRs[DestinationRegister] = Memory[finalSourceRegister];
        
        SPRs[9] = (short) (SPRs[9] + 1);
        
        System.out.println("MOVL instruction executed.");
        
    }
    
    public void MOVS(){
        
        //Store Word
           
        short SourceRegister = Memory[SPRs[9]];
         
        SPRs[9] = (short) (SPRs[9] + 1);
        short sourceRegister = Memory[SPRs[9]];
        
        SPRs[9] = (short) (SPRs[9] + 1);
        short offset = unsignedToBytes(Memory[SPRs[9]]);
         
        short finalDestinationRegister = (short) (sourceRegister+offset);
        Memory[finalDestinationRegister] = (byte) GPRs[SourceRegister];
        
        SPRs[9] = (short) (SPRs[9] + 1);
        
        System.out.println("MOVS instruction executed.");
        
    }
    
    public void SHL(){
        
        //Shift Left Logical
        
        short DestinationRegister = Memory[SPRs[9]];
         
        //SPRs[9] = (short) (SPRs[9] + 1);
        //short num = Memory[SPRs[9]];
        
        String Binary = Integer.toBinaryString(GPRs[DestinationRegister]);
        
        //if(Binary.charAt(0) == '1'){ //Checking Conditions in order to set FlagRegister
            //setFlagRegCarryBit();
        //}
            
         
        short finalnum = (short) Integer.parseInt(Integer.toBinaryString(GPRs[DestinationRegister] << 1), 2);
        
        //Checking Conditions in order to set FlagRegister
        
        //if(finalnum == 0)
            //setFlagRegZeroBit();
        //else if(finalnum < 0)
            //setFlagRegSignBit();
        
        GPRs[DestinationRegister] = finalnum;
        
        SPRs[9] = (short) (SPRs[9] + 1);
        
        System.out.println("SHL instruction executed.");
         
    }
    
    
    public void SHR(){
        
        //Shift Right Logical
        
        short DestinationRegister = Memory[SPRs[9]];
         
        //SPRs[9] = (short) (SPRs[9] + 1);
        //short num = Memory[SPRs[9]];
        
        String Binary = Integer.toBinaryString(GPRs[DestinationRegister]);
        
        //if(Binary.charAt(0) == '1'){ //Checking Conditions in order to set FlagRegister
            //setFlagRegCarryBit();
        //}
         
        short finalnum = (short) Integer.parseInt(Integer.toBinaryString(GPRs[DestinationRegister] >> 1), 2); 
        
        //Checking Conditions in order to set FlagRegister
        
        //if(finalnum == 0)
            //setFlagRegZeroBit();
        //else if(finalnum < 0)
            //setFlagRegSignBit();
        
        GPRs[DestinationRegister] = finalnum;
        
        SPRs[9] = (short) (SPRs[9] + 1);
        
        System.out.println("SHR instruction executed.");
        
    }
    
    public void RTL(){
        
        //Rotate Left
        
        short DestinationRegister = Memory[SPRs[9]];
         
        //SPRs[9] = (short) (SPRs[9] + 1);
        //short num = Memory[SPRs[9]];
        
        String Binary = Integer.toBinaryString(GPRs[DestinationRegister]);
        
        //if(Binary.charAt(0) == '1'){ //Checking Conditions in order to set FlagRegister
            //setFlagRegCarryBit();
        //}
         
        short finalnum = (short) Integer.rotateLeft(GPRs[DestinationRegister], 1);
        
        //Checking Conditions in order to set FlagRegister
        
        //if(finalnum == 0)
            //setFlagRegZeroBit();
        //else if(finalnum < 0)
            //setFlagRegSignBit();
        
        GPRs[DestinationRegister] = finalnum;
        
        SPRs[9] = (short) (SPRs[9] + 1);
        
        System.out.println("RTL instruction executed.");
    }
    
    public void RTR(){
        
        //Rotate Right
        
        short DestinationRegister = Memory[SPRs[9]];
         
        //SPRs[9] = (short) (SPRs[9] + 1);
        //short num = Memory[SPRs[9]];
        
        String Binary = Integer.toBinaryString(GPRs[DestinationRegister]);
        
        //if(Binary.charAt(0) == '1'){ //Checking Conditions in order to set FlagRegister
            //setFlagRegCarryBit();
        //}
         
        short finalnum = (short) Integer.rotateRight(GPRs[DestinationRegister], 1);
        
        //Checking Conditions in order to set FlagRegister
        
        //if(finalnum == 0)
            //setFlagRegZeroBit();
        //else if(finalnum < 0)
            //setFlagRegSignBit();
        
        GPRs[DestinationRegister] = finalnum;
                
        SPRs[9] = (short) (SPRs[9] + 1);
        
        System.out.println("RTR instruction executed.");
        
    }
    
    public void INC(){
        
        short DestinationRegister = Memory[SPRs[9]];
        
        GPRs[DestinationRegister] ++; //Incrementing the value of the Destination Register by 1
        
        SPRs[9] = (short) (SPRs[9] + 1);
        
        System.out.println("INC instruction executed.");
        
    }
    
    public void DEC(){
        
        short DestinationRegister = Memory[SPRs[9]];
        
        GPRs[DestinationRegister] --; //Decrementing the value of the destinatio register by 1
        
        SPRs[9] = (short) (SPRs[9] + 1);
        
        System.out.println("DEC instruction executed.");
         
    }
    
    public void NOOP(){
        //No Process
        System.out.println("NOOP instruction executed.");
        
    }
    
    public void print(){
        
        System.out.println("Current contents of all the registers: ");
        System.out.println("The General Purpose Registers:");
        
        for(int i = 0; i < GPRs.length; i++)   
            System.out.println("R" + i + ": " + GPRs[i]);
        
        System.out.println();
        
        System.out.println("The Special Purpose Registers:");
            
        for(int i = 0; i < SPRs.length; i++)
            System.out.println("SPR " + i + ": " + SPRs[i]);
        
        System.out.println();
        System.out.println("----------------------------------------------------------");
        System.out.println(); 
         
    }
    
    public void setFlagRegCarryBit(){
        
        //Setting Carry Bit to 1
        String FlagRegBinary = Integer.toBinaryString(SPRs[13]);
        if(FlagRegBinary.length() < 16){
            
            while(FlagRegBinary.length() != 16)
                FlagRegBinary = "0" + FlagRegBinary;
            
        }
        FlagRegBinary = FlagRegBinary.substring(0,FlagRegBinary.length() - 1) + "1";
        short NewFlagReg = (short) Integer.parseInt(FlagRegBinary, 2);
        SPRs[13] = NewFlagReg;
        
    }
    
    public void setFlagRegZeroBit(){
        
        //Setting Zero Bit to 1
        String FlagRegBinary = Integer.toBinaryString(SPRs[13]);
        if(FlagRegBinary.length() < 16){
            
            while(FlagRegBinary.length() != 16)
                FlagRegBinary = "0" + FlagRegBinary;
            
        }
        FlagRegBinary = FlagRegBinary.substring(0,FlagRegBinary.length() - 2) + "1" + FlagRegBinary.substring(FlagRegBinary.length() - 1);
        short NewFlagReg = (short) Integer.parseInt(FlagRegBinary, 2);
        SPRs[13] = NewFlagReg;
        
    }
    
    public void setFlagRegSignBit(){
        
        //Setting Sign bit to 1
        String FlagRegBinary = Integer.toBinaryString(SPRs[13]);
        if(FlagRegBinary.length() < 16){
            
            while(FlagRegBinary.length() != 16)
                FlagRegBinary = "0" + FlagRegBinary;
            
        }
        FlagRegBinary = FlagRegBinary.substring(0,FlagRegBinary.length() - 3) + "1" + FlagRegBinary.substring(FlagRegBinary.length() - 2);
        short NewFlagReg = (short) Integer.parseInt(FlagRegBinary, 2);
        SPRs[13] = NewFlagReg;
        
    }
    
    public void setFlagRegOverflowBit(){
        
        //Setting Overflow bit to 1
        String FlagRegBinary = Integer.toBinaryString(SPRs[13]);
        if(FlagRegBinary.length() < 16){
            
            while(FlagRegBinary.length() != 16)
                FlagRegBinary = "0" + FlagRegBinary;
            
        }
        FlagRegBinary = FlagRegBinary.substring(0,FlagRegBinary.length() - 4) + "1" + FlagRegBinary.substring(FlagRegBinary.length() - 3);
        short NewFlagReg = (short) Integer.parseInt(FlagRegBinary, 2);
        SPRs[13] = NewFlagReg;
        
    }
    
    
}
