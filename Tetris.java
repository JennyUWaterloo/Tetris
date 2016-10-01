package tetris;

import sun.invoke.empty.Empty;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.LinkedList;

/**
 * Created by bwbecker on 2016-09-19.
 */
public class Tetris extends JPanel implements ActionListener{
    private static int gameBoardWidth = 10;
    private static int gameBoardHeight = 24;
    private Block currentBlock;
    private Block.BlockType[] gameBoard;
    private int currentPositionX = 0;
    private int currentPositionY = 0;
    public int fps;
    public double speed;
    public String sequence;
    private int sequenceIndex = 0;
    public boolean isFalling = true;
    public boolean isGameOn = false;
    public boolean isGamePaused = false;
    public boolean isSplashScreen = true;
    public int totalScore = 0;
    public int currentScore = 0;
    public int currentLevel = 1;
    public Timer timer;
    public JLabel score;
    public Point mousePoint;
    public boolean isMouseInside = false;
    public JLabel splash;

    class MouseAdapterClass extends MouseAdapter {
        public void mouseClicked(MouseEvent e) {
            if (!currentBlock.isSelectedByMouse) {
                currentBlock.selectedByMouse();
            } else {
                lowerPiece(true);
            }
        }

        public void mouseEntered(MouseEvent e) {
            isMouseInside = true;
        }

        public void mouseExited(MouseEvent e) {
            isMouseInside = false;
        }

        public void mouseMoved(MouseEvent e) {
            if (!currentBlock.isSelectedByMouse || !isMouseInside) {
                return;
            }
            mousePoint = e.getPoint();
            if (mousePoint.getX() > 0) {
                tryToMove(currentBlock, (int)mousePoint.getX()/gameBoardWidth, currentPositionY);
            }
        }

        public void mouseWheelMoved(MouseWheelEvent e) {
            if (!currentBlock.isSelectedByMouse || !isMouseInside) {
                return;
            }
            tryToMove(currentBlock.rotateRight(), currentPositionX, currentPositionY);
        }
    }

    class KeyboardAdapter extends KeyAdapter {
        public void keyPressed(KeyEvent e) {
            if (currentBlock.getBlockType() == Block.BlockType.EmptyBlock || !isGameOn) return;

            int keyInput = e.getKeyCode();

            switch (keyInput) {
                case KeyEvent.VK_LEFT:
                    tryToMove(currentBlock, currentPositionX-1, currentPositionY);
                    break;
                case KeyEvent.VK_NUMPAD4:
                    tryToMove(currentBlock, currentPositionX-1, currentPositionY);
                    break;
                case KeyEvent.VK_RIGHT:
                    tryToMove(currentBlock, currentPositionX+1, currentPositionY);
                    break;
                case KeyEvent.VK_NUMPAD6:
                    tryToMove(currentBlock, currentPositionX+1, currentPositionY);
                    break;
                case KeyEvent.VK_SPACE:
                    lowerPiece(true);
                    break;
                case KeyEvent.VK_NUMPAD8:
                    lowerPiece(true);
                    break;
                case KeyEvent.VK_UP:
                    tryToMove(currentBlock.rotateRight(), currentPositionX, currentPositionY);
                    break;
                case KeyEvent.VK_X:
                    tryToMove(currentBlock.rotateRight(), currentPositionX, currentPositionY);
                    break;
                case KeyEvent.VK_NUMPAD1:
                    tryToMove(currentBlock.rotateRight(), currentPositionX, currentPositionY);
                    break;
                case KeyEvent.VK_NUMPAD5:
                    tryToMove(currentBlock.rotateRight(), currentPositionX, currentPositionY);
                    break;
                case KeyEvent.VK_NUMPAD9:
                    tryToMove(currentBlock.rotateRight(), currentPositionX, currentPositionY);
                    break;
                case KeyEvent.VK_CONTROL:
                    tryToMove(currentBlock.rotateLeft(), currentPositionX, currentPositionY);
                    break;
                case KeyEvent.VK_Z:
                    tryToMove(currentBlock.rotateLeft(), currentPositionX, currentPositionY);
                    break;
                case KeyEvent.VK_NUMPAD3:
                    tryToMove(currentBlock.rotateLeft(), currentPositionX, currentPositionY);
                    break;
                case KeyEvent.VK_NUMPAD7:
                    tryToMove(currentBlock.rotateLeft(), currentPositionX, currentPositionY);
                    break;
                case 'p':
                    pauseGame();
                    if (isGamePaused) return;
                    break;
                case 'P':
                    pauseGame();
                    if (isGamePaused) return;
                    break;
            }
        }
    }

