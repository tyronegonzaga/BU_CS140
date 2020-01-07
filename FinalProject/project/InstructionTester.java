package project;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import project.Instruction.DivideByZeroException;

public class InstructionTester {
	MachineModel machine = new MachineModel();
	int[] dataCopy = new int[Memory.DATA_SIZE];
	int accInit;
	int ipInit;

	@Before
	public void setup() {
		for (int i = 0; i < Memory.DATA_SIZE; i++) {
			dataCopy[i] = -5*Memory.DATA_SIZE + 10*i;
			machine.setData(i, -5*Memory.DATA_SIZE + 10*i);
			// Initially the machine will contain a known spread
			// of different numbers: 
			// -10240, -19230, -10220, ..., 0, 10, 20, ..., 10230 
			// This allows us to check that the instructions do 
			// not corrupt machine unexpectedly.
			// 0 is at index 1024
		}
		accInit = 50;
		ipInit = 100;
		machine.setAccumulator(accInit);
		machine.setInstructionPointer(ipInit);
	}


	@Test
	public void testNOP(){
		Instruction instr = machine.get(0x0);
		instr.execute(0);
		//Test machine is not changed
		assertArrayEquals(dataCopy, machine.getData());
		//Test program counter incremented
		assertEquals("Program counter incremented", ipInit+1,
				machine.getInstructionPointer());
		//Test accumulator untouched
		assertEquals("Accumulator unchanged", accInit,
				machine.getAccumulator());
	}

	@Test
	// Test whether load is correct with immediate addressing
	public void testLODI(){
		Instruction instr = machine.get(0x1);
		int arg = 12;
			// should load 12 into the accumulator
			instr.execute(arg);
		//Test machine is not changed
        assertArrayEquals(dataCopy, machine.getData());
        //Test program counter incremented
        assertEquals("Program counter incremented", ipInit+1,
                machine.getInstructionPointer());
        //Test accumulator modified
        assertEquals("Accumulator changed", 12,
                machine.getAccumulator());
	}

	@Test
	// Test whether load is correct with direct addressing
	public void testLOD(){
		Instruction instr = machine.get(0x2);
		int arg = 12;
			// should load -10240+120 into the accumulator
		instr.execute(arg);
		//Test machine is not changed
        assertArrayEquals(dataCopy, machine.getData());
        //Test program counter incremented
        assertEquals("Program counter incremented", ipInit+1,
        		machine.getInstructionPointer());
        //Test accumulator modified
        assertEquals("Accumulator changed", -10240+120,
        		machine.getAccumulator());
	}

	@Test
	// Test whether load is correct with direct addressing
	public void testLODN() {
		Instruction instr = machine.get(0x3);
		int arg = 1030; // we know that address is -10240+10300 = 60
		// should load data[-10240+10300] = data[60] = -10240 + 600
		// into the accumulator
		instr.execute(arg);
		//Test machine is not changed
        assertArrayEquals(dataCopy, machine.getData());
        //Test program counter incremented
        assertEquals("Program counter incremented", ipInit+1,
                machine.getInstructionPointer());
        //Test accumulator modified
        assertEquals("Accumulator changed", -10240 + 600,
                machine.getAccumulator());
	}	
	
	@Test
	// Test whether store is correct with direct addressing
	public void testSTO() {
		Instruction instr = machine.get(0x4);
		int arg = 12;
		dataCopy[12] = accInit;
		instr.execute(arg);
		//Test machine is changed correctly
		assertArrayEquals(dataCopy, machine.getData());
		//Test program counter incremented
		assertEquals("Program counter incremented", ipInit+1,
				machine.getInstructionPointer());
		//Test accumulator unchanged
		assertEquals("Accumulator unchanged", accInit,
				machine.getAccumulator());
	}

	@Test
	// Test whether store is correct with indirect addressing
	public void testSTON() {
		Instruction instr = machine.get(0x5);
		int arg = 1030; // we know that address is -10240+10300 = 60
		dataCopy[60] = accInit;
		instr.execute(arg);
		//Test machine is changed correctly
		assertArrayEquals(dataCopy, machine.getData());
		//Test program counter incremented
		assertEquals("Program counter incremented", ipInit+1,
				machine.getInstructionPointer());
		//Test accumulator unchanged
		assertEquals("Accumulator unchanged", accInit,
				machine.getAccumulator());
	}

