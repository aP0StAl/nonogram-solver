package com.apostal.nonogramsolver.util;

import org.opencv.core.Core;

import java.lang.reflect.Field;

public class OpenCVUtils {
    public static void loadOpenCV_Lib() throws Exception {
        // get the model
        String model = System.getProperty("sun.arch.data.model");
        // the path the .dll lib location
        String libraryPath = Const.Paths.OPENCV_x86;
        // check for if system is 64 or 32
        if(model.equals("64")) {
            libraryPath = Const.Paths.OPENCV_x64;
        }
        // set the path
        System.setProperty("java.library.path", libraryPath);
        Field sysPath = ClassLoader.class.getDeclaredField("sys_paths");
        sysPath.setAccessible(true);
        sysPath.set(null, null);
        // load the lib
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }
}