    public Tetris(TetrisMain tetrisGame) {
        setFocusable(true);
        currentBlock = new Block();
        gameBoard = new Block.BlockType[gameBoardWidth*gameBoardHeight];
        this.score = tetrisGame.scoreLabel;

        for (int i = 0; i < gameBoardWidth*gameBoardHeight; i++) {
            gameBoard[i] = Block.BlockType.EmptyBlock;
        }

        KeyboardAdapter keyboardAdapter = new KeyboardAdapter();
        MouseAdapterClass mouseAdapterClass = new MouseAdapterClass();
        addMouseListener(mouseAdapterClass);
        addMouseMotionListener(mouseAdapterClass);
        addMouseWheelListener(mouseAdapterClass);
        addKeyListener(keyboardAdapter);
    }

    public void setFpsSpeedSequence(int fps, double speed, String sequence) {
        this.fps = fps;
        this.speed = speed;
        this.sequence = sequence;
        timer = new Timer(fps, this);
        timer.setDelay((int)speed*50);
    }

    public void setScore() {
        score.setText("TotalScore: " + totalScore + "   Lines Until Next Level: " + (currentLevel-currentScore) + "   Level: " + currentLevel);
    }

    public void drawGrid(Graphics g, int x, int y, Block.BlockType blockType) {
        g.setColor(getBlockColorAt(blockType));
        g.fillRect(x, y, getSingleGridWidth(), getSingleGridHeight());
    }

    public void paint(Graphics g) { //Override
        super.paint(g);
        if(!isGamePaused) {
            int topOfBoard = (int) getSize().getHeight()-gameBoardHeight*getSingleGridHeight();

            for (int i = 0; i < gameBoardHeight; i++) {
                for (int j = 0; j < gameBoardWidth; j++) {
                    Block.BlockType blockType = getSingleGridTypeAt(j, gameBoardHeight - i - 1);

                    if (blockType != Block.BlockType.EmptyBlock) {
                        drawGrid(g, j*getSingleGridWidth(), i*getSingleGridHeight()+topOfBoard, blockType);
                    }
                }
            }

            if (tryToMove(currentBlock, currentPositionX, currentPositionY)) {
                for (int i = 0; i < 4; i++) {
                    int x = currentPositionX + currentBlock.getBlockPositionX(i);
                    int y = gameBoardHeight - (currentPositionY - currentBlock.getBlockPositionY(i));
                    drawGrid(g, x * getSingleGridWidth(), topOfBoard + y * getSingleGridHeight(), currentBlock.getBlockType());
                }
            }

        } else {
            g.drawString("How To Play Tetris", (int)getSize().getWidth()/3, (int)getSize().getHeight()/4);
            g.drawString("Action", (int)getSize().getWidth()/12, (int)getSize().getHeight()/3);
            g.drawString("Pause", (int)getSize().getWidth()/12, (int)getSize().getHeight()/60*24);
            g.drawString("Left", (int)getSize().getWidth()/12, (int)getSize().getHeight()/60*27);
            g.drawString("Right", (int)getSize().getWidth()/12, (int)getSize().getHeight()/60*30);
            g.drawString("Drop", (int)getSize().getWidth()/12, (int)getSize().getHeight()/60*33);
            g.drawString("Rotate Right", (int)getSize().getWidth()/12, (int)getSize().getHeight()/60*36);
            g.drawString("Rotate Left", (int)getSize().getWidth()/12, (int)getSize().getHeight()/60*39);
            g.drawString("Select Piece", (int)getSize().getWidth()/12, (int)getSize().getHeight()/60*42);

            g.drawString("Keyboard", (int)(getSize().getWidth()/12*4), (int)getSize().getHeight()/3);
            g.drawString("P, p", (int)getSize().getWidth()/12*4, (int)getSize().getHeight()/60*24);
            g.drawString("Left Arrow", (int)getSize().getWidth()/12*4, (int)getSize().getHeight()/60*27);
            g.drawString("Right Arrow", (int)getSize().getWidth()/12*4, (int)getSize().getHeight()/60*30);
            g.drawString("Space Bar", (int)getSize().getWidth()/12*4, (int)getSize().getHeight()/60*33);
            g.drawString("Up Arrow, X", (int)getSize().getWidth()/12*4, (int)getSize().getHeight()/60*36);
            g.drawString("Control, Z", (int)getSize().getWidth()/12*4, (int)getSize().getHeight()/60*39);

            g.drawString("Numpad", (int)(getSize().getWidth()/12*7), (int)getSize().getHeight()/3);
            g.drawString("Num 4", (int)getSize().getWidth()/12*7, (int)getSize().getHeight()/60*27);
            g.drawString("Num 6", (int)getSize().getWidth()/12*7, (int)getSize().getHeight()/60*30);
            g.drawString("Num 8", (int)getSize().getWidth()/12*7, (int)getSize().getHeight()/60*33);
            g.drawString("Num 1, 5, 9", (int)getSize().getWidth()/12*7, (int)getSize().getHeight()/60*36);
            g.drawString("Num 3, 7", (int)getSize().getWidth()/12*7, (int)getSize().getHeight()/60*39);

            g.drawString("Mouse", (int)(getSize().getWidth()/12*9.5), (int)getSize().getHeight()/3);
            g.drawString("Move", (int)(getSize().getWidth()/12*9.5), (int)getSize().getHeight()/60*27);
            g.drawString("After Select", (int)(getSize().getWidth()/12*9.5), (int)getSize().getHeight()/60*28);
            g.drawString("Move", (int)(getSize().getWidth()/12*9.5), (int)getSize().getHeight()/60*30);
            g.drawString("After Select", (int)(getSize().getWidth()/12*9.5), (int)getSize().getHeight()/60*31);
            g.drawString("Click", (int)(getSize().getWidth()/12*9.5), (int)getSize().getHeight()/60*33);
            g.drawString("After Select", (int)(getSize().getWidth()/12*9.5), (int)getSize().getHeight()/60*34);
            g.drawString("Scroll", (int)(getSize().getWidth()/12*9.5), (int)getSize().getHeight()/60*36);
            g.drawString("After Select", (int)(getSize().getWidth()/12*9.5), (int)getSize().getHeight()/60*37);
            g.drawString("Scroll", (int)(getSize().getWidth()/12*9.5), (int)getSize().getHeight()/60*39);
            g.drawString("After Select", (int)(getSize().getWidth()/12*9.5), (int)getSize().getHeight()/60*40);
            g.drawString("Click", (int)(getSize().getWidth()/12*9.5), (int)getSize().getHeight()/60*42);

            if (isSplashScreen) {
                g.drawString("Press P To Play", (int)(getSize().getWidth()/3), (int)getSize().getHeight()/4*3);
            }
        }

    }

