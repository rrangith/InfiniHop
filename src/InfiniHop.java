/**
 * Created by rahul on 2017-12-22.
 */
/* The InfiniHop Game
 * A game where the player must fight against gravity for as long as possible
 * Created By: Rahul Rangith and Justin Liao
 * Created on May 12 - June 12
 */

//Imports
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;

import javax.swing.*;

public class InfiniHop extends JApplet implements KeyListener, ActionListener, Runnable {

    //...VARIABLE DECLARATIONS...//
    // start game variables
    public static InfiniHop game;
    boolean extreme = false; //Checks to see if extreme mode
    boolean start = false; //Checks to see if game can start
    boolean fly = false; //Checks to see if fly mode

    // movement keys
    static boolean right = false;
    static boolean left = false;

    // variables declaration for ball
    int ballx = 160;
    int bally = 230;

    // variables declaration for special interactive objects
    //platform
    int platformx = 70;
    int platformy = 30;

    //blackhole
    int deathx = 70;
    int deathy = 75;

    //flight pad
    int flightx = 0;
    int flighty = 0;

    //slow field
    int slowx = 0;
    int slowy = 0;

    // variable declaration for ingame variables
    boolean gravity = true; //checks to see if ball can fall down
    boolean checkSlow = false; //checks to see if the ball is slowed down
    int level = 1; //level number
    int multiplier = 1; //variable to increase difficulty

    // declaring JComponents
    JButton survivalButton = new JButton ("Survival Mode");
    JButton extremeButton = new JButton ("Extreme Mode");
    JButton flightButton = new JButton ("Flight Mode");
    JButton instructionButton = new JButton("Instructions");
    Image charac = new ImageIcon(InfiniHop.class.getClassLoader().getResource("happy_smiley_face.png")).getImage(); //https://encrypted-tbn3.gstatic.com/images?q=tbn:ANd9GcQYMTbI7RNHzPyOkk9ktc0-E84EAoSwBkZKxfGYl8EoNREQIvNlsShNGQ
    Image back = new ImageIcon(InfiniHop.class.getClassLoader().getResource("game_background.png")).getImage(); //https://encrypted-tbn2.gstatic.com/images?q=tbn:ANd9GcTTe6SIaCu09LBxxNaGHU8OyX7g9moUkVAP-MvpJGxoGWgDRzm-
    Image platforms = new ImageIcon(InfiniHop.class.getClassLoader().getResource("platform.png")).getImage(); //data:image/jpeg;base64,/9j/4AAQSkZJRgABAQAAAQABAAD/2wCEAAkGBxASEBUSEQ8WFRIVFQ8VEhAVEBcXERgVFRYXFhUVFhUYHiggGBonGxUVITEhJSkrLi4u
    Image blackHole = new ImageIcon(InfiniHop.class.getClassLoader().getResource("black_hole.png")).getImage(); //https://mademistakes.com/images/paper-53-space-blackhole-3-lg.jpg
    Image flightPic = new ImageIcon(InfiniHop.class.getClassLoader().getResource("flight.png")).getImage(); //http://i.imgur.com/fKafPPx.jpg
    Image laser = new ImageIcon(InfiniHop.class.getClassLoader().getResource("laser.png")).getImage(); //https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSSUdkFt4_GGh_TMuonBgC_DPpEMGlAmVVJIl2SUIYNfJp0Y1a1
    JLabel background = new JLabel(new ImageIcon(InfiniHop.class.getClassLoader().getResource("menu_background.png"))); //https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTg_2LOkit3dGwjzxeL6Q5rQna-bBnGZiENMXcDUqPaQMn2jQyTDw
    JLabel instructions = new JLabel(new ImageIcon(InfiniHop.class.getClassLoader().getResource("Instructions.png")), JLabel.CENTER);
    JLabel desc = new JLabel();

    // declaring ball and special objects
    Rectangle Ball = new Rectangle(ballx, bally, 7, 7);   //size
    Rectangle[] Platform = new Rectangle[8];
    Rectangle[] PlatformEx = new Rectangle[4];
    Rectangle[] Death = new Rectangle[4];
    Rectangle Flight = new Rectangle();
    Rectangle Laser = new Rectangle();
    Rectangle Slow = new Rectangle();

    // declaring frames
    JFrame frame = new JFrame();
    JFrame frameG = new JFrame();
    JFrame frameI = new JFrame();

