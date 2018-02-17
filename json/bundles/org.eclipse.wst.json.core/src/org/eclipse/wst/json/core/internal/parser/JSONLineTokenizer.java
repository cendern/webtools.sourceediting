/**
 *  Copyright (c) 2013-2014 Angelo ZERR.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *  Angelo Zerr <angelo.zerr@gmail.com> - initial API and implementation
 */
/* The following code was generated by JFlex 1.4.2 on 18/03/15 22:51 */

/*nlsXXX*/
package org.eclipse.wst.json.core.internal.parser;

import java.io.CharArrayReader;
import java.io.IOException;
import java.util.Stack;

import org.eclipse.wst.json.core.internal.parser.regions.JSONTextRegionFactory;
import  org.eclipse.wst.json.core.regions.JSONRegionContexts;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;


/**
 * This class is a scanner generated by 
 * <a href="http://www.jflex.de/">JFlex</a> 1.4.2
 * on 18/03/15 22:51 from the specification file
 * <tt>JSONLineTokenizer.jflex</tt>
 */
public class JSONLineTokenizer extends AbstractJSONTokenizer {

  /** This character denotes the end of file */
  public static final int YYEOF = -1;

  /** initial size of the lookahead buffer */
  private static final int ZZ_BUFFERSIZE = 16384;

  /** lexical states */
  public static final int ST_JSON_OBJECT_COLON = 6;
  public static final int ST_JSON_ARRAY = 4;
  public static final int YYINITIAL = 0;
  public static final int ST_JSON_COMMENT = 10;
  public static final int ST_JSON_VALUE = 8;
  public static final int ST_JSON_OBJECT = 2;

  /**
   * ZZ_LEXSTATE[l] is the state in the DFA for the lexical state l
   * ZZ_LEXSTATE[l+1] is the state in the DFA for the lexical state l
   *                  at the beginning of a line
   * l is of the form l = 2*k, k a non negative integer
   */
  private static final int ZZ_LEXSTATE[] = { 
     0,  0,  1,  1,  2,  2,  3,  3,  4,  4,  5, 5
  };

  /** 
   * Translates characters to character classes
   */
  private static final String ZZ_CMAP_PACKED = 
    "\10\0\1\0\1\16\1\20\1\0\1\14\1\7\22\0\1\16\1\12"+
    "\1\17\4\12\1\15\2\12\1\27\1\6\1\25\1\1\1\4\1\26"+
    "\1\2\11\3\1\30\6\12\1\37\3\12\1\5\1\35\5\12\1\40"+
    "\1\12\1\42\3\12\1\32\1\41\1\31\1\33\5\12\1\23\1\13"+
    "\1\24\1\12\1\12\1\12\1\36\3\10\1\11\1\34\5\12\1\40"+
    "\1\12\1\42\3\12\1\32\1\41\1\31\1\33\5\12\1\21\1\12"+
    "\1\22\1\12\1\0\uff80\12";

  /** 
   * Translates characters to character classes
   */
  private static final char [] ZZ_CMAP = zzUnpackCMap(ZZ_CMAP_PACKED);

  /** 
   * Translates DFA states to action switch labels.
   */
  private static final int [] ZZ_ACTION = zzUnpackAction();

  private static final String ZZ_ACTION_PACKED_0 =
    "\1\0\4\1\1\0\1\2\1\1\1\3\1\4\1\5"+
    "\1\2\1\1\2\2\2\6\2\2\1\7\3\2\1\10"+
    "\1\11\1\12\3\0\1\13\4\0\1\14\10\0\1\13"+
    "\1\6\1\0\1\6\2\0\1\14\2\0\1\14\3\0"+
    "\1\12\3\0\1\15\1\16\14\0";

