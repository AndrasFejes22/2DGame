package game2d;

import java.util.Random;

public class Game {

    static int height = 20;
    static int width = 20;
    static int powerUpInLevel = height;
    static final int  GAME_LOOP_NUMBER = 300;
    //static Random RANDOM = new Random(); //ha ez = new Random(100L); akkor nem változik a pálya mert álvéletlen számokat generál
    //static Random RANDOM = new Random(160L);
    static Random RANDOM = new Random(100L); //100L (1,4) 20,20
    public static void main(String[] args) throws InterruptedException {
    	
    	//game area
        String[][] level = new String[height][width];
        //draw the level: --initlevel method //drawing random walls
        
        do {
        	initLevel(level);
            addRandomWalls(level, 1, 4);
        }while(!isPassable(level));
        
        //System.exit(0);
        //Who win?
        GameResult gameResult = GameResult.TIE;
    	
    	//player
    	Direction playerDirection = Direction.RIGHT; //First, the player moving to the right
    	String playerMark = "O"; //represents the player
    	
    	//random first coordinates for the player
    	int[] playerStartingCoordinates = getRandomStartingCoordinates(level);
    	int playerRow = playerStartingCoordinates[0];
    	int playerColumn = playerStartingCoordinates[1];
    	
    	//enemy
    	Direction enemyDirection = Direction.LEFT; //First, the player moving to the right
    	String enemyMark = "@"; //represents the enemy
    	//random first coordinates for the enemy
    	int[] enemyStartingCoordinates = getRandomStartingCoordinatesForADistance(level, playerStartingCoordinates, 10);
    	int enemyRow = enemyStartingCoordinates[0];
    	int enemyColumn = enemyStartingCoordinates[1];
    	
    	//power-up
    	
    	String powerUpMark = "*"; //represents the power-up, egy helyben fog állni
    	//random first coordinates for the power-up
    	int[] powerUpStartingCoordinates = getRandomStartingCoordinates(level);
    	int powerUpRow = powerUpStartingCoordinates[0];
    	int powerUpColumn = powerUpStartingCoordinates[1];
    	boolean powerUpPresentOnLevel = false;
    	boolean powerUpActive = false;
    	int powerUpPresenceCounter = 0;
    	int powerUpActiveCounter = 0;


        for(int iterationNumber = 1; iterationNumber < GAME_LOOP_NUMBER; iterationNumber++) {//lépteti a karaktert -->makeMove method
            //player irányváltoztatása
			if (powerUpActive) {//powerup aktív = a játékos kergeti az enemyt
				playerDirection = changeDirectionTowards(level, playerDirection, playerRow, playerColumn, enemyRow, enemyColumn);
						
			} else {
				if(powerUpPresentOnLevel) {
					playerDirection = changeDirectionTowards(level, playerDirection, playerRow, playerColumn, powerUpRow, powerUpColumn);
				
				}else{
					if (iterationNumber % 15 == 0) {
						playerDirection = changeDirection(playerDirection);// 15 looponként derékszögû irányváltoztatás =
					}														// körkörös mozgás
				}
			}
            //kiszedjük a koordinátákat és átadjuk a draw()-nak
            //ez a 3 sor a léptetése a playernek
            int[] playerCoordinates = makeMove(playerDirection, level,playerRow, playerColumn);
            playerRow = playerCoordinates[0];
            playerColumn = playerCoordinates[1];

            //enemy irányváltoztatása: ez nincs benne if blokkban mert leköveti a player mozgását, (utánamegy), tehát a player mozgása vezérli
            if (powerUpActive) {
            Direction directionTowardsPlayer = changeDirectionTowards(level, enemyDirection, enemyRow, enemyColumn, playerRow, playerColumn);
            enemyDirection = getEscapeDirection(level, enemyRow, enemyColumn, directionTowardsPlayer);
            }else {
            	enemyDirection = changeDirectionTowards(level, enemyDirection, enemyRow, enemyColumn, playerRow, playerColumn);
            }
            //enemy léptetése:
            //kiszedjük a koordinátákat és átadjuk a draw()-nak
            if(iterationNumber % 2 == 0){//minden 2. körben lép csak    
            	int[] enemyCoordinates = makeMove(enemyDirection, level,enemyRow, enemyColumn);
            	enemyRow = enemyCoordinates[0];
            	enemyColumn = enemyCoordinates[1];
            }
            
            //powerUp updateing:
            if(powerUpActive) {
            	powerUpActiveCounter++;
            }else {
            powerUpPresenceCounter++;//minden iterációban növeljük eggyel a számlálót
            }
            if(powerUpPresenceCounter >= powerUpInLevel/2) {//ez*
            	if(powerUpPresentOnLevel) {//jelen van, és fog kapni random koordinátákat
            		powerUpStartingCoordinates = getRandomStartingCoordinates(level);
                	powerUpRow = powerUpStartingCoordinates[0];
                	powerUpColumn = powerUpStartingCoordinates[1];
            	}
            	powerUpPresentOnLevel = !powerUpPresentOnLevel; //vagy a pályán van, vagy nincs, és mindig kiváltjuk x (most 20) körönként ennek az ellenkezõjét
            	powerUpPresenceCounter = 0; //*meg ez csinálja hogy mindig elõlrõl kezdõdhessen a számlálás és 20 körig van pUp, 20 körig nincs
            	//és így tovább
            }
            if(powerUpActiveCounter >= powerUpInLevel) {
            	powerUpActive = false;
            	powerUpActiveCounter = 0;
            	powerUpStartingCoordinates = getRandomStartingCoordinates(level);
            	powerUpRow = powerUpStartingCoordinates[0];
            	powerUpColumn = powerUpStartingCoordinates[1];
            }
            
            //player-powerUp interaction handling:
            if(powerUpPresentOnLevel && playerRow == powerUpRow && playerColumn == powerUpColumn) {
            	powerUpActive = true;
            	powerUpPresentOnLevel = false;
            	powerUpPresenceCounter = 0;
            }
            
            //drawing level and a playerMark; minden körben mindenki kirajzolása = "mozi"
            draw(level, playerMark, playerRow, playerColumn, enemyMark, enemyRow, enemyColumn, 
            		powerUpMark, powerUpRow, powerUpColumn, powerUpPresentOnLevel,powerUpActive); 

            //várakoztatás
            addSomeDelay(iterationNumber, 200L);//print the iteration number and do the delay 
            
            //ha az enemy elkapta a playert (= a koordinátáik megegyeznek), akkor game over
            if(playerRow == enemyRow && playerColumn == enemyColumn) {//több élet?
            	if(powerUpActive) {
            		gameResult = GameResult.WIN;
            	}else {
            		gameResult = GameResult.LOSE;
            	System.out.println("The enemy caught the player!");
            	}
            	break;
            }

        }
        switch (gameResult) {
		case WIN: 
			System.out.println("Congratulation You WIN!");
			break;
		case LOSE: 
			System.out.println("Sorry You LOSE!");
			break;	
		case TIE: 
			System.out.println("Draw Game.");
			break;	
        }	
    }//main end
    