	@Test 
	// this test checks whether the positive relative jump is done correctly
	public void testJMPRpos() {
		Instruction instr = machine.get(0x6);
		int arg = 260;  
		instr.execute(arg); 
		// should have set the instruction pointer to ipInit+260,
		assertArrayEquals(dataCopy, machine.getData()); 
		assertEquals("Instruction pointer was changed", ipInit+260,
				machine.getInstructionPointer());
		assertEquals("Accumulator was not changed", accInit,
				machine.getAccumulator());
	}

	@Test 
	// this test checks whether the negative relative jump is done correctly
	public void testJMPRneg() {
		Instruction instr = machine.get(0x6);
		int arg = -260;  
		instr.execute(arg); 
		// should have set the instruction pointer to ipInit-260
		assertArrayEquals(dataCopy, machine.getData()); 
		assertEquals("Instruction pointer was changed", ipInit-260,
				machine.getInstructionPointer());
		assertEquals("Accumulator was not changed", accInit,
				machine.getAccumulator());
	}

	@Test 
	// this test checks whether the zero relative jump is done correctly
	public void testJMPRzero() {
		Instruction instr = machine.get(0x6);
		int arg = 0;  
		instr.execute(arg); 
		// should have set the instruction pointer to ipInit
		System.out.println("machine.getInstructionPointer()" + machine.getInstructionPointer());
		assertArrayEquals(dataCopy, machine.getData()); 
		assertEquals("Instruction pointer was not changed", ipInit,
				machine.getInstructionPointer());
		assertEquals("Accumulator was not changed", accInit,
				machine.getAccumulator());
	}

	@Test 
	// this test checks whether the positive direct relative jump is done correctly
	public void testJUMPpos() {
		Instruction instr = machine.get(0x7);
		int arg = 1030;  
		instr.execute(arg); 
		// should have set the instruction pointer to ipInit+60
		assertArrayEquals(dataCopy, machine.getData()); 
		assertEquals("Instruction pointer was changed", ipInit+60,
				machine.getInstructionPointer());
		assertEquals("Accumulator was not changed", accInit,
				machine.getAccumulator());
	}

	@Test 
	// this test checks whether the negative direct relative jump is done correctly
	public void testJUMPneg() {
		Instruction instr = machine.get(0x7);
		int arg = 1020;  
		instr.execute(arg); 
		// should have set the instruction pointer to ipInit-40
		assertArrayEquals(dataCopy, machine.getData()); 
		assertEquals("Instruction pointer was changed", ipInit-40,
				machine.getInstructionPointer());
		assertEquals("Accumulator was not changed", accInit,
				machine.getAccumulator());
	}

	@Test 
	// this test checks whether the zero direct relative jump is done correctly
	public void testJUMPzero() {
		Instruction instr = machine.get(0x7);
		int arg = 1024;  
		// does not affect the instruction pointer
		instr.execute(arg); 
		assertArrayEquals(dataCopy, machine.getData()); 
		assertEquals("Instruction pointer was not changed", ipInit,
				machine.getInstructionPointer());
		assertEquals("Accumulator was not changed", accInit,
				machine.getAccumulator());
	}

	@Test 
	// this test checks whether the jump immediate is done correctly, when
	// address is the argument
	public void testJUMPI() {
		Instruction instr = machine.get(0x8);
		int arg = 260;  
		instr.execute(arg); 
		// should have set the instruction pointer to 260
		assertArrayEquals(dataCopy, machine.getData()); 
		assertEquals("Instruction pointer was changed", 260,
				machine.getInstructionPointer());
		assertEquals("Accumulator was not changed", accInit,
				machine.getAccumulator());
	}

	@Test 
	// this test checks whether the positive relative jump-zero is done correctly
	// when accum is zero
	public void testJMPZRpos() {
		Instruction instr = machine.get(0x9);
		int arg = 260;  
		machine.setAccumulator(0);
		instr.execute(arg); 
		// should have set the instruction pointer to ipInit+260
		assertArrayEquals(dataCopy, machine.getData()); 
		assertEquals("Instruction pointer was changed", ipInit+260,
				machine.getInstructionPointer());
		assertEquals("Accumulator was not changed", 0,
				machine.getAccumulator());
	}

	@Test 
	// this test checks whether the negative relative jump-zero is done correctly
	//when accum is 0
	public void testJMPZRneg() {
		Instruction instr = machine.get(0x9);
		int arg = -260;  
		machine.setAccumulator(0);
		instr.execute(arg); 
		// should have set the instruction pointer to ipInit-260
		assertArrayEquals(dataCopy, machine.getData()); 
		assertEquals("Instruction pointer was changed", ipInit-260,
				machine.getInstructionPointer());
		assertEquals("Accumulator was not changed", 0,
				machine.getAccumulator());
	}

