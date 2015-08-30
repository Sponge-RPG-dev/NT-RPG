package cz.neumimto.scripting;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

/**
 * Created by NeumimTo on 14.3.2015.
 */
public class FileInputStreamReader extends InputStreamReader {

    public FileInputStreamReader(InputStream in) {
        super(in);
    }

    public FileInputStreamReader(InputStream in, String charsetName) throws UnsupportedEncodingException {
        super(in, charsetName);
    }

    public FileInputStreamReader(InputStream in, Charset cs) {
        super(in, cs);
    }

    public FileInputStreamReader(InputStream in, CharsetDecoder dec) {
        super(in, dec);
    }

    public FileInputStreamReader(File file) throws FileNotFoundException {
        this(new FileInputStream(file));
    }
}