    //Csak olyan pályát rajzoljon amiben nincsenek zárt terek:
    
    
	private static boolean isPassable(String[][] level) {
		// pálya lemásolása
		int counter = 0;
		int counter2 = 0;
		String[][] levelCopy = copy(level);// 2D array lemásolása
		// elsõ szóköz megkeresése és *-al történõ helyettesítése
		outer: for (int row = 0; row < height; row++) {
			for (int column = 0; column < width; column++) {
				if (levelCopy[row][column].equals(" ")) {
					levelCopy[row][column] = "*";
					counter++;
					break outer;
				}
			}
		}

		// stars spreading
		// nem tud alul/felülindexelõdni mert szélsõséges esetben 1,1-rõl vagy
		// max-1,max-1 rõl indul
		for (int row = 0; row < height; row++) {
			counter2++;
			for (int column = 0; column < width; column++) {
				
				boolean changed = false;
				// a körülötte lévõ helyekre *-ot rak (amíg tud)
				if ("*".equals(levelCopy[row][column])) {
					if (" ".equals(levelCopy[row - 1][column])) {
						levelCopy[row - 1][column] = "*";
						changed = true;
					}
					if (" ".equals(levelCopy[row + 1][column])) {
						levelCopy[row + 1][column] = "*";
						changed = true;
					}
					if (" ".equals(levelCopy[row][column - 1])) {
						levelCopy[row][column - 1] = "*";
						changed = true;
					}
					if (" ".equals(levelCopy[row][column + 1])) {// levelCopy[1][4]:" "
						levelCopy[row][column + 1] = "*";
						changed = true;
					}
				}
				if(changed) {
					draw2DArray(levelCopy);
				}
			}
		}
			//draw2DArray(levelCopy);
			/*
			 * for (int row2 = 0; row2 < height; row2++) { for (int column2 = 0; column2 <
			 * width; column2++) { System.out.print(levelCopy[row2][column2]); }
			 * System.out.println(); }
			 */
			System.out.println("----" + counter2 + "----");
    	
    	
    	//prorgram leállítása
    	
    	System.out.println("counter: " + counter);
    	//System.out.println("levelCopy[15][13]: "+levelCopy[15][13]);
    	//System.out.println("levelCopy[15][13]: "+levelCopy[15][14]);
    	//System.out.println("levelCopy[16][14]: "+levelCopy[16][14]);
    	System.exit(0);
		return false;
		
	}
    
    
  //2D array lemásolása
    static String[][] copy(String[][] level){
    	String[][] copy = new String[height][width];
    	for (int row = 0; row < height; row++) {
    		for (int column = 0; column < width; column++) {
    			copy[row][column] = level[row][column];
            }
    	}
		return copy;
    	
    }