	@Test 
	// this test checks whether the zero relative jump-zero is done correctly
	//when accum is zero
	public void testJMPZRzero() {
		Instruction instr = machine.get(0x9);
		int arg = 0;  
		machine.setAccumulator(0);
		instr.execute(arg); 
		// should have left the instruction pointer to ipInit
		assertArrayEquals(dataCopy, machine.getData()); 
		assertEquals("Instruction pointer was not changed", ipInit,
				machine.getInstructionPointer());
		assertEquals("Accumulator was not changed", 0,
				machine.getAccumulator());
	}

	@Test 
	// this test checks whether the jump-zero is done correctly, when
	// accum is not zero
	public void testJMZRaccnonzero() {
		Instruction instr = machine.get(0x9);
		int arg = 260;  
		instr.execute(arg); 
		// increments instruction pointer
		assertArrayEquals(dataCopy, machine.getData()); 
		assertEquals("Instruction pointer was incremented", ipInit+1,
				machine.getInstructionPointer());
		assertEquals("Accumulator was not changed", accInit,
				machine.getAccumulator());
	}

	@Test 
	// this test checks whether the positive direct relative jump-zero is done correctly
	// when accum is zero
	public void testJMPZpos() {
		Instruction instr = machine.get(0xA);
		int arg = 1030;  
		machine.setAccumulator(0);
		instr.execute(arg); 
		// should have set the instruction pointer to ipInit+60
		assertArrayEquals(dataCopy, machine.getData()); 
		assertEquals("Instruction pointer was changed", ipInit+60,
				machine.getInstructionPointer());
		assertEquals("Accumulator was not changed", 0,
				machine.getAccumulator());
	}

	@Test 
	// this test checks whether the negative direct relative jump-zero is done correctly
	// when accum is zero
	public void testJMPZneg() {
		Instruction instr = machine.get(0xA);
		int arg = 1020;  
		machine.setAccumulator(0);
		instr.execute(arg); 
		// should have set the instruction pointer to ipInit-40
		assertArrayEquals(dataCopy, machine.getData()); 
		assertEquals("Instruction pointer was changed", ipInit-40,
				machine.getInstructionPointer());
		assertEquals("Accumulator was not changed", 0,
				machine.getAccumulator());
	}

	@Test 
	// this test checks whether the zero direct relative jump-zero is done correctly
	// when accum is zero
	public void testJMPZzero() {
		Instruction instr = machine.get(0xA);
		int arg = 1024;  
		machine.setAccumulator(0);
		// does not affect the instruction pointer
		instr.execute(arg); 
		assertArrayEquals(dataCopy, machine.getData()); 
		assertEquals("Instruction pointer was not changed", ipInit,
				machine.getInstructionPointer());
		assertEquals("Accumulator was not changed", 0,
				machine.getAccumulator());
	}

	@Test 
	// this test checks whether the jump is done correctly, when
	// accum is not zero
	public void testJMPZacczero() {
		Instruction instr = machine.get(0xA);
		int arg = 1030;  
		instr.execute(arg); 
		// increments instruction pointer
		assertArrayEquals(dataCopy, machine.getData()); 
		assertEquals("Instruction pointer was incremented", ipInit+1,
				machine.getInstructionPointer());
		assertEquals("Accumulator was not changed", accInit,
				machine.getAccumulator());
	}

	@Test 
	// this test checks whether immediate jump-zero is done if accumulator is zero, 
	public void testJMPZIaccumZero() {
		Instruction instr = machine.get(0xB);
		int arg = 260;  
		machine.setAccumulator(0);
		instr.execute(arg); 
		// should have set the program counter incremented
		assertArrayEquals(dataCopy, machine.getData()); 
		assertEquals("Program counter was set to 260", 260,
				machine.getInstructionPointer());
		assertEquals("Accumulator was not changed", 0,
				machine.getAccumulator());
	}

	@Test 
	// this test checks whether immediate jump-zero is done if accumulator is not zero, 
	public void testJMPZIaccumNonZero() {
		Instruction instr = machine.get(0xB);
		int arg = 260;  
		instr.execute(arg); 
		// should have set the program counter incremented
		assertArrayEquals(dataCopy, machine.getData()); 
		assertEquals("Program counter was incremented", ipInit + 1,
				machine.getInstructionPointer());
		assertEquals("Accumulator was not changed", accInit,
				machine.getAccumulator());
	}

	@Test 
	// this test checks whether the add is done correctly, when
	// addressing is immediate
	public void testADDI() {
		Instruction instr = machine.get(0xC);
		int arg = 12; 
		machine.setAccumulator(200);
		instr.execute(arg); 
		// should have added 12 to accumulator
		assertArrayEquals(dataCopy, machine.getData()); 
		assertEquals("Program counter was incremented", ipInit + 1,
				machine.getInstructionPointer());
		assertEquals("Accumulator was changed", 200+12,
				machine.getAccumulator());
	}

