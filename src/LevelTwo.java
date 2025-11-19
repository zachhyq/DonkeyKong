import bagel.*;
import bagel.Input;
import java.util.Properties;
import java.util.ArrayList;

/**
 * This class  contains all the attributes and instances used in LevelTwo of the game.
 * Large parts similar to lvl 1, with different game logic implemented in the update section
 * Notable modifications are the monkeys and projectiles.
 */
public class LevelTwo {
    private final Properties GAME_PROPS;

    private Mario mario;
    private Barrel[] barrels;   // Array of barrels in the game
    private Ladder[] ladders;   // Array of ladders in the game
    private Hammer hammer;      // The hammer object that Mario can collect
    private Donkey donkey;      // Donkey Kong, the objective of the game
    private Image background;   // Background image for the game
    private Platform[] platforms; // Array of platforms in the game
    private Gun[] guns;
    private ArrayList<Monkey> monkeys;
    private ArrayList<Projectile> projectiles;

    // Frame tracking
    private int currFrame = 0;  // Tracks the number of frames elapsed

    // Game parameters
    private final int MAX_FRAMES;  // Maximum number of frames before game ends

    // Display text variables
    private final Font STATUS_FONT;
    private final int SCORE_X;
    private final int SCORE_Y;
    private final Font HEALTH_FONT;
    private final Font BULLET_FONT;
    private final int HEALTH_X;
    private final int HEALTH_Y;
    private static final String SCORE_MESSAGE = "SCORE ";
    private static final String TIME_MESSAGE = "Time Left ";
    private static final String HEALTH_MESSAGE = "DONKEY HEALTH ";
    private static final String BULLET_MESSAGE = "BULLET ";

    //Scoring constants
    private static final int BARREL_SCORE = 100;
    private static final int TIME_DISPLAY_DIFF_Y = 30;
    private static final int BARREL_CROSS_SCORE = 30;
    private static final int MONKEY_KILL_PRIZE = 100;
    private int score = 0;  // Player's score for jumping over barrels
    private boolean isGameOver = false; // Game over flag


    /**
     * This class is a constructor for the level Two game screen. Take in all the
     * settings from app.props, parse them and instantiate in-game objects.
     * @param gameProps the settings file
     * @param retainScore score from lvl 1 if it was played.
     */
    public LevelTwo(Properties gameProps, int retainScore) {
        this.score = retainScore;
        this.GAME_PROPS = gameProps;

        // Load game parameters
        this.MAX_FRAMES = Integer.parseInt(gameProps.getProperty("gamePlay.maxFrames"));

        //LHS GamePlayText
        this.STATUS_FONT = new Font(
                gameProps.getProperty("font"),
                Integer.parseInt(gameProps.getProperty("gamePlay.score.fontSize"))
        );
        this.SCORE_X = Integer.parseInt(gameProps.getProperty("gamePlay.score.x"));
        this.SCORE_Y = Integer.parseInt(gameProps.getProperty("gamePlay.score.y"));

        //RHS GamePlayText (Health + Bullets)
        this.HEALTH_FONT = new Font(
                gameProps.getProperty("font"),
                Integer.parseInt(gameProps.getProperty("gamePlay.score.fontSize"))
        );
        this.BULLET_FONT = new Font(
                gameProps.getProperty("font"),
                Integer.parseInt(gameProps.getProperty("gamePlay.score.fontSize"))
        );
        String []healthCoords = gameProps.getProperty("gamePlay.donkeyhealth.coords").split(",");
        this.HEALTH_X = Integer.parseInt(healthCoords[0]);
        this.HEALTH_Y = Integer.parseInt(healthCoords[1]);

        this.background = new Image("res/background.png");

        // Initialize game objects
        initializeGameObjects();
    }