	private static Direction getEscapeDirection(String[][] level, int enemyRow, int enemyColumn, Direction directionTowardsPlayer) {
			
		Direction escapeDirection = getOppositeDirection(directionTowardsPlayer);
		switch (escapeDirection){	
		case UP://menjen erre vagy jobbra, vagy balra de mindenképpen elfele, végsõ esetben a falnak is ütközhet
			if (level[enemyRow - 1][enemyColumn].equals(" ")) {
				return Direction.UP;
			}else if (level[enemyRow][enemyColumn-1].equals(" ")) {
				return Direction.LEFT;
			}else if (level[enemyRow][enemyColumn +1].equals(" ")) {
				return Direction.RIGHT;
			}else {
				return Direction.UP;
			}
			
		case DOWN://menjen erre vagy jobbra, vagy balra de mindenképpen elfele, végsõ esetben a falnak is ütközhet
			if (level[enemyRow + 1][enemyColumn].equals(" ")) {
				return Direction.DOWN;
			}else if (level[enemyRow][enemyColumn-1].equals(" ")) {
				return Direction.LEFT;
			}else if (level[enemyRow][enemyColumn +1].equals(" ")) {
				return Direction.RIGHT;
			}else {
				return Direction.DOWN;
			}
		case RIGHT://menjen erre vagy jobbra, vagy balra de mindenképpen elfele, végsõ esetben a falnak is ütközhet
			if (level[enemyRow][enemyColumn + 1].equals(" ")) {
				return Direction.RIGHT;
			}else if (level[enemyRow - 1][enemyColumn].equals(" ")) {
				return Direction.UP;
			}else if (level[enemyRow + 1][enemyColumn ].equals(" ")) {
				return Direction.DOWN;
			}else {
				return Direction.RIGHT;
			}
			
		case LEFT://menjen erre vagy jobbra, vagy balra de mindenképpen elfele, végsõ esetben a falnak is ütközhet
			if (level[enemyRow][enemyColumn - 1].equals(" ")) {
				return Direction.LEFT;
			}else if (level[enemyRow - 1][enemyColumn].equals(" ")) {
				return Direction.UP;
			}else if (level[enemyRow + 1][enemyColumn ].equals(" ")) {
				return Direction.DOWN;
			}else {
				return Direction.LEFT;
			}
		default:
			return escapeDirection;

			
		}	
	}