    //thread variable declaration
    Thread gameCounter = null;
    File highScoresFile = new File("highscores.txt"); //creates high score file
    int[] highScore = new int[3]; //array to store all 3 high scores for each gamemode (1 per gamemode)

    public InfiniHop() {
        game = this;      //set game to this frame
        setInstructFrame();    //set up instruction frame
        updateHighScore();    //read text file score and save in highscore array using method

        //set up main menu frame
        frame.setTitle ("InfiniHop!");
        frame.setSize (650, 360);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.setResizable(false);

        //background
        frame.setLayout(new BorderLayout());
        frame.add(background,BorderLayout.NORTH);

        background.setLayout(new BoxLayout(background, BoxLayout.PAGE_AXIS));
        JLabel title = new JLabel("InfiniHop");
        title.setForeground(Color.YELLOW);
        title.setFont(new Font("Arial", Font.ITALIC, 30));

        //add JComponents/buttons
        background.add(title);
        background.add(survivalButton);
        background.add(extremeButton);
        background.add(flightButton);
        background.add(instructionButton);

        //set various actionlisteners
        survivalButton.addActionListener (new ActionListener () {
            public void actionPerformed (ActionEvent event)
            {
                runGame();   //run game normally
            }
        });

        extremeButton.addActionListener (new ActionListener () {
            public void actionPerformed (ActionEvent event)
            {
                extreme = true;    //run game with extreme variable as true
                runGame();
            }
        });

        flightButton.addActionListener (new ActionListener () {
            public void actionPerformed (ActionEvent event)
            {
                fly = true;     //run game with fly variable as true
                runGame();
            }
        });

        instructionButton.addActionListener (new ActionListener () {
            public void actionPerformed (ActionEvent event)
            {
                frameI.setVisible(true);     //make instruction(frame) visible
            }
        });

    }

    //main method
    public static void main(String[] args) {
        new InfiniHop();
    }

    //void method, set up frame for instruction
    public void setInstructFrame() {
        frameI.setTitle("InfiniHop! (Instructions)");
        frameI.setSize(650, 400);
        frameI.setResizable(false);
        frameI.setLocationRelativeTo(null);
        frameI.getContentPane().setLayout(new FlowLayout(FlowLayout.LEADING,0,-20));
        frameI.add(instructions);   //add instruction picture
        frameI.setVisible(false);   //initialize as false
    }

    //void method, runs game and thread when used
    public void runGame() {
        JButton exitButton = new JButton("Back to Menu");   //add exit button for game

        //set up game frame
        frameG.setTitle ("InfiniHop!");
        frameG.setSize(350, 450);
        frameG.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frameG.getContentPane().add(game);
        frameG.add(exitButton, BorderLayout.SOUTH);

        //determine current mode (using variables) and display the JLabel
        if (fly == true)
            desc.setText("Flight Mode");
        else if (extreme == true)
            desc.setText("Extreme Mode");
        else
            desc.setText("Survival Mode");

        frameG.add(desc, BorderLayout.NORTH);

        frameG.setLocationRelativeTo(null);
        frameG.setResizable(false);
        frameG.setVisible(true);

        //actionlistener for exit button
        exitButton.addActionListener(new ActionListener () {
            public void actionPerformed (ActionEvent event)
            {
                frameG.setVisible(false);    //make frame invisible
                //reset all situational variables
                extreme = false;
                fly = false;
                start = false;
                level = 1;
                //stop thread if game is running
                if (gameCounter != null) {
                    System.out.println("Destroyed Thread: " + gameCounter.getId());
                    gameCounter.stop();
                }
                gameCounter = null;
            }
        });

        //set keylistener and
        game.addKeyListener(game);
        game.setFocusable(true);

        //if does not exist, run game
        if(gameCounter == null) {
            gameCounter = new Thread(game);
            System.out.println("Created Thread: " + gameCounter.getId());
            gameCounter.start();
        }

        //restart game for it to take effect
        bally = 999999;
        restart();
    }

