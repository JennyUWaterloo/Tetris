package tetris;

import com.sun.javafx.tools.ant.Info;

import javax.swing.*;
import java.awt.*;

public class TetrisMain extends JFrame{
    public JLabel scoreLabel;
    public JLabel splashLabel;

    public TetrisMain() {
        setSize(400,800);
        scoreLabel = new JLabel("WELCOME TO TETRIS");
        add(scoreLabel, BorderLayout.NORTH);
        splashLabel = new JLabel("How to play:");
        add(splashLabel, BorderLayout.CENTER);

        Tetris tetris = new Tetris(this);
        tetris.setFpsSpeedSequence(ProgramArgs.fps, ProgramArgs.speed, ProgramArgs.sequence);
        add(tetris);

        tetris.gameOn();

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    public static void main(String[] args) {

        try {
            ProgramArgs.parseArgs(args);
            TetrisMain tetrisGame = new TetrisMain();
            tetrisGame.setLocationRelativeTo(null);
            tetrisGame.setVisible(true);

        } catch (IllegalArgumentException e) {
            System.out.println(e);
        }
    }
}


