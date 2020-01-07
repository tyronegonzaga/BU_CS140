package project;

import java.util.TreeMap;

import project.Instruction.DivideByZeroException;
import project.CodeAccessException;
import projectview.*;
public class MachineModel	{
	
	public class CPU	{
		private int accumulator;
		private int instructionPointer;
		private int memoryBase;
		
		public void incrementIP(int val) {
			instructionPointer += val;
		}
	}
	
	public static final TreeMap<Integer, Instruction> INSTRUCTIONS = new TreeMap<Integer, Instruction>();
	private CPU cpu = new CPU();
	private Memory memory = new Memory();
	private boolean withGUI;
	private HaltCallback callback;
	
	
	// ******** Part Two *******
		private Job[] jobs = new Job[2];
		
		private Job currentJob;
		
		public Job getCurrentJob() {
			return currentJob;
		}
		
		public void setJob(int i) {
			if(i != 0 && i != 1) throw new IllegalArgumentException("Must input either 0 or 1");
			currentJob.setCurrentAcc(cpu.accumulator);
			currentJob.setCurrentIP(cpu.instructionPointer);
			
			currentJob = jobs[i];
			
			cpu.accumulator = currentJob.getCurrentAcc();
			cpu.instructionPointer = currentJob.getCurrentIP();
			cpu.memoryBase = currentJob.getStartmemoryIndex();
			}
	
	//***********************************************************************************************
	