    //draws graphics
    public void paint(Graphics g) {
        g.drawImage(back, 0,0, null);

        if (Slow != null && Slow.x != 0) { //slowfield
            g.setColor(Color.GRAY);
            g.fillOval(Slow.x, Slow.y, Slow.width, Slow.height);
        }

        if (Flight != null && Flight.x != 0)  //flight pad
            g.drawImage(flightPic, Flight.x, Flight.y, this);

        g.drawImage(charac, Ball.x, Ball.y, this);

        for (int i = 0; i < Platform.length; i++) //platform
            if (Platform[i] != null)
                g.drawImage(platforms, Platform[i].x, Platform[i].y-10, this);

        if (extreme == true) { //checks to see if extreme mode
            for (int i = 0; i < PlatformEx.length; i++)
                if (PlatformEx[i] != null)
                    g.drawImage(platforms, PlatformEx[i].x, PlatformEx[i].y-10, this);
        }

        if (level > 4) //checks to see if level is greater than 4
            for (int i = 0; i < Death.length; i++) //draws in black holes
                if (Death[i] != null)
                    g.drawImage(blackHole, Death[i].x, Death[i].y, this);

        if (Laser != null && Laser.y != 0 && level != 1) //draws in laser
            g.drawImage(laser, Laser.x-10, Laser.y-50, this);

        g.setColor(Color.red); //border
        g.drawRect(0, 0, 343, 394);
    }

    //........GAME LOOP........//
    //loop variable declaration
    double movex = 1.0; //amount ball moves in x direction
    double movey = -6.0; //amount ball moves in y direction
    int [] moveDeathx = new int[4]; //arraw for moving blackholes

    public void run() {
        while (true) {

            //...EXIT WINDOW OPERATIONS...//

            if (level == 1 && start == false) {
                countDown();
                start = true;
            }

            //...OBJECT INTERACTIONS...//
            //platform actions
            if (movey >= 0 && fly == false) {    //only interacts with character when it's is falling
                for (int i = 0; i < Platform.length; i++) {
                    if (Platform[i] != null && Platform[i].intersects(Ball)) {      //shifts character upwards upon impact
                        Platform[i] = null;
                        movey = -6;
                    } //end of second if statement (survival mode)
                } //end of for loop

                if (extreme == true) {     //more platforms more extreme mode
                    for (int i = 0; i < PlatformEx.length; i++) {
                        if (PlatformEx[i] != null && PlatformEx[i].intersects(Ball)) {      //shifts character upwards upon impact
                            PlatformEx[i] = null;
                            movey = -6;
                        }
                    } //end of for loop
                } //end of second if statement (extreme mode)
            } //end of first if statement

            //blackhole actions
            for (int i = 0; i < Death.length; i++) {
                if (Death[i] != null) {
                    if (Death[i].intersects(Ball)) {    //kills player upon impact
                        level = 1;
                        multiplier = 1;
                        restart();
                        start = false;
                    }
                    if (level > 19) {      //moves starting from level 20
                        if (Death[i].x > 333 || Death[i].x < 0) {
                            moveDeathx[i] = -moveDeathx[i];
                        }
                        Death[i].x += moveDeathx[i];
                    }
                } //end of first if statement
            } //end of for loop

            //flight block actions
            if (Flight != null && Flight.intersects(Ball)) {     //deletes the flight block and causing flying upon impact
                Flight = null;
                gravity = false;
            }

            //laser actions
            if (Laser != null && Laser.y != 0) {
                if (Laser.intersects(Ball)) {    //kills player upon impact
                    level = 1;
                    multiplier = 1;
                    restart();
                    start = false;
                }
                if (fly == false)       //moves upwards, speed depending on mode
                    Laser.y -= 0.45;
                else
                    Laser.y -= (0.15 + multiplier * 0.3);
            }

            //slow field actions
            if (Slow != null) {
                if (Slow.intersects(Ball))
                    checkSlow = true;
                else
                    checkSlow = false;
            }

            repaint();

            //...BALL VERTICAL MOVEMENT AND SPECIAL INTERACTIONS...//
            //gravity interactions:
            if (gravity == false)   //if flight block is triggered, ball flies up
                movey = -2 - multiplier;
            else if (fly == true)    //if flight mode is on, disable gravity and make ball go upwards
                movey = -3.5;
            else         //in normal circumstance, ball's vertical motion is always decreasing
                movey += 0.155;

            //when flight mode is on, cause ball to "repel" on contact with platform
            if (fly == true) {
                for (int i = 0; i < Platform.length; i++) {
                    if (Platform[i] != null && Platform[i].intersects(Ball)) {
                        Ball.y += 10;   //reduces balls vertical position
                        movey = 0;
                    }
                }
            }

            //slow field interaction
            movex = 1;   //default left and right key sensitivity
            if (checkSlow == true && fly == true) {
                movey = movey/(1.5 + multiplier * 0.3);  //slows ball according to multiplier during flight mode
                movex = movex/1.5;
            }
            else if (checkSlow == true) {
                movey = movey/1.01; //slows ball
                movex = movex/1.5; //slows ball
            }

            if (extreme == true)
                movex = movex*1.23;

            Ball.y += movey;

            //...BALL HORIZONTAL MOVEMENT...//
            if (left == true) {
                Ball.x -= 4 * movex; //left movement
                right = false; //stops right movement
            }
            if (right == true) {
                Ball.x += 4 * movex; //right movement
                left = false; //stops left movement
            }


            if (Ball.x < 0) { //checks if ball goes out of screen to left
                Ball.x = 342; //puts ball at border on right of screen
            }
            if (Ball.x > 343) { //checks if ball goes out of screen to right
                Ball.x = 1; //puts ball at border on left of screen
            }

            //...ADVANCE NEXT LEVEL...//
            if (Ball.y < 0) {
                level++; //increases level
                restart(); //redraws
                if (level > highScore[2] && fly == true) //checks if highscore
                    highScore[2] = level;
                else if (level > highScore[1] && extreme == true) //checks if highscore
                    highScore[1] = level;
                else if (level > highScore[0] && extreme == false && fly == false) //checks if highscore
                    highScore[0] = level;

                setHighScore(highScore); //sets new highscore

                if (fly == true)
                    desc.setText("Flight Mode - Current Level: Lvl " + level + " - High Score: Lvl " + highScore[2]); //shows level and current highscore
                else if (extreme == true)
                    desc.setText("Extreme Mode - Current Level: Lvl " + level + " - High Score: Lvl " + highScore[1]); //shows level and current highscore
                else
                    desc.setText("Survival Mode - Current Level: Lvl " + level + " - High Score: Lvl " + highScore[0]); //shows level and current highscore
            }

            //...FALLING DOWN...//
            if (Ball.y >= 450) {
                //reset all variables and restart game
                level = 1;
                multiplier = 1;
                restart();
                start = false;
            }
            //stop for a short while
            try {
                Thread.sleep(10);
            } catch (Exception ex) {
            } //try catch ends here
        } //while loop ends here
    } //method ends here