    /**
     * Class to initialise all game assets required in the level
     * Parses the gameProps data to initialise in correct locations.
     */
    private void initializeGameObjects() {
        // 1) Create Mario
        String[] marioCoords = GAME_PROPS.getProperty("mario.level1").split(",");
        double marioX = Double.parseDouble(marioCoords[0]);
        double marioY = Double.parseDouble(marioCoords[1]);
        this.mario = new Mario(marioX, marioY);

        // 2) Create Donkey Kong
        String[] donkeyCoords = GAME_PROPS.getProperty("donkey.level1").split(",");
        double donkeyX = Double.parseDouble(donkeyCoords[0]);
        double donkeyY = Double.parseDouble(donkeyCoords[1]);
        this.donkey = new Donkey(donkeyX, donkeyY);

        // 3) Create the Barrels array
        int barrelCount = Integer.parseInt(GAME_PROPS.getProperty("barrel.level2.count"));
        this.barrels = new Barrel[barrelCount];
        int barrelIndex = 0;
        for (int i = 1; i <= barrelCount; i++) {
            String barrelData = GAME_PROPS.getProperty("barrel.level2." + i);
            if (barrelData != null) {
                String[] coords = barrelData.split(",");
                if (coords.length < 2) {
                    System.out.println("Warning: Incomplete data for barrel." + i);
                    continue; // Skip invalid entries
                }
                double barrelX = Double.parseDouble(coords[0]);
                double barrelY = Double.parseDouble(coords[1]);
                if (barrelIndex < barrelCount) {
                    barrels[barrelIndex] = new Barrel(barrelX, barrelY);
                    barrelIndex++;
                }
            }
        }

        // 4) Create the Ladders array
        int ladderCount = Integer.parseInt(GAME_PROPS.getProperty("ladder.level2.count"));
        this.ladders = new Ladder[ladderCount];
        int ladderIndex = 0;
        for (int i = 1; i <= ladderCount; i++) {
            String ladderData = GAME_PROPS.getProperty("ladder.level2." + i);
            if (ladderData != null) {
                String[] coords = ladderData.split(",");
                if (coords.length < 2) {
                    System.out.println("Warning: Incomplete data for ladder." + i);
                    continue; // Skip invalid entries
                }
                double ladderX = Double.parseDouble(coords[0]);
                double ladderY = Double.parseDouble(coords[1]);
                if (ladderIndex < ladderCount) {
                    ladders[ladderIndex] = new Ladder(ladderX, ladderY);
                    ladderIndex++;
                }
            }
        }
        //Create guns
        int gunCount = Integer.parseInt(GAME_PROPS.getProperty("blaster.level2.count"));
        this.guns = new Gun[gunCount];
        int gunIndex = 0;
        for (int i=1; i<= gunCount; i++){
            String[] gunCoords = GAME_PROPS.getProperty("blaster.level2." + i).split(",");
            double gunX = Double.parseDouble(gunCoords[0]);
            double gunY = Double.parseDouble(gunCoords[1]);
            guns[gunIndex] = new Gun(gunX, gunY);
            gunIndex++;

        }

        // 5) Create the Platforms array
        String platformData = GAME_PROPS.getProperty("platforms.level2");
        if (platformData != null && !platformData.isEmpty()) {
            String[] platformEntries = platformData.split(";");
            this.platforms = new Platform[platformEntries.length];
            int pIndex = 0;
            for (String entry : platformEntries) {
                String[] coords = entry.trim().split(",");
                if (coords.length < 2) {
                    System.out.println("Warning: Invalid platform entry -> " + entry);
                    continue; // Skip invalid entries
                }
                double x = Double.parseDouble(coords[0]);
                double y = Double.parseDouble(coords[1]);
                if (pIndex < platformEntries.length) {
                    platforms[pIndex] = new Platform(x, y);
                    pIndex++;
                }
            }
        } else {
            this.platforms = new Platform[0]; // No platform data
        }

        // 6) Create Hammer
        String[] hammerCoords = GAME_PROPS.getProperty("hammer.level2.1").split(",");
        double hammerX = Double.parseDouble(hammerCoords[0]);
        double hammerY = Double.parseDouble(hammerCoords[1]);
        this.hammer = new Hammer(hammerX, hammerY);

        //7 Create the monkeys
        monkeys = new ArrayList<Monkey>();
        int monkeyCount = Integer.parseInt(GAME_PROPS.getProperty("normalMonkey.level2.count"));
        for (int i=1; i<=monkeyCount; i++) {
            String monkeyData = GAME_PROPS.getProperty("normalMonkey.level2." + i);
            monkeys.add(new Monkey(monkeyData));
        }

        //8 Smarter Monkeys
        int smartCount = Integer.parseInt(GAME_PROPS.getProperty("intelligentMonkey.level2.count"));
        for (int i=1; i<=smartCount; i++) {
            String smartData = GAME_PROPS.getProperty("intelligentMonkey.level2." + i);
            monkeys.add(new SmartMonkey(smartData));
        }

        //9 create projectiles arraylist
        projectiles = new ArrayList<Projectile>();

    }