	private static Direction getOppositeDirection(Direction direction) {
		
		switch (direction){
        case RIGHT : 
        	return Direction.LEFT; 
           
        case DOWN  : 
        	return Direction.UP; 
           
        case LEFT : 
        	return Direction.RIGHT;
          
        case UP : 
        	return Direction.DOWN; 
          
        default:    
        	return direction; 
		}
    
	}

	//random kezdõ koordináták sorsolása az eneminek, de**
    private static int[] getRandomStartingCoordinatesForADistance(String[][] level, int[] playerStartingCoordinates, int distance) {
    	int playerStartingRow = playerStartingCoordinates[0];
    	int playerStartingColumn = playerStartingCoordinates[1];
    	int randomRow;
		int randomColumn;
		int counter = 0; //infinite loop kivédése //////////////EZ MÉG NEM JÓÓÓ
		do {
			counter++;
			randomRow = RANDOM.nextInt(height);//nemjo ezek nullát felvehetnek!!!
			randomColumn = RANDOM.nextInt(width);
		//**nem lehet a két koordináta közelebb egymáshoz mint distance pályaegység	
		}while(counter < 10 
			&& (!level[randomRow][randomColumn].equals(" ") || calculateDistance(randomRow, randomColumn, playerStartingRow, playerStartingColumn) < distance));
		
	return new int[] {randomRow, randomColumn};
	}

	private static int calculateDistance(int row1, int column1, int row2, int column2) {
			
		int rowDifference = Math.abs(row1 - row2);
		int columnDifference = Math.abs(column1 - column2);
		return rowDifference + columnDifference;
	}

	//random kezdõ koordináták sorsolása egy tetszõleges playernek:
	private static int[] getRandomStartingCoordinates(String[][] level) {
		int randomRow;
		int randomColumn;
		do {
			randomRow = RANDOM.nextInt(height);//nemjo ezek nullát felvehetnek, de az most nem baj mert az "X", és azt kivédi a while itt
			randomColumn = RANDOM.nextInt(width);
			
		}while(!level[randomRow][randomColumn].equals(" "));
		
	return new int[] {randomRow, randomColumn};
	}

	//WALLS
    
    static void addRandomWalls(String [][]level, int numberOfHorizontalWalls, int numberOfWerticalWalls ) {
    	//TODO fal ne kerüljön a játékosokra
    	for(int i = 0; i < numberOfHorizontalWalls; i++) {
    		addHorizontalWall(level);
    	}
    	for(int i = 0; i < numberOfWerticalWalls; i++) {
    		addVerticalWall(level);
    	}
    	
    }
    	    
    
    static void addHorizontalWall(String [][]level) {
    	
    	int wallWidth = RANDOM.nextInt(width-3);
    	int wallRow = RANDOM.nextInt(height-2)+1;
    	int wallColumn = RANDOM.nextInt(width-2-wallWidth);
    	for(int i = 0; i < wallWidth; i++ ) {
    		level[wallRow][wallColumn + i] = "X";
    	}
    }
    
    static void addVerticalWall(String [][]level) {
    	
    	int wallHeight = RANDOM.nextInt(height-3);
    	int wallRow = RANDOM.nextInt(height-2 - wallHeight);
    	int wallColumn = RANDOM.nextInt(width-2)+1;
    	for(int i = 0; i < wallHeight; i++ ) {
    		level[wallRow + i][wallColumn] = "X";
    	}
    	
    }
    
    //késleltetés
    private static void addSomeDelay(int loopCounter, long timeout) throws InterruptedException {
        System.out.println("-------" + loopCounter + "------");
        Thread.sleep(timeout);
    }
    
