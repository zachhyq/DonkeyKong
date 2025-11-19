import bagel.*;
import java.util.Properties;

/**
 * The main class for the Shadow Donkey Kong game.
 * This class extends {@code AbstractGame} and is responsible for managing game initialization,
 * updates, rendering, and handling user input.
 *
 * It sets up the game world, initializes characters, platforms, ladders, and other game objects,
 * and runs the game loop to ensure smooth gameplay.
 */
public class ShadowDonkeyKong extends AbstractGame {

    private final Properties GAME_PROPS;
    private final Properties MESSAGE_PROPS;

    private HomeScreen homeScreen;
    private LevelOne levelOne;
    private LevelTwo levelTwo;
    private GameEndScreen gameEndScreen;
    private final int TIME_WEIGHT = 3;
    private final int POINTS_WEIGHT = 1;

    public static double screenWidth;

    public static double screenHeight;

    /**
     * Constructs a new instance of the ShadowDonkeyKong game.
     * Initializes the game window using provided properties and sets up the home screen.
     *
     * @param gameProps     A {@link Properties} object containing game configuration settings
     *                      such as window width and height.
     * @param messageProps  A {@link Properties} object containing localized messages or UI labels,
     *                      including the title for the home screen.
     */
    public ShadowDonkeyKong(Properties gameProps, Properties messageProps) {
        super(Integer.parseInt(gameProps.getProperty("window.width")),
                Integer.parseInt(gameProps.getProperty("window.height")),
                messageProps.getProperty("home.title"));

        this.GAME_PROPS = gameProps;
        this.MESSAGE_PROPS = messageProps;
        this.screenWidth = Integer.parseInt(gameProps.getProperty("window.width"));
        this.screenHeight = Integer.parseInt(gameProps.getProperty("window.height"));

        homeScreen = new HomeScreen(GAME_PROPS, MESSAGE_PROPS);
    }


    /**
     * Render the relevant screen based on the keyboard input given by the user and the status of the gameplay.
     * @param input The current mouse/keyboard input.
     */
    @Override
    public void update(Input input) {
        if (input.wasPressed(Keys.ESCAPE)) {
            Window.close();
        }

        // Home Screen
        if (levelOne == null && levelTwo == null && gameEndScreen == null) {
            int homeres = homeScreen.update(input);
            if (homeres == 1) {
                levelOne = new LevelOne(GAME_PROPS);
            } else if (homeres == 2) {
                levelTwo = new LevelTwo(GAME_PROPS, 0);
            }
        }
        // Currently on lvl 1
        else if (levelOne != null && gameEndScreen == null) {

            if (levelOne.update(input)) {
                //Level one finished. If won, start Level2 else, GameOver
                boolean successOne = levelOne.isLevelCompleted();
                if (successOne) {
                    int carryScore = getLvlOneScore(levelOne.getScore(), levelOne.getSecondsLeft());
                    levelTwo = new LevelTwo(GAME_PROPS, carryScore);
                    levelOne = null;
                } else {
                    //Setup the GameOver Screen as we have lost
                    int finalScore = levelOne.getScore();
                    int timeRemaining = levelOne.getSecondsLeft();
                    gameEndScreen = new GameEndScreen(GAME_PROPS, MESSAGE_PROPS);
                    gameEndScreen.setIsWon(successOne);
                    gameEndScreen.setFinalScore(timeRemaining, finalScore);

                    levelOne = null;
                }

            }

        }
        //Currently on level two
        else if (levelTwo != null && levelOne == null) {
            if (levelTwo.update(input)) {

                boolean isWon = levelTwo.isLevelCompleted();

                // 1) GET THE SCORE
                int finalScore = levelTwo.getScore();
                int timeRemaining = levelTwo.getSecondsLeft();

                // 2) CREATE THE END SCREEN
                gameEndScreen = new GameEndScreen(GAME_PROPS, MESSAGE_PROPS);

                // 3) PASS finalScore
                gameEndScreen.setIsWon(isWon);
                gameEndScreen.setFinalScore(timeRemaining, finalScore);

                // 4) Nullify gameplay
                levelTwo = null;
            }

        }
        // Game Over / Victory Screen
        else if (levelOne == null && levelTwo == null) {
            if (gameEndScreen.update(input)) {
                levelOne = null;
                levelTwo = null;
                gameEndScreen = null;

            }
        }
    }

    /**
     * Retrieves the width of the game screen.
     *
     * @return The width of the screen in pixels.
     */
    public static double getScreenWidth() {
        return screenWidth;
    }

    /**
     * Retrieves the height of the game screen.
     *
     * @return The height of the screen in pixels.
     */
    public static double getScreenHeight() {
        return screenHeight;
    }

    /**
     * Return the combined score from level one, summing bonus points and time points
     * @param timeRemaining time shown on hud
     * @param gainedScore points shown on hud
     * @return score int
     */
    public int getLvlOneScore(int timeRemaining, int gainedScore) {
        // Weight assigned to time-based scoring
        // Weight assigned to points-based scoring
        return (TIME_WEIGHT * timeRemaining + gainedScore);
    }

    /**
     * The main entry point of the Shadow Donkey Kong game.
     *
     * This method loads the game properties and message files, initializes the game,
     * and starts the game loop.
     *
     * @param args Command-line arguments (not used in this game).
     */
    public static void main(String[] args) {
        Properties gameProps = IOUtils.readPropertiesFile("res/app.properties");
        Properties messageProps = IOUtils.readPropertiesFile("res/message.properties");
        ShadowDonkeyKong game = new ShadowDonkeyKong(gameProps, messageProps);
        game.run();
    }


}
