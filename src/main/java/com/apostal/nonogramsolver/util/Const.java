package com.apostal.nonogramsolver.util;

public interface Const extends ConstTextRU {
    interface Paths {
        String IMAGES = ".\\images";
        String MODEL = ".\\model";
        String OPENCV_x86 = ".\\opencv\\x86";
        String OPENCV_x64 = ".\\opencv\\x64";
    }

    interface Default {
        int CELL_SIZE = 15;
    }

    interface FileName {
        String MODEL = "cnn-model.data";
    }

    interface Pattern {
        String LOG = "{}: {}";
    }
}
