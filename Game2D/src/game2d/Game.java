package game2d;

import java.util.Random;

public class Game {

    static int height = 40;
    static int width = 40;
    static int powerUpInLevel = height;
    static final int  GAME_LOOP_NUMBER = 300;
    static Random RANDOM = new Random(); //ha ez = new Random(100L); akkor nem v�ltozik a p�lya mert �lv�letlen sz�mokat gener�l
    //static Random RANDOM = new Random(160L);
    //static Random RANDOM = new Random(27L); //100L (1,4) 20,20
    public static void main(String[] args) throws InterruptedException {
    	
    	//game area
        String[][] level = new String[height][width];
        //draw the level: --initlevel method //drawing random walls
        int isPassableCounter = 0;
        do {
        	initLevel(level);
            //addRandomWalls(level, 10, 10);
            addRandomWalls(level, 5, 6);
            isPassableCounter++;
        }while(!isPassable(level));
        
        System.out.println("The No " + isPassableCounter + " board is passable");
        
        ////////DRAW ASTERISKS OR NOT/////////
        //overloaded isPassable() kirajzolja-e a csillagokat
        //isPassable(level, true);
        isPassable(level, false);
        
        ////////VIZSG�LAT ELEJE///////
        //ez a 2 sor csak azt vizsg�lja, hogy h�nyadik gener�l�sra kapunk �tj�rhat� p�ly�t, a f� programban nem kell
        //draw2DArray(level);
        //System.exit(0);
        ////////VIZSG�LAT V�GE///////
        
   
        //Who win?
        GameResult gameResult = GameResult.TIE;
    	
    	//player
    	Direction playerDirection = Direction.RIGHT; //First, the player moving to the right
    	String playerMark = "O"; //represents the player
    	
    	//random first coordinates for the player
    	int[] playerStartingCoordinates = getRandomStartingCoordinates(level);
    	int playerRow = playerStartingCoordinates[0];
    	int playerColumn = playerStartingCoordinates[1];
    	//random first coordinates for the player when escapeing
    	int[] playerEscapeCoordinates = getFarthestCorner(level, playerRow, playerColumn);
    	int playerEscapeRow = playerEscapeCoordinates[0];
    	int playerEscapeColumn = playerEscapeCoordinates[1];
    	
    	//enemy
    	Direction enemyDirection = Direction.LEFT; //First, the player moving to the right
    	String enemyMark = "@"; //represents the enemy
    	//random first coordinates for the enemy
    	int[] enemyStartingCoordinates = getRandomStartingCoordinatesForADistance(level, playerStartingCoordinates, 10);
    	int enemyRow = enemyStartingCoordinates[0];
    	int enemyColumn = enemyStartingCoordinates[1];
    	//random first coordinates for the enemy when escapeing
    	int[] enemyEscapeCoordinates = getFarthestCorner(level, enemyRow, enemyColumn);
    	int enemyEscapeRow = enemyEscapeCoordinates[0];
    	int enemyEscapeColumn = enemyEscapeCoordinates[1];
    	
    	//power-up
    	
    	String powerUpMark = "*"; //represents the power-up, egy helyben fog �llni
    	//random first coordinates for the power-up
    	int[] powerUpStartingCoordinates = getRandomStartingCoordinates(level);
    	int powerUpRow = powerUpStartingCoordinates[0];
    	int powerUpColumn = powerUpStartingCoordinates[1];
    	boolean powerUpPresentOnLevel = false;
    	boolean powerUpActive = false;
    	int powerUpPresenceCounter = 0;
    	int powerUpActiveCounter = 0;


        for(int iterationNumber = 1; iterationNumber < GAME_LOOP_NUMBER; iterationNumber++) {//l�pteti a karaktert -->makeMove method
        	///////UPDATED PLAYER MOVING////////
            //player ir�nyv�ltoztat�sa
			if (powerUpActive) {//powerup akt�v = a j�t�kos kergeti az enemyt
				//valami fel� l�p, ez m�g nem okos
				//playerDirection = changeDirectionTowards(level, playerDirection, playerRow, playerColumn, enemyRow, enemyColumn);
				//okosan megy a powerUp fel�:
				playerDirection = getShortestPath(level, playerDirection, playerRow, playerColumn, enemyRow, enemyColumn);
						
			} else {
				if(powerUpPresentOnLevel) {
					//playerDirection = changeDirectionTowards(level, playerDirection, playerRow, playerColumn, powerUpRow, powerUpColumn);
					playerDirection = getShortestPath(level, playerDirection, playerRow, playerColumn, powerUpRow, powerUpColumn);
				
				}else{
					//player menek�l:
					if (iterationNumber % 50 == 0) {//v�laszt�s 50 l�p�senk�nt
						//ink�bb menek�lj�n a legt�volabbi pontba:
						playerEscapeCoordinates = getFarthestCorner(level, playerRow, playerColumn);
						playerEscapeRow = playerEscapeCoordinates[0];
						playerEscapeColumn = playerEscapeCoordinates[1];
					}
					playerDirection = getShortestPath(level, playerDirection, playerRow, playerColumn, playerEscapeRow, playerEscapeColumn);// minden k�rben der�ksz�g� ir�nyv�ltoztat�s 
				}
			}
            //kiszedj�k a koordin�t�kat �s �tadjuk a draw()-nak
            //ez a 3 sor a l�ptet�se a playernek
            int[] playerCoordinates = makeMove(playerDirection, level, playerRow, playerColumn);
            playerRow = playerCoordinates[0];
            playerColumn = playerCoordinates[1];
            
            ///////UPDATED ENEMY MOVING////////

            //enemy ir�nyv�ltoztat�sa: ez nincs benne if blokkban mert lek�veti a player mozg�s�t, (ut�namegy), teh�t a player mozg�sa vez�rli
            if (powerUpActive) {
            	//r�gi mozg�s:
            	//Direction directionTowardsPlayer = changeDirectionTowards(level, enemyDirection, enemyRow, enemyColumn, playerRow, playerColumn);
            	//enemyDirection = getEscapeDirection(level, enemyRow, enemyColumn, directionTowardsPlayer);
            	
            	//enemy menek�l
            	if(iterationNumber % 50 == 0) {//v�laszt�s 50 l�p�senk�nt
            		enemyEscapeCoordinates = getFarthestCorner(level, enemyRow, enemyColumn);
            		enemyEscapeRow = enemyEscapeCoordinates[0];
            		enemyEscapeColumn = enemyEscapeCoordinates[1];
                	
            	}	
            	enemyDirection = getShortestPath(level, enemyDirection, enemyRow, enemyColumn, enemyEscapeRow, enemyEscapeColumn);
            }else {
            	//enemyDirection = changeDirectionTowards(level, enemyDirection, enemyRow, enemyColumn, playerRow, playerColumn);
            	//felokos�tva:
            	//enemy �ld�z:
            	enemyDirection = getShortestPath(level, enemyDirection, enemyRow, enemyColumn, playerRow, playerColumn);
            }
            //enemy l�ptet�se:
            //kiszedj�k a koordin�t�kat �s �tadjuk a draw()-nak
            if(iterationNumber % 2 == 0){//minden 2. k�rben l�p csak    
            	int[] enemyCoordinates = makeMove(enemyDirection, level,enemyRow, enemyColumn);
            	enemyRow = enemyCoordinates[0];
            	enemyColumn = enemyCoordinates[1];
            }
            
            //powerUp updateing:
            if(powerUpActive) {
            	powerUpActiveCounter++;
            }else {
            powerUpPresenceCounter++;//minden iter�ci�ban n�velj�k eggyel a sz�ml�l�t
            }
            //powerUpPresenceCounter mennyi ideig van a p�ly�n
            if(powerUpPresenceCounter >= powerUpInLevel) {//ez*
            	if(powerUpPresentOnLevel) {//jelen van, �s fog kapni random koordin�t�kat
            		powerUpStartingCoordinates = getRandomStartingCoordinates(level);
                	powerUpRow = powerUpStartingCoordinates[0];
                	powerUpColumn = powerUpStartingCoordinates[1];
            	}
            	powerUpPresentOnLevel = !powerUpPresentOnLevel; //vagy a p�ly�n van, vagy nincs, �s mindig kiv�ltjuk x (most 20) k�r�nk�nt ennek az ellenkez�j�t
            	powerUpPresenceCounter = 0; //*meg ez csin�lja hogy mindig el�lr�l kezd�dhessen a sz�ml�l�s �s 20 k�rig van pUp, 20 k�rig nincs
            	//�s �gy tov�bb
            }
            if(powerUpActiveCounter >= powerUpInLevel*2) {
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
            
            //drawing level and a playerMark; minden k�rben mindenki kirajzol�sa = "mozi"
            draw(level, playerMark, playerRow, playerColumn, enemyMark, enemyRow, enemyColumn, 
            		powerUpMark, powerUpRow, powerUpColumn, powerUpPresentOnLevel,powerUpActive); 

            //v�rakoztat�s
            addSomeDelay(iterationNumber, 200L);//print the iteration number and do the delay 
            
            //ha az enemy elkapta a playert (= a koordin�t�ik megegyeznek), akkor game over
            if(playerRow == enemyRow && playerColumn == enemyColumn) {//t�bb �let?
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
    
    
    
    

	private static int[] getFarthestCorner(String[][] level, int fromRow, int fromColumn) {
		// p�lya lem�sol�sa
		String[][] levelCopy = copy(level);// 2D array(p�lya) lem�sol�sa
		// els� csillag lehelyez�se
    	levelCopy[fromRow][fromColumn] = "*";
    	
    	int farthestRow = 0;
    	int farthestColumn = 0;
    	
    	while(spreadAsterisksWithCheck(levelCopy)) {
    		outer: for (int row = 0; row < height; row++) {
    			for (int column = 0; column < width; column++) {
    				if (levelCopy[row][column].equals(" ")) {
    					farthestRow = row;
    			    	farthestColumn = column;
    			    	break outer;
    				}
    			}
    		}
    	}
		return new int[] {farthestRow, farthestColumn};
	}





	//rajzolja-e a csillagok terjed�s�t
    static boolean isPassable(String[][] level) {
		return isPassable(level, false);
		
	}

	//Csak olyan p�ly�t rajzoljon amiben nincsenek z�rt terek:
    
    
	static boolean isPassable(String[][] level, boolean drawAsterisks) {
		// p�lya lem�sol�sa
		String[][] levelCopy = copy(level);// 2D array lem�sol�sa
		// els� sz�k�z megkeres�se �s *-al t�rt�n� helyettes�t�se
		outer: for (int row = 0; row < height; row++) {
			for (int column = 0; column < width; column++) {
				if (levelCopy[row][column].equals(" ")) {
					levelCopy[row][column] = "*";
					
					break outer;
					// break;
				}
			}
		}

		// stars spreading
		// nem tud alul/fel�lindexel�dni mert sz�ls�s�ges esetben 1,1-r�l vagy
		// max-1,max-1 r�l indul
		
		while(spreadAsterisks(levelCopy)) {
			if(drawAsterisks) {
			draw2DArray(levelCopy);
			}
		}
		//draw2DArray(levelCopy);
		//p�lyam�solat vizsg�lata: maradt-e sz�k�z valahol
		
		for (int row = 0; row < height; row++) {
			for (int column = 0; column < width; column++) {
				if (levelCopy[row][column].equals(" ")) {
					return false;
				}
			}
		}
		
		//System.exit(0);
		//return false;
		return true;
		
	}
	
	//UPDATED PLAYER MOVING
	//Az �t megtal�l�sa a terjed� csillagok method visszafel�
	//A *-ok ugyanis mindig a legr�videbb ir�nyban terjednek
    static Direction getShortestPath(String[][]level, Direction defaultDirection, int fromRow, int fromColumn, int toRow, int toColumn) {
    	// p�lya lem�sol�sa:
    	String[][] levelCopy = copy(level);// 2D array lem�sol�sa
    	// els� csillag lehelyez�se
    	levelCopy[toRow][toColumn] = "*";
    	while(spreadAsterisksWithCheck(levelCopy)) {
			if("*".equals(levelCopy[fromRow -1][fromColumn])) {//ha a visszafel� terjed� csillagok k�z�l az els� fel�lr�l 
				//jelent meg akkor felfele megy�nk*
				return Direction.UP;
			}
			if("*".equals(levelCopy[fromRow +1][fromColumn])) {//ha a visszafel� terjed� csillagok k�z�l az els� alulr�l 
				//jelent meg akkor lefele megy�nk*
				return Direction.DOWN;
			}
			if("*".equals(levelCopy[fromRow][fromColumn -1])) {//ha a visszafel� terjed� csillagok k�z�l az els� balr�l 
				//jelent meg akkor balra megy�nk*
				return Direction.LEFT;
			}
			if("*".equals(levelCopy[fromRow][fromColumn+1])) {//ha a visszafel� terjed� csillagok k�z�l az els� jobbra 
				//jelent meg akkor jobbra megy�nk*
				return Direction.RIGHT;
			}
		}
		return defaultDirection;
    	
    }
	
	private static boolean spreadAsterisksWithCheck(String[][] levelCopy) {
		boolean[][] mask = new boolean [height][width]; //alap�rtelmezetten csupa false-val van tele
		//v�gigmegyek a levelCopy-n, �s ha tal�lok valahol csillagot, ott a mask-ot true-ra �ll�tom:
		for (int row = 0; row < height; row++) {
    		for (int column = 0; column < width; column++) {
    			if ("*".equals(levelCopy[row][column])) {
					mask[row][column] = true;
					
				}
            }
    	}
		boolean changed = false;
		for (int row = 0; row < height; row++) {

			for (int column = 0; column < width; column++) {
				
				// a k�r�l�tte l�v� helyekre *-ot rak (am�g tud)
				if ("*".equals(levelCopy[row][column]) && mask[row][column]) {// * van valahol �S a mask az true
					if (" ".equals(levelCopy[row - 1][column])) {
						levelCopy[row - 1][column] = "*";
						changed = true;//ez �gy mindig csak 1-et l�p
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
			}
		}
		return changed;
	}

	private static boolean spreadAsterisks(String[][] levelCopy) {
		boolean changed = false;

		for (int row = 0; row < height; row++) {

			for (int column = 0; column < width; column++) {
				
				// a k�r�l�tte l�v� helyekre *-ot rak (am�g tud)
				if ("*".equals(levelCopy[row][column])) {// * van valahol
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
			}
		}
		return changed;
	}
    
    
  //2D array lem�sol�sa
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
		case UP://menjen erre vagy jobbra, vagy balra de mindenk�ppen elfele, v�gs� esetben a falnak is �tk�zhet
			if (level[enemyRow - 1][enemyColumn].equals(" ")) {
				return Direction.UP;
			}else if (level[enemyRow][enemyColumn-1].equals(" ")) {
				return Direction.LEFT;
			}else if (level[enemyRow][enemyColumn +1].equals(" ")) {
				return Direction.RIGHT;
			}else {
				return Direction.UP;
			}
			
		case DOWN://menjen erre vagy jobbra, vagy balra de mindenk�ppen elfele, v�gs� esetben a falnak is �tk�zhet
			if (level[enemyRow + 1][enemyColumn].equals(" ")) {
				return Direction.DOWN;
			}else if (level[enemyRow][enemyColumn-1].equals(" ")) {
				return Direction.LEFT;
			}else if (level[enemyRow][enemyColumn +1].equals(" ")) {
				return Direction.RIGHT;
			}else {
				return Direction.DOWN;
			}
		case RIGHT://menjen erre vagy jobbra, vagy balra de mindenk�ppen elfele, v�gs� esetben a falnak is �tk�zhet
			if (level[enemyRow][enemyColumn + 1].equals(" ")) {
				return Direction.RIGHT;
			}else if (level[enemyRow - 1][enemyColumn].equals(" ")) {
				return Direction.UP;
			}else if (level[enemyRow + 1][enemyColumn ].equals(" ")) {
				return Direction.DOWN;
			}else {
				return Direction.RIGHT;
			}
			
		case LEFT://menjen erre vagy jobbra, vagy balra de mindenk�ppen elfele, v�gs� esetben a falnak is �tk�zhet
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

	//random kezd� koordin�t�k sorsol�sa az eneminek, de**
    private static int[] getRandomStartingCoordinatesForADistance(String[][] level, int[] playerStartingCoordinates, int distance) {
    	int playerStartingRow = playerStartingCoordinates[0];
    	int playerStartingColumn = playerStartingCoordinates[1];
    	int randomRow;
		int randomColumn;
		int counter = 0; //infinite loop kiv�d�se //////////////EZ M�G NEM J���
		do {
			counter++;
			randomRow = RANDOM.nextInt(height);//nemjo ezek null�t felvehetnek!!!
			randomColumn = RANDOM.nextInt(width);
		//**nem lehet a k�t koordin�ta k�zelebb egym�shoz mint distance p�lyaegys�g	
		}while(counter < 10 
			&& (!level[randomRow][randomColumn].equals(" ") || calculateDistance(randomRow, randomColumn, playerStartingRow, playerStartingColumn) < distance));
		
	return new int[] {randomRow, randomColumn};
	}

	private static int calculateDistance(int row1, int column1, int row2, int column2) {
			
		int rowDifference = Math.abs(row1 - row2);
		int columnDifference = Math.abs(column1 - column2);
		return rowDifference + columnDifference;
	}

	//random kezd� koordin�t�k sorsol�sa egy tetsz�leges playernek:
	private static int[] getRandomStartingCoordinates(String[][] level) {
		int randomRow;
		int randomColumn;
		do {
			randomRow = RANDOM.nextInt(height);//nemjo ezek null�t felvehetnek, de az most nem baj mert az "X", �s azt kiv�di a while itt
			randomColumn = RANDOM.nextInt(width);
			
		}while(!level[randomRow][randomColumn].equals(" "));
		
	return new int[] {randomRow, randomColumn};
	}

	//WALLS
    
    static void addRandomWalls(String [][]level, int numberOfHorizontalWalls, int numberOfWerticalWalls ) {
    	//TODO fal ne ker�lj�n a j�t�kosokra
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
    
    //k�sleltet�s
    private static void addSomeDelay(int loopCounter, long timeout) throws InterruptedException {
        System.out.println("-------" + loopCounter + "------");
        Thread.sleep(timeout);
    }
    
    //maga a mozg�s
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

    //p�lya megrajzol�sa
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

    //k�rk�r�s mozg�s
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
        return direction; //ha egyik s-w �g sem tut le, az eredeti ir�nnyal t�r�nk vissza
        //return: vez�rl�s�tad� utas�t�s, met�dus szinten dolgozik
    }
    
    //k�vet� mozg�s: ha sorok vagy oszlopok index�ben delta van akkor ezt az enemy igyekszik kiegyenl�teni = ut�na megy
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
		

    

    //p�lya �s j�t�kosok kirajzol�sa
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
        /*
        if(powerUpActive) {
        	System.out.println("power-up is active!");
        }
        if(powerUpPresentOnLevel) {
        	System.out.println("power-up is on the board!");
        }
        */
    }
}

