// COMP3290/6290 CD Compiler
//
// Asm2Obj class -	OpCodes for SM assembly instructions.
//
// P. Mahata: 27-May-2017

import java.io.*;
import java.util.*;


public class AsmDef {

	private static Map<String, Integer> asm = new HashMap<>();
	private static Map<Integer, String> obj = new HashMap<>();
	
	public AsmDef() {
		asm.put("HALT",  0);  // No operands, 			Stop execution
		asm.put("NO-OP", 1);  // No operands, 			Do nothing
		asm.put("TRAP",  2);	// No operands, 			Stop Execution – Abort
		asm.put("ZERO",  3);  // No operands, 			Push the INTG zero onto the stack
		asm.put("FALSE", 4);  // No operands, 			Push the BOOL false onto the stack
		asm.put("TRUE",  5);  // No operands,	 			Push the BOOL true onto the stack
		// Type coercion
		asm.put("TYPE",  7);  // 1 x arithmetic			swap types INTG >> FLOT, FLOT>>INTG
		asm.put("ITYPE", 8);	// 1 x arithmetic  			set type for TOS to INTG
		asm.put("FTYPE", 9);  // 1 x arithmetic			set type for TOS to FLOT
		// Arithmetic
		asm.put("ADD",   11); // 2 x arithmetic			add them, push the result
		asm.put("SUB",   12);	// 2 x arithmetic			subtract first popped from second, push
		asm.put("MUL",   13);	// 2 x arithmetic			multiply them, push the result
		asm.put("DIV",   14); // 2 x arithmetic			divide second popped by first, push
		asm.put("REM",   15);	// 2 x Integer				second popped MOD first, push
		asm.put("POW",   16); // 2 x arithmetic			raise second popped to power of first, push for fl.pt powers, use ex.log a
		asm.put("CHS",   17); // 1 x arithmetic			push negative of operand popped
		asm.put("ABS",	 18); // 1 x arithmetic			push absolute value of operand popped
		asm.put("GT",	 21); // 1 x arithmetic			if TOS >   0, push true else false
		asm.put("GE",	 22); // 1 x arithmetic			if TOS >=   0, push true else false
		asm.put("LT",	 23); // 1 x arithmetic			if TOS <   0, push true else false
		asm.put("LE",	 24); // 1 x arithmetic			if TOS <=   0, push true else false
		asm.put("EQ",	 25); // 1 x arithmetic			if TOS ==   0, push true else false
		                        // 1 x Float        		if | TOS | < 0.000001, push true else false
		asm.put("NE",	 26); // 1 x arithmetic			if TOS !=   0, push true else false
		                        // 1 x Float        		if | TOS | > 0.000001, push true else false
		// Boolean operations
		asm.put("AND",	 31); // 2 x Boolean				if (popped) (b1 & b2), push true else false
		asm.put("OR",	 32); // 2 x Boolean				if (popped) (b1 | b2), push true else false
		asm.put("XOR",	 33); // 2 x Boolean				if (popped) (b1 exclusive-or b2), push true else false
	    asm.put("NOT",	 34); // 1 x Boolean				push logical negation of popped item
	    // Jump instruction
	    asm.put("BT",    35); // 1 bool, 1 addr			if boolean is true  then place address into pc
	    asm.put("BF",    36); // 1 bool, 1 addr			if boolean is false  then place address into pc
	 	asm.put("BR",    37); // 1 x addr. 				place address into pc
	 	// Load-Store
	 	asm.put("L",     40); // 1 x Address				pop address, push the value at this address
		asm.put("LB",    41); // 1 x Instr Byte			push 8-bits, sign extended to 64, as INTG
		asm.put("LH",    42); // 1 x Instr Byte			push 16-bits, sign extended to 64, as INTG
		asm.put("ST",    43); // 1 arith/bool, 1 addr. 	store value (first) popped at address popped  
		// stack pointer related operations or addressing
		asm.put("STEP",  51); // no operand				step the SP one word (tagged UNDF)
		asm.put("ALLOC", 52); // 1 x Integer				step the sp by this many (popped) words
		asm.put("ARRAY", 53); // 1 int, 1 addr			construct descriptor and step sp by size
		asm.put("INDEX", 54); // 1 int, 1 desc			construct address of elt (int) in array (addr)
		asm.put("SIZE",  55); // 1 descriptor				pop descriptor, extract & push array size
		asm.put("DUP",   56); // no operands				push a duplicate of the top item on the stack	
		// Input from stdin
		asm.put("READF", 60); // no operands				input floating pt value, push to stack
		asm.put("READI", 61); // no operands				input integer value, push to stack
		asm.put("READB", 62); // no operands 				input int value, if 0 push false else push true
		// Output to stdout
		asm.put("VALPR", 64); // 1 x arithmetic			print a space and then the popped value
		asm.put("STRPR", 65); // 1 x Address				print string const at popped address
		asm.put("CHRPR", 66); // 1 x Address				print character const at popped address
		asm.put("NEWLN", 67); // no operands				terminate the current line of output
		asm.put("SPACE", 68); // no operands				print a single space character
		// Function return specific instructions
		asm.put("RVAL",  70); // 1 x arith/bool			pop value, store in function return position
		asm.put("RETN",  71); // no operands				pop the proc environment, return to caller
		asm.put("JS2",   72); // 1 int, 1 addr			construct call frame, branch to proc/fn
		// Special memory rferencing instructions
		// base register 0 is used for constants; base register 1 is used for main programs; base register 2 is udes for functions
		// Load value family of instructions
		asm.put("LV0",   80); // LV0 is followed by a 4-byte offset. Load value from address (base register 0 + offset) to the top of stack 
		asm.put("LV1",   81); // LV1 is followed by a 4-byte offset. Load value from address (base register 1 + offset) to the top of stack 
		asm.put("LV2",   82); // LV2 is followed by a 4-byte offset. Load value from address (base register 2 + offset) to the top of stack 
		// Load address family of instructions
		asm.put("LA0",   90); // LA0 is followed by a 4-byte offset. Load address (base register 0 + offset) to the top of stack 
		asm.put("LA1",   91); // LA1 is followed by a 4-byte offset. Load address (base register 1 + offset) to the top of stack 
		asm.put("LA2",   92); // LA2 is followed by a 4-byte offset. Load address (base register 2 + offset) to the top of stack 
		
		reverse();					
	}

	public Integer get_obj(String instr) {
		return asm.get(instr);
	}


	public String get_asm(Integer code) {
		return obj.get(code);
	}

	public static void reverse() {

	    for (String key : asm.keySet()) {
    		obj.put(asm.get(key), key);
		}

    }
};