    //maga a mozgás
    static int[] makeMove(Direction direction, String[][] level, int row, int column){
        switch (direction) {
            case UP:
                if (level[row - 1][column].equals(" ")) {
                    row--;
                }
                break;
            case DOWN:
                if (level[row + 1][column].equals(" ")) {
                    row++;
                }
                break;
            case LEFT:
                if (level[row][column - 1].equals(" ")) {
                    column--;
                }
                break;
            case RIGHT:
                if (level[row][column + 1].equals(" ")) {
                    column++;
                }
                break;
        }
        return new int[] {row, column};
    }

    //pálya megrajzolása
    private static void initLevel(String[][] level) {
        for (int row = 0; row < height; row++) {
            for (int column = 0; column < width; column++) {
                if(row == 0 ||  row == height-1 || column == 0 || column == width-1) {//walls
                    level[row][column] = "X"; //walls
                }else{
                    level[row][column] = " ";
                }
            }
        }
    }

    //körkörös mozgás
    private static Direction changeDirection(Direction direction) {
        switch (direction){
            case RIGHT : 
                direction = Direction.DOWN; 
                break;
            case DOWN  : 
                direction = Direction.LEFT; 
                break;
            case LEFT : 
                direction = Direction.UP;
                break;
            case UP : 
                direction = Direction.RIGHT; 
                break;
        }
        return direction; //ha egyik s-w ág sem tut le, az eredeti iránnyal térünk vissza
        //return: vezérlésátadó utasítás, metódus szinten dolgozik
    }
    
    //követõ mozgás: ha sorok vagy oszlopok indexében delta van akkor ezt az enemy igyekszik kiegyenlíteni = utána megy
    // && = ha mehet arra = nincs fal = .equals(" ")
    private static Direction changeDirectionTowards(String[][] level, Direction oiginalEnemyDirection, int enemyRow, int enemyColumn, int playerRow, int playerColumn ) {
    	if(playerRow < enemyRow && level[enemyRow -1][enemyColumn].equals(" ")) {// && mehet arra
    		return Direction.UP;
    	}
    	if(playerRow > enemyRow && level[enemyRow +1][enemyColumn].equals(" ")) {
    		return Direction.DOWN;
    	}
    	if(playerColumn < enemyColumn && level[enemyRow][enemyColumn - 1].equals(" ")) {
    		return Direction.LEFT;
    	}
    	if(playerColumn > enemyColumn && level[enemyRow][enemyColumn + 1].equals(" ")) {
    		return Direction.RIGHT;
    	}
    	return oiginalEnemyDirection;
    }

    public static void draw2DArray(String[][] arr){

        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr[i].length; j++) {
                System.out.print(arr[i][j]);
            }
            System.out.println();
        }

    }
    
    public static boolean leftAStringInAnArray(String[][] level, String str){

    	for (int row = 0; row < height; row++) {
    		for (int column = 0; column < width; column++) {
    			if(level[row][column].equals(str));
    			return false;
            }
    	}
		return true;

    }

    //pálya és játékosok kirajzolása
    public static void draw(String[][] board, String playerMark, int playerRow, int playerColumn, String enemyMark, int enemyRow, int enemyColumn, 
    		String powerUpMark, int powerUpRow, int powerUpColumn, boolean powerUpPresentOnLevel, boolean powerUpActive){
        for (int row = 0; row < height; row++) {
            for (int column = 0; column < width; column++) {
                if (row == playerRow && column == playerColumn) {
                    System.out.print(playerMark);
                } else if(row == enemyRow && column == enemyColumn) {
                    System.out.print(enemyMark);
                } else if(powerUpPresentOnLevel && row == powerUpRow && column == powerUpColumn) {
                    System.out.print(powerUpMark);    
                }else{
                    System.out.print(board[row][column]);
                }
            }
            System.out.println();
        }
        if(powerUpActive) {
        	System.out.println("power-up is active!");
        }
        if(powerUpPresentOnLevel) {
        	System.out.println("power-up is on the board!");
        }
    }
}