    /**
     * Update is called every cycle and updates all instances in the game world based on
     * user input and their interactions with other objects.
     * @param input
     * @return True if the game has been completed or player has failed. else, false.
     */
    public boolean update(Input input) {
        currFrame++;

        background.drawFromTopLeft(0, 0);

        // 1) Draw and update platforms
        for (Platform platform : platforms) {
            if (platform != null) {
                platform.draw();
            }
        }

        // 2) Update ladders
        for (Ladder ladder : ladders) {
            if (ladder != null) {
                ladder.update(platforms);
            }
        }

        // 3) Update barrels
        for (Barrel barrel : barrels) {
            if (barrel == null) continue;
            if (mario.jumpOver(barrel)) {
                score += BARREL_CROSS_SCORE;
            }
            if (!barrel.isDestroyed() && mario.isTouchingBarrel(barrel)) {
                if (!mario.holdHammer()) {
                    isGameOver = true;
                } else {
                    barrel.destroy();
                    score += BARREL_SCORE;
                }
            }
            barrel.update(platforms);
        }

        //4 Update the monkeys
        for (Monkey monkey : monkeys) {
            boolean points = false;
            if (monkey instanceof SmartMonkey) {
                //Handle previously upcasted SmartMonkeys by downCasting them
                points = ((SmartMonkey)monkey).update(platforms, projectiles, mario);
            } else {
                points = monkey.update(platforms, mario);
            }
            if (points){
                score += MONKEY_KILL_PRIZE;
            }
        }

        //5 Update all projectiles in the arrayList.
        manageProjectile();


        // 6 Check game time and update donkey
        if (checkingGameTime()) {
            isGameOver = true;
        }
        donkey.update(platforms);

        // 7) Draw hammer and donkey
        hammer.draw();
        donkey.draw();

        // 8) Manage guns and whether mario hits them.
        for (Gun gun: guns) {
            gun.update(mario);
        }
        // 9) Update Mario
        mario.update(input, ladders, platforms, hammer, projectiles);

        // 10) Check if Mario reaches Donkey
        if ((mario.hasReached(donkey) && !mario.holdHammer())||
                mario.isDestroyed) {
            isGameOver = true;
        }

        // 11) Display score and time left
        displayInfo(mario, donkey);

        // 12) Return game state
        return isGameOver || isLevelCompleted();
    }

    /**
     * method to manage the projectile array. Updates all the projectiles if they haven't been destroyed
     * otherwise, it will remove them from the arrayList and then trim the ArrayList
     */
    private void manageProjectile() {
        ArrayList<Projectile> rubbish = new ArrayList<Projectile>();
        for (Projectile projectile : projectiles) {
            if (projectile.checkDestroyed()) {
                rubbish.add(projectile);
            } else {
                int toAdd = projectile.update(monkeys, mario, donkey,platforms);
                score += MONKEY_KILL_PRIZE * toAdd;
            }
        }
        for (Projectile destroyed : rubbish) {
            projectiles.remove(destroyed);
        }
        projectiles.trimToSize(); //Remove excess memory from the arraylist
        //System.out.println(projectiles.size()); //for testing memory size
    }


    /**
     * Display HUD info for the user such as time, bullet count and donkey health
     * @param mario the user
     * @param donkey the enemy
     */
    private void displayInfo(Mario mario, Donkey donkey) {
        STATUS_FONT.drawString(SCORE_MESSAGE + score, SCORE_X, SCORE_Y);
        HEALTH_FONT.drawString(HEALTH_MESSAGE + donkey.getHealth(), HEALTH_X, HEALTH_Y);
        BULLET_FONT.drawString(BULLET_MESSAGE + mario.bulletsLeft(), HEALTH_X, HEALTH_Y+30);
        // Time left in seconds
        int secondsLeft = (MAX_FRAMES - currFrame) / 60;
        int TIME_X = SCORE_X;
        int TIME_Y = SCORE_Y + TIME_DISPLAY_DIFF_Y;
        STATUS_FONT.drawString(TIME_MESSAGE + secondsLeft, TIME_X, TIME_Y);
    }

    /**
     * Return whether the level has been completed and the game won
     * @return True if won, Else false
     */
    public boolean isLevelCompleted() {
        return (mario.hasReached(donkey) && mario.holdHammer())||(donkey.getHealth() == 0);
    }

    /**
     * Used to check the game time.
     * @return
     */
    public boolean checkingGameTime() {
        return currFrame >= MAX_FRAMES;
    }

    /**
     * Return the current score.
     * @return
     */
    public int getScore() {
        return score;
    }

    /**
     * Return the number of seconds left
     * @return
     */
    public int getSecondsLeft() {
        return (MAX_FRAMES - currFrame) / 60;
    }






}
