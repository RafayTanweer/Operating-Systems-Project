
import java.io.FileNotFoundException;
import java.io.IOException;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

//GROUP MEMBERS:
//Abdur Rafay Tanweer 19734
//Ali Muhammad Aslam 18585
//Abdur Rafai Tariq 18583

public class Run {
    
    
    public static void main(String[] args) throws FileNotFoundException, IOException {
        
        //Rough Code
        
        /*int[] Array = new int[5];
        for(int i = 0; i < 5; i++){
            Array[i] = i;
        }
        
        int a = Array[1];
        //System.out.println(a);
        Array[1] = 99;
        //System.out.println(a);
        
        byte Var = 1;
        
        String opcode1 = "30";
        String opcode2 = "30";
        
        System.out.println(opcode1 == opcode2);
        
        short b = 258;
        short c = 260;
        
        System.out.println(b & c);
        
        short num1 = 0;
        short num2 = 1;
        String HexOfNum = Integer.toHexString(num1) + Integer.toHexString(num2);
        System.out.println(HexOfNum);
        short finalnum = (short) Integer.parseInt(HexOfNum, 16);
        System.out.println(finalnum);
        
        
        String[] hex_instr = {"30","01","00","01","30","02","7F","FF","19","01","02","F3"};
        
        byte[] byte_instr = new byte[12];
        
        for(int i = 0; i < hex_instr.length; i++){
            
            byte_instr[i] = (byte) Integer.parseInt(hex_instr[i], 16);
            System.out.println(byte_instr[i]);
            
        }
        
        byte a = 1;
        
        short b = (short) (a & 0xFF);
        
        System.out.println(b);
        
        System.out.println(Integer.toHexString(26));*/
        
        //------------------------------------------------------------------------------------------------------------------------
        
        OperatingSystemPhaseOneAndPhaseTwo RunOS = new OperatingSystemPhaseOneAndPhaseTwo(); //Creating an object of the OperatingSystemPhase1 class
        
        RunOS.FetchDecodeExecute(); //Running the FetchDecodeExecute function
        
        
        
    }
    
}