    //............. HANDLING KEY EVENTS................//
    //use override to gather these child classes
    @Override
    public void keyPressed(KeyEvent e) { //checks if keys are pressed
        int keyCode = e.getKeyCode();
        if (keyCode == KeyEvent.VK_LEFT) { //checks which arrow key
            left = true;
        }

        if (keyCode == KeyEvent.VK_RIGHT) { //checks which arrow key
            right = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) { //checks if arrow key is released
        int keyCode = e.getKeyCode();
        if (keyCode == KeyEvent.VK_LEFT) {
            left = false;
        }

        if (keyCode == KeyEvent.VK_RIGHT) {
            right = false;
        }
    }

    //does not matter, classes that do nothing for overriding purposes
    @Override
    public void keyTyped(KeyEvent arg0) {

    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }


    //....COUNT DOWN FOR GAME START..../
    public void countDown() { //countdown at top of screen
        System.out.println("Print Thread: " + Thread.currentThread().getId());
        for (int t = 3; t > 0; t--) {
            //countdown according to gamemodes
            if (fly == true)
                desc.setText("Flight Mode: Game Starts in " + t + "...");  //countdown
            else if (extreme == true)
                desc.setText("Extreme Mode: Game Starts in " + t + "..."); //countdown
            else
                desc.setText("Survival Mode: Game Starts in " + t + "..."); //countdown
            try {
                Thread.sleep(1000);
            } catch (Exception ex) {
            }
        }
        //header during first level of gamemode
        if (fly == true)
            desc.setText("Flight Mode: Game has started! Current Level: 1"); //header
        else if (extreme == true)
            desc.setText("Extreme Mode: Game has started! Current Level: 1"); //header
        else
            desc.setText("Survival Mode: Game has started! Current Level: 1"); //header
    }

    //....START/RESTART GAME AND REMAKE LEVEL...//
    public void restart() {
        requestFocus(true);
        ballx = 160; //resets ball position
        bally = 230; //resets ball position

        //assigns basic interaction shapes for objects
        Ball = new Rectangle(ballx, bally, 7, 7);
        Platform = new Rectangle[8];
        PlatformEx = new Rectangle[4];
        Death = new Rectangle[4];
        Laser = new Rectangle();
        Flight = new Rectangle();
        Slow = new Rectangle();

        gravity = true; //sets gravity to true
        //resets movement
        movex = 1;
        movey = -6;

        if (extreme == true) //checks if mode is extreme
            multiplier = 3; //increases difficulty
        else if (level == 40 || level == 60)
            multiplier++; //increases difficulty

        platformx = (int)(Math.random()*300); //randomly generates platforms
        platformy = 30;
        for (int i = 0; i < Platform.length; i++) //generates all platforms in array
        {
            Platform[i] = new Rectangle(platformx, platformy, 50, 5);
            platformx = (int)(Math.random()*300); //randomizer 0-299
            if (i%2==0 && extreme == true) {
                PlatformEx[i/2] = new Rectangle(platformx, platformy, 50, 5);
                platformx = (int)(Math.random()*300);
            }
            platformy +=45; //spaced out 45 pixels in y direction
        }

        if (level > 4) {
            deathy = 75;   //set starting black hole vertical position
            for (int i = 0; i < Death.length; i++)  //randomly generates black hole positions
            { do {
                deathx = (int)(Math.random()*300); //randomizer 0-299
                Death[i] = new Rectangle(deathx, deathy, 10, 10);
            } while (deathx > 100 && deathx < 200); //makes sure black holes are in certain area
                deathy +=65; //spaced out 65 pixels in y direction

                //moves black holes according to current multiplier and position using moveDeathx array
                if (Death[i].x <= 100)
                    moveDeathx[i] = -multiplier;
                else if (Death[i].x >= 200)
                    moveDeathx[i] = multiplier;
            }
        }

        if (level > 7 && fly == false) { //checks if level is over 7 and not flight mode
            int extra = (int)(Math.random()*8); //randomizer for powerups
            if (extra == 2) //option for flight pad
            {
                flightx = (int)((Math.random()*200)+50); //randomizes flight pad
                flighty = (int)((Math.random()*50)+300); //randomizes flight pad
                Flight = new Rectangle(flightx, flighty, 200, 10);
            }
            else if (extra == 4) //option for laser
                Laser = new Rectangle(0, 350, 350, 400);
            else if (extra == 6) //option for slowfield
            {
                slowx = (int)((Math.random()*200)+50); //randomizes slowfield
                slowy = (int)((Math.random()*100)+100); //randomizes slowfield
                Slow = new Rectangle(slowx, slowy, 70+(20*multiplier), 70+(20*multiplier)); //creates depending on difficulty, higher difficulty, bigger slow field
            }
        }

        if (level > 9 && fly == true) { //generates obstacles to make flight mode more difficult
            Laser = new Rectangle(0, 350, 350, 400);
            slowx = (int)((Math.random()*200)+50); //randomizes slowfield
            slowy = (int)((Math.random()*100)+100); //randomizes slowfield
            Slow = new Rectangle(slowx, slowy, 70+(20*multiplier), 70+(20*multiplier));
        }

        repaint();  //repaint graphics
    }


    public void updateHighScore() { //to read text file with scores
        try {
            Scanner scanner = new Scanner(highScoresFile); //creates new scanner to input high scores from text file
            int count = 0; //to get all 3 highscores saved
            while (scanner.hasNext()) {
                highScore[count] = scanner.nextInt();
                count++;
            }
            scanner.close();
        } catch (FileNotFoundException e) { // File doesn't exist yet
            for (int i = 0; i < 3; i++)
                highScore[i] = 0; //high score is 0 from the start
        }
    }
    //to save highscore
    public void setHighScore(int[] score) {
        try {
            PrintWriter writer = new PrintWriter(highScoresFile); //creates a print writer
            for (int i = 0; i < score.length; i++) {
                writer.println("" + score[i]);     //write scores to text file
            }
            writer.close();
        } catch (FileNotFoundException e) {    //avoid FileNotFoundException
        }

        updateHighScore();   //use this method to read text file with scores
    }
}