		public MachineModel() {
			this(false, null);
		}	
		
		
	public MachineModel(boolean hasGUI, HaltCallback setcallback) {
		withGUI = hasGUI;
		callback = setcallback;
		for(int i=0; i<2; i++) {
			jobs[i] = new Job();
		}
		
		
		currentJob = jobs[0];
		jobs[0].setStartcodeIndex(0);
		jobs[0].setStartmemoryIndex(0);
		jobs[0].setCurrentState(States.NOTHING_LOADED);
		
		jobs[1].setStartcodeIndex(Memory.CODE_MAX/4);
		jobs[1].setStartmemoryIndex(Memory.DATA_SIZE/2);
		jobs[1].setCurrentState(States.NOTHING_LOADED);
		
		

		//INSTRUCTION_MAP entry for "NOP"	0x0
		INSTRUCTIONS.put(0x0, arg -> {
            cpu.incrementIP(1);
        });
		
		//INSTRUCTION_MAP entry for "LODI"	0x1
		INSTRUCTIONS.put(0x1, arg -> {
			cpu.accumulator = arg;
			cpu.incrementIP(1);
        });
		
		//INSTRUCTION_MAP entry for "LOD"	0x2
		INSTRUCTIONS.put(0x2, arg -> {
			int arg1 = memory.getData(cpu.memoryBase + arg);
			cpu.accumulator = arg1;
			cpu.incrementIP(1);
        });			
		
		//INSTRUCTION_MAP entry for "LODN"	0x3
		INSTRUCTIONS.put(0x3, arg -> {
			int arg1 = memory.getData(cpu.memoryBase + arg);
			int arg2 = memory.getData(cpu.memoryBase + arg1);
			cpu.accumulator = arg2;
			cpu.incrementIP(1);
        });
		
		//INSTRUCTION_MAP entry for "STO"	0x4
		INSTRUCTIONS.put(0x4, arg -> {
			memory.setData((cpu.memoryBase + arg), cpu.accumulator);
			cpu.incrementIP(1);
		});
		
		//INSTRUCTION_MAP entry for "STON"	0x5
		INSTRUCTIONS.put(0x5, arg -> {
			int val = memory.getData(cpu.memoryBase + arg);
			memory.setData((cpu.memoryBase + val), cpu.accumulator);
			cpu.incrementIP(1);
		});	
		
		//INSTRUCTION_MAP entry for "JMPR"	0x6
		INSTRUCTIONS.put(0x6, arg -> {
			//cpu.instructionPointer += arg;
			cpu.incrementIP(arg);
			
		});	
		
		//INSTRUCTION_MAP entry for "JUMP"	0x7
		INSTRUCTIONS.put(0x7, arg -> {
			//cpu.instructionPointer += (cpu.memoryBase + arg);
			cpu.incrementIP(memory.getData(cpu.memoryBase + arg));
		});	
		
		//INSTRUCTION_MAP entry for "JUMPI"	0x8
		INSTRUCTIONS.put(8, arg -> {
			cpu.instructionPointer = currentJob.getStartcodeIndex() + arg;
		});
		
		//INSTRUCTION_MAP entry for "JMPZR"	0x9
		INSTRUCTIONS.put(0x9, arg -> {
			if(cpu.accumulator == 0) {
				//cpu.instructionPointer += arg;
				cpu.incrementIP(arg);
			}
			else {
				cpu.incrementIP(1);
			}
		});	
		
		//INSTRUCTION_MAP entry for "JMPZ"	0xA
		INSTRUCTIONS.put(0xA, arg -> {
			if(cpu.accumulator == 0) {
				//cpu.instructionPointer += (cpu.memoryBase + arg);
				cpu.incrementIP(memory.getData(cpu.memoryBase + arg));
			}
			else {
				cpu.incrementIP(1);
			}
		});	
		
		//INSTRUCTION_MAP entry for "JMPZI"	0xB
		INSTRUCTIONS.put(0xB, arg -> {
			if(cpu.accumulator == 0)
				cpu.instructionPointer = currentJob.getStartcodeIndex() + arg;
			else
				cpu.incrementIP(1);
		});
		
		  //INSTRUCTION_MAP entry for "ADDI"
        INSTRUCTIONS.put(0xC, arg -> {
            cpu.accumulator += arg;
            cpu.incrementIP(1);
        });

        //INSTRUCTION_MAP entry for "ADD"
        INSTRUCTIONS.put(0xD, arg -> {
            int arg1 = memory.getData(cpu.memoryBase+arg);
            cpu.accumulator += arg1;
            cpu.incrementIP(1);
        });

        //INSTRUCTION_MAP entry for "ADDN"
        INSTRUCTIONS.put(0xE, arg -> {
            int arg1 = memory.getData(cpu.memoryBase+arg);
            int arg2 = memory.getData(cpu.memoryBase+arg1);
            cpu.accumulator += arg2;
            cpu.incrementIP(1);
        });
		
        //INSTRUCTION_MAP entry for SUBI 0xF
        INSTRUCTIONS.put(0xF, arg -> {
            cpu.accumulator -= arg;
            cpu.incrementIP(1);
        });
        
        //INSTRUCTION_MAP entry for SUB 0x10
        INSTRUCTIONS.put(0x10, arg -> {
            int arg1 = memory.getData(cpu.memoryBase+arg);
            cpu.accumulator -= arg1;
            cpu.incrementIP(1);
        });

        
        //INSTRUCTION_MAP entry for SUBN 0x11
        INSTRUCTIONS.put(0x11, arg -> {
            int arg1 = memory.getData(cpu.memoryBase+arg);
            int arg2 = memory.getData(cpu.memoryBase+arg1);
            cpu.accumulator -= arg2;
            cpu.incrementIP(1);
        });
        
        //INSTRUCTION_MAP entry for MULI 0x12
        INSTRUCTIONS.put(0x12, arg -> {
            cpu.accumulator *= arg;
            cpu.incrementIP(1);
        });
        
        //INSTRUCTION_MAP entry for MUL 0x13
        INSTRUCTIONS.put(0x13, arg -> {
            int arg1 = memory.getData(cpu.memoryBase+arg);
            cpu.accumulator *= arg1;
            cpu.incrementIP(1);
        });
        
        //INSTRUCTION_MAP entry for MULN 0x14
        INSTRUCTIONS.put(0x14, arg -> {
            int arg1 = memory.getData(cpu.memoryBase+arg);
            int arg2 = memory.getData(cpu.memoryBase+arg1);
            cpu.accumulator *= arg2;
            cpu.incrementIP(1);
        });
        
        //INSTRUCTION_MAP entry for DIVI 0x15
        INSTRUCTIONS.put(0x15, arg -> {
        	if(arg == 0) {
        		throw new DivideByZeroException("Cannot divide by zero");
        	}
            cpu.accumulator /= arg;
            cpu.incrementIP(1);
        });
        
        //INSTRUCTION_MAP entry for DIV 0x16
        INSTRUCTIONS.put(0x16, arg -> {
            int arg1 = memory.getData(cpu.memoryBase+arg);
            if(arg1 == 0) {
        		throw new DivideByZeroException("Cannot divide by zero");
        	}
            cpu.accumulator /= arg1;
            cpu.incrementIP(1);
        });
        
        //INSTRUCTION_MAP entry for DIVN 0x17
        INSTRUCTIONS.put(0x17, arg -> {
            int arg1 = memory.getData(cpu.memoryBase+arg);
            int arg2 = memory.getData(cpu.memoryBase+arg1);
            if(arg2 == 0) {
        		throw new DivideByZeroException("Cannot divide by zero");
        	}
            cpu.accumulator /= arg2;
            cpu.incrementIP(1);
        });
        
        //INSTRUCTION_MAP entry for ANDI 0x18
        INSTRUCTIONS.put(0x18, arg -> {
        	if(cpu.accumulator != 0 && arg != 0) {
        		cpu.accumulator = 1;
        		cpu.incrementIP(1);
        	}
        	else {
        		cpu.accumulator = 0;
        		cpu.incrementIP(1);
        	}
        });
        	
        //INSTRUCTION_MAP entry for AND 0x19
        INSTRUCTIONS.put(0x19, arg -> {
        	if(cpu.accumulator != 0 && memory.getData(cpu.memoryBase + arg) != 0) {
        		cpu.accumulator = 1;
        		cpu.incrementIP(1);
        	}
        	else {
        		cpu.accumulator = 0;
        		cpu.incrementIP(1);
        	}
        });
        
        //INSTRUCTION_MAP entry for NOT 0x1A
        INSTRUCTIONS.put(0x1A, arg -> {
        	if(cpu.accumulator != 0) {
        		cpu.accumulator = 0;
        		cpu.incrementIP(1);
        	}
        	else if(cpu.accumulator == 0) {
        		cpu.accumulator = 1;
        		cpu.incrementIP(1);
        	}
        });
        
        //INSTRUCTION_MAP entry for CMPL 0x1B
        INSTRUCTIONS.put(0x1B, arg -> {
        	if(memory.getData(cpu.memoryBase + arg) < 0) {
        		cpu.accumulator = 1;
        		cpu.incrementIP(1);
        	}
        	else {
        		cpu.accumulator = 0;
        		cpu.incrementIP(1);
        	}
        });
        
        //INSTRUCTION_MAP entry for CMPZ 0x1C
        INSTRUCTIONS.put(0x1C, arg -> {
        	if(memory.getData(cpu.memoryBase + arg) == 0) {
        		cpu.accumulator = 1;
        		cpu.incrementIP(1);
        	}
        	else {
        		cpu.accumulator = 0;
        		cpu.incrementIP(1);
        	}
        });
        
      //INSTRUCTION_MAP entry for "JUMPN"
        INSTRUCTIONS.put(29, arg -> {
        	int arg1 = memory.getData(cpu.memoryBase+arg);
        	cpu.instructionPointer = currentJob.getStartcodeIndex() + arg1;
        });
        
        //INSTRUCTION_MAP entry for DIVN 0x1F
        INSTRUCTIONS.put(0x1F, arg -> {
        	callback.halt();			
        });
        
	}