    public int getSingleGridWidth() {
        return (int)getSize().getWidth()/gameBoardWidth;
    }

    public int getSingleGridHeight() {
        return (int)getSize().getHeight()/gameBoardHeight;
    }

    public Block.BlockType getSingleGridTypeAt(int posX, int posY) {
        return gameBoard[posY*gameBoardWidth+posX];
    }

    public void resetGame() {
        for (int i = 0; i < gameBoardWidth*gameBoardHeight; i++) {
            gameBoard[i] = Block.BlockType.EmptyBlock;
        }
    }

    public void getNewBlock() {
        currentPositionX = gameBoardWidth/2;
        currentPositionY = gameBoardHeight + currentBlock.getminYValue()-1;
        Block.BlockType blockType;
        switch(sequence.toCharArray()[sequenceIndex]) {
            case 'I':
                blockType = Block.BlockType.IBlock;
                break;
            case 'L':
                blockType = Block.BlockType.LBlock;
                break;
            case 'J':
                blockType = Block.BlockType.JBlock;
                break;
            case 'S':
                blockType = Block.BlockType.SBlock;
                break;
            case 'Z':
                blockType = Block.BlockType.ZBlock;
                break;
            case 'O':
                blockType = Block.BlockType.OBlock;
                break;
            case 'T':
                blockType = Block.BlockType.TBlock;
                break;
            default:
                blockType = Block.BlockType.EmptyBlock;
                break;
        }
        currentBlock.setBlockTypeAndPosition(blockType);
        sequenceIndex++;
        if (sequenceIndex == sequence.length()) {
            sequenceIndex = 0;
        }
        if (!tryToMove(currentBlock, currentPositionX, currentPositionY-1)) {
            timer.stop();
            isGameOn = false;
            currentBlock.setBlockTypeAndPosition(Block.BlockType.EmptyBlock);
            score.setText("Game Over!");
        }
        isFalling = true;
        currentBlock.isSelectedByMouse = false;
    }