	@Test 
	// this test checks whether the add is done correctly, when
	// addressing is direct
	public void testADD() {
		Instruction instr = machine.get(0xD);
		int arg = 12; // we know that machine value is -10240+120
		machine.setAccumulator(200);
		instr.execute(arg); 
		// should have added -2560+120 to accumulator
		assertArrayEquals(dataCopy, machine.getData()); 
		assertEquals("Program counter was incremented", ipInit + 1,
				machine.getInstructionPointer());
		assertEquals("Accumulator was changed", 200-10240+120,
				machine.getAccumulator());
	}

	@Test 
	// this test checks whether the add is done correctly, when
	// addressing is indirect
	public void testADDN() {
		Instruction instr = machine.get(0xE);
		int arg = 1030; // we know that address is -10240+10300 = 60
		// and the machine value is data[60] = -10240+600 
		machine.setAccumulator(200);
		instr.execute(arg); 
		// should have added -2560+400 to accumulator
		assertArrayEquals(dataCopy, machine.getData()); 
		assertEquals("Program counter was incremented", ipInit + 1,
				machine.getInstructionPointer());
		assertEquals("Accumulator was changed", 200-10240+600,
				machine.getAccumulator());
	}

	@Test 
	// this test checks whether the subtract is done correctly, when
	// addressing is immediate
	public void testSUBI() {
		Instruction instr = machine.get(0xF);
		int arg = 12; 
		machine.setAccumulator(200);
		instr.execute(arg); 
		// should have subtracted 12 from accumulator
		assertArrayEquals(dataCopy, machine.getData()); 
		assertEquals("Program counter was incremented", ipInit + 1,
				machine.getInstructionPointer());
		assertEquals("Accumulator was changed", 200-12,
				machine.getAccumulator());
	}

	@Test 
	// this test checks whether the subtract is done correctly, when
	// addressing is direct
	public void testSUB() {
		Instruction instr = machine.get(0x10);
		int arg = 12; // we know that machine value is -10240+120
		machine.setAccumulator(200);
		instr.execute(arg); 
		// should have subtracted -10240+120 from accumulator
		assertArrayEquals(dataCopy, machine.getData()); 
		assertEquals("Program counter was incremented", ipInit + 1,
				machine.getInstructionPointer());
		assertEquals("Accumulator was changed", 200+10240-120,
				machine.getAccumulator());
	}

	@Test 
	// this test checks whether the subtract is done correctly, when
	// addressing is indirect
	public void testSUBN() {
		Instruction instr = machine.get(0x11);
		int arg = 1030; // we know that address is -10240+10300 = 60
		// and the machine value is data[60] = -10240+600 
		machine.setAccumulator(200);
		instr.execute(arg); 
		// should have subtracted -10240+600 from accumulator
		assertArrayEquals(dataCopy, machine.getData()); 
		assertEquals("Program counter was incremented", ipInit + 1,
				machine.getInstructionPointer());
		assertEquals("Accumulator was changed", 200-(-10240+600),
				machine.getAccumulator());
	}

	@Test 
	// this test checks whether the multiplication is done correctly, when
	// addressing is immediate
	public void testMULI() {
		Instruction instr = machine.get(0x12);
		int arg = 12; 
		machine.setAccumulator(200);
		instr.execute(arg); 
		// should have multiplied accumulator by 12
		assertArrayEquals(dataCopy, machine.getData()); 
		assertEquals("Program counter was incremented", ipInit + 1,
				machine.getInstructionPointer());
		assertEquals("Accumulator was changed", 200*12,
				machine.getAccumulator());
	}

	@Test 
	// this test checks whether the multiplication is done correctly, when
	// addressing is direct
	public void testMUL() {
		Instruction instr = machine.get(0x13);
		int arg = 12; // we know that machine value is -10240+120
		machine.setAccumulator(200);
		instr.execute(arg); 
		// should have multiplied accumulator by -10240+120 
		assertArrayEquals(dataCopy, machine.getData()); 
		assertEquals("Program counter was incremented", ipInit + 1,
				machine.getInstructionPointer());
		assertEquals("Accumulator was changed", 200*(-10240+120),
				machine.getAccumulator());
	}

