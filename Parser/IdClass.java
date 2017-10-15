// COMP3290/6290 CD Compiler
//
// IdClass class -	Enumeration of different classes of CD Identifiers.
//
//			This looks forward to the Code Generator, remembering how the
//			variable name was declared so that memory can be allocated later.
//
// M.Hannaford
// 03-Sep-2016
//
// Modified:
//

public enum IdClass {
			IUNDEF,		// Temp classification for id's tbd
			ICONST,		// No storage but 0,offset for value
			IFIELD,		// No storage - offset within array row
			IRECTYP,	// No storage - row of array
			IARRTYP,	// Descriptor - size is length * record size
			IARRAY,		// 1,offset allocated for descriptor
			IFUNC,		// 0,offset for entry point
			ISIMPAR,	// 2,-offset for parameter value
			IARRPAR,	// 2,-offset for array descriptor copy
			ISIMPLE		// 1,offset for main local; 2,+offset for func local
};


