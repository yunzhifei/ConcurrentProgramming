package com.bj.cachedthreadpool;

import java.util.ArrayList;
import java.util.concurrent.ThreadFactory;

public class MyThreaFactory implements ThreadFactory {

    @Override
    public Thread newThread(Runnable r) {
        return null;
    }
    public ArrayList<Integer> printMatrix(int [][] matrix) {
        ArrayList<Integer> list = new ArrayList<Integer>();
        int rowStart=0;
        int colStart=0;
        int rowEnd=matrix.length-1;
        int colEnd=matrix[0].length-1;
        while(rowStart<rowEnd&&colStart<colEnd){
            int colTemp=colStart;
            int rowTemp=rowStart;
            while(colTemp<colEnd){
                list.add(matrix[rowStart][colTemp++]);

            }
            while(rowTemp<rowEnd){
                list.add(matrix[rowTemp++][colEnd]);

            }
            while(colTemp>colStart){
                list.add(matrix[rowEnd][colTemp--]);

            }
            while(rowTemp>rowStart){
                list.add(matrix[rowTemp--][colStart]);

            }
            rowStart++;
            rowEnd--;
            colStart++;
            colEnd--;
        }
        if(rowStart==rowEnd){
            while(colStart<=colEnd){
                list.add(matrix[rowStart][colStart++]);
            }
        }else if(colStart==colEnd){
            while (rowStart<rowEnd){
                list.add(matrix[rowStart++][colStart]);
            }
        }

        return list;
    }
}
