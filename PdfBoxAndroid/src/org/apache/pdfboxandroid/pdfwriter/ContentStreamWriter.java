package org.apache.pdfboxandroid.pdfwriter;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import org.apache.pdfboxandroid.cos.COSArray;
import org.apache.pdfboxandroid.cos.COSBase;
import org.apache.pdfboxandroid.cos.COSBoolean;
import org.apache.pdfboxandroid.cos.COSDictionary;
import org.apache.pdfboxandroid.cos.COSFloat;
import org.apache.pdfboxandroid.cos.COSInteger;
import org.apache.pdfboxandroid.cos.COSName;
import org.apache.pdfboxandroid.cos.COSString;
import org.apache.pdfboxandroid.util.ImageParameters;
import org.apache.pdfboxandroid.util.PDFOperator;

/**
 * A class that will take a list of tokens and write out a stream with them.
 *
 * @author <a href="mailto:ben@benlitchfield.com">Ben Litchfield</a>
 * @version $Revision: 1.8 $
 */
public class ContentStreamWriter {
	/**
     * space character.
     */
    public static final byte[] SPACE = new byte[] { 32 };

    /**
     * standard line separator
     */
    public static final byte[] EOL = new byte[] { 0x0A };
	
	private OutputStream output;
	
	/**
     * This will create a new content stream writer.
     *
     * @param out The stream to write the data to.
     */
    public ContentStreamWriter( OutputStream out )
    {
        output = out;
    }
    
    /**
     * This will write out the list of tokens to the stream.
     *
     * @param tokens The tokens to write to the stream.
     * @param start The start index into the list of tokens.
     * @param end The end index into the list of tokens.
     * @throws IOException If there is an error writing to the stream.
     */
    public void writeTokens( List tokens, int start, int end ) throws IOException
    {
        for( int i=start; i<end; i++ )
        {
            Object o = tokens.get( i );
            writeObject( o );
            //write a space between each object.
            output.write( 32 );
        }
        output.flush();
    }

    private void writeObject( Object o ) throws IOException
    {
        if( o instanceof COSString )
        {
            ((COSString)o).writePDF( output );
        }
        else if( o instanceof COSFloat )
        {
            ((COSFloat)o).writePDF( output );
        }
        else if( o instanceof COSInteger )
        {
            ((COSInteger)o).writePDF( output );
        }
        else if( o instanceof COSBoolean )
        {
            ((COSBoolean)o).writePDF( output );
        }
        else if( o instanceof COSName )
        {
            ((COSName)o).writePDF( output );
        }
        else if( o instanceof COSArray )
        {
            COSArray array = (COSArray)o;
            output.write(COSWriter.ARRAY_OPEN);
            for( int i=0; i<array.size(); i++ )
            {
                writeObject( array.get( i ) );
                output.write( SPACE );
            }

            output.write( COSWriter.ARRAY_CLOSE );
        }
        else if( o instanceof COSDictionary )
        {
            COSDictionary obj = (COSDictionary)o;
            output.write( COSWriter.DICT_OPEN );
            for (Map.Entry<COSName, COSBase> entry : obj.entrySet())
            {
                if (entry.getValue() != null)
                {
                    writeObject( entry.getKey() );
                    output.write( SPACE );
                    writeObject( entry.getValue() );
                    output.write( SPACE );
                }
            }
            output.write( COSWriter.DICT_CLOSE );
            output.write( SPACE );
        }
        else if( o instanceof PDFOperator )
        {
            PDFOperator op = (PDFOperator)o;
            if( op.getOperation().equals( "BI" ) )
            {
                output.write( "BI".getBytes("ISO-8859-1") );
                ImageParameters params = op.getImageParameters();
                COSDictionary dic = params.getDictionary();
                for( COSName key : dic.keySet() )
                {
                    Object value = dic.getDictionaryObject( key );
                    key.writePDF( output );
                    output.write( SPACE );
                    writeObject( value );
                    output.write( EOL );
                }
                output.write( "ID".getBytes("ISO-8859-1") );
                output.write( EOL );
                output.write( op.getImageData() );
            }
            else
            {
                output.write( op.getOperation().getBytes("ISO-8859-1") );
                output.write( EOL );
            }
        }
        else
        {
            throw new IOException( "Error:Unknown type in content stream:" + o );
        }
    }

    /**
     * This will write out the list of tokens to the stream.
     *
     * @param tokens The tokens to write to the stream.
     * @throws IOException If there is an error writing to the stream.
     */
    public void writeTokens( List tokens ) throws IOException
    {
        writeTokens( tokens, 0, tokens.size() );
    }
}