	@Test 
	// this test checks whether the multiplication is done correctly, when
	// addressing is indirect
	public void testMULN() {
		Instruction instr = machine.get(0x14);
		int arg = 1030; // we know that address is -10240+10300 = 60
		// and the machine value is data[60] = -10240+600 
		machine.setAccumulator(200);
		instr.execute(arg); 
		// should have multiplied to accumulator -10240+600
		assertArrayEquals(dataCopy, machine.getData()); 
		assertEquals("Program counter was incremented", ipInit + 1,
				machine.getInstructionPointer());
		assertEquals("Accumulator was changed", 200*(-10240+600),
				machine.getAccumulator());
	}

 	@Test 
	// this test checks whether the division is done correctly, when
	// addressing is immediate
	public void testDIVI() {
		Instruction instr = machine.get(0x15);
		int arg = 12; 
		machine.setAccumulator(200);
		instr.execute(arg); 
		// should have divided accumulator by 12
		assertArrayEquals(dataCopy, machine.getData()); 
		assertEquals("Program counter was incremented", ipInit + 1,
				machine.getInstructionPointer());
		assertEquals("Accumulator was changed", 200/12,
				machine.getAccumulator());
	}

	@Test 
	// this test checks whether the division is done correctly, when
	// addressing is direct
	public void testDIV() {
		Instruction instr = machine.get(0x16);
		int arg = 12; // we know that machine value is -2560+120
		machine.setAccumulator(200);
		instr.execute(arg); 
		// should have divided accumulator by -2560+120 
		assertArrayEquals(dataCopy, machine.getData()); 
		assertEquals("Program counter was incremented", ipInit + 1,
				machine.getInstructionPointer());
		assertEquals("Accumulator was changed", 200/(-2560+120),
				machine.getAccumulator());
	}

	@Test 
	// this test checks whether the division is done correctly, when
	// addressing is indirect
	public void testDIVN() {
		Instruction instr = machine.get(0x17);
		int arg = 1126; // we know that address is -10240+11260 = 1020
		// and the machine value is data[1020] = -10240+10200 = -40 
		machine.setAccumulator(200);
		instr.execute(arg); 
		// should have divided to accumulator -40
		assertArrayEquals(dataCopy, machine.getData()); 
		assertEquals("Program counter was incremented", ipInit + 1,
				machine.getInstructionPointer());
		assertEquals("Accumulator was changed", 200/(-10240+10200),
				machine.getAccumulator());
	}
	
	@Test (expected=DivideByZeroException.class) 
	// this test checks whether the DivideByZeroException is thrown 
	// for immediate division by 0
	public void testDIVIzero() {
		Instruction instr = machine.get(0x15);
		int arg = 0; 
		instr.execute(arg);
	}

	@Test (expected=DivideByZeroException.class) 
	// this test checks whether the DivideByZeroException is thrown 
	// for division by 0 from machine
	public void testDIVzero() {
		Instruction instr = machine.get(0x16);
		int arg = 1024; 
		instr.execute(arg);
	}

	@Test (expected=DivideByZeroException.class) 
	// this test checks whether the DivideByZeroException is thrown 
	// for division by 0 from machine
	public void testDIVNzero() {
		Instruction instr = machine.get(0x17);
		machine.setData(100, 1024);
		int arg = 100; 
		instr.execute(arg);
	}
	
	@Test
	// Check ANDI when accum and arg equal to 0 gives false
	public void testANDIaccEQ0argEQ0() {
		Instruction instr = machine.get(0x18);
		int arg = 0;
		machine.setAccumulator(0);
		instr.execute(arg);
		//Test machine is not changed
        assertArrayEquals(dataCopy, machine.getData()); 
        //Test program counter incremented
        assertEquals("Program counter incremented", ipInit + 1,
                machine.getInstructionPointer());
        //Accumulator is 1
        assertEquals("Accumulator is 0", 0,
                machine.getAccumulator());
	}
	
	@Test
	// Check ANDI when accum and arg pos gives true
	public void testANDIaccGT0argGT0() {
		Instruction instr = machine.get(0x18);
		int arg = 300;
		machine.setAccumulator(10);
		instr.execute(arg);
		//Test machine is not changed
        assertArrayEquals(dataCopy, machine.getData()); 
        //Test program counter incremented
        assertEquals("Program counter incremented", ipInit + 1,
                machine.getInstructionPointer());
        //Accumulator is 1
        assertEquals("Accumulator is 1", 1,
                machine.getAccumulator());
	}
	
