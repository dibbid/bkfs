package com.dib.neo.bkfs.fs;

import java.io.IOException;
import java.net.URI;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.FileAttributeView;
import java.nio.file.spi.FileSystemProvider;
import java.util.Map;
import java.util.Set;

public class BKFileSystemProvider extends FileSystemProvider {

  public static final String BKFILE_SCEME = "bkfile";

  @Override
  public String getScheme() {
      return BKFILE_SCEME;
  }

  private void checkUri(URI var1) {
    if (!var1.getScheme().equalsIgnoreCase(this.getScheme())) {
      throw new IllegalArgumentException("URI does not match this provider");
    } else if (var1.getAuthority() != null) {
      throw new IllegalArgumentException("Authority component present");
    } else if (var1.getPath() == null) {
      throw new IllegalArgumentException("Path component is undefined");
    } else if (!var1.getPath().equals("/")) {
      throw new IllegalArgumentException("Path component should be '/'");
    } else if (var1.getQuery() != null) {
      throw new IllegalArgumentException("Query component present");
    } else if (var1.getFragment() != null) {
      throw new IllegalArgumentException("Fragment component present");
    }
  }

  @Override
  public FileSystem newFileSystem(URI var1, Map<String, ?> var2) throws IOException {
    this.checkUri(var1);
    throw new FileSystemAlreadyExistsException();
  }

  @Override
  public FileSystem getFileSystem(URI uri) {
    return null;
  }

  @Override
  public Path getPath(URI uri) {

    return null;
  }

  @Override
  public SeekableByteChannel newByteChannel(Path path, Set<? extends OpenOption> options, FileAttribute<?>... attrs) throws IOException {
    return null;
  }

  @Override
  public DirectoryStream<Path> newDirectoryStream(Path dir, DirectoryStream.Filter<? super Path> filter) throws IOException {
    return null;
  }

  @Override
  public void createDirectory(Path dir, FileAttribute<?>... attrs) throws IOException {

  }

  @Override
  public void delete(Path path) throws IOException {

  }

  @Override
  public void copy(Path source, Path target, CopyOption... options) throws IOException {

  }

  @Override
  public void move(Path source, Path target, CopyOption... options) throws IOException {

  }

  @Override
  public boolean isSameFile(Path path, Path path2) throws IOException {
    return false;
  }

  @Override
  public boolean isHidden(Path path) throws IOException {
    return false;
  }

  @Override
  public FileStore getFileStore(Path path) throws IOException {
    return null;
  }

  @Override
  public void checkAccess(Path path, AccessMode... modes) throws IOException {

  }

  @Override
  public <V extends FileAttributeView> V getFileAttributeView(Path path, Class<V> type, LinkOption... options) {
    return null;
  }

  @Override
  public <A extends BasicFileAttributes> A readAttributes(Path path, Class<A> type, LinkOption... options) throws IOException {
    return null;
  }

  @Override
  public Map<String, Object> readAttributes(Path path, String attributes, LinkOption... options) throws IOException {
    return null;
  }

  @Override
  public void setAttribute(Path path, String attribute, Object value, LinkOption... options) throws IOException {

  }
}
