package com.example;

import org.apache.commons.io.FileSystemUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;

public class MyClass {

    public static void main(String[] args) {
        System.out.println("Hello World!");
        for (int i = 0; i < args.length; i++) {
            System.out.println(args);
        }
        System.out.println("heeheheh");
        String filePath = "d:/s/e/a.rar";
        File file = new File(filePath);
//        mkDir(file);

        try {
            if(!file.exists()) {
                System.out.println("not have create");
                FileUtils.forceMkdirParent(file);
            }else{
                System.out.println("have hahaha");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        String filename = FilenameUtils.getName(file.getAbsolutePath());
        String fileParent = FilenameUtils.getFullPathNoEndSeparator(file.getAbsolutePath());
        System.out.println(filename+"文件路径"+fileParent);
    }

        public static void mkDir(File file) {
            if (file.getParentFile().exists()) {
                file.mkdir();
            } else {
                mkDir(file.getParentFile());
                file.mkdir();
            }
        }

    public static String getFileNameWithSuffix(String filePath){
        String fileName=null;
        return fileName;
    }


    }
