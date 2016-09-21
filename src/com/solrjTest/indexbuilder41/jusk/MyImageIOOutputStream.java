/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package indexbuilder41.jusk;

import java.io.IOException;
import java.io.OutputStream;

/**
 * A OutputStream which can be used to write Images
 * with the ImageIO in servlets.
 */
public class MyImageIOOutputStream extends OutputStream {

    private OutputStream out;
    private volatile boolean isActive = true;

    public MyImageIOOutputStream(OutputStream out) {
        this.out = out;
    }

    @Override
    public void close() throws IOException {
        if (isActive) {
            isActive = false; // deactivate
            try {
                out.close();
            } finally {
                out = null;
            }
        }
    }

    @Override
    public void flush() throws IOException {
      if(isActive) {
        out.flush();
      }
      // otherwise do nothing (prevent polluting the stream)
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        if (isActive)
            out.write(b, off, len);
    }

    @Override
    public void write(byte[] b) throws IOException {
        if (isActive)
            out.write(b);
    }

    @Override
    public void write(int b) throws IOException {
        if (isActive)
            out.write(b);
    }   

}