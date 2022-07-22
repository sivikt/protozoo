package servag.ftpserver.transmode;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import servag.ftpserver.datarepresent.Representation;

public class CompressedTransmissionMode extends TransmissionMode
{

	public CompressedTransmissionMode() 
	{
		super( "compressed", "C" );
	}

	@Override
	public void receiveFile(OutputStream out, Socket sock,
			Representation representation) throws IOException {
		// TODO Auto-generated method stub
	}

	@Override
	public void sendFile(InputStream in, Socket sock,
			Representation representation) throws IOException {
		// TODO Auto-generated method stub
	}

}