	@Test
	// Check ANDI when accum and arg neg gives true
	public void testANDIaccLT0argLT0() {
		Instruction instr = machine.get(0x18);
		int arg = -200;
		machine.setAccumulator(-10);
		instr.execute(arg);
		//Test machine is not changed
        assertArrayEquals(dataCopy, machine.getData()); 
        //Test program counter incremented
        assertEquals("Program counter incremented", ipInit + 1,
                machine.getInstructionPointer());
        //Accumulator is 1
        assertEquals("Accumulator is 1", 1,
                machine.getAccumulator());
	}
	
	@Test
	// Check ANDI when accum neg and arg pos gives true
	public void testANDIaccLT0argGT0() {
		Instruction instr = machine.get(0x18);
		int arg = 300;
		machine.setAccumulator(-10);
		instr.execute(arg);
		//Test machine is not changed
        assertArrayEquals(dataCopy, machine.getData()); 
        //Test program counter incremented
        assertEquals("Program counter incremented", ipInit + 1,
                machine.getInstructionPointer());
        //Accumulator is 1
        assertEquals("Accumulator is 1", 1,
                machine.getAccumulator());
	}
	
	@Test
	// Check ANDI when accum pos and arg neg gives true
	public void testANDIaccGT0argLT0() {
		Instruction instr = machine.get(0x18);
		int arg = -200;
		machine.setAccumulator(10);
		instr.execute(arg);
		//Test machine is not changed
        assertArrayEquals(dataCopy, machine.getData()); 
        //Test program counter incremented
        assertEquals("Program counter incremented", ipInit + 1,
                machine.getInstructionPointer());
        //Accumulator is 1
        assertEquals("Accumulator is 1", 1,
                machine.getAccumulator());
	}
	
	@Test
	// Check AND when accum pos mem equal to zero gives false
	public void testANDIaccGT0argEQ0() {
		Instruction instr = machine.get(0x18);
		int arg = 0;
		machine.setAccumulator(10);
		instr.execute(arg);
		//Test machine is not changed
        assertArrayEquals(dataCopy, machine.getData()); 
        //Test program counter incremented
        assertEquals("Program counter incremented", ipInit + 1,
                machine.getInstructionPointer());
        //Accumulator is 1
        assertEquals("Accumulator is 0", 0,
                machine.getAccumulator());
	}
	
	@Test
	// Check ANDI when accum neg mem equal to zero gives false
	public void testANDIaccLT0argEQ0() {
		Instruction instr = machine.get(0x18);
		int arg = 0;
		machine.setAccumulator(-10);
		instr.execute(arg);
		//Test machine is not changed
        assertArrayEquals(dataCopy, machine.getData()); 
        //Test program counter incremented
        assertEquals("Program counter incremented", ipInit + 1,
                machine.getInstructionPointer());
        //Accumulator is 1
        assertEquals("Accumulator is 0", 0,
                machine.getAccumulator());
	}
	
	@Test
	// Check ANDI when accum equal to zero and mem pos gives false
	public void testANDIaccEQ0argGT0() {
		Instruction instr = machine.get(0x18);
		int arg = 300;
		machine.setAccumulator(0);
		instr.execute(arg);
		//Test machine is not changed
        assertArrayEquals(dataCopy, machine.getData()); 
        //Test program counter incremented
        assertEquals("Program counter incremented", ipInit + 1,
                machine.getInstructionPointer());
        //Accumulator is 1
        assertEquals("Accumulator is 0", 0,
                machine.getAccumulator());
	}
	
	@Test
	// Check ANDI when accum equal to zero and mem neg gives false
	public void testANDIaccEQ0argLT0() {
		Instruction instr = machine.get(0x18);
		int arg = -200;
		machine.setAccumulator(0);
		instr.execute(arg);
		//Test machine is not changed
        assertArrayEquals(dataCopy, machine.getData()); 
        //Test program counter incremented
        assertEquals("Program counter incremented", ipInit + 1,
                machine.getInstructionPointer());
        //Accumulator is 1
        assertEquals("Accumulator is 0", 0,
                machine.getAccumulator());
	}

	@Test
	// Check AND when accum and mem equal to 0 gives false
	public void testANDaccEQ0memEQ0() {
		Instruction instr = machine.get(0x19);
		int arg = 1024;
		machine.setAccumulator(0);
		instr.execute(arg);
		//Test machine is not changed
        assertArrayEquals(dataCopy, machine.getData()); 
        //Test program counter incremented
        assertEquals("Program counter incremented", ipInit + 1,
                machine.getInstructionPointer());
        //Accumulator is 1
        assertEquals("Accumulator is 0", 0,
                machine.getAccumulator());
	}
	
