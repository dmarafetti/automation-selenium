package com.ideasquefluyen.selenium.xctrl.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;


/**
 *
 *
 *
 * @author dmarafetti
 *
 */
public abstract class StreamUtils {

	/**
	 * Copy bytes from one source to another. Close input stream after copy.
	 * @param source
	 * @param destiny
	 * @throws RuntimeException
	 */
	public static void copy(InputStream source, OutputStream destiny) throws RuntimeException {

		 ReadableByteChannel readableByteChannel = Channels.newChannel(source);
	     WritableByteChannel writableByteChannel = Channels.newChannel(destiny);
	     ByteBuffer byteBuffer					 = ByteBuffer.allocate(8096);

	     try {
	    	 while(readableByteChannel.read(byteBuffer) != -1) {

	    		 byteBuffer.flip();
	    		 writableByteChannel.write(byteBuffer);
	    		 byteBuffer.compact();
	    	 }

	    	 byteBuffer.flip();
	    	 while(byteBuffer.hasRemaining()) {
	    		 writableByteChannel.write(byteBuffer);
	    	 }
	    	 writableByteChannel.close();
	    	 source.close();

	     } catch(IOException ex) {
	    	 throw new RuntimeException(ex);
	     }

	}

}