  private static int [] zzUnpackAction() {
    int [] result = new int[74];
    int offset = 0;
    offset = zzUnpackAction(ZZ_ACTION_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackAction(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }


  /** 
   * Translates a state to a row index in the transition table
   */
  private static final int [] ZZ_ROWMAP = zzUnpackRowMap();

  private static final String ZZ_ROWMAP_PACKED_0 =
    "\0\0\0\43\0\106\0\151\0\214\0\257\0\322\0\322"+
    "\0\322\0\322\0\322\0\365\0\u0118\0\u013b\0\u015e\0\u0181"+
    "\0\u01a4\0\u01c7\0\u01ea\0\322\0\u020d\0\u0230\0\u0253\0\322"+
    "\0\322\0\u0276\0\u0299\0\u013b\0\u02bc\0\322\0\u02df\0\u0302"+
    "\0\u01c7\0\u0325\0\322\0\u01ea\0\u0348\0\u036b\0\u038e\0\u03b1"+
    "\0\u03d4\0\u03f7\0\u041a\0\u013b\0\u043d\0\u0460\0\u0460\0\u0483"+
    "\0\u04a6\0\u01c7\0\u04c9\0\u04ec\0\u01ea\0\u050f\0\u0532\0\u0555"+
    "\0\322\0\u0578\0\u059b\0\u05be\0\322\0\322\0\u05e1\0\u0604"+
    "\0\u0627\0\u064a\0\u066d\0\u0690\0\u06b3\0\u06d6\0\u06f9\0\u071c"+
    "\0\u073f\0\u0762";

  private static int [] zzUnpackRowMap() {
    int [] result = new int[74];
    int offset = 0;
    offset = zzUnpackRowMap(ZZ_ROWMAP_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackRowMap(String packed, int offset, int [] result) {
    int i = 0;  /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int high = packed.charAt(i++) << 16;
      result[j++] = high | packed.charAt(i++);
    }
    return j;
  }

  /** 
   * The transition table of the DFA
   */
  private static final int [] ZZ_TRANS = zzUnpackTrans();

  private static final String ZZ_TRANS_PACKED_0 =
    "\20\7\1\10\1\11\1\12\1\13\2\7\1\14\23\7"+
    "\1\15\6\7\1\15\1\16\1\15\1\7\1\12\3\7"+
    "\1\14\15\7\1\17\1\20\1\21\3\7\1\15\5\7"+
    "\1\22\1\15\1\23\1\15\1\11\1\12\1\13\1\24"+
    "\1\7\1\14\2\7\1\25\2\7\2\26\4\7\1\27"+
    "\7\7\1\15\6\7\1\15\1\7\1\15\1\7\1\12"+
    "\3\7\1\14\1\7\1\30\13\7\1\17\1\20\1\21"+
    "\3\7\1\15\5\7\1\22\1\15\1\23\1\15\1\11"+
    "\1\12\1\13\1\24\1\31\1\14\2\7\1\25\2\7"+
    "\2\26\4\7\1\27\20\7\1\10\1\7\1\12\3\7"+
    "\1\14\14\7\71\0\1\32\1\33\22\0\1\15\6\0"+
    "\1\15\1\0\1\15\23\0\6\34\1\0\3\34\1\35"+
    "\1\0\2\34\1\36\1\0\22\34\2\0\1\20\1\21"+
    "\43\0\1\37\1\40\3\0\1\40\33\0\2\21\1\37"+
    "\1\40\3\0\1\40\32\0\6\41\1\0\3\41\1\42"+
    "\1\0\1\43\2\41\1\0\22\41\1\0\6\44\1\0"+
    "\3\44\1\45\1\0\2\44\1\43\1\0\22\44\32\0"+
    "\1\46\46\0\2\47\36\0\1\50\7\0\20\32\1\0"+
    "\22\32\27\33\1\51\13\33\1\0\1\34\2\52\3\34"+
    "\1\53\2\52\1\34\1\35\3\34\1\54\14\34\1\52"+
    "\1\34\1\52\4\34\2\0\2\55\40\0\1\56\2\57"+
    "\2\0\1\56\35\0\1\41\2\60\3\41\1\61\2\60"+
    "\1\41\1\42\1\41\1\62\16\41\1\60\1\41\1\60"+
    "\4\41\1\0\1\44\2\63\3\44\1\64\2\63\1\44"+
    "\1\45\3\44\1\65\14\44\1\63\1\44\1\63\4\44"+
    "\33\0\1\66\47\0\1\67\42\0\1\70\2\0\26\33"+
    "\1\71\1\51\13\33\1\0\1\34\2\72\4\34\2\72"+
    "\1\34\1\35\3\34\1\36\14\34\1\72\1\34\1\72"+
    "\4\34\1\0\6\34\1\0\3\34\1\35\1\0\2\34"+
    "\1\36\23\34\2\0\2\55\1\0\1\40\3\0\1\40"+
    "\33\0\2\57\40\0\1\41\2\73\4\41\2\73\1\41"+
    "\1\42\1\41\1\43\16\41\1\73\1\41\1\73\4\41"+
    "\1\0\6\41\1\0\3\41\1\42\1\0\1\43\25\41"+
    "\1\0\1\44\2\74\4\44\2\74\1\44\1\45\3\44"+
    "\1\43\14\44\1\74\1\44\1\74\4\44\1\0\6\44"+
    "\1\0\3\44\1\45\1\0\2\44\1\43\23\44\5\0"+
    "\1\75\3\0\1\75\72\0\1\66\41\0\1\76\3\0"+
    "\1\34\2\77\4\34\2\77\1\34\1\35\3\34\1\36"+
    "\14\34\1\77\1\34\1\77\4\34\1\0\1\41\2\100"+
    "\4\41\2\100\1\41\1\42\1\41\1\43\16\41\1\100"+
    "\1\41\1\100\4\41\1\0\1\44\2\101\4\44\2\101"+
    "\1\44\1\45\3\44\1\43\14\44\1\101\1\44\1\101"+
    "\4\44\1\0\1\34\2\102\4\34\2\102\1\34\1\35"+
    "\3\34\1\36\14\34\1\102\1\34\1\102\4\34\1\0"+
    "\1\41\2\103\4\41\2\103\1\41\1\42\1\41\1\43"+
    "\16\41\1\103\1\41\1\103\4\41\1\0\1\44\2\104"+
    "\4\44\2\104\1\44\1\45\3\44\1\43\14\44\1\104"+
    "\1\44\1\104\4\44\1\0\1\34\2\105\4\34\2\105"+
    "\1\34\1\35\3\34\1\36\14\34\1\105\1\34\1\105"+
    "\4\34\1\0\1\41\2\106\4\41\2\106\1\41\1\42"+
    "\1\41\1\43\16\41\1\106\1\41\1\106\4\41\1\0"+
    "\1\44\2\107\4\44\2\107\1\44\1\45\3\44\1\43"+
    "\14\44\1\107\1\44\1\107\4\44\1\0\1\34\2\110"+
    "\4\34\2\110\1\34\1\35\3\34\1\36\14\34\1\110"+
    "\1\34\1\110\4\34\1\0\1\41\2\111\4\41\2\111"+
    "\1\41\1\42\1\41\1\43\16\41\1\111\1\41\1\111"+
    "\4\41\1\0\1\44\2\112\4\44\2\112\1\44\1\45"+
    "\3\44\1\43\14\44\1\112\1\44\1\112\4\44\1\0"+
    "\12\34\1\35\3\34\1\36\23\34\1\0\12\41\1\42"+
    "\1\41\1\43\25\41\1\0\12\44\1\45\3\44\1\43"+
    "\23\44";

  private static int [] zzUnpackTrans() {
    int [] result = new int[1925];
    int offset = 0;
    offset = zzUnpackTrans(ZZ_TRANS_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackTrans(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      value--;
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }


  /* error codes */
  private static final int ZZ_UNKNOWN_ERROR = 0;
  private static final int ZZ_NO_MATCH = 1;
  private static final int ZZ_PUSHBACK_2BIG = 2;

  /* error messages for the codes above */
  private static final String ZZ_ERROR_MSG[] = {
    "Unkown internal scanner error",
    "Error: could not match input",
    "Error: pushback value was too large"
  };

  /**
   * ZZ_ATTRIBUTE[aState] contains the attributes of state <code>aState</code>
   */
  private static final int [] ZZ_ATTRIBUTE = zzUnpackAttribute();

  private static final String ZZ_ATTRIBUTE_PACKED_0 =
    "\1\0\4\1\1\0\5\11\10\1\1\11\3\1\2\11"+
    "\1\1\3\0\1\11\4\0\1\11\10\0\2\1\1\0"+
    "\1\1\2\0\1\1\2\0\1\1\3\0\1\11\3\0"+
    "\2\11\14\0";

  private static int [] zzUnpackAttribute() {
    int [] result = new int[74];
    int offset = 0;
    offset = zzUnpackAttribute(ZZ_ATTRIBUTE_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackAttribute(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }

  /** the input device */
  private java.io.Reader zzReader;

  /** the current state of the DFA */
  private int zzState;

  /** the current lexical state */
  private int zzLexicalState = YYINITIAL;

  /** this buffer contains the current text to be matched and is
      the source of the yytext() string */
  private char zzBuffer[] = new char[ZZ_BUFFERSIZE];

  /** the textposition at the last accepting state */
  private int zzMarkedPos;

  /** the current text position in the buffer */
  private int zzCurrentPos;

  /** startRead marks the beginning of the yytext() string in the buffer */
  private int zzStartRead;

  /** endRead marks the last character in the buffer, that has been read
      from input */
  private int zzEndRead;

  /** number of newlines encountered up to the start of the matched text */
  private int yyline;

  /** the number of characters up to the start of the matched text */
  private int yychar;

  /**
   * the number of characters from the last newline up to the start of the 
   * matched text
   */
  private int yycolumn;

  /** 
   * zzAtBOL == true <=> the scanner is currently at the beginning of a line
   */
  private boolean zzAtBOL = true;

  /** zzAtEOF == true <=> the scanner is at the EOF */
  private boolean zzAtEOF;

  /* user code: */
	private final static String UNDEFINED = "undefined";
	private String fBufferedContext = null;
	private int fBufferedStart;
//	private int fBufferedTextLength;
	private int fBufferedLength;
//	private StringBuilder fBufferedText = null;
	private JSONTextRegionFactory fRegionFactory = JSONTextRegionFactory.getInstance();
	private int fInitialState = YYINITIAL;
	public final static int BUFFER_SIZE_NORMAL = 16384;
	public final static int BUFFER_SIZE_SMALL = 256;
	private int fInitialBufferSize = BUFFER_SIZE_NORMAL;

	public void setInitialState(int state) {
		fInitialState = state;
	}
	
	public void setInitialBufferSize(int size) {
		fInitialBufferSize = size;
	}

	@Override
	protected void setJSONArrayState() {
		yybegin(ST_JSON_ARRAY);
	}

	@Override
	protected void setJSONObjectState() {
		yybegin(ST_JSON_OBJECT);
	}

	@Override
	protected void setJSONValueState() {
		yybegin(ST_JSON_VALUE);
	}
	
	/* user method */
  	public final ITextRegion getNextToken() throws IOException {
		String context;
		String nextTokenType;
		boolean spaceFollows;
//		StringBuilder text;
		int start;
		int textLength;
		int length;
		if (fBufferedContext != null) {
			context = fBufferedContext;
//			text = fBufferedText;
			start = fBufferedStart;
			textLength = length = fBufferedLength;

			fBufferedContext = null;
		} else {
			context = primGetNextToken();
//			text = new StringBuilder(yytext());
			start = yychar;
			textLength = length = yylength();
		}

		if (context != null) {
			if (context == UNDEFINED) {
				// undef -> concatenate undef's
				nextTokenType = primGetNextToken();
				while (nextTokenType == UNDEFINED) {
//					text.append(yytext());
					textLength += yylength();
					length = textLength;
					nextTokenType = primGetNextToken();
				}
				fBufferedContext = nextTokenType;
//				fBufferedText = new StringBuilder(yytext());
				fBufferedStart = yychar;
				fBufferedLength = yylength();
			} else {
				nextTokenType = null;
				spaceFollows = false;
				/*if (JSONRegionUtil.isDeclarationValueType(context)) { // declaration value can contain VALUE_S
					nextTokenType = primGetNextToken();
					spaceFollows = (nextTokenType == JSON_DECLARATION_VALUE_S);
				} else 
				*/
				//if (canContainSpace(context)) {
					nextTokenType = primGetNextToken();
					//spaceFollows = (nextTokenType == JSON_S);
				//}
				if (nextTokenType != null) { // nextToken is retrieved
					if (spaceFollows && (context != JSON_COMMENT)) {
						// next is space -> append
//						text.append(yytext());
						length += yylength();
					} else {
						// next is NOT space -> push this for next time, return itself
						fBufferedContext = nextTokenType;
//						fBufferedText = new StringBuilder(yytext());
						fBufferedStart = yychar;
						fBufferedLength = yylength();
					}
				}
			}
		}

		if (context != null) {
			if (context == UNDEFINED) {
				context = JSON_UNKNOWN;
			}
			return fRegionFactory.createRegion(context, start, textLength, length);
		} else {
			return null;
		}
  	}

	/* user method */
	/* for standalone use */
  	/*public final List parseText() throws IOException {
  		List tokens = new ArrayList();

  		JSONTextToken token;
		for (String kind = primGetNextToken(); kind != null; kind = primGetNextToken()) {
			token = new JSONTextToken();
			token.kind = kind;  				
			token.start = yychar;
			token.length = yylength();
			token.image = yytext();
			tokens.add(token);
		}

  		return tokens;
  	}*/
  	
  	/* user method */
  	/*private boolean canContainSpace(String type) {
  		if (type == JSON_DELIMITER || type == JSON_RBRACE || type == JSON_DECLARATION_DELIMITER) {
  			return false;
  		} else {
  			return true;
  		}
  	}*/

	/* user method */
	public final int getOffset() {
		return yychar;
	}
	
	/* user method */
	public final int getLine() {
		return yyline;
	}
	
	/* user method */
	public final boolean isEOF() {
		return zzAtEOF;
	}

	/* user method */
	public void reset(char[] charArray) {
		reset(new CharArrayReader(charArray), 0);
	}

	/* user method */
	public final void reset(java.io.Reader in, int newOffset) {
		/** the input device */
		zzReader = in;

		/** the current state of the DFA */
		zzState = 0;

		/** the current lexical state */
		zzLexicalState = fInitialState; //YYINITIAL;

		/** this buffer contains the current text to be matched and is
			the source of the yytext() string */
		if (zzBuffer.length != fInitialBufferSize) {
			zzBuffer = new char[fInitialBufferSize];
		}
		java.util.Arrays.fill(zzBuffer, (char)0);

		/** the textposition at the last accepting state */
		zzMarkedPos = 0;

		/** the textposition at the last state to be included in yytext */
//		yy_pushbackPos = 0;

		/** the current text position in the buffer */
		zzCurrentPos = 0;

		/** startRead marks the beginning of the yytext() string in the buffer */
		zzStartRead = 0;

		/** endRead marks the last character in the buffer, that has been read
			from input */
		zzEndRead = 0;

		/** number of newlines encountered up to the start of the matched text */
		yyline = 0;

		/** the number of characters up to the start of the matched text */
		yychar = 0;

		/**
		 * the number of characters from the last newline up to the start of the 
		 * matched text
		 */
//		yycolumn = 0; 

		/** 
		 * yy_atBOL == true <=> the scanner is currently at the beginning of a line
		 */
//		yy_atBOL = false;
		
		/** zzAtEOF == true <=> the scanner has returned a value for EOF */
		zzAtEOF = false;

		/* user variables */
		//		fUndefined.delete(0, fUndefined.length());
		jsonContextStack.clear();
	}
	
	/* user method */
	public JSONLineTokenizer() {
		super();
	}

	/**
	 * Added to workaround stricter compilation options without creating
	 * an alternate skeleton file
	 */
	void _usePrivates() {
		System.out.print(yycolumn);
		System.out.print(yyline);
		System.out.print(Boolean.toString(zzAtBOL));
	}


  /**
   * Creates a new scanner
   * There is also a java.io.InputStream version of this constructor.
   *
   * @param   in  the java.io.Reader to read input from.
   */
  public JSONLineTokenizer(java.io.Reader in) {
    this.zzReader = in;
  }

  /**
   * Creates a new scanner.
   * There is also java.io.Reader version of this constructor.
   *
   * @param   in  the java.io.Inputstream to read input from.
   */
  public JSONLineTokenizer(java.io.InputStream in) {
    this(new java.io.InputStreamReader(in));
  }

  /** 
   * Unpacks the compressed character translation table.
   *
   * @param packed   the packed character translation table
   * @return         the unpacked character translation table
   */
  private static char [] zzUnpackCMap(String packed) {
    char [] map = new char[0x10000];
    int i = 0;  /* index in packed string  */
    int j = 0;  /* index in unpacked array */
    while (i < 128) {
      int  count = packed.charAt(i++);
      char value = packed.charAt(i++);
      do map[j++] = value; while (--count > 0);
    }
    return map;
  }


  /**
   * Refills the input buffer.
   *
   * @return      <code>false</code>, iff there was new input.
   * 
   * @exception   java.io.IOException  if any I/O-Error occurs
   */
  private boolean zzRefill() throws java.io.IOException {

    /* first: make room (if you can) */
    if (zzStartRead > 0) {
      System.arraycopy(zzBuffer, zzStartRead,
                       zzBuffer, 0,
                       zzEndRead-zzStartRead);

      /* translate stored positions */
      zzEndRead-= zzStartRead;
      zzCurrentPos-= zzStartRead;
      zzMarkedPos-= zzStartRead;
      zzStartRead = 0;
    }

    /* is the buffer big enough? */
    if (zzCurrentPos >= zzBuffer.length) {
      /* if not: blow it up */
      char newBuffer[] = new char[zzCurrentPos*2];
      System.arraycopy(zzBuffer, 0, newBuffer, 0, zzBuffer.length);
      zzBuffer = newBuffer;
    }

    /* finally: fill the buffer with new input */
    int numRead = zzReader.read(zzBuffer, zzEndRead,
                                            zzBuffer.length-zzEndRead);

    if (numRead > 0) {
      zzEndRead+= numRead;
      return false;
    }
    // unlikely but not impossible: read 0 characters, but not at end of stream    
    if (numRead == 0) {
      int c = zzReader.read();
      if (c == -1) {
        return true;
      } else {
        zzBuffer[zzEndRead++] = (char) c;
        return false;
      }     
    }

	// numRead < 0
    return true;
  }

    
  /**
   * Closes the input stream.
   */
  public final void yyclose() throws java.io.IOException {
    zzAtEOF = true;            /* indicate end of file */
    zzEndRead = zzStartRead;  /* invalidate buffer    */

    if (zzReader != null)
      zzReader.close();
  }


  /**
   * Resets the scanner to read from a new input stream.
   * Does not close the old reader.
   *
   * All internal variables are reset, the old input stream 
   * <b>cannot</b> be reused (internal buffer is discarded and lost).
   * Lexical state is set to <tt>ZZ_INITIAL</tt>.
   *
   * @param reader   the new input stream 
   */
  public final void yyreset(java.io.Reader reader) {
    zzReader = reader;
    zzAtBOL  = true;
    zzAtEOF  = false;
    zzEndRead = zzStartRead = 0;
    zzCurrentPos = zzMarkedPos = 0;
    yyline = yychar = yycolumn = 0;
    zzLexicalState = YYINITIAL;
  }


  /**
   * Returns the current lexical state.
   */
  public final int yystate() {
    return zzLexicalState;
  }


  /**
   * Enters a new lexical state
   *
   * @param newState the new lexical state
   */
  public final void yybegin(int newState) {
    zzLexicalState = newState;
  }


  /**
   * Returns the text matched by the current regular expression.
   */
  public final String yytext() {
    return new String( zzBuffer, zzStartRead, zzMarkedPos-zzStartRead );
  }


  /**
   * Returns the character at position <tt>pos</tt> from the 
   * matched text. 
   * 
   * It is equivalent to yytext().charAt(pos), but faster
   *
   * @param pos the position of the character to fetch. 
   *            A value from 0 to yylength()-1.
   *
   * @return the character at position pos
   */
  public final char yycharat(int pos) {
    return zzBuffer[zzStartRead+pos];
  }


  /**
   * Returns the length of the matched text region.
   */
  public final int yylength() {
    return zzMarkedPos-zzStartRead;
  }


  /**
   * Reports an error that occured while scanning.
   *
   * In a wellformed scanner (no or only correct usage of 
   * yypushback(int) and a match-all fallback rule) this method 
   * will only be called with things that "Can't Possibly Happen".
   * If this method is called, something is seriously wrong
   * (e.g. a JFlex bug producing a faulty scanner etc.).
   *
   * Usual syntax/scanner level error handling should be done
   * in error fallback rules.
   *
   * @param   errorCode  the code of the errormessage to display
   */
  private void zzScanError(int errorCode) {
    String message;
    try {
      message = ZZ_ERROR_MSG[errorCode];
    }
    catch (ArrayIndexOutOfBoundsException e) {
      message = ZZ_ERROR_MSG[ZZ_UNKNOWN_ERROR];
    }

    throw new Error(message);
  } 


  /**
   * Pushes the specified amount of characters back into the input stream.
   *
   * They will be read again by then next call of the scanning method
   *
   * @param number  the number of characters to be read again.
   *                This number must not be greater than yylength()!
   */
  public void yypushback(int number)  {
    if ( number > yylength() )
      zzScanError(ZZ_PUSHBACK_2BIG);

    zzMarkedPos -= number;
  }


  /**
   * Resumes scanning until the next regular expression is matched,
   * the end of input is encountered or an I/O-Error occurs.
   *
   * @return      the next token
   * @exception   java.io.IOException  if any I/O-Error occurs
   */
  public String primGetNextToken() throws java.io.IOException {
    int zzInput;
    int zzAction;

    // cached fields:
    int zzCurrentPosL;
    int zzMarkedPosL;
    int zzEndReadL = zzEndRead;
    char [] zzBufferL = zzBuffer;
    char [] zzCMapL = ZZ_CMAP;

    int [] zzTransL = ZZ_TRANS;
    int [] zzRowMapL = ZZ_ROWMAP;
    int [] zzAttrL = ZZ_ATTRIBUTE;

    while (true) {
      zzMarkedPosL = zzMarkedPos;

      yychar+= zzMarkedPosL-zzStartRead;

      boolean zzR = false;
      for (zzCurrentPosL = zzStartRead; zzCurrentPosL < zzMarkedPosL;
                                                             zzCurrentPosL++) {
        switch (zzBufferL[zzCurrentPosL]) {
        case '\u000B':
        case '\u000C':
        case '\u0085':
        case '\u2028':
        case '\u2029':
          yyline++;
          zzR = false;
          break;
        case '\r':
          yyline++;
          zzR = true;
          break;
        case '\n':
          if (zzR)
            zzR = false;
          else {
            yyline++;
          }
          break;
        default:
          zzR = false;
        }
      }

      if (zzR) {
        // peek one character ahead if it is \n (if we have counted one line too much)
        boolean zzPeek;
        if (zzMarkedPosL < zzEndReadL)
          zzPeek = zzBufferL[zzMarkedPosL] == '\n';
        else if (zzAtEOF)
          zzPeek = false;
        else {
          boolean eof = zzRefill();
          zzEndReadL = zzEndRead;
          zzMarkedPosL = zzMarkedPos;
          zzBufferL = zzBuffer;
          if (eof) 
            zzPeek = false;
          else 
            zzPeek = zzBufferL[zzMarkedPosL] == '\n';
        }
        if (zzPeek) yyline--;
      }
      zzAction = -1;

      zzCurrentPosL = zzCurrentPos = zzStartRead = zzMarkedPosL;
  
      zzState = ZZ_LEXSTATE[zzLexicalState];


      zzForAction: {
        while (true) {
    
          if (zzCurrentPosL < zzEndReadL)
            zzInput = zzBufferL[zzCurrentPosL++];
          else if (zzAtEOF) {
            zzInput = YYEOF;
            break zzForAction;
          }
          else {
            // store back cached positions
            zzCurrentPos  = zzCurrentPosL;
            zzMarkedPos   = zzMarkedPosL;
            boolean eof = zzRefill();
            // get translated positions and possibly new buffer
            zzCurrentPosL  = zzCurrentPos;
            zzMarkedPosL   = zzMarkedPos;
            zzBufferL      = zzBuffer;
            zzEndReadL     = zzEndRead;
            if (eof) {
              zzInput = YYEOF;
              break zzForAction;
            }
            else {
              zzInput = zzBufferL[zzCurrentPosL++];
            }
          }
          int zzNext = zzTransL[ zzRowMapL[zzState] + zzCMapL[zzInput] ];
          if (zzNext == -1) break zzForAction;
          zzState = zzNext;

          int zzAttributes = zzAttrL[zzState];
          if ( (zzAttributes & 1) == 1 ) {
            zzAction = zzState;
            zzMarkedPosL = zzCurrentPosL;
            if ( (zzAttributes & 8) == 8 ) break zzForAction;
          }

        }
      }

      // store back cached position
      zzMarkedPos = zzMarkedPosL;

      switch (zzAction < 0 ? zzAction : ZZ_ACTION[zzAction]) {
        case 10: 
          { return JSON_COMMENT;
          }
        case 15: break;
        case 1: 
          { return WHITE_SPACE;
          }
        case 16: break;
        case 8: 
          { yybegin(ST_JSON_VALUE); return JSON_COLON;
          }
        case 17: break;
        case 7: 
          { return endElement(true);
          }
        case 18: break;
        case 13: 
          { yybegin(ST_JSON_VALUE); return JSON_VALUE_BOOLEAN;
          }
        case 19: break;
        case 14: 
          { yybegin(ST_JSON_VALUE); return JSON_VALUE_NULL;
          }
        case 20: break;
        case 2: 
          { return UNDEFINED;
          }
        case 21: break;
        case 9: 
          { if (!isArrayParsing()) {
   yybegin(ST_JSON_OBJECT);
 }
 return JSON_COMMA;
          }
        case 22: break;
        case 5: 
          { return startElement(true);
          }
        case 23: break;
        case 12: 
          { yybegin(ST_JSON_VALUE); return JSON_VALUE_STRING;
          }
        case 24: break;
        case 4: 
          { return endElement(false);
          }
        case 25: break;
        case 3: 
          { return startElement(false);
          }
        case 26: break;
        case 6: 
          { yybegin(ST_JSON_VALUE); return JSON_VALUE_NUMBER;
          }
        case 27: break;
        case 11: 
          { yybegin(ST_JSON_OBJECT_COLON);	return JSON_OBJECT_KEY;
          }
        case 28: break;
        default: 
          if (zzInput == YYEOF && zzStartRead == zzCurrentPos) {
            zzAtEOF = true;
            return null;
          } 
          else {
            zzScanError(ZZ_NO_MATCH);
          }
      }
    }
  }


}