    public Color getBlockColorAt(Block.BlockType blockType) {
        switch (blockType) {
            case EmptyBlock:
                return Color.white;

            case IBlock:
                return Color.blue;

            case LBlock:
                return Color.orange;

            case JBlock:
                return Color.cyan;

            case TBlock:
                return Color.pink;

            case OBlock:
                return Color.yellow;

            case SBlock:
                return Color.green;

            case ZBlock:
                return Color.red;

            default:
                System.out.println("unrecognized block type");
                return null;
        }
    }

    public void gameOn() {
        if (isGamePaused) {
            return;
        }

        setScore();
        isGameOn = true;
        isFalling = true;
        currentScore = 0;
        resetGame();
        getNewBlock();
        timer.start();
        pauseGame();
    }

    public void pauseGame() {
        if (!isGameOn) {
            return;
        }

        if (isGamePaused) {
            isGamePaused = false;
            isSplashScreen = false;
            setScore();
            timer.start();
        } else {
            isGamePaused = true;
        }

        if (isGamePaused) {
            timer.stop();
            if (isSplashScreen) {
                score.setText("Welcome to Tetris");
            } else {
                score.setText("Game Paused; Press P");
            }
        }

        repaint();
    }

    public void oneDown() {
        for (int i = 0; i < 4; i++) {
            int nextXPosition = currentPositionX+currentBlock.getBlockPositionX(i);
            int nextRowPosition = (currentPositionY-currentBlock.getBlockPositionY(i))*gameBoardWidth;
            int gameBoardIndex = nextXPosition + nextRowPosition;
            gameBoard[gameBoardIndex] = currentBlock.getBlockType();
        }
        clearFullLines();
        if (isFalling) {
            getNewBlock();
        }
    }

    public void lowerPiece(boolean isDropped) {
        if (isDropped) {
            for (int i = currentPositionY; i > 0; i--) {
                if (!tryToMove(currentBlock, currentPositionX, i-1));
            }
            oneDown();
        } else {
            if (!tryToMove(currentBlock, currentPositionX, currentPositionY-1)) {
                oneDown();
            }
        }
    }

    public void clearLine(int yIndex) {
        for (int i = yIndex; i < gameBoardHeight-1; i++) {
            for (int j = 0; j < gameBoardWidth; j++) {
                int gameBoardRowIndex = i*gameBoardWidth;
                gameBoard[gameBoardRowIndex + j] = getSingleGridTypeAt(j, i+1);
            }
        }
    }

    public void clearFullLines() {
        boolean isRowFull;
        for (int i = gameBoardHeight-1; i > 0; i--) {
            isRowFull = true;
            for (int j = 0; j < gameBoardWidth; j++) {
                if (getSingleGridTypeAt(j, i) == Block.BlockType.EmptyBlock) {
                    isRowFull = false;
                }
            }

            if (isRowFull) {
                isFalling = false;
                clearLine(i);
                currentScore++;
                totalScore++;
                if (currentScore == currentLevel) {
                    currentLevel++;
                    currentScore = 0;
                    timer.setDelay(((int)speed-1)*50);
                }
                setScore();
                currentBlock.setBlockTypeAndPosition(Block.BlockType.EmptyBlock);

            }
        }
        repaint();
    }



    public boolean tryToMove(Block block, int posX, int posY) {
        int newPosX, newPosY;
        boolean canMove = false;
        for (int i = 0; i < 4; i++) {
            newPosX = block.getBlockPositionX(i) + posX;
            newPosY = posY - block.getBlockPositionY(i);
            if (newPosX < gameBoardWidth &&
                    newPosY < gameBoardHeight &&
                    newPosX >= 0 &&
                    newPosY >= 0 &&
                    getSingleGridTypeAt(newPosX, newPosY) == Block.BlockType.EmptyBlock) {
                canMove = true;
            } else {
                return false;
            }
        }

        currentBlock = block;
        currentPositionX = posX;
        currentPositionY = posY;

        repaint();
        return canMove;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (isFalling) {
            lowerPiece(false);
        } else {
            isFalling = false;
            getNewBlock();
        }
    }
}