	@Test
	// Check AND when accum and mem pos gives true
	public void testANDaccGT0memGT0() {
		Instruction instr = machine.get(0x19);
		int arg = 1300;
		instr.execute(arg);
		//Test machine is not changed
        assertArrayEquals(dataCopy, machine.getData()); 
        //Test program counter incremented
        assertEquals("Program counter incremented", ipInit + 1,
                machine.getInstructionPointer());
        //Accumulator is 1
        assertEquals("Accumulator is 1", 1,
                machine.getAccumulator());
	}
	
	@Test
	// Check AND when accum and mem neg gives true
	public void testANDaccLT0memLT0() {
		Instruction instr = machine.get(0x19);
		int arg = 200;
		machine.setAccumulator(-10);
		instr.execute(arg);
		//Test machine is not changed
        assertArrayEquals(dataCopy, machine.getData()); 
        //Test program counter incremented
        assertEquals("Program counter incremented", ipInit + 1,
                machine.getInstructionPointer());
        //Accumulator is 1
        assertEquals("Accumulator is 1", 1,
                machine.getAccumulator());
	}
	
	@Test
	// Check AND when accum neg and mem pos gives true
	public void testANDaccLT0memGT0() {
		Instruction instr = machine.get(0x19);
		int arg = 1300;
		machine.setAccumulator(-10);
		instr.execute(arg);
		//Test machine is not changed
        assertArrayEquals(dataCopy, machine.getData()); 
        //Test program counter incremented
        assertEquals("Program counter incremented", ipInit + 1,
                machine.getInstructionPointer());
        //Accumulator is 1
        assertEquals("Accumulator is 1", 1,
                machine.getAccumulator());
	}
	
	@Test
	// Check AND when accum pos and mem neg gives true
	public void testANDaccGT0memLT0() {
		Instruction instr = machine.get(0x19);
		int arg = 200;
		instr.execute(arg);
		//Test machine is not changed
        assertArrayEquals(dataCopy, machine.getData()); 
        //Test program counter incremented
        assertEquals("Program counter incremented", ipInit + 1,
                machine.getInstructionPointer());
        //Accumulator is 1
        assertEquals("Accumulator is 1", 1,
                machine.getAccumulator());
	}
	
	@Test
	// Check AND when accum pos mem equal to zero gives false
	public void testANDaccGT0memEQ0() {
		Instruction instr = machine.get(0x19);
		int arg = 1024;
		instr.execute(arg);
		//Test machine is not changed
        assertArrayEquals(dataCopy, machine.getData()); 
        //Test program counter incremented
        assertEquals("Program counter incremented", ipInit + 1,
                machine.getInstructionPointer());
        //Accumulator is 1
        assertEquals("Accumulator is 0", 0,
                machine.getAccumulator());
	}
	
	@Test
	// Check AND when accum neg mem equal to zero gives false
	public void testANDaccLT0memEQ0() {
		Instruction instr = machine.get(0x19);
		int arg = 1024;
		machine.setAccumulator(-10);
		instr.execute(arg);
		//Test machine is not changed
        assertArrayEquals(dataCopy, machine.getData()); 
        //Test program counter incremented
        assertEquals("Program counter incremented", ipInit + 1,
                machine.getInstructionPointer());
        //Accumulator is 1
        assertEquals("Accumulator is 0", 0,
                machine.getAccumulator());
	}
	
	@Test
	// Check AND when accum equal to zero and mem pos gives false
	public void testANDaccEQ0memGT0() {
		Instruction instr = machine.get(0x19);
		int arg = 1300;
		machine.setAccumulator(0);
		instr.execute(arg);
		//Test machine is not changed
        assertArrayEquals(dataCopy, machine.getData()); 
        //Test program counter incremented
        assertEquals("Program counter incremented", ipInit + 1,
                machine.getInstructionPointer());
        //Accumulator is 1
        assertEquals("Accumulator is 0", 0,
                machine.getAccumulator());
	}
	
	@Test
	// Check AND when accum equal to zero and mem neg gives false
	public void testANDaccEQ0memLT0() {
		Instruction instr = machine.get(0x19);
		int arg = 200;
		machine.setAccumulator(0);
		instr.execute(arg);
		//Test machine is not changed
        assertArrayEquals(dataCopy, machine.getData()); 
        //Test program counter incremented
        assertEquals("Program counter incremented", ipInit + 1,
                machine.getInstructionPointer());
        //Accumulator is 1
        assertEquals("Accumulator is 0", 0,
                machine.getAccumulator());
	}

