package com.tofti.zcs;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class WoodsEnvironment {
    // the default is the Woods 1 environment
    private static final char[][] WOODS1;
    private static final String USER_DIR = System.getProperty("user.dir");

    static {
        WOODS1 = new char[][]{{'.','.','.','.','.'},
                {'.','O','O','F','.'},
                {'.','O','O','O','.'},
                {'.','O','O','O','.'},
                {'.','.','.','.','.'}};
    }

    // animat location
    private int animatRow, animatColumn;

    // map details
    private int mapRows;
    private int mapColumns;
    private char[][] map;

    // setup woods with the specified map
    private WoodsEnvironment(char[][] map) {
        this.map = map;
        mapRows = map.length;
        // assume we have no ragged arrays
        mapColumns = map[0].length;
        reset();
    }

    // determine a new starting point
    public void reset() {
        int initialAnimatRow;
        int initialAnimatColumn;

        do  {
            initialAnimatRow = (int)(Math.random() * mapRows);
            initialAnimatColumn = (int)(Math.random() * mapColumns);
        }while(map[initialAnimatRow][initialAnimatColumn] == 'O');

        animatRow = initialAnimatRow;
        animatColumn = initialAnimatColumn;
    }

    static final int MOVE_NORTH = 0b000;
    static final int MOVE_NORTH_EAST = 0b001;
    static final int MOVE_EAST = 0b010;
    static final int MOVE_SOUTH_EAST = 0b011;
    static final int MOVE_SOUTH = 0b100;
    static final int MOVE_SOUTH_WEST = 0b101;
    static final int MOVE_WEST = 0b110;
    static final int MOVE_NORTH_WEST = 0b111;

    // move the animat returns the reward
    public int move(char[] move) {
        int moveIndex = Integer.parseInt(new String(move), 2);
        int newAnimatRow = animatRow;
        int newAnimatColumn = animatColumn;

        switch(moveIndex) {
            case MOVE_NORTH:
                newAnimatRow = newAnimatRow - 1;
                break;

            case MOVE_NORTH_EAST:
                newAnimatRow = newAnimatRow - 1; newAnimatColumn = newAnimatColumn + 1;
                break;

            case MOVE_EAST:
                newAnimatColumn = newAnimatColumn + 1;
                break;

            case MOVE_SOUTH_EAST:
                newAnimatRow = newAnimatRow + 1; newAnimatColumn = newAnimatColumn + 1;
                break;

            case MOVE_SOUTH:
                newAnimatRow = newAnimatRow + 1;
                break;

            case MOVE_SOUTH_WEST:
                newAnimatRow = newAnimatRow + 1; newAnimatColumn = newAnimatColumn - 1;
                break;

            case MOVE_WEST:
                newAnimatColumn = newAnimatColumn -1;
                break;

            case MOVE_NORTH_WEST:
                newAnimatRow = newAnimatRow - 1; newAnimatColumn = newAnimatColumn - 1;
                break;
        }

        // wrap on row
        if(newAnimatRow > (mapRows - 1)) {
            newAnimatRow = 0;
        }
        else if(newAnimatRow < 0) {
            newAnimatRow = mapRows - 1;
        }
        // wrap on column
        if(newAnimatColumn > (mapColumns - 1)) {
            newAnimatColumn = 0;
        }
        else if(newAnimatColumn < 0) {
            newAnimatColumn = mapColumns - 1;
        }

        // if there is no rock then move
        if(map[newAnimatRow][newAnimatColumn] != 'O') {
            animatRow = newAnimatRow;
            animatColumn = newAnimatColumn;
        }

        // is there food?
        if(map[animatRow][animatColumn] == 'F') {
            return 1000;
        }
        return 0;
    }

    public char[] senseLocation() {
        String north = senseLocationValue(animatRow - 1 , animatColumn);
        String northEast = senseLocationValue(animatRow - 1, animatColumn + 1);
        String east = senseLocationValue(animatRow, animatColumn + 1);
        String southEast = senseLocationValue(animatRow + 1, animatColumn + 1);
        String south = senseLocationValue(animatRow + 1, animatColumn);
        String southWest = senseLocationValue(animatRow + 1, animatColumn - 1);
        String west = senseLocationValue(animatRow, animatColumn - 1);
        String northWest = senseLocationValue(animatRow - 1, animatColumn - 1);
        // concatenate them all
        String location = north + northEast + east + southEast + south + southWest + west + northWest;
        return location.toCharArray();
    }

    private String senseLocationValue(int pi, int pj) {
        // wrap on row
        if(pi > (mapRows - 1)) {
            pi = 0;
        }
        else if(pi < 0) {
            pi = mapRows - 1;
        }

        // wrap on column
        if(pj > (mapColumns - 1)) {
            pj = 0;
        }
        else if(pj < 0) {
            pj = mapColumns - 1;
        }

        char location = map[pi][pj];
        switch(location) {
            case '.':
                return "00";

            case 'O':
                return "10";

            case 'F':
                return "11";
        }
        return null;
    }

    public String toString() {
        StringBuffer mapBuffer = new StringBuffer();
        for(int row = 0; row < mapRows; row++) {
            for(int col = 0; col < mapColumns; col++) {
                if(row == animatRow && col == animatColumn) {
                    mapBuffer.append("*");
                } else {
                    mapBuffer.append(map[row][col]);
                }
            }
            mapBuffer.append("\n");
        }
        return mapBuffer.toString();
    }


    public static WoodsEnvironment getDefaultWoodsEnvironment() {
        return new WoodsEnvironment(WOODS1);
    }

    public static WoodsEnvironment getWoodsEnvironment(String filename) throws IOException {
        BufferedReader in = new BufferedReader(new FileReader(USER_DIR + filename));
        List<String> rows = new ArrayList<>();
        String currentLine;
        while((currentLine = in.readLine()) != null) {
            rows.add(currentLine);
        }
        char[][] map = new char[rows.size()][];
        for(int i = 0; i < rows.size(); i++) {
            map[i] = rows.get(i).toCharArray();
        }
        in.close();
        return new WoodsEnvironment(map);
    }
}