	public int[] getData() {
		return memory.getData();
	}
	public int getData(int index) {
		return memory.getData(index);
	}
	public void setData(int index, int value) {
		memory.setData(index, value);
	}
	
	public int getChangedIndex() {
		return memory.getChangedIndex();
	}
	
	public void setCode(int index, int op, int value) {
		memory.setCode(index, op, value);
	}
	
	public int getAccumulator()	{
		return cpu.accumulator;
	}
	public int getInstructionPointer() {
		return cpu.instructionPointer;
	}
	public int getMemoryBase() {
		return cpu.memoryBase;
	}
	
	public int getOp(int i) {
		return memory.getOp(i);
	}
	
	public int getArg(int i) {
		return memory.getArg(i);
	}
	
	public Instruction get(int key) {
		return INSTRUCTIONS.get(key);
	}

	public void setAccumulator(int i) {
		cpu.accumulator = i;
		
	}

	public void setInstructionPointer(int ipInit) {
		cpu.instructionPointer = ipInit;
		
	}
	
	public int[] getCode() {
		return memory.getCode();
	}
	
	public String getHex(int i) {
		return memory.getHex(i);
	}
	
	public String getDecimal(int i) {
		return memory.getDecimal(i);
	}
	
	public States getCurrentState() {
		return currentJob.getCurrentState();
	}
	
	public void setCurrentState(States currentState) {
		currentJob.setCurrentState(currentState);
	} 
	
	public void clearJob() {
		memory.clearData(currentJob.getStartmemoryIndex(), currentJob.getStartmemoryIndex()+Memory.DATA_SIZE/2);
		memory.clearCode(currentJob.getStartcodeIndex(), currentJob.getStartcodeIndex()+currentJob.getCodeSize());
		cpu.accumulator = 0;
		cpu.instructionPointer = currentJob.getStartcodeIndex();
		currentJob.reset();
	}
	
	
	
	public void step() {System.out.print("enteringStep");
		try { 
			int ip = cpu.instructionPointer;
			if(!(ip >= currentJob.getStartcodeIndex()) && !(ip < currentJob.getStartcodeIndex()+currentJob.getCodeSize())) {
				throw new CodeAccessException("Illegal Code Access");
			}
			int opcode = getOp(ip);
			int arg = getArg(ip);
			System.out.println(opcode + " " + arg);
			get(opcode).execute(arg);
			
			
		}catch(Exception e) {
			System.out.println("halt");
			callback.halt();
			throw e;
		}
	}
	
	
	
}
