package soj.util;

import static org.apache.commons.io.FilenameUtils.getFullPath;
import static org.apache.commons.io.FilenameUtils.normalize;
import static org.slf4j.LoggerFactory.getLogger;

import static com.google.common.base.Charsets.UTF_8;
import static com.google.common.io.Files.newWriter;

public class FileWriter
{
    public FileWriter(String filename) {
        _filename = normalize(filename);
        _writer = getWriter(false);
    }

}