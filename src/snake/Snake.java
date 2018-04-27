package snake;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Snake extends JFrame{
    
    
    public Random rnd = new Random();
    Renderer render = new Renderer();
    boolean shouldrender = true;
    Clip eatingfx;
    
    public Snake(){
        this.setSize(700,500);
        this.setTitle("Snake Game");
        this.add(render);
        this.setVisible(true);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        
        KeyListener keys = new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_A){
                    if(render.dir != Direction.RIGHT)
                        render.dir = Direction.LEFT;
                }
                if(e.getKeyCode() == KeyEvent.VK_D){
                    if(render.dir != Direction.LEFT)
                        render.dir = Direction.RIGHT;
                }
                if(e.getKeyCode() == KeyEvent.VK_S){
                    if(render.dir != Direction.UP)
                        render.dir = Direction.DOWN;
                }
                if(e.getKeyCode() == KeyEvent.VK_W){
                    if(render.dir != Direction.DOWN)
                        render.dir = Direction.UP;
                }
                if(e.getKeyCode() == KeyEvent.VK_E){
                    if(!shouldrender){
                        System.out.println("Start rendering");
                        render.snake.clear();
                        render.score = 0;
                        render.dir = Direction.DOWN;
                        render.snake.add(new Coordinate(25,25));
                        render.snake.add(new Coordinate(25 + render.snake_width,25));
                        render.snake.add(new Coordinate(25 + render.snake_width * 2,25));
                        render.snake.add(new Coordinate(25 + render.snake_width * 3,25));
                        render.food = new Coordinate(rnd.nextInt(300/2), rnd.nextInt(400 / 2));
                        shouldrender = true;
                    }
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                
            }
        };
        
        this.addKeyListener(keys);
        
        Runnable rn = new Runnable() {
            @Override
            public void run() {
                try {
                    gameLoop();
                } catch (UnsupportedAudioFileException ex) {
                    Logger.getLogger(Snake.class.getName()).log(Level.SEVERE, null, ex);
                } catch (LineUnavailableException ex) {
                    Logger.getLogger(Snake.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        };
        Thread thrd = new Thread(rn);
        thrd.start();
        this.addWindowListener(new WindowAdapter(){
                public void windowClosing(WindowEvent e){
                    thrd.stop();
                }
        });
    }
    
    public void gameLoop() throws UnsupportedAudioFileException, LineUnavailableException{
        long drstart_time = System.currentTimeMillis();
        String filename = "bg.wav";
        Clip clip = AudioSystem.getClip();
        try {
            clip.open(AudioSystem.getAudioInputStream(new File(filename)));
        } catch (IOException ex) {
            Logger.getLogger(Snake.class.getName()).log(Level.SEVERE, null, ex);
        } catch (LineUnavailableException ex) {
            Logger.getLogger(Snake.class.getName()).log(Level.SEVERE, null, ex);
        }
        clip.start();
        
        String eat_sound = "eat.wav";
        
        try {
            eatingfx = AudioSystem.getClip();
            eatingfx.open(AudioSystem.getAudioInputStream(new File(eat_sound)));
        } catch (LineUnavailableException ex) {
            Logger.getLogger(Snake.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Snake.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedAudioFileException ex) {
            Logger.getLogger(Snake.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        long nanstart = System.nanoTime();
        
        while(true){
            
            
            while(System.nanoTime() - nanstart < 1000){
                
            }
            nanstart = System.nanoTime();
            
            if(!shouldrender){
                continue;
            }
            
            long drcurrent_time = System.currentTimeMillis();
            long delta = drcurrent_time - drstart_time;
            
            if(!clip.isRunning()){
                clip.setMicrosecondPosition(0);
                clip.start();
            }
            
            gameLogic();
            if(delta >= 30){
                render.repaint();
                drstart_time = drcurrent_time;
            }
        }
    }
    
    public void gameLogic(){
        Coordinate snek = render.snake.get(0);
        Coordinate fud = render.food;
        Color[] swap = {Color.BLACK,Color.GRAY,Color.PINK,Color.RED};
        
        
        
        if(Math.abs(snek.x - fud.x) < render.food_size + render.snake_width && Math.abs(snek.y - fud.y) < render.food_size + render.snake_width){
            render.food = new Coordinate(rnd.nextInt(this.getWidth() / 2), rnd.nextInt(this.getHeight() / 2));
            System.out.println("Food eated");
            eatingfx.setFramePosition(0);
            eatingfx.start();
            render.snake.add(new Coordinate(render.snake.get(render.snake.size() - 1).x, render.snake.get(render.snake.size() - 1).y));
            render.score += 10;
            render.snake_color = swap[rnd.nextInt(swap.length)];
        }
    }
    
    public class Renderer extends JPanel{
        
        private Random rndm = new Random();
        public final int snake_width = 10;
        public ArrayList<Coordinate> snake = new ArrayList<>();
        public Direction dir = Direction.DOWN;
        public final int food_size = 10;
        public Coordinate food = new Coordinate(rndm.nextInt(400), rndm.nextInt(400));
        public int score = 0;
        public Color snake_color = Color.BLACK;
        
        public Renderer(){
            snake.add(new Coordinate(25,25));
            snake.add(new Coordinate(25 + snake_width,25));
            snake.add(new Coordinate(25 + snake_width * 2,25));
            snake.add(new Coordinate(25 + snake_width * 3,25));
        }
        
        @Override
        public void paint(Graphics g){
            super.paint(g);
            Coordinate old_coordinate = snake.get(0);
            Coordinate new_coordinate = new Coordinate(0, 0);
            switch(dir){
                case DOWN:
                    new_coordinate = new Coordinate(old_coordinate.x, old_coordinate.y + snake_width);   
                    break;
                case UP:
                    new_coordinate = new Coordinate(old_coordinate.x, old_coordinate.y - snake_width);   
                    break;
                case LEFT:
                    new_coordinate = new Coordinate(old_coordinate.x - snake_width, old_coordinate.y);
                    break;
                case RIGHT:
                    new_coordinate = new Coordinate(old_coordinate.x + snake_width, old_coordinate.y);
                    break;
            }
            if(new_coordinate.x > this.getWidth())
                new_coordinate.x = 0;
            else if(new_coordinate.x < 0)
                new_coordinate.x = this.getWidth();
            else if(new_coordinate.y < 0)
                new_coordinate.y = this.getHeight();
            else if(new_coordinate.y > this.getHeight())
                new_coordinate.y = 0;
            for(int i = 1 ;i < snake.size();i++){
                Coordinate body = snake.get(i);
                if(new_coordinate.x >= body.x && new_coordinate.x <= body.x + snake_width && new_coordinate.y >= body.y && new_coordinate.y < body.y + snake_width){
                    String loose = "You loose, Press 'E' to play again...";
                    System.out.println("Stopped rendering");
                    g.drawChars(loose.toCharArray(), 0, loose.toCharArray().length, 250, 250);
                    shouldrender = false;       
                }
            }
            snake.set(0, new_coordinate);
            for(int i = 1; i < snake.size();i++){
                Coordinate temp = snake.get(i);
                snake.set(i, old_coordinate);
                old_coordinate = temp;
            }
            for(Coordinate pos: snake){
                if(snake.indexOf(pos) == 0){
                    g.setColor(Color.orange);
                    g.fillRect(pos.x, pos.y, snake_width, snake_width);
                }
                else{
                    g.setColor(snake_color);
                    g.fillRect(pos.x, pos.y, snake_width, snake_width);
                }
            }
            g.setColor(Color.red);
            g.fillRect(food.x, food.y, food_size, food_size);
            g.setColor(Color.darkGray);
            String scr = "Score : " + score;
            g.drawChars(scr.toCharArray(), 0, scr.toCharArray().length, 0, g.getFont().getSize());
            String instruction = "Press A = Left, D = Right, S = Down and W = Up";
            g.setColor(Color.red);
            g.drawChars(instruction.toCharArray(), 0, instruction.toCharArray().length, render.getWidth() - g.getFont().getSize() * instruction.toCharArray().length / 2, g.getFont().getSize());
        }
    }
    
    public enum Direction{
        LEFT,RIGHT,UP,DOWN
    }
    
    public class Coordinate{
        public int x = 0;
        public int y = 0;
        
        public Coordinate(int ex,int wy){
            this.x = ex;
            this.y = wy;
        }
    }
    
    public static void main(String[] args) {
        new Snake();
    }
    
}
