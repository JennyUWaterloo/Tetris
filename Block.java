package tetris;

import java.util.Random;

public class Block {
    enum BlockType {
        EmptyBlock(new int[][] {{0,0}, {0,0}, {0,0}, {0,0}}),
        IBlock(new int[][] {{0,-1}, {0,0}, {0,1}, {0,2}}),
        LBlock(new int[][] {{1,-1}, {0,-1}, {0,0}, {0,1}}),
        JBlock(new int[][] {{-1,-1}, {0,-1}, {0,0}, {0,1}}),
        TBlock(new int[][] {{-1,0}, {0,0}, {0,-1}, {1,0}}),
        OBlock(new int[][] {{0,0}, {1,0}, {0,1}, {1,1}}),
        SBlock(new int[][] {{-1,-1}, {0,-1}, {0,0}, {1,0}}),
        ZBlock(new int[][] {{-1,1}, {0,1}, {0,0}, {1,0}});

        public int[][] position;

        private BlockType(int[][] position) {
            this.position = position;
        }
    }

    private int[][] position;
    private BlockType blockType;
    public boolean isSelectedByMouse;

    public Block() {
        this.position = new int [4][4];
        this.setBlockTypeAndPosition(BlockType.EmptyBlock);
        isSelectedByMouse = false;
    }

    public void selectedByMouse() {
        isSelectedByMouse = true;
    }

    public void setBlockTypeAndPosition (BlockType blockType) {
        this.blockType = blockType;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 2; j++) {
                this.position[i][j] = blockType.position[i][j];
            }
        }
    }

    public void setBlockPositionX (int xIndex, int newX) {
        this.position[xIndex][0] = newX;
    }

    public int getBlockPositionX (int xIndex) {
        return position[xIndex][0];
    }

    public void setBlockPositionY (int yIndex, int newY) {
        this.position[yIndex][1] = newY;
    }

    public int getBlockPositionY (int yIndex) {
        return position[yIndex][1];
    }

    public BlockType getBlockType() {
        return this.blockType;
    }

    public int getminYValue() {
        //first y value of the block
        int minYPosition = position[0][1];

        for (int i = 0; i < 4; i++) {
            minYPosition = Math.min(position[i][1], minYPosition);
        }

        return minYPosition;
    }

    public Block rotateLeft() {
        if (this.blockType == BlockType.OBlock) {
            return this;
        } else {
            Block rotatedBlock = new Block();
            rotatedBlock.blockType = this.blockType;

            for (int i = 0; i < 4; i++) {
                rotatedBlock.setBlockPositionX(i, getBlockPositionY(i));
                rotatedBlock.setBlockPositionY(i, -(getBlockPositionX(i)));
            }

            return rotatedBlock;
        }
    }

    public Block rotateRight() {
        if (this.blockType == BlockType.OBlock) {
            return this;
        } else {
            Block rotatedBlock = new Block();
            rotatedBlock.blockType = this.blockType;

            for (int i = 0; i < 4; i++) {
                rotatedBlock.setBlockPositionX(i, -(getBlockPositionY(i)));
                rotatedBlock.setBlockPositionY(i, getBlockPositionX(i));
            }

            return rotatedBlock;
        }
    }
}
