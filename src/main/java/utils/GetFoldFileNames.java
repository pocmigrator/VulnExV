package utils;

import com.google.common.collect.Lists;

import java.io.File;
import java.util.List;
import java.util.Objects;

public class GetFoldFileNames {

    public static List<String> readFiles(String filepath) {
        return readFileWithType(filepath, "java");
    }

    public static List<String> readFileWithType(String filepath, String type) {
        List<String> fileNames = Lists.newArrayList();
        try {
            File file = new File(filepath);
            if (!file.exists()) {
                System.out.println(filepath + " no files");
                return Lists.newArrayList();
            }

            if (file.isFile() && filepath.endsWith(type)) {
                fileNames.add(filepath);
            }

            if (file.isDirectory()) {
                String[] filelist = file.list();
                for (int i = 0; i < Objects.requireNonNull(filelist).length; i++) {
                    File readfile = new File(filepath + "/" + filelist[i]);
                    if (!readfile.isDirectory()) {
                        String fileName = readfile.getName();
                        String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
                        if (suffix.equals(type)) {
                            fileNames.add(filepath + "/" + filelist[i]);
                        }
                    } else if (readfile.isDirectory()) {
                        fileNames.addAll(readFileWithType(filepath + "/" + filelist[i], type));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.error("readfile()  Exception:" + e.getMessage());
        }
        return fileNames;
    }
}