package com.dib.neo.bkfs.fs;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class BKFile extends FileBase {
  @Override
  public long size() throws IOException {
    return 0;
  }

  @Override
  public long position() throws IOException {
    return 0;
  }

  @Override
  public FileChannel position(long newPosition) throws IOException {
    return null;
  }

  @Override
  public int read(ByteBuffer dst) throws IOException {
    return 0;
  }

  @Override
  public int write(ByteBuffer src) throws IOException {
    return 0;
  }

  @Override
  public FileChannel truncate(long size) throws IOException {
    return null;
  }
}
