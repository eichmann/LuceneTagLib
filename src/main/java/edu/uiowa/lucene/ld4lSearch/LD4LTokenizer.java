package edu.uiowa.lucene.ld4lSearch;

import java.io.IOException;
import java.io.Reader;

import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;

import edu.uiowa.lucene.biomedical.biomedicalLexerFlex;

public class LD4LTokenizer extends Tokenizer {
    /** A private instance of the JFlex-constructed scanner */
    private ld4lLexerFlex scanner;

    /** String token types that correspond to token type int constants */
    public static final String [] TOKEN_TYPES = new String [] {
      "<ALPHANUM>",
      "<NUM>"
    };
    
    /** Absolute maximum sized token */
    public static final int MAX_TOKEN_LENGTH_LIMIT = 1024 * 1024;
    
    private int skippedPositions;

    private int maxTokenLength = StandardAnalyzer.DEFAULT_MAX_TOKEN_LENGTH;

    /**
     * Set the max allowed token length.  Tokens larger than this will be chopped
     * up at this token length and emitted as multiple tokens.  If you need to
     * skip such large tokens, you could increase this max length, and then
     * use {@code LengthFilter} to remove long tokens.  The default is
     * {@link StandardAnalyzer#DEFAULT_MAX_TOKEN_LENGTH}.
     * 
     * @throws IllegalArgumentException if the given length is outside of the
     *  range [1, {@value #MAX_TOKEN_LENGTH_LIMIT}].
     */ 
    public void setMaxTokenLength(int length) {
      if (length < 1) {
        throw new IllegalArgumentException("maxTokenLength must be greater than zero");
      } else if (length > MAX_TOKEN_LENGTH_LIMIT) {
        throw new IllegalArgumentException("maxTokenLength may not exceed " + MAX_TOKEN_LENGTH_LIMIT);
      }
      if (length != maxTokenLength) {
        maxTokenLength = length;
        scanner.setBufferSize(length);
      }
    }

    /** Returns the current maximum token length
     * 
     *  @see #setMaxTokenLength */
    public int getMaxTokenLength() {
      return maxTokenLength;
    }

    /**
     * Creates a new StandardTokenizer with a given {@link org.apache.lucene.util.AttributeFactory} 
     */
    public LD4LTokenizer(Reader reader) {
      super(reader);
      init();
    }

    private void init() {
      this.scanner = new ld4lLexerFlex(input);
    }

    // this tokenizer generates three attributes:
    // term offset, positionIncrement and type
    private final CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);
    private final OffsetAttribute offsetAtt = addAttribute(OffsetAttribute.class);
    private final PositionIncrementAttribute posIncrAtt = addAttribute(PositionIncrementAttribute.class);
    private final TypeAttribute typeAtt = addAttribute(TypeAttribute.class);

    /*
     * (non-Javadoc)
     *
     * @see org.apache.lucene.analysis.TokenStream#next()
     */
    @Override
    public final boolean incrementToken() throws IOException {
      clearAttributes();
      skippedPositions = 0;

      while(true) {
        int tokenType = scanner.getNextToken();

        if (tokenType == biomedicalLexerFlex.YYEOF) {
          return false;
        }

        if (scanner.yylength() <= maxTokenLength) {
          posIncrAtt.setPositionIncrement(skippedPositions+1);
          scanner.getText(termAtt);
          final int start = (int)scanner.yychar();
          offsetAtt.setOffset(correctOffset(start), correctOffset(start+termAtt.length()));
          typeAtt.setType(TOKEN_TYPES[0]); // tokenType
          return true;
        } else
          // When we skip a too-long term, we still increment the
          // position increment
          skippedPositions++;
      }
    }
    
    @Override
    public final void end() throws IOException {
      super.end();
      // set final offset
      int finalOffset = correctOffset((int)scanner.yychar() + scanner.yylength());
      offsetAtt.setOffset(finalOffset, finalOffset);
      // adjust any skipped tokens
      posIncrAtt.setPositionIncrement(posIncrAtt.getPositionIncrement()+skippedPositions);
    }

    @Override
    public void close() throws IOException {
      super.close();
      scanner.yyreset(input);
    }

    @Override
    public void reset() throws IOException {
      super.reset();
      scanner.yyreset(input);
      skippedPositions = 0;
    }
}
