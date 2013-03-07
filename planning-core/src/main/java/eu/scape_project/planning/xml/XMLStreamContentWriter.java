package eu.scape_project.planning.xml;

import java.io.IOException;
import java.io.Writer;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

/**
 * A simple {@link Writer} which outputs the content to a given {@link XMLStreamWriter} 
 * 
 * @author Michael Kraxner
 *
 */
public class XMLStreamContentWriter extends Writer {
    
    private XMLStreamWriter streamWriter;
    
    public XMLStreamContentWriter(final XMLStreamWriter streamWriter) {
        this.streamWriter = streamWriter;
    }

    @Override
    public void close() throws IOException {
    }

    @Override
    public void flush() throws IOException {
    }

    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
        try {
            streamWriter.writeCharacters(cbuf, off, len);
        } catch (XMLStreamException e) {
            throw new IOException(e);
        }
    }

}