	@Test
	// Check NOT greater than 0 gives false
	public void testNOTaccGT0() {
		Instruction instr = machine.get(0X1A);
		machine.setAccumulator(10);
		instr.execute(0);
		//Test machine is not changed
        assertArrayEquals(dataCopy, machine.getData()); 
        //Test program counter incremented
        assertEquals("Program counter incremented", ipInit + 1,
                machine.getInstructionPointer());
        //Accumulator is 1
        assertEquals("Accumulator is 0", 0,
                machine.getAccumulator());
	}

	@Test
	// Check NOT equal to 0 gives true
	public void testNOTaccEQ0() {
		Instruction instr = machine.get(0X1A);
		machine.setAccumulator(0);
		instr.execute(0);
		//Test machine is not changed
        assertArrayEquals(dataCopy, machine.getData()); 
        //Test program counter incremented
        assertEquals("Program counter incremented", ipInit + 1,
                machine.getInstructionPointer());
        //Accumulator is 1
        assertEquals("Accumulator is 1", 1,
                machine.getAccumulator());
	}
	
	@Test
	// Check NOT less than 0 gives false
	public void testNOTaccLT0() {
		Instruction instr = machine.get(0X1A);
		machine.setAccumulator(-10);
		instr.execute(0);
		//Test machine is not changed
        assertArrayEquals(dataCopy, machine.getData()); 
        //Test program counter incremented
        assertEquals("Program counter incremented", ipInit + 1,
                machine.getInstructionPointer());
        //Accumulator is 1
        assertEquals("Accumulator is 0", 0,
                machine.getAccumulator());
	}
	
	@Test
	// Check CMPL when comparing less than 0 gives true
	public void testCMPLmemLT0() {
		Instruction instr = machine.get(0x1B);
		int arg = 100;
		instr.execute(arg);
		//Test machine is not changed
        assertArrayEquals(dataCopy, machine.getData()); 
        //Test program counter incremented
        assertEquals("Program counter incremented", ipInit + 1,
                machine.getInstructionPointer());
        //Accumulator is 1
        assertEquals("Accumulator is 1", 1,
                machine.getAccumulator());
	}

	@Test
	// Check CMPL when comparing equal to 0 gives false
	public void testCMPLmemEQ0() {
		Instruction instr = machine.get(0x1B);
		int arg = 1024;
		instr.execute(arg);
		//Test machine is not changed
        assertArrayEquals(dataCopy, machine.getData()); 
        //Test program counter incremented
        assertEquals("Program counter incremented", ipInit + 1,
                machine.getInstructionPointer());
        //Accumulator is 1
        assertEquals("Accumulator is 0", 0,
                machine.getAccumulator());
	}

	@Test
	// Check CMPL when comparing greater than 0 gives false
	public void testCMPLmemGT0() {
		Instruction instr = machine.get(0x1B);
		int arg = 1030;
		instr.execute(arg);
		//Test machine is not changed
        assertArrayEquals(dataCopy, machine.getData()); 
        //Test program counter incremented
        assertEquals("Program counter incremented", ipInit + 1,
                machine.getInstructionPointer());
        //Accumulator is 1
        assertEquals("Accumulator is 0", 0,
                machine.getAccumulator());
	}

	@Test
	// Check CMPZ when comparing less than 0 gives false
	public void testCMPZmemLT0() {
		Instruction instr = machine.get(0x1C);
		int arg = 100;
		instr.execute(arg);
		//Test machine is not changed
        assertArrayEquals(dataCopy, machine.getData()); 
        //Test program counter incremented
        assertEquals("Program counter incremented", ipInit + 1,
                machine.getInstructionPointer());
        //Accumulator is 1
        assertEquals("Accumulator is 0", 0,
                machine.getAccumulator());
	}

	@Test
	// Check CMPZ when comparing equal to 0 gives true
	public void testCMPZmemEQ0() {
		Instruction instr = machine.get(0x1C);
		int arg = 1024;
		instr.execute(arg);
		//Test machine is not changed
        assertArrayEquals(dataCopy, machine.getData()); 
        //Test program counter incremented
        assertEquals("Program counter incremented", ipInit + 1,
                machine.getInstructionPointer());
        //Accumulator is 1
        assertEquals("Accumulator is 1", 1,
                machine.getAccumulator());
	}

	@Test
	// Check CMPZ when comparing greater than 0 gives false
	public void testCMPZmemGT0() {
		Instruction instr = machine.get(0x1C);
		int arg = 1030;
		instr.execute(arg);
		//Test machine is not changed
        assertArrayEquals(dataCopy, machine.getData()); 
        //Test program counter incremented
        assertEquals("Program counter incremented", ipInit + 1,
                machine.getInstructionPointer());
        //Accumulator is 1
        assertEquals("Accumulator is 0", 0,
                machine.getAccumulator());
	}
